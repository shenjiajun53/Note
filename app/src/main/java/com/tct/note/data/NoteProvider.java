/**
 * *****************************************************************************************************************************************************
 * **************************************************************************
 */
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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

import com.tct.note.data.Note.Audio;
import com.tct.note.data.Note.Group;
import com.tct.note.data.Note.Image;
import com.tct.note.data.Note.Reminder;
import com.tct.note.data.Note.Text;
import com.tct.note.util.AttachmentUtils;
import com.tct.note.util.TctLog;


public class NoteProvider extends ContentProvider {
    public static final String DB_NAME = "note.db";

    public static final String URI_AUTHORITY = "com.tct.note";
    private static final int URI_CODE_TEXT = 1;
    private static final int URI_CODE_TEXT_ID = 2;
    private static final int URI_CODE_IMAGE = 3;
    private static final int URI_CODE_IMAGE_ID = 4;
    private static final int URI_CODE_REMINDER = 5;
    private static final int URI_CODE_REMINDER_ID = 6;
    private static final int URI_CODE_AUDIO = 7;
    private static final int URI_CODE_AUDIO_ID = 8;
    private static final int URI_CODE_NOTE = 9;
    private static final int URI_CODE_NOTE_ID = 10;
    private static final int URI_CODE_GROUP = 11;
    private static final int URI_CODE_GROUP_ID = 12;
    private static final int URI_CODE_NOTES = 13;
    private static final int URI_CODE_NOTES_ID = 14;
    private static final int URI_SEARCH_BY_KEY_ID = 15;

    private static final int URI_ATTACH_LEN_ID = 16;//search attachs size that contain pic sizes and audio size for CR1032977/1033004

    public static final String URI_MIME_TEXT = "vnd.android.cursor.dir/vnd.note.text";
    public static final String URI_ITEM_MIME_TEXT = "vnd.android.cursor.item/vnd.note.text";
    public static final String URI_MIME_IMAGE = "vnd.android.cursor.dir/vnd.note.image";
    public static final String URI_ITEM_MIME_IMAGE = "vnd.android.cursor.item/vnd.note.image";
    public static final String URI_MIME_REMINDER = "vnd.android.cursor.dir/vnd.note.reminder";
    public static final String URI_ITEM_MIME_REMINDER = "vnd.android.cursor.item/vnd.note.reminder";
    public static final String URI_MIME_AUDIO = "vnd.android.cursor.dir/vnd.note.audio";
    public static final String URI_ITEM_MIME_AUDIO = "vnd.android.cursor.item/vnd.note.audio";
    public static final String URI_MIME_NOTE = "vnd.android.cursor.dir/vnd.note.note";
    public static final String URI_ITEM_MIME_NOTE = "vnd.android.cursor.item/vnd.note.note";
    public static final String URI_MIME_GROUP = "vnd.android.cursor.dir/vnd.note.group";
    public static final String URI_ITEM_MIME_GROUP = "vnd.android.cursor.item/vnd.note.group";
    public static final String URI_MIME_NOTES = "vnd.android.cursor.dir/vnd.note.notes";
    public static final String URI_ITEM_MIME_NOTES = "vnd.android.cursor.item/vnd.note.notes";
    public static final String URI_MIME_NOTE_SEARCH = "vnd.android.cursor.dir/vnd.note.note_temp";

    public static final String URI_MIME_ATTACH_LEN = "vnd.android.cursor.dir/vnd.note.len";//for CR1032977/1033004

    private static final String TAG = NoteProvider.class.getSimpleName();

