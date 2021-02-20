package com.sap.httppost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ICOCreate {

	public static void main(String[] args) throws Exception {
		
	//	 String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceAll("/bin", "") + "PIConfig.properties";
					 String rootPath =  "C:\\Users\\Administrator\\Desktop\\Folder\\PIConfig.properties";
								 
		
			//System.out.println(rootPath);
			InputStream input = new FileInputStream(rootPath);

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			
			String host = prop.getProperty("PI.Host");
			String port = prop.getProperty("PI.Port");
			String user = prop.getProperty("PI.User");
			String password = prop.getProperty("PI.Password");
			String mFileName = prop.getProperty("PI.InterfaceInfoSheet");
			FileInputStream fis = new FileInputStream(mFileName);

		  try {

	    // we create an XSSF Workbook object for our XLSX Excel File

	    XSSFWorkbook workbook = new XSSFWorkbook(fis);

	    // we get first sheet

	    XSSFSheet sheet = workbook.getSheetAt(0);
	   

	    int rowNumber = sheet.getLastRowNum() + 1;
        for (int j = 1; j < rowNumber; j++) {
        	List<Object> interfaceInfo = new ArrayList();
            Iterator it = sheet.getRow(j).cellIterator();
            while (it.hasNext()) {
             //   System.out.print(it.next().toString()+ " ");
                
                interfaceInfo.add(it.next().toString());
                
            }
            String[] intInfo =  interfaceInfo.toArray(new String[0]);
            
            DirAPICall objDir = new DirAPICall();
           objDir.CallICOAPI(host, port, user, password, intInfo);
           
            SendMessagePI objMsg = new SendMessagePI();
            objMsg.sendMessageToPI(host, port, user, password, intInfo);
            
            workbook.close();
        }
		  }
 catch (Exception ex) {
    ex.getMessage();
    writeException(ex);
    ex.printStackTrace();
} finally {
    if (fis != null) {
        try {
            fis.close();
            
        } catch (Exception ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
    }
		
}}

	private static void writeException(Exception e) {
		try {
	        FileWriter fs = new FileWriter("C:\\Users\\Administrator\\Desktop\\Errout.txt", true);
	        BufferedWriter out = new BufferedWriter(fs);
	        PrintWriter pw = new PrintWriter(out, true);
	        e.printStackTrace(pw);
	     }
	     catch (Exception ie) {
	        throw new RuntimeException("Could not write Exception to file", ie);
	     }
		
	}
	}
	
