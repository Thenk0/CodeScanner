package com.mycompany.app;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mycompany.app.CsvWriter;

class TestCsvWriter {

	private CsvWriter csvWriter;

	public static Integer getReadCountFromFile(String filename) throws IOException {
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String fileReadLine;
		Integer counter = 0;
		while ((fileReadLine = br.readLine()) != null) {
			List<String> lineList = Arrays.asList(fileReadLine.split("\\s*,\\s*"));
			for (int i = 0; i < lineList.size(); i++) {
				counter++;
			}
		}
		br.close();
		return counter;
	}

	@BeforeEach
	void prepare() {
		File file = new File("test.csv");
		file.delete();
		String fileName = "test";
		String extention = "csv";
		Integer fileAmount = 1;
		while (true) {
			file = new File(String.format("%s%d.%s", fileName, fileAmount, extention));
			if (!file.exists()) {
				break;
			}
			file.delete();
			fileAmount += 1;
		}
		csvWriter = new CsvWriter("test");
	}

	@Test
	void testWriting() throws IOException {
		csvWriter.writeToFile("test");
		csvWriter.getFileWriter().close();
		Integer counter = getReadCountFromFile("test.csv");
		assertEquals(Integer.valueOf(1), counter);
	}

	@Test
	void testWritingValidation() throws IOException {
		csvWriter.writeToFile(null);
		csvWriter.writeToFile("");
		csvWriter.writeToFile(" ");
		csvWriter.writeToFile("         ");
		csvWriter.getFileWriter().close();
		Integer counter = getReadCountFromFile("test.csv");
		assertEquals(Integer.valueOf(0), counter);
	}

	@Test
	void testWritingUnique() throws IOException {
		for (int i = 0; i < 20; i++) {
			csvWriter.writeToFile("test");
		}
		csvWriter.getFileWriter().close();
		Integer counter = getReadCountFromFile("test.csv");
		assertEquals(Integer.valueOf(1), counter);
	}

	@Test
	void testWritingToDifferentFile() throws IOException {
		for (int i = 0; i < 65000; i++) {
			csvWriter.writeToFile(String.valueOf(i));
		}
		csvWriter.getFileWriter().close();
		Integer counter = getReadCountFromFile("test.csv");
		counter += getReadCountFromFile("test1.csv");
		assertEquals(Integer.valueOf(65000), counter);
	}

	@Test
	void testContinueWriting() throws IOException {
		for (int i = 0; i < 65000; i++) {
			csvWriter.writeToFile(String.valueOf(i));
		}
		csvWriter.getFileWriter().close();
		File file = new File("test1.csv");
		file.delete();
		csvWriter = new CsvWriter("test");
		for (int i = 0; i < 65000; i++) {
			csvWriter.writeToFile(String.valueOf(i));
		}
		csvWriter.getFileWriter().close();
		Integer counter = getReadCountFromFile("test.csv");
		counter += getReadCountFromFile("test1.csv");
		assertEquals(Integer.valueOf(65000), counter);
	}

	@Test
	void testCheckingNextFile() throws IOException {
		for (int i = 0; i < 105000; i++) {
			csvWriter.writeToFile(String.valueOf(i));
		}
		csvWriter.getFileWriter().close();
		File file = new File("test1.csv");
		file.delete();
		csvWriter = new CsvWriter("test");
		for (int i = 0; i < 105000; i++) {
			csvWriter.writeToFile(String.valueOf(i));
		}
		csvWriter.getFileWriter().close();
		Integer counter = getReadCountFromFile("test.csv");
		counter += getReadCountFromFile("test1.csv");
		counter += getReadCountFromFile("test2.csv");
		assertEquals(Integer.valueOf(105000), counter);
	}

	@Test
	void testNullFileMapGeneration() throws IOException {
		Boolean caught = false;
		try {
			for (int i = 0; i < 105000; i++) {
				csvWriter.writeToFile(String.valueOf(i));
			}
			csvWriter.getFileWriter().close();
			csvWriter = new CsvWriter("test");
		} catch (NullPointerException e) {
			caught = true;
		}
		assertFalse(caught);
	}
	
	@AfterAll
	public static void cleanup() {
		File file = new File("test.csv");
		file.delete();
		String fileName = "test";
		String extention = "csv";
		Integer fileAmount = 1;
		while (true) {
			file = new File(String.format("%s%d.%s", fileName, fileAmount, extention));
			if (!file.exists()) {
				break;
			}
			file.delete();
			fileAmount += 1;
		}
	}
}
