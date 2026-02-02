package com.resumequill.app.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailService {
  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private final Session session;
  private final String fromEmail;
  private final String fromName;
  private final Configuration freemarkerConfig;

  public EmailService(
    @Value("${mail.smtp.host:smtp.gmail.com}") String smtpHost,
    @Value("${mail.smtp.port:587}") String smtpPort,
    @Value("${mail.username:}") String username,
    @Value("${mail.password:}") String password,
    @Value("${mail.from.name:ResumeQuill}") String fromName
  ) {
    this.fromEmail = username;
    this.fromName = fromName;

    Properties props = new Properties();
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", smtpPort);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    this.session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_33);
    this.freemarkerConfig.setClassLoaderForTemplateLoading(
      getClass().getClassLoader(), "email-templates"
    );
    this.freemarkerConfig.setDefaultEncoding("UTF-8");
  }

  public void sendPasswordResetEmail(String toEmail, String resetLink) {
    try {
      Map<String, Object> model = Map.of(
        "resetLink", resetLink
      );

      String htmlContent = processTemplate("password-reset.ftl", model);

      sendHtmlEmail(toEmail, "Reset Your Password", htmlContent);

      logger.info("Password reset email sent to: {}", toEmail);
    } catch (Exception e) {
      logger.error("Failed to send password reset email to: {}", toEmail, e);
      throw new RuntimeException("Failed to send email", e);
    }
  }

  private String processTemplate(String templateName, Map<String, Object> model) throws Exception {
    Template template = freemarkerConfig.getTemplate(templateName);
    StringWriter writer = new StringWriter();
    template.process(model, writer);
    return writer.toString();
  }

  private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
    MimeMessage message = new MimeMessage(session);

    message.setFrom(new InternetAddress(fromEmail, fromName));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    message.setSubject(subject);
    message.setContent(htmlContent, "text/html; charset=utf-8");

    Transport.send(message);
  }
}
