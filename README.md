# To do server

Service built in Ktor, based originally on this tutorial: https://www.raywenderlich.com/7265034-ktor-rest-api-for-mobile

Got it working and added a landing page route, unit tests and updated how secrets are managed to mock them in unit tests. Next steps below.

**Technologies:**
* Database: PostgreSQL, Exposed (JetBrains library to easily access the database) and Hikari (Configure the database and manage secrets).
* Authentication: JWT (JSON Web Token)

**Existing functionality:**
* **v1/ (GET):** Returns welcome message.
* **v1/users/create (POST):** Creates new user account with email, password and display name. Issues session token and returns it. Stores user details in database.
* **v1/users/login (POST):** Login using user email and password, returns session token.
* **v1/users/logout (POST):** Log out using user session, clears session.
* **v1/todos (POST):** When signed in, creates a to do passing in a description and completed / uncompleted (optional, set to uncompleted by default). Stores the todo in the database, associated with the user account.
* **v1/todos (GET):** When signed in, returns all the todos from the database.

**Steps (after completing tutorial):**
* Added landing page route
* Moved secrets from being Environment Variables to a separate file whose responses could be mocked in unit tests. (Not shown in project as it's under gitignore.
* Added unit tests including error handling (LandingRoutesTests, UserRoutesTests, ToDoRoutesTests). Still to fix some of them (mockk mocking issue related to mocking the repository response).
* Added logout functionality

**Next steps:**
* Fix remaining failing tests
* Add landing page exceptions tests
* Set up Rerouting using Status Pages (For example, if a user tries to create an account but the email exists - redirect to login page). https://ktor.io/docs/status-pages.html#redirect
* Generally, improve error handling and provide more useful error messages / redirection.
* Add new table for Event (including eventId, description, notes, event date, taskId) (This is the start of an Event reminder service (Birthdays, important dates etc) to be used by an android app.
* Link Event up with to do id, so that the user can have tasks associated with the event e.g. send a card, buy a gift.

*Landing page:*
![Create account](https://user-images.githubusercontent.com/20640903/103582497-dd232280-4ed5-11eb-933e-ed2ce074e0ff.png)

*Create account:*
![Create account](https://user-images.githubusercontent.com/20640903/103581545-25414580-4ed4-11eb-8e29-c073de2c1331.png)

*Login:*
![Login](https://user-images.githubusercontent.com/20640903/103581554-27a39f80-4ed4-11eb-8ab2-ed734d2b74cb.png)

*Add a to do:*
![Add a to do](https://user-images.githubusercontent.com/20640903/103581557-283c3600-4ed4-11eb-9fb3-1db1422982e9.png)

*See all todos:*)
![See all todos](https://user-images.githubusercontent.com/20640903/103581558-28d4cc80-4ed4-11eb-86a5-18e71def9f27.png)


**How to run it:**
* Add a text file called application.conf to resources package with the following:

￼**Other resources used:**
 https://ktor.io/docs/welcome.html
 https://ktor.io/docs/http-client-testing.html
 Medium articles / Stack Overflow





