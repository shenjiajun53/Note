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


import android.content.ContentValues;
import android.net.Uri;

//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
import com.tct.note.Constants;
import com.tct.note.util.TctLog;

public class Note {
    public final static String TABLE_NAME = "note";
    public final static String TABLE_NAME_SEARCH = "note_temp";

    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_GROUP_ID = "group_id";
    public final static String COLUMN_TITLE = "title";
    public final static String COLUMN_LABEL = "label";
    public final static String COLUMN_HAS_TEXT = "has_text";
    public final static String COLUMN_HAS_IMAGE = "has_image";
    public final static String COLUMN_HAS_REMINDER = "has_reminder";
    public final static String COLUMN_HAS_AUDIO = "has_audio";
    public final static String COLUMN_THUMBNAIL_URI = "thumbnail_uri";
    public final static String COLUMN_SMALL_THUMBNAIL_URI = "small_thumbnail_uri";
    public final static String COLUMN_BG_IMAGE_RES_ID = "bg_image_res_id";
    public final static String COLUMN_CREATE_TIME = "create_time";
    public final static String COLUMN_MODIFY_TIME = "modify_time";
    public final static String COLUMN_WIDGET_NOTE = "widget_note_id";//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,

    public final static String ATTACH_LEN="attach_len";//get attach‘s totalsize for CR1032977/1033004

    public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY + "/"
            + TABLE_NAME);
    public final static Uri CONTENT_URI_SEARCH = Uri.parse("content://"
            + NoteProvider.URI_AUTHORITY + "/" + TABLE_NAME_SEARCH);
    //for CR1032977/1033004 start
    //for test uses, get attach's totalsize
    public final static Uri ATTACH_LEN_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY + "/"
            + ATTACH_LEN);
    //for CR1032977/1033004 end

    public long mId;
    public long mGroupId = 1;
    public String mTitle;
    public String mLabel;
    public boolean mHasText;
    public boolean mHasImage;
    public boolean mHasReminder;
    public boolean mHasAudio;
    public String mThumbnailUri;
    public String mSmallThumbnailUri;
    public int mBgImageResId = Constants.DEFAULT_NOTE_BG_RES_ID;
    public long mCreateTime = System.currentTimeMillis();
    public long mModifyTime = System.currentTimeMillis();
    public long WidgetNoteId = -1;//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
    public static String TAG = "Note";

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (mId != 0) {
            values.put(COLUMN_ID, mId);
        }
        values.put(COLUMN_GROUP_ID, mGroupId);
        values.put(COLUMN_TITLE, mTitle);
        values.put(COLUMN_LABEL, mLabel);
        values.put(COLUMN_HAS_TEXT, mHasText);
        values.put(COLUMN_HAS_IMAGE, mHasImage);
        values.put(COLUMN_HAS_REMINDER, mHasReminder);
        values.put(COLUMN_HAS_AUDIO, mHasAudio);
        values.put(COLUMN_THUMBNAIL_URI, mThumbnailUri);
        values.put(COLUMN_SMALL_THUMBNAIL_URI, mSmallThumbnailUri);
        values.put(COLUMN_WIDGET_NOTE, WidgetNoteId);//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        if (mBgImageResId != 0) {
            values.put(COLUMN_BG_IMAGE_RES_ID, mBgImageResId);
        }
        values.put(COLUMN_CREATE_TIME, mCreateTime);
        values.put(COLUMN_MODIFY_TIME, mModifyTime);
        return values;
    }

    public void toPrintLog() {
        TctLog.e(TAG, "dosaveNote mNote.mHasImage: " + (mHasImage));
        TctLog.e(TAG, "dosaveNote mNote.mHasText: " + (mHasText));
        TctLog.e(TAG, "dosaveNote mHasAudio: " + mHasAudio);
        TctLog.e(TAG, "dosaveNote mNote.mHasReminder: " + mHasReminder);
    }

    public static class Text {
        public final static String TABLE_NAME = "note_text";

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NOTE_ID = "note_id";
        public final static String COLUMN_CONTENT = "content";
        public final static String COLUMN_TEXT = "text";

        public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY
                + "/" + TABLE_NAME);

        public long mId;
        public long mNoteId;
        public String mContent;
        public String mText;
        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            if (mId != 0) {
                values.put(COLUMN_ID, mId);
            }
            values.put(COLUMN_NOTE_ID, mNoteId);
            values.put(COLUMN_CONTENT, mContent);
            values.put(COLUMN_TEXT, mText);
            return values;
        }
    }

    public static class Image {
        public final static String TABLE_NAME = "note_image";

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NOTE_ID = "note_id";
        public final static String COLUMN_URI = "mime_type";
        public final static String COLUMN_Content = "content";
        public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY
                + "/" + TABLE_NAME);

        public long mId;
        public long mNoteId;
        public String mUri;
        public String mContent;


        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            if (mId != 0) {
                values.put(COLUMN_ID, mId);
            }
            values.put(COLUMN_NOTE_ID, mNoteId);
            values.put(COLUMN_URI, mUri);
            values.put(COLUMN_Content, mContent);
            return values;
        }
    }

    public static class Reminder {
        public final static String TABLE_NAME = "note_reminder";

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NOTE_ID = "note_id";
        public final static String COLUMN_URI = "ringtone_uri";
        public final static String COLUMN_TIME = "reminder_time";

        public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY
                + "/" + TABLE_NAME);

        public long mId;
        public long mNoteId;
        public String mUri;
        public int mLayer;
        public long mReminderTime = -1;

        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            if (mId != 0) {
                values.put(COLUMN_ID, mId);
            }
            values.put(COLUMN_NOTE_ID, mNoteId);
            values.put(COLUMN_URI, mUri);
            values.put(COLUMN_TIME, mReminderTime);
            return values;
        }
    }

    public static class Audio {
        public final static String TABLE_NAME = "note_audio";

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NOTE_ID = "note_id";
        public final static String COLUMN_URI = "uri";
        public final static String COLUMN_DURATION = "duration";

        public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY
                + "/" + TABLE_NAME);

        public long mId;
        public long mNoteId;
        public String mUri;
        public long mDuration = System.currentTimeMillis();

        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            if (mId != 0) {
                values.put(COLUMN_ID, mId);
            }
            values.put(COLUMN_NOTE_ID, mNoteId);
            values.put(COLUMN_URI, mUri);
            values.put(COLUMN_DURATION, mDuration);
            return values;
        }
    }

    public static class Group {
        public final static String TABLE_NAME = "note_group";

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NAME = "count";
        public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY
                + "/" + TABLE_NAME);

        public long mId;
        public long mName;

        public ContentValues toContentValues() {
            ContentValues values = new ContentValues();
            if (mId != 0) {
                values.put(COLUMN_ID, mId);
            }
            values.put(COLUMN_NAME, mName);
            return values;
        }
    }
}
