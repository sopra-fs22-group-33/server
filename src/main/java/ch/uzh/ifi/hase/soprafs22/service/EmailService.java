package ch.uzh.ifi.hase.soprafs22.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


public class EmailService {

    @Autowired
    public EmailService() {
    }

    public void sendEmail(String toAddress, String subject, String content) throws Exception{
        Email from = new Email("sopra.shiftplanner@gmail.com");
        //String subject = subject;
        Email to = new Email(toAddress);
        Content value = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, to, value);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
    }
}
