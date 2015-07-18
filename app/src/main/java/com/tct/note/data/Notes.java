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
import android.net.Uri;

public class Notes {

    public final static String TABLE_NAME = "notes";

    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_DEFAULT_SORT = "default_sort";
    public final static String COLUMN_DEFAULT_VIEW = "default_view";
    public final static String COLUMN_DEFAULT_BACKGROUND = "default_background";

    public final static Uri CONTENT_URI = Uri.parse("content://" + NoteProvider.URI_AUTHORITY + "/"
            + TABLE_NAME);

    public long mId;
    public int mDefaultSort;
    public int mDefaultView;
    public String mDefaultBackground;

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        if (mId != 0) {
            values.put(COLUMN_ID, mId);
        }
        values.put(COLUMN_DEFAULT_SORT, mDefaultSort);
        values.put(COLUMN_DEFAULT_VIEW, mDefaultView);
        values.put(COLUMN_DEFAULT_BACKGROUND, mDefaultBackground);
        return values;
    }
}
