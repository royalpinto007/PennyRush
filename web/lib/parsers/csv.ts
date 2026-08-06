import Papa from "papaparse";
import type { ImportCandidate, TransactionType } from "@pennyrush/shared";

export type ImportRowStatus = "new" | "duplicate";

export type ParsedImportCandidate = ImportCandidate & {
  category?: string;
  category_id?: string | null;
  confidence?: number;
  dedupeKey: string;
  importedHash: string;
  status: ImportRowStatus;
};

export type ParseCsvImportResult = {
  candidates: ParsedImportCandidate[];
  errors: string[];
  headers: string[];
  rawRows: number;
  skippedRows: number;
};

export type ExistingTransactionLike = {
  amount: number;
  type: TransactionType;
  date: string;
  merchant: string | null;
  note: string | null;
  imported_hash?: string | null;
};

export type TransactionInsertPayload = {
  user_id: string;
  account_id: string;
  category_id: string | null;
  amount: number;
  type: TransactionType;
  date: string;
  merchant: string;
  note: string | null;
  source: "import";
  ai_confidence: number | null;
  imported_hash: string;
};

type Mapping = {
  date: number;
  merchant: number;
  note: number;
  amount: number;
  debit: number;
  credit: number;
  type: number;
};

const defaultMapping: Mapping = {
  date: -1,
  merchant: -1,
  note: -1,
  amount: -1,
  debit: -1,
  credit: -1,
  type: -1,
};

const monthNames = new Map(
  [
    ["jan", 1],
    ["feb", 2],
    ["mar", 3],
    ["apr", 4],
    ["may", 5],
    ["jun", 6],
    ["jul", 7],
    ["aug", 8],
    ["sep", 9],
    ["sept", 9],
    ["oct", 10],
    ["nov", 11],
    ["dec", 12],
  ],
);

export function parseCsvTransactions(text: string): ParsedImportCandidate[] {
  return parseCsvImport(text).candidates;
}

export function parseCsvImport(text: string): ParseCsvImportResult {
  const parsed = Papa.parse<string[]>(text, {
    skipEmptyLines: "greedy",
  });
  const rows = parsed.data
    .map((row) => row.map((cell) => String(cell ?? "").trim()))
    .filter((row) => row.some(Boolean));
  const errors = parsed.errors.map((error) => error.message);

  if (rows.length < 2) {
    return {
      candidates: [],
      errors: errors.length > 0 ? errors : ["Statement needs a header and at least one activity entry."],
      headers: [],
      rawRows: rows.length,
      skippedRows: 0,
    };
  }

  const headerIndex = rows.findIndex((row) => mappingFor(row).date >= 0 && hasMoneyMapping(mappingFor(row)));
  if (headerIndex < 0) {
    return {
      candidates: [],
      errors: [
        ...errors,
        "Could not find recognizable columns. Expected Date, Merchant/Description, and Amount or Debit/Credit.",
      ],
      headers: rows[0],
      rawRows: rows.length,
      skippedRows: Math.max(0, rows.length - 1),
    };
  }

  const headers = rows[headerIndex];
  const mapping = mappingFor(headers);
  const body = rows.slice(headerIndex + 1);
  const candidates: ParsedImportCandidate[] = [];
  let skippedRows = 0;

  for (const [index, row] of body.entries()) {
    const date = normalizeDate(row[mapping.date]);
    const merchant = readableCell(row[mapping.merchant]) || readableCell(row[mapping.note]) || `Imported entry ${index + 1}`;
    const note = readableCell(row[mapping.note]) || "Statement import";
    const money = amountAndTypeFor(row, mapping);

    if (!date || !money || money.amount <= 0) {
      skippedRows += 1;
      continue;
    }

    const base = {
      date,
      merchant,
      amount: money.amount,
      type: money.type,
      note,
    };
    const dedupeKey = buildImportDedupeKey(base);
    const importedHash = buildImportHash(dedupeKey);

    candidates.push({
      id: `import_${importedHash}`,
      ...base,
      dedupeKey,
      importedHash,
      status: "new",
    });
  }

  return {
    candidates,
    errors,
    headers,
    rawRows: body.length,
    skippedRows,
  };
}

