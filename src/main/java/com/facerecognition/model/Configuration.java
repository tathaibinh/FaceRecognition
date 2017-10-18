/**
 * 
 * FACE_RECOGNITION PROPRIETARY
 * Copyright© 2017 FACE_RECOGNITION, INC.
 * UNPUBLISHED WORK
 * ALL RIGHTS RESERVED
 * 
 * This software is the confidential and proprietary information of
 * FACE_RECOGNITION, Inc. ("Proprietary Information").  Any use, reproduction, 
 * distribution or disclosure of the software or Proprietary Information, 
 * in whole or in part, must comply with the terms of the license 
 * agreement, nondisclosure agreement or contract entered into with 
 * FACE_RECOGNITION providing access to this software.
 */
package com.facerecognition.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static final String CONFIG_FILE = "config.properties";
	private String projectName ;
	private String testDir;
	private String serverURL;
	private String account;

	public Configuration() {
		super();
	}

	public Configuration init() {
		Properties prop = new Properties();
		InputStream input = null;

		/* load prop */
		input = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
		try {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		projectName = prop.getProperty("project.name");
		testDir = prop.getProperty("img.base.dir");
		serverURL = prop.getProperty("server.url");
		account = prop.getProperty("account");
				

		return this;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTestDir() {
		return testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	

}
