package com.mycompany.app;

import com.mycompany.app.CsvWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextPane;

import com.mycompany.app.Camera;

public class ScannerRunnable implements Runnable {

	private CsvWriter csvWriter;
	private Camera camera;
	private JLabel label;
	private JLabel allLabel;
	private JLabel works;
	private Integer counter;
	private Integer allCounter;
	private JTextPane lastScans;
	private JLabel currentFile;
	private List<String> listScans = new ArrayList<String>();

	public ScannerRunnable(CsvWriter csvWriter, Camera camera, JLabel label, JLabel allLabel, JLabel works, JTextPane lastScans, JLabel currentFile) {
		this.csvWriter = csvWriter;
		this.camera = camera;
		this.label = label;
		this.allLabel= allLabel;
		this.works = works;
		this.counter = 0;
		this.allCounter = 0;
		this.lastScans = lastScans;
		this.currentFile = currentFile;
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
					//Try to reconnect indefinitely, until connection is successful or process is terminated
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
					if (listScans.size() > 15) {
						listScans.remove(listScans.size() - 1);
					}
					listScans.add(0, cameraInput);
					this.works.setText("Works");
					String outputString = "";
					for (String string : listScans) {
						outputString += String.format("%s\n", string);
					}
					this.lastScans.setText(outputString);
					Thread.sleep(10);
				}
				writeResult = csvWriter.writeToFile(cameraInput);
				if (writeResult) {
					counter++;
					if (!currentFile.getText().equals(csvWriter.getFullFileName())) {
						currentFile.setText(csvWriter.getFullFileName());						
					}
				}
				this.allLabel.setText(String.valueOf(allCounter));
				this.label.setText(String.valueOf(counter));
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}

}
