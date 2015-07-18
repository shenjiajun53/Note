package com.tct.note.data;

/*import java.util.TimeZone;*/
import java.util.TreeMap;

import com.tct.note.data.Note.Text;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
/*import android.text.format.DateFormat;
import android.text.format.DateUtils;*/

public class MiniAppDataCache {

	private static final String[] NOTE_PROJECTION = new String[] {
        Note.COLUMN_ID, 
        Note.COLUMN_CREATE_TIME, 
        Note.COLUMN_MODIFY_TIME
	};
	
	private static final String[] TEXT_PROJECTION = new String[] {
        Text.COLUMN_NOTE_ID, 
        Text.COLUMN_TEXT
	};
	
	private static final long MAX_CACHE_NUM = 30;
	
	//private String mNoteCreateTime;
	//private String mNoteModifiedTime;
	private boolean mIsNoteItemNull = true;
	
	private Context mContext;
	
	private static MiniAppDataCache sMiniAppDataCache = null;
	private MiniAppItemUnity mCurrentAppItem = null;
	
	private TreeMap<String, MiniAppItemUnity> mMap = new TreeMap<String, MiniAppItemUnity>();
	
	public MiniAppDataCache(Context aContext) {
		mContext = aContext;
		//mNoteCreateTime = "";
		//mNoteModifiedTime = "";
		mCurrentAppItem = new MiniAppItemUnity();
		
	}
	
	public static MiniAppDataCache getInstance(Context aContext) {
		if (null == sMiniAppDataCache) {
			sMiniAppDataCache = new MiniAppDataCache(aContext);
		}
		
		return sMiniAppDataCache;
	}
	
	public boolean bindCurrentNote(long aNoteID) {
		if (mMap.containsKey(String.valueOf(aNoteID))) {
			
			mCurrentAppItem.setFormatModifiedTime(mMap.get(String.valueOf(aNoteID)).getFormatModifiedTime());
			mCurrentAppItem.setNoteText(mMap.get(String.valueOf(aNoteID)).getNoteText());
			
			return true;
		}
		else {
			MiniAppItemUnity tmpUnity = new MiniAppItemUnity();
			ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(ContentUris.withAppendedId(Note.CONTENT_URI, aNoteID),
            		NOTE_PROJECTION, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
            	String noteText = "";
            	String formatTime = getModifiedTime(cursor);
            	tmpUnity.setFormatModifiedTime(formatTime);
            	mCurrentAppItem.setFormatModifiedTime(formatTime);
            	
            	cursor.close();
            	
            	cursor = resolver.query(Text.CONTENT_URI, TEXT_PROJECTION, Text.COLUMN_NOTE_ID + "=" + aNoteID, null, null);
            	if (null != cursor && cursor.moveToNext()) {
            		int columnIndex;
                    columnIndex = cursor.getColumnIndex(Text.COLUMN_TEXT);
                    if (-1 != columnIndex) {
                    	noteText = cursor.getString(columnIndex);
                    	tmpUnity.setNoteText(noteText);
                    	mCurrentAppItem.setNoteText(noteText);
                    }
                    
                    cursor.close();
                    cursor = null;
            	}
            	
            	if (mMap.size() < MAX_CACHE_NUM) {
                	mMap.put(String.valueOf(aNoteID), tmpUnity);
                }
            }
            
            if (null != cursor && !cursor.isClosed()) {
            	cursor.close();
            	cursor = null;
            }
            return true;
		}
	}
	
	public void deleteNoteData(long aNoteId) {
		if (mMap.containsKey(String.valueOf(aNoteId))) {
			mMap.remove(String.valueOf(aNoteId));
		}
	}
	
	public void updateNoteData(long aNoteId) {
		if (mMap.containsKey(String.valueOf(aNoteId))) {
			MiniAppItemUnity tmpUnity = new MiniAppItemUnity();
			ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(ContentUris.withAppendedId(Note.CONTENT_URI, aNoteId),
            		NOTE_PROJECTION, null, null, null);
            if (null != cursor && cursor.moveToNext()) {
            	String noteText = "";
            	String formatTime = getModifiedTime(cursor);
            	tmpUnity.setFormatModifiedTime(formatTime);
            	
            	cursor.close();
            	
            	cursor = resolver.query(Text.CONTENT_URI, TEXT_PROJECTION, Text.COLUMN_NOTE_ID + "=" + aNoteId, null, null);
            	if (null != cursor && cursor.moveToNext()) {
            		int columnIndex;
                    columnIndex = cursor.getColumnIndex(Text.COLUMN_TEXT);
                    if (-1 != columnIndex) {
                    	noteText = cursor.getString(columnIndex);
                    	tmpUnity.setNoteText(noteText);
                    }
                    
                    cursor.close();
                    cursor = null;
            	}
            	
            	
                mMap.put(String.valueOf(aNoteId), tmpUnity);
            }
            
            if (null != cursor && !cursor.isClosed()) {
            	cursor.close();
            	cursor = null;
            }
		}
	}
	
	public void releaseCache() {
		if (null != mMap) {
			mMap.clear();
		}
	}
	
	@SuppressWarnings({ })
	private String getModifiedTime(Cursor aCr) {
		int columnIndex;
        columnIndex = aCr.getColumnIndex(Note.COLUMN_MODIFY_TIME);
        if (columnIndex != -1) {
        	long modifiedTime = aCr.getLong(columnIndex);
        	
        	/*int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                    | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
                    | DateUtils.FORMAT_ABBREV_WEEKDAY;

            String dateString;
            synchronized (TimeZone.class) {
                dateString = DateUtils.formatDateTime(mContext, modifiedTime, flags);
                // setting the default back to null restores the correct behavior
                TimeZone.setDefault(null);
            }
        	
        	flags = DateUtils.FORMAT_SHOW_TIME;
            if (DateFormat.is24HourFormat(mContext)) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
            String timeString;
            synchronized (TimeZone.class) {
                timeString = DateUtils.formatDateTime(mContext, modifiedTime, flags);
                TimeZone.setDefault(null);
            }
            
            return dateString + " " + timeString;*/
        	return String.valueOf(modifiedTime);
        }
        return "";
	}
	
	public boolean isNoteItemNull() {
		return mIsNoteItemNull;
	}
	
	public String getFormatTime() {
		return mCurrentAppItem.getFormatModifiedTime();
	}
	
	public String getNoteText() {
		return mCurrentAppItem.getNoteText();
	}
}
