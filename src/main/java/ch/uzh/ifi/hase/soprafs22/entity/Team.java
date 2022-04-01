package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

  @ManyToMany
  @JoinTable(
    name = "team_user",
    joinColumns = @JoinColumn(name = "user_id"), 
    inverseJoinColumns = @JoinColumn(name = "team_id"))
  private Set<User>  users = new HashSet<User>();

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

/*   public void addUser(User user){
      if (!getUsers().contains(user)){
        getUsers().add(user);
      }
  }

  public Collection<User> getUsers() {
    return users;
  }   */
}