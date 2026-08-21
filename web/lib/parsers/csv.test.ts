import { strict as assert } from "node:assert";
import test from "node:test";
import {
  buildTransactionInsertPayload,
  parseCsvImport,
  withDuplicateStatuses,
} from "./csv.ts";

test("parses signed amount CSVs with quoted commas and escaped quotes", () => {
  const csv = [
    "Txn Date,Description,Amount",
    "2026-05-12,\"POS STARBUCKS, BANDRA \"\"WEST\"\"\",-612.75",
    "2026-05-13,Salary,50000.00",
  ].join("\n");

  const result = parseCsvImport(csv);

  assert.equal(result.errors.length, 0);
  assert.equal(result.candidates.length, 2);
  assert.equal(result.candidates[0].date, "2026-05-12");
  assert.equal(result.candidates[0].merchant, 'POS STARBUCKS, BANDRA "WEST"');
  assert.equal(result.candidates[0].amount, 612.75);
  assert.equal(result.candidates[0].type, "expense");
  assert.equal(result.candidates[1].amount, 50000);
  assert.equal(result.candidates[1].type, "income");
});

test("parses debit and credit split bank CSVs", () => {
  const csv = [
    "Date,Narration,Withdrawal Amount,Deposit Amount",
    "15/05/2026,UPI/123456789012/SWIGGY/food@upi,249.50,",
    "14/05/2026,SALARY MAY 2026,,50000.00",
  ].join("\n");

  const result = parseCsvImport(csv);

  assert.equal(result.candidates.length, 2);
  assert.deepEqual(
    result.candidates.map((candidate) => [candidate.date, candidate.amount, candidate.type]),
    [
      ["2026-05-15", 249.5, "expense"],
      ["2026-05-14", 50000, "income"],
    ],
  );
});

test("returns a helpful error for unknown headers", () => {
  const result = parseCsvImport("Foo,Bar,Baz\none,two,three");

  assert.equal(result.candidates.length, 0);
  assert.match(result.errors.join(" "), /recognizable columns/);
});

test("marks duplicates against existing transactions", () => {
  const result = parseCsvImport("Date,Merchant,Amount\n2026-05-12,Coffee Shop,-120.00");
  const marked = withDuplicateStatuses(result.candidates, [
    {
      date: "2026-05-12",
      amount: 120,
      type: "expense",
      merchant: "Coffee Shop",
      note: null,
      imported_hash: null,
    },
  ]);

  assert.equal(marked[0].status, "duplicate");
});

test("shapes import insert payloads", () => {
  const [candidate] = parseCsvImport("Date,Merchant,Amount\n2026-05-12,Coffee Shop,-120.00").candidates;
  const payload = buildTransactionInsertPayload(
    {
      ...candidate,
      category_id: "cat_123",
      confidence: 0.86,
    },
    "user_123",
    "acct_123",
  );

  assert.deepEqual(payload, {
    user_id: "user_123",
    account_id: "acct_123",
    category_id: "cat_123",
    amount: 120,
    type: "expense",
    date: "2026-05-12",
    merchant: "Coffee Shop",
    note: "Statement import",
    source: "import",
    ai_confidence: 0.86,
    imported_hash: candidate.importedHash,
  });
});
