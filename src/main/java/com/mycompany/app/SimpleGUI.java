package com.mycompany.app;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.mycompany.app.Camera;
import com.mycompany.app.CsvWriter;
import com.mycompany.app.ScannerRunnable;

public class SimpleGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton buttonStart;
	private JButton buttonStop;
	private JButton buttonCreateFileName;
	private JButton buttonSelectADirectory;

	private JLabel labelIp;
	private JLabel labelPort;
	private JLabel labelFileName;
	private JLabel labelTextForNumbers;
	private JLabel labelNumbers;
	private JLabel labelTextForNumbersUnique;
	private JLabel labelNumbersUnique;
	private JLabel labelStatus;

	private JTextPane lastScans;
	private JLabel lastScansLabel;

	private JLabel labelCurrentFile;
	private JLabel labelDirectory;

	private JTextField inputIp;
	private JTextField inputPort;
	private JTextField inputFileName;

	private static Camera camera = null;
	private static CsvWriter csvWriter = null;
	private static Thread writerThread = null;
	private static String selectedDirectory = "";

	private JFileChooser fileChooser = null;

	public SimpleGUI() {
		super("Code Scanner");
		this.setBounds(100, 100, 650, 450);
		this.setMinimumSize(new Dimension(550, 425));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		this.add(pane);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipadx = 0;

		constraints.weightx = 0.4;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.gridy = 0;
		constraints.ipadx = 0;
		pane.add(Box.createGlue(), constraints);

		labelIp = new JLabel("IP", JLabel.RIGHT);
		constraints.weightx = 0;
		constraints.gridx = 1;
		constraints.gridwidth = 1;
		constraints.gridy = 0;
		constraints.ipadx = 5;
		pane.add(labelIp, constraints);

		inputIp = new JTextField("", 15);
		constraints.weightx = 0;
		constraints.gridx = 2;
		constraints.gridwidth = 4;
		constraints.gridy = 0;
		constraints.ipadx = 5;
		constraints.ipady = 5;
		pane.add(inputIp, constraints);

		labelPort = new JLabel("Port", JLabel.RIGHT);
		constraints.weightx = 0;
		constraints.gridx = 1;
		constraints.gridwidth = 1;
		constraints.gridy = 1;
		constraints.ipadx = 5;
		constraints.ipady = 0;
		pane.add(labelPort, constraints);

		inputPort = new JTextField("", 15);
		constraints.weightx = 0;
		constraints.gridx = 2;
		constraints.gridwidth = 4;
		constraints.gridy = 1;
		constraints.ipadx = 5;
		constraints.ipady = 5;
		pane.add(inputPort, constraints);

		labelFileName = new JLabel("File Name", JLabel.RIGHT);
		constraints.weightx = 0;
		constraints.gridx = 1;
		constraints.gridwidth = 1;
		constraints.gridy = 2;
		constraints.ipady = 0;
		pane.add(labelFileName, constraints);

		inputFileName = new JTextField("", 15);
		constraints.weightx = 0;
		constraints.gridx = 2;
		constraints.gridwidth = 4;
		constraints.gridy = 2;
		constraints.ipadx = 5;
		constraints.ipady = 5;
		pane.add(inputFileName, constraints);

		buttonSelectADirectory = new JButton("Select a directory");
		constraints.weightx = 0.1;
		constraints.gridx = 2;
		constraints.gridwidth = 2;
		constraints.gridy = 3;
		constraints.ipady = 10;
		pane.add(buttonSelectADirectory, constraints);

		labelDirectory = new JLabel("");
		Dimension newDim = new Dimension(75, 15);

		labelDirectory.setMinimumSize(newDim);
		labelDirectory.setPreferredSize(newDim);
		labelDirectory.setMaximumSize(newDim);
		labelDirectory.setSize(newDim);
		labelDirectory.revalidate();

		constraints.weightx = 0.1;
		constraints.gridx = 2;
		constraints.gridwidth = 2;
		constraints.gridy = 4;
		constraints.ipady = 10;
		pane.add(labelDirectory, constraints);

		buttonCreateFileName = new JButton("Create new file");
		constraints.weightx = 0.1;
		constraints.gridx = 4;
		constraints.gridwidth = 2;
		constraints.gridy = 3;
		constraints.ipady = 10;
		pane.add(buttonCreateFileName, constraints);

		labelCurrentFile = new JLabel("");
		constraints.weightx = 0.1;
		constraints.gridx = 4;
		constraints.gridwidth = 2;
		constraints.gridy = 4;
		constraints.ipady = 10;
		pane.add(labelCurrentFile, constraints);

		labelTextForNumbers = new JLabel("Total scans:");
		constraints.weightx = 0;
		constraints.gridx = 2;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		pane.add(labelTextForNumbers, constraints);

		labelNumbers = new JLabel("0");
		constraints.weightx = 0;
		constraints.gridx = 3;
		constraints.gridy = 5;
		constraints.ipady = 0;
		pane.add(labelNumbers, constraints);

		labelTextForNumbersUnique = new JLabel("Unique scans:");
		constraints.weightx = 0;
		constraints.gridx = 4;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.ipady = 0;
		pane.add(labelTextForNumbersUnique, constraints);

		labelNumbersUnique = new JLabel("0");
		constraints.weightx = 0;
		constraints.gridx = 5;
		constraints.gridy = 5;
		constraints.ipady = 0;
		pane.add(labelNumbersUnique, constraints);

		labelStatus = new JLabel("", JLabel.CENTER);
		constraints.weightx = 0;
		constraints.gridx = 2;
		constraints.gridwidth = 4;
		constraints.gridy = 6;
		constraints.ipady = 0;
		pane.add(labelStatus, constraints);

		buttonStart = new JButton("Start");
		constraints.weightx = 0;
		constraints.gridx = 2;
		constraints.gridwidth = 2;
		constraints.gridy = 7;
		constraints.ipady = 10;
		pane.add(buttonStart, constraints);

		buttonStop = new JButton("Stop and save");
		constraints.weightx = 0;
		constraints.gridx = 4;
		constraints.gridwidth = 2;
		constraints.gridy = 7;
		constraints.ipady = 10;
		pane.add(buttonStop, constraints);

		constraints.weightx = 0.4;
		constraints.gridx = 6;
		constraints.gridwidth = 1;
		constraints.gridy = 0;
		constraints.ipadx = 0;
		pane.add(Box.createGlue(), constraints);

		lastScansLabel = new JLabel("Last scans", JLabel.CENTER);
		constraints.weightx = 0;
		constraints.gridx = 0;
		constraints.gridwidth = 8;
		constraints.gridy = 8;
		constraints.ipady = 0;
		pane.add(lastScansLabel, constraints);

		lastScans = new JTextPane();
		StyledDocument doc = lastScans.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		StyleConstants.setForeground(center, Color.GRAY);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		constraints.weightx = 1;
		constraints.weighty = 0.4;
		constraints.gridx = 0;
		constraints.gridwidth = 8;
		constraints.gridy = 9;
		constraints.ipadx = 0;
		lastScans.setEditable(false);
		lastScans.setOpaque(false);
		pane.add(lastScans, constraints);

		labelStatus.setVisible(true);
		buttonStop.setEnabled(false);
		buttonCreateFileName.setEnabled(false);
		pane.setVisible(true);
		pane.validate();

		fileChooser = new JFileChooser();
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = inputIp.getText();
				String port = inputPort.getText();
				String fileName = inputFileName.getText();
				if (ip.equals("")) {
					JOptionPane.showMessageDialog(null, "Поле IP не заполнено", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (port.equals("")) {
					JOptionPane.showMessageDialog(null, "Поле Port не заполнено", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					Integer.parseInt(port);
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Port должен быть числом.", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (fileName.equals("")) {
					JOptionPane.showMessageDialog(null, "Поле File name не заполнено.", "Ошибка", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					camera = new Camera(ip, Integer.valueOf(port));
					csvWriter = new CsvWriter(fileName);
					labelCurrentFile.setText(csvWriter.getFullFileName());
					writerThread = new Thread(new ScannerRunnable(csvWriter, camera, labelNumbersUnique, labelNumbers,
							labelStatus, lastScans, labelCurrentFile));
					writerThread.start();
					buttonStart.setEnabled(false);
					buttonStop.setEnabled(true);
					buttonCreateFileName.setEnabled(true);
					buttonSelectADirectory.setEnabled(false);
					inputIp.setEnabled(false);
					inputPort.setEnabled(false);
					inputFileName.setEnabled(false);

				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
							String.format("Вы не подключились. Причина: %s", e1.getMessage()), "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
//				label_numbers.setText(String.valueOf(csvWriter.getRecordAmount()));
			}

		});

		buttonSelectADirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Выберите папку");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showSaveDialog(SimpleGUI.this);
				selectedDirectory = "";
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedDirectory = fileChooser.getSelectedFile().toString() + "/";
					labelDirectory.setToolTipText(selectedDirectory);
					labelDirectory.setText(String.format("Current directory: %s", selectedDirectory));
				}
			}
		});

		buttonCreateFileName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					csvWriter.changeFile();
					labelCurrentFile.setText(csvWriter.getFullFileName());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while(writerThread.isAlive()) {
					writerThread.interrupt();					
				}
				try {
					// Waiting for half a second to avoid "socket closed" exception
					Thread.sleep(500);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
				try {
					camera.disconnect();
					csvWriter.getFileWriter().close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				writerThread = null;
				csvWriter = null;
				camera = null;
				labelCurrentFile.setText("");
				labelStatus.setVisible(false);
				buttonStart.setEnabled(true);
				buttonStop.setEnabled(false);

				buttonSelectADirectory.setEnabled(true);
				buttonCreateFileName.setEnabled(false);
				inputIp.setEnabled(true);
				inputPort.setEnabled(true);
				inputFileName.setEnabled(true);
			}
		});
		pane.setVisible(true);
	}

	public static void main(String[] args) {
		UIManager.put("FileChooser.saveButtonText", "Выбрать");
		UIManager.put("FileChooser.cancelButtonText", "Отмена");
		UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
		UIManager.put("FileChooser.lookInLabelText", "Директория");
		UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
		UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
		SimpleGUI app = new SimpleGUI();
		app.setVisible(true);

	}

}
