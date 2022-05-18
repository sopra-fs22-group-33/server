package ch.uzh.ifi.hase.soprafs22.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EmailServiceTest {

  @Mock
  private EmailService emailService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

  }

  @Test
  public void sendEmail_validInputs_success() throws Exception {
    String key = System.getenv("SENDGRID_API_KEY");
    emailService.sendEmail("test@123uzh.ch", "created user", "you created a new user");
    Mockito.verify(emailService, Mockito.times(1)).sendEmail(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
  }
}
