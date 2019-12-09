package com.pluskynet.interceptors;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.struts2.StrutsStatics;
import org.jsoup.helper.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.pluskynet.batch.BatchConstant;
import com.pluskynet.util.JedisUtils;

/**
* @author HF
* @version 创建时间：2019年12月3日 下午2:37:15
* 类说明
*/
public class IpCountInterceptor extends MethodFilterInterceptor{
	private Logger LOGGER = Logger.getLogger(this.getClass());
	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		
		HttpServletRequest request = (HttpServletRequest)ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
		String ip = request.getHeader("x-forwarded-for");
    	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    		ip = request.getHeader("Proxy-Client-IP");
    	}
    	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    		ip = request.getHeader("WL-Proxy-Client-IP");
    	}
    	if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    		ip = request.getRemoteAddr();
    	}
    	if (StringUtil.isBlank(ip)) {
			ip = "unknown";
		}
    	if (JedisUtils.existsObject(BatchConstant.REDIS_BLACK_IP+ip)) {
    		return "Excessive number of requests.";
		}
    	String key = ip+BatchConstant.REDIS_INCR_KEY.format(new Date());
    	long incrCount = JedisUtils.incrCount(key);
		if (incrCount == 1) {
			JedisUtils.expire(key, 1);
		}
		if (incrCount > 100) {
			JedisUtils.setObject(BatchConstant.REDIS_BLACK_IP+ip, "Excessive number of requests.", 3600*24*7);
			LOGGER.info("Excessive number of requests. ip : "+ip);
			return null;
		}
		return invocation.invoke();
	}
	
	 /**
	  * 改用正则判断是否内网ip
	  * @param ip
	  * @return
	  */
	private boolean innerIP(String ip) {
		 Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
	     Matcher match = reg.matcher(ip);
	     return match.find();
	}
}
