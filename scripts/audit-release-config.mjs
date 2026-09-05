import { readFileSync, existsSync } from "node:fs";

const requiredEnv = [
  "NEXT_PUBLIC_SUPABASE_URL",
  "NEXT_PUBLIC_SUPABASE_ANON_KEY",
  "PENNYRUSH_SUPABASE_URL",
  "PENNYRUSH_SUPABASE_ANON_KEY",
  "SUPABASE_SERVICE_ROLE_KEY",
  "PENNYRUSH_RELEASE_KEYSTORE_BASE64",
  "PENNYRUSH_RELEASE_STORE_FILE",
  "PENNYRUSH_RELEASE_STORE_PASSWORD",
  "PENNYRUSH_RELEASE_KEY_ALIAS",
  "PENNYRUSH_RELEASE_KEY_PASSWORD",
];

const requiredFiles = [
  ".env.example",
  ".github/workflows/release-build.yml",
  ".github/workflows/verify.yml",
  "android/app/build.gradle.kts",
  "android/app/src/main/AndroidManifest.xml",
  "android/fastlane/metadata/android/en-US/title.txt",
  "android/fastlane/metadata/android/en-US/short_description.txt",
  "android/fastlane/metadata/android/en-US/full_description.txt",
  "android/fastlane/metadata/android/en-US/privacy_url.txt",
  "docs/play-store-listing.md",
  "docs/play-data-safety.md",
  "docs/production-deployment.md",
  "docs/privacy-policy.md",
  "docs/release-checklist.md",
  "SUPPORT.md",
  "scripts/audit-android-artifacts.mjs",
  "scripts/audit-privacy-surface.mjs",
  "scripts/audit-store-metadata.mjs",
  "scripts/check-production-web.mjs",
  "scripts/run-release-verify.mjs",
  "web/app/api/account/delete/route.ts",
  "web/app/manifest.ts",
  "web/app/privacy/page.tsx",
  "web/app/robots.ts",
  "web/app/sitemap.ts",
  "web/app/terms/page.tsx",
  "web/next.config.mjs",
];

