/******************************************************************************/
/*                                                               Date:07/2013 */
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
/* 07/05/2013|       Yan.Teng       |       CR484292       |the development   */
/*           |                      |                      |of note and note  */
/*           |                      |                      |widget            */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note;

import java.io.IOException;
import java.util.ArrayList;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;

//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
// FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
import com.tct.note.data.Note;
import com.tct.note.util.TctLog;
//import com.tct.note.widget.NoteAppWidgetProvider;
import com.tct.note.widget.NoteMiniAppProvider;

public class MediaPlaybackService extends Service {
    private String TAG = "MediaPlaybackService";
    private MediaPlayer mp;
    private ArrayList<String> audioUriList = new ArrayList<String>();
    private int flag_Audio_num = 0;
    private long noteId;
    public static boolean PauseAudio;
    private boolean mFlag;//add by mingyue.wang for PR880846

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public Cursor getAudioCursor(Context context, long id) {
        Cursor c = context.getContentResolver().query(Note.Audio.CONTENT_URI, null,
                "note_id = " + id, null, null);
        return c;

    }

    @Override
    public void onCreate() {
    	TctLog.d("nb", "ssss");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	TctLog.d("nb", "ddddd");
        noteId = intent.getLongExtra(Constants.EXTRA_NOTE_ID, 0);
        mFlag = intent.getBooleanExtra("skip", false);//add by mingyue.wang for PR880846
        if (mp != null) {
            PauseAudio = mp.isPlaying();
        } else {
            PauseAudio = false;
        }
        Cursor auddioCursor = getAudioCursor(this, noteId);
        if (auddioCursor != null && auddioCursor.getCount() > 0) {
            TctLog.e("ty--", "auddioCursor.getCount(): " + auddioCursor.getCount());
            while (auddioCursor.moveToNext()) {
                audioUriList.add(auddioCursor.getString(auddioCursor
                        .getColumnIndex(Note.Audio.COLUMN_URI)));
            }
        }
        if (auddioCursor != null)
            auddioCursor.close();
        if (PauseAudio) {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
            UpdateWidgetView(!PauseAudio);
            stopSelf();
        } else {
            UpdateWidgetView(!PauseAudio);
            play();
        }
        return startId;

    }

    public void UpdateWidgetView(boolean pauseAudio) {
//        Intent widgetIntent = new Intent("com.jrdcom.note.widget.UPDATE");
    	
    	Intent widgetIntent = new Intent("com.tct.note.widget.UPDATE");
        widgetIntent.setComponent(NoteMiniAppProvider.COMPONENT);
        widgetIntent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
        widgetIntent.putExtra("playing_audio", pauseAudio);
        widgetIntent.putExtra("skip", mFlag);//add by mingyue.wang for PR880846
        sendBroadcast(widgetIntent);
        
        //PR921823.Change pre/next at widget,play icon change.Added by hz_nanbing.zou at 2015/02/03 begin
        SharedPreferences widgetNoteMap = getSharedPreferences("widget_audio",
                Context.MODE_PRIVATE);
        widgetNoteMap.edit().putBoolean("pauseAudio", pauseAudio).apply();
        //PR921823.Change pre/next at widget,play icon change.Added by hz_nanbing.zou at 2015/02/03 end
        
    }

    public void play() {
        try {
            if (audioUriList.size() > 0) {
                mp = new MediaPlayer();
                mp.setDataSource(audioUriList.get(flag_Audio_num));
                mp.prepare();
                mp.start();
                mp.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        TctLog.e(TAG, "audioUriList: " + audioUriList.get(flag_Audio_num));
                        flag_Audio_num++;
                        TctLog.e(TAG, "(flag_Audio_num < audioUriList.size()):  "
                                + (flag_Audio_num < audioUriList.size()));
                        if (flag_Audio_num < audioUriList.size()) {
                            mp.stop();
                            if (mp != null) {
                                mp.release();
                                mp = null;
                            }
                            play();
                        } else {
                            flag_Audio_num = 0;
                            mp.stop();
                            PauseAudio = false;
                            if (mp != null) {
                                mp.release();
                                mp = null;
                            }
                            UpdateWidgetView(PauseAudio);
                            stopSelf();
                        }
                    }

                });
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TctLog.e(TAG, "services onDestroy() ");
        if (mp != null) {
            mp.release();
            mp = null;
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }
}
