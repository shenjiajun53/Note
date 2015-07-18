/******************************************************************************/
/*                                                               Date:11/2014 */
/*                                PRESENTATION                                */
/*                                                                            */
/*       Copyright 2013 TCL Communication Technology Holdings Limited.        */
/*                                                                            */
/* This material is company confidential, cannot be reproduced in any form    */
/* without the written permission of TCL Communication Technology Holdings    */
/* Limited.                                                                   */
/*                                                                            */
/* -------------------------------------------------------------------------- */
/*  Author :  nanbing.zou                                                        */
/*  Email  :  nanbing.zou@tcl.com                                         */
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
/* 13/11/2014|       nanbing.zou    |       PR 836619      |the record and    */
/*           |                      |                      | music play  at   */
/*           |                      |                      |the same time     */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/
//PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
package com.tct.note.util;

import com.tct.note.ui.ReminderActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MusicReceiver extends BroadcastReceiver {

	boolean playing;
	boolean sentpause;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
		if(intent.getAction() == "com.android.music.playstatechanged")
			playing = intent.getBooleanExtra("playing", false);
		TctLog.d("nb","action.."+intent.getAction());
		TctLog.d("nb","isplay.."+playing);
		
		SharedPreferences sh = context.getSharedPreferences("music", Context.MODE_WORLD_READABLE);
		sh.edit().putBoolean("isplaying", playing).commit();
    }

}
//PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end