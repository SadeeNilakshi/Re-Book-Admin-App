package lk.javainstitute.rebook_admin.Mail;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class SendMail {

    private static final String EMAIL = "rebooka613@gmail.com";
    private static final String PASSWORD = "xrws zshi gjfh kvwn";

    public static void sendEmail(final Context context, final String recipient, final String subject, final String body) {
        new Thread(() -> {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);

                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "Email Sent Successfully", Toast.LENGTH_SHORT).show()
                );

                Log.d("SendMail", "Email Sent Successfully!");
            } catch (MessagingException e) {
                e.printStackTrace();

            }
        }).start();
    }
}



