/***********************************************************************************************/
/*                                                           Date : 05/2011 */
/*                            PRESENTATION                                  */
/*              Copyright (c) 2010 JRD Communications, Inc.                 */
/***********************************************************************************************/
/*                                                                                             */
/*    This material is company confidential, cannot be reproduced in any    */
/*    form without the written permission of JRD Communications, Inc.       */
/*                                                                                             */
/*=============================================================================================*/
/*   Author :  min.zhang                                                    */
/*   Role :   Provides access to a database of notes.                       */
/*   Reference documents : android 2.3 help document                        */
/*=============================================================================================*/
/* Comments :                                                               */
/*     file    : ../com/jrdcom/notepad2/ui/appwidget/IDefine.java        */
/*     Labels  :                                                            */
/*=============================================================================================*/
/* Modifications   (month/day/year)                                         */
/*=============================================================================================*/
/*=============================================================================================*/
/* date    | author         |FeatureID                |modification         */
/*=========|==============|=========================|==========================================*/
/*2011-09-15 | minzhang.zhang |FR-150611    |FR-150611-min-zhang-001               */
/*=============================================================================================*/
/* Problems Report(PR/CR)                                                   */
/*=============================================================================================*/
/* date    | author    | PR #     |                                         */
/*=========|===========|==========|============================================================*/
/*10/07/11 |Chuan Cheng|  PR-171274-CHUAN-CHENG-001     |Add time change NOTE_REFRESH_WHAT type*/
/*=============================================================================================*/
package com.tct.note.widget;

import android.net.Uri;

public interface IDefine {
	public static final String NOTE_PREV="com.jrd.notepad.PREV";
	public static final String NOTE_ADD="com.jrd.notepad.ADD";
	public static final String NOTE_DELETE="com.jrd.notepad.DELETE";
	public static final String NOTE_NEXT="com.jrd.notepad.NEXT";
	public static final String NOTE_UPDATE="com.jrd.notepad.UPDATE";
	public static final String NOTE_VIEW="com.jrd.notepad.VIEW";//add by lijun.ye for PR396044
	public static final int LISTVIEWWIDTH=320;
	
    //public static final String SUFFIX = ".txt";PR212418-Chuan Cheng -001
	
	
	public static final int NOTE_PREV_WHAT=0;
	public static final int NOTE_ADD_WHAT=NOTE_PREV_WHAT+1;
	public static final int NOTE_DELETE_WHAT=NOTE_ADD_WHAT+1;
	public static final int NOTE_NEXT_WHAT=NOTE_DELETE_WHAT+1;
	public static final int NOTE_UPDATE_WHAT=NOTE_NEXT_WHAT+1;
	public static final int NOTE_REFRESH_WHAT=NOTE_UPDATE_WHAT+1;//PR-171274-CHUAN-CHENG-001
	public static final int NOTE_VIEW_WHAT=NOTE_REFRESH_WHAT+1;//add by lijun.ye for PR396044
	public static final int NOTE_PROGRESSBAR=NOTE_VIEW_WHAT+1;//add by Johnson.Yu for PR611733
	public static final int NOTE_NOT_WIDGET_VIEW_UPDATE=NOTE_PROGRESSBAR+1; /* PR635003- Neo Skunkworks - Tony - 001 */

	public static final String CURRENT_SHAREFERENCE_PREFERENCE="current_index";//add by lijun.ye for PR396044 2013-01-26
	public static final String DB_CLEAR_FLAG="db_clear_flag";//PR659335- Neo Skunkworks - Tony - 001 add
	
	public static final String CURRENT_INDEX="currentIndex";//add by lijun.ye for PR396044 2013-01-26
	//package name!
//	public static final String PACKAGENAME="com.jrdcom.ui.notes";
	public static final String PACKAGENAME="com.tct.note";
	
//	public static final String APPWIDGETNAME="com.jrdcom.notepad2.ui.appwidget.NotePadWidgetProvider";
	public static final String APPWIDGETNAME="com.tct.note.ui.appwidget.NotePadWidgetProvider";

	//content provider attribute!~
	public static final String AUTHORITY = "com.android.email.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/account");

}