export function buildImportDedupeKey(input: {
  date: string;
  amount: number;
  type: TransactionType;
  merchant?: string | null;
  note?: string | null;
}) {
  const merchant = normalizeImportText(input.merchant || input.note || "entry");
  const amount = Math.abs(Number(input.amount)).toFixed(2);
  return [input.date, amount, input.type, merchant].join("|");
}

export function buildImportHash(input: string | Parameters<typeof buildImportDedupeKey>[0]) {
  const value = typeof input === "string" ? input : buildImportDedupeKey(input);
  return `web-${fnv1a(value)}`;
}

export function existingImportIdentities(rows: ExistingTransactionLike[]) {
  const hashes = new Set<string>();
  const keys = new Set<string>();

  for (const row of rows) {
    if (row.imported_hash) hashes.add(row.imported_hash);
    keys.add(buildImportDedupeKey(row));
  }

  return { hashes, keys };
}

export function withDuplicateStatuses<T extends ParsedImportCandidate>(
  candidates: T[],
  existingRows: ExistingTransactionLike[],
) {
  const existing = existingImportIdentities(existingRows);
  const seenInFile = new Set<string>();

  return candidates.map((candidate) => {
    const duplicate =
      existing.hashes.has(candidate.importedHash) ||
      existing.keys.has(candidate.dedupeKey) ||
      seenInFile.has(candidate.dedupeKey);
    seenInFile.add(candidate.dedupeKey);
    return {
      ...candidate,
      status: duplicate ? "duplicate" : "new",
    } satisfies T;
  });
}

export function buildTransactionInsertPayload(
  candidate: ParsedImportCandidate,
  userId: string,
  accountId: string,
): TransactionInsertPayload {
  return {
    user_id: userId,
    account_id: accountId,
    category_id: candidate.category_id ?? null,
    amount: Math.abs(candidate.amount),
    type: candidate.type,
    date: candidate.date,
    merchant: candidate.merchant.trim(),
    note: candidate.note?.trim() || null,
    source: "import",
    ai_confidence: candidate.confidence ?? null,
    imported_hash: candidate.importedHash,
  };
}

function mappingFor(headers: string[]): Mapping {
  const normalized = headers.map(normalizeHeader);

  return {
    ...defaultMapping,
    date: findHeader(normalized, ["date", "txn date", "transaction date", "posted date", "value date"]),
    merchant: findHeader(normalized, [
      "merchant",
      "description",
      "narration",
      "details",
      "particulars",
      "payee",
    ]),
    note: findHeader(normalized, ["note", "memo", "remarks"]),
    amount: findExactHeader(normalized, ["amount", "txn amount", "transaction amount", "value"]),
    debit: findHeader(normalized, ["debit", "withdrawal", "withdrawal amount", "paid out", "dr"]),
    credit: findHeader(normalized, ["credit", "deposit", "deposit amount", "paid in", "cr"]),
    type: findHeader(normalized, ["type", "direction", "transaction type", "dr cr"]),
  };
}

function hasMoneyMapping(mapping: Mapping) {
  return mapping.amount >= 0 || mapping.debit >= 0 || mapping.credit >= 0;
}

function amountAndTypeFor(row: string[], mapping: Mapping) {
  if (mapping.debit >= 0 || mapping.credit >= 0) {
    const debit = parseMoney(row[mapping.debit]) ?? 0;
    const credit = parseMoney(row[mapping.credit]) ?? 0;
    if (debit > 0) return { amount: Math.abs(debit), type: "expense" as const };
    if (credit > 0) return { amount: Math.abs(credit), type: "income" as const };
  }

  if (mapping.amount < 0) return null;

  const amount = parseMoney(row[mapping.amount]);
  if (amount === null || amount === 0) return null;
  const typeText = mapping.type >= 0 ? row[mapping.type] : undefined;
  return {
    amount: Math.abs(amount),
    type: inferType(amount, typeText),
  };
}

