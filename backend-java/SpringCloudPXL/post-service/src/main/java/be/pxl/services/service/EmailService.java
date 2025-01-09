package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendMail(Post post, Review review) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(post.getAuthorEmail());
        message.setSubject(post.getStatus().toString() + " - " + post.getTitle());
        message.setText(review.getDescription());

        javaMailSender.send(message);
        log.info("Email sent to " + post.getAuthorEmail());
    }
}
