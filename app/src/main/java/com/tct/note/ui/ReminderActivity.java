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
/*  Email  :  Yan.Teng@tcl.com                                                */
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
/*5/26/2014  |    yanmei.zhang      |      PR-682697       |[Notes]Note can't */
/*           |                      |                      |be closed when    */
/*           |                      |                      |reminder time is up*/
/* ----------|----------------------|----------------------|----------------- */
/*02/28/2015 |     Gu Feilong       |      PR936779        |Record is still   */
/*           |                      |                      |playing when note */
/*           |                      |                      |reminderbox pop up*/
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.ui;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.tct.note.Constants;
import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.util.ReminderReceiver;
import com.tct.note.util.TctLog;
import com.tct.note.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
//import android.app.StatusBarManager;

//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
import android.content.ContentResolver;
//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.PendingIntent;
import android.content.Context;

public class ReminderActivity extends Activity implements DialogInterface.OnClickListener,
        DialogInterface.OnDismissListener {
    private static String TAG = "ReminderActivity";
    private int mNoteId;
    private int mInitialCallState;
    private MediaPlayer mPlayer;
    //private CharSequence mSnippet;
    //private StatusBarManager mStatusBarManager;
    
    private AlertDialog dialog;
    private int count = 0;
    private boolean isCancel =false;
    private boolean isPlaying = false;
    
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-11,PR896382
    private final static int ONLY_HAS_IMAGE = 1;
    private final static int ONLY_HAS_AUDIO = 2;
    private final static int ONLY_HAS_REMINDER = 3;
    private final static int HAS_IMAGE_AUDIO = 4;
    private final static int HAS_IMAGE_REMINDER = 5;
    private final static int HAS_AUDIO_REMINDER = 6;
    private final static int HAS_ALL = 7;
    private final static int HAS_NOTHING = 0;
  //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-11,PR896382
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-26,PR913570
    private WakeLock mWakeLock;
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-26,PR913570
    OverWriteReminderBrocast owr;
    
    ScreenReceicer sr; 
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString) {
            if ((paramAnonymousInt != 0)
                    && (paramAnonymousInt != ReminderActivity.this.mInitialCallState))
                ReminderActivity.this.stopAlarmSound();
        }
    };

    private void stopAlarmSound() {
        if (this.mPlayer != null) {
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
       //PR936779 Add by Gu Feilong start
       AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
          if (am == null) {
              TctLog.e(TAG, "Get audio service failed");
          } else {
              if (am.abandonAudioFocus(mAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            	  TctLog.e(TAG, "Abandon audio focus failed");
              }
          }
        //PR936779 Add by Gu Feilong end
        }
    }

    @SuppressWarnings("static-access")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window localWindow = getWindow();
		//FR789597 modified the code for standalone APK development by bing.wang.hz 2014.09.15 begin
        localWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        //if (!isScreenOn())//[BUGFIX]-Del-BEGIN by TCTNB.(Yan.Teng),01/15/2014,PR-588549
            localWindow.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    /*| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON*///for defect 357054
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);//[BUGFIX]-Mod-BEGIN by TCTNB.(Yan.Teng),01/15/2014,PR-588549
        
            
        if(getIntent().getIntExtra("next_alarm", -1) != -1){
        	this.mNoteId = getIntent().getIntExtra("next_alarm", -1);
        }else{
        	this.mNoteId = Integer.valueOf(getIntent().getData().getPathSegments().get(1));
        }  
        
        SharedPreferences settings = this.getSharedPreferences("count_reminder", this.MODE_PRIVATE);
        count = settings.getInt("count_reminder", 0);
        TctLog.d("nb","onCreate.."+count);
	
        
        //PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 begin
		SharedPreferences sh = this.getSharedPreferences("music", Context.MODE_WORLD_READABLE);
		isPlaying = sh.getBoolean("isplaying", false);
        if(isPlaying){
//        	mHandler.sendEmptyMessage(3);//should not used sent "pause" brocast
        }
        //PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 end
        
        this.mTelephonyManager = ((TelephonyManager) getSystemService(this.TELEPHONY_SERVICE));
        this.mTelephonyManager.listen(this.mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
        //this.mStatusBarManager = ((StatusBarManager) getSystemService(this.STATUS_BAR_SERVICE));
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setOnCompletionListener(mCompletionListener);//PR936779 Add by Gu Feilong
		//FR789597 modified the code for standalone APK development by bing.wang.hz 2014.09.15 end
        showActionDialog();
        playAlarmSound();
//[BUGFIX]-add-BEGIN by TSCD(yanmei.zhang),05/26/2014,PR-682697,
        //sendNotificaction();
//[BUGFIX]-add-END by TSCD(yanmei.zhang)
 
      //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 begin
//		 new Thread(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					Thread.sleep(1000*5);
//					
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				Message msg =new Message();
//				msg.what = WAIT_ALARM_RING;
//				mHandler.sendEmptyMessage(msg.what);
//			}
//			
//		}.start();
		//PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 end
        
		
		
        owr = new OverWriteReminderBrocast();
        IntentFilter iflt = new IntentFilter();
        iflt.addAction("reminder_overwrite");
        this.registerReceiver(owr, iflt);
        
        sr = new ScreenReceicer();
        IntentFilter iflt1 = new IntentFilter();
        iflt1.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        this.registerReceiver(sr, iflt1);
		
    }
    
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-26,PR913570
    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.PARTIAL_WAKE_LOCK, "SimpleTimer");
        mWakeLock.acquire();
    }
    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-26,PR913570
    
