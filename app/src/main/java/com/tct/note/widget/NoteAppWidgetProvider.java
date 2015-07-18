/******************************************************************************//*
                                                               Date:07/2013 
                                PRESENTATION                                
                                                                            
       Copyright 2013 TCL Communication Technology Holdings Limited.        
                                                                            
 This material is company confidential, cannot be reproduced in any form    
 without the written permission of TCL Communication Technology Holdings    
 Limited.                                                                   
                                                                            
 -------------------------------------------------------------------------- 
  Author :  Yan.Teng                                                        
  Email  :  Yan.Teng@tcl-mobile.com                                         
  Role   :                                                                  
  Reference documents :                                                     
 -------------------------------------------------------------------------- 
  Comments :                                                                
  File     :                                                                
  Labels   :                                                                
 -------------------------------------------------------------------------- 
 ========================================================================== 
     Modifications on Features list / Changes Request / Problems Report     
 -------------------------------------------------------------------------- 
    date   |        author        |         Key          |     comment      
 ----------|----------------------|----------------------|----------------- 
 07/05/2013|       Yan.Teng       |       CR484292       |the development   
           |                      |                      |of note and note  
           |                      |                      |widget            
 ----------|----------------------|----------------------|----------------- 
 06/09/2014|      yanmei.zhang    |      PR-692135       |Notes Widgets     
           |                      |                      |Content disappear 
           |                      |                      |After restart the 
           |                      |                      |phone             
 ----------|----------------------|----------------------|----------------- 
*//******************************************************************************//*

package com.tct.note.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;

import com.tct.note.Constants;
import com.tct.note.MediaPlaybackService;
import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.ui.NotesListActivity;
import com.tct.note.util.TctLog;
import com.tct.note.R;

public class NoteAppWidgetProvider extends AppWidgetProvider {

    public static final ComponentName COMPONENT = new ComponentName("com.tct.note",
            "com.tct.note.widget.NoteAppWidgetProvider");
	
//	public static final ComponentName COMPONENT = new ComponentName("com.jrdcom.note",
//            "com.jrdcom.note.widget.NoteAppWidgetProvider");
    
    
    
    private static final String TAG = "NoteAppWidgetProvider";
    public long currentNoteId;//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
    private RemoteViews views;
    private static boolean audioState;

    private Context mContext;
    private Cursor total_cursor;
    private boolean flag = false;
    private int position =0;
    private SharedPreferences widgetNoteMap;//add by mingyue.wang for PR880846
    
    static ComponentName getComponentName(Context context) {
        return new ComponentName(context, NoteAppWidgetProvider.class);
    }

    public void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    	mContext = context;
    	getTotal();

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
            views = new RemoteViews(context.getPackageName(), R.layout.widget_idol3);
            //[BUGFIX]-add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
            views.setOnClickPendingIntent(R.id.miniEnter, getMainIntent(context,appWidgetIds[i]));
            // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 begin
            currentNoteId = (long)widgetNoteMap.getLong("widget", -1);
            // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 end

            
            TctLog.d("nb","ID.."+currentNoteId);
            
            TctLog.i(TAG, "onUpdate  currentNoteId===" + currentNoteId + "   appWidgetIds  ===" + appWidgetIds[i]);
            //[BUGFIX]-add-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
            Cursor noteCursor = getCursor(context);
            if (noteCursor == null || (noteCursor != null && noteCursor.getCount() <= 0)) {
                views.setViewVisibility(R.id.empty_layout, View.VISIBLE);
                views.setViewVisibility(R.id.note_empty2, View.VISIBLE);
                views.setViewVisibility(R.id.miniadd, View.VISIBLE);
                views.setViewVisibility(R.id.miniemptyEnter, View.VISIBLE);
//                views.setTextViewText(R.id.note_empty2,
//                        context.getResources().getString(R.string.widget_note_empty));

                setUI_0();
                
                views.setViewVisibility(R.id.widget_header, View.GONE);
                views.setOnClickPendingIntent(R.id.miniadd, getAddIntent(context,appWidgetIds[i]));
                views.setViewVisibility(R.id.widget_audio, View.GONE);
                views.setViewVisibility(R.id.all_line, View.GONE);
                views.setViewVisibility(R.id.all_line2, View.GONE);
                views.setViewVisibility(R.id.note_list, View.GONE);
                views.setOnClickPendingIntent(R.id.miniemptyEnter, getMainIntent(context,appWidgetIds[i]));
                views.setOnClickPendingIntent(R.id.note_empty2, getAddIntent(context,appWidgetIds[i]));  //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
            } else {
                noteCursor.moveToFirst();
                views.setViewVisibility(R.id.note_empty2, View.GONE);
                views.setViewVisibility(R.id.miniadd, View.GONE);
                views.setViewVisibility(R.id.miniemptyEnter, View.GONE);
                views.setViewVisibility(R.id.empty_layout, View.GONE);
                views.setViewVisibility(R.id.widget_header, View.VISIBLE);
                views.setViewVisibility(R.id.all_line, View.VISIBLE);
                views.setViewVisibility(R.id.all_line2, View.VISIBLE);
                views.setViewVisibility(R.id.note_list, View.VISIBLE);

                setUI_1(noteCursor,appWidgetIds[i]);

                String text = getText(context);
                if( text != null && !"".equalsIgnoreCase(text)) {
                views.setViewVisibility(R.id.text_content, View.VISIBLE);
                views.setTextViewText(R.id.text_content, text);
                } else {
                    views.setViewVisibility(R.id.text_content, View.GONE);
                }

                String imageUri = getImagePath(context);
                if (imageUri != null ) {
                    views.setViewVisibility(R.id.image_content, View.VISIBLE);
                DisplayMetrics localDisplayMetrics =context.getResources().getDisplayMetrics();
                Bitmap bmp = resizeImageAttachment((int) (0.7F * localDisplayMetrics.widthPixels),
                        (int) (0.6F * localDisplayMetrics.heightPixels),imageUri,(NinePatchDrawable) context.getResources().getDrawable(
                        R.drawable.actionbar));
                views.setImageViewBitmap(R.id.image_content, bmp);
                } else {
                    views.setViewVisibility(R.id.image_content, View.GONE);
                }

//                views.setImageViewResource(R.id.add, R.drawable.widget_create_new);
                views.setOnClickPendingIntent(R.id.widget_content, getViewIntent(context,appWidgetIds[i]));          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
                views.setOnClickPendingIntent(R.id.add, getAddIntent(context,appWidgetIds[i]));          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
                boolean hasAudio = noteCursor.getInt(noteCursor
                        .getColumnIndex(Note.COLUMN_HAS_AUDIO)) == 1;
                if (hasAudio) {
                    views.setViewVisibility(R.id.widget_audio, View.VISIBLE);
                    views.setImageViewResource(R.id.widget_audio, R.drawable.audio_play);
                    views.setOnClickPendingIntent(R.id.widget_audio, getPlayAudiointent(context,appWidgetIds[i]));          //[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
                } else {
                    views.setViewVisibility(R.id.widget_audio, View.GONE);
                }
                if (audioState) {
                    views.setImageViewResource(R.id.widget_audio, R.drawable.audio_pause);
                } else {
                    views.setImageViewResource(R.id.widget_audio, R.drawable.audio_play);
                }
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
                Note.COLUMN_ID + "= " + currentNoteId, null, Note.COLUMN_MODIFY_TIME);
        return cursor;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        TctLog.e("nb", "onReceive: " + intent.getAction());
        String action = intent.getAction();
        //modify by mingyue.wang for PR880846 begin
        widgetNoteMap= context.getSharedPreferences("widget_note_map",Context.MODE_PRIVATE);
        if ("com.tct.note.widget.UPDATE".equals(action)
                || "com.tct.note.widget.DEL_FROM_APP".equals(action)) {
            currentNoteId = intent.getLongExtra(Constants.EXTRA_NOTE_ID, 0);
            audioState = intent.getBooleanExtra("playing_audio", false);
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            
            flag = intent.getBooleanExtra("skip", false);
//            position = intent.getIntExtra("position", 0);
            
            position = widgetNoteMap.getInt("position", 0);
        //modify by mingyue.wang for PR880846 end
            performUpdate(context, gm, gm.getAppWidgetIds(getComponentName(context)));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        TctLog.i("nb", "onUpdate().");
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
    private void setUI_1(Cursor c,int id){
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
    	if(total_cursor!=null&&total_cursor.getCount()>0){
    		total_cursor.moveToLast();//oFirst();
    		
    		if(flag){
    			total_cursor.moveToPosition(position);
    		}

    		position = total_cursor.getPosition();
            widgetNoteMap.edit().putInt("position", position).apply();//add by mingyue.wang for PR880846
    		total = total_cursor.getCount();
    	}

    	TctLog.d("nb","p.."+position+"\ntotal.."+total);

    	if((total-position) == 1){
        	views.setImageViewResource(R.id.widget_pre, R.drawable.notesmini_back_off);
        	views.setImageViewResource(R.id.widget_next, R.drawable.widget_next);
    	}else if((total-position) == total){
        	views.setImageViewResource(R.id.widget_next, R.drawable.notesmini_forward_off);
        	views.setImageViewResource(R.id.widget_pre, R.drawable.widget_pre);
    	}else{
    		views.setImageViewResource(R.id.widget_pre, R.drawable.widget_pre);
    		views.setImageViewResource(R.id.widget_next, R.drawable.widget_next);
    	}
    	
    	if(total == 1){
    		views.setImageViewResource(R.id.widget_pre, R.drawable.notesmini_back_off);
    		views.setImageViewResource(R.id.widget_next, R.drawable.notesmini_forward_off);
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
            s_time = shortDateFormat.format(d);
        }

    	return s_time;
    }
    private boolean isToday(Date a) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        return sdf2.format(today).equals(sdf2.format(a));
    }

    private void getTotal(){
    	total_cursor = mContext.getContentResolver().query(Note.CONTENT_URI, null, null, null,
                null);
    }
    //This is for the new widget.Added "previous"\"next"
}
*/