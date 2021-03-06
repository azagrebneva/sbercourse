package mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring5.SpringTemplateEngine;


@Service("emailService")
public class EmailServiceImpl implements EmailService {

    private static final String NOREPLY_ADDRESS = "anna.zagrebneva@gmail.com";

    private final JavaMailSender emailSender;

    private final SimpleMailMessage template;

    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender,
                            SimpleMailMessage template, SpringTemplateEngine thymeleafTemplateEngine) {
        this.emailSender = emailSender;
        this.template = template;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @Value("classpath:/mail-templates/trust_love.jpg")
    private Resource resourceFile;

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to,
                                               String subject,
                                               String ...templateModel) {
        String text = String.format(template.getText(), templateModel);
        System.out.println(text);
        sendSimpleMessage(to, subject, text);
    }

    @Override
    public void sendMessageWithAttachment(String to,
                                          String subject,
                                          String text,
                                          String pathToAttachment) {
//        try {
//            MimeMessage message = emailSender.createMimeMessage();
//            // pass 'true' to the constructor to create a multipart message
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom(NOREPLY_ADDRESS);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text);
//
//            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
//            helper.addAttachment("Invoice", file);
//
//            emailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
    }

//    @Override
//    public void sendMessageUsingThymeleafTemplate(String to, String subject, Map<String, Object> templateModel) throws IOException, MessagingException {
//
//    }


    @Override
    public void sendMessageUsingThymeleafTemplate(
 //       String to, String subject, Map<String, Object> templateModel)
        String to, String toName, String subject, String text)
            throws MessagingException {

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("recipientName", toName);
        templateModel.put("text", text);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);

        String htmlBody = thymeleafTemplateEngine.process("template-thymeleaf.html", thymeleafContext);

        System.out.println(htmlBody);
        sendHtmlMessage(to, subject, htmlBody);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        //message.setSubject(subject, "UTF-8");
        //mimeMessage.setContent(htmlMessage, "text/html; charset=UTF-8");
        //message.setContent(htmlBody, "text/html; charset=UTF-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(NOREPLY_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline("attachment.png", resourceFile);
        emailSender.send(message);
    }
   
}
