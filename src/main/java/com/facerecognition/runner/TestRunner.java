/**
 * 
 * FACE_RECOGNITION PROPRIETARY
 * Copyright� 2014 FACE_RECOGNITION, INC.
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
package com.facerecognition.runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.facerecognition.model.Configuration;
import com.facerecognition.model.Message;
import com.facerecognition.model.TestCase;
import com.facerecognition.model.TestCaseStatus;
import com.facerecognition.model.XPathValue;
import com.facerecognition.report.ExcelReport;
import com.facerecognition.util.TestUtils;

public class TestRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
	
	private static final String GENDER_MALE = "M";
	private static final String GENDER_FEMALE = "F";
	private static final String HTTP_CMD = "/usr/local/bin/http --ignore-stdin ";


	private static final String GENDER_OBJ_PATH = "//gender";
	private static final String NAME_OBJ_PATH = "//name";

	private TestCase testCase;

	private String logMessage;
	private StringBuffer debugLog;
	private StringBuffer logger;
	private String body;
	private Document document;

	private Configuration configuration;

	public static Date startTime;
	public static Date endTime;

	private ExcelReport report;

	private List<TestCase> testCases;
	private String username;
	private String gender;

	public void excuteTest(Configuration configuration) {
		startTime = new Date();
		this.configuration = configuration;

		testCases = new ArrayList<>();
		// create test case
		File dir = new File(configuration.getTestDir());
		if (dir == null || !dir.isDirectory()) {
			LOGGER.error("Base Directory is not a directory " + configuration.getTestDir());
			return;
		}
		File[] listOfFiles = dir.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File userFolder = listOfFiles[i];
			if (!userFolder.isDirectory()) {
				LOGGER.warn("User folder is not a directory: " + userFolder.getAbsolutePath());
				continue;
			}

			getUserInfo(userFolder.getName());
			testCase = new TestCase();
			testCase.setId(username);

			File[] listOfImgs = userFolder.listFiles();
			if (listOfImgs.length == 0) {
				testCase.setMessage(Message.FOLDER_EMPTY);
				testCase.setUploadedImg(userFolder);
			} else {
				testCase.setUploadedImg(listOfImgs[0]);
				if (listOfImgs.length == 1) {
					testCase.setMessage(Message.FOLDER_1_IMG);
				} else {
					testCase.setVerifiedImg(listOfImgs[1]);
					testCase.setMessage("");
					executeTestCase();
				}
			}

			testCases.add(testCase);

		}

		endTime = new Date();
		// generate report
		report = new ExcelReport(testCases, configuration);
		report.generateReport();

	}

	private void executeTestCase() {
		if (uploadImage()) {
			verifyImage();
		}else{
			LOGGER.info("Upload failed: "+testCase.getId());
		}
		if (testCase.getMessage().equals("")) {
			testCase.setStatus(TestCaseStatus.PASSED.toString());
		} else {
			testCase.setStatus(TestCaseStatus.FAILED.toString());
		}
	}

	/**
	 * "Added 6ba1ab673df880644e09a939f0511374c2413b6e6242b13bac7e53793835f213
	 * embedding"
	 */
	private boolean uploadImage() {
		boolean isUploaded = false;

		String uploadCommand = String.format(" -a %s -f POST %s/imagebank/ image@%s name=%s gender=%s",
				configuration.getAccount(), configuration.getServerURL(), testCase.getUploadedImg().getAbsolutePath(),
				username, gender);

		String response = TestUtils.executeCommand(HTTP_CMD + uploadCommand);
		try {
			assertTrue("Response starts with [\"Added or [\"Updated",response.startsWith("[\"Added") ||response.startsWith("[\"Updated"));
//			assertTrue("Response ends with embedding\"]",response.endsWith("embedding\"]"));
			isUploaded = true;
		} catch (Throwable e) {
			testCase.setMessage(testCase.getMessage() + e.getMessage() + "\n"+response+"\n");
		}

		return isUploaded;
	}
	


	private boolean verifyImage() {
		boolean isExisted = false;

		String verifyCommand = String.format(" -a %s -f POST %s/imagebank/ image@%s ", configuration.getAccount(),
				configuration.getServerURL(), testCase.getVerifiedImg().getAbsolutePath());

		String response = TestUtils.executeCommand(HTTP_CMD + verifyCommand);
		response = modifyResponse(response);
		try {
			document = TestUtils.jsonToDocument(response);
			// verify gender
			XPathValue actualGender = TestUtils.getXPathValue(this.document, GENDER_OBJ_PATH);
			if (GENDER_MALE.equalsIgnoreCase(gender)) {
				assertEquals(GENDER_MALE,actualGender.getNodeValue());
			} else  {
				assertEquals(GENDER_FEMALE,actualGender.getNodeValue());
			}

			// verify username
			XPathValue actualUsername = TestUtils.getXPathValue(this.document, NAME_OBJ_PATH);
			assertEquals(username,actualUsername.getNodeValue());

		} catch (Throwable e) {
			testCase.setMessage(testCase.getMessage() + e.getMessage() + "\n"+response+"\n");
		}
		return isExisted;
	}


	private  String modifyResponse(String response) {
		StringBuilder builder = new StringBuilder(response.trim());
		builder.deleteCharAt(builder.length()-1);
		builder.deleteCharAt(0);
		return builder.toString();
	}

	private void getUserInfo(String name) {
		String[] user = name.split("_");
		username = user[0];
		if (user.length > 1) {
			gender = user[1].toUpperCase();
		} else {
			gender = GENDER_MALE;
		}

	}

	public void setFailureMessage(String type, String msg) {
		testCase.setMessage(testCase.getMessage() + "\n" + type + ": " + msg);
	}

	public void reportFail() {
		testCase.setPassed(false);
		testCase.setStatus(TestCaseStatus.FAILED.toString());

		logger = new StringBuffer().append(logMessage).append("\n>>>>FAILURE");
		LOGGER.info(logger.toString());

	}

	public void reportPass() {
		testCase.setPassed(true);
		testCase.setStatus(TestCaseStatus.PASSED.toString());
		testCase.setMessage("SUCCESS");
	}

	public void reportResponse() {
		logger = new StringBuffer().append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>").append("\n");
		debugLog.append(logger);
		LOGGER.debug(logger.toString());

		logger = new StringBuffer().append("RESPONSE BODY:\n").append(body).append("\n");
		debugLog.append(logger);
		LOGGER.debug(logger.toString());

		logger = new StringBuffer().append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>").append("\n");
		debugLog.append(logger);
		LOGGER.debug(logger.toString());

	}

	public void reportStart() {
		testCase.setPassed(false);
		testCase.setStatus(TestCaseStatus.NOT_RUN.toString());

		logger = new StringBuffer().append("RUN TEST CASE:").append(testCase.getId()).append("\n\n");
		debugLog.append(logger);
		LOGGER.info(debugLog.toString());
	}

	public void reportBlock() {
		testCase.setPassed(false);
		testCase.setMessage(TestCaseStatus.BLOCKED.toString());
		LOGGER.info(logMessage + "\n>>>>BLOCKED");
	}

	public void assertValue(String expected, XPathValue actual, String objectPath) {
		String nodeVal = actual.getNodeValue();

		Assert.assertEquals(expected.toString(), nodeVal);
	}

	private boolean isSimpleDateFormat(String value) {
		String pattern = "\\d{4}[-]\\d{2}[-]\\d{2}";

		return value.matches(pattern);
	}

	private String convertToSimpleDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(date);
	}

	private String convertToUTCDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(date);
	}

}
