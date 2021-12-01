package com.mycompany.app;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

import com.mycompany.app.CsvWriter;

import java.lang.*;

public class Camera {
	private String ip;
	private Integer port;
	private Socket socket;
	private BufferedReader input;
	public Camera(String ip, Integer port) throws UnknownHostException, IOException {
		this.ip = ip;
		this.port = port;
		this.connect();
	}
	
	public void connect() throws UnknownHostException, IOException {
		this.socket = new Socket(this.ip, this.port);
		socket.setSoTimeout(5*1000);
		InputStream in = this.socket.getInputStream();
		this.input = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	}
	
	public void disconnect() throws IOException {
		this.socket.close();
		this.input.close();
	}
	
	public String readData() throws IOException {
		String cameraInput = this.input.readLine();
		cameraInput = cameraInput.replaceAll("","");
		cameraInput = cameraInput.replaceAll("","");
		return cameraInput;
	}
	
	public static void main(String args[]) throws UnknownHostException, IOException, InterruptedException, NullPointerException{
		System.out.println("Started");
		CsvWriter csvWriter = new CsvWriter("Text");
		Camera camera = new Camera("192.168.1.17", 51236);
		String cameraInput;
		Integer counter = 0;
		while (counter < 2000) {
			Thread.sleep(10);
			counter++;
			cameraInput = camera.readData();
			System.out.println(cameraInput);
			csvWriter.writeToFile(cameraInput);
		}
		camera.disconnect();
		csvWriter.getFileWriter().close();
	}
}