function inferType(amount: number, rawType: string | undefined): TransactionType {
  const normalized = normalizeImportText(rawType ?? "");
  if (normalized.includes("credit") || normalized.includes("income") || normalized.includes("deposit")) {
    return "income";
  }
  if (normalized.includes("cr") && !normalized.includes("debit")) {
    return "income";
  }
  if (normalized.includes("transfer")) return "transfer";
  if (amount < 0 || normalized.includes("debit") || normalized.includes("expense") || normalized.includes("withdraw")) {
    return "expense";
  }
  return "income";
}

function parseMoney(raw: string | undefined): number | null {
  const value = raw?.trim();
  if (!value || value === "-") return null;

  const negative = value.startsWith("(") && value.endsWith(")") || /^-/.test(value) || /\bdr\b/i.test(value);
  const cleaned = value
    .replace(/[()]/g, "")
    .replace(/\b(cr|dr)\b/gi, "")
    .replace(/[^0-9.-]/g, "");
  const parsed = Number(cleaned);

  if (!Number.isFinite(parsed)) return null;
  return negative ? -Math.abs(parsed) : parsed;
}

function normalizeDate(raw: string | undefined) {
  const value = raw?.trim();
  if (!value) return null;

  const ymd = value.match(/^(\d{4})[-/](\d{1,2})[-/](\d{1,2})/);
  if (ymd) return datePartsToIso(Number(ymd[1]), Number(ymd[2]), Number(ymd[3]));

  const dmy = value.match(/^(\d{1,2})[-/](\d{1,2})[-/](\d{2,4})/);
  if (dmy) {
    const first = Number(dmy[1]);
    const second = Number(dmy[2]);
    const year = normalizeYear(Number(dmy[3]));
    const dayFirst = first > 12 || second <= 12;
    return dayFirst ? datePartsToIso(year, second, first) : datePartsToIso(year, first, second);
  }

  const named = value.match(/^(\d{1,2})[-\s]([A-Za-z]{3,})[-\s](\d{2,4})/);
  if (named) {
    const month = monthNames.get(named[2].toLowerCase());
    if (month) return datePartsToIso(normalizeYear(Number(named[3])), month, Number(named[1]));
  }

  const parsed = new Date(value);
  if (Number.isNaN(parsed.valueOf())) return null;
  return parsed.toISOString().slice(0, 10);
}

function datePartsToIso(year: number, month: number, day: number) {
  if (month < 1 || month > 12 || day < 1 || day > 31) return null;
  const date = new Date(Date.UTC(year, month - 1, day));
  if (date.getUTCFullYear() !== year || date.getUTCMonth() !== month - 1 || date.getUTCDate() !== day) {
    return null;
  }
  return date.toISOString().slice(0, 10);
}

function normalizeYear(year: number) {
  if (year >= 100) return year;
  return year >= 70 ? 1900 + year : 2000 + year;
}

function normalizeHeader(value: string) {
  return value.toLowerCase().replace(/[_()]+/g, " ").replace(/\s+/g, " ").trim();
}

function normalizeImportText(value: string) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, " ").replace(/\s+/g, " ").trim();
}

function readableCell(value: string | undefined) {
  return value?.trim() ?? "";
}

function findHeader(headers: string[], candidates: string[]) {
  return headers.findIndex((header) =>
    candidates.some((candidate) => {
      const escaped = candidate.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
      return new RegExp(`(^| )${escaped}( |$)`).test(header);
    }),
  );
}

function findExactHeader(headers: string[], candidates: string[]) {
  return headers.findIndex((header) => candidates.includes(header));
}

function fnv1a(value: string) {
  let hash = 0x811c9dc5;
  for (let index = 0; index < value.length; index += 1) {
    hash ^= value.charCodeAt(index);
    hash = Math.imul(hash, 0x01000193);
  }
  return (hash >>> 0).toString(16).padStart(8, "0");
}
