\---

name: android-room-sync

description: Use when debugging Android Kotlin Room Retrofit sync, especially lesson progress by slug.

\---



\# Android Room Sync Skill



For Android progress bugs:

1\. Check Retrofit DTO field names.

2\. Check repository mapping.

3\. Check Room entity primary keys.

4\. Check DAO insert/replace behavior.

5\. Check ViewModel userId lifecycle.

6\. Check Compose state observation.



Required logs:

\- PROGRESS\_SYNC

\- PROGRESS\_ROOM

\- PROGRESS\_UI



Rules:

\- Store lesson\_slug, not numeric ID.

\- Compare UI using exact slug.

\- Keep user progress isolated by userId.

\- Do not modify backend unless API response is wrong.



