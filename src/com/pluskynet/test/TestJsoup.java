package com.pluskynet.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
* @author HF
* @version 创建时间：2019年11月8日 下午4:39:24
* 类说明
*/
public class TestJsoup {

	public static void main(String[] args) {
		String ip = "103.105.126.30";
		int port = 83;
		String url = "https://www.baidu.com/baidu?wd=ip&tn=monline_7_dg&ie=utf-8";
		getDocByJsoup(url, ip, port);
	}
	
	
	public static void getDocByJsoup(String href,String ip,int port) {
		try {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
			URL url = new URL(href);
			URLConnection urlcon = url.openConnection(proxy);
			urlcon.connect(); // 获取连接
			InputStream is = urlcon.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
			StringBuffer bs = new StringBuffer();
			String l = null;
			while ((l = buffer.readLine()) != null) {
				bs.append(l);
			}
			System.out.println(bs.toString());
			Document doc = Jsoup.parse(bs.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
