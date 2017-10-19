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
package com.facerecognition.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facerecognition.model.Configuration;
import com.facerecognition.model.TestCase;
import com.facerecognition.model.TestCaseStatus;
import com.facerecognition.runner.TestRunner;
import com.facerecognition.util.Utils;

public class ExcelReport {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReport.class);

	private static final String REPORT_TEMPLATE = "TestReport_Template.xlsx";

	private static final String SHEET_TEST_RESULT = "Test-Result";
	private static final String SHEET_STYLE = "Style";

	/*********************************************
	 * For Test suite result in Summary Sheet
	 **********************************************/
	private static final int TEST_RESULT_SUMMARY_ROW_START = 1;
	private static final int TEST_RESULT_SUMMARY_COL = 1;

	private static final int TEST_RESULT_DETAIL_ROW_START = 11;

	/*********************************************
	 * For Test case result in Detail Result Sheet
	 **********************************************/
	private static final int DETAIL_TEST_CASE_ID_COL = 0;

	private XSSFSheet sheetReader;
	private XSSFSheet sheetWriter;
	private XSSFWorkbook wbReader;
	private XSSFRow row;
	private XSSFCell cell;

	public String SnapShotName;
	public String SnapShotHyperlink;
	public String FolderScenarioName;
	public String FolderScenarioHyperlink;

	private int iRow;
	private int iCol;
	private int numOfFailedCase;
	private int numOfPassedCase;

	private TestCase testCase;
	private List<TestCase> testCases;
	private String temp = "";
	private File img;

	private CellStyle cellStyleNormal;
	private CellStyle cellStyleFail;
	private CellStyle cellStylePass;

	private Configuration configuration;

	public static String reportFile;

	public ExcelReport(List<TestCase> testCases, Configuration configuration) {
		this.testCases = testCases;
		this.configuration = configuration;
	}

	public void start() {
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy-hhmmss");
		reportFile = new StringBuffer("report/TestReport-").append(df.format(new Date().getTime())).append(".xlsx")
				.toString();
	}

	public void generateReport() {
		start();

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(REPORT_TEMPLATE);

		try {
			wbReader = new XSSFWorkbook(inputStream);

			initCellStyle();

			createTestResultSheet();

			OutputStream outputStream = new FileOutputStream(new File(reportFile));

			wbReader.write(outputStream);

			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	private void initCellStyle() {
		sheetReader = wbReader.getSheet(SHEET_STYLE);

		row = sheetReader.getRow(1);

		cellStyleNormal = row.getCell(0).getCellStyle();

		cellStyleFail = row.getCell(1).getCellStyle();

		cellStylePass = row.getCell(2).getCellStyle();

		wbReader.removeSheetAt(wbReader.getSheetIndex(sheetReader));

	}

	private void createTestResultSheet() throws Exception {
		sheetReader = wbReader.getSheet(SHEET_TEST_RESULT);

		/* TEST RESULT DETAILS */
		iRow = TEST_RESULT_DETAIL_ROW_START;
		writeTestResultDetail();

		// TEST RESULT SUMMARY
		iRow = TEST_RESULT_SUMMARY_ROW_START;
		iCol = TEST_RESULT_SUMMARY_COL;
		setCellValueForCol(configuration.getProjectName());
		setCellValueForCol(TestRunner.startTime.toString());
		setCellValueForCol(TestRunner.endTime.toString());
		setCellValueForCol(Utils.difference(TestRunner.startTime, TestRunner.endTime));

		int totalTestCase = numOfFailedCase + numOfPassedCase;
		setCellValueForCol(Utils.toString(totalTestCase));

		setCellValueForCol(
				Utils.toString(this.numOfPassedCase) + " " + Utils.percent(this.numOfPassedCase, totalTestCase));
		this.cell.setCellStyle(cellStylePass);

		setCellValueForCol(
				Utils.toString(this.numOfFailedCase) + " " + Utils.percent(this.numOfFailedCase, totalTestCase));
		this.cell.setCellStyle(cellStyleFail);

	}

	private void writeTestResultDetail() {
		iRow = TEST_RESULT_DETAIL_ROW_START;

		for (TestCase testCase : this.testCases) {
			this.testCase = testCase;
			writeTestCase();
			iRow++;
		}
	}

	private void writeTestCase() {
		// Test Case Id
		iCol = DETAIL_TEST_CASE_ID_COL;
		setCellValueForRow(this.testCase.getId());

		// Status
		temp = this.testCase.getStatus();
		setCellValueForRow(temp);
		if (TestCaseStatus.PASSED.toString().equalsIgnoreCase(temp)) {
			numOfPassedCase++;
			this.cell.setCellStyle(cellStylePass);
		} else {
			numOfFailedCase++;
			this.cell.setCellStyle(cellStyleFail);
		}

		// Uploaded Image
		img = testCase.getUploadedImg();
		if (img != null) {
			setCellValueForRow(img.getName(), img.getAbsolutePath());
		}

		// Verified Image
		img = testCase.getVerifiedImg();
		if (img != null) {
			setCellValueForRow(img.getName(), img.getAbsolutePath());
		}

		// Message
		setCellValueForRow(this.testCase.getMessage());

	}

	private void setCellValueForRow(String value, String hyperLink) {
		setCellValue(this.iRow, this.iCol, value, hyperLink);
		this.iCol++;

	}

	private void setCellValueForRow(String value) {
		setCellValue(this.iRow, this.iCol, value);
		this.iCol++;

	}

	private void setCellValueForCol(String value) {
		setCellValue(this.iRow, this.iCol, value);
		this.iRow++;
	}

	private void setCellValueForCol(String value, String hyperLink) {
		setCellValue(this.iRow, this.iCol, value, hyperLink);
		this.iRow++;
	}

	private void setCellValue(int iRow, int iCol, String value) {
		setCellValue(iRow, iCol, value, null);
	}

	private void setCellValue(int iRow, int iCol, String value, String hyperLink) {
		row = sheetReader.getRow(iRow);
		if (row == null) {
			row = sheetReader.createRow(iRow);
		}
		if ((cell = row.getCell(iCol)) == null) {
			cell = row.createCell(iCol);
		}
		cell.setCellValue(value);
		cell.setCellStyle(cellStyleNormal);
		if (hyperLink != null) {
			XSSFCellStyle style = cell.getCellStyle();
			XSSFFont font = wbReader.createFont();
			font.setUnderline((byte) 1);
			style.setFont((Font) font);
			XSSFCreationHelper createHelper = wbReader.getCreationHelper();
			Hyperlink link = createHelper.createHyperlink(HyperlinkType.FILE);
			link.setAddress(hyperLink.replace("\\", "/").replace(" ", "%20").replace("+", "%2B"));
			cell.setHyperlink(link);
			cell.setCellStyle((CellStyle) style);
		}
	}
}
