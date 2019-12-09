package com.pluskynet.service.impl;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pluskynet.service.MailService;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

/**
* @author HF
* @version 创建时间：2019年12月6日 上午9:38:15
* 类说明
*/
public class MailServiceImpl implements MailService {
	private JavaMailSender javaMailSender;
	public JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}
	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void sendMail(String to, String text) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(to);
            helper.setFrom("hefei@pluskynet.com");
            helper.setSubject("ylt");
            helper.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
