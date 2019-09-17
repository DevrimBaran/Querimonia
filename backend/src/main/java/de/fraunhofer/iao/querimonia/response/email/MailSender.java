package de.fraunhofer.iao.querimonia.response.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {

  private static void versendeEMail(String betreff, String inhalt, String absender,
                                String empfaenger) throws MessagingException {
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", "mailhost.iao.fraunhofer.de");
    Session session = Session.getDefaultInstance(properties);
    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(absender));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(empfaenger));
    message.setSubject(betreff, "ISO-8859-1");
    message.setText(inhalt, "UTF-8");
    Transport.send(message);
  }

  public static void mailTest() {
    try {
      versendeEMail("Test", "Test Mail Inhalt", "samuel.gigliotti@iao.fraunhofer.de",
          "st157598@stud.uni-stuttgart.de");
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
