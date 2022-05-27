# SoPra RESTful Service Template FS22

## Getting started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/

## Setup this Template with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions will help you to run it more easily:
-   `pivotal.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`
-   `richardwillis.vscode-gradle`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs22` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle

You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

## Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing

Have a look here: https://www.baeldung.com/spring-boot-testing

## Introduction
Many teams who are working in shifts are still using hand made plans to organize their work. This takes a lot of time for the team leader, who has to create the plan. The team members have to check the physical plan for changes on a regular basis. The goal of this project is to provide a software system which supports this process. A team leader can create a base plan where the team members get assigned to automatically. Team members get their working plan directly into their calendars and they can post their prefered shifts or days off directly into the system. In case of a conflict, the team members can sort it
out in real time.

## Technologies
- Java 15 
- Gradle 6.8.1
- Sonarqube 3.1.1
- Heroku
- Sendgrid e-mail API 4.4.1
- optimizer lpsolve 5.5.2.0
## High-level components
- User
  - Calendar
  - PreferenceCalendar
- Team
  - Calendar
- Game
  - resolving conflicts
- Optimizer
  - assigns users to empty shifts according to their preferences
## Launch & Deployment

## Illustrations
## Roadmap
- [ ] automated finalizing of calendars
- [ ] shift swaps
- [ ] individual constraints for team members (working hours per week, number of jokers etc.) 
- [ ] export calendar as .ics
## Authors and acknowledgement
shift planner is created by vmjulia, UpstairsForest, eoeaee and grueezi

thanks to maettuu for guiding us through the semester. 

thanks to the SoPra team for your support and providing a cool course! 

## License

