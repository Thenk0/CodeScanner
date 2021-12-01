package com.mycompany.app;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import com.mycompany.app.Camera;
import com.mycompany.app.CsvWriter;
import com.mycompany.app.ScannerRunnable;

public class SimpleGUI extends JFrame{
	private JButton button_start = new JButton("Start");
	private JButton button_end = new JButton("End and save");
	private JButton button_create_file_name = new JButton ("Create new file");
	
	private JLabel label_ip = new JLabel("IP");
	private JLabel label_port = new JLabel("Port");
	private JLabel label_file_name = new JLabel("File name");
	private JLabel label_text_for_numbers = new JLabel("Counter all");
	private JLabel label_numbers = new JLabel ("0");
	private JLabel label_text_for_numbers_unique = new JLabel("Counter unique");
	private JLabel label_numbers_unique = new JLabel ("0");
	private JLabel label_empty = new JLabel ("");
	private JLabel label_works = new JLabel (""); 
	private JLabel label_empty_1 = new JLabel ("");
	
	private JTextField input_ip = new JTextField ("", 15);
	private JTextField input_port = new JTextField ("", 15);
	private JTextField input_name = new JTextField ("", 15);
	private static Camera camera = null;
	private static CsvWriter csvWriter = null;
	private static Thread writerThread = null;
	public SimpleGUI () {
		super("Program");
		this.setBounds(100, 100, 550, 350);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container container = this.getContentPane();
		container.setLayout(new GridLayout(8, 2));
		
		container.add(label_ip);
		container.add(input_ip);
		container.add(label_port);
		container.add(input_port);
		container.add(label_file_name);
		container.add(input_name);
		container.add(button_create_file_name);
		container.add(label_empty);
		container.add(label_text_for_numbers);
		container.add(label_numbers);
		container.add(label_text_for_numbers_unique);
		container.add(label_numbers_unique);
		container.add(label_works);
		container.add(label_empty_1);
		container.add(button_start);
		container.add(button_end);
	
		label_works.setVisible(false);
		
		button_end.setEnabled(false);
		button_create_file_name.setEnabled(false);
		
		button_start.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
            	String ip = input_ip.getText();
            	String port = input_port.getText();
            	String fileName = input_name.getText();
				if(ip.equals("")){
            		JOptionPane.showMessageDialog(null,"Вы не ввели IP");
            		return;
            	}
				if(port.equals("")){
            		JOptionPane.showMessageDialog(null,"Вы не ввели Port.");
            		return;
            	}
				if(fileName.equals("")){
            		JOptionPane.showMessageDialog(null,"Вы не ввели название файла.");
            		return;
            	}
				try {
					camera = new Camera(ip, Integer.valueOf(port));
					csvWriter = new CsvWriter(fileName);
					writerThread = new Thread(new ScannerRunnable(csvWriter, camera, label_numbers_unique, label_numbers, label_works));
					writerThread.start();
					button_start.setEnabled(false);
					button_end.setEnabled(true);
					button_create_file_name.setEnabled(true);
					input_ip.setEnabled(false);
					input_port.setEnabled(false);
					input_name.setEnabled(false);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Вы не подключились. Проверьте правильность заполнение полей 'IP' и 'Port'");
				}
//				label_numbers.setText(String.valueOf(csvWriter.getRecordAmount()));
            }
			
		});
		
		button_create_file_name.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					csvWriter.changeFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		button_end.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				writerThread.interrupt();
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
        		label_works.setVisible(false);
            	button_start.setEnabled(true);
				button_end.setEnabled(false);
				input_ip.setEnabled(true);
				input_port.setEnabled(true);
				input_name.setEnabled(true);
            }
		});

	}
	
	public static void main(String[] args) {
		SimpleGUI app = new SimpleGUI();
		app.setVisible(true);
	}

	
}
