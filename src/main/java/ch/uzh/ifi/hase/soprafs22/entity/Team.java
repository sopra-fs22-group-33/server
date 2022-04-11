package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TEAM")
public class Team implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToMany (fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinTable(
    name = "team_user",
    joinColumns = @JoinColumn(name = "user_id"), 
    inverseJoinColumns = @JoinColumn(name = "team_id"))
  private Set<User>  users = new HashSet<User>();

  @OneToOne(mappedBy = "team", cascade = CascadeType.ALL)
  @JoinColumn
  private TeamCalendar teamCalendar;

/*TODO  
 @Column(nullable = false)
  private List<Role>  roles; */

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<User> getUsers(){
    return users;
  }

  public void setUsers(Set<User> users){
    this.users = users;
  }

  public void addUser(User user){
    if (!this.getUsers().contains(user)){
      this.users.add(user);
      user.getTeams().add(this);
    }
  }

  public void removeUser(long userId){
    User user = this.users.stream().filter(t -> t.getId() == userId).findFirst().orElse(null);
    if (user != null) this.users.remove(user);
    user.getTeams().remove(this);
  }

    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }

    public void setTeamCalendar(TeamCalendar teamCalendar) {
        this.teamCalendar = teamCalendar;
    }


}