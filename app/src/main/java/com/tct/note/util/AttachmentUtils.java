/**
 * *****************************************************************************************************************************************************
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
/* 12/15/2014|     feilong.gu       |        872590        | Tap the picture  */  
/*           |                      |                      |in note will dis_ */
/*           |                      |                      |play the hyperlink*/
/*           |                      |                      |to phone          */
/* ----------|----------------------|----------------------|----------------- */

/******************************************************************************/
package com.tct.note.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
//import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.tct.note.Constants;
import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.view.NoteEditView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AttachmentUtils {
    private static final String TAG = AttachmentUtils.class.getSimpleName();

    public static void deleteAttachment(Context paramContext, String paramString) {
        if (!TextUtils.isEmpty(paramString)) {
            deleteAttachmentFile(paramContext, paramString);
            paramContext.getContentResolver().delete(Note.Image.CONTENT_URI, "content=?",
                    new String[]{
                            paramString
                    });
        }
    }

    private static void deleteAttachmentFile(Context paramContext, String paramString) {
        File localFile = new File(getAttachmentPath(paramContext, paramString));
        if ((localFile.exists()) && (localFile.isFile()))
            localFile.delete();
    }

    // public static void deleteIsolatedAttachment(Context paramContext)
    // {
    // AsyncTask.execute(new Runnable()
    // {
    // public void run()
    // {
    // AttachmentUtils.deleteIsolatedAttachmentBlocking(this.val$ctx);
    // }
    // });
    // }

    // public static void deleteIsolatedAttachmentBlocking(Context paramContext)
    // {
    // Cursor localCursor =
    // paramContext.getContentResolver().query(Notes.CONTENT_DATA_URI, new
    // String[] { "_id", "content" }, "note_id<=0", null, null);
    // if (localCursor != null)
    // try
    // {
    // if (localCursor.moveToNext())
    // deleteAttachmentFile(paramContext, localCursor.getString(1));
    // }
    // finally
    // {
    // localCursor.close();
    // }
    // paramContext.getContentResolver().delete(Notes.CONTENT_DATA_URI,
    // "note_id<=0", null);
    // }

    public static final String getAttachmentDir(Context paramContext) {
        Log.e("ty--", "paramContext.getFilesDir().getAbsolutePath(): "
                + paramContext.getFilesDir().getAbsolutePath());
        return paramContext.getFilesDir().getAbsolutePath() + "/Image";
    }

    public static final String getAttachmentPath(Context paramContext, String paramString) {
        return getAttachmentDir(paramContext) + "/" + paramString;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Pair<Long, String> getDataIdAndMimeTypeByAttachmentName(Context paramContext,
                                                                          String paramString) {
        Cursor localCursor = paramContext.getContentResolver().query(Note.Image.CONTENT_URI,
                new String[]{
                        "_id", "mime_type"
                }, "content=?", new String[]{
                        paramString
                }, null);
        Pair localPair = null;
        if (localCursor != null)
            ;
        try {
            if (localCursor.moveToNext()) {
                localPair = new Pair(Long.valueOf(localCursor.getLong(0)), localCursor.getString(1));
                return localPair;
            }
            return null;
        } finally {
            localCursor.close();
        }
    }

    // public static HashMap<String, ImageInfo> getImageInfosByName(Context
    // paramContext, List<String> paramList)
    // {
    // if ((paramList == null) || (paramList.isEmpty()))
    // return null;
    // String str1 = Utils.joinAsString(paramList, "','");
    // String str2 = "content IN ('" + str1 + "')";
    // Cursor localCursor =
    // paramContext.getContentResolver().query(Notes.CONTENT_DATA_URI, new
    // String[] { "_id", "content", "mime_type" }, str2, null, null);
    // if (localCursor != null)
    // {
    // HashMap localHashMap;
    // try
    // {
    // localHashMap = new HashMap(localCursor.getCount());
    // while (localCursor.moveToNext())
    // {
    // long l = localCursor.getLong(0);
    // String str3 = localCursor.getString(1);
    // localHashMap.put(str3, new ImageInfo(l, str3, localCursor.getString(2)));
    // }
    // }
    // finally
    // {
    // localCursor.close();
    // }
    // localCursor.close();
    // return localHashMap;
    // }
    // return null;
    // }

    public static String saveAttachment(Context paramContext, Uri paramUri, String paramString,
                                        long paramLong) {
        long l = System.currentTimeMillis();

        //PR893899.Click image,switch screen,image change to char;Modified by hz_nanbing.zou at 13/01/2015 begin
        //PR872590 modified the code by feilong.gu 2014.12.15 start
        //String str = "AT_" + l+"END";
        String str = "_AT" + l + "END";//modify by mingyue.wang for pr930907
        //PR872590 modified the code by feilong.gu 2014.12.15 start
        //PR893899.Click image,switch screen,image change to char;Modified by hz_nanbing.zou at 13/01/2015 end

        if (NoteUtils.isMTKPlatform()) {
            str = "AT_" + l + "END";
        }
        ContentValues localContentValues = new ContentValues();
        localContentValues.put(Note.Image.COLUMN_URI, paramString);
        localContentValues.put("note_id", Long.valueOf(paramLong));
        localContentValues.put("content", str);
        paramContext.getContentResolver().insert(Note.Image.CONTENT_URI, localContentValues);
        saveAttachmentFile(paramContext, paramUri, str);
        return str;
    }
    //FR789597 modified the code for standalone APK development by bing.wang.hz 2014.09.15 begin

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    //FR789597 modified the code for standalone APK development by bing.wang.hz 2014.09.15 end

    @SuppressWarnings("finally")
    private static boolean saveAttachmentFile(Context paramContext, Uri paramUri, String paramString) {
        File localFile = new File(getAttachmentPath(paramContext, paramString));
        if (!localFile.exists()) {
            new File(localFile.getParent()).mkdirs();
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        InputStream localInputStream = null;
        try {
            localInputStream = paramContext.getContentResolver().openInputStream(paramUri);
            copyToFile(localInputStream, localFile);//FR789597 by bing.wang.hz 2014.09.15
            localFile.setReadable(true, false);
            try {
                if (localInputStream != null)
                    localInputStream.close();
            } catch (IOException localIOException3) {
                Log.w(TAG, "Close attachment uri failed", localIOException3);
            }
        } catch (Exception localException) {
            Log.w(TAG, "Save attachment file failed", localException);
            try {
                if (localInputStream != null)
                    localInputStream.close();
            } catch (IOException localIOException2) {
                Log.w(TAG, "Close attachment uri failed", localIOException2);
            }
        } finally {
//[BUGFIX]-DEL-BEGIN by TCTNB.Yan.TENG,01/02/2014,PR580222
            // Log.e("ty--", "paramUri.getPath()============" + paramUri.getPath());
            //File file = new File(paramUri.getPath());
            //if (file != null && file.exists())   file.delete();
//[BUGFIX]-DEL-END by TCTNB.Yan.TENG
            if (localInputStream == null) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static String saveImage(Context paramContext, Uri paramUri, long paramLong) {
        String str = tryGetImageMimeType(paramContext, paramUri);
        Log.e("ty--", "str============" + str);
        if (!TextUtils.isEmpty(str))
            return saveAttachment(paramContext, paramUri, str, paramLong);
        return null;
    }

    @SuppressWarnings("unused")
    private static String tryGetImageMimeType(Context paramContext, Uri paramUri) {
        String str = null;
        if (paramUri == null)
            return str;
        InputStream localInputStream;
        localInputStream = null;
        try {
            localInputStream = paramContext.getContentResolver().openInputStream(paramUri);
            if (localInputStream != null) {
                BitmapFactory.Options localOptions = new BitmapFactory.Options();
                localOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(localInputStream, null, localOptions);
                str = localOptions.outMimeType;
                if (localInputStream != null)
                    try {
                        localInputStream.close();
                        return str;
                    } catch (IOException localIOException3) {
                        Log.w(TAG, "Can close stream", localIOException3);
                        return str;
                    }
            } else {
                str = null;
                if (localInputStream != null)
                    try {
                        localInputStream.close();
                        return null;
                    } catch (IOException localIOException4) {
                        Log.w(TAG, "Can close stream", localIOException4);
                        return null;
                    }
            }
        } catch (FileNotFoundException localFileNotFoundException) {
            Log.w(TAG, "Can open attachment uri", localFileNotFoundException);
            str = null;
            if (localInputStream != null)
                try {
                    localInputStream.close();
                    return null;
                } catch (IOException localIOException2) {
                    Log.w(TAG, "Can close stream", localIOException2);
                    return null;
                }
        } finally {
            if (localInputStream == null)
                return null;
        }
        return str;
    }

    public static class ImageInfo {
        public final long id;
        public final String imageName;
        public final String mimeType;

        public ImageInfo(long paramLong, String paramString1, String paramString2) {
            this.id = paramLong;
            this.imageName = paramString1;
            this.mimeType = paramString2;
        }
    }


    //ForPR946939\965000 begin
    private static Note.Text mText;
    //    private static NoteEditorView mNoteEditor;//Can not use static for this,it will cause OOM
    private static long noteId;

    public static void setTextInfo(Note.Text text,/*NoteEditView mEdit,*/long id) {
        mText = text;
//    	mNoteEditor = mEdit;

        noteId = id;
    }

    public static void updateAfterDele(Context paramContext, NoteEditView mEdit) {
        mText.mNoteId = noteId;
        mText.mContent = mEdit.getRichText().toString();
        mText.mText = mEdit.getplainText().toString();
        ContentValues values = mText.toContentValues();
        paramContext.getContentResolver().update
                (Note.Text.CONTENT_URI, values, Note.Text.COLUMN_NOTE_ID + " = " + mText.mNoteId, null);
    }
    //ForPR946939\965000 end
    /*
     * get attachs totally size update by xlli
     */
    //for CR1032977/1033004 start
    public static long getAllStorageInfoSum(Context context) {
        long num = 0;
        try {
            long audioLength = FileUtils.getInstance().getDirectorySize(Constants.AUDIO_DIR);//audio size
            long picLength = FileUtils.getInstance().getDirectorySize(context.getFilesDir().getAbsolutePath() + "/Image/");//image size
            num = audioLength + picLength;
        } catch (Exception e) {
            Log.e("StorageUtil", e.toString());
        }
        return num;
    }
    //for CR1032977/1033004 END
}

/*
 * Location: /home/likewise-open/SAGEMWIRELESS/93416/Desktop/classes_dex2jar.jar
 * Qualified Name: com.miui.notes.tool.AttachmentUtils JD-Core Version: 0.6.2
 */
