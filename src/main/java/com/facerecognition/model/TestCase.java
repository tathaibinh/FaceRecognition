package com.facerecognition.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class TestCase implements Serializable, Cloneable {

	private final static long serialVersionUID = 1L;

	protected boolean passed;

	protected String id;
	protected String status;
	protected String message;
	protected File uploadedImg;
	protected File verifiedImg;

	private transient Date startTime;
	private transient Date endTime;

	public TestCase() {
		super();
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public File getUploadedImg() {
		return uploadedImg;
	}

	public void setUploadedImg(File uploadedImg) {
		this.uploadedImg = uploadedImg;
	}

	public File getVerifiedImg() {
		return verifiedImg;
	}

	public void setVerifiedImg(File verifiedImg) {
		this.verifiedImg = verifiedImg;
	}

}
