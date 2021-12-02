package com.mycompany.app;

import com.mycompany.app.CsvWriter;

import java.io.IOException;

import javax.swing.JLabel;

import com.mycompany.app.Camera;

public class ScannerRunnable implements Runnable {

	private CsvWriter csvWriter;
	private Camera camera;
	private JLabel label;
	private JLabel allLabel;
	private JLabel works;
	private Integer counter;
	private Integer allCounter;

	public ScannerRunnable(CsvWriter csvWriter, Camera camera, JLabel label, JLabel allLabel, JLabel works) {
		this.csvWriter = csvWriter;
		this.camera = camera;
		this.label = label;
		this.allLabel= allLabel;
		this.works = works;
		this.counter = 0;
		this.allCounter = 0;
	}

	@Override
	public void run() {
		String cameraInput;
		Boolean writeResult;
		while (!Thread.currentThread().isInterrupted()) {
			try {
				this.works.setVisible(true);
				this.works.setText("Waiting");
				try {
					cameraInput = this.camera.readData();					
				} catch (Exception SocketTimedOutException) {
					camera.disconnect();
					this.works.setText("Connection failed: Reconnecting");
					while (true) {
						try {
							camera.connect();
							Thread.sleep(500);
							cameraInput = this.camera.readData();	
							break;
						} catch (Exception e) {
							camera.disconnect();
						}
					}
				}
				if (!cameraInput.equals("")) {
					allCounter++;
					this.works.setText("Works");
					Thread.sleep(10);
				}
				writeResult = csvWriter.writeToFile(cameraInput);
				if (writeResult) {
					counter++;
				}
				this.allLabel.setText(String.valueOf(allCounter));
				this.label.setText(String.valueOf(counter));
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
