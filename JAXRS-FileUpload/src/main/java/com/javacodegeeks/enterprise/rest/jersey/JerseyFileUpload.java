package com.javacodegeeks.enterprise.rest.jersey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/upload")
public class JerseyFileUpload {

	private static final String SERVER_UPLOAD_LOCATION_FOLDER = "C://jetty-distribution-9.2.19.v20160908/webapps/Upload_Files/";

	/**
	 * Upload a Image
	 */

	@POST
	@Path("/images")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		Map<String, String> FileMap = contentDispositionHeader.getParameters();
		//System.out.println("FileName: "+FileMap.get("filename"));
		String FileName = FileMap.get("filename");
		
		String filePath = SERVER_UPLOAD_LOCATION_FOLDER
				+ FileName;
		
		// save the file to the server
		saveFile(fileInputStream, filePath);
		
		String postfilePath = "http://ws.crm.com.tw:8080/Upload_Files/"
					+FileName;
		
		JSONObject jsonObject = new JSONObject();
		JSONObject datajsonObject = new JSONObject();
		datajsonObject.put("src", postfilePath);
		jsonObject.put("data", datajsonObject);
		jsonObject.put("msg", "");
		jsonObject.put("code", 0);

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();

		//String output = "File saved to server location : " + filePath;

		//return Response.status(200).entity(output).build();

	}
	
	/**
	 * Upload a File
	 */

	@POST
	@Path("/files")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
		
		Map<String, String> FileMap = contentDispositionHeader.getParameters();
		//System.out.println("FileName: "+FileMap.get("filename"));
		String FileName = FileMap.get("filename");
		
		String filePath = SERVER_UPLOAD_LOCATION_FOLDER
				+ FileName;
		
		// save the file to the server
		saveFile(fileInputStream, filePath);
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("config.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String Upload_Files_protocol = prop.getProperty("Upload_Files.protocol");
		String Upload_Files_hostname = prop.getProperty("Upload_Files.hostname");
		String Upload_Files_port = prop.getProperty("Upload_Files.port");
		
		String postfilePath = Upload_Files_protocol+"//"+Upload_Files_hostname+":"+Upload_Files_port+"/Upload_Files/"
					+FileName;
		
		JSONObject jsonObject = new JSONObject();
		JSONObject datajsonObject = new JSONObject();
		datajsonObject.put("src", postfilePath);
		datajsonObject.put("name", FileName);
		jsonObject.put("data", datajsonObject);
		jsonObject.put("msg", "");
		jsonObject.put("code", 0);

		return Response.status(200).entity(jsonObject.toString())
				.header("Access-Control-Allow-Origin", "*")
			    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
			    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With").build();

		//String output = "File saved to server location : " + filePath;

		//return Response.status(200).entity(output).build();

	}

	// save uploaded file to a defined location on the server
	private void saveFile(InputStream uploadedInputStream, String serverLocation) {

		try {
			OutputStream outpuStream = new FileOutputStream(new File(
					serverLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			outpuStream = new FileOutputStream(new File(serverLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
			outpuStream.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}