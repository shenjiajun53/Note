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
/* 06/09/2014|      yanmei.zhang    |      PR-692135       |Notes Widgets     */
/*           |                      |                      |Content disappear */
/*           |                      |                      |After restart the */
/*           |                      |                      |phone             */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;



import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

//PR833528 Guest_Mode not perform note.Added by hz_nanbing.zou at 17/14/2014 begin
import com.tct.note.Constants;
import com.tct.note.SystemProperties;
import com.tct.note.util.AttachmentUtils;
import com.tct.note.util.ReminderReceiver;
import com.tct.note.util.TctLog;
//import com.tct.note.widget.NoteAppWidgetProvider;
import com.tct.note.widget.NoteMiniAppProvider;
import com.tct.note.widget.UpdateWidgetService;
//PR833528 Guest_Mode not perform note.Added by hz_nanbing.zou at 17/14/2014 end

//[BUGFIX]-Add-BEGIN by TCTNB.YuTao.Yang,09/26/2013,PR528733,
//[Notes]The reminder still prompt an alarm after delete it.
import android.content.ContentUris;
import android.app.AlarmManager;
import android.app.PendingIntent;

public class NoteUtils {
    private static final String TAG = NoteUtils.class.getSimpleName();

    public static Note toNote(Cursor cursor) {
        Note note = new Note();

        int columnIndex;
        columnIndex = cursor.getColumnIndex(Note.COLUMN_ID);
        if (columnIndex != -1) {
            note.mId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_GROUP_ID);
        if (columnIndex != -1) {
            note.mGroupId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_TITLE);
        if (columnIndex != -1) {
            note.mTitle = cursor.getString(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_LABEL);
        if (columnIndex != -1) {
            note.mLabel = cursor.getString(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_HAS_TEXT);
        if (columnIndex != -1) {
            note.mHasText = cursor.getInt(columnIndex) == 1;
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_HAS_IMAGE);
        if (columnIndex != -1) {
            note.mHasImage = cursor.getInt(columnIndex) == 1;
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_HAS_REMINDER);
        if (columnIndex != -1) {
            note.mHasReminder = cursor.getInt(columnIndex) == 1;
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_HAS_AUDIO);
        if (columnIndex != -1) {
            note.mHasAudio = cursor.getInt(columnIndex) == 1;
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_THUMBNAIL_URI);
        if (columnIndex != -1) {
            note.mThumbnailUri = cursor.getString(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_SMALL_THUMBNAIL_URI);
        if (columnIndex != -1) {
            note.mSmallThumbnailUri = cursor.getString(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_BG_IMAGE_RES_ID);
        if (columnIndex != -1) {
            note.mBgImageResId = cursor.getInt(columnIndex);
            if (note.mBgImageResId == 0) {
                note.mBgImageResId = Constants.DEFAULT_NOTE_BG_RES_ID;
            }
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_CREATE_TIME);
        if (columnIndex != -1) {
            note.mCreateTime = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_MODIFY_TIME);
        if (columnIndex != -1) {
            note.mModifyTime = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.COLUMN_WIDGET_NOTE);
        if (columnIndex != -1) {
            note.WidgetNoteId = cursor.getInt(columnIndex);//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        }

        return note;
    }

    public static Note.Text toText(Cursor cursor) {
        Note.Text text = new Note.Text();

        int columnIndex;
        columnIndex = cursor.getColumnIndex(Note.Text.COLUMN_ID);
        if (columnIndex != -1) {
            text.mId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Text.COLUMN_NOTE_ID);
        if (columnIndex != -1) {
            text.mNoteId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Text.COLUMN_CONTENT);
        if (columnIndex != -1) {
            text.mContent = cursor.getString(columnIndex);
        }

        return text;
    }

    public static Note.Image toImage(Cursor cursor) {
        Note.Image image = new Note.Image();

        int columnIndex;
        columnIndex = cursor.getColumnIndex(Note.Image.COLUMN_ID);
        if (columnIndex != -1) {
            image.mId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Image.COLUMN_NOTE_ID);
        if (columnIndex != -1) {
            image.mNoteId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Image.COLUMN_URI);
        if (columnIndex != -1) {
            image.mUri = cursor.getString(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Image.COLUMN_Content);
        if (columnIndex != -1) {
            image.mContent = cursor.getString(columnIndex);
        }
        return image;
    }

    public static Note.Reminder toReminder(Cursor cursor) {
        Note.Reminder Reminder = new Note.Reminder();

        int columnIndex;
        columnIndex = cursor.getColumnIndex(Note.Reminder.COLUMN_ID);
        if (columnIndex != -1) {
            Reminder.mId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Reminder.COLUMN_NOTE_ID);
        if (columnIndex != -1) {
            Reminder.mNoteId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Reminder.COLUMN_URI);
        if (columnIndex != -1) {
            Reminder.mUri = cursor.getString(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Reminder.COLUMN_TIME);
        if (columnIndex != -1) {
            Reminder.mReminderTime = cursor.getLong(columnIndex);
        }

        return Reminder;
    }

    public static Note.Audio toAudio(Cursor cursor) {
        Note.Audio audio = new Note.Audio();

        int columnIndex;
        columnIndex = cursor.getColumnIndex(Note.Audio.COLUMN_ID);
        if (columnIndex != -1) {
            audio.mId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Audio.COLUMN_NOTE_ID);
        if (columnIndex != -1) {
            audio.mNoteId = cursor.getLong(columnIndex);
        }
        columnIndex = cursor.getColumnIndex(Note.Audio.COLUMN_URI);
        if (columnIndex != -1) {
            audio.mUri = cursor.getString(columnIndex);
        }

        columnIndex = cursor.getColumnIndex(Note.Audio.COLUMN_DURATION);
        if (columnIndex != -1) {
            audio.mDuration = cursor.getLong(columnIndex);
        }

        return audio;
    }

    public static int deleteImage(Uri uri, String where, String[] selectionArgs,
            ContentResolver resolver) {
        Cursor cursor = resolver.query(uri, new String[] {
                Note.Image.COLUMN_ID, Note.Image.COLUMN_URI
        }, where, selectionArgs, null);
        if (cursor != null) {
            String path;
            File file;
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(Note.Image.COLUMN_URI));
                if (path != null) {
                    file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            cursor.close();

            return resolver.delete(uri, where, selectionArgs);
        }
        return 0;
    }

    public static void deleteNote(long noteId, Context context) {
        Note note = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Note.CONTENT_URI, null, Note.COLUMN_ID + "=" + noteId, null,
                null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                note = NoteUtils.toNote(cursor);
            }
            cursor.close();
        }
        if (note == null) {
            return;
        }
        File file;
        String path;
        if (note.mHasImage == true) {
            cursor = resolver.query(Note.Image.CONTENT_URI, new String[] {
                Note.Image.COLUMN_Content
            }, Note.Image.COLUMN_NOTE_ID + "=" + noteId, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    path = cursor.getString(cursor.getColumnIndex(Note.Image.COLUMN_Content));
                    TctLog.i("NoteUtils","deleteNote  path ========" + path);
                    if(path != null) {
                        AttachmentUtils.deleteAttachment(context,path);
                    }
                }
                cursor.close();
            }
        }
        if (note.mHasAudio == true) {
            cursor = resolver.query(Note.Audio.CONTENT_URI, new String[] {
                Note.Audio.COLUMN_URI
            }, Note.Audio.COLUMN_NOTE_ID + "=" + noteId, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    path = cursor.getString(cursor.getColumnIndex(Note.Audio.COLUMN_URI));
                    if (path != null) {
                        file = new File(path);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                cursor.close();
            }
        }
      //[BUGFIX]-Add-BEGIN by TCTNB.YuTao.Yang,09/26/2013,PR528733,
      //[Notes]The reminder still prompt an alarm after delete it.
        if (note.mHasReminder == true) {
            cancelAlarm(context,noteId);
        }
      //[BUGFIX]-Add-END by TCTNB.YuTao.Yang

        if (note.mThumbnailUri != null) {
            file = new File(note.mThumbnailUri);
            if (file.exists()) {
                file.delete();
            }
        }

        if (note.mSmallThumbnailUri != null) {
            file = new File(note.mSmallThumbnailUri);
            if (file.exists()) {
                file.delete();
            }
        }
        int value = 0;
        Cursor groupcursor = resolver.query(Note.Group.CONTENT_URI, null, Note.Group.COLUMN_ID
                + " = " + note.mGroupId, null, null);
        if (groupcursor != null && groupcursor.getCount() >0) {
            groupcursor.moveToFirst();
            value = groupcursor.getInt(groupcursor.getColumnIndex(Note.Group.COLUMN_NAME)) - 1;
            if (value <0 ) value =0;
            groupcursor.close();
        }

        ContentProviderOperation.Builder builder;
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        builder = ContentProviderOperation.newDelete(Note.Text.CONTENT_URI);
        builder.withSelection(Note.Text.COLUMN_NOTE_ID + "=" + noteId, null);
        operations.add(builder.build());

//        builder = ContentProviderOperation.newDelete(Note.Image.CONTENT_URI);
//        builder.withSelection(Note.Image.COLUMN_NOTE_ID + "=" + noteId, null);
//        operations.add(builder.build());

        builder = ContentProviderOperation.newDelete(Note.Reminder.CONTENT_URI);
        builder.withSelection(Note.Reminder.COLUMN_NOTE_ID + "=" + noteId, null);
        operations.add(builder.build());

        builder = ContentProviderOperation.newDelete(Note.Audio.CONTENT_URI);
        builder.withSelection(Note.Audio.COLUMN_NOTE_ID + "=" + noteId, null);
        operations.add(builder.build());

        builder = ContentProviderOperation.newDelete(Note.CONTENT_URI);
        builder.withSelection(Note.COLUMN_ID + "=" + noteId, null);
        operations.add(builder.build());

        builder = ContentProviderOperation.newUpdate(Note.Group.CONTENT_URI);
        builder.withSelection(Note.Group.COLUMN_ID + "=" + note.mGroupId, null);
        builder.withValue(Note.Group.COLUMN_NAME, value);
        operations.add(builder.build());

        try {
            resolver.applyBatch(NoteProvider.URI_AUTHORITY, operations);
        } catch (Exception e) {
            TctLog.e(TAG, e.toString(), e);
        }
      //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        SharedPreferences widgetNoteMap = context.getSharedPreferences("widget_note_map",
                Context.MODE_PRIVATE);
        long currentNoteId = (long)widgetNoteMap.getLong(String.valueOf(note.WidgetNoteId), -1);
        if (note.WidgetNoteId != -1 && currentNoteId == noteId) {
            widgetNoteMap.edit().remove(String.valueOf(note.WidgetNoteId)).commit();
            Intent widgetIntent = new Intent("com.tct.note.widget.DEL_FROM_APP");
            widgetIntent.setComponent(NoteMiniAppProvider.COMPONENT);
            context.sendBroadcast(widgetIntent);
        }
        
        Intent i = new Intent();
        i.setAction(UpdateWidgetService.DATA_DELETED);
        i.putExtra("note_id", noteId);
        context.sendBroadcast(i);
      //[BUGFIX]-Mod-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
    }
//[BUGFIX]-Add-BEGIN by TCTNB.YuTao.Yang,09/26/2013,PR528733,
//[Notes]The reminder still prompt an alarm after delete it.
    public static  void cancelAlarm(Context context,long NoteId) {
        Intent i = new Intent(context, ReminderReceiver.class);
        i.setData(ContentUris.withAppendedId(Note.CONTENT_URI, NoteId));
        PendingIntent p = PendingIntent.getBroadcast(context, 0, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(p);
    }
//[BUGFIX]-Add-END by TCTNB.YuTao.Yang

    
    //PR833528 Guest_Mode not perform note.Added by hz_nanbing.zou at 17/14/2014 begin
    private static final String GUEST_MODE_FLAG = "persist.security.guestmode";
    //private static final int GUEST_MODE_ON = 1;
    private static final int GUEST_MODE_OFF = 0;
    //public static final String GUEST = "guest";
    /**
     * get guest mode, if mode is off return false, else return true.
     * @return
     */
    public static boolean isGuestModeOn(){
  	  if(GUEST_MODE_OFF == Integer.parseInt(SystemProperties.get(GUEST_MODE_FLAG, "0")))
  	     return false;
  	  return true;
    }
    //PR833528 Guest_Mode not perform note.Added by hz_nanbing.zou at 17/14/2014 end  
    
    public static boolean isMTKPlatform() {
        try {
            String platform = SystemProperties.get("ro.mediatek.platform");
            if (platform.startsWith("MT")) {
                return true;
            }
        } catch (Exception e) {
        	TctLog.i(TAG,"It's not MTK platform");
        }
        return false;
    }

    
    public static boolean isEnglishLan(){
    	
    	String language = Locale.getDefault().getLanguage();
    	TctLog.d("nb", "lang:"+language);
    	if(language.equals("en")){
    			return true;
    		}
    	return false;
    }
    public static boolean isSpaniLan(){
    	
    	String language = Locale.getDefault().getLanguage();
    	TctLog.d("nb", "lang:"+language);
    	if(language.equals("es")){
    			return true;
    		}
    	return false;
    }
       public static boolean isChinaLan(){//For defect360088
    	
    	String language = Locale.getDefault().getLanguage();
    	TctLog.d("nb", "lang:"+language);
    	if(language.equals("zh")){
    			return true;
    		}
    	return false;
    } 
}
