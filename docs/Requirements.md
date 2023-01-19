# Requirements

1. Account creation with Name, email, phone, company, about_me, working_hours (Day -> time-interval)
   1. TODO: Create a POST API (which returns account id), Account Service, Account DTO, Account entity and Account Repository
   2. TODO: Create a working_hours table with (account_id, day, start_time_millis, end_time_millis)
   3. TODO: Create a meetings table with (account_id, day, start_time_millis, end_time_millis, requester_name, requester_email, requester_phone, requester_notes)
2. Changing default availability (days and working hours (e.g. 9AM - 5PM))
3. Create meeting link with slot size (e.g. 15 min), available date and time range
4. Opening the meeting link should show the event details and availability
5. Blocking someone's calendar with slot size, date, time, name, email, phone and notes.
6. Find overlap in schedules of 2 users
   1. TODO:
      1. Expose a GET API which takes two account IDs as query params and returns the List<Pair> of available slots
      2. Fetch the busy intervals for both users from the DB. Find the available slots for both users. Find the common available slots.