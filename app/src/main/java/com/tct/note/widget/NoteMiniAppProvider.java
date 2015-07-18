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
/* 03/11/2015|      Gu Feilong      |      PR-945637       |ANR in com.tct.   */
/*           |                      |                      |note              */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tct.note.Constants;
import com.tct.note.MediaPlaybackService;
import com.tct.note.SystemProperties;
import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.data.Note.Group;
import com.tct.note.ui.NotesListActivity;
import com.tct.note.util.TctLog;
import com.tct.note.R;

public class NoteMiniAppProvider extends AppWidgetProvider {

    public static final ComponentName COMPONENT = new ComponentName("com.tct.note",
            "com.tct.note.widget.NoteMiniAppProvider");
	
//	public static final ComponentName COMPONENT = new ComponentName("com.jrdcom.note",
//            "com.jrdcom.note.widget.NoteAppWidgetProvider");
    
    
    
    private static final String TAG = "NoteMiniAppProvider";
    public long currentNoteId;//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
    private RemoteViews views;
    private static boolean audioState;

    private boolean isNew = false;
    
    private Context mContext;
    private Cursor total_cursor;
    private boolean flag = false;
    private int position =0;
    private SharedPreferences widgetNoteMap;//add by mingyue.wang for PR880846
    
    private final static int ONLY_HAS_IMAGE = 1;
    private final static int ONLY_HAS_AUDIO = 2;
    private final static int ONLY_HAS_REMINDER = 3;
    private final static int HAS_IMAGE_AUDIO = 4;
    private final static int HAS_IMAGE_REMINDER = 5;
    private final static int HAS_AUDIO_REMINDER = 6;
    private final static int HAS_ALL = 7;
    private final static int HAS_NOTHING = 0;  
    
    
    private boolean isTimeSet = false;//For PR969764/PR966365
    
    static ComponentName getComponentName(Context context) {
        return new ComponentName(context, NoteMiniAppProvider.class);
    }

    public void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    	mContext = context;
//    	getTotal();

