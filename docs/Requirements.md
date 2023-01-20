# Requirements

1. Account creation with Name, email, phone, company, about_me, working_hours (Day -> time-interval)
   1. Done: Create REST APIs and Account Service, Account DTO, Account entity and Account Repository etc. backing it
2. Changing default availability (days and working hours (e.g. 9AM - 5PM))
   1. Done: Create REST APIs with a working_hours table with (account_id, day, start_time, end_time)
3. Create meeting link with slot size (e.g. 15 min), available date and time range
   1. Done: Create in_flight_meetings table with (id, account_id, date_start, date_end)
4. Requester can use the meeting link to schedule a meeting
   1. Needs testing: Create a meetings table with (id, meeting_link_id, day, start_time, end_time, requester_name, requester_email, requester_phone, requester_notes)
5. Find overlap in schedules of 2 users
   1. TODO: Expose a GET API which takes two account IDs as query params and returns the List<Pair> of available slots
   2. TODO: Fetch the busy intervals for both users from the DB. Find the available slots for both users. Find the common available slots.
