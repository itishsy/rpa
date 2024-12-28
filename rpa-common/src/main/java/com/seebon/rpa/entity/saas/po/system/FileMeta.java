package com.seebon.rpa.entity.saas.po.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

//ignore "bytes" when return json format
@JsonIgnoreProperties({ "bytes" })
public class FileMeta {

	private String fileName;
	private int fileID;
	private String fileSize;
	private String fileType;

	private byte[] bytes;

	private int num;

	private Map<String , String> sheetNames;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileID() {
		return fileID;
	}

	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getNum(){
		return num;
	}

	public void setNum(int num){
		this.num = num;
	}

    public Map<String, String> getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(Map<String, String> sheetNames) {
        this.sheetNames = sheetNames;
    }
}
