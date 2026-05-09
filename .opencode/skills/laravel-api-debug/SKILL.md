\---

name: laravel-api-debug

description: Use when debugging Laravel Sanctum APIs, validation errors, SQL errors, migrations, and MySQL consistency.

\---



\# Laravel API Debug Skill



Debug order:

1\. Check route in routes/api.php or routes/web.php.

2\. Check controller validation.

3\. Check model fillable fields.

4\. Check migration/table columns.

5\. Check storage/logs/laravel.log.

6\. Reproduce with PowerShell Invoke-RestMethod.



Common checks:

\- 500 usually means SQL or exception.

\- 422 means validation.

\- 401 means token/auth.

\- Unknown column means migration/schema mismatch.



Do not break:

\- Sanctum token response

\- Google login

\- email login

\- progress endpoints

