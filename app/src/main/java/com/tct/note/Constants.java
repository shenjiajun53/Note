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

import java.io.File;

import android.os.Environment;

import com.tct.note.R;

public class Constants {
    public static final String EXTRA_NOTE_ID = "note_id";
    public static final String EXTRA_RES_ID = "res_id";
    public static final String EXTRA_NOTE_INDEX = "note_index";
    public static final String EXTRA_NOTE_AMOUNT = "note_amount";
    public static final String EXTRA_NOTE_GROUP_ID = "note_group_id";
    public static final String EXTRA_NOTE_SORT_ORDER = "sort_order";
    public static final String EXTRA_NOTE_GROUP_IDS= "group_ids";

    public static final String SHARED_PREFS_NAME = "com.tct.note";
    public static final String KEY_NOTE_BG_RES_ID = "note_bg_res_id";

    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang,2015-1-30,PR917795
    public static final int NOTE_BG_White_ID=1;
    public static final int NOTE_BG_Blue_ID=2;
    public static final int NOTE_BG_Green_ID=3;
    public static final int NOTE_BG_Yellow_ID=4;
    public static final int NOTE_BG_Red_ID=5;
    public static final int NOTE_BG_Purple_ID=6;
    public static final int DEFAULT_NOTE_BG_RES_ID = NOTE_BG_White_ID;
    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang,2015-1-30,PR917795
 
    public static final File THUMBNAIL_DIR = new File(
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/.note/Thumbnail");

  //[BUGFIX]-ADD-BEGIN by TCTNB.Yan.TENG,01/02/2014,PR580222
    public static final File IMAGE_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
            //Environment.getExternalStorageDirectory().getAbsolutePath() + "/.note/Image");
  //[BUGFIX]-ADD-END by TCTNB.Yan.TENG

   // public static final File PAINT_DIR = new File(
         //   Environment.getExternalStorageDirectory().getAbsolutePath() + "/.note/Reminder");

    public static final File AUDIO_DIR = new File(
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/.note/Audio");

    public static final File GALLERY_DIR = new File(
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures");
}
