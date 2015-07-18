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

package com.tct.note.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tct.note.Constants;
import com.tct.note.data.Note.Audio;
import com.tct.note.data.Note.Group;
import com.tct.note.data.Note.Image;
import com.tct.note.data.Note.Reminder;
import com.tct.note.data.Note.Text;
import com.tct.note.util.FileUtils;

import java.io.IOException;

public class NoteDbHelper extends SQLiteOpenHelper {
    public NoteDbHelper(Context context, String name, int version) {

        this(context, name, null, version);
    }

    public NoteDbHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Text.TABLE_NAME + "("
                + Text.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Text.COLUMN_NOTE_ID + " INTEGER,"
                + Text.COLUMN_CONTENT + " TEXT, "
                + Text.COLUMN_TEXT + " TEXT "
                + ")");

        db.execSQL("CREATE TABLE " + Image.TABLE_NAME + "("
                + Image.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Image.COLUMN_NOTE_ID + " INTEGER,"
                + Image.COLUMN_URI + " TEXT,"
                + Image.COLUMN_Content + " TEXT "
                + ")");

        db.execSQL("CREATE TABLE " + Audio.TABLE_NAME + "("
                + Audio.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Audio.COLUMN_NOTE_ID + " INTEGER,"
                + Audio.COLUMN_URI + " TEXT,"
                + Audio.COLUMN_DURATION + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + Reminder.TABLE_NAME + "("
                + Reminder.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Reminder.COLUMN_NOTE_ID + " INTEGER,"
                + Reminder.COLUMN_URI + " TEXT,"
                + Reminder.COLUMN_TIME + " TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + Note.TABLE_NAME + "("
                + Note.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Note.COLUMN_LABEL + " TEXT,"
                + Note.COLUMN_TITLE + " TEXT,"
                + Note.COLUMN_CREATE_TIME + " TEXT,"
                + Note.COLUMN_MODIFY_TIME + " TEXT,"
                + Note.COLUMN_HAS_IMAGE + " INTEGER DEFAULT 0,"
                + Note.COLUMN_HAS_AUDIO + " INTEGER DEFAULT 0,"
                + Note.COLUMN_HAS_REMINDER + " INTEGER DEFAULT 0,"
                + Note.COLUMN_HAS_TEXT + " INTEGER DEFAULT 0,"
                + Note.COLUMN_THUMBNAIL_URI + " TEXT,"
                + Note.COLUMN_SMALL_THUMBNAIL_URI + " TEXT,"
                + Note.COLUMN_BG_IMAGE_RES_ID + " INTEGER DEFAULT "
                + Constants.DEFAULT_NOTE_BG_RES_ID + ","
                + Note.COLUMN_GROUP_ID + " INTEGER DEFAULT 1,"
                + Note.COLUMN_WIDGET_NOTE + " INTEGER DEFAULT 0"
                + ")");

        db.execSQL("CREATE TABLE " + Group.TABLE_NAME + "("
                + Group.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Group.COLUMN_NAME + " INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + Notes.TABLE_NAME + "("
                + Notes.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Notes.COLUMN_DEFAULT_BACKGROUND + "INTEGER,"
                + Notes.COLUMN_DEFAULT_SORT + " INTEGER DEFAULT 0,"
                + Notes.COLUMN_DEFAULT_VIEW + " INTEGER DEFAULT 0"
                + ")");
        for(int i = 0;i<6;i++) {
            ContentValues defaultValues = new ContentValues();
            defaultValues.clear();
            defaultValues.put(Group.COLUMN_NAME, 0);
            db.insert(Group.TABLE_NAME, null, defaultValues);
    }
        //for CR1032977/1033004 start
        //del old audio file
        try {
            FileUtils.getInstance().del(Constants.AUDIO_DIR);
        }catch (IOException e){
            Log.e("NoteDbHelper",e.toString());
        }
        //for CR1032977/1033004 end

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