    private static UriMatcher mUriMatcher;
    private NoteDbHelper mNoteDbHelper;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(URI_AUTHORITY, Text.TABLE_NAME, URI_CODE_TEXT);
        mUriMatcher.addURI(URI_AUTHORITY, Text.TABLE_NAME + "/#", URI_CODE_TEXT_ID);
        mUriMatcher.addURI(URI_AUTHORITY, Image.TABLE_NAME, URI_CODE_IMAGE);
        mUriMatcher.addURI(URI_AUTHORITY, Image.TABLE_NAME + "/#", URI_CODE_IMAGE_ID);
        mUriMatcher.addURI(URI_AUTHORITY, Reminder.TABLE_NAME, URI_CODE_REMINDER);
        mUriMatcher.addURI(URI_AUTHORITY, Reminder.TABLE_NAME + "/#", URI_CODE_REMINDER_ID);
        mUriMatcher.addURI(URI_AUTHORITY, Audio.TABLE_NAME, URI_CODE_AUDIO);
        mUriMatcher.addURI(URI_AUTHORITY, Audio.TABLE_NAME + "/#", URI_CODE_AUDIO_ID);
        mUriMatcher.addURI(URI_AUTHORITY, Group.TABLE_NAME, URI_CODE_GROUP);
        mUriMatcher.addURI(URI_AUTHORITY, Group.TABLE_NAME + "/#", URI_CODE_GROUP_ID);
        mUriMatcher.addURI(URI_AUTHORITY, Note.TABLE_NAME, URI_CODE_NOTE);
        mUriMatcher.addURI(URI_AUTHORITY, Note.TABLE_NAME + "/#", URI_CODE_NOTE_ID);
        //for CR1032977/1033004 start
        mUriMatcher.addURI(URI_AUTHORITY, Note.ATTACH_LEN, URI_ATTACH_LEN_ID);
        //for CR1032977/1033004 end
        mUriMatcher.addURI(URI_AUTHORITY, Notes.TABLE_NAME, URI_CODE_NOTES);
        mUriMatcher.addURI(URI_AUTHORITY, Notes.TABLE_NAME + "/#", URI_CODE_NOTES_ID);
        mUriMatcher.addURI(URI_AUTHORITY, Note.TABLE_NAME_SEARCH, URI_SEARCH_BY_KEY_ID);
    }

    @Override
    public boolean onCreate() {
        mNoteDbHelper = new NoteDbHelper(getContext(), DB_NAME, 1);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case URI_CODE_TEXT:
                return URI_MIME_TEXT;
            case URI_CODE_TEXT_ID:
                return URI_ITEM_MIME_TEXT;
            case URI_CODE_IMAGE:
                return URI_MIME_IMAGE;
            case URI_CODE_IMAGE_ID:
                return URI_ITEM_MIME_IMAGE;
            case URI_CODE_REMINDER:
                return URI_MIME_REMINDER;
            case URI_CODE_REMINDER_ID:
                return URI_ITEM_MIME_REMINDER;
            case URI_CODE_AUDIO:
                return URI_MIME_AUDIO;
            case URI_CODE_AUDIO_ID:
                return URI_ITEM_MIME_AUDIO;
            case URI_CODE_NOTE:
                return URI_MIME_NOTE;
            case URI_CODE_NOTE_ID:
                return URI_ITEM_MIME_NOTE;
            case URI_CODE_GROUP:
                return URI_MIME_GROUP;
            case URI_CODE_GROUP_ID:
                return URI_ITEM_MIME_GROUP;
            case URI_CODE_NOTES:
                return URI_MIME_NOTES;
            case URI_CODE_NOTES_ID:
                return URI_ITEM_MIME_NOTES;
            case URI_SEARCH_BY_KEY_ID:
                return URI_MIME_NOTE_SEARCH;
            case URI_ATTACH_LEN_ID:
                return URI_MIME_ATTACH_LEN;//attach's size for CR1032977/1033004
            default:
                TctLog.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId;
        Uri rowUri = null;
        SQLiteDatabase db = mNoteDbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case URI_CODE_TEXT:
                rowId = db.insert(Text.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Text.CONTENT_URI, rowId);
                }
                break;
            case URI_CODE_IMAGE:
                rowId = db.insert(Image.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Image.CONTENT_URI, rowId);
                }
                break;
            case URI_CODE_REMINDER:
                rowId = db.insert(Reminder.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Reminder.CONTENT_URI, rowId);
                }
                break;
            case URI_CODE_AUDIO:
                rowId = db.insert(Audio.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Audio.CONTENT_URI, rowId);
                }
                break;
            case URI_CODE_NOTE:
                rowId = db.insert(Note.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Note.CONTENT_URI, rowId);
                }
                break;

            case URI_CODE_GROUP:
                rowId = db.insert(Group.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Group.CONTENT_URI, rowId);
                }
                break;

            case URI_CODE_NOTES:
                rowId = db.insert(Notes.TABLE_NAME, null, values);
                if (rowId != -1) {
                    rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
                }
                break;

        }
        db.close();
        return rowUri;
    }

    //For PR984232 begin
    private static SQLiteDatabase deldb;
    private static SQLiteDatabase updatedb;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = mNoteDbHelper.getWritableDatabase();
        deldb = db;
        switch (mUriMatcher.match(uri)) {
            case URI_CODE_TEXT:
                count = db.delete(Text.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_TEXT_ID:
                count = db.delete(Text.TABLE_NAME, Text.COLUMN_ID + "=" + uri.getLastPathSegment()
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;

            case URI_CODE_IMAGE:
                count = db.delete(Image.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_IMAGE_ID:
                count = db
                        .delete(Image.TABLE_NAME,
                                Image.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_REMINDER:
                count = db.delete(Reminder.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_REMINDER_ID:
                count = db
                        .delete(Reminder.TABLE_NAME,
                                Reminder.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_AUDIO:
                count = db.delete(Audio.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_AUDIO_ID:
                count = db
                        .delete(Audio.TABLE_NAME,
                                Audio.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_NOTE:
                count = db.delete(Note.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_NOTE_ID:
                count = db.delete(Note.TABLE_NAME, Note.COLUMN_ID + "=" + uri.getLastPathSegment()
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;

            case URI_CODE_GROUP:
                count = db.delete(Group.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_GROUP_ID:
                count = db
                        .delete(Group.TABLE_NAME,
                                Group.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_NOTES:
                count = db.delete(Notes.TABLE_NAME, selection, selectionArgs);
                break;
            case URI_CODE_NOTES_ID:
                count = db
                        .delete(Notes.TABLE_NAME,
                                Notes.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            default:
                TctLog.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
//        db.close();
        return count;
    }


    public static void closeDeldb() {
        if (deldb != null)
            deldb.close();
    }

    public static void closeupdatedb() {
        if (updatedb != null)
            updatedb.close();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mNoteDbHelper.getReadableDatabase();
        updatedb = db;
        int count;
        switch (mUriMatcher.match(uri)) {
            case URI_CODE_TEXT:
                count = db.update(Text.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_TEXT_ID:
                count = db
                        .update(Text.TABLE_NAME,
                                values,
                                Text.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_IMAGE:
                count = db.update(Image.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_IMAGE_ID:
                count = db
                        .update(Image.TABLE_NAME,
                                values,
                                Image.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_REMINDER:
                count = db.update(Reminder.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_REMINDER_ID:
                count = db
                        .update(Reminder.TABLE_NAME,
                                values,
                                Reminder.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_AUDIO:
                count = db.update(Audio.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_AUDIO_ID:
                count = db
                        .update(Audio.TABLE_NAME,
                                values,
                                Audio.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_NOTE:
                count = db.update(Note.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_NOTE_ID:
                count = db
                        .update(Note.TABLE_NAME,
                                values,
                                Note.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_GROUP:
                count = db.update(Group.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_GROUP_ID:
                count = db
                        .update(Group.TABLE_NAME,
                                values,
                                Group.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;

            case URI_CODE_NOTES:
                count = db.update(Notes.TABLE_NAME, values, selection, selectionArgs);
                break;
            case URI_CODE_NOTES_ID:
                count = db
                        .update(Notes.TABLE_NAME,
                                values,
                                Notes.COLUMN_ID
                                        + "="
                                        + uri.getLastPathSegment()
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                        + ")" : ""), selectionArgs);
                break;
            default:
                TctLog.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
//        db.close();
        return count;
    }
    //For PR984232 end

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor = null;
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        switch (mUriMatcher.match(uri)) {
            //for CR1032977/1033004 start
            case URI_ATTACH_LEN_ID:
                //search how many size in attachment that contain pic and audio
                String[] lenCursor = new String[]{"attachlen"};
                MatrixCursor mcursor = new MatrixCursor(lenCursor);
                mcursor.addRow(new Long[]{AttachmentUtils.getAllStorageInfoSum(getContext().getApplicationContext())});
                return mcursor;
            //for CR1032977/1033004 end
            case URI_CODE_TEXT:
                sqlBuilder.setTables(Text.TABLE_NAME);
                break;
            case URI_CODE_TEXT_ID:
                sqlBuilder.setTables(Text.TABLE_NAME);
                sqlBuilder.appendWhere(Text.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case URI_CODE_IMAGE:
                sqlBuilder.setTables(Image.TABLE_NAME);
                break;
            case URI_CODE_IMAGE_ID:
                sqlBuilder.setTables(Image.TABLE_NAME);
                sqlBuilder.appendWhere(Image.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case URI_CODE_REMINDER:
                sqlBuilder.setTables(Reminder.TABLE_NAME);
                break;
            case URI_CODE_REMINDER_ID:
                sqlBuilder.setTables(Reminder.TABLE_NAME);
                sqlBuilder.appendWhere(Reminder.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case URI_CODE_AUDIO:
                sqlBuilder.setTables(Audio.TABLE_NAME);
                break;
            case URI_CODE_AUDIO_ID:
                sqlBuilder.setTables(Audio.TABLE_NAME);
                sqlBuilder.appendWhere(Audio.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case URI_CODE_NOTE:
                sqlBuilder.setTables(Note.TABLE_NAME);
                break;
            case URI_CODE_NOTE_ID:
                sqlBuilder.setTables(Note.TABLE_NAME);
                sqlBuilder.appendWhere(Note.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case URI_CODE_GROUP:
                sqlBuilder.setTables(Group.TABLE_NAME);
                break;
            case URI_CODE_GROUP_ID:
                sqlBuilder.setTables(Group.TABLE_NAME);
                sqlBuilder.appendWhere(Group.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;

            case URI_CODE_NOTES:
                sqlBuilder.setTables(Notes.TABLE_NAME);
                break;
            case URI_CODE_NOTES_ID:
                sqlBuilder.setTables(Notes.TABLE_NAME);
                sqlBuilder.appendWhere(Notes.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case URI_SEARCH_BY_KEY_ID:
                final SQLiteDatabase db = mNoteDbHelper.getWritableDatabase();
                cursor = db.rawQuery(selection, selectionArgs);
                return cursor;
            default:
                TctLog.e(TAG, "Unknown URI:" + uri);
                throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        cursor = sqlBuilder.query(mNoteDbHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;
    }

}
