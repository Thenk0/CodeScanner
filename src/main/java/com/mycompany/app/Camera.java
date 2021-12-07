package com.mycompany.app;

import java.io.*;
import java.net.*;
import com.mycompany.app.CsvWriter;

public class Camera {
	private String ip;
	private Integer port;
	private Socket socket;
	private BufferedReader input;
	/**
	  * <p>Connects to provided IP and port</p>
	  * <p>Calls connect</p>
	  * @throws IOException if connection failed
	  * @throws UnknownHostException
	  */
	public Camera(String ip, Integer port) throws UnknownHostException, IOException {
		this.ip = ip;
		this.port = port;
		this.connect();
	}
	 /**
	  * <p>Initializes socket connection.</p>
	  * <p>Opens input stream and prepares for data input</p>
	  */
	public void connect() throws UnknownHostException, IOException {
		this.socket = new Socket(this.ip, this.port);
		socket.setSoTimeout(5*1000);
		InputStream in = this.socket.getInputStream();
		this.input = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	}
	 /**
	  * <p>Closes input stream and disconnects from socket</p>
	  */
	public void disconnect() throws IOException {
		this.socket.close();
		this.input.close();
	}
	 /**
	  * <p>Reads a single line from connected socket</p>
	  * @throws IOException if data was read incorrectly or socket timed out
	  * @return String: A single line read from socket
	  */
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
