/**
 * 
 * FACE_RECOGNITION PROPRIETARY
 * Copyright� 2015 FACE_RECOGNITION, INC.
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

public enum TestCaseStatus {
	NOT_RUN("NOT RUN"), BLOCKED("BLOCKED"), PASSED("PASSED"), FAILED("FAILED");

	private String status;

	private TestCaseStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.status;
	}

}
