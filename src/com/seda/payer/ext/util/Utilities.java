package com.seda.payer.ext.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Hex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utilities {

    public static String getTagOrario()
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
        return dateFormatter.format(new java.util.Date());
    }

    public static String getMD5Hash(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes("UTF-8"), 0, text.length());
        byte[] md5hash = md.digest();
        return new String(Hex.encodeHex(md5hash)); 
    }
    
    
    public static Document getXmlDocumentFromString(String xmlString) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlString));
		Document doc = builder.parse(is);
		
		return doc;
	}
	
	public static String getElementValue(String xpath_expr, Document doc)
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try
		{
			Element elem = (Element)(xpath.evaluate(xpath_expr, doc, XPathConstants.NODE));
			if (elem != null)
				return elem.getTextContent();
		}
		catch (Exception e) {}
		
		return "";
	}
	
	public static void setElementValue(Document doc, String xpath_expr, String sValue)
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try
		{
			Element elem = (Element)(xpath.evaluate(xpath_expr, doc, XPathConstants.NODE));
			if (elem != null)
				elem.setTextContent(sValue);
		}
		catch (Exception e) {}
	}
	
	public static NodeList getNodeList(String xpath_expr, Document doc)
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try
		{
			NodeList nodeList = (NodeList)(xpath.evaluate(xpath_expr, doc, XPathConstants.NODESET));
			return nodeList;
		}
		catch (Exception e) {}
		
		return null;
	}
	
	
	//inizio EVOLUZIONE 3.0
	public static boolean checkNodeElement(String xpath_expr, Document doc)
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try
		{
			Element elem = (Element)(xpath.evaluate(xpath_expr, doc, XPathConstants.NODE));
			return (elem != null);
		}
		catch (Exception e) {}
		
		return false;
		
	}
    //fine EVOLUZIONE 3.0
	
}
