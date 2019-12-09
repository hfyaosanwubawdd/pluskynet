package com.pluskynet.service;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

/**
* @author HF
* @version 创建时间：2019年12月6日 上午9:36:14
* 类说明
*/
public interface MailService {
    void sendMail(String to,String text) throws MessagingException;
}
