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
package com.facerecognition.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.facerecognition.model.XPathValue;


public class TestUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);


	@SuppressWarnings("unchecked")
	public static <T> T xmlToResponse(String xml, Class<T> clazz) throws JAXBException {
		Reader reader = new StringReader(xml);
		// setup object mapper using the ServiceConfig class
		JAXBContext context = JAXBContext.newInstance(clazz);

		return (T) context.createUnmarshaller().unmarshal(reader);
	}

	public static Document xmlToDocument(String xmlString) {
		Document doc = null;
		try {
			LOGGER.debug("Trying to Parse String: " + xmlString);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		}

		return doc;
	}

	public static Document jsonToDocument(String jsonString) {
		Document doc = null;
		JSONObject jsonObject = null;
		jsonObject = new JSONObject(jsonString);
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>" + XML.toString(jsonObject) + "</root>";
		doc = xmlToDocument(xml);

		return doc;
	}

	public static NodeList getObjectUsingXPath(Document document, String objectPath) {
		NodeList nodeList = null;
		XPath xPath = XPathFactory.newInstance().newXPath();

		try {
			XPathExpression xp = xPath.compile(objectPath);
			nodeList = (NodeList) xp.evaluate(document, XPathConstants.NODESET);
		} catch (Exception e) {
		}
		return nodeList;

	}

	public static XPathValue getXPathValue(Document document, String objectPath) {
		XPathValue xPathValue = new XPathValue();

		NodeList result = getObjectUsingXPath(document, objectPath);
		if(result!=null){
			xPathValue.setNodeList(result);
			int length = result.getLength();
			xPathValue.setCount(length);
			if (length > 0) {
				xPathValue.setNodeName(result.item(0).getNodeName());
				Node child = result.item(0).getFirstChild();
				if (child != null) {
					xPathValue.setFirstChild(child);
					xPathValue.setNodeValue(child.getNodeValue());
				}
			}
			
		}
		return xPathValue;
	}
	
	public static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	public static void main(String[] args) {
		try {
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><channel><linear adult=\"true\"></linear><linear /></channel></response>";
			String objectPath = null;
			XPathValue xPathValue;

			objectPath = "//channel/linear[1]/@adult";
			xPathValue = getXPathValue(xmlToDocument(xml), objectPath);
			System.out.println(objectPath + " getNodeName: " + xPathValue.getNodeName());
			System.out.println(objectPath + " getFirstChild: " + xPathValue.getFirstChild());
			System.out.println(objectPath + " count: " + xPathValue.getCount());
			System.out.println(objectPath + " value: " + xPathValue.getNodeValue());

//			objectPath = "//channel/linear[2]";
//			xPathValue = getXPathValue(xmlToDocument(xml), objectPath);
//			System.out.println(objectPath + " getNodeName: " + xPathValue.getNodeName());
//			System.out.println(objectPath + " getFirstChild: " + xPathValue.getFirstChild());
//			System.out.println(objectPath + " count: " + xPathValue.getCount());
//			System.out.println(objectPath + " value: " + xPathValue.getNodeValue());

		} catch (Exception e) {
		}

	}

}