const checks = [
  {
    file: ".github/workflows/release-build.yml",
    values: [
      "npm run release:check-env",
      "npm run release:verify",
      "PENNYRUSH_RELEASE_STORE_FILE",
      "PENNYRUSH_REQUIRE_SIGNED_ANDROID",
      "pennyrush-release-aab",
    ],
  },
  {
    file: ".github/workflows/verify.yml",
    values: [
      "npm audit",
      "npm run web:test",
      "npm run web:lint",
      "npm run web:typecheck",
      "npm run web:build",
      "testDebugUnitTest",
      ":app:lintRelease",
      "assembleDebug",
    ],
  },
  {
    file: "android/app/build.gradle.kts",
    values: [
      'applicationId = "dev.pennyrush.app"',
      'versionName = "1.0.0"',
      "PENNYRUSH_WEB_BASE_URL",
      "https://pennyrush.dev",
      "PENNYRUSH_RELEASE_STORE_FILE",
      "PENNYRUSH_RELEASE_STORE_PASSWORD",
      "PENNYRUSH_RELEASE_KEY_ALIAS",
      "PENNYRUSH_RELEASE_KEY_PASSWORD",
    ],
  },
  {
    file: "android/app/src/main/AndroidManifest.xml",
    values: [
      "android:usesCleartextTraffic=\"false\"",
      "android:networkSecurityConfig=\"@xml/network_security_config\"",
      "pennyrush",
      "auth-callback",
    ],
  },
  {
    file: "web/next.config.mjs",
    values: [
      "Strict-Transport-Security",
      "X-Content-Type-Options",
      "X-Frame-Options",
      "Referrer-Policy",
      "Permissions-Policy",
    ],
  },
  {
    file: "web/app/api/account/delete/route.ts",
    values: ["SUPABASE_SERVICE_ROLE_KEY", "admin.auth.admin.deleteUser", "Bearer"],
  },
  {
    file: "docs/release-checklist.md",
    values: [
      "npm run release:verify",
      "Upload a signed AAB",
      "docs/play-data-safety.md",
      "Delete a throwaway account",
      "Play Data Safety",
      "no third-party tracking SDKs",
      "https://github.com/royalpinto007/PennyRush/issues",
      "docs/production-deployment.md",
    ],
  },
  {
    file: "docs/production-deployment.md",
    values: [
      "GitHub Actions secrets",
      "Vercel",
      "Supabase",
      "Google Play Console",
      "npm run release:verify",
      "Signed AAB",
      "Production smoke test",
    ],
  },
  {
    file: "scripts/audit-store-metadata.mjs",
    values: [
      "Store metadata audit passed.",
      "short_description.txt",
      "full_description.txt",
      "https://pennyrush.dev/privacy",
    ],
  },
  {
    file: "scripts/audit-android-artifacts.mjs",
    values: [
      "PENNYRUSH_REQUIRE_SIGNED_ANDROID",
      "PENNYRUSH_RELEASE_STORE_FILE",
      "android/app/build/outputs/apk/release/output-metadata.json",
      "android/app/build/outputs/bundle/release/app-release.aab",
      "Android artifact audit passed",
    ],
  },
  {
    file: "scripts/audit-privacy-surface.mjs",
    values: [
      "android.permission.INTERNET",
      "android.permission.POST_NOTIFICATIONS",
      "android.permission.READ_SMS",
      "firebase-(analytics|crashlytics|perf|messaging)",
      "Privacy surface audit passed",
    ],
  },
  {
    file: "package.json",
    values: ["release:audit-android", "release:audit-privacy"],
  },
  {
    file: "scripts/check-production-web.mjs",
    values: [
      "PENNYRUSH_PRODUCTION_WEB_URL",
      "strict-transport-security",
      "x-content-type-options",
      "Production web check passed",
      "/privacy",
      "/terms",
      "/robots.txt",
      "/sitemap.xml",
    ],
  },
  {
    file: "package.json",
    values: ["release:check-production"],
  },
  {
    file: "SUPPORT.md",
    values: ["https://github.com/royalpinto007/PennyRush/issues"],
  },
  {
    file: "web/app/privacy/page.tsx",
    values: ["github.com/royalpinto007/PennyRush/issues"],
  },
  {
    file: "web/app/terms/page.tsx",
    values: ["github.com/royalpinto007/PennyRush/issues"],
  },
  {
    file: "docs/play-data-safety.md",
    values: [
      "Ads: No",
      "Third-party tracking SDKs: No",
      "Account deletion: Yes",
      "Raw CSV statement files are not uploaded",
      "POST_NOTIFICATIONS",
      "does not request SMS",
    ],
  },
  {
    file: "scripts/run-release-verify.mjs",
    values: [
      "release:audit-config",
      "web:test",
      "release:audit-privacy",
      "web:lint",
      "web:typecheck",
      "web:build",
      ":app:compileDebugKotlin",
      ":app:lintRelease",
      ":app:bundleRelease",
      "release:audit-android",
    ],
  },
];

const failures = [];

for (const file of requiredFiles) {
  if (!existsSync(file)) {
    failures.push(`Missing required release file: ${file}`);
  }
}

for (const file of [".env.example", "docs/release-checklist.md"]) {
  const body = existsSync(file) ? readFileSync(file, "utf8") : "";
  for (const envName of requiredEnv) {
    if (!body.includes(envName)) {
      failures.push(`${file} does not mention ${envName}`);
    }
  }
}

const releaseWorkflow = existsSync(".github/workflows/release-build.yml")
  ? readFileSync(".github/workflows/release-build.yml", "utf8")
  : "";
for (const envName of requiredEnv.filter((name) => name !== "PENNYRUSH_RELEASE_STORE_FILE")) {
  if (!releaseWorkflow.includes(envName)) {
    failures.push(`release-build.yml does not pass ${envName}`);
  }
}

for (const check of checks) {
  const body = existsSync(check.file) ? readFileSync(check.file, "utf8") : "";
  for (const value of check.values) {
    if (!body.includes(value)) {
      failures.push(`${check.file} does not include expected release marker: ${value}`);
    }
  }
}

if (failures.length > 0) {
  console.error("Release configuration audit failed:");
  for (const failure of failures) {
    console.error(`- ${failure}`);
  }
  process.exit(1);
}

console.log("Release configuration audit passed.");
