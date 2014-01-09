package com.marklogic.hello.spring;

import org.springframework.beans.factory.FactoryBean;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public class DatabaseClientFactoryBean implements FactoryBean<DatabaseClient> {
	private String hostname;
	private int port;
	private String username;
	private String password;

	public DatabaseClient getObject() throws Exception {
		return DatabaseClientFactory.newClient("localhost", 8003, "admin",
				"admin", Authentication.DIGEST);
	}

	public Class<?> getObjectType() {
		return DatabaseClient.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
