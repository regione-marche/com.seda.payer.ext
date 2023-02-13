package com.seda.payer.ext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.seda.payer.ext.util.Messages;
import com.seda.payer.ext.util.SedaExtException;
import com.seda.payer.ext.util.Utilities;


public class CoreCS {

	
	
	protected String creaBuffer(String bufferDati, String encryptIV, String encryptKey, String codicePortale, Logger logger) throws SedaExtException
	{
		String buffer = null;
		
		try {
			/*TripleDESChryptoService cryptoService = new TripleDESChryptoService();
			cryptoService.setIv(encryptIV);
			cryptoService.setKeyValue(encryptKey);*/
			
			String sTagOrario =Utilities.getTagOrario();
			String hash = Utilities.getMD5Hash(encryptIV + bufferDati + encryptKey + sTagOrario);
			//String bufferDatiCrypt = Base64.encode(bufferDati.getBytes()); //URLEncoder.encode(cryptoService.encryptBASE64(bufferDati), "UTF-8");
			
			String bufferDatiCrypt = new String(Base64.encodeBase64(bufferDati.getBytes("UTF-8")));
			//String bufferDatiCrypt = "M2FmYzAyNTctZDE3Ni00Y jBmLTg3M2ItYTAyYjA4ODk2MTk3";
			/*cryptoService.destroy();
			cryptoService = null;*/
			
			buffer = "<Buffer>" +
	        	"<TagOrario>" + sTagOrario + "</TagOrario>" + 
	        	"<CodicePortale>" + codicePortale + "</CodicePortale>" +
	        	"<BufferDati>" + bufferDatiCrypt + "</BufferDati>" + 
	        	"<Hash>" + hash + "</Hash>" +
	    	"</Buffer>";
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.HASH_CREATION_ERROR.format(), e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.HASH_CREATION_ERROR.format(), e);
		} 

