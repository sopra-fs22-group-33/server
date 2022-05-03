package ch.uzh.ifi.hase.soprafs22.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;


public class EmailService {
    public static void sendEmail(String toAddress, String subject, String content) throws Exception{
        Email from = new Email("sopra.shiftplanner@gmail.com");
        //String subject = subject;
        Email to = new Email(toAddress);
        Content value = new Content("text/plain", content);
        Mail mail = new Mail(from, subject, to, value);

        //TODO add key as env var
        SendGrid sg = new SendGrid();
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}