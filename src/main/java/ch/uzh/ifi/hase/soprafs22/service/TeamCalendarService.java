package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.Optimizer;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import lpsolve.LpSolveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.exp;
import static java.time.temporal.ChronoUnit.DAYS;


@Service
@Transactional
public class TeamCalendarService {

    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final DayRepository dayRepository;

    private final ScheduleRepository scheduleRepository;


    @Autowired
    public TeamCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                               @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,
                               @Qualifier("dayRepository") DayRepository dayRepository, @Qualifier("scheduleRepository") ScheduleRepository scheduleRepository)
    {
        this.teamCalendarRepository = teamCalendarRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.playerRepository= playerRepository;
        this.dayRepository= dayRepository;
        this.scheduleRepository= scheduleRepository;

    }

    public  List<TeamCalendar> getCalendars() {
        return this.teamCalendarRepository.findAll();
    }

    public  TeamCalendar getCalendar(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            return foundTeam.getTeamCalendar();
      }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "team was not found");
    }

    public void updateOptimizedTeamCalendar(Long id, TeamCalendar newCalendar){
        // clean existing data from the fixed calendar

        teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();

        // create copy of the base plan
        List<Day> basePlan = new ArrayList<>();
        int latestDay = 0;
        for (Day dayFixed: newCalendar.getBasePlan()){
            if (dayFixed.getWeekday()>latestDay){
                latestDay = dayFixed.getWeekday();
            }

            Day day = new Day();
            day.setWeekday(dayFixed.getWeekday());
            List<Slot> slots = new ArrayList<>();
            for (Slot fixedSlot: dayFixed.getSlots()){
                Slot slot = new Slot();
                slot.setDay(day);
                slot.setTimeTo(fixedSlot.getTimeTo());
                slot.setTimeFrom(fixedSlot.getTimeFrom());
                slot.setRequirement(fixedSlot.getRequirement());
                slots.add(slot);
                List<Schedule> schedules = new ArrayList<>();
                for (Schedule fixedSchedule: fixedSlot.getSchedules()){
                    Schedule schedule = new Schedule();
                    schedule.setSlot(slot);
                    schedule.setBase(fixedSchedule.getBase());
                    schedule.setSpecial(fixedSchedule.getSpecial());
                    schedule.setUser(fixedSchedule.getUser());
                    schedule.setAssigned(fixedSchedule.getAssigned());
                    schedule.setFinal(true);

                    // this is not required anymore
                    fixedSchedule.setAssigned(0);
                    fixedSchedule.setSpecial(-1);
                    schedules.add(schedule);
                }
                slot.setSchedules(schedules);
            }
            day.setSlots(slots);
            basePlan.add(day);
        }


        Optional<Team> team = teamRepository.findById(id);

        if (team.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar foundCalendar = foundTeam.getTeamCalendar();
            // used the stored basePlan to fill out the fixed calendar
            int diff = (int) DAYS.between( foundCalendar.getStartingDateFixed(), foundCalendar.getStartingDate());


            for (Day day: basePlan){
                day.setTeamCalendar(foundCalendar);
                day.setWeekday(day.getWeekday()+diff);
                foundCalendar.getBasePlanFixed().add(day);

            }

            // update the dates
            //foundCalendar.setStartingDateFixed(foundCalendar.getStartingDate()); - dont update starting date fixed
            foundCalendar.setStartingDate(foundCalendar.getStartingDate().plusDays(latestDay+ 1));


            teamCalendarRepository.save(foundCalendar);
            teamCalendarRepository.flush();
        }
    }


    public TeamCalendar updatePreferences(Long id, TeamCalendar newCalendar, Long userId){
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar oldCalendar = foundTeam.getTeamCalendar();
            for (Day day: newCalendar.getBasePlan()){
                for (Slot slot: day.getSlots()){
                    for (Schedule schedule: slot.getSchedules()){
                        if( schedule.getUser().getId().equals(userId)){
                                Optional<Schedule> optinalSchedule = scheduleRepository.findById(schedule.getId());
                                if (optinalSchedule.isPresent()){
                                    Schedule foundSchedule = optinalSchedule.get();
                                    if (foundSchedule.getSlot().getDay().getTeamCalendar().getId() == oldCalendar.getId()){
                                        foundSchedule.setSpecial(schedule.getSpecial());
                                        foundSchedule.setBase(schedule.getBase());
                                    }
                                    else throw new ResponseStatusException(HttpStatus.CONFLICT, "one of the schedule does not belong to this calendar");
                                }
                                else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of the schedules is not in the database");
                        }
                    }
                }
            }

            teamCalendarRepository.save(oldCalendar);
            teamCalendarRepository.flush();
            return oldCalendar;
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "team was not found");
    }

    public boolean authorizeAdmin(Team team, String token){
        for (Membership membership : team.getMemberships()){
            if (membership.getUser().getToken().matches(token) && membership.getIsAdmin()){
                return true;
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you have no admin rights in this team");
    }

    public void mapUserPreferences(Schedule schedule){
        LocalDate  date =   schedule.getSlot().getDay().getTeamCalendar().getStartingDate().plusDays(schedule.getSlot().getDay().getWeekday());
        DayOfWeek dayofWeek = date.getDayOfWeek();
        int weekday =0;
        switch (dayofWeek) {
            case MONDAY:
                weekday = 0;
                break;
            case TUESDAY:
                weekday = 1;
                break;
            case WEDNESDAY:
               weekday = 2;
               break;
            case THURSDAY:
                weekday = 3;
                break;
            case FRIDAY:
                weekday = 4;
                break;
            case SATURDAY:
                weekday = 5;
                break;
            case SUNDAY:
              weekday = 6;
              break;
        }
        PreferenceCalendar calendar = schedule.getUser().getPreferenceCalendar();
        if (calendar!= null){
        if (calendar.getPreferencePlan()!= null){
            PreferenceDay foundDay = calendar.getPreferencePlan().get(0);
            for (PreferenceDay day:calendar.getPreferencePlan()){
                if (day.getWeekday() == weekday){
                    foundDay = day;
                    break;
                }
            }

            int sum = 0;
            int nHours = 0;
            // for each hour
            for (int t = schedule.getSlot().getTimeFrom(); t<schedule.getSlot().getTimeTo(); t++){
                        for (PreferenceSlot preferenceSlot: foundDay.getSlots()){
                            if ((preferenceSlot.getTimeFrom()<=t)&&(preferenceSlot.getTimeTo()>t)){
                                sum+= preferenceSlot.getBase();
                            }
                        }

                    nHours+=1;
            }
            if(nHours!=0){
                schedule.setBase(sum/nHours);
            }
            else schedule.setBase(0);
         }
        }
        else schedule.setBase(0);

        // set to default
        schedule.setSpecial(-1);
    }

    public TeamCalendar updateTeamCalendar(Long id, TeamCalendar newCalendar, String token){
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            authorizeAdmin(foundTeam, token);
            TeamCalendar oldCalendar = foundTeam.getTeamCalendar();
            oldCalendar.getBasePlan().clear();
            teamCalendarRepository.save(oldCalendar);
            teamCalendarRepository.flush();}


        Optional<Team> teamagain = teamRepository.findById(id);
        if (teamagain.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar oldCalendar = foundTeam.getTeamCalendar();

            for (Day day : newCalendar.getBasePlan()) {
                oldCalendar.getBasePlan().add(day);
                day.setTeamCalendar(oldCalendar);

                if (day.getSlots() != null) {
                    for (Slot slot : day.getSlots()) {
                        slot.setDay(day);
                        List<Schedule> schedules= new ArrayList<>();
                        for (Membership m: foundTeam.getMemberships()) {
                            Schedule schedule = new Schedule();
                            schedule.setUser(m.getUser());
                            schedule.setSlot(slot);
                            mapUserPreferences(schedule);
                            schedules.add(schedule);

                        }
                        slot.setSchedules(schedules);
                    }
                }
            }
            oldCalendar.setStartingDate(newCalendar.getStartingDate());

            TeamCalendar savedCalendar = teamCalendarRepository.save(oldCalendar);
            teamCalendarRepository.flush();
            return savedCalendar;
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no team");
    }

    public void addTeamMemberToCalendar(Long teamId) {

        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar calendar = foundTeam.getTeamCalendar();

            for (Day day : calendar.getBasePlan()) {
                if (day.getSlots() != null) {
                    for (Slot slot : day.getSlots()) {
                        if (slot.getSchedules() != null) {
                            List<Schedule> schedules = slot.getSchedules();
                            for (Membership m: foundTeam.getMemberships()) {
                                boolean scheduleExists = false;
                                for (Schedule schedule : schedules) {
                                    if (schedule.getUser().getId().equals(m.getUser().getId())){
                                        scheduleExists = true;
                                        break;
                                    }
                                }
                                if (!scheduleExists) {
                                    Schedule newSchedule = new Schedule();
                                    newSchedule.setUser(m.getUser());
                                    newSchedule.setSlot(slot);
                                    mapUserPreferences(newSchedule);
                                    schedules.add(newSchedule);
                                }
                            }
                        }
                    }
                }
            }

            teamCalendarRepository.save(calendar);
            teamCalendarRepository.flush();
        }

    }




    public TeamCalendar createTeamCalendar(long id, TeamCalendar newCalendar) {

        newCalendar.setStartingDateFixed(newCalendar.getStartingDate());
        //checkIfTeamHasCalendar(id);
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            foundTeam.setTeamCalendar(newCalendar);
            newCalendar.setTeam(foundTeam);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "couldn't find team");

        if (newCalendar.getBasePlan() != null){
            for (Day day : newCalendar.getBasePlan()) {
                day.setTeamCalendar(newCalendar);

                if (day.getSlots() != null) {
                    for (Slot slot : day.getSlots()) {
                        slot.setDay(day);
                        if (slot.getSchedules() != null) {
                            for (Schedule schedule : slot.getSchedules()) {
                                schedule.setSlot(slot);
                                Optional<User> user = userRepository.findById(schedule.getUser().getId());
                                if (user.isPresent()) {
                                    User foundUser = user.get();
                                    //foundUser.addSchedule(schedule);
                                    schedule.setUser(foundUser);
                                }
                                else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "couldn't find user");
                            }
                        }
                    }
                }
            }
        }

        newCalendar = teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();
        log.debug("Created calendar for Team: {}",id);
        return newCalendar;
    }

    public boolean checkCollisionsWithoutGameStart(TeamCalendar teamCalendar){
        boolean x = false;
        for (Day day : teamCalendar.getBasePlan()) {
            if (day.getSlots() != null) {
                for (Slot slot : day.getSlots()) {
                    int requirement = slot.getRequirement();
                    int assignment = 0; // make 0 - does not want, 1 - wants, -1 - no  prference
                    int possible = 0;
                    if (slot.getSchedules() != null) {
                        for (Schedule schedule : slot.getSchedules()) {
                            if (schedule.getSpecial()!=-1){
                                assignment  += schedule.getSpecial();} // should be or should not be assigned.
                            else{ possible  += 1;} // dont have special preference - could theoretically be asigned
                        }
                    }
                    if (assignment > requirement || (assignment+possible) < requirement ){
                        x = true;

                    }
                }
            }
        }
        return x;
    }

    public void deleteOldDays(Long id){
        Optional<Team> teamtest = teamRepository.findById(id);
        if (teamtest.isPresent()){
            Team foundTeam = teamtest.get();
            TeamCalendar foundCalendar = foundTeam.getTeamCalendar();
            foundCalendar.getBasePlanFixed().clear();
            foundCalendar.setStartingDateFixed(LocalDate.now());

        teamCalendarRepository.save(foundCalendar);
        teamCalendarRepository.flush();
    }
    }

    public String finalCalendarSubmission(Long id){
        // clear fixed calendar here because after optimizer it doesnt work
        Optional<Team> teamtest = teamRepository.findById(id);
        if (teamtest.isPresent()){
            Team foundTeam = teamtest.get();
            TeamCalendar foundCalendar = foundTeam.getTeamCalendar();
           //foundCalendar.getBasePlanFixed().clear();
            foundCalendar.setBusy(true);
            teamCalendarRepository.save(foundCalendar);
            teamCalendarRepository.flush();

        }
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar foundCalendar = foundTeam.getTeamCalendar();
            int res = checkCollisions(foundCalendar);


            if (res == 0){
                foundCalendar.setBusy(false);

                try {
                    new Optimizer(foundCalendar);
                    updateOptimizedTeamCalendar(id, foundCalendar);
                    return "optimizer worked";

                }

                catch (LpSolveException ex) {
                   return "Something did not work with lp solve";}

                catch (ArithmeticException ex) {
                    return "no solution found";

                }

                catch (Exception ex) {
                    return "something went wrong";
                }
            }


            else if (res ==1){
                return "there are collisions and games were started";
            }
            else { // -1
                foundCalendar.setBusy(false);
                teamCalendarRepository.save(foundCalendar);
                teamCalendarRepository.flush();

                EmailService emailService = new EmailService();
                try {

                    for (Membership membership: foundCalendar.getTeam().getMemberships()){
                        if (membership.getIsAdmin()){
                            User admin = membership.getUser();
                            emailService.sendEmail(admin.getEmail(), "collision detected",
                                    "Hi "+  admin.getUsername() + "\nSome requirements for your team" + foundCalendar.getTeam() + "cannot be satisfied. \nLog in to your shift planner account to correct them.");
                            break;
                        }
                    }

                }
                catch (Exception e) {
                    //do nothing
                }
                return "bad requirement, email sent";
            }

        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "team is not found");

    }


    public int checkCollisions(TeamCalendar teamCalendar){ // return 1 - if the game/games created; 0 - nothing is done, -1 terrible collision, inform admin
        boolean isGame = false;
        teamCalendar.setCollisions(0);

        for (Day day : teamCalendar.getBasePlan()) {
            if (day.getSlots() != null) {
                for (Slot slot : day.getSlots()) {
                    int requirement = slot.getRequirement();
                    int assignment = 0; // make 0 - does not want, 1 - wants, -1 - no  preference
                    int possible = 0;
                    int lazy = 0;

                    if (slot.getSchedules() != null) {
                        for (Schedule schedule : slot.getSchedules()) {
                           if (schedule.getSpecial()!=-1){
                            assignment  += schedule.getSpecial();} // should be or should not be assigned.
                           if(schedule.getSpecial() ==0){lazy+=1;}
                           else{ possible  += 1;} // dont have special preference - could theoretically be asigned
                        }
                    }

                    if(requirement> (assignment+possible+lazy) ){ // irresolvable collision
                        return -1;
                    }
  // PART 1 : OVERSUPPLY OF USERS
                    if ((assignment > requirement) && (assignment >1) && (requirement >0)) { // if too many people want and it is not trivial case with just one player or trivial case with no requirements.
                        isGame = true;
                        initializeGame(slot);
                        teamCalendar.setCollisions( teamCalendar.getCollisions() +1);

                        //send email notification
                        EmailService emailService = new EmailService();
                        for (Schedule schedule : slot.getSchedules()) {

                            if (schedule.getSpecial()==1){ // only send e-mail to affected users - who WANT to work
                                try {
                                    emailService.sendEmail(schedule.getUser().getEmail(), "collision detected",
                                            "Hi " + schedule.getUser().getUsername() + "\nSomeone is trying to steal your highly preferred slot. \nLog in to your shift planner account to fight for it.");
                                }
                                catch (Exception e) {
                                    //do nothing
                                }
                            }
                        }
                    }

                    // trivial collision resolution
                    // if it is just one user causing  problems, turn off his special
                    // if no requirement is set - > game is meaningless since you just need to turn off all the special 1 on that one
                    else if ((assignment > requirement) && ((assignment ==1)|| (requirement ==0))) {
                        for (Schedule schedule : slot.getSchedules()) {
                            if (schedule.getSpecial() == 1) { schedule.setSpecial(-1);
                            }
                        }
                    }

// PART 2: UNDERSUPPLY
                    //TODO this case is not possible, it is checked at line 254
                    else if ((assignment+possible) < requirement && (requirement-possible-assignment!=1|| lazy!=1 ) ){ // if there are too littleusers and it is not trivial case when just one user is a problem // TODO: make sure that there are no other corner cases
                        isGame = true;
                        initializeGame(slot);
                        teamCalendar.setCollisions( teamCalendar.getCollisions() +1);

                        //send email notification
                        EmailService emailService = new EmailService();
                        for (Schedule schedule : slot.getSchedules()) {

                            if (schedule.getSpecial()==0){ // only send e-mail to affected users - who DONT want to work
                                try {
                                    emailService.sendEmail(schedule.getUser().getEmail(), "collision detected",
                                            "Hi " + schedule.getUser().getUsername() + "\nSomeone is trying to steal your highly preferred slot. \nLog in to your shift planner account to fight for it.");
                                }
                                catch (Exception e) {
                                    //do nothing
                                }
                            }
                        }
                    }

                    // trivial collision resolution - if it is juts one lazy and he is the bottleneck - switch off his special preferences
                    else if ((assignment+possible) < requirement && (requirement-possible-assignment==1) && lazy==1 ){
                        for (Schedule schedule : slot.getSchedules()) {
                            if (schedule.getSpecial() == 0) { schedule.setSpecial(-1);
                            }
                        }
                    }
                }
            }
        }

       if (isGame){
           return 1;
       }
       else return 0;
    }

    public void initializeGame(Slot slot) {
        Game game = new Game();
        game.setStatus("on");
        game.setSlot(slot);
        slot.setGame(game);
        Random rand = new Random();

        // set board size based on number of players
        int size = (int) (60*(1- exp(-slot.getSchedules().size()/4.0)));
        if (size == 0) {
            return;
        }
        game.setBoardLength(size);


        List<Location> apples = new ArrayList<>();
        for (int j = 0; j<5; j++){
            Location apple = new Location();
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            apple.setX(x);
            apple.setY(y);
            apples.add(j, apple);
        }
        game.setApples(apples);

        // put the required players into the game
        for (Schedule schedule:slot.getSchedules()) {
            if (schedule.getSpecial() != -1) { // user has special preference, he should become player
                Player player = new Player();
                player.setUser(schedule.getUser());
                player.setGame(game);
                player.setSpecial(schedule.getSpecial());
                Location chunck = new Location();
                int x = rand.nextInt(size);
                int y = rand.nextInt(size);
                chunck.setX(x);
                chunck.setY(y);
                List<Location> chunks = new ArrayList<>();
                chunks.add(chunck);
                player.setChunks(chunks);

                playerRepository.save(player);
                playerRepository.flush();
            }
        }
    }
}
