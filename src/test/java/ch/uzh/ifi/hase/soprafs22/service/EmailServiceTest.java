package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.InvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

  @Mock
  private InvitationRepository invitationRepository;

  @InjectMocks
  private EmailService emailService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

  }

  @Test
  public void sendEmail_validInputs_success() throws Exception {
    //String key = System.getenv("SENDGRID_API_KEY");
    // EmailService.sendEmail("mark.rueetschi@uzh.ch", "created user", "you created a new user");


  }
}
