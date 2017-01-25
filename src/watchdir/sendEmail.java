/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchdir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author hbakiewicz
 */
public class sendEmail implements Runnable {

    String email_from;
    String SMTP_HOST;
    String SMTP_PORT;
    String SMTP_USER;
    String SMTP_PASSWORD;
    String email_to;
    String mail_subject;
    String mail_body;
    String EDI_PATH;
    File attachemnet;
    String NRDOK;
    boolean delAS = false;

    public sendEmail(String email_from, String email_to, File attachemnet, String cfg_file, String _nrDok) {
        this.email_from = email_from;
        this.email_to = email_to;
        this.attachemnet = attachemnet;
        this.NRDOK = _nrDok;

        Properties prop = new Properties();

        try {

            InputStream input = new FileInputStream(cfg_file);

            prop.load(input);
            SMTP_HOST = prop.getProperty("mail_smtp_host", "");
            SMTP_PORT = prop.getProperty("mail_smtp_port", "");
            SMTP_USER = prop.getProperty("mail_smtp_user", "");
            SMTP_PASSWORD = prop.getProperty("mail_smtp_password", "");
            mail_body = prop.getProperty("mail_body", "");
            EDI_PATH = prop.getProperty("edi_path", "");
            mail_subject = prop.getProperty("mail_subject", "");
            delAS = Boolean.valueOf(prop.getProperty("delete_edi_file_after_send", "0"));

        } catch (IOException ex) {
            System.err.println("brak pliku config.properties");
            System.exit(1);
        }

    }

    public void setAttachemnet(File attachemnet) {
        this.attachemnet = attachemnet;
    }

    @Override
    public void run() {
        send(this.email_to, this.attachemnet);
    }

    private boolean send(String _email_to, File Attachemnt) {
        Session session = null;
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", SMTP_PORT);
            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                }
            });
        } catch (Exception e) {
            System.err.println(e);
        }

        try {
            
                     // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(this.email_from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(_email_to));

         // Set Subject: header field
         message.setSubject(this.mail_subject + NRDOK);

         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         // Now set the actual message
         messageBodyPart.setText(mail_body);

         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment
         messageBodyPart = new MimeBodyPart();
         
         String file = EDI_PATH + "\\" + Attachemnt.getName();
            String fileName = Attachemnt.getName();
         String filename = fileName;
         DataSource source = new FileDataSource(file);
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(filename);
         multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(multipart);
            
            
            
            /*

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.email_from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(_email_to));
            message.setSubject(this.mail_subject + NRDOK);
            message.setText(mail_body);
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            messageBodyPart = new MimeBodyPart();
            String file = EDI_PATH + "\\" + Attachemnt.getName();
            String fileName = Attachemnt.getName();
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            messageBodyPart.addHeader("Content-Type", "application/octet-stream; name=" + fileName);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
*/
            Transport.send(message);

            System.out.println("Wysłano : " + NRDOK + " do : " + _email_to);

            if (delAS) {
                if (new File(file).delete()) {
                    System.out.println("skasowano plik : " + file);

                } else {
                    System.err.println("Błąd kasowania pliku : " + file);
                }
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
