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
package com.facerecognition.model;

public enum AssertType {
	isNull("isNull"), isNotNull("isNotNull"), isEmpty("isEmpty"), isNotEmpty("isNotEmpty"), isDateTime("isDateTime"), eq("eq"), lt(
			"lt"), gt("gt"), lte("lte"), gte("gte"), contains("contains"), startsWith("startsWith"), endsWith("endsWith");

	AssertType(String assertType) {
		this.assertType = assertType;
	}

	private String assertType;

	@Override
	public String toString() {
		return this.assertType;
	}
}
