/******************************************************************************/
/*                                                               Date:12/2014 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  Yan.Teng                                                        */
/*  Email  :  Yan.Teng@tcl-mobile.com                                         */
/*  Role   :                                                                  */
/*  Reference documents :                                                     */
/* -------------------------------------------------------------------------- */
/*  Comments :                                                                */
/*  File     :                                                                */
/*  Labels   :                                                                */
/* -------------------------------------------------------------------------- */
/* ========================================================================== */
/*     Modifications on Features list / Changes Request / Problems Report     */
/* -------------------------------------------------------------------------- */
/*    date   |        author        |         Key          |     comment      */
/* ----------|----------------------|----------------------|----------------- */
/* 12/23/2014|      Gu Feilong      |       PR880702       |Note has stopped  */
/*           |                      |                      |when edit the note*/
/*           |                      |                      |                  */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
package com.tct.note.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;

import com.tct.note.Constants;
import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.util.TctLog;

public class MyWidgetService extends Service  {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		int p = intent.getIntExtra("position", 0);
		boolean flag = intent.getBooleanExtra("skip", false);
		
		
		
		Cursor total_cursor = this.getContentResolver().query(Note.CONTENT_URI, null, null, null,
				Note.COLUMN_MODIFY_TIME + " ASC");
		
		if(total_cursor == null){
			return;
		}
		
		total_cursor.moveToPosition(p);
		
		if("com.tct.note.widget.pre".equals(intent.getAction())){
			if(p!=0){
				total_cursor.moveToPrevious();
				TctLog.d("nb","moveToPrevious..");
			}
		}else if("com.tct.note.widget.next".equals(intent.getAction())){
			if(p<total_cursor.getCount()){
				total_cursor.moveToNext();
				TctLog.d("nb","moveToNext..");
			}
		}
		
		p = total_cursor.getPosition();
		
		
		if(p<total_cursor.getCount() && total_cursor.getCount() > 0){//For PR961059
		
		Note mNote = NoteUtils.toNote(total_cursor);
		TctLog.d("nb","onStart_ID.."+mNote.mId);
		
		
		if(total_cursor!=null)
			total_cursor.close();
		
		
		
        Intent widgetIntent = new Intent("com.tct.note.widget.UPDATE");
//        widgetIntent.setComponent(NoteAppWidgetProvider.COMPONENT);
        widgetIntent.putExtra(Constants.EXTRA_NOTE_ID, mNote.mId);
        SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",
                Context.MODE_PRIVATE);
        widgetNoteMap.edit().putLong("widget", mNote.mId).apply();
       //modify by mingyue.wang for PR880846 begin
        widgetNoteMap.edit().putInt("position", p).apply();
        widgetNoteMap.edit().putBoolean("skip", flag).apply();
        widgetIntent.putExtra("skip", flag);
        //widgetIntent.putExtra("position", p);
        
        //PR921823.Change pre/next at widget,play icon change.Added by hz_nanbing.zou at 2015/02/03 begin
        SharedPreferences widgetaudio = getSharedPreferences("widget_audio",
                Context.MODE_PRIVATE);
        boolean isaudio = widgetaudio.getBoolean("pauseAudio", false);
        widgetIntent.putExtra("playing_audio", isaudio);
        //PR921823.Change pre/next at widget,play icon change.Added by hz_nanbing.zou at 2015/02/03 end
        
       //modify by mingyue.wang for PR880846 end
        sendBroadcast(widgetIntent);
		}
	}
	
       //PR880702 modified by Gu Feilong at 2014-12-23 start
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // TODO Auto-generated method stub
            if(null==intent){
                return START_REDELIVER_INTENT;
            }else{
                return super.onStartCommand(intent, flags, startId);
            }
        }
      //PR880702 modified by Gu Feilong at 2014-12-23 end
}
