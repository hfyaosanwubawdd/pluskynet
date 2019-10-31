package com.pluskynet.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.pluskynet.domain.User;
import com.pluskynet.quartz.DataUpdateQuartz;
import com.pluskynet.service.LoginService;
import com.pluskynet.util.BaseAction;

public class LoginAction extends BaseAction {
	/**
	 * 用户登录和退出
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	private LoginService loginService;

	
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
}