        int count = appWidgetIds.length;
      //[BUGFIX]-Add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        TctLog.i(TAG, "count ===" + count);
        //modify by mingyue.wang for PR880846 begin
        if(widgetNoteMap==null){
           widgetNoteMap= context.getSharedPreferences("widget_note_map",Context.MODE_PRIVATE);
        }
       //modify by mingyue.wang for PR880846 begin
      //[BUGFIX]-add-End by TSCD(yanmei.zhang),06/09/2014,PR692135,
        for (int i = 0; i < count; i++) {
            views = new RemoteViews(context.getPackageName(), R.layout.widget_mini2);
            //[BUGFIX]-add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,

            
            
            views.setOnClickPendingIntent(R.id.miniEnter, getMainIntent(context,appWidgetIds[i]));  
            // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 begin
            currentNoteId = (long)widgetNoteMap.getLong("widget", -1);
            // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 end

            
            TctLog.d("nb","ID.."+currentNoteId);
            
            TctLog.i(TAG, "onUpdate  currentNoteId===" + currentNoteId + "   appWidgetIds  ===" + appWidgetIds[i]);
            //[BUGFIX]-add-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
            Cursor noteCursor = null;
            
            SharedPreferences widgetDel = context.getSharedPreferences("delete_widget",Context.MODE_PRIVATE);
            boolean isdelete = widgetDel.getBoolean("isdelete", false);
            if(!isdelete){//If is delete now,should not update,after delete will update
 /*           	if(!isTimeSet)////For PR969764/PR966365
            		deleteEmptyNote();//For pr960523
            	else
            		isTimeSet = false;*/
            	
            noteCursor = getCursor(context);
            }else{
            	return;
            }
            if(noteCursor!=null&& noteCursor.getCount() > 0){
            	noteCursor.moveToFirst();
            Note mNote = NoteUtils.toNote(noteCursor);
            int AttachmentFlag = IsOnlyOneAttachment(mNote.mHasImage, mNote.mHasAudio,
                    mNote.mHasReminder);
            ConfirmDisplayPlace(AttachmentFlag, views);
            }
            
            if (noteCursor == null || (noteCursor != null && noteCursor.getCount() <= 0) || NoteUtils.isGuestModeOn()) {
            	setEmptyUI();
            	views.setOnClickPendingIntent(R.id.add, getAddIntent(context,appWidgetIds[i]));
            	
                if(NoteUtils.isGuestModeOn()){//For defect231806
                	Toast.makeText(context, R.string.guest_mode_view, Toast.LENGTH_SHORT).show();
                }
            	
            	/*
                views.setViewVisibility(R.id.empty_layout, View.VISIBLE);
                views.setViewVisibility(R.id.empty_fill, View.VISIBLE);
                views.setViewVisibility(R.id.note_empty2, View.VISIBLE);
                views.setViewVisibility(R.id.miniadd, View.VISIBLE);
                views.setViewVisibility(R.id.miniemptyEnter, View.VISIBLE);
                views.setViewVisibility(R.id.all_line3, View.VISIBLE);
                views.setViewVisibility(R.id.all_line4, View.VISIBLE);
                
                //PR932945.No note should dispaly.Added by hz_nanbing.zou at 2015/02/14 begin
                views.setViewVisibility(R.id.widget_no_note, View.VISIBLE);
                //PR932945.No note should dispaly.Added by hz_nanbing.zou at 2015/02/14 end
                
//                views.setTextViewText(R.id.note_empty2,
//                        context.getResources().getString(R.string.widget_note_empty));

                setUI_0();
                
                views.setViewVisibility(R.id.widget_header, View.GONE);
                views.setOnClickPendingIntent(R.id.miniadd, getAddIntent(context,appWidgetIds[i]));
//                views.setViewVisibility(R.id.widget_audio, View.GONE);
                views.setViewVisibility(R.id.all_line, View.GONE);
                views.setViewVisibility(R.id.all_line2, View.GONE);
                views.setViewVisibility(R.id.note_list, View.GONE);
                views.setOnClickPendingIntent(R.id.miniemptyEnter, getMainIntent(context,appWidgetIds[i]));
                views.setOnClickPendingIntent(R.id.note_empty2, getAddIntent(context,appWidgetIds[i]));  //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
            */} else {
                noteCursor.moveToFirst();
                views.setViewVisibility(R.id.note_empty2, View.GONE);
                views.setViewVisibility(R.id.miniadd, View.GONE);
                views.setViewVisibility(R.id.miniemptyEnter, View.GONE);
                views.setViewVisibility(R.id.empty_layout, View.GONE);
                views.setViewVisibility(R.id.empty_fill, View.GONE);
                views.setViewVisibility(R.id.all_line3, View.GONE);
                views.setViewVisibility(R.id.all_line4, View.GONE);
                //PR932945.No note should dispaly.Added by hz_nanbing.zou at 2015/02/14 begin
                views.setViewVisibility(R.id.widget_no_note, View.GONE);
                //PR932945.No note should dispaly.Added by hz_nanbing.zou at 2015/02/14 end
                
                
                views.setViewVisibility(R.id.widget_header, View.VISIBLE);
                views.setViewVisibility(R.id.all_line, View.VISIBLE);
                views.setViewVisibility(R.id.all_line2, View.VISIBLE);
                views.setViewVisibility(R.id.note_list, View.VISIBLE);
                views.setViewVisibility(R.id.add, View.VISIBLE);

                setUI_1(noteCursor,appWidgetIds[i]);

                String text = getText(context);
                if( text != null && !"".equalsIgnoreCase(text)) {
                views.setViewVisibility(R.id.text_content, View.VISIBLE);
                views.setTextViewText(R.id.text_content, text);
                } else {
                    views.setViewVisibility(R.id.text_content, View.GONE);
                }

//                String imageUri = getImagePath(context);
//                if (imageUri != null ) {
//                    views.setViewVisibility(R.id.image_content, View.VISIBLE);
//                DisplayMetrics localDisplayMetrics =context.getResources().getDisplayMetrics();
//                Bitmap bmp = resizeImageAttachment((int) (0.7F * localDisplayMetrics.widthPixels),
//                        (int) (0.6F * localDisplayMetrics.heightPixels),imageUri,(NinePatchDrawable) context.getResources().getDrawable(
//                        R.drawable.actionbar));
//                views.setImageViewBitmap(R.id.image_content, bmp);
//                } else {
//                    views.setViewVisibility(R.id.image_content, View.GONE);
//                }

//                views.setImageViewResource(R.id.add, R.drawable.widget_create_new);
                views.setOnClickPendingIntent(R.id.widget_content, getViewIntent(context,appWidgetIds[i]));          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
                views.setOnClickPendingIntent(R.id.add, getAddIntent(context,appWidgetIds[i]));          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
//                boolean hasAudio = noteCursor.getInt(noteCursor
//                        .getColumnIndex(Note.COLUMN_HAS_AUDIO)) == 1;
//                if (hasAudio) {
//                    views.setViewVisibility(R.id.widget_audio, View.VISIBLE);
//                    views.setImageViewResource(R.id.widget_audio, R.drawable.audio_play);
//                    views.setOnClickPendingIntent(R.id.widget_audio, getPlayAudiointent(context,appWidgetIds[i]));          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
//                } else {
//                    views.setViewVisibility(R.id.widget_audio, View.GONE);
//                }
//                if (audioState) {
//                    views.setImageViewResource(R.id.widget_audio, R.drawable.audio_pause);
//                } else {
//                    views.setImageViewResource(R.id.widget_audio, R.drawable.audio_play);
//                }
            }
            if (noteCursor != null)
                noteCursor.close();

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }
    public String getText(Context context){
        String contentString = null;
        Cursor c = context.getContentResolver().query(
                Uri.parse("content://com.tct.note/note_text"), null, "note_id =" +currentNoteId, null,
                null);
        if (c != null && c.getCount() > 0) {
            c.moveToLast();
            contentString = c.getString(c.getColumnIndex("text"));
        }
        if (c != null)
            c.close();
        TctLog.i(TAG, "contentString: " + contentString);
        return contentString;
    }
    public String getImagePath(Context context) {
        String ImageStr = null;
        Cursor Imagecursor = context.getContentResolver().query(Uri.parse("content://com.tct.note/note_image"), null,
                "note_id" + " = " + currentNoteId, null, null);
        if (Imagecursor != null && Imagecursor.getCount() > 0) {
            Imagecursor.moveToFirst();
            ImageStr = Imagecursor.getString(Imagecursor.getColumnIndex("content"));
        }
        if (Imagecursor != null) {
            Imagecursor.close();
        }
        if (ImageStr != null && !"".equalsIgnoreCase(ImageStr)) {
            return context.getFilesDir().getAbsolutePath() + "/Image/" + ImageStr;
        } else {
            return null;
        }

    }
    public Bitmap resizeImageAttachment(int paramInt1, int paramInt2, String paramString,
            NinePatchDrawable paramNinePatchDrawable) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        int i = localOptions.outWidth;
        int j = localOptions.outHeight;
        if ((i <= 0) || (j <= 0))
            return null;
        if (j > paramInt2) {
            i = i * paramInt2 / j;
            j = paramInt2;
        }
        if (i > paramInt1) {
            j = j * paramInt1 / i;
            i = paramInt1;
        }
        int k = localOptions.outWidth / i;
        localOptions.inJustDecodeBounds = false;
        localOptions.inSampleSize = k;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(paramString, localOptions);
        TctLog.i("nb", "localBitmap1: " + k);
        if (localBitmap1 == null)
            return null;
        Rect localRect = new Rect();
        paramNinePatchDrawable.getPadding(localRect);
        Bitmap localBitmap2 = Bitmap.createBitmap(i + localRect.left + localRect.right, j
                + localRect.top + localRect.bottom, Bitmap.Config.ARGB_8888);
        TctLog.i(TAG, "localBitmap2: " + localBitmap2);
        Canvas localCanvas = new Canvas(localBitmap2);
        paramNinePatchDrawable.setBounds(0, 0, localBitmap2.getWidth(), localBitmap2.getHeight());
        paramNinePatchDrawable.draw(localCanvas);
        localCanvas.drawBitmap(localBitmap1, null, new Rect(localRect.left, localRect.top, i
                + localRect.left, j + localRect.top), null);
        localBitmap1.recycle();
        return localBitmap2;
    }

    private PendingIntent getPlayAudiointent(Context context,int id) {//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        final ComponentName serviceName = new ComponentName(context, MediaPlaybackService.class);
        Intent intent = new Intent();
        intent.setComponent(serviceName);
        intent.putExtra(Constants.EXTRA_NOTE_ID, currentNoteId);
        intent.putExtra("skip", true);//add by mingyue.wang for PR880846
        PendingIntent pendingIntent = PendingIntent.getService(context, id, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        return pendingIntent;
    }

    //PR844471 It can save empty note.Modified by hz_nanbing.zou at 20/11/2014 begin
    private PendingIntent getAddIntent(Context context,int id) {          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        Intent intent = new Intent("com.tct.note.NewNote");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra("from_widget", true);
        intent.putExtra("appWidgetId", id);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent,PendingIntent.FLAG_UPDATE_CURRENT);//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        return pi;
    }
    private PendingIntent getViewIntent(Context context,int id) {//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        Intent intent = new Intent("com.tct.note.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("from_widget", true);
        intent.putExtra(Constants.EXTRA_NOTE_ID, (int)currentNoteId);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent,PendingIntent.FLAG_UPDATE_CURRENT);//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        return pi;
    }
    //PR844471 It can save empty note.Modified by hz_nanbing.zou at 20/11/2014 end

    private PendingIntent getMainIntent(Context context,int id) {//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        Intent intent = new Intent(context,NotesListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("from_widget", true);
        intent.putExtra(Constants.EXTRA_NOTE_ID, (int)currentNoteId);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent,PendingIntent.FLAG_UPDATE_CURRENT);//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
        return pi;
    }
    
    
    private Cursor getCursor(Context context) {
        Uri uri = Note.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null,
                Note.COLUMN_ID + "= " + currentNoteId, null, Note.COLUMN_MODIFY_TIME + " ASC");
        return cursor;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;//PR945637 Modified by Gu Feilong
        
