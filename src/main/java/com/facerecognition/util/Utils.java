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
package com.facerecognition.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	public static int toInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (Exception e) {
			return -1;
		}
	}

	public static String toString(int num) {
		return new Integer(num).toString();
	}

	public static String[] toArrayString(String text, String regex) {
		String[] arr = text.split(regex);
		return arr;

	}

	public static String subDateVsDate(Date start, Date end) {
		try {
			if (start == null || end == null) {
				return "";
			}
			final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(CommonConstant.DDMMYYYYFORMAT);
			LocalDate starttime = LocalDate.parse(convertDatetoStringFormat(start), dtf);
			LocalDate endtime = LocalDate.parse(convertDatetoStringFormat(end), dtf);
			Period diff = Period.between(endtime, starttime);
			if (diff.isNegative()) {
				return String.format("%d years %d months %d days", 0, 0, 0);
			}
			return String.format("%d years %d months %d days", diff.getYears(), diff.getMonths(), diff.getDays());
		} catch (Exception e) {
			LOGGER.error("Exception: Error Convert Date Time " + e);
		}
		return null;
	}

	public static String convertDatetoStringFormat(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(CommonConstant.DDMMYYYYFORMAT);
		return format.format(date);
	}

	public static String difference(Date start, Date end) {
		if (start == null || end == null) {
			return "";
		}
		long millis = end.getTime() - start.getTime();
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

	}

	public static String percent(int num, int total){
		return "("+(double)Math.round( 100 *num/total )+" %)";
	}
	public static void main(String[] args) {
		System.out.println(difference(new Date(), new Date()));
	}

}
