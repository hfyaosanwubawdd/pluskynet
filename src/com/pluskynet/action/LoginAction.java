package com.pluskynet.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.pluskynet.batch.BatchConstant;
import com.pluskynet.domain.User;
import com.pluskynet.quartz.DataUpdateQuartz;
import com.pluskynet.service.LoginService;
import com.pluskynet.service.MailService;
import com.pluskynet.util.BaseAction;
import com.pluskynet.util.JedisUtils;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

public class LoginAction extends BaseAction {
	/**
	 * 用户登录和退出
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	private LoginService loginService;
	private Logger LOGGER = Logger.getLogger(this.getClass());
	
	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public void login() {
		String msg = "成功";
		msg = loginService.login(user);
		System.out.println(user.toString());
		outJsonByMsg(msg);
	}

	public void dataReport() {
		outJsonByMsg(DataUpdateQuartz.getDataInfo(),"成功");
	}
	public void logout() {
		user = (User) ActionContext.getContext().getSession().get("user");
		if (user!=null && user.getUsername().equals(user.getUsername())) {
			ActionContext.getContext().getSession().remove("user");
			outJsonByMsg("成功");
			return;
		}
		outJsonByMsg("未登录");
	}
	public void selectuser() {
		user = (User) ActionContext.getContext().getSession().get("user");
		if (user!=null) {
			Map map = new HashMap();
			map.put("name", user.getName());
			map.put("username", user.getUsername());
			map.put("role", user.getRolecode());
			map.put("userid", user.getUserid());
			outJsonByMsg(map, "成功");
			return;
		}
		outJsonByMsg("未登录");
	}

	@Override
	public Object getModel() {
		user = new User();
		return user;
	}
	
	private MailService mailService;
	private String to;
	private String text;
	public MailService getMailService() {
		return mailService;
	}
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public void sendMail() {
		String key = "";
		if ("hfyaosanwubawdd@163.com".equals(to)) {
			key =  BatchConstant.REDIS_MAIL_KEY_HF.format(new Date())+to;
		}
		key = BatchConstant.REDIS_MAIL_KEY.format(new Date())+to;
		if (JedisUtils.existsObject(key)) {
			LOGGER.info(to+" already has sent ");
			return;
		}
		JedisUtils.setObject(key, new Date().toString(), 3600*24);
		try {
			mailService.sendMail(to, text);
			LOGGER.info(to+" ok");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
