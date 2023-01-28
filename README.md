# Read Me
A Kotlin + Springboot implementation of the MVP for Calendly-like app following RESTful API design guidelines.

## Build instructions
1. Install latest JDK 8
2. Install Kotlin v1.6 (Recommendation is to install IntelliJ IDEA as it comes bundled with Kotlin and makes build tasks simpler) 
3. Clone the project from [GitHub](https://github.com/saurabhagas/calendly)
4. Build the project using `./gradlew build`
5. Start the application using `./gradlew bootRun` and access it at `localhost:8080`

## Project structure
1. `${projectRoot}/src/main/kotlin` contains the source code where:
   1. The API entrypoints are defined in `com.harbor.calendly.controller` package in `*Controller` classes.
   2. The business logic for the entrypoints in defined in corresponding `*Service` classes.
   3. `*DTO` classes contain the payload definitions.
   4. `*Repository` classes contain the wrapper for interacting with the database.
   5. `*Entity` classes define the object equivalent of a SQL table. These are used in conjunction with `*Repository` classes.
   6. Other miscellaneous classes to support the logic and organize code better.
   7. `CalendlyApplication.kt` is the executable.
2. `${projectRoot}/src/test/kotlin` contains the source code where:
   1. `*.rest` files contain HTTP requests which can be sent to the server manually.
   2. `AcceptanceTests.kt` contains a demo of the desired functionalities in the MVP.
   3. `*.Test` contain integration tests for each API root entrypoint.

## Supported features
1. Creating an account via `/accounts` resource.
2. Creating an availability for given registered user via `/accounts/{accoundId}/availability` resource.
3. Finding overlaps in availabilities using `/accounts/{accoundId}/overlap` resource.
4. Creating a meeting link for a registered user via `/accounts/{accoundId}/meeting-links` resource.
5. Using the meeting link to block a registered user's calendar via `/accounts/{accoundId}/meeting-links/{meetingLinkId}/meetings` resource.

## Testing the functionality
### Testing via sending http requests on the deployed instance
1. The application is hosted at `54.234.192.121:8080`
2. Use the `acceptance-tests.rest` file (located in `${projectRoot}/src/test/` directory along with other `*.rest` files) to send HTTP requests to the server running at `54.234.192.121:8080`

### Testing via sending http requests with a locally running instance
1. Start the application locally by running `./gradlew bootRun`
`*.rest` files containing HTTP requests are contained in `${projectRoot}/src/test/` directory, which can be run by
following steps as mentioned in the [previous heading](#testing-via-acceptance-tests).

### Testing via acceptance tests on local machine
Run `AcceptanceTests.kt` test suite. The file contains a demo of the desired functionalities in the MVP.
Steps to run:
1. `AcceptanceTests.kt` can be executed directly from IntelliJ by clicking on the 'Run' button next to the class name
2. `acceptance-tests.rest` contain the http requests equivalent, which can be directly run from IntelliJ with the help of "HTTP Client" plugin. `curl` can also be used to send these requests.
3. Run `./gradlew test --tests "com.harbor.calendly.AcceptanceTests"` from project root

### Testing via integration tests on local machine
Integration tests for each API root entrypoint are contained in `${projectRoot}/src/test/kotlin` directory, which can be run by
following steps as mentioned in the [previous heading](#testing-via-acceptance-tests)

## Implementation notes
Following simplifications were made in the implementation
1. No persistence in database. In-memory H2 relational database was used to simplify testing and deployment
2. No support for syncing with availability with calendars like Google and Apple calendar. This can be built by leveraging the availability overlap finding logic in `OverlapFinder.kt` class though.
