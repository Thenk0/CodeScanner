package com.mycompany.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvWriter {
	private String fileName;
	private String fileFolder = "";
	private String fullFileName;
	private Integer allRecordAmount = 0;
	private Integer recordAmount = 0;
	private Integer files = 0;
	private String postfix = "";
	private String extention = "csv";
	private File file;
	private Map<String, Boolean> hashMap;
	private FileWriter fw;

	public CsvWriter(String fileName) {
		this.setFileName(fileName);
		this.setFile(this.getFullFileName());
		try {
			this.setFileWriter(this.getFile());
			this.setFileHashMap(this.generateFileMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public CsvWriter(String fileName, String fileFolder) {
		this.setFileFolder(fileFolder);
		this.setFileName(fileName);
		this.setFile(this.getFullFileName());
		try {
			this.setFileWriter(this.getFile());
			this.setFileHashMap(this.generateFileMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
		this.fullFileName = String.format("%s%s%s.%s",this.getFileFolder(), filename, this.postfix, this.extention);
	}
	
	public void setFileFolder(String fileFolder) {
		File file = new File(fileFolder);
		if (file.exists() && file.isDirectory()) {
			this.fileFolder = fileFolder;
			return;
		}
		this.fileFolder = "";
	}
	
	public String getFileFolder() {
		return this.fileFolder;
	}

	public String getFullFileName() {
		return this.fullFileName;
	}

	public String getExtention() {
		return this.extention;
	}

	public void setExtention(String extention) {
		// TODO: add validation
		this.extention = extention;
	}

	public Map<String, Boolean> getFileHashMap() {
		return this.hashMap;
	};

	public void setFileHashMap(Map<String, Boolean> hashMap) {
		this.hashMap = hashMap;
	}

	public void setFile(String fileName) {
		this.file = new File(fileName);
	}

	public File getFile() {
		return this.file;
	}

	public void setFileWriter(File file) throws IOException {
		this.fw = new FileWriter(file, true);
	}

	public FileWriter getFileWriter() {
		return this.fw;
	}

	public void setRecordAmount(Integer recordAmount) {
		this.recordAmount = recordAmount;
		this.allRecordAmount = this.files * 50000 + recordAmount;
	}

	public Integer getRecordAmount() {
		return this.recordAmount;
	}

	public Integer getAllRecordAmount() {
		return this.allRecordAmount;
	}

	public Boolean writeToFile(String inputStr) throws IOException {
		if (inputStr == null) {
			return false;
		}
		if (inputStr.equals("") || inputStr.trim().isEmpty()) {
			return false;
		}
		Boolean canWrite = !this.getFileHashMap().containsKey(inputStr);
		String resultString = String.format("%s\n", inputStr);
		if (!canWrite) {
			return canWrite;
		}
		this.getFileWriter().write(resultString);
		this.setRecordAmount(this.getRecordAmount() + 1);
		this.getFileHashMap().put(inputStr, true);
		// Somehow this fixes the problem with writing 1 column more
		if (this.getRecordAmount() > 49999) {
			this.setRecordAmount(0);
			this.changeFile();
		}
		return canWrite;
	}

	public void changeFile() throws IOException {
		if (this.postfix.equals("")) {
			this.postfix = "1";
		} else {
			Integer postfixInt = Integer.parseInt(this.postfix) + 1;
			this.postfix = postfixInt.toString();
		}
		this.setFileName(this.getFileName());
		this.setFile(this.getFullFileName());
		if (this.getFile().exists() && this.getFile().isFile()) {
			// Preventing adding to hashMap if file was changed during hashMap generation
			if (this.getFileHashMap() != null) {
				this.addToFileMapFromFile();
			}
		} else {
			this.getFile().createNewFile();
		}
		this.getFileWriter().close();
		this.setFileWriter(this.getFile());
		this.files += 1;
	}

	private Map<String, Boolean> generateFileMap() throws IOException {
		Map<String, Boolean> hashMap = new HashMap<String, Boolean>();
		while (true) {
			try (BufferedReader br = new BufferedReader(new FileReader(this.getFile()))) {
				String fileReadLine;
				while ((fileReadLine = br.readLine()) != null) {
					List<String> lineList = Arrays.asList(fileReadLine.split("\\s*,\\s*"));
					for (String value : lineList) {
						hashMap.put(value, true);
					}
					this.setRecordAmount(this.getRecordAmount() + lineList.size());
				}
				if (this.getRecordAmount() < 50000) {
					break;
				}
				this.setRecordAmount(0);
				this.changeFile();
			} catch (FileNotFoundException e) {
				file.createNewFile();
			}
		}
		return hashMap;
	}

	private void addToFileMapFromFile() throws IOException {
		Map<String, Boolean> hashMap = new HashMap<String, Boolean>();
		BufferedReader br = new BufferedReader(new FileReader(this.getFile()));
		String fileReadLine;
		while ((fileReadLine = br.readLine()) != null) {
			List<String> lineList = Arrays.asList(fileReadLine.split("\\s*,\\s*"));
			for (String value : lineList) {
				hashMap.put(value, true);
			}
		}
		br.close();
		this.getFileHashMap().putAll(hashMap);
	}

	public static String getFileExtention(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	// TODO: fix write;
	public static void main(String[] args) throws IOException {
		CsvWriter csvWriter = new CsvWriter("results");
		while (true) {
			for (int i = 0; i < 100020; i++) {
				csvWriter.writeToFile(String.valueOf(i));
			}
			break;
		}
		csvWriter.getFileWriter().close();
	}

}
