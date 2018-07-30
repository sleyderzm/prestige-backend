package silicon.handler;

import com.sendgrid.*;
import silicon.model.*;
import silicon.thread.MailThread;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

public class MailHandler {

    private static void sendMails(String[] toEmails, String subject, String contentHTML){
        String toEmail = toEmails[0];
        Personalization p1 = new Personalization();
        toEmails = Arrays.copyOfRange(toEmails, 1, toEmails.length);
        p1.addTo(new Email(toEmail));
        for(String email: toEmails){
            p1.addBcc(new Email(email));
        }
        Email from = new Email(System.getenv("SENDGRID_EMAIL"));
        Email to = new Email(toEmail);
        Content content = new Content("text/html", contentHTML);
        Mail mail = new Mail(from, subject, to, content);
        mail.addPersonalization(p1);
        buildMail(mail);
    }

    private static void buildMail(Mail mail){
        MailThread mailThread = new MailThread(mail);
        mailThread.start();
    }

    private static void sendMail(String toEmail, String subject, String contentHTML){
        Email from = new Email(System.getenv("SENDGRID_EMAIL"));
        Email to = new Email(toEmail);
        Content content = new Content("text/html", contentHTML);
        Mail mail = new Mail(from, subject, to, content);
        buildMail(mail);
    }

    public static void sendSubscribeValidation(Subscriber subscriber){
        String image = "";
        String email = subscriber.getEmail();
        Project project = subscriber.getProject();
        String apiToken = subscriber.getApiToken();
        if(project.getAwsAccessKeyId() != null){
            String url = S3.getPublicURL(project.getAwsAccessKeyId());
            image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        }
        String link = System.getenv("FRONT_URL") + "/confirm_subscribe/"+apiToken;
        String button = "<a href='"+ link +"' style='box-sizing:border-box;border-color:#348eda;font-weight:400;text-decoration:none;display:inline-block;margin:0;color:#ffffff;background-color:#348eda;border:solid 1px #348eda;border-radius:2px;font-size:14px;padding:12px 45px' target='_blank'>View my status</a>";
        String subject = "Welcome to " + project.getName();
        String content = image +
                "Hello " + subscriber.getFirstName() + ", <br>" +
                "<br>" +
                "Thank you for your interest in " + project.getName() +"." +
                "<br>" +
                "By clicking on the link below, you will be able to verify and view the status of your participation." +
                "<br><br>" +
                button +
                "<br><br>" +
                "Regards,<br>" +
                "The Prestige Team";
        sendMail(email,subject,content);
    }

    public static void sendLoginLink(Subscriber subscriber){
        String image = "";
        String email = subscriber.getEmail();
        Project project = subscriber.getProject();
        String apiToken = subscriber.getApiToken();
        if(project.getAwsAccessKeyId() != null){
            String url = S3.getPublicURL(project.getAwsAccessKeyId());
            image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        }
        String link = System.getenv("FRONT_URL") + "/signin";
        String button = "<a href='"+ link +"' style='box-sizing:border-box;border-color:#348eda;font-weight:400;text-decoration:none;display:inline-block;margin:0;color:#ffffff;background-color:#348eda;border:solid 1px #348eda;border-radius:2px;font-size:14px;padding:12px 45px' target='_blank'>Login</a>";
        String subject = "Welcome to " + project.getName();
        String content = image +
                "Hello " + subscriber.getFirstName() + ", <br>" +
                "<br>" +
                "Thank you for your interest in " + project.getName() +"." +
                "<br>" +
                "By clicking on the link below and login in to your account, you will be able to view the status of your participation." +
                "<br><br>" +
                button +
                "<br><br>" +
                "Regards,<br>" +
                "The Prestige Team";
        sendMail(email,subject,content);
    }

    public static void sendNewPost(Project project){
        String image = "";
        Set<Subscriber> subscribers = project.getSubscribers();
        String[] emails = new String[subscribers.size()];
        int i = 0;
        for (Subscriber subscriber: subscribers){
            emails[i] = subscriber.getEmail();
            i++;
        }
        if(project.getAwsAccessKeyId() != null){
            String url = S3.getPublicURL(project.getAwsAccessKeyId());
            image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        }
        String link = System.getenv("FRONT_URL") + "/dashboard/subscriber_project/feed";
        String button = "<a href='"+ link +"' style='box-sizing:border-box;border-color:#348eda;font-weight:400;text-decoration:none;display:inline-block;margin:0;color:#ffffff;background-color:#348eda;border:solid 1px #348eda;border-radius:2px;font-size:14px;padding:12px 45px' target='_blank'>Go to Link</a>";
        String subject = "New Post in " + project.getName();
        String content = image +
                "Hello, <br>" +
                "<br>" +
                "The administrator of project " + project.getName() +" has posted a new message." +
                "<br>" +
                "You can see the message here:" +
                "<br><br>" +
                button +
                "<br><br>" +
                "Regards,<br>" +
                "The Prestige Team";
        sendMails(emails,subject,content);
    }

