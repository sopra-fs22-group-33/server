# shift planner SoPra FS22 Group 33

## Introduction
Many teams who are working in shifts are still using hand made plans to organize their work. This takes a lot of time for the team leader, who has to create the plan. The team members have to check the physical plan for changes on a regular basis. The goal of this project is to provide a software system which supports this process. A team leader can create a base plan where the team members get assigned automatically. Team members get their working plan directly into their calendars, and they can post their prefered shifts directly into the system. In case of a conflict, the team members can sort it
out in real time.

## Technologies
- Java 15 
- Gradle
- Spring Boot
- Sonarqube 
- Heroku
- Sendgrid e-mail API 
- optimizer lpsolve 
## High-level components

- [User](https://github.com/sopra-fs22-group-33/server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/entity/User.java) is responsible for all information of the user profile including preferred working hours and personal calendar.
- [Team](https://github.com/sopra-fs22-group-33/server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/entity/Team.java) is responsible for all information of the team. contains a team calendar where the team member can be assigned to timeslots.
- [Game](https://github.com/sopra-fs22-group-33/server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/entity/Game.java) is responsible for resolving conflicts if too many people placed a joker on the same timeslot.
- [Optimizer](https://github.com/sopra-fs22-group-33/server/blob/master/src/main/java/ch/uzh/ifi/hase/soprafs22/entity/Game.java) is responsible for automated assigning of users to timeslots according to their preferences to create the best possible plan for all users. 

## Launch & Deployment
Use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

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
GNU GENERAL PUBLIC LICENSE Version 3 or later