		logger.info(Messages.BUFFER_CREATED.format(buffer));
        return buffer;
	}
	
	protected String decodeBuffer(String buffer, int window_minutes, String encryptIV, String encryptKey, Logger logger) throws SedaExtException
	{
		try
		{
			//verifica dati buffer
	        Document doc = Utilities.getXmlDocumentFromString(buffer);
	        String sTagOrario = Utilities.getElementValue("/Buffer/TagOrario", doc);
	        String sBufferDatiCrypt = Utilities.getElementValue("/Buffer/BufferDati", doc);
	        //String sBufferDatiCrypt =  "PFBheW1lbnRSZXF1ZXN0PjxQb3J0YWxlSUQ+UG9ydGFsZUV4dDwvUG9ydGFsZUlEPjxGdW56aW9uZT5QQUdBTUVOVE88L0Z1bnppb25lPjxVUkxEaVJpdG9ybm8+aHR0cDovL3BheWVyLnN2aWx1cHBvLnNlZGEuaXQ6MTEyODEvZXh0dGVzdC 9leHRwaWQvcml0b3Juby5kbzwvVVJMRGlSaXRvcm5vPjxVUkxEaU5vdGlmaWNhPmh0dHA6Ly9wYXllci5zdmlsdXBwby5zZWRhLml0OjExMjgxL2V4dHRlc3QvZXh0cGlkL25vdGlmaWNhLmRvPC9VUkxEaU5vdGlmaWNhPjxVUkxCYWNrPmh0dHA6Ly9wYXllci5zdmlsdXBwby5zZWRhLml0OjExMjgxL2V4dHRlc3QvZXh0cGlkL2JhY2suZG88L1VSTEJhY2s+PENvbW1pdE5vdGlmaWNhPlM8L0NvbW1pdE5vdGlmaWNhPjxVc2VyRGF0YT48RW1haWxVdGVudGU+YWFAYmIuaXQ8L0VtYWlsVXRlbnRlPjxJZGVudGlmaWNhdGl2b1V0ZW50ZT5UTlRQUlY2NUM1OEE2NjJFPC9JZGVudGlmaWNhdGl2b1V0ZW50ZT48L1VzZXJEYXRhPjxTZXJ2aWNlRGF0YT48Q29kaWNlVXRlbnRlPjAwMFRPPC9Db2RpY2VVdGVudGU+PENvZGljZUVudGU+MDY5NTQ8L0NvZGljZUVudGU+PFRpcG9VZmZpY2lvPjwvVGlwb1VmZmljaW8+PENvZGljZVVmZmljaW8+PC9Db2RpY2VVZmZpY2lvPjxUaXBvbG9naWFTZXJ2aXppbz5UQVI8L1RpcG9sb2dpYVNlcnZpemlvPjxOdW1lcm9PcGVyYXppb25lPkFCQzI2NDEyODg0MTgzN0twcGRmPC9OdW1lcm9PcGVyYXppb25lPjxOdW1lcm9Eb2N1bWVudG8+Mjg5NTkxMjQ0NTQxNzgzMjQxPC9OdW1lcm9Eb2N1bWVudG8+PEFubm9Eb2N1bWVudG8+MjAxMDwvQW5ub0RvY3VtZW50bz48VmFsdXRhPkVVUjwvVmFsdXRhPjxJbXBvcnRvPjU3MTwvSW1wb3J0bz48RGF0aVNwZWNpZmljaSAvPjwvU2VydmljZURhdGE+PEFjY291bnRpbmdEYXRhPjxSaXZlcnNhbWVudG9BdXRvbWF0aWNvPmZhbHNlPC9SaXZlcnNhbWVudG9BdXRvbWF0aWNvPjxJbXBvcnRpQ29udGFiaWxpIC8+PEVudGlEZXN0aW5hdGFyaT48RW50ZURlc3RpbmF0YXJpbz48Q29kaWNlRW50ZVBvcnRhbGVFc3Rlcm5vPjExMTExMTExMTExMTwvQ29kaWNlRW50ZVBvcnRhbGVFc3Rlcm5vPjxEZXNjckVudGVQb3J0YWxlRXN0ZXJubz5FbnRlIDExMTExMTExMTExMTwvRGVzY3JFbnRlUG9ydGFsZUVzdGVybm8+PFZhbG9yZT41NzE8L1ZhbG9yZT48Q2F1c2FsZT48IVtDREFUQVtDYXVzYWxlIHBlciBsJ2VudGUgMTExMTExMTExMTExXV0+PC9DYXVzYWxlPjxJbXBvcnRvQ29udGFiaWxlSW5ncmVzc28+PC9JbXBvcnRvQ29udGFiaWxlSW5ncmVzc28+PEltcG9ydG9Db250YWJpbGVVc2NpdGE+PC9JbXBvcnRvQ29udGFiaWxlVXNjaXRhPjxDb2RpY2VVdGVudGVCZW5lZmljaWFyaW8+MDAwVE88L0NvZGljZVV0ZW50ZUJlbmVmaWNpYXJpbz48Q29kaWNlRW50ZUJlbmVmaWNpYXJpbz4wNjk1NDwvQ29kaWNlRW50ZUJlbmVmaWNpYXJpbz48VGlwb1VmZmljaW9CZW5lZmljaWFyaW8+PC9UaXBvVWZmaWNpb0JlbmVmaWNpYXJpbz48Q29kaWNlVWZmaWNpb0JlbmVmaWNpYXJpbz48L0NvZGljZVVmZmljaW9CZW5lZmljaWFyaW8+PC9FbnRlRGVzdGluYXRhcmlvPjwvRW50aURlc3RpbmF0YXJpPjwvQWNjb3VudGluZ0RhdGE+PC9QYXltZW50UmVxdWVzdD4=";
	        String sHashRicevuto = Utilities.getElementValue("/Buffer/Hash", doc);
	        
	        if (sTagOrario.equals(""))
				throw new SedaExtException(Messages.ERROR_XML_NODE.format("TagOrario"));
			if (sBufferDatiCrypt.equals(""))
				throw new SedaExtException(Messages.ERROR_XML_NODE.format("BufferDati"));
			if (sHashRicevuto.equals(""))
				throw new SedaExtException(Messages.ERROR_XML_NODE.format("Hash"));
			
			//verifica finestra temporale
	        verificaFinestraTemporale(sTagOrario, window_minutes);
	        logger.info(Messages.TIME_WINDOW_VERIFIED.format());
	        
	        //decodifica buffer Base64
	        String bufferDati = decodificaBuffer(sBufferDatiCrypt);
	        
	        //verifica hash
	        verificaHash(sHashRicevuto, bufferDati, encryptIV, encryptKey, sTagOrario);
	        logger.info(Messages.HASH_VERIFIED.format());
	       	        
	        logger.info(Messages.DATA_BUFFER.format(bufferDati));
	        return bufferDati;
	        
		}  catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.XML_EXCEPTION.format(), e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.XML_EXCEPTION.format(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.XML_EXCEPTION.format(), e);
		}
	}
	
	private void verificaFinestraTemporale(String sTagOrario, int window_minutes) throws SedaExtException
	{
	    long longTagOrario = 0L;
	    try
	    {
	        Calendar calReceived = Calendar.getInstance();
	        String sAnno = sTagOrario.substring(0, 4);
	        String sMese = sTagOrario.substring(4, 6);
	        String sGiorno = sTagOrario.substring(6, 8);
	        String sOra = sTagOrario.substring(8, 10);
	        String sMinuti = sTagOrario.substring(10, 12);
	        
	        calReceived.set(Calendar.YEAR, Integer.parseInt(sAnno));
	        calReceived.set(Calendar.MONTH, Integer.parseInt(sMese) - 1);
	        calReceived.set(Calendar.DATE, Integer.parseInt(sGiorno));
	        calReceived.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sOra));
	        calReceived.set(Calendar.MINUTE, Integer.parseInt(sMinuti));
	        
	        longTagOrario = calReceived.getTimeInMillis(); 
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	throw new SedaExtException(Messages.INVALID_PARAMETER_VALUE.format("TagOrario"), e);
	    }
	    
	    Calendar calNow = Calendar.getInstance();
	
	    long longActualDate = calNow.getTimeInMillis(); 
	    long lMinutiDiff = Math.abs((longActualDate - longTagOrario) / (long)60000);
	    
	    if(lMinutiDiff > (long)window_minutes)
	    {
	    	throw new SedaExtException(Messages.TIME_WINDOW_EXPIRED.format());
	    }
	}
	
	private String decodificaBuffer(String sBufferDatiCrypt) 
	{
	    //String bufferDati = new String(Base64.decode(sBufferDatiCrypt)); 
		String bufferDati="";
		try {
			bufferDati = new String(Base64.decodeBase64(sBufferDatiCrypt.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return bufferDati;
	}
	
	private void verificaHash(String sHashRicevuto, String bufferDati, String encryptIV, String encryptKey, String sTagOrario) throws SedaExtException
	{
		String hashCalcolato = null;
		try {
			hashCalcolato = Utilities.getMD5Hash(encryptIV + bufferDati + encryptKey + sTagOrario);
			System.out.println("encryptIV = " + encryptIV);
			System.out.println("bufferDati = " + bufferDati);
			System.out.println("encryptKey = " + encryptKey);
			System.out.println("sTagOrario = " + sTagOrario);
			System.out.println("-----------------------");
			System.out.println("hashCalcolato = " + hashCalcolato);
			System.out.println("sHashRicevuto = " + sHashRicevuto);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.HASH_CREATION_ERROR.format(), e);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.HASH_CREATION_ERROR.format(), e);
		}
	    if(hashCalcolato == null || hashCalcolato.equals(""))
	    	throw new SedaExtException(Messages.HASH_CREATION_ERROR.format());

	    if(!hashCalcolato.equalsIgnoreCase(sHashRicevuto))
	    	throw new SedaExtException(Messages.HASH_CREATION_ERROR.format(sHashRicevuto, hashCalcolato));
	}
	
	protected String checkPaymentRequestData(String bufferDatiPaymentRequest) throws SedaExtException
	{
		LinkedList<String> listErrors = new LinkedList<String>();
		String sError = "";
		try
		{
			//verifica dati buffer
	        Document doc = Utilities.getXmlDocumentFromString(bufferDatiPaymentRequest);
	        
	        checkNodeNotEmpty("PortaleID", Utilities.getElementValue("/PaymentRequest/PortaleID", doc), listErrors);
	        checkNodeNotEmpty("Funzione", Utilities.getElementValue("/PaymentRequest/Funzione", doc), listErrors);
	        checkNodeNotEmpty("URLDiRitorno", Utilities.getElementValue("/PaymentRequest/URLDiRitorno", doc), listErrors);
	        checkNodeNotEmpty("URLDiNotifica", Utilities.getElementValue("/PaymentRequest/URLDiNotifica", doc), listErrors);
	        checkNodeNotEmpty("URLBack", Utilities.getElementValue("/PaymentRequest/URLBack", doc), listErrors);
	        checkNodeValueMaxLenght("CommitNotifica", Utilities.getElementValue("/PaymentRequest/CommitNotifica", doc), 1, listErrors);
	        checkNodeValue("CommitNotifica", Utilities.getElementValue("/PaymentRequest/CommitNotifica", doc), Arrays.asList("S", "N"), listErrors);
	        
	        //UserData
	        checkNodeNotEmpty("UserData/IdentificativoUtente", Utilities.getElementValue("/PaymentRequest/UserData/IdentificativoUtente", doc), listErrors);
	        checkNodeValueMaxLenght("UserData/IdentificativoUtente", Utilities.getElementValue("/PaymentRequest/UserData/IdentificativoUtente", doc), 16, listErrors);
	        
	        checkNodeValueMaxLenght("UserData/Denominazione", Utilities.getElementValue("/PaymentRequest/UserData/Denominazione", doc), 70, listErrors); //PG190150
	        
	        
	        if(Utilities.checkNodeElement("/PaymentRequest/ServiceData", doc)){
	        	
	        
			//ServiceData
	        checkNodeNotEmpty("ServiceData/CodiceUtente", Utilities.getElementValue("/PaymentRequest/ServiceData/CodiceUtente", doc), listErrors);
	        checkNodeValueMaxLenght("ServiceData/CodiceUtente", Utilities.getElementValue("/PaymentRequest/ServiceData/CodiceUtente", doc), 5, listErrors);
	        checkNodeNotEmpty("ServiceData/CodiceEnte", Utilities.getElementValue("/PaymentRequest/ServiceData/CodiceEnte", doc), listErrors);
	        checkNodeValueMaxLenght("ServiceData/CodiceEnte", Utilities.getElementValue("/PaymentRequest/ServiceData/CodiceEnte", doc), 5, listErrors);
	        checkNodeValueMaxLenght("ServiceData/TipoUfficio", Utilities.getElementValue("/PaymentRequest/ServiceData/TipoUfficio", doc), 1, listErrors);
	        checkNodeValueMaxLenght("ServiceData/CodiceUfficio", Utilities.getElementValue("/PaymentRequest/ServiceData/CodiceUfficio", doc), 6, listErrors);
	        checkNodeNotEmpty("ServiceData/TipologiaServizio", Utilities.getElementValue("/PaymentRequest/ServiceData/TipologiaServizio", doc), listErrors);
	        // segnalazione Lepida del 04/06/2013, possibilità di tipologia servizio più lunga di 3 bytes seguendo la linea di CUP. Portata a 6 caratteri
	        //checkNodeValueMaxLenght("ServiceData/TipologiaServizio", Utilities.getElementValue("/PaymentRequest/ServiceData/TipologiaServizio", doc), 3, listErrors);
	        checkNodeValueMaxLenght("ServiceData/TipologiaServizio", Utilities.getElementValue("/PaymentRequest/ServiceData/TipologiaServizio", doc), 6, listErrors);
			
	        checkNodeNotEmpty("ServiceData/NumeroOperazione", Utilities.getElementValue("/PaymentRequest/ServiceData/NumeroOperazione", doc), listErrors);
	        checkNodeValueMaxLenght("ServiceData/NumeroOperazione", Utilities.getElementValue("/PaymentRequest/ServiceData/NumeroOperazione", doc), 128, listErrors);
	        checkNodeNotEmpty("ServiceData/NumeroDocumento", Utilities.getElementValue("/PaymentRequest/ServiceData/NumeroDocumento", doc), listErrors);
	        checkNodeValueMaxLenght("ServiceData/NumeroDocumento", Utilities.getElementValue("/PaymentRequest/ServiceData/NumeroDocumento", doc), 20, listErrors);
	        checkNodeValueMaxLenght("ServiceData/Valuta", Utilities.getElementValue("/PaymentRequest/ServiceData/Valuta", doc), 3, listErrors);
			
	        checkNodeNotEmpty("ServiceData/Importo", Utilities.getElementValue("/PaymentRequest/ServiceData/Importo", doc), listErrors);

	        }
	      //inizio EVOLUZIONE 3.0
	        else if(Utilities.checkNodeElement("/PaymentRequest/MultiEnte", doc)){

	        	//checkNodeNotEmpty		("MultiEnte/Valuta",			Utilities.getElementValue("/PaymentRequest/MultiEnte/Valuta", doc), listErrors);
		        checkNodeValueMaxLenght	("MultiEnte/Valuta", 			Utilities.getElementValue("/PaymentRequest/MultiEnte/Valuta", doc), 3, listErrors);
	        	checkNodeNotEmpty		("MultiEnte/ImportoTotale",		Utilities.getElementValue("/PaymentRequest/MultiEnte/ImportoTotale", doc), listErrors);
	        	checkNodeNotEmpty		("MultiEnte/NumeroOperazione",	Utilities.getElementValue("/PaymentRequest/MultiEnte/NumeroOperazione", doc), listErrors);
		        checkNodeValueMaxLenght	("MultiEnte/NumeroOperazione", 	Utilities.getElementValue("/PaymentRequest/MultiEnte/NumeroOperazione", doc), 128, listErrors);
		        
	        	NodeList enti = Utilities.getNodeList("/PaymentRequest/MultiEnte/Ente", doc);
	        	if (enti != null)
	        	{

	        		for(int i = 1; i <= enti.getLength(); i ++){
	        			String sBaseXPath = "/PaymentRequest/MultiEnte/Ente[" + i + "]";
	        			String sPath = 		"MultiEnte/Ente[" + i + "]";
	        		        	
	        		        	checkNodeNotEmpty		(sPath + "/CodiceUtente",		Utilities.getElementValue(sBaseXPath + "/CodiceUtente",	 	doc), listErrors);
	        			        checkNodeValueMaxLenght	(sPath + "/CodiceUtente", 		Utilities.getElementValue(sBaseXPath + "/CodiceUtente",		doc), 5, listErrors);
	        		        	checkNodeNotEmpty		(sPath + "/CodiceEnte",			Utilities.getElementValue(sBaseXPath + "/CodiceEnte", 		doc), listErrors);
	        			        checkNodeValueMaxLenght	(sPath + "/CodiceEnte", 		Utilities.getElementValue(sBaseXPath + "/CodiceEnte", 		doc), 5, listErrors);
//	        		        	checkNodeNotEmpty		(sPath + "/TipoUfficio",		Utilities.getElementValue(sBaseXPath + "/TipoUfficio", 		doc), listErrors);
	        			        checkNodeValueMaxLenght	(sPath + "/TipoUfficio", 		Utilities.getElementValue(sBaseXPath + "/TipoUfficio", 		doc), 1, listErrors);
//	        		        	checkNodeNotEmpty		(sPath + "/CodiceUfficio",		Utilities.getElementValue(sBaseXPath + "/CodiceUfficio", 	doc), listErrors);
	        			        checkNodeValueMaxLenght	(sPath + "/CodiceUfficio", 		Utilities.getElementValue(sBaseXPath + "/CodiceUfficio", 	doc), 6, listErrors);
	        		        	checkNodeNotEmpty		(sPath + "/TipologiaServizio",	Utilities.getElementValue(sBaseXPath + "/TipologiaServizio", doc), listErrors);
	        			        checkNodeValueMaxLenght	(sPath + "/TipologiaServizio", 	Utilities.getElementValue(sBaseXPath + "/TipologiaServizio", doc), 3, listErrors);
	        		        	checkNodeNotEmpty		(sPath + "/NumeroDocumento",	Utilities.getElementValue(sBaseXPath + "/NumeroDocumento", 	doc), listErrors);
	        			        checkNodeValueMaxLenght	(sPath + "/NumeroDocumento", 	Utilities.getElementValue(sBaseXPath + "/NumeroDocumento", 	doc), 20, listErrors);
//	        		        	checkNodeNotEmpty		(sPath + "/AnnoDocumento",		Utilities.getElementValue(sBaseXPath + "/AnnoDocumento", 	doc), listErrors);
	        		        	checkNodeNotEmpty		(sPath + "/Importo",			Utilities.getElementValue(sBaseXPath + "/Importo", 			doc), listErrors);
	        		        	//checkNodeNotEmpty		(sPath + "/DatiSpecifici",		Utilities.getElementValue(sBaseXPath + "/DatiSpecifici", 	doc), listErrors);
	        		}
	        	}
	        	
	        }
	        //Accounting Data
	        /*NodeList enti = Utilities.getNodeList("/PaymentRequest/AccountingData/EntiDestinatari/EnteDestinatario", doc);
			if (enti != null)
			{
				for(int i = 1; i <= enti.getLength(); i ++){
					String sBaseXPath = "/PaymentRequest/AccountingData/EntiDestinatari/EnteDestinatario[" + i + "]";
					String sPath = "AccountingData/EntiDestinatari/EnteDestinatario[" + i + "]";
					checkNodeNotEmpty(sPath + "/CodiceEntePortaleEsterno", Utilities.getElementValue(sBaseXPath + "/CodiceEntePortaleEsterno", doc), listErrors);
					checkNodeValueMaxLenght(sPath + "/CodiceEntePortaleEsterno", Utilities.getElementValue(sBaseXPath + "/CodiceEntePortaleEsterno", doc), 50, listErrors);
					checkNodeNotEmpty(sPath + "/DescrEntePortaleEsterno", Utilities.getElementValue(sBaseXPath + "/DescrEntePortaleEsterno", doc), listErrors);
					checkNodeValueMaxLenght(sPath + "/DescrEntePortaleEsterno", Utilities.getElementValue(sBaseXPath + "/DescrEntePortaleEsterno", doc), 256, listErrors);
					checkNodeNotEmpty(sPath + "/Valore", Utilities.getElementValue(sBaseXPath + "/Valore", doc), listErrors);
					checkNodeValueMaxLenght(sPath + "/Causale", Utilities.getElementValue(sBaseXPath + "/Causale", doc), 256, listErrors);
				}
			}*/
			
			if (listErrors.size() > 0)
			{
				StringBuffer sbErr = new StringBuffer();
				sbErr.append("<ErrorsPaymentRequest>");
				for (String sErr : listErrors)
					sbErr.append("<err>" + sErr + "</err>");
				sbErr.append("</ErrorsPaymentRequest>");
				sError = sbErr.toString();
			}
			
	        return sError;
	        
		}  catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.XML_EXCEPTION.format(), e);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.XML_EXCEPTION.format(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SedaExtException(Messages.XML_EXCEPTION.format(), e);
		}
	}
	
	private void checkNodeNotEmpty(String sNodeName, String sNodeValue,  List<String> listErrors)
	{
		if (sNodeValue == null || sNodeValue.length() == 0)
			listErrors.add(Messages.XML_REQUIRED_VALUE.format(sNodeName));
	}
	
	private void checkNodeValueMaxLenght(String sNodeName, String sNodeValue, int iMaxLen, List<String> listErrors)
	{
		if (sNodeValue != null && sNodeValue.length() > iMaxLen)
			listErrors.add(Messages.XML_INVALID_LENGHT.format(sNodeName, String.valueOf(iMaxLen)));
	}
	
	private void checkNodeValue(String sNodeName, String sNodeValue, List<String> listAdmitedValues, List<String> listErrors)
	{
		if (sNodeValue != null && sNodeValue.length() > 0)
		{
			if (!listAdmitedValues.contains(sNodeValue))
			{
				String sValues = "";
				for (String val : listAdmitedValues)
					sValues += "," + val;
				listErrors.add(Messages.XML_INVALID_VALUE.format(sNodeName, sValues.substring(1)));
			}
		}
	}
}