    public static void sendStatusChange(Subscriber subscriber){
        String image = "";
        Project project = subscriber.getProject();
        if(project.getAwsAccessKeyId() != null){
            String url = S3.getPublicURL(project.getAwsAccessKeyId());
            image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        }
        String link = System.getenv("FRONT_URL") + "/confirm_subscribe/"+subscriber.getApiToken();
        String button = "<a href='"+ link +"' style='box-sizing:border-box;border-color:#348eda;font-weight:400;text-decoration:none;display:inline-block;margin:0;color:#ffffff;background-color:#348eda;border:solid 1px #348eda;border-radius:2px;font-size:14px;padding:12px 45px' target='_blank'>Purchase tokens</a>";
        String subject = project.getName() + " Whitelist - Your subscription has been " + subscriber.getStatusName();
        String content = image +
                "Hello " + subscriber.getFirstName() + ", <br><br>" +
                "You are receiving this message because your subscription to the WhiteList process for " + project.getName() + " has been " + subscriber.getStatusName() + "." +
                "<br>";
        if(subscriber.getStatus() == Subscriber.STATUS_ACCEPTED){
            content = content + "Please click on the following link to purchase your Token PXCX." +
                    "<br><br>" +
                    button +
                    "<br><br>";
        }else if(subscriber.getStatus() == Subscriber.STATUS_REJECTED){
            content = content + "Thanks for applying." +
                    "<br><br>";
        }
        content = content + "Regards,<br>" +
                "The Prestige Team";

        sendMail(subscriber.getEmail(),subject,content);
    }
    
    public static void forgotPassword(User user){
        String image = "";
        String email = user.getEmail();
        String url = System.getenv("FRONT_URL") + "/assets/img/ns-logo.png";
        image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        String link = System.getenv("FRONT_URL") + "/change-password/"+user.getTokenResetPassword();
        String button = "<a href='"+ link +"' style='box-sizing:border-box;border-color:#348eda;font-weight:400;text-decoration:none;display:inline-block;margin:0;color:#ffffff;background-color:#348eda;border:solid 1px #348eda;border-radius:2px;font-size:14px;padding:12px 45px' target='_blank'>Reset Password</a>";
        String subject = "Password Reset from Prestige";
        String content = image +
                "Hello "+ user.getFirstName() + ",<br>" +
                "<br>" +
                "There was recently a request to change the password for your account." +
                "<br>" +
                "If you requested this password change, please reset your password here:" +
                "<br><br>" +
                button +
                "<br><br>" +
                "If you did not make this request, you can ignore this message and your password will remain the same." +
                "<br><br>" +
                "Thank you,<br>" +
                "The Prestige Team";
        sendMail(email,subject,content);
    }

    public static void sendCreateOrder(Order order, User user){
        String image = "";
        String email = user.getEmail();
        String url = System.getenv("FRONT_URL") + "/assets/img/ns-logo.png";
        image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        String subject = "Successful Order";
        String content = image +
                "Hello "+ user.getFirstName() + ",<br>" +
                "<br>" +
                "Your order with Transaction Id " + order.getTransactionId() + " was created successfully." +
                "<br><br>" +
                "Thank you,<br>" +
                "The Prestige Team";
        sendMail(email,subject,content);
    }

    public static void sendCheckStatusResult(CheckStatusResult checkStatusResult, User user){
        String image = "";
        String email = user.getEmail();
        String transactionId = checkStatusResult.getEcheckResponse().getTransactionId();
        String url = System.getenv("FRONT_URL") + "/assets/img/ns-logo.png";
        image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        String subject = "Status Echeck Order";
        String content = image +
                "Hello "+ user.getFirstName() + ",<br>" +
                "<br>" +
                "The status for you Echeck with TransactionId " + transactionId + " changed as below:" +
                "<br><br>" +
                checkStatusResult.getVerifyResultDescription() +
                "<br><br>" +
                "Thank you,<br>" +
                "The Prestige Team";
        sendMail(email,subject,content);
    }

    public static void sendTransactionStatusResult(Order order){
        String image = "";
        String email = order.getUser().getEmail();
        String transactionId = order.getTransactionId();
        String url = System.getenv("FRONT_URL") + "/assets/img/ns-logo.png";
        image = "<div style='width: 100%; background-color: white; padding: 30px;'><img src='"+ url +"' alt='company' width='200'><br></div><hr>";
        String subject = "Status Transaction Changed";
        String content = image +
                "Hello "+ order.getUser().getFirstName() + ",<br>" +
                "<br>" +
                "The status for you order with TransactionId " + transactionId + " changed as below:" +
                "<br><br>" +
                order.getStatusDescription() +
                "<br><br>" +
                "Thank you,<br>" +
                "The Prestige Team";
        sendMail(email,subject,content);
    }


}
