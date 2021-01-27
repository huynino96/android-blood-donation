# android-blood-donation

Blood Point - A Blood Donation App

Ho Tuan Huy s3756868
Tran Kim Long s3755614
Tran Mach So Han s3750789
Nguyen Minh Trang s3751450

Features:
1. Let users register to be a blood donor
2. Let users post, edit and delete blood requests
3. Let users customize their profile, including blood type and division (hospital)
4. Let users view current requests posted by other users
5. Let users view nearby hospitals from GoogleMap (using Google Places API)
6. Let users search for donors of a desired blood type
7. Let users donate blood (+ count down period after last donation)
8. Let users view date of their last donation and number of total donations
9. Admins can view all users, view and delete all blood requests
10. Admins can view percentages of blood types in the database, and number of recent registrations
11. Helpful infographics on donor eligibility and blood type compatibility

Technologies Used:
- Google My Location Service
- Google Places API (retrieve nearby hospitals)
- Firebase Realtime Database for data persistence
- Broadcast Receiver (check internet connectivity across activities)
- Timer Service (track count down period after last donation)
