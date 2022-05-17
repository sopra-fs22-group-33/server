package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.Optimizer;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.lang.Math.exp;


@Service
@Transactional
public class TeamCalendarService {

    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final DayRepository dayRepository;

    private final ScheduledExecutorService executorService;


    @Autowired
    public TeamCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                               @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,
                               @Qualifier("dayRepository") DayRepository dayRepository) {
        this.teamCalendarRepository = teamCalendarRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.playerRepository= playerRepository;
        this.dayRepository= dayRepository;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
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
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public void updateOptimizedTeamCalendar(Long id, TeamCalendar newCalendar){
        teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();
    }

    public TeamCalendar updateTeamCalendar(Long id, TeamCalendar newCalendar){
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
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
                        if (slot.getSchedules() != null) {
                            for (Schedule schedule : slot.getSchedules()) {
                                schedule.setSlot(slot);
                                Optional<User> user = userRepository.findById(schedule.getUser().getId());
                                if (user.isPresent()) {
                                    User foundUser = user.get();
                                    //foundUser.addSchedule(schedule);
                                    schedule.setUser(foundUser);
                                }
                                else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                            }
                        }
                    }
                }
            }

            TeamCalendar savedCalendar = teamCalendarRepository.save(oldCalendar);
            teamCalendarRepository.flush();
            return savedCalendar;
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public TeamCalendar createTeamCalendar(long id, TeamCalendar newCalendar) {

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
                                else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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

    public String finalCalendarSubmission(Long id){
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar foundCalendar = foundTeam.getTeamCalendar();
            int res = checkCollisions(foundCalendar);


            if (res == 0){

                Runnable task = () ->{
                    try{
                        new Optimizer(foundCalendar);
                        updateOptimizedTeamCalendar(id, foundCalendar);}
                    catch (Exception ex){
                        log.debug("somehting probbaly went wrong with the lp_solve");
                    }
                     // here is the old optimizer used because the new one is not on this branch - TODO: DONT FORGET TO CHANGE THIS LINE
                    // TODO: CHECK THE EXCEPTIONS AGAIN
                };


                try{

                    this.executorService.execute(task);
                }
                catch (Exception ex) {
                    ; // I dont know why this could go wrong
                }

                return "optimizer is working";
            }
            else if (res ==1){
                return "there are collisions and games were started";
            }
            else { // -1 case
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
                return "admin has stupid requirement, I have sent him email";
            }

        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "team is not found");

    }


    public int checkCollisions(TeamCalendar teamCalendar){ // return 1 - if the game/games created; 0 - nothing is done, -1 terrible collision, inform admin
        boolean isGame = false;
        boolean isBadCollision = false;

        teamCalendar.setCollisions(0); // remove this
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

                    if(requirement> (assignment+possible) ){ // irresolvable collision
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
        Random rand = new Random();

        // set board size based on number of players
        int size = (int) (100*(1- exp(-slot.getSchedules().size()/4.0)));
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
