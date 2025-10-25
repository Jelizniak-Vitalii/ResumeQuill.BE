package com.resumequill.app;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class ResumeQuillApplication {
	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);

		// Disable TLD scanning (speeds up startup if there is no JSP)
		System.setProperty("org.apache.catalina.startup.TldConfig.jarsToSkip", "*");

		Connector connector = tomcat.getConnector();
		connector.setProperty("maxThreads", "500");
		connector.setProperty("minSpareThreads", "20");
		connector.setProperty("acceptCount", "200");
		connector.setProperty("connectionTimeout", "30000");
		connector.setProperty("keepAliveTimeout", "15000");
		connector.setProperty("maxKeepAliveRequests", "100");

		tomcat.addWebapp("", new File(".").getAbsolutePath());

		tomcat.start();
		tomcat.getServer().await();
	}
}
