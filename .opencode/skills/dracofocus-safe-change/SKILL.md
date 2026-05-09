\---

name: dracofocus-safe-change

description: Use this skill before modifying DracoFocus code. It enforces minimal safe changes and prevents breaking auth, progress sync, or lesson slugs.

\---



\# DracoFocus Safe Change Skill



Before modifying files:

1\. Identify the exact bug or mismatch.

2\. List the files that need changes.

3\. Confirm what must NOT be touched.

4\. Prefer smallest possible diff.



Never change:

\- Google login

\- Email login

\- Register

\- TokenManager

\- Sanctum auth flow

\- Existing lesson slugs

\- Progress sync contract:

&#x20; - POST /api/progress

&#x20; - GET /api/progress

&#x20; - completed\_lessons

&#x20; - completed\_lesson\_ids



After changes:

1\. Show modified files.

2\. Explain why each change was needed.

3\. Give test commands.

4\. Give commit message.

