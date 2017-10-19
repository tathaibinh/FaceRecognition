/**
 * 
 * FACE_RECOGNITION PROPRIETARY
 * Copyright� 2017 FACE_RECOGNITION, INC.
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
package com.facerecognition.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facerecognition.model.Configuration;
import com.facerecognition.runner.TestRunner;

public class AppRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

	public static void main(String[] args) {
		Configuration configuration;
		LOGGER.info("Creating configuration environment ...");
		if (args.length == 0) {
			configuration = new Configuration().init();
		} else {
			configuration = new Configuration(args[0]).init();
		}
		LOGGER.info("############# Start running test ... ################");
		new TestRunner().excuteTest(configuration);

		LOGGER.info("################ Done running test ... ################");
	}

}
