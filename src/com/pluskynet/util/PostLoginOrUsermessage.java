package com.pluskynet.util;

public class PostLoginOrUsermessage {
	public String Usermessage(){
		HttpRequest httpRequest = new HttpRequest();
		String usermessage = httpRequest
				.sendPost("http://114.242.17.135:8081/pluskynet/LoginAction!selectuser.action", "");
		return usermessage;
	}
	public String login(){
		return null;	
	}
}
