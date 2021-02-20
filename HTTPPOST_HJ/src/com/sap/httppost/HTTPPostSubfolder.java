package com.sap.httppost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.methods.HttpPost;

public class HTTPPostSubfolder {

	public StringBuffer httpCall(String URL, String userAuth, String fileN) {
		StringBuffer response = new StringBuffer();
		try {
			String finalURL = URL;
			File textFile = new File(fileN);

			URL url = new URL(finalURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			String encoding = Base64.getEncoder().encodeToString((userAuth).getBytes("UTF-8"));

			con.setRequestProperty("Authorization", "Basic " + encoding);
			con.setRequestProperty("Content-Type", "text/plain");
			con.setRequestMethod("POST");
			con.setRequestProperty("HeaderFieldOne", fileN);
		
			con.setDoOutput(true);
			con.connect();
			OutputStream os = con.getOutputStream();
			Files.copy(textFile.toPath(), os);
			os.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();

			response.append(con.getResponseMessage() +con.getHeaderField("msgguid"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	public static List<File> listf(String directoryName) {
		File directory = new File(directoryName);

		List<File> resultList = new ArrayList<File>();
		List<File> resultListF = new ArrayList<File>();
		// get all the files from a directory
		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));
		for (File file : fList) {
			if (file.isFile() && !file.getName().contains("Response")   && !file.getName().contains(".jar")  && !file.getName().contains(".properties")  ) {
				resultListF.add(file);
				// System.out.println(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				// resultList.addAll(listf(file.getAbsolutePath()));
				resultListF.addAll(listf(file.getAbsolutePath()));
			}
		}
		// System.out.println(fList);
		return resultListF;
	}

	public static void main(String[] args) {
		try {

			
			String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath()
					.replaceAll("/bin", "") + "PIConfig.properties";
			//System.out.println(rootPath);
			InputStream input = new FileInputStream(rootPath);

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			HTTPPostSubfolder obj = new HTTPPostSubfolder();
			String finalURL = prop.getProperty("PI.URL");
			String folder = prop.getProperty("PI.Folder");
			String User = prop.getProperty("PI.User");
			String password = prop.getProperty("PI.Password");
			String userCred = User + ":" + password;
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			
			
			
			List<File> filesTest = listf(folder);
			int i = 0;
			for (Iterator<File> it = filesTest.iterator(); it.hasNext(); i++) {
				String path = it.next().getPath();
				String parentFolder = path.substring(0,path.lastIndexOf("\\"));
				String fileName = path.substring(path.lastIndexOf("\\")+1);
				
				StringBuffer resp = obj.httpCall(finalURL, userCred, path);
				
				File responseFile = new File(parentFolder+"/Response_"+fileName+"_" +timeStamp +".txt");
				FileWriter fw = new FileWriter(responseFile);
				fw.write(path + " "+resp.toString()+"\n");
				fw.close();
			}
		
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
