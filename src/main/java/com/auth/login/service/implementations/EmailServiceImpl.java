package com.auth.login.service.implementations;

import com.auth.login.model.entities.VerificationCode;
import com.auth.login.model.repository.VerificationCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class EmailServiceImpl {

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public EmailServiceImpl(JavaMailSender mailSender, VerificationCodeRepository verificationCodeRepository) {
        this.mailSender = mailSender;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    /**
     * Sender verification codes to an email
     * @param email
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    @Transactional
    public void sendVerificationCode(String email) throws MessagingException, UnsupportedEncodingException {
        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        verificationCodeRepository.deleteByEmail(email);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCodeRepository.save(verificationCode);


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Código de verificación para cambiar contraseña");
        helper.setText("Tu código de verificación es: " + code);
        helper.setFrom("no-reply@app.com", "Soporte de Mi App");

        System.out.println("Enviando al : "+email);

        mailSender.send(message);
    }
}
