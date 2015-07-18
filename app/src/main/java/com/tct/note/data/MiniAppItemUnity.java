package com.tct.note.data;

public class MiniAppItemUnity {

	private String mNoteText;
	
	private String mFormatCreateTime;
	
	private String mFormatModifiedTime;
	
	public MiniAppItemUnity() {
		mNoteText = "";
		mFormatCreateTime = "";
		mFormatModifiedTime = "";
	}
	
	public void setNoteText(String aText) {
		mNoteText = aText;
	}
	
	public void setFormatCreateTime(String aFormatCreateTime) {
		mFormatCreateTime = aFormatCreateTime;
	}
	
	public void setFormatModifiedTime(String aFormatModifiedTime) {
		mFormatModifiedTime = aFormatModifiedTime;
	}
	
	public String getNoteText() {
		return mNoteText;
	}
	
	public String getFormatCreateTime() {
		return mFormatCreateTime;
	}
	
	public String getFormatModifiedTime() {
		return mFormatModifiedTime;
	}
	
}
