package silicon.thread;

import com.sendgrid.*;

import java.io.IOException;

public class MailThread extends Thread {

    private Mail mail;

    public MailThread(Mail mail) {
        this.mail = mail;
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public void run() {
        Request request = new Request();
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
