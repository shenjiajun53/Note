package com.tct.note.widget;

import com.tct.jrdnote.INotePad;
import com.tct.note.Constants;
import com.tct.note.data.MiniAppDataCache;
import com.tct.note.data.Note;

import com.tct.note.ui.NoteEditorActivity;

import android.app.Service;
//import android.content.ContentResolver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
//import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class UpdateWidgetService extends Service implements IDefine {

	private static final String[] NOTE_PROJECTION = new String[] {
        Note.COLUMN_ID, 
	};
	
	/*private static final int MSG_GET_TIME = 0;
	private static final int MSG_GET_DBTEXT = 1;
	private static final int MSG_GET_CURRENT_POSITION = 2;
	private static final int MSG_GET_ITEM_COUNT = 3;
	private static final int MSG_BIND_DATA = 4;*/
	
	public final static int NOTE_PREV_WHAT = 0;
    public final static int NOTE_NEXT_WHAT = NOTE_PREV_WHAT + 1;
    public final static int NOTE_UPDATE_WHAT = NOTE_NEXT_WHAT + 1;
    public final static int NOTE_VIEW_WHAT = NOTE_UPDATE_WHAT + 1;
    public final static int NOTE_REFRESH_WHAT = NOTE_VIEW_WHAT + 1;
    public final static int NOTE_UPDATE_INIT = NOTE_REFRESH_WHAT + 1;
    
    public final static String DATA_CHANGED = "com.tct.note.action.datachanged";
    public final static String DATA_DELETED = "com.tct.note.action.datadeleted";
	
	//private MyServiceHandler mHandler;
	
	private SharedPreferences mSPref;
	private NoteDataChangeObserver mContentObserver;
	private MiniAppDataCache mMiniAppDataCache;
	private NoteDataChangedReciever mReciever;
		
	private int mCurrentIndex = -1;
	private int mNoteItemCount = 0;
	
	private Cursor mCursor;
	
	/*private class MyServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			
			
			super.handleMessage(msg);
		}
		
	}*/
	
	private class NoteDataChangeObserver extends ContentObserver {

		public NoteDataChangeObserver(Handler handler) {
			super(handler);
			
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			
			super.onChange(selfChange, uri);
		}

		@Override
		public void onChange(boolean selfChange) {
			mCursor = UpdateWidgetService.this.getContentResolver().query(Note.CONTENT_URI, NOTE_PROJECTION, null, null,
					Note.COLUMN_MODIFY_TIME + " DESC");
			if (null != mCursor) {
				mNoteItemCount = mCursor.getCount();
			}
			super.onChange(selfChange);
		}
		
	}
	
	public class NoteDataChangedReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			long noteId = intent.getLongExtra("note_id", 0);
			mCursor = UpdateWidgetService.this.getContentResolver().query(Note.CONTENT_URI, NOTE_PROJECTION, null, null,
					Note.COLUMN_MODIFY_TIME + " DESC");
			if (null != mCursor) {
				mNoteItemCount = mCursor.getCount();
			}
			
			if (DATA_CHANGED.equals(action) && null != mMiniAppDataCache) {
				mMiniAppDataCache.updateNoteData(noteId);
			}
			else if (DATA_DELETED.equals(action) && null != mMiniAppDataCache) {
				mMiniAppDataCache.deleteNoteData(noteId);
			}
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return new NoteBinder();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		mSPref = this.getSharedPreferences("note_index", Context.MODE_PRIVATE);
		//mHandler = new MyServiceHandler();
		mCursor = getContentResolver().query(Note.CONTENT_URI, NOTE_PROJECTION, null, null,
				Note.COLUMN_MODIFY_TIME + " DESC");
		if (null != mCursor) {
			mNoteItemCount = mCursor.getCount();
		}
		
		mContentObserver = new NoteDataChangeObserver(new Handler());
		this.getContentResolver().registerContentObserver(Note.CONTENT_URI, true, mContentObserver);
		
		mMiniAppDataCache = MiniAppDataCache.getInstance(this);
		
		mReciever = new NoteDataChangedReciever();
		IntentFilter intentF = new IntentFilter();
		intentF.addAction(DATA_DELETED);
		intentF.addAction(DATA_CHANGED);
		this.registerReceiver(mReciever, intentF);
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		
		if (null != mCursor && !mCursor.isClosed()) {
			mCursor.close();
			mCursor = null;
		}
		
		if (null != mContentObserver) {
			this.getContentResolver().unregisterContentObserver(mContentObserver);
		}
		
		if (null != mMiniAppDataCache) {
			mMiniAppDataCache.releaseCache();
		}
		
		if (null != mReciever) {
			this.unregisterReceiver(mReciever);
		}
				
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		
		super.onRebind(intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		
		return super.onUnbind(intent);
	}
	
	class NoteBinder extends INotePad.Stub {

		@Override
		public int bindNoteData(int type) throws RemoteException {
			if (NOTE_VIEW_WHAT == type) {
				startEdit();
				
				return 1;
			}
			else {
				return bindData(type);
			}
		}

		@Override
		public String getTime() throws RemoteException {
			
			return mMiniAppDataCache.getFormatTime();
		}

		@Override
		public String getDBText() throws RemoteException {
			
			return mMiniAppDataCache.getNoteText();
		}

		@Override
		public String getSDCardText() throws RemoteException {
			
			return "";
		}

		@Override
		public long getCurrentPosition() throws RemoteException {
			
			return mCurrentIndex;
		}

		@Override
		public long getDataLength() throws RemoteException {
			
			return mNoteItemCount;
		}

		@Override
		public boolean isNotesItemNull() throws RemoteException {
			
			return (mNoteItemCount == 0);
		}

		@Override
		public int getType() throws RemoteException {
			
			return 0;
		}

		@Override
		public boolean addNewNote() throws RemoteException {
			
			startCreateOne();
            
			return true;
		}

		@Override
		public void updateNoteWidget(int flag) throws RemoteException {
			
			
		}
		
	}
	
	public void startEdit() {
		mCurrentIndex = mSPref.getInt("currentIndex", 1);
		
		if (null != mCursor && 0 != mCursor.getCount() && mCurrentIndex <= mCursor.getCount()) {
			
			mCursor.moveToPosition(mCurrentIndex - 1);
			int noteID = mCursor.getInt(mCursor.getColumnIndex(Note.COLUMN_ID));
			
            Intent intent = new Intent("com.tct.note.action.VIEW");
            intent.putExtra(Constants.EXTRA_NOTE_ID, noteID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            UpdateWidgetService.this.startActivity(intent);
		}
	}
	
	public void startCreateOne() {
		Intent intent = new Intent();
        intent.setClass(getApplicationContext(), NoteEditorActivity.class);
        intent.putExtra(Constants.EXTRA_NOTE_ID, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}
	
	public int bindData(int type) {
		boolean result = false;
		mCurrentIndex = mSPref.getInt("currentIndex", 1);
		
		switch (type) {
		case NOTE_PREV_WHAT:
			mCurrentIndex -= 1;
			
			break;
		case NOTE_NEXT_WHAT:
			mCurrentIndex += 1;
			
			break;
		case NOTE_UPDATE_WHAT:
		case NOTE_REFRESH_WHAT:
			
			break;
			default:
				break;
		}
		
		if (null != mCursor && 0 != mCursor.getCount() && mCurrentIndex <= mCursor.getCount()) {
			mCursor.moveToPosition(mCurrentIndex - 1);
			mNoteItemCount = mCursor.getCount();
			long noteID = mCursor.getInt(mCursor.getColumnIndex(Note.COLUMN_ID));
			
			result = mMiniAppDataCache.bindCurrentNote(noteID);
		}
		else if (null != mCursor && 0 != mCursor.getCount() && mCurrentIndex > mCursor.getCount()) {
			mCurrentIndex = mCursor.getCount();
			mNoteItemCount = mCursor.getCount();
			mCursor.moveToPosition(mCurrentIndex - 1);
			long noteID = mCursor.getInt(mCursor.getColumnIndex(Note.COLUMN_ID));
			
			result = mMiniAppDataCache.bindCurrentNote(noteID);
		}
		else if (null != mCursor && 0 == mCursor.getCount()) {
			mCurrentIndex = 0;
			mNoteItemCount = 0;
			result = false;
		}
		if (0 != mCurrentIndex) {
			SharedPreferences.Editor editor = mSPref.edit();
			editor.putInt("currentIndex", mCurrentIndex);
			editor.commit();
		}
		
		//PR869874.MiniApp display not normal.
		if(type == NOTE_UPDATE_WHAT){
			return 1;
		}
		//PR869874.MiniApp display not normal.
		
		return result? 1:0;
	}
}
