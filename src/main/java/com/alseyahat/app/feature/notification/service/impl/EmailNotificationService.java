package com.alseyahat.app.feature.notification.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.alseyahat.app.feature.notification.dto.EmailDto;
import com.alseyahat.app.feature.notification.service.NotificationService;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailNotificationService implements NotificationService<EmailDto> {


    JavaMailSender mailSender;

    Configuration freemarkerConfig;

    @Override
    public void prepareAndSendMessage(final EmailDto data, final String notificationType, final Locale locale, final String template) {
        log.info("Start Sending email to: {} with content: {}", data.getTo(), data.getContent());
        final MimeMessage message = mailSender.createMimeMessage();
        try {
        final MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setFrom(data.getFrom());
        helper.setTo(data.getTo());
        if(data.getCc()!=null)
        helper.setCc(data.getCc());
        helper.setSubject(data.getSubject());
        final Template t = freemarkerConfig.getTemplate("/" + notificationType + "/" + locale.toString() + template);
        helper.setText(FreeMarkerTemplateUtils.processTemplateIntoString(t, data.getContent()), true);
        mailSender.send(message);
        }catch(Exception ex) {
        	System.out.println("Error sending in email -------------"+ex);
        }
        log.info("End Sending email to: {} with content: {}", data.getTo(), data.getContent());
    }
}