//CR881890.Should cancel the notification.Modified by hz_nanbing.zou at 25/12/2014 begin    
//[BUGFIX]-add-BEGIN by TSCD(yanmei.zhang),05/26/2014,PR-682697,
//    private   int notifyId = R.string.note_reminder;
//     @SuppressWarnings("deprecation")
//	@SuppressLint("InlinedApi")
//	private void sendNotificaction() {
//        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification.Builder(this)
//        .setContentTitle("alert")
//        .setContentText("select ")
//        .setSmallIcon(R.drawable.reminder_mark)
//        .setOngoing(true)
//        .setAutoCancel(false)
//        .setPriority(Notification.PRIORITY_MAX)
//        .setDefaults(Notification.DEFAULT_LIGHTS)
//        .setWhen(0).build();
//        
//        //PR        Set more than one reminder,after get one,click the Statues bar icon,Force cloe.Modified by hz_nanbing.zou at 19/11/2014 begin
//        Intent intent = new Intent(this, NotesListActivity.class);
//        //PR        Set more than one reminder,after get one,click the Statues bar icon,Force cloe.Modified by hz_nanbing.zou at 19/11/2014 end
//        
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//        
//        intent.putExtra("from_notify", true);
//        
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(this, getResources().getString(R.string.note_reminder),getResources().getString(R.string.note_notify_text), pendingIntent);
//        manager.notify(notifyId, notification);
//    }
//[BUGFIX]-add-END by TSCD(yanmei.zhang)

    protected void onDestroy() {
//[BUGFIX]-add-BEGIN by TSCD(yanmei.zhang),05/26/2014,PR-682697,
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        TctLog.e(TAG, "reminder onDestroy");
//        manager.cancel(notifyId);
//[BUGFIX]-add-END by TSCD(yanmei.zhang)

        	// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
        	ContentResolver resolver = getContentResolver();
        	Cursor c = resolver.query(Note.Reminder.CONTENT_URI, null, null, null,null);
        	//If has any reminder not arrived,we should set the alarm icon again
        	while (c.moveToNext()) {
        			String time = c.getString(c
        					.getColumnIndex(Note.Reminder.COLUMN_TIME));
        			//If have a reminder later than current time,set notify
        			if (Long.parseLong(time) > System.currentTimeMillis()) {
//        				sendNotificaction();
        				break;
        			}
        	}
        	// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end
        	if(c != null)
        		c.close();
        	
        this.mTelephonyManager.listen(this.mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        
        this.unregisterReceiver(owr);
        this.unregisterReceiver(sr);
        
        super.onDestroy();
    }

    private void playAlarmSound() {
        // TODO Auto-generated method stub
        this.mInitialCallState = this.mTelephonyManager.getCallState();
        
        
        if(this.mInitialCallState == TelephonyManager.CALL_STATE_OFFHOOK){//For PR962231
        	TctLog.e(TAG, "It is a calling.");
        	return;
        }
        
        Uri localUri = RingtoneManager
                .getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        if (localUri == null) {
            TctLog.e(TAG, "url is null.");
            return;
        }
        TctLog.e(TAG, "localUri: . +" + localUri);
        try {
            this.mPlayer.setDataSource(this, localUri);
            this.mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);//For PR985141
            this.mPlayer.prepare();
            this.mPlayer.setLooping(false);
            this.mPlayer.start();
            //PR936779 Add by Gu Feilong start
		     AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		        if (am == null) {
		        	TctLog.e(TAG, "playAlarmSound failed! Get audio service failed");
		        } else {
		            if (am.requestAudioFocus(mAudioFocusChangeListener,
		                    AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {//For PR982257
		            	TctLog.e(TAG, "playAlarmSound failed! Request audio focus failed");
		            }

		        }
	         //PR936779 Add by Gu Feilong end
            return;
        } catch (Exception localException) {
            TctLog.e(TAG, "playAlarmSound failed.", localException);
            return;
        }
    }

    private void showActionDialog() {
        // TODO Auto-generated method stub
        String content = "";
        Cursor c = this.getContentResolver().query(Note.Text.CONTENT_URI, null,
                Note.Text.COLUMN_NOTE_ID + " = " + mNoteId, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            content = c.getString(c.getColumnIndex(Note.Text.COLUMN_TEXT));
        }
        if (c != null) {
            c.close();
        }
        //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-11,PR896382
        View view= LayoutInflater.from(ReminderActivity.this).inflate(R.layout.showreminder_dialog, null);
        Cursor mCursor=this.getContentResolver().query(Note.CONTENT_URI, null, Note.COLUMN_ID+ " = " + mNoteId, null,
               null);
        //PR841643 DateFormat as same as setting.Modified by hz_nanbing.zou at 19/11/2014 begin
        String s_time;
        SimpleDateFormat sdf;
        if(mCursor!=null&& mCursor.getCount() > 0){
                mCursor.moveToFirst();
                Note mNote = NoteUtils.toNote(mCursor);
                Date d = new Date(mNote.mModifyTime);
               if (isToday(d)) {
                 //PR860384 The time icon'：'looks so close to hour.Modified by hz_nanbing.zou at 03/12/2014 begin
                 if(DateFormat.is24HourFormat(ReminderActivity.this)){
                    sdf = new SimpleDateFormat("HH:mm");
                  }else{
                    sdf = new SimpleDateFormat("hh:mm aa");
                 }
                //PR860384 The time icon'：'looks so close to hour.Modified by hz_nanbing.zou at 03/12/2014 end
                  s_time = sdf.format(d);
               } else {
                //PR866274 There has some overlap on note when set data format as Wed,Dec 11,2014. added by feilong.gu at 12/11/2014 start 
                     sdf = new SimpleDateFormat("MM/dd/yyyy");
                      s_time = sdf.format(d);
                  //PR866274 There has some overlap on note when set data format as Wed,Dec 31,2014. added by feilong.gu at 12/11/2014 end
               }
               
               int AttachmentFlag = IsOnlyOneAttachment(mNote.mHasImage, mNote.mHasAudio,
                       mNote.mHasReminder);
               TctLog.d("nb","AttachmentFlag..."+AttachmentFlag);
             
               TextView display_content = ((TextView) view.findViewById(R.id.display_content));
               if(!TextUtils.isEmpty(content)){
               	 display_content.setVisibility(View.VISIBLE);
               	 display_content.setText(content);
               }else{
               	display_content.setVisibility(View.GONE);
               }
               ConfirmDisplayPlace(AttachmentFlag, view);
               TextView mGName = ((TextView) view.findViewById(R.id.timetv));
               mGName.setVisibility(View.VISIBLE);
               mGName.setText(s_time);
        }
        if (mCursor != null) {
            mCursor.close();
        }

        //<!-- PR854590 Reminder dialog ugly.Added by hz_nanbing.zou at 27/11/2014 begin-->
        //PR915668.Locked screen,reminder dialog not show.Added by hz_nanbing.zou at 28/01/2015 begin
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this,R.style.myDialog);
        //PR915668.Locked screen,reminder dialog not show.Added by hz_nanbing.zou at 28/01/2015 end
        //<!-- PR854590 Reminder dialog ugly.Added by hz_nanbing.zou at 27/11/2014 end-->
        
        localBuilder.setTitle(R.string.note_reminder);
        localBuilder.setView(view);
        localBuilder.setPositiveButton(R.string.btn_view, this);
        localBuilder.setCancelable(true);
       //if (isScreenOn())//[BUGFIX]-Del-BEGIN by TCTNB.Yan.TENG,10/14/2013,PR535438
        
        if(NoteUtils.isEnglishLan())
        	localBuilder.setNegativeButton(R.string.btn_dismiss, this);
        else
        	localBuilder.setNegativeButton(R.string.btn_cancel, this);
        
        //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 begin
        ///localBuilder.show().setOnDismissListener(this);
        dialog = localBuilder.show();
        dialog.setOnDismissListener(this);
        //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 end
    }
    
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-11,PR896382
    public boolean isToday(Date a) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        return sdf2.format(today).equals(sdf2.format(a));
    }
    
    public int IsOnlyOneAttachment(boolean hasImage, boolean hasAudio, boolean hasReminder) {
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
    
    public void ConfirmDisplayPlace(int flag, View view) {
        switch (flag) {
            case ONLY_HAS_IMAGE: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_image_grey600_18dp);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case ONLY_HAS_AUDIO: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_mic_grey600_18dp);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case ONLY_HAS_REMINDER: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_access_alarm_grey600_18dp);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case HAS_IMAGE_AUDIO: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_mic_grey600_18dp);
                ((ImageView) view.findViewById(R.id.image_mark))
                        .setBackgroundResource(R.drawable.ic_image_grey600_18dp);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case HAS_AUDIO_REMINDER: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_access_alarm_grey600_18dp);
                ((ImageView) view.findViewById(R.id.image_mark))
                        .setBackgroundResource(R.drawable.ic_mic_grey600_18dp);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case HAS_IMAGE_REMINDER: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_access_alarm_grey600_18dp);
                ((ImageView) view.findViewById(R.id.image_mark))
                        .setBackgroundResource(R.drawable.ic_image_grey600_18dp);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case HAS_ALL: {
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.VISIBLE);
                break;
            }
            default: {
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
        }
    }
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-11,PR896382

    /*private boolean isScreenOn() {
        return ((PowerManager) getSystemService(this.POWER_SERVICE)).isScreenOn();
    }*/

    @Override
    public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
        //[BUGFIX]-add-BEGIN by TSCD(yanmei.zhang),05/26/2014,PR-682697,
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(notifyId);
//[BUGFIX]-add-END by TSCD(yanmei.zhang)
        TctLog.e(TAG, "reminder onDismiss");
        

        
        stopAlarmSound();
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        TctLog.e(TAG, "reminder onClick: " + which);
        switch (which) {
            case -1: {
                Intent localIntent = new Intent("com.tct.note.action.VIEW");//[BUGFIX]-MOd-BEGIN by TCTNB.Yan.TENG,01/02/2014,PR583095
                localIntent.putExtra(Constants.EXTRA_NOTE_ID, this.mNoteId);
                startActivity(localIntent);
                break;
            }
            case -2:
                break;
            default:
                break;
        }
        
        
        //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 begin
        //mHandler.sendEmptyMessage(2);
        isCancel = true;
        SharedPreferences settings = ReminderActivity.this.getSharedPreferences("isCancel", ReminderActivity.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isCancel"+mNoteId, isCancel);//modify by mingyue.wang for pr895636
        editor.commit();
        //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 end
        
    }

  //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 begin
    public static final int WAIT_NEXT_ALARM = 0;
    public static final int WAIT_ALARM_RING = 1;
    public static final int CANCEL_ALARM = 2;
    public static final int PAUSE_MUSIC = 3;
    public static final int CONTINUE_MUSIC = 4;
    Handler mHandler = new Handler(){

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			
			case CANCEL_ALARM:
				count = 4;
				mHandler.sendEmptyMessage(0);
				break;
			case WAIT_NEXT_ALARM:
				TctLog.d("nb","WAIT_NEXT_ALARM.."+count);
				
				
				Cursor cur = getContentResolver().query(Note.Text.CONTENT_URI, null,
		                Note.Text.COLUMN_NOTE_ID + " = " + mNoteId, null, null);
				
				if(cur.getCount() == 0){
					count = 4;
					TctLog.d("nb","cursor delete");
				}
				
	            //modify by mingyue.wang for pr881920 begin 
     
				SharedPreferences isCancleShare = ReminderActivity.this.getSharedPreferences("isCancel", ReminderActivity.this.MODE_PRIVATE);
				if(!isCancleShare.getBoolean("isCancel"+mNoteId, false)){
				//modify by mingyue.wang for pr881920 end 
				 

				if(count < 3){
				Intent i = new Intent(ReminderActivity.this, ReminderReceiver.class);
				i.putExtra("next_alarm",mNoteId);
				sendBroadcast(i);
//				new Thread(){
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						try {
//							Thread.sleep(1000);
//							
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						Message msg =new Message();
//						msg.what = WAIT_ALARM_RING;
//						mHandler.sendEmptyMessage(msg.what);
//					}
//					
//				}.start();
				}else{
					
					//PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 begin
					if(isPlaying){
						sendEmptyMessage(4);
					}
					//PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 end
					
					count = 0;
			        SharedPreferences settings = ReminderActivity.this.getSharedPreferences("count_reminder", ReminderActivity.this.MODE_PRIVATE);
			        SharedPreferences.Editor editor = settings.edit();
			        editor.putInt("count_reminder", count);
			        editor.commit();
			        stopAlarmSound();
			        finish();
				}
				}else{

		        	TctLog.d("nb","isCancel");
		            isCancel = false;
		            SharedPreferences settings = ReminderActivity.this.getSharedPreferences("isCancel", ReminderActivity.this.MODE_PRIVATE);
		            SharedPreferences.Editor editor = settings.edit();
		            editor.putBoolean("isCancel"+mNoteId, isCancel); //modify by mingyue.wang for pr881920
		            editor.commit();
		            
					count = 0;
			        SharedPreferences c = ReminderActivity.this.getSharedPreferences("count_reminder", ReminderActivity.this.MODE_PRIVATE);
			        SharedPreferences.Editor editor_c = c.edit();
			        editor_c.putInt("count_reminder", count);
			        editor_c.commit();
			        stopAlarmSound();
			        finish();
		        
				}
				break;
			case WAIT_ALARM_RING:

		        {
		        	if(dialog!=null)
		        	 dialog.dismiss();
		        	
		        	//PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 begin
					if(isPlaying){
						sendEmptyMessage(4);
					}
					//PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 end
		        	
		        	count++;
		        	SharedPreferences settings = ReminderActivity.this.getSharedPreferences("count_reminder", ReminderActivity.this.MODE_PRIVATE);
		        	SharedPreferences.Editor editor = settings.edit();
		        	editor.putInt("count_reminder", count);
		        	editor.commit();
		        	TctLog.d("nb","WAIT_ALARM_RING"+count);
		        	new Thread(){

		        		@Override
		        		public void run() {
		        			// TODO Auto-generated method stub
		        			try {
		        				Thread.sleep(1000*50);
							
		        			} catch (InterruptedException e) {
		        				// TODO Auto-generated catch block
		        				e.printStackTrace();
		        			}
						Message msg =new Message();
						msg.what = WAIT_NEXT_ALARM;
						mHandler.sendEmptyMessage(msg.what);
					}
		        	}.start();
		        }
				break;
				
			//PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 begin	
			case PAUSE_MUSIC:
				TctLog.d("nb","PAUSE_MUSIC");
				Intent it = new Intent("com.android.music.musicservicecommand");
				it.putExtra("command", "pause");
				sendBroadcast(it);
				break;
			case CONTINUE_MUSIC:
				TctLog.d("nb","music brocast go on");
				Intent i = new Intent("com.android.music.musicservicecommand");
				i.putExtra("command", "play");
				sendBroadcast(i);
				break;
			//PR842943.Music and reminder play at the same time.Added by hz_nanbing.zou at 19/11/2014 end	
				
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
    	
    };
  //PR831498 alarm ring all the time.Add by hz_nanbing.zou at 10/11/2014 end

    //PR854658 Enter Note from statue bar,reminder can be ring.Added by hz_nanbing.zou at 27/11/2014 begin
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TctLog.d("nb", "Reminder_onPause.."+this.getCallingActivity());
        //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-26,PR913570
        mWakeLock.release();
        //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-26,PR913570
		
	}

	private class OverWriteReminderBrocast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			TctLog.d("nb", "OverWriteReminderBrocast..");
			if(intent.getAction() == "reminder_overwrite"){
	        	TctLog.d("nb","isCancel");
	            isCancel = false;
	            SharedPreferences settings = ReminderActivity.this.getSharedPreferences("isCancel", ReminderActivity.this.MODE_PRIVATE);
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putBoolean("isCancel"+mNoteId, isCancel); //modify by mingyue.wang for pr881920
	            editor.commit();
	            
				count = 0;
		        SharedPreferences c = ReminderActivity.this.getSharedPreferences("count_reminder", ReminderActivity.this.MODE_PRIVATE);
		        SharedPreferences.Editor editor_c = c.edit();
		        editor_c.putInt("count_reminder", count);
		        editor_c.commit();
		        stopAlarmSound();
		        finish();
			}
		}
		
	}
	//PR854658 Enter Note from statue bar,reminder can be ring.Added by hz_nanbing.zou at 27/11/2014 end

    //PR936779 Add by Gu Feilong start
	//For PR956458 
	private OnAudioFocusChangeListener mAudioFocusChangeListener = new OnAudioFocusChangeListener() {
		 @Override
	        public void onAudioFocusChange(int focusChange) {
	            switch (focusChange) {
	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	                case AudioManager.AUDIOFOCUS_LOSS:
	                	TctLog.d("nb","OnAudioFocusChangeListener");
	                	stopAlarmSound();
                        break;
	                case AudioManager.AUDIOFOCUS_GAIN:
	                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
	                default:
	                    ;
	            }
		 }
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener(){

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			stopAlarmSound();
		}

    };
    //PR936779 Add by Gu Feilong end

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		TctLog.d("nb","onStop:");
		if(dialog !=null){
//			stopAlarmSound();//for PR958842
		}
		super.onStop();
	}
	
	
	
	private class ScreenReceicer extends BroadcastReceiver{//For PR946519/958842

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String SYSTEM_REASON = "reason";
			  String action = intent.getAction();
			  if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
				  String reason = intent.getStringExtra(SYSTEM_REASON);
				  if(reason!=null){
						if(dialog !=null){
							stopAlarmSound();//for PR958842
						}  
				  }
				  }
			
		}
		
	}
	
	
}