//        if(NoteUtils.isGuestModeOn()){//For defect231806
//        	Toast.makeText(context, R.string.guest_mode_view, Toast.LENGTH_SHORT).show();
//        	return;
//        }
        
        
        TctLog.e("nb", "onReceivemini: " + intent.getAction());
        //modify by mingyue.wang for PR880846 begin
        widgetNoteMap= context.getSharedPreferences("widget_note_map",Context.MODE_PRIVATE);
        
        String action = intent.getAction();
        
        if( Intent.ACTION_TIME_CHANGED .equals(action) 
        		|| Intent.ACTION_DATE_CHANGED .equals(action) 
        		|| Intent.ACTION_TIMEZONE_CHANGED .equals(action)){////For PR969764/PR966365
        	isTimeSet = true;
        }
        
        //PR906497 Date,Date format changed,should change.Added by hz_nanbing.zou at 19/01/2015 begin
        /*if ("com.tct.note.widget.UPDATE".equals(action)
                || "com.tct.note.widget.DEL_FROM_APP".equals(action))*/
        //PR906497 Date,Date format changed,should change.Added by hz_nanbing.zou at 19/01/2015 end
        
        {
            currentNoteId = intent.getLongExtra(Constants.EXTRA_NOTE_ID, 0);
            audioState = intent.getBooleanExtra("playing_audio", false);
            
            flag = intent.getBooleanExtra("skip", false);
//            position = intent.getIntExtra("position", 0);
            isNew = intent.getBooleanExtra("from_editor", false);
            
            position = widgetNoteMap.getInt("position", 0);
        //modify by mingyue.wang for PR880846 end

            
            
            if("com.tct.note.widget.PREVIOUS_CLICK".equals(action) 
            		|| "com.tct.note.widget.NEXT_CLICK".equals(action)){
            	
            	Message msg = mHandler.obtainMessage();
            	msg.what = 1;
            	mHandler.removeMessages(1);
            	mHandler.sendMessageDelayed(msg, 500);

            }else if("com.tct.note.widget.UPDATE".equals(action)||"com.tct.note.widget.DEL_FROM_APP".equals(action)){//PR1043292 Monkey[Monitor][Crash][Note]After monkey test appear to com.tct.note crash july 14 2015 update by lxl
                delEmptyAsync delEmpty = new delEmptyAsync();
                delEmpty.execute(0);
            }

       //PR945637 Modified by Gu Feilong start
//        	Message upMs = mHandler.obtainMessage();
//            upMs.what = 0;
//            mHandler.removeMessages(0);
//            mHandler.sendMessage(upMs);
       //PR945637 Modified by Gu Feilong end

        }
    }

    //PR945637 Modified by Gu Feilong start
    private Handler mHandler = new Handler() {
         public void handleMessage(Message message) {
            switch (message.what) {
            case 0:
            if(null!= mContext){
            AppWidgetManager gm = AppWidgetManager.getInstance(mContext);
            performUpdate(mContext, gm, gm.getAppWidgetIds(getComponentName(mContext)));
            Log.e(TAG, "In mHandler,context is not null and update views !");
                 }else{
                     Log.e(TAG, "In mHandler,context is  null !");
                 }
            break;

            case 1:
            	delEmptyAsync delEmpty = new delEmptyAsync();
            	delEmpty.execute(0);
            	break;
            default :
                 break;
            }
        }
    };
    //PR945637 Modified by Gu Feilong end

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	TctLog.d("nb","onUpdatetime:"+System.currentTimeMillis());
        if(NoteUtils.isGuestModeOn()){//For defect231806
        	Toast.makeText(context, R.string.guest_mode_view, Toast.LENGTH_SHORT).show();
        	return;
        }
    	isTimeSet = true;//For PR969764/PR966365
        performUpdate(context, appWidgetManager, appWidgetIds);
    }

    //This is for the new widget.Added "previous"\"next"
    private void setUI_0(){
    	views.setViewVisibility(R.id.widget_title, View.GONE);
    	views.setViewVisibility(R.id.widget_count, View.GONE);
    	views.setViewVisibility(R.id.widget_date, View.GONE);
    	views.setViewVisibility(R.id.widget_pre, View.GONE);
    	views.setViewVisibility(R.id.widget_next, View.GONE);
    }
    private void setEmptyUI(){
    	
    	views.setViewVisibility(R.id.widget_date, View.GONE);
    	views.setViewVisibility(R.id.minifirst, View.GONE);
    	views.setViewVisibility(R.id.minisecond, View.GONE);
    	views.setViewVisibility(R.id.minithree, View.GONE);
    	views.setViewVisibility(R.id.widget_content, View.GONE);
    	views.setViewVisibility(R.id.text_content, View.GONE);
    	views.setViewVisibility(R.id.no_note, View.VISIBLE);
    	views.setTextViewText(R.id.widget_count, "0/0");
		views.setImageViewResource(R.id.widget_pre, R.drawable.ic_chevron_left_grey200_24dp_disable);
		views.setImageViewResource(R.id.widget_next, R.drawable.ic_chevron_right_grey200_24dp_disable);
    	
    	
    }
    private void setUI_1(Cursor c,int id){
    	views.setViewVisibility(R.id.no_note, View.GONE);
    	views.setViewVisibility(R.id.widget_content, View.VISIBLE);
		views.setImageViewResource(R.id.widget_pre, R.drawable.widget_pre);
		views.setImageViewResource(R.id.widget_next, R.drawable.widget_next);
    	views.setViewVisibility(R.id.widget_title, View.VISIBLE);
    	views.setViewVisibility(R.id.widget_count, View.VISIBLE);
    	views.setViewVisibility(R.id.widget_date, View.VISIBLE);
    	views.setViewVisibility(R.id.widget_pre, View.VISIBLE);
    	views.setViewVisibility(R.id.widget_next, View.VISIBLE);

    	views.setTextViewText(R.id.widget_date, getDate(c));
    	views.setTextViewText(R.id.widget_count, getCount());

    	views.setOnClickPendingIntent(R.id.widget_pre, getNext(id));
    	views.setOnClickPendingIntent(R.id.widget_next,getPre(id) );

    }

    private PendingIntent getPre(int id){
    	Intent intent = new Intent("com.tct.note.widget.pre");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        intent.putExtra("position", position);
        intent.putExtra("skip", true);
        PendingIntent pi = PendingIntent.getService(mContext, id, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    	return pi;
    }
    private PendingIntent getNext(int id){
    	Intent intent = new Intent("com.tct.note.widget.next");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra("position", position);
        intent.putExtra("skip", true);
        PendingIntent pi = PendingIntent.getService(mContext, id, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    	return pi;
    }
    private String getCount(){
    	//getPosition();
    	int total = 0;
    	
    	total_cursor = mContext.getContentResolver().query(Note.CONTENT_URI, null, null, null,
    			Note.COLUMN_MODIFY_TIME + " ASC");
    	if(total_cursor!=null&&total_cursor.getCount()>0){
    		total_cursor.moveToLast();//oFirst();
    		
    		
    		if(!isNew){
    		SharedPreferences mini = mContext.getSharedPreferences("widget_note_map",
                    Context.MODE_PRIVATE);
    		flag = mini.getBoolean("skip", flag);
    		position = mini.getInt("position", position);
    		}
    		
    		if(flag){
    			total_cursor.moveToPosition(position);
    		}

    		position = total_cursor.getPosition();
            widgetNoteMap.edit().putInt("position", position).apply();//add by mingyue.wang for PR880846
    		total = total_cursor.getCount();
    		
    		if(total_cursor!=null)
    			total_cursor.close();
    	}

    	TctLog.d("nb","p.."+position+"\ntotal.."+total);

    	if((total-position) == 1){
//        	views.setImageViewResource(R.id.widget_pre, R.drawable.notesmini_back_off);
        	views.setImageViewResource(R.id.widget_pre, R.drawable.ic_chevron_left_grey200_24dp_disable);
    	}else if((total-position) == total){
//        	views.setImageViewResource(R.id.widget_next, R.drawable.notesmini_forward_off);
        	views.setImageViewResource(R.id.widget_next, R.drawable.ic_chevron_right_grey200_24dp_disable);
    	}else{
    		views.setImageViewResource(R.id.widget_pre, R.drawable.widget_pre);
    		views.setImageViewResource(R.id.widget_next, R.drawable.widget_next);
    	}
    	
    	if(total == 1){
    		views.setImageViewResource(R.id.widget_pre, R.drawable.ic_chevron_left_grey200_24dp_disable);
    		views.setImageViewResource(R.id.widget_next, R.drawable.ic_chevron_right_grey200_24dp_disable);
    	}

    	return (total-position)+"/"+total;
    }
    private String getDate(Cursor c){
    	java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(mContext);
    	String s_time;
        SimpleDateFormat sdf;
        Note mNote = NoteUtils.toNote(c);
        Date d = new Date(mNote.mModifyTime);
        if (isToday(d)) {

        	//PR860384 The time icon'：'looks so close to hour.Modified by hz_nanbing.zou at 03/12/2014 begin
        	if(DateFormat.is24HourFormat(mContext)){
        		sdf = new SimpleDateFormat("HH:mm");	
        	}else{
        		sdf = new SimpleDateFormat("hh:mmaa");
        	}
        	//PR860384 The time icon'：'looks so close to hour.Modified by hz_nanbing.zou at 03/12/2014 end

        	s_time = sdf.format(d);
        } else {
              //sdf = new SimpleDateFormat("yyyy-MM-dd");
        	sdf = new SimpleDateFormat("MM/dd/yyyy");

        	//PR932190 CTS,change the date format for Russian.Added by hz_nanbing.zou at 02/13/2015 begin
        	String language = Locale.getDefault().getLanguage();
        	TctLog.d("nb", "lang:"+language);
        	if(language.equals("ru")){
        		sdf = new SimpleDateFormat("dd.MM.yyyy");	
        	}
            s_time = sdf.format(d);
        }

    	return s_time;
    }
    private boolean isToday(Date a) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        return sdf2.format(today).equals(sdf2.format(a));
    }

//    private void getTotal(){
//    	total_cursor = mContext.getContentResolver().query(Note.CONTENT_URI, null, null, null,
//                null);
//    }
    //This is for the new widget.Added "previous"\"next"
    
    

    private void ConfirmDisplayPlace(int flag, RemoteViews view) {
        switch (flag) {
            case ONLY_HAS_IMAGE: {
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_image_blue_18dp);
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.GONE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                break;
            }
            case ONLY_HAS_AUDIO: {
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_mic_blue_18dp);
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.GONE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                break;
            }
            case ONLY_HAS_REMINDER: {
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_access_alarm_blue_18dp);
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.GONE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                break;
            }
            case HAS_IMAGE_AUDIO: {
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_mic_blue_18dp);
                view.setImageViewResource(R.id.minisecond, R.drawable.ic_image_blue_18dp);
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.VISIBLE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                break;
            }
            case HAS_AUDIO_REMINDER: {
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_mic_blue_18dp);
                view.setImageViewResource(R.id.minisecond, R.drawable.ic_access_alarm_blue_18dp);
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.VISIBLE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                break;
            }
            case HAS_IMAGE_REMINDER: {
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_image_blue_18dp);
                view.setImageViewResource(R.id.minisecond, R.drawable.ic_access_alarm_blue_18dp);
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.VISIBLE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                break;
            }
            case HAS_ALL: {
                view.setViewVisibility(R.id.minifirst,View.VISIBLE);
                view.setViewVisibility(R.id.minisecond,View.VISIBLE);
                view.setViewVisibility(R.id.minithree,View.VISIBLE);
                
                //PR935888 The icon display error.Added by hz_nanbing.zou at 2015/02/27 begin
                view.setImageViewResource(R.id.minifirst, R.drawable.ic_mic_blue_18dp);
                view.setImageViewResource(R.id.minisecond, R.drawable.ic_image_blue_18dp);
                view.setImageViewResource(R.id.minithree, R.drawable.ic_access_alarm_blue_18dp);
                //PR935888 The icon display error.Added by hz_nanbing.zou at 2015/02/27 end
                
                break;
            }
            default: {
                view.setViewVisibility(R.id.minifirst,View.GONE);
                view.setViewVisibility(R.id.minisecond,View.GONE);
                view.setViewVisibility(R.id.minithree,View.GONE);
                
                break;
            }
        }
    }
    private int IsOnlyOneAttachment(boolean hasImage, boolean hasAudio, boolean hasReminder) {
        if (hasImage && !hasAudio && !hasReminder) {
            return ONLY_HAS_IMAGE;
        } else if (!hasImage && hasAudio && !hasReminder) {
            return ONLY_HAS_AUDIO;
        } else if (!hasImage && !hasAudio && hasReminder) {
            return ONLY_HAS_REMINDER;
        } else if (hasImage && hasAudio && !hasReminder) {
            return HAS_IMAGE_AUDIO;
        } else if (!hasImage && hasAudio && hasReminder) {
            return HAS_AUDIO_REMINDER;
        } else if (hasImage && !hasAudio && hasReminder) {
            return HAS_IMAGE_REMINDER;
        } else if (hasImage && hasAudio && hasReminder) {
            return HAS_ALL;
        }
        return HAS_NOTHING;
    }
    
    public void  deleteEmptyNote(){//For PR960531
        String selection ="has_image=0 and has_audio=0 and has_reminder=0 and has_text=0";
        try{//PR1043292 add protect After monkey test appear to com.tct.note  update by lxl july 17 2015
            Cursor mCursor = mContext.getContentResolver().query(Note.CONTENT_URI, null, selection, null,
                    null);
            if (mCursor != null && mCursor.getCount() > 0){
                while(mCursor.moveToNext()){
                    long id = mCursor.getLong(mCursor.getColumnIndex(Note.COLUMN_ID));
                    mContext.getContentResolver().delete(Note.CONTENT_URI, Note.COLUMN_ID + " = " + id, null);
                }
            }
            mCursor.close();
        }catch (Exception e){//PR1043292 add protect After monkey test appear to com.tct.note  update by lxl july 17 2015
            Log.e("NoteMiniAppProvider",e.toString());
        }

     }
    
    //Modified for ANR problem
    private class delEmptyAsync extends AsyncTask<Object, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO Auto-generated method stub
			
           	if(!isTimeSet)////For PR969764/PR966365
        		deleteEmptyNote();//For pr960523
        	else
        		isTimeSet = false;
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
            Message upMs = mHandler.obtainMessage();
            upMs.what = 0;
            mHandler.removeMessages(0);
            mHandler.sendMessage(upMs);
		}
    	
    }
    
}
