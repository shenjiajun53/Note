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
/* 11/13/2013|        bo.xu         |      PR-550155       |[Notes]Display is */
/*           |                      |                      | not correct when */
/*           |                      |                      | create a new no- */
/*           |                      |                      |te                */
/* ----------|----------------------|----------------------|----------------- */
/* 11/26/2013|      wenlu.wu        |      PR-558068       |Notes force close */
/*           |                      |                      |when attach record*/
/*           |                      |                      |audio and pull out*/
/*           |                      |                      |the SD card       */
/* ----------|----------------------|----------------------|----------------- */
/* 06/09/2014|      yanmei.zhang    |      PR-692135       |Notes Widgets     */
/*           |                      |                      |Content disappear */
/*           |                      |                      |After restart the */
/*           |                      |                      |phone             */
/* ----------|----------------------|----------------------|----------------- */
/* 06/18/2014|       dongjin        |        706375        |Notes background  */
/*           |                      |                      |color can't be c- */
/*           |                      |                      |hanged after cha- */
/*           |                      |                      |nge it            */
/* ----------|----------------------|----------------------|----------------- */
/* 06/21/2014|       dongjin        |        711267        |[Notes]Reminders  */
/*           |                      |                      |will disppear af- */
/*           |                      |                      |ter save record   */
/*           |                      |                      |audio             */
/* ----------|----------------------|----------------------|----------------- */
/* 07/18/2014|        gerong        |       PR-692034      |Number of Notes   */
/*           |                      |                      |display error     */
/*           |                      |                      |after sort by     */
/*           |                      |                      |color audio       */
/* ----------|----------------------|----------------------|----------------- */
/* 08/04/2014|        gerong        |      PR-754288       |Note force close  */
/*           |                      |                      |unexpectedly      */
/*----------------------------------------------------------------------------*/
/* 12/19/2014|     Gu Feilong       |      PR-876860       |Enter into gallery*/
/*           |                      |                      |when launch notes */
/*----------------------------------------------------------------------------*/
/* 12/26/2014|     Gu Feilong       |      PR-853776       |[ANR][Note]DUT pop*/
/*           |                      |                      |up force close    */
/*           |                      |                      |when  try to play */
/*           |                      |                      |a big record file */
/*           |                      |                      |in note app       */
/*----------------------------------------------------------------------------*/
/* 01/19/2015|     Gu Feilong       |      PR-903993       |The record time   */
/*           |                      |                      |display is wrong  */
/*           |                      |                      |when the max      */
/*           |                      |                      |record time is 4  */
/*           |                      |                      |hours.            */
/*----------------------------------------------------------------------------*/
/* 01/20/2015|     Gu Feilong       |      PR-900713       |The recording time*/
/*           |                      |                      |changed when play */
/*           |                      |                      |record            */
/*----------------------------------------------------------------------------*/
/* 03/11/2015|     Gu Feilong       |      PR-945404       |It pop up FC after*/
/*           |                      |                      | reject a call    */
/*----------------------------------------------------------------------------*/
/* 03/12/2015|     Gu Feilong       |      PR-947315       |Recorder can      */
/*           |                      |                      |playing wheile    */
/*           |                      |                      |talking on the    */
/*           |                      |                      |telephone         */
/*----------------------------------------------------------------------------*/
/* 03/13/2015|     Gu Feilong       |      PR-948178       |Notes with        */
/*           |                      |                      |reminder can not  */
/*           |                      |                      |save              */
/*----------------------------------------------------------------------------*/
/* 03/19/2015|     Gu Feilong       |      PR-952991       |Tap to recording, */
/*           |                      |                      |recorder time is  */
/*           |                      |                      |changed while     */
/*           |                      |                      |during the call.  */
/*----------------------------------------------------------------------------*/
/******************************************************************************/

package com.tct.note.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tct.note.Constants;
import com.tct.note.R;
import com.tct.note.data.Note;
import com.tct.note.data.NoteProvider;
import com.tct.note.data.Note.Group;
import com.tct.note.data.NoteUtils;
import com.tct.note.util.AttachmentUtils;
import com.tct.note.util.ReminderReceiver;
import com.tct.note.util.TctLog;
import com.tct.note.util.setMenuIconUtils;
import com.tct.note.view.NoteEditorView;
import com.tct.note.widget.NoteMiniAppProvider;
//import com.tct.note.widget.NoteAppWidgetProvider;
import com.tct.note.widget.UpdateWidgetService;
//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
//[BUGFIX]-ADD-BEGIN by TCTNB.Yan.TENG,01/02/2014,PR580222

public class NoteEditorActivity extends Activity {
    private static final String TAG = NoteEditorActivity.class.getSimpleName();
    //private ImageButton attachItem;
    //private ImageButton colorItem;
    private Button stopAudio;
    private ImageView imageview;
    private TextView date_view;
    
    private RelativeLayout mRecord_Layout;
    private TextView tap_view;
    
    private boolean isFrom_Reminder = false;
    
    private PopupWindow popupwindow;
    private View editorView;
    private Note.Reminder mRminder;
    private Note mNote;
    private Note.Text mText;
    private Note.Audio mNewAudio = null;//[BUGFIX]-Mod-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135

    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private NoteEditorView mNoteEditor;
    private ArrayList<Note.Audio> mCacheAudioList = new ArrayList<Note.Audio>();
    private ArrayList<String> mImageList = new ArrayList<String>();
    public static final int REQUEST_CODE_TAKE_IMAGE = 1;
    public static final int REQUEST_CODE_PICK_IMAGE = 2;
    public static final int REQUEST_CODE_PICK_REMINDER = 3;
    public static final int REQUEST_CODE_TAKE_RECORD = 4;

    //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 begin
    public static final int STATE_RECORD_ERROR = -1;
    //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 end
    
    public static final int STATE_CHANGED_SCREEN = 0;
    
    public static final int STATE_START_RECORD = 1;
    public static final int STATE_STOP_RECORD = 2;
    public static final int STATE_START_PLAY = 3;
    public static final int STATE_STOP_OR_PAUSE_PLAY = 4;
    protected static final int MSG_WHAT_SWITCH_RECORD_ICON = 1;
    protected static final int MSG_WHAT_UPDATE_PLAY_PROGRESS = 2;
    
    protected static final int MSG_WHAT_START_INSERT_IMAGE = 7;
    protected static final int MSG_WHAT_END_INSERT_IMAGE = 8;
    
    protected static final int MSG_WHAT_SAVE_FROM_HOMEKEY = 9;//For PR984546
    
    protected static final int MSG_WHAT_SHOW_PAUSE_DURATION = 10;//For pause anr
    
    private int Recordstate = STATE_STOP_RECORD;
    private int Playstate = STATE_STOP_OR_PAUSE_PLAY;
    private boolean MediaPause = false;
    private ListView list;
    private ArrayList<View> itemView = new ArrayList<View>();
    private AudioAdapter adapter;
    private LinearLayout AllLayout;
    private ImageButton Color_one;
    private ImageButton Color_three;
    private ImageButton Color_two;
    private ImageButton Color_four;

    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
    private ImageButton deleteReminder;
    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
    
    //Added for Idol3
    private TextView edit_add;
    private ImageButton saveItem;
  //Added for Idol3
    
	//PR879516 .The note editor will response slowly after dome some operations.Added by hz_nanbing.zou at 23/12/2014 begin
    private TelephonyManager mTelephonyManager;
    private int mInitialCallState;
	//PR879516 .The note editor will response slowly after dome some operations.Added by hz_nanbing.zou at 23/12/2014 end
    
    //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 begin
    private boolean illegalFlag = false;
  //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 end
    
    //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
    MusicBrocastReceiver mbr;
    //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end

    //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
    private AudioManager audioManager;
    private MediaFocusListener mfl;
    private audioListView alv;
    //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end

    //PR844471.After home key --> enter from widget,can save empty. Added by hz_nanbing.zou at 19/11/2014 begin
    HomeKeyReceiver hombr;
    //PR851521 Home key save note prompt.Added by hz_nanbing.zou at 25/11/2014 begin
    private boolean isHomeKey = false;
    //PR851521 Home key save note prompt.Added by hz_nanbing.zou at 25/11/2014 end
    //PR844471.After home key --> enter from widget,can save empty. Added by hz_nanbing.zou at 19/11/2014 end
    
    //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
    private boolean isOtherActivity = false;
    //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
    
    private boolean isView = false;
    
    private LoadNoteAsyncTask mLoadNoteAsyncTask;
    private int NO_SET_REMINDER = -1;
    private String currentFileName;
    private boolean DeleteDisplay = false;
    private long mOldGroudId = 0;
    private long mOldReminderId =0;
    //private boolean isFromwidget = false;
 //[BUGFIX]-Add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135
    private long newReminderTime = -1;
    private long AudioCount = 0;
   //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-24,PR876537
    private String mUriStr="";
   //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-24,PR876537
    
	 private DatePickerDialog dpd;
	 private TimePickerDialog tpd;
	 
	 
	 private String textFromQR = "";
    
	 private int tempTime = 0;//For PR967021
	 
	 private TextView vRecordTime ;
	 private TextView vRecordTips ;
	 private TextView vRecordDiscard ;
	 private TextView vRecordSave ;
	 private ImageButton vRecordTap;
	 private ImageButton vRecordTap2;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.tct.note.util.TctLog.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);
        Intent intent = getIntent();
        if (mNote == null)
            mNote = new Note();
        mText = new Note.Text();
        adapter = new AudioAdapter(NoteEditorActivity.this);
        long noteId = intent.getIntExtra(Constants.EXTRA_NOTE_ID, 0);
        //isFromwidget = intent.getBooleanExtra("from_widget", false);
      //[BUGFIX]-Add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135
        mNote.WidgetNoteId = intent.getIntExtra("appWidgetId", -1);
        TctLog.e(TAG, "note Editor noteId : " + noteId + " WidgetNoteId  ==" + mNote.WidgetNoteId);
      //[BUGFIX]-Add-END by TSCD(yanmei.zhang),06/09/2014,PR692135

        // PR 824190 keyboard pop up. Modified by hz_nanbing_zou at 30/10/2014 begin


        list = (ListView) (findViewById(android.R.id.list));
        
//        list.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,120));
        
        AllLayout = (LinearLayout) findViewById(R.id.note_editor);


        mNoteEditor = (NoteEditorView) findViewById(R.id.text_field);
        
        
        // FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
        deleteReminder = (ImageButton) findViewById(R.id.delete_reminder);
        // FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
        
        //PR844471.After home key --> enter from widget,can save empty. Added by hz_nanbing.zou at 19/11/2014 begin
        hombr = new HomeKeyReceiver();
        IntentFilter itf = new IntentFilter();
        itf.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        itf.addAction(Intent.ACTION_SHUTDOWN);
        itf.addAction(Intent.ACTION_REBOOT);
        
        itf.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        
        this.registerReceiver(hombr, itf);
        //PR844471.After home key --> enter from widget,can save empty. Added by hz_nanbing.zou at 19/11/2014 end
        
        
        //PR866920,Pop up "Touch this button to create your first note" While has a note in Notes.Modified by hz_nanbing.zou at 10/12/2014 begin
        if("android.intent.action.SEND".equals(intent.getAction())){
        	TctLog.d("nb","SEND");
            SharedPreferences send = this.getSharedPreferences("send", this.MODE_PRIVATE);
            SharedPreferences.Editor editor = send.edit();
            editor.putBoolean("send", true);
            editor.commit();
        
        }
        //PR866920,Pop up "Touch this button to create your first note" While has a note in Notes.Modified by hz_nanbing.zou at 10/12/2014 end
        
        if ("com.tct.note.action.VIEW".equals(intent.getAction())) {
        	TctLog.d("nb","view");
            
            //PR833528 Guest_Mode not perform note.Added by hz_nanbing.zou at 17/14/2014 begin
            if(NoteUtils.isGuestModeOn()){
            	Toast.makeText(this, R.string.guest_mode_view, Toast.LENGTH_SHORT).show();
            	finish();
            }else{
            mNote.mId = noteId;
            mLoadNoteAsyncTask = new LoadNoteAsyncTask();
            mLoadNoteAsyncTask.execute(noteId);
            
            isView = true;
            
            }
        } else {
            if(NoteUtils.isGuestModeOn()){
            	Toast.makeText(this, R.string.guest_mode_creat, Toast.LENGTH_SHORT).show();
            	finish();
            }else{

            //PR 850747.It will display the blank note after reboot phone on editing screen.Modified by hz_nanbing.zou at 03/12/2014 begin
            createNewNote();
            //PR 850747.It will display the blank note after reboot phone on editing screen.Modified by hz_nanbing.zou at 03/12/2014 end	

            textFromQR = intent.getStringExtra("text");
            
            TctLog.d("nb","new: "+textFromQR);
            //PR824190 Keyboard not pop up auto when enter from widget.Modified by hz_nanbing.zou at 19/11/2014 begin
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            //PR824190 Keyboard not pop up auto when enter from widget.Modified by hz_nanbing.zou at 19/11/2014 end
            
            }
        }
        //PR833528 Guest_Mode not perform note.Added by hz_nanbing.zou at 17/14/2014 end
        
		//PR879516 .The note editor will response slowly after dome some operations.Added by hz_nanbing.zou at 23/12/2014 begin
        this.mTelephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        this.mTelephonyManager.listen(this.mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
		//PR879516 .The note editor will response slowly after dome some operations.Added by hz_nanbing.zou at 23/12/2014 end
        
        
        // PR 824190 keyboard pop up. Modified by hz_nanbing_zou at 30/10/2014 end

        //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
//        mbr = new MusicBrocastReceiver();
//        IntentFilter iflt = new IntentFilter();
//        iflt.addAction("audioplay");
//        iflt.addAction("audioplayend");
//        this.registerReceiver(mbr, iflt);
        //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
        
        ActionBar actionBar = getActionBar();
        editorView = LayoutInflater.from(this).inflate(R.layout.custom_action_bar2, null);
        imageview = (ImageView) findViewById(R.id.audio_view);
        date_view = (TextView) findViewById(R.id.date);
        
        tap_view = 	(TextView) findViewById(R.id.tap);
        
        tap_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				TctLog.d("nb","tap click");
                //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-7,PR888129
                if(Playstate == STATE_START_PLAY){
                      Playstate=STATE_STOP_OR_PAUSE_PLAY;
                      for(int i=0;i<itemView.size();i++){
                        audioListView audiolistview = (audioListView) itemView.get(i).getTag();
                        audiolistview.audioPlay.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
                        audiolistview.start_time.setVisibility(View.GONE);
                        audiolistview.total_time.setVisibility(View.GONE);
                        audiolistview.total_time_before.setVisibility(View.VISIBLE);
                        }
                      mHandler.removeMessages(MSG_WHAT_UPDATE_PLAY_PROGRESS);
                      MediaPause = true;
                       if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                           mMediaPlayer.pause();
                           //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                           sendBroadcast(new Intent("audioplayend"));
                           //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                       }
                }
              //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-7,PR888129
				
				
				
                //[BUGFIX]-ADD-BEGIN by wenlu.wu,11/26/2013,PR-558068
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    date_view.setVisibility(View.GONE);
                    Toast.makeText(NoteEditorActivity.this, R.string.insert_sd_card, Toast.LENGTH_LONG).show();
                    return;
                }
                
                //PR900585 FC at Add record when get a call.Added by hz_nanbing.zou at 14/01/2015 begin 
                if(mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
                	Toast.makeText(NoteEditorActivity.this, R.string.record_incall, Toast.LENGTH_SHORT).show();
                	return;
                }
                //PR900585 FC at Add record when get a call.Added by hz_nanbing.zou at 14/01/2015 end
                
                date_view.setVisibility(View.VISIBLE);
                //[BUGFIX]-ADD-END by wenlu.wu,11/26/2013,PR-558068
                stopAudio.setBackgroundResource(R.drawable.recording);
                onStartRecord();
                Recordstate = STATE_START_RECORD;
                // TctLog.e(TAG, "state222: " + Recordstate);
                
                //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                sendBroadcast(new Intent("audioplay"));
                //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end

                tap_view.setVisibility(View.GONE); 
                stopAudio.setVisibility(View.VISIBLE);
                imageview.setVisibility(View.GONE);
            
			}
		});
        
        //Added for idol3
        edit_add = (TextView) editorView.findViewById(R.id.editor_add);
        //Added for idol3
        
        
        mRecord_Layout = (RelativeLayout)findViewById(R.id.record_layout);
        
        stopAudio = (Button) findViewById(R.id.stop_audio_item);
        saveItem = (ImageButton) editorView.findViewById(R.id.done_menu_item);

        saveItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	//PR844391  When recording,click save,record can not be save. Added by hz_nanbing.zou at 11/11/2014 begin
                if (mMediaRecorder != null) {
                	TctLog.d("nb", "mMediaRecorder");
                    onStopRecord();
                    list.setVisibility(View.VISIBLE);
//                    list.setAdapter(adapter);
                    
                    
                    setRecorderList();

                    stopAudio.setBackgroundResource(R.drawable.audio_start_record_02);
                    imageview.setVisibility(View.GONE);
                    date_view.setVisibility(View.GONE);
                    stopAudio.setVisibility(View.GONE);
                    
                    mRecord_Layout.setVisibility(View.GONE);
                    
                    //colorItem.setVisibility(View.VISIBLE);
                    //attachItem.setVisibility(View.VISIBLE);
                    Recordstate = STATE_STOP_RECORD;
                }
                //PR844391  When recording,click save,record can not be save. Added by hz_nanbing.zou at 11/11/2014 end
                
                //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                sendBroadcast(new Intent("audioplayend"));
                //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
            	
                saveAll();
            }
        });
/*        attachItem = (ImageButton) editorView.findViewById(R.id.add_attachment_item);
        attachItem.setVisibility(View.VISIBLE);
        attachItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                colorItem.setBackgroundResource(R.drawable.note_action_bar_bg);
                attachItem.setBackgroundResource(R.drawable.note_action_bar_bg);
                ((ImageView) editorView.findViewById(R.id.add_attachment_flag))
                        .setVisibility(View.VISIBLE);
                ((ImageView) editorView.findViewById(R.id.change_bg_flag)).setVisibility(View.GONE);
               // initAttachPopupWindow();
            }
        });*/
        deleteReminder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditorActivity.this);
                builder.setMessage(getString(R.string.delete_item))
                        .setPositiveButton(getString(R.string.delete_menu_title),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        (findViewById(R.id.reminder_layout))
                                                .setVisibility(View.GONE);
                                        Cursor c = getContentResolver().query(
                                                Note.Reminder.CONTENT_URI, null,
                                                Note.Reminder.COLUMN_NOTE_ID + " =" + mNote.mId,
                                                null, null, null);
                                        if (c != null && c.getCount() > 0) {
                                            getContentResolver().delete(Note.Reminder.CONTENT_URI,
                                                    Note.Reminder.COLUMN_NOTE_ID + "=" + mNote.mId,
                                                    null);
                                        }
                                        if (c != null)
                                            c.close();
                                        if (mRminder != null)
                                            mRminder.mReminderTime = NO_SET_REMINDER;
                                        newReminderTime = 0;//[BUGFIX]-by TSCD(yanmei.zhang),06/09/2014,PR692135
                                        mRminder = null;

                                        //PR881769.The reminder lost when add reminder.Modified by hz_nanbing.zou at 23/12/2014 begin
                                        mOldReminderId=0;
                                        //PR881769.The reminder lost when add reminder.Modified by hz_nanbing.zou at 23/12/2014 end

//                                        deleteReminder.setVisibility(View.GONE);
                                        
                                        
                                        cancelAlarm();

                                        //modify by mingyue.wang for pr881920 begin 
                                       SharedPreferences isCancleShare = NoteEditorActivity.this.getSharedPreferences("isCancel", NoteEditorActivity.this.MODE_PRIVATE);
                                       isCancleShare.edit().putBoolean("isCancel"+mNote.mId, true).apply();
                                       //modify by mingyue.wang for pr881920 end 
                                        
                                        //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//                                        cancelNotify();
                                        //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

                                    }

                                }).setNegativeButton(getString(R.string.btn_cancel), null).show();
            }

        });
        stopAudio.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                date_view.setText(timeToString(0));
                TctLog.e(TAG, "state: " + Recordstate);
                //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-7,PR888129
                if(Playstate == STATE_START_PLAY){
                      Playstate=STATE_STOP_OR_PAUSE_PLAY;
                      for(int i=0;i<itemView.size();i++){
                        audioListView audiolistview = (audioListView) itemView.get(i).getTag();
                        audiolistview.audioPlay.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
                        audiolistview.start_time.setVisibility(View.GONE);
                        audiolistview.total_time.setVisibility(View.GONE);
                        audiolistview.total_time_before.setVisibility(View.VISIBLE);
                        }
                      mHandler.removeMessages(MSG_WHAT_UPDATE_PLAY_PROGRESS);
                      MediaPause = true;
                       if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                           mMediaPlayer.pause();
                           //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                           sendBroadcast(new Intent("audioplayend"));
                           //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                       }
                }
              //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-7,PR888129
                if (Recordstate == STATE_STOP_RECORD) {
                    //[BUGFIX]-ADD-BEGIN by wenlu.wu,11/26/2013,PR-558068
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        date_view.setVisibility(View.GONE);
                        Toast.makeText(NoteEditorActivity.this, R.string.insert_sd_card, Toast.LENGTH_LONG).show();
                        return;
                    }
                    date_view.setVisibility(View.VISIBLE);
                    //[BUGFIX]-ADD-END by wenlu.wu,11/26/2013,PR-558068
                    stopAudio.setBackgroundResource(R.drawable.recording);
                    onStartRecord();
                    Recordstate = STATE_START_RECORD;
                    // TctLog.e(TAG, "state222: " + Recordstate);
                    
                    //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                    sendBroadcast(new Intent("audioplay"));
                    //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                    
                } else if (Recordstate == STATE_START_RECORD) {
                    onStopRecord();
                    TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
                    list.setVisibility(View.VISIBLE);
//                    list.setAdapter(adapter);

                    setRecorderList();
                    
                    stopAudio.setBackgroundResource(R.drawable.audio_click);
                    imageview.setVisibility(View.GONE);
                    date_view.setVisibility(View.GONE);
                    stopAudio.setVisibility(View.GONE);
                    
                    mRecord_Layout.setVisibility(View.GONE);
                    
                    //colorItem.setVisibility(View.VISIBLE);
                   // attachItem.setVisibility(View.VISIBLE);
                    Recordstate = STATE_STOP_RECORD;
                    
                    
                    edit_add.setVisibility(View.VISIBLE);
                    menus.setGroupVisible(Menu.NONE, true);
                    
                    //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                    sendBroadcast(new Intent("audioplayend"));
                    //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                    

                }

            }

        });
        /*colorItem = (ImageButton) editorView.findViewById(R.id.change_bg_item);
        colorItem.setVisibility(View.VISIBLE);
        colorItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                //attachItem.setBackgroundResource(R.drawable.note_action_bar_bg);
                colorItem.setBackgroundResource(R.drawable.note_action_bar_bg);
                ((ImageView) editorView.findViewById(R.id.add_attachment_flag))
                        .setVisibility(View.GONE);
                ((ImageView) editorView.findViewById(R.id.change_bg_flag))
                        .setVisibility(View.VISIBLE);
               // initColorPopupWindow();
            }
        });*/

		 findViewById(R.id.reminder_date).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 setDatePicker(2);
			}
		});
		findViewById(R.id.reminder_time).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTimePicker(2);
			}
		});
        
        //PR906486 Tap to reset reminder.Added by hz_nanbing.zou at 19/01/2015 begin
//        (findViewById(R.id.reminder_layout)).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//                // [BUGFIX]-Mod-BEGIN by TCTNB.YuTao.Yang,09/24/2013,PR527679,
//                // [Notes]The reminder display abnormity after modify it
//                if (mRminder == null) {
//                    mRminder = new Note.Reminder();
//                    mRminder.mReminderTime = NO_SET_REMINDER;
//                    TctLog.i(TAG, "mRminder == null");
//                }
//                // [BUGFIX]-Mod-END by TCTNB.YuTao.Yang
//                
//                //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
//                isOtherActivity = true;
//                //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
//                
////                Intent intent = new Intent();
////                intent.setClass(NoteEditorActivity.this, ReminderSettingActivity.class);
////                startActivityForResult(intent, REQUEST_CODE_PICK_REMINDER);
//                
//                setDatePicker(2);
//			}
//		});
        //PR906486 Tap to reset reminder.Added by hz_nanbing.zou at 19/01/2015 end
        
/*        (findViewById(R.id.reminder_layout)).setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
//                displayDeleteItem();

                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditorActivity.this);
                builder.setMessage(getString(R.string.delete_item))
                        .setPositiveButton(getString(R.string.btn_ok),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        (findViewById(R.id.reminder_layout))
                                                .setVisibility(View.GONE);
                                        Cursor c = getContentResolver().query(
                                                Note.Reminder.CONTENT_URI, null,
                                                Note.Reminder.COLUMN_NOTE_ID + " =" + mNote.mId,
                                                null, null, null);
                                        if (c != null && c.getCount() > 0) {
                                            getContentResolver().delete(Note.Reminder.CONTENT_URI,
                                                    Note.Reminder.COLUMN_NOTE_ID + "=" + mNote.mId,
                                                    null);
                                        }
                                        if (c != null)
                                            c.close();
                                        if (mRminder != null)
                                            mRminder.mReminderTime = NO_SET_REMINDER;
                                        newReminderTime = 0;//[BUGFIX]-by TSCD(yanmei.zhang),06/09/2014,PR692135
                                        mRminder = null;

                                        //PR881769.The reminder lost when add reminder.Modified by hz_nanbing.zou at 23/12/2014 begin
                                        mOldReminderId=0;
                                        //PR881769.The reminder lost when add reminder.Modified by hz_nanbing.zou at 23/12/2014 end

                                        deleteReminder.setVisibility(View.GONE);
                                        
                                        
                                        cancelAlarm();

                                        //modify by mingyue.wang for pr881920 begin 
                                       SharedPreferences isCancleShare = NoteEditorActivity.this.getSharedPreferences("isCancel", NoteEditorActivity.this.MODE_PRIVATE);
                                       isCancleShare.edit().putBoolean("isCancel"+mNote.mId, true).apply();
                                       //modify by mingyue.wang for pr881920 end 
                                        
                                        //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//                                        cancelNotify();
                                        //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

                                    }

                                }).setNegativeButton(getString(R.string.btn_cancel), null).show();
            
                return false;
            }

        });*/
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(editorView);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
        initEditor();
        //[BUGFIX]-Add-BEGIN by TCTNB.Tang.Ding,11/11/2013,551800,
        //[Gallery]Fail to share the image from gallery by notes
        if(Intent.ACTION_SEND.equals(intent.getAction())){
                handleTakeOrPickImage(intent,false);
        }
        //[BUGFIX]-Add-END by TCTNB.Tang.Ding  
        
        
      //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
        audioManager = (AudioManager) NoteEditorActivity.this.getSystemService(Context.AUDIO_SERVICE);
        mfl = new MediaFocusListener();
      //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end
        

        //Added for View mode.
        setViewMode(isView);
        //Added for View mode.

        
    }
    private void initEditor() {
        mNoteEditor.setNoteId(mNote.mId);
        String str = loadTextandImage();
        
        if(textFromQR != "")
        	str = textFromQR;
        
        mNoteEditor.setRichText(str);
        if (TextUtils.isEmpty(str))
            mNoteEditor.setCursorVisible(true);
        
        
        AttachmentUtils.setTextInfo(mText, /*mNoteEditor,*/ mNote.mId);//ForPR946939\965000
    }

    private String loadTextandImage() {
        String content = "";
        Cursor c = null ;
        if(mNote != null)//For PR1005708
        c= getContentResolver().query(Note.Text.CONTENT_URI, new String[] {
            Note.Text.COLUMN_CONTENT
        }, Note.Text.COLUMN_NOTE_ID + " = " + mNote.mId, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            content = c.getString(c.getColumnIndex(Note.Text.COLUMN_CONTENT));
        }
        if (c != null)
            c.close();
        return content;
    }

//    audioListView audiolistview;
//    public void displayDeleteItem() {
//        deleteReminder.setVisibility(View.VISIBLE);
//        if (itemView != null) {
//            for (int i = 0; i < itemView.size(); i++) {
//                 audiolistview = (audioListView) itemView.get(i).getTag();
//                if (audiolistview != null)
//                    audiolistview.deleteItem.setVisibility(View.VISIBLE);
//            }
//        }
//        DeleteDisplay = true;
//    }
    
    
    audioListView audiolistview;
    public void displayDeleteItem(boolean isView) {
    	
    	if(isView){
            deleteReminder.setVisibility(View.GONE);
            if (itemView != null) {
                for (int i = 0; i < itemView.size(); i++) {
                     audiolistview = (audioListView) itemView.get(i).getTag();
                    if (audiolistview != null)
                        audiolistview.deleteItem.setVisibility(View.GONE);
                }
            }
    	}else{
            deleteReminder.setVisibility(View.VISIBLE);
            if (itemView != null) {
                for (int i = 0; i < itemView.size(); i++) {
                     audiolistview = (audioListView) itemView.get(i).getTag();
                    if (audiolistview != null)
                        audiolistview.deleteItem.setVisibility(View.VISIBLE);
                }
            }
    	}
    	
    }

    @Override
    public void onBackPressed() {
        if (DeleteDisplay) {
            deleteReminder.setVisibility(View.GONE);
            if (itemView != null) {
                for (int i = 0; i < itemView.size(); i++) {
                    audioListView audiolistview = (audioListView) itemView.get(i).getTag();
                    if (audiolistview != null)
                        audiolistview.deleteItem.setVisibility(View.INVISIBLE);
                }
            }
            DeleteDisplay = false;
            return;
        }
        TctLog.d("nb", "mMediaRecorder.."+mMediaRecorder);
        if (mMediaRecorder != null) {
        	TctLog.d("nb", "back");
            onStopRecord();
            list.setVisibility(View.VISIBLE);
//            list.setAdapter(adapter);

            setRecorderList();
            
            stopAudio.setBackgroundResource(R.drawable.audio_start_record_02);
            imageview.setVisibility(View.GONE);
            date_view.setVisibility(View.GONE);
            stopAudio.setVisibility(View.GONE);
            
            mRecord_Layout.setVisibility(View.GONE);
            
            //colorItem.setVisibility(View.VISIBLE);
            //attachItem.setVisibility(View.VISIBLE);
            Recordstate = STATE_STOP_RECORD;
        } else {
            super.onBackPressed();
            saveAll();
        }
    }
  //[BUGFIX]-Add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
    public boolean isHasNoChange() {
        boolean reminderNoChange = (mRminder != null && newReminderTime == mRminder.mReminderTime)|| (newReminderTime == -1);
        boolean audioNoChange = (mNewAudio == null) && (AudioCount == mCacheAudioList.size());
        boolean TextNoChange = loadTextandImage().equals(getWorkingText());//[BUGFIX]-Mod-BEGIN by TCTNB.Yan.TENG,02/24/2014,PR608128
        return reminderNoChange && audioNoChange && TextNoChange;
    }
  //[BUGFIX]-Add-END by TSCD(yanmei.zhang),06/09/2014,PR692135,

    public void saveAll() {
      //[BUGFIX]-Add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
    	
    	//PR 850747.It will display the blank note after reboot phone on editing screen.Modified by hz_nanbing.zou at 03/12/2014 begin
    	//createNewNote();
    	//PR 850747.It will display the blank note after reboot phone on editing screen.Modified by hz_nanbing.zou at 03/12/2014 end
    	
    	
    	
    	
    	
    	if(!isEnoughSpace(1)){//For PR980628
    		Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.msg_devices_no_space), Toast.LENGTH_SHORT).show();
    		DeleteNote(getContentResolver());
    		finish();
    		return;
    	}
    	
    	mNoteEditor.updateCut();
    	
    	TctLog.d("nb", "empty.."+isEmpty());
        if (isEmpty()) {
        	
        	//PR884271. Reminder can be ring after save empty note.Added by hz_nanbing.zou at 31/12/2014 begin
        	if(mNote.mHasReminder){
        		cancelAlarm();
        	}
        	//PR884271. Reminder can be ring after save empty note.Added by hz_nanbing.zou at 31/12/2014 end
        	
            DeleteNote(getContentResolver());
            Toast.makeText(NoteEditorActivity.this, R.string.save_empty, Toast.LENGTH_LONG).show();//[BUGFIX]-MOd-BEGIN by TCTNB.Yan.TENG,01/02/2014,PR583139
            finish();
            return;
        }
        if (isHasNoChange()) {
          //[BUGFIX]-Add-BEGIN by TCTNB.dongjin,06/18/2014,706375,
          //Notes background color can't be changed after change it
            ContentResolver resolver = getContentResolver();
            dosaveText(mNote.mId, resolver);
            mNote.mHasText = IsHasPlainText();

            //PR911676.After delete pic at only has record&&image,it will display pic icon.Added by hz_nanbing.zou at 26/01/2015 begin
            mNote.mHasImage = IshasImage();
            //PR911676.After delete pic at only has record&&image,it will display pic icon.Added by hz_nanbing.zou at 26/01/2015 end

            mNote.mModifyTime = System.currentTimeMillis();
            ContentValues values = mNote.toContentValues();
            resolver.update(Note.CONTENT_URI, values, Note.COLUMN_ID + " = " + mNote.mId, null);
            updateGroup(resolver);
            
            Intent i = new Intent();
            i.setAction("com.tct.note.widget.UPDATE");
            i.putExtra("note_id", mNote.mId);
            this.sendBroadcast(i);
            
          //[BUGFIX]-Add-END by TCTNB.dongjin
            TctLog.i(TAG, "isHasNoChange ==== true");
            finish();
            return;
        }
      //[BUGFIX]-Add-END by by TSCD(yanmei.zhang),06/09/2014,PR692135,
        ContentResolver resolver = getContentResolver();
      //[BUGFIX]-Add-BEGIN by TCTNB.dongjin,06/21/2014,711267,
      //[Notes]Reminders will disppear after save record audio
        
            //PR841614  Delete reminder at enter from a note which had a reminder,the main screen still had reminder icon.Fixed by hz_nanbing.zou at 21/11/2014 begin 
        if(mNote != null)//for defect 290271    
        mNote.mHasReminder = (mRminder != null) || ((newReminderTime != NO_SET_REMINDER)
            		&& (newReminderTime !=0));//[BUGFIX]-MOd-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
            //PR841614  Delete reminder at enter from a note which had a reminder,the main screen still had reminder icon.Fixed by hz_nanbing.zou at 21/11/2014 end

            //PR870206 Back key from Reminder setting,the main screen still had reminder icon.Fixed by hz_nanbing.zou at 11/12/2014 begin
            if(newReminderTime == NO_SET_REMINDER){
            	if(mOldReminderId==0)
            	mNote.mHasReminder =false;
            }

            //PR875884 Back key from Reminder twice setting,the main screen still had reminder icon.Fixed by hz_nanbing.zou at 19/12/2014 begin
            if(newReminderTime == 0 && mOldReminderId==0){
            	mNote.mHasReminder =false;
            }
            //PR875884 Back key from Reminder twice setting,the main screen still had reminder icon.Fixed by hz_nanbing.zou at 19/12/2014 end 

          //PR870206 Back key from Reminder setting,the main screen still had reminder icon.Fixed by hz_nanbing.zou at 11/12/2014 end

      //[BUGFIX]-Add-END by TCTNB.dongjin
            mNote.mHasAudio = mCacheAudioList.size() > 0;
            if (mNote.mHasReminder == true) {
                dosaveReminder(mNote.mId, resolver);
                if ((mRminder != null) && (mRminder.mReminderTime > System.currentTimeMillis())) {
                    setAlarm();

                    //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//                    sendNotify();
                    //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

                }
            }
            if (mNote.mHasAudio == true)
                dosaveAudio(mNote.mId, resolver);
            mNote.mHasImage = IshasImage();
            dosaveText(mNote.mId, resolver);
            mNote.mHasText = IsHasPlainText();
            mNote.mModifyTime = System.currentTimeMillis();
            ContentValues values = mNote.toContentValues();
            resolver.update(Note.CONTENT_URI, values, Note.COLUMN_ID + " = " + mNote.mId, null);
            updateGroup(resolver);
            // widget
          //[BUGFIX]-Add-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
            TctLog.e(TAG, "mNote.isWidgetNote: " + mNote.WidgetNoteId);

            // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 begin
            if (true) {
              SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",
                        Context.MODE_PRIVATE);
                widgetNoteMap.edit().putLong("widget", mNote.mId).apply();
            // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 begin

                Intent widgetIntent = new Intent("com.tct.note.widget.UPDATE");
//                widgetIntent.setComponent(NoteAppWidgetProvider.COMPONENT);
                widgetIntent.putExtra(Constants.EXTRA_NOTE_ID, mNote.mId);
                widgetIntent.putExtra("from_editor", true);
                sendBroadcast(widgetIntent);
            }
            
            Intent i = new Intent();
            i.setAction(UpdateWidgetService.DATA_CHANGED);
            i.putExtra("note_id", mNote.mId);
            this.sendBroadcast(i);
          //[BUGFIX]-Add-END by TSCD(yanmei.zhang),06/09/2014,PR692135,

            //PR851521 Home key save note prompt.Added by hz_nanbing.zou at 25/11/2014 begin
            if(isHomeKey){
            	Toast.makeText(NoteEditorActivity.this, R.string.homekey_save, Toast.LENGTH_LONG).show();
            }
            //PR851521 Home key save note prompt.Added by hz_nanbing.zou at 25/11/2014 end

        finish();
    }

    public void DeleteNote(ContentResolver resolver) {
        resolver.delete(Note.CONTENT_URI, Note.COLUMN_ID + " = " + mNote.mId, null);

        //PR955752.The count is wrong when select note.Added by hz_nanbing.zou at 2015/03/20 begin
        updateGroup(resolver);
        //PR955752.The count is wrong when select note.Added by hz_nanbing.zou at 2015/03/20 end
    }

    public boolean IshasImage() {
        Cursor c = getContentResolver().query(Note.Image.CONTENT_URI, null,
                Note.Image.COLUMN_NOTE_ID + " = " + mNote.mId, null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return true;
        } else {
            if (c != null)
                c.close();
            return false;
        }
    }

    //PR839784.Input space can be save.Modified by hz_nanbing.zou at 17/11/2014 begin
    public boolean isEmpty() {
/*        return (mImageList.size() <= 0)
                && (getWorkingText().length() <= 0 || "".equals(getWorkingText().toString().trim()))
                && (mCacheAudioList.size() <= 0)
                && (mRminder == null || (mRminder != null && mRminder.mReminderTime == NO_SET_REMINDER))*/;

        //PR 873206 .Can not save only reminder.Modified by hz_nanbing.zou at 19/12/2014 begin
        //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-11,PR896382
        return (mImageList.size() <= 0)
                && (getWorkingText().length() <= 0 || "".equals(getWorkingText().toString().trim()))
                && (mCacheAudioList.size() <= 0)
               /* && (mRminder == null || (mRminder != null && newReminderTime == NO_SET_REMINDER))*/;
         //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-11,PR896382
        //PR 873206 .Can not save only reminder.Modified by hz_nanbing.zou at 19/12/2014 end

    }
    //PR839784.Input space can be save.Modified by hz_nanbing.zou at 17/11/2014 end

    public void setAlarm() {
        Intent i = new Intent(NoteEditorActivity.this, ReminderReceiver.class);
        i.setData(ContentUris.withAppendedId(Note.CONTENT_URI, mNote.mId));
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent p = PendingIntent.getBroadcast(NoteEditorActivity.this, 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, mRminder.mReminderTime, p);
    }

    public void cancelAlarm() {
        Intent i = new Intent(NoteEditorActivity.this, ReminderReceiver.class);
        i.setData(ContentUris.withAppendedId(Note.CONTENT_URI, mNote.mId));
        PendingIntent p = PendingIntent.getBroadcast(NoteEditorActivity.this, 0, i, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(p);
    }

    private String getWorkingText() {
        String str = mNoteEditor.getRichText().toString();
        return str;
    }

    public void createNewNote() {
        ContentResolver resolver = getContentResolver();
        mNote.mModifyTime = System.currentTimeMillis();
        ContentValues values = mNote.toContentValues();
        Uri uri = resolver.insert(Note.CONTENT_URI, values);
        if (uri != null) {
            mNote.mId = Long.valueOf(uri.getLastPathSegment());
        }
    }

    public void dosaveText(long noteId, ContentResolver resolver) {
        mText.mNoteId = mNote.mId;
        mText.mContent = getWorkingText();
        mText.mText = mNoteEditor.getplainText().toString();
        ContentValues values = mText.toContentValues();
        if (!IsHasText()) {
            Uri uri = resolver.insert(Note.Text.CONTENT_URI, values);
            if (uri != null) {
                mText.mId = Long.valueOf(uri.getLastPathSegment());
            }
        } else {
            resolver.update(Note.Text.CONTENT_URI, values, Note.Text.COLUMN_NOTE_ID + " = "
                    + mText.mNoteId, null);
        }
    }

    private boolean IsHasPlainText() {
        Cursor c = getContentResolver().query(
                Note.Text.CONTENT_URI,
                null,
                Note.Text.COLUMN_NOTE_ID + " = " + mNote.mId + " and  " + Note.Text.COLUMN_TEXT
                        + " is not null ", null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return true;
        } else {
            if (c != null)
                c.close();
            return false;
        }
    }

    private boolean IsHasText() {
        Cursor c = getContentResolver().query(Note.Text.CONTENT_URI, null,
                Note.Text.COLUMN_NOTE_ID + " = " + mNote.mId, null, null);
        if (c != null && c.getCount() > 0) {
            c.close();
            return true;
        } else {
            if (c != null)
                c.close();
            return false;
        }
    }

    public long getbgCount(long id) {
        long count = 0;
        Cursor noteCursor = this.getContentResolver().query(Note.CONTENT_URI, null,
                Note.COLUMN_GROUP_ID + " = " + id, null, null);
        if (noteCursor != null && noteCursor.getCount() > 0) {
            count = noteCursor.getCount();
        }
        if (noteCursor != null)
            noteCursor.close();
        return count;
    }

    public void updateGroup(ContentResolver resolver) {
        ContentValues defaultValues = new ContentValues();
        defaultValues.put(Group.COLUMN_NAME, getbgCount(mNote.mGroupId));
        resolver.update(Note.Group.CONTENT_URI, defaultValues, Note.Group.COLUMN_ID + " = "
                + mNote.mGroupId, null);

        //[BUGFIX]-Add-BEGIN by TSCD.gerong,07/18/2014,PR-692034,
        //Number of Notes display error after sort by color
        if ((mOldGroudId != 0) && (mOldGroudId != mNote.mGroupId)) {
             defaultValues.put(Group.COLUMN_NAME, getbgCount(mOldGroudId));
             resolver.update(Note.Group.CONTENT_URI, defaultValues, Note.Group.COLUMN_ID + " = "
                + mOldGroudId, null);
        }
        //[BUGFIX]-Add-END by TSCD.gerong
    }

    public void dosaveReminder(long noteId, ContentResolver resolver) {
        if (mRminder != null && newReminderTime > NO_SET_REMINDER) {
            mRminder.mNoteId = noteId;
            if(newReminderTime !=0)//for PR958445
            mRminder.mReminderTime = newReminderTime;//[BUGFIX]-MOd-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
            ContentValues values = mRminder.toContentValues();
            if (mOldReminderId == 0) {
                Uri uri = resolver.insert(Note.Reminder.CONTENT_URI, values);
                if (uri != null) {
                    mRminder.mId = Long.valueOf(uri.getLastPathSegment());
                }
            } else {
                resolver.update(Note.Reminder.CONTENT_URI, values, Note.Reminder.COLUMN_NOTE_ID
                        + " = " + mRminder.mNoteId, null);

                //PR854251 Modified the same note,reminder can not be overwrite.Added by hz_nanbing.zou at 27/11/2014 begin 
                Intent it = new Intent("reminder_overwrite");
    			sendBroadcast(it);
    			//PR854251 Modified the same note,reminder can not be overwrite.Added by hz_nanbing.zou at 27/11/2014 end 

            }
        }
    }

    public void dosaveAudio(long noteId, ContentResolver resolver) {
        for (int i = 0; i < mCacheAudioList.size(); i++) {
            mCacheAudioList.get(i).mNoteId = noteId;
            ContentValues values = mCacheAudioList.get(i).toContentValues();
            if (mCacheAudioList.get(i).mId == 0) {
                Uri uri = resolver.insert(Note.Audio.CONTENT_URI, values);
                if (uri != null) {
                    mCacheAudioList.get(i).mId = Long.valueOf(uri.getLastPathSegment());
                }
            } else {
                resolver.update(Note.Audio.CONTENT_URI, values, Note.Audio.COLUMN_ID + " = "
                        + mCacheAudioList.get(i).mId, null);
            }
        }
    }


    private String timeToString(int second) {
    	int hour =second / 3600;
    	
    	second = second % 3600;
    	
        int minite = second / 60;
        second = second % 60;

        StringBuilder strBuilder = new StringBuilder();
        if(hour > 0)
        	strBuilder.append(hour < 10 ? "0" : "").append(hour).append(":");//For PR966478
        strBuilder.append(minite < 10 ? "0" : "").append(minite).append(":");
        strBuilder.append(second < 10 ? "0" : "").append(second);
        return strBuilder.toString();
    }
    
    private int getDuration(){
        if ((mNewAudio != null) && (mNewAudio.mUri != null)) {
            //PR900713 modified by Gu Feilong at 2015-01-19 start
        	Uri uri = Uri.parse(mNewAudio.mUri);
        	
        	if(uri != null){//For PR1003611
        	
            MediaPlayer tMediaPlayer = null;
            try{//For PR1025122
            tMediaPlayer = MediaPlayer.create(NoteEditorActivity.this,uri );
            }catch(Exception e){
            	TctLog.e("nb", e.toString());
            	if(tMediaPlayer != null){
                    tMediaPlayer.reset();
                    tMediaPlayer.release();
                    tMediaPlayer = null;
            	}
            }
            
            if(tMediaPlayer != null){
            int duration = (tMediaPlayer.getDuration()) / 1000;
            if (tMediaPlayer.getDuration() % 1000 != 0) {
                duration += 1;
            }
            tMediaPlayer.reset();
            tMediaPlayer.release();
            return duration;
            }else{
            	File f = new File(mNewAudio.mUri);
            	if(!f.exists())
            		return -1;
            }
        	}else
        		return 0;
        }
        return 0;
    }
    
    private int getHasFile(){

    	if ((mNewAudio != null) && (mNewAudio.mUri != null)){
    	File f = new File(mNewAudio.mUri);
    	if(!f.exists())
    		return -1;
    	}
    	return 0;
    }
    
    protected void onStartRecord() {
    	//For PR999702
    	audioManager.requestAudioFocus(mfl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    	
        try {
        	
        	if(!isEnoughSpace(0)){//For PR980628
        		Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.msg_devices_no_space), Toast.LENGTH_SHORT).show();
        		if(d != null)
        			d.dismiss();
        		return;
        	}
        	
        	isTimeout = false;
        	
            if (mMediaRecorder == null)
                mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //PR903993 modified by Gu Feilong at 2015-01-19 start
            mMediaRecorder.setMaxDuration((4*60*60)*1000);
            
            mMediaRecorder.setOnInfoListener(new OnInfoListener(){

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {
					// TODO Auto-generated method stub
					switch(what){
					case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
						timeOutStopRecord();
						break;
					}
				}

            });
            //PR903993 modified by Gu Feilong at 2015-01-19 end
            mNewAudio = new Note.Audio();
            if (mNewAudio.mUri == null) {
                long timeMillis = System.currentTimeMillis();
                while (true) {

                	//PR836588  Can't play record with file manager.Modified by hz_nanbing.zou at 14/11/2014 begin
                    //mNewAudio.mUri = Constants.AUDIO_DIR + "/" + timeMillis + ".3pg";
                    mNewAudio.mUri = Constants.AUDIO_DIR + "/" + timeMillis + ".amr";
                    //PR836588  Can't play record with file manager.Modified by hz_nanbing.zou at 14/11/2014 begin

                    try {
                        File file = new File(mNewAudio.mUri);
                        if (!file.exists()) {
                            new File(file.getParent()).mkdirs();
                            file.createNewFile();
                            break;
                        }
                    } catch (IOException e) {
                        TctLog.d(TAG, e.toString(), e);
                    }
                    timeMillis++;
                }
            }
            
            
            TctLog.e(TAG, "mNewAudio.mUri: " + mNewAudio.mUri);
            mMediaRecorder.setOutputFile(mNewAudio.mUri);

            illegalFlag = false;//For PR1017406
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            Message msg = new Message();
            msg.what = MSG_WHAT_SWITCH_RECORD_ICON;
            msg.arg1 = 0;
            mHandler.sendMessage(msg);

            return;
        } catch (Exception e) {
            TctLog.d(TAG, e.toString(), e);  
            //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 begin
            illegalFlag = true;
            mHandler.sendEmptyMessage(STATE_RECORD_ERROR);
            //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 end

        } /*catch (IOException e) {
            TctLog.d(TAG, e.toString(), e);
        }*/
    }

    protected void onPauseRecord(){
    	
    	
    	mHandler.removeMessages(MSG_WHAT_SWITCH_RECORD_ICON);
//    	tempTime = getDuration();//For PR967021
    	
    	
//    	vRecordTime.setText(timeToString(tempTime));
    	
    	mHandler.removeMessages(MSG_WHAT_SHOW_PAUSE_DURATION);
    	mHandler.sendEmptyMessageDelayed(MSG_WHAT_SHOW_PAUSE_DURATION, 500);
    	
//    	tempTime = getDuration();//For PR967021
    	
    	if(illegalFlag){
    		illegalFlag = false;
    		return;
    	}
    	if (mMediaRecorder != null){
    		Class cls;
			try {
				cls = Class.forName("android.media.MediaRecorder");
	    		Method mtd;
				try {/*
					mtd = cls.getDeclaredMethod("pause");
					try {
						mtd.invoke(mMediaRecorder);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				*/
			          if(!invoke(cls,"pause",mMediaRecorder)){
			              if(!invoke(cls,"setParametersExtra",mMediaRecorder)){
			                invoke(cls,"tct_pause",mMediaRecorder);
			              }
			           }	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    }
    //Modified this invoke function for different platform
    public boolean invoke(Class cls,String mtdname,MediaRecorder recorder){
        try{
           if(mtdname.equals("setParametersExtra")){
              Method mtd = cls.getDeclaredMethod(mtdname,String.class);
              mtd.invoke(recorder,"media-param-pause=1");
           }else{
              Method mtd = cls.getDeclaredMethod(mtdname);
              mtd.invoke(recorder);
           }
        }catch (Exception e) {
             return false;
        }
             return true;
   }
    protected void onStopRecord() {
        //[BUGFIX]-Mod-BEGIN by TSCD.gerong,08/04/2014,PR-754288,
        //Note force close unexpectedly
    	
    	//PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 begin
    	if(illegalFlag){
    		illegalFlag = false;
    		return;
    	}
    	//PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 end
    	
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e){//modify by mingyue.wang for pr924240
                TctLog.e(TAG, e.toString(), e);
                
                return;
                
            } finally {
            	// 这里之前Activity是间接的被强引用，这里置空
            	mMediaRecorder.setOnInfoListener(null);//added by xinyao.ye
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }
        //[BUGFIX]-Mod-END by TSCD.gerong
        if ((mNewAudio != null) && (mNewAudio.mUri != null)) {
            //PR900713 modified by Gu Feilong at 2015-01-19 start
            MediaPlayer tMediaPlayer = MediaPlayer.create(NoteEditorActivity.this, Uri.parse(mNewAudio.mUri));
            if(tMediaPlayer != null){
            int duration = (tMediaPlayer.getDuration()) / 1000;
            if (tMediaPlayer.getDuration() % 1000 != 0) {
                duration += 1;
            }
            mNewAudio.mDuration = duration;
            date_view.setText(timeToString(duration));
            adapter.addItem(mNewAudio);
            tMediaPlayer.reset();
            tMediaPlayer.release();
            }else{
            	return;
            }
            //PR900713 modified by Gu Feilong at 2015-01-19 end
        }
        Toast.makeText(this, R.string.save_record, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        TctLog.e(TAG, "onDestroy");
        super.onDestroy();
        if (mMediaRecorder != null) {
            // 这里之前Activity是间接的被强引用，这里置空
           	mMediaRecorder.setOnInfoListener(null);//added by xinyao.ye
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
//        mNote = null;//For PR1017417
//        mRminder = null;
        
        if(audioManager != null && mfl != null)
        	audioManager.abandonAudioFocus(mfl);   
        //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
        //this.unregisterReceiver(mbr);
        //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
        this.unregisterReceiver(hombr);
         
        
        this.mTelephonyManager.listen(this.mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        
        
    	//For PR984232//1016546
        NoteProvider.closeDeldb();
        NoteProvider.closeupdatedb();
        
        //PR853175  After delete the latest note when had only one note,widget not update.Added by hz_nanbing.zou at 26/11/2014 begin
    	Cursor ccc = getContentResolver().query(Note.CONTENT_URI, null, null, null,
        		Note.COLUMN_MODIFY_TIME + " DESC");
    	if( ccc.getCount() > 0){
    		ccc.moveToFirst();
    		updatewidget(ccc.getLong(ccc.getColumnIndex(Note.COLUMN_ID)));
    	}else{
    		updatewidget(-1);
    	}
    	ccc.close();
    	//PR853175  After delete the latest note when had only one note,widget not update.Added by hz_nanbing.zou at 26/11/2014 end
    	
//    	//For PR984232
//        NoteProvider.closeDeldb();
//        NoteProvider.closeupdatedb();
    }

    @SuppressWarnings("deprecation")
	public void initAttachPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.button_attach, null);
        popupwindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupwindow.setBackgroundDrawable(new BitmapDrawable());
        popupwindow.setFocusable(false);
        popupwindow.setOutsideTouchable(true);
        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {/*
                // TODO Auto-generated method stub
                //attachItem.setBackgroundResource(R.drawable.note_action_bar_bg);
                //colorItem.setBackgroundResource(R.drawable.note_action_bar_bg);
                ((ImageView) editorView.findViewById(R.id.add_attachment_flag))
                        .setVisibility(View.GONE);
                ((ImageView) editorView.findViewById(R.id.change_bg_flag)).setVisibility(View.GONE);
            */}
        });
        popupwindow.showAsDropDown(((RelativeLayout) findViewById(R.id.actionbar_title)));
        popupwindow.update();

        Button camera = (Button) contentView.findViewById(R.id.camera_item);
        camera.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                takeImage();
            }
        });

        Button AddImage = (Button) contentView.findViewById(R.id.add_image_item);
        AddImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pickPhoto();
            }
        });
        Button Record = (Button) contentView.findViewById(R.id.record_item);
        Record.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                //colorItem.setVisibility(View.GONE);
                //attachItem.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                //[BUGFIX]-DELETE-BEGIN by wenlu.wu,11/26/2013,PR-558068
                //date_view.setVisibility(View.VISIBLE);
                //[BUGFIX]-DELETE_END by wenlu.wu,11/26/2013,PR-558068
                stopAudio.setVisibility(View.VISIBLE);
            }
        });
        Button Reminder = (Button) contentView.findViewById(R.id.reminder_item);
        Reminder.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                // [BUGFIX]-Mod-BEGIN by TCTNB.YuTao.Yang,09/24/2013,PR527679,
                // [Notes]The reminder display abnormity after modify it
                if (mRminder == null) {
                    mRminder = new Note.Reminder();
                    mRminder.mReminderTime = NO_SET_REMINDER;
                    TctLog.i(TAG, "mRminder == null");
                }
                // [BUGFIX]-Mod-END by TCTNB.YuTao.Yang
                Intent intent = new Intent();
                intent.setClass(NoteEditorActivity.this, ReminderSettingActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PICK_REMINDER);
            }
        });

    }


    protected void pickPhoto() {
        try {
        	
        	//PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
        	isOtherActivity = true;
        	
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            //PR876860 modified by Gu Feilong at 2014-12-19 start
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
            
            //PR876860 modified by Gu Feilong at 2014-12-19 end
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            // Toast.makeText(this, R.string.image_picker_not_found,
            // Toast.LENGTH_SHORT).show();
        }
    }

    protected void takeImage() {
        try {
            currentFileName = getPhoteFileName();// getExternalCacheDir().getAbsolutePath()
            File file = new File(Constants.IMAGE_DIR + "/" + currentFileName + ".jpg");
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
            isOtherActivity = true;
            
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//For PR958191
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "ActivityNotFoundException", Toast.LENGTH_SHORT).show();
        }
    }
    
    protected void takeRecord(){

        try {
         //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
            isOtherActivity = true;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
            intent.setType("audio/amr");
			intent.setPackage("com.tct.soundrecorder");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, REQUEST_CODE_TAKE_RECORD);
            
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "ActivityNotFoundException", Toast.LENGTH_SHORT).show();
        }
    
    }
    
    

    public String getPhoteFileName() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            popupwindow = null;
        }
        if (resultCode == RESULT_OK) {
        	
        	//PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
        	isOtherActivity = false;
        	//PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
        	
            switch (requestCode) {
                case REQUEST_CODE_TAKE_IMAGE: {
                    TctLog.e(TAG, "REQUEST_CODE_TAKE_IMAGE");
                    File file = new File(Constants.IMAGE_DIR + File.separator + currentFileName + ".jpg");
                    if (data == null) {
                        data = new Intent();
                    }
//[BUGFIX]-ADD-BEGIN by TCTNB.Yan.TENG,01/02/2014,PR580222
                    // Add the image to the media store
                    if(!NoteUtils.isMTKPlatform())//For defect 251259
                    MediaScannerConnection.scanFile(
                            NoteEditorActivity.this,
                            new String[] { Constants.IMAGE_DIR.toString()  + File.separator + currentFileName + ".jpg" },
                            new String[] { null },
                            null);
//[BUGFIX]-ADD-END by TCTNB.Yan.TENG

                    try {
                        data.setData(Uri.fromFile(file));
                        handleTakeOrPickImage(data, false);
                    } catch (Exception e) {
                        TctLog.d(TAG, e.toString(), e);
                    }
                    break;
                }
                case REQUEST_CODE_PICK_IMAGE: {
                    //modify by mingyue.wang for pr898647 begin
                    try {
                        handleTakeOrPickImage(data, false);
                    } catch (Exception e) {
                        TctLog.d(TAG, e.toString(), e);
                    }
                    //modify by mingyue.wang for pr898647 end
                    break;
                }
                case REQUEST_CODE_PICK_REMINDER: {
                  //[BUGFIX]-MOd-BEGIN by TSCD(yanmei.zhang),06/09/2014,PR692135,
                    newReminderTime = data.getLongExtra("reminder_time", -1);
                    (findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);

                    //PR 827813 set reminder display delete wrong. Fixed by hz_nanbing_zou at 3/11/2014 begin
                    //deleteReminder.setVisibility(View.GONE);
                    //PR 826663 set reminder display delete wrong. Fixed by hz_nanbing_zou at 3/11/2014 end

                    setDate((TextView) findViewById(R.id.reminder_date), newReminderTime);
                    setTime((TextView) findViewById(R.id.reminder_time), newReminderTime);
                    TctLog.e(TAG, "edit mil : " + newReminderTime);
                  //[BUGFIX]-MOd-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
                    break;
                }
                case REQUEST_CODE_TAKE_RECORD:{
                	mNewAudio = new Note.Audio();
                	mNewAudio.mUri = getRealPath(data.getData());
                    if ((mNewAudio != null) && (mNewAudio.mUri != null)) {
                        //PR900713 modified by Gu Feilong at 2015-01-19 start
                        MediaPlayer tMediaPlayer = MediaPlayer.create(NoteEditorActivity.this, data.getData());
                        if(tMediaPlayer != null){
                        int duration = (tMediaPlayer.getDuration()) / 1000;
                        if (tMediaPlayer.getDuration() % 1000 != 0) {
                            duration += 1;
                        }
                        mNewAudio.mDuration = duration;
                        date_view.setText(timeToString(duration));
                        adapter.addItem(mNewAudio);
                        tMediaPlayer.reset();
                        tMediaPlayer.release();
                        }
                        //PR900713 modified by Gu Feilong at 2015-01-19 end
                    }
					TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
					list.setVisibility(View.VISIBLE);
					setRecorderList();
					Recordstate = STATE_STOP_RECORD;
                    Toast.makeText(this, R.string.save_record, Toast.LENGTH_SHORT).show();
                	break;
                }
            }
        }
    }

    private void setDate(TextView view, long millis) {
    	
    	
    	//Modified for defect 250255, date format should be changed for different language
        TctLog.e(TAG, "timeString: " + millis);
       int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;

        String dateString;
        synchronized (TimeZone.class) {
            dateString = DateUtils.formatDateTime(this, millis, flags);
            // setting the default back to null restores the correct behavior
            TimeZone.setDefault(null);
        }
        
        if(NoteUtils.isSpaniLan()){
        	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        	dateString = sdf.format(new Date(millis));
        }
        view.setText(dateString);
    }

    @SuppressWarnings("deprecation")
	private void setTime(TextView view, long millis) {
        int flags = DateUtils.FORMAT_SHOW_TIME;
        if (DateFormat.is24HourFormat(this)) {//For defect360088
            flags |= DateUtils.FORMAT_24HOUR;
        }
        String timeString;
        synchronized (TimeZone.class) {
            timeString = DateUtils.formatDateTime(this, millis, flags);
            TimeZone.setDefault(null);
        }
        view.setText(timeString);
    }

    public void handleTakeOrPickImage(Intent data, boolean isForReplace) {
    	
    	showLoading(true);
    	InsertImageAsyncTask insert = new InsertImageAsyncTask();
    	insert.execute(data);
    	
    	/*
        Uri uri = data.getData();
        if (Intent.ACTION_SEND.equals(data.getAction()))
            if (data.getExtras().containsKey(Intent.EXTRA_STREAM)) {
                uri = (Uri) data.getExtras().getParcelable(Intent.EXTRA_STREAM);
            }
        //[BUGFIX]-Add-BEGIN by yang.zhang3,2014-12-18,PR871586
        if (uri != null){
            File fpath=Environment.getDataDirectory();
            long availableBlocks=fpath.getUsableSpace();
            long fileSize=0;
            try {
                fileSize = getContentResolver().openInputStream(uri).available();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d(TAG, "memoryinfo---devices space="+availableBlocks+"-------fileSize="+fileSize);
            if(availableBlocks<=fileSize){
                Log.d(TAG, "devices has no space");
                Toast.makeText(this, getResources().getString(R.string.msg_devices_no_space), Toast.LENGTH_LONG).show();
                return;
            }
          //[BUGFIX]-Add-END by yang.zhang3,2014-12-18,PR871586
            mNoteEditor.insertImage(uri);
        }
    */}

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            long uptimeMillis = SystemClock.uptimeMillis();
            switch (message.what) {
            	
            
            case MSG_WHAT_SHOW_PAUSE_DURATION:
            	tempTime = getDuration();
            	vRecordTime.setText(timeToString(tempTime));
            	break;
            
            case MSG_WHAT_SAVE_FROM_HOMEKEY://For PR984546
            	saveAll();
            	break;
            	
            	case MSG_WHAT_END_INSERT_IMAGE:
            		showLoading(false);
            		break;
            	case STATE_CHANGED_SCREEN:
            		setDatePicker(1);
            		setTimePicker(1);
            		break;
            
            	//PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 begin
                case STATE_RECORD_ERROR:
//                	stopAudio.performClick();
    	            if(null!=d&&d.isShowing()){
                        d.dismiss();
   	            }
    	            if(mMediaRecorder !=null){//Modified this,because dimiss() will make
					mMediaRecorder.reset();
					mMediaRecorder.release();
					audioManager.abandonAudioFocus(mfl);
                	//PR912301.FC after home key when record at SoundRecorder record.Added by hz_nanbing.zou at 23/01/2015 begin
                	mMediaRecorder = null;
                	//PR912301.FC after home key when record at SoundRecorder record.Added by hz_nanbing.zou at 23/01/2015 end
    	            }
                	Toast.makeText(NoteEditorActivity.this, R.string.record_error, Toast.LENGTH_SHORT).show();
                	return;
                //PR 855311.Record error. Added by hz_nanbing.zou at 19/12/2014 end

                case MSG_WHAT_SWITCH_RECORD_ICON: {
                    TctLog.e(TAG, " message.arg1: " + message.arg1);
                    
                    mInitialCallState = mTelephonyManager.getCallState();
                    
                    if (Recordstate == STATE_START_RECORD) {
                        TctLog.e(TAG, " message.arg122222: " + message.arg1);
                        mNewAudio.mDuration = message.arg1 + 1;
                    }
                    if (mMediaRecorder == null)
                        message.arg1 = 0;
//                    ((TextView) findViewById(R.id.date)).setText(timeToString(message.arg1));
                    if(getHasFile() == -1)
    	            if(null!=d&&d.isShowing()){
                        d.dismiss();
    	            }
                   
                    vRecordTime.setText(timeToString(message.arg1+tempTime));//For PR967021
                    
                    break;
                }
                case MSG_WHAT_UPDATE_PLAY_PROGRESS: {
                    audioListView audiolistview = (audioListView) ((View) message.obj).getTag();
                    alv = audiolistview;
                    if (!MediaPause) {
                        audiolistview.start_time.setText(timeToString(message.arg1)+"/");
                    } else {
                        audiolistview.total_time_before.setText(timeToString(message.arg2));
                    }
                    if ((message.arg1 == message.arg2) && (Playstate == STATE_START_PLAY)) {
                        audiolistview.audioPlay.performClick();
                        
                        //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
                        audioManager.abandonAudioFocus(mfl);
                        //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end
                        
                        //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                        sendBroadcast(new Intent("audioplayend"));
                        //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                        
                    }
                    break;
                }
            }
            TctLog.e(TAG, " (mMediaRecorder != null): " + (mMediaRecorder != null));
            if ((!MediaPause && (message.arg1 < message.arg2) && (message.what == MSG_WHAT_UPDATE_PLAY_PROGRESS))
                    || (message.what == MSG_WHAT_SWITCH_RECORD_ICON && (mMediaRecorder != null))) {
                //PR853776 Add by Gu Feilong at 26-12-2014 start
                //PR903993 modified by Gu Feilong at 2015-01-19 start
//                if(((message.arg1 + 1)>4*60*60-1)&&(Recordstate == STATE_START_RECORD)){
//                    timeOutStopRecord();
//                }else{
                    Message msg = new Message();
                    msg.what = message.what;
                    msg.arg1 = message.arg1 + 1;
                    msg.arg2 = message.arg2;
                    msg.obj = message.obj;
                    mHandler.sendMessageAtTime(msg, uptimeMillis + 1000);
//                }
              //PR903993 modified by Gu Feilong at 2015-01-19 end
              //PR853776 Add by Gu Feilong at 26-12-2014 end
            }
        };
    };
    //PR853776 Add by Gu Feilong at 26-12-2014 start
    private boolean isTimeout = false;
    private void timeOutStopRecord(){
    	
    	if(d != null){
    		isTimeout = true;
    		d.dismiss();
    	}
    	
         if(illegalFlag){
             illegalFlag = false;
            return;
        }
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e){
                TctLog.e(TAG, e.toString(), e);
                return;
            } finally {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }
        //[BUGFIX]-Mod-END by TSCD.gerong
        if ((mNewAudio != null) && (mNewAudio.mUri != null)) {
            //PR903993 modified by Gu Feilong at 2015-01-19 start
            MediaPlayer tMediaPlayer = MediaPlayer.create(NoteEditorActivity.this, Uri.parse(mNewAudio.mUri));
            int duration = tMediaPlayer.getDuration() / 1000;
            if (tMediaPlayer.getDuration() % 1000 != 0) {
                duration += 1;
            }
            mNewAudio.mDuration = duration;
            date_view.setText(timeToString(duration));
            adapter.addItem(mNewAudio);
            tMediaPlayer.reset();
            tMediaPlayer.release();
            //PR903993 modified by Gu Feilong at 2015-01-19 end
        }
        TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
        list.setVisibility(View.VISIBLE);
        setRecorderList();
        stopAudio.setBackgroundResource(R.drawable.audio_click);
        imageview.setVisibility(View.GONE);
        date_view.setVisibility(View.GONE);
        stopAudio.setVisibility(View.GONE);
        
        mRecord_Layout.setVisibility(View.GONE);
        
        Recordstate = STATE_STOP_RECORD;
        edit_add.setVisibility(View.VISIBLE);
        menus.setGroupVisible(Menu.NONE, true);
        sendBroadcast(new Intent("audioplayend"));
        Toast.makeText(NoteEditorActivity.this, R.string.record_max_time, Toast.LENGTH_LONG).show();
    }
    //PR853776 Add by Gu Feilong at 26-12-2014 end
    public class audioListView {
        ImageButton audioPlay;
        TextView record_content;
        TextView total_time;
        TextView start_time;
        TextView total_time_before;
        ImageButton deleteItem;
    }

    public class AudioAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public AudioAdapter(Context context) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(Note.Audio Audio) {
            mCacheAudioList.add(Audio);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            // TctLog.e(TAG,"mCacheAudioList.size(): " + mCacheAudioList.size());
            return mCacheAudioList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mCacheAudioList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //PR888209.Play icon error.Added by hz_nanbing.zou at 29/12/2014 begin
        int tempP = -1;
        //PR888209.Play icon error.Added by hz_nanbing.zou at 29/12/2014 end
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final audioListView audiolistview;
            if (convertView == null) {
                audiolistview = new audioListView();
                convertView = mInflater.inflate(R.layout.audio_list_item, null);
                audiolistview.audioPlay = (ImageButton) convertView.findViewById(R.id.audio_play);
                audiolistview.total_time = (TextView) convertView.findViewById(R.id.total_time);
                audiolistview.start_time = (TextView) convertView.findViewById(R.id.start_time);
                audiolistview.deleteItem = (ImageButton) convertView.findViewById(R.id.delete_item);
                audiolistview.total_time_before = (TextView) convertView
                        .findViewById(R.id.total_time_before);
                convertView.setTag(audiolistview);
            } else {
                audiolistview = (audioListView) convertView.getTag();
            }
            final View view = convertView;
            //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-5,PR891111
            itemView.add(position, convertView);
            //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-5,PR891111
            if (mCacheAudioList != null && mCacheAudioList.size() != 0) {
                audiolistview.total_time_before.setText(timeToString((int) mCacheAudioList
                        .get(position).mDuration));
            }
//            if (DeleteDisplay) {
//                displayDeleteItem();
//            }
            
            displayDeleteItem(isView);
            
/*            (convertView.findViewById(R.id.audio_layout))
                    .setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            // TODO Auto-generated method stub
//                            displayDeleteItem();

                            // TODO Auto-generated method stub
                            AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditorActivity.this);
                            builder.setMessage(getString(R.string.delete_item))
                                    .setPositiveButton(getString(R.string.btn_ok),
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                  //[BUGFIX]-Add-BEGIN by yang.zhang3,2014-12-15,PR864161
                                                    //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
                                                      audioManager.abandonAudioFocus(mfl);
                                                      //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end
                                                      MediaPause = true;
                                                      if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                                          mMediaPlayer.pause();
                                                          //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                                                          sendBroadcast(new Intent("audioplayend"));
                                                          //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                                                      }
                                                      Playstate = STATE_STOP_OR_PAUSE_PLAY;
                                                    //[BUGFIX]-Add-END by yang.zhang3,2014-12-15,PR864161

                                                  //PR835838 Force close when delete record at recording.Modified by hz_nanbing.zou at 11/11/2014 begin
                                                  //mNewAudio = null;//[BUGFIX]-MOd-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
                                                  //PR835838 Force close when delete record at recording.Modified by hz_nanbing.zou at 11/11/2014 end
                                                   
                                                	if (mCacheAudioList != null
                                                            && mCacheAudioList.size() != 0) {
                                                        Cursor c = getContentResolver()
                                                                .query(Note.Audio.CONTENT_URI,
                                                                        null,
                                                                        Note.Audio.COLUMN_URI
                                                                                + " like '"
                                                                                + mCacheAudioList
                                                                                        .get(position).mUri
                                                                                + "'", null, null, null);
                                                        if (c != null && c.getCount() > 0) {
                                                            getContentResolver()
                                                                    .delete(Note.Audio.CONTENT_URI,
                                                                            Note.Audio.COLUMN_URI
                                                                                    + " like '"
                                                                                    + mCacheAudioList
                                                                                            .get(position).mUri
                                                                                    + "'", null);
                                                            c.close();
                                                        }
                                                        File file = new File(
                                                                mCacheAudioList.get(position).mUri);
                                                        if (file.exists()) {
                                                            file.delete();
                                                        }
                                                        mCacheAudioList.remove(position);
                                                        if (mCacheAudioList.size() == 0)
                                                            list.setVisibility(View.GONE);
//                                                        notifyDataSetChanged();
                                                        setRecorderList();
                                                    }
                                                }

                                            }).setNegativeButton(getString(R.string.btn_cancel), null)
                                    .show();

                        
                            return false;
                        }

                    });*/
            audiolistview.deleteItem.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditorActivity.this);
                    builder.setMessage(getString(R.string.delete_item))
                            .setPositiveButton(getString(R.string.delete_menu_title),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                          //[BUGFIX]-Add-BEGIN by yang.zhang3,2014-12-15,PR864161
                                            //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
                                              audioManager.abandonAudioFocus(mfl);
                                              //PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end
                                              MediaPause = true;
                                              if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                                  mMediaPlayer.pause();
                                                  //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                                                  sendBroadcast(new Intent("audioplayend"));
                                                  //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                                              }
                                              Playstate = STATE_STOP_OR_PAUSE_PLAY;
                                            //[BUGFIX]-Add-END by yang.zhang3,2014-12-15,PR864161

                                          //PR835838 Force close when delete record at recording.Modified by hz_nanbing.zou at 11/11/2014 begin
                                          //mNewAudio = null;//[BUGFIX]-MOd-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
                                          //PR835838 Force close when delete record at recording.Modified by hz_nanbing.zou at 11/11/2014 end
                                           
                                        	if (mCacheAudioList != null
                                                    && mCacheAudioList.size() != 0) {
                                                Cursor c = getContentResolver()
                                                        .query(Note.Audio.CONTENT_URI,
                                                                null,
                                                                Note.Audio.COLUMN_URI
                                                                        + " like '"
                                                                        + mCacheAudioList
                                                                                .get(position).mUri
                                                                        + "'", null, null, null);
                                                if (c != null && c.getCount() > 0) {
                                                    getContentResolver()
                                                            .delete(Note.Audio.CONTENT_URI,
                                                                    Note.Audio.COLUMN_URI
                                                                            + " like '"
                                                                            + mCacheAudioList
                                                                                    .get(position).mUri
                                                                            + "'", null);
                                                    c.close();
                                                }
                                                File file = new File(
                                                        mCacheAudioList.get(position).mUri);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                                mCacheAudioList.remove(position);
                                               //modify by mingyue.wang for pr948466 begin
                                                if (mCacheAudioList.size() == 0) {
                                                    list.setVisibility(View.GONE);
                                                    mNote.mHasAudio = false;
                                                    mNote.mHasText = !(getWorkingText().length() <= 0 || "".equals(getWorkingText().toString().trim()));
                                                    ContentResolver resolver = getContentResolver();
                                                    ContentValues values = mNote.toContentValues();
                                                    resolver.update(Note.CONTENT_URI, values, Note.COLUMN_ID + " = " + mNote.mId, null);
                                                }
                                                //modify by mingyue.wang for pr948466 end
//                                                notifyDataSetChanged();
                                                setRecorderList();
                                            }
                                        }

                                    }).setNegativeButton(getString(R.string.btn_cancel), null)
                            .show();

                }

            });
//            audiolistview.audioPlay.setImageResource(R.drawable.audio_start_play_record);
            
            audiolistview.audioPlay.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);

            //PR850113.After rotate to landscape mode when record playing,play icon error.Added by hz_nanbing.zou at 26/11/2014 begin
            if (Playstate == STATE_START_PLAY && tempP == position) {
//                audiolistview.audioPlay
//                        .setImageResource(R.drawable.audio_pause_play_record);
                
                audiolistview.audioPlay
                .setImageResource(R.drawable.ic_stop_grey600_24dp);
            }
            //PR850113.After rotate to landscape mode when record playing,play icon error.Added by hz_nanbing.zou at 26/11/2014 end

            audiolistview.audioPlay.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
           //PR947315 Add by Gu Feilong start
                     if(mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
                          Toast.makeText(NoteEditorActivity.this, R.string.playrecord_incall, Toast.LENGTH_SHORT).show();
                         return;
                     }
           //PR947315 Add by Gu Feilong end
                    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-7,PR888129
                    if(Recordstate == STATE_START_RECORD){
                         return;
                     }
                    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-7,PR888129
                	//[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-23,PR876537
                	
                	tempP = position;
                	
                	 for(int i=0;i<itemView.size();i++){
                       	if(position!=i){
                       		 audioListView audiolistview = (audioListView) itemView.get(i).getTag();
                       		 audiolistview.audioPlay.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
                       		 audiolistview.start_time.setVisibility(View.GONE);
                       		 audiolistview.total_time.setVisibility(View.GONE);
                       		 audiolistview.total_time_before.setVisibility(View.VISIBLE);
                       	}
                       }
                     Message msg = new Message();
                     String mUri=mCacheAudioList.get(position).mUri;
                     if(!mUriStr.equals(mUri)){
                         mUriStr=mUri;
                         Playstate=STATE_STOP_OR_PAUSE_PLAY;
                         mHandler.removeMessages(MSG_WHAT_UPDATE_PLAY_PROGRESS);
                         MediaPause = true;
                          if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                              mMediaPlayer.pause();
                              //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                              sendBroadcast(new Intent("audioplayend"));
                              //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                          }
                     }
                    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-24,PR876537
                    if (Playstate == STATE_STOP_OR_PAUSE_PLAY) {

                    	//PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
                    	audioManager.requestAudioFocus(mfl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);//For PR982257
                    	//PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end

                        Playstate = STATE_START_PLAY;
                        audiolistview.audioPlay
                                .setImageResource(R.drawable.ic_stop_grey600_24dp);
                        try {
                            TctLog.e(TAG, "mMediaPlayer: " + mMediaPlayer);

                            if (mMediaPlayer == null) {
                                mMediaPlayer = new MediaPlayer();
                            } else {
                                mMediaPlayer.reset();
                            }
                            TctLog.e(TAG,
                                    "mCacheAudioList.get(position).mUri: "
                                            + mCacheAudioList.get(position).mUri);
                            if (mCacheAudioList != null && mCacheAudioList.size() != 0) {

                            	try{
                                mMediaPlayer.setDataSource(mCacheAudioList.get(position).mUri);
                            	}catch(IOException e){
                            		TctLog.e(TAG, "IOException:"+mCacheAudioList.get(position).mUri);
                            		mMediaPlayer = null;
                            		Toast.makeText(NoteEditorActivity.this, R.string.no_file, Toast.LENGTH_SHORT).show();
                            		return;
                            	}
                            }
                            mMediaPlayer.prepare();
                            int duration = mMediaPlayer.getDuration() / 1000;
                            if (mMediaPlayer.getDuration() % 1000 != 0) {
                                duration += 1;
                            }
                            audiolistview.total_time_before.setVisibility(View.GONE);
                            audiolistview.total_time.setVisibility(View.VISIBLE);
                            audiolistview.start_time.setVisibility(View.VISIBLE);
                            audiolistview.start_time.setText(timeToString(0)+"/");//For PR966479
                            audiolistview.total_time.setText(timeToString(duration));
                            TctLog.e(TAG, "mMediaPlayer: " + mMediaPlayer);
                            if (mMediaPlayer != null)
                                mMediaPlayer.start();
                            MediaPause = false;

                            msg.what = MSG_WHAT_UPDATE_PLAY_PROGRESS;
                            TctLog.e(TAG,
                                    "mMediaPlayer.getCurrentPosition(): "
                                            + mMediaPlayer.getCurrentPosition());
                            int CurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                            msg.arg1 = CurrentPosition;
                            msg.arg2 = duration;
                            msg.obj = view;
                            mHandler.sendMessage(msg);
                            
                            //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                            sendBroadcast(new Intent("audioplay"));
                            //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                            
                        } catch (IllegalArgumentException e) {
                            TctLog.d(TAG, e.toString(), e);
                        } catch (SecurityException e) {
                            TctLog.d(TAG, e.toString(), e);
                        } catch (IllegalStateException e) {
                            TctLog.d(TAG, e.toString(), e);
                        } catch (IOException e) {
                            TctLog.d(TAG, e.toString(), e);
                        }
                    } else if (Playstate == STATE_START_PLAY) {

                    	//PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
                    	audioManager.abandonAudioFocus(mfl);
                    	//PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end

                    	mHandler.removeMessages(MSG_WHAT_UPDATE_PLAY_PROGRESS);//for PR959042
                        audiolistview.audioPlay
                                .setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
                        audiolistview.total_time.setVisibility(View.GONE);
                        audiolistview.start_time.setVisibility(View.GONE);
                        audiolistview.total_time_before.setVisibility(View.VISIBLE);
                        MediaPause = true;
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();

                            //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
                            sendBroadcast(new Intent("audioplayend"));
                            //PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end
                        
                        }

                        Playstate = STATE_STOP_OR_PAUSE_PLAY;
                    }

                }
            });

            return convertView;
        }
    }

    private class LoadNoteAsyncTask extends AsyncTask<Long, Integer, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Long... params) {
            loadNote(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang,2015-1-30,PR917795
        	
        	if(mNote != null){//For PR989967
                switch (mNote.mBgImageResId) {
                case Constants.NOTE_BG_White_ID: {
                    AllLayout.setBackgroundColor(NoteEditorActivity.this
                            .getResources().getColor(R.color.theme_bg_white));
                    break;
                }
                case Constants.NOTE_BG_Blue_ID: {
                    AllLayout.setBackgroundColor(NoteEditorActivity.this
                            .getResources().getColor(R.color.theme_bg_blue));
                    break;
                }
                case Constants.NOTE_BG_Green_ID: {
                    AllLayout.setBackgroundColor(NoteEditorActivity.this
                            .getResources().getColor(R.color.theme_bg_green));
                    break;
                }
                case Constants.NOTE_BG_Yellow_ID: {
                    AllLayout.setBackgroundColor(NoteEditorActivity.this
                            .getResources().getColor(R.color.theme_bg_yellow));
                    break;
                }
                case Constants.NOTE_BG_Red_ID: {
                    AllLayout.setBackgroundColor(NoteEditorActivity.this
                          .getResources().getColor(R.color.theme_bg_red));
                  break;
              }
                case Constants.NOTE_BG_Purple_ID: {
                    AllLayout.setBackgroundColor(NoteEditorActivity.this
                          .getResources().getColor(R.color.theme_bg_purple));
                  break;
              }
            }
            //[BUGFIX]-ADD-END by AMNJ.rurong.zhang,2015-1-30,PR917795
                mOldGroudId = mNote.mGroupId;
                TctLog.d("nb", "1:"+mRminder+"2:"+mNote.mHasReminder);
                if (mRminder != null && mNote.mHasReminder == true) {
                    (findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);
//                    deleteReminder.setVisibility(View.GONE);
                    setDate((TextView) findViewById(R.id.reminder_date), mRminder.mReminderTime);
                    setTime((TextView) findViewById(R.id.reminder_time), mRminder.mReminderTime);
                }
                if (mNote.mHasAudio == true) {
                    list.setVisibility(View.VISIBLE);
//                    list.setAdapter(adapter);
                    
                    
                    setRecorderList();

                }
        	}

        }



        private void loadNote(long noteId) {
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(ContentUris.withAppendedId(Note.CONTENT_URI, noteId),
                    null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    mNote = NoteUtils.toNote(cursor);
                }
                cursor.close();

                if (mNote != null) {
                    cursor = resolver.query(Note.Reminder.CONTENT_URI, null,
                            Note.Reminder.COLUMN_NOTE_ID + "=" + noteId, null, null);
                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            mRminder = new Note.Reminder();
                            mRminder = NoteUtils.toReminder(cursor);
                            mOldReminderId = mRminder.mId;
                        }
                        cursor.close();
                    }

                    cursor = resolver.query(Note.Audio.CONTENT_URI, null, Note.Audio.COLUMN_NOTE_ID
                            + "=" + noteId, null, null);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            adapter.addItem(NoteUtils.toAudio(cursor));
                            AudioCount = cursor.getCount();//[BUGFIX]-Add-END by TSCD(yanmei.zhang),06/09/2014,PR692135,
                        }
                        cursor.close();
                    }
                }
            }
        }
    }

    //PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//    private   int notifyId = R.string.note_reminder;
//    /**cancel a notify
//     * return void
//     */
//    private void cancelNotify() {
//        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(notifyId);
//	}
//	/**set alarm icon on statue bar
//	 * return void
//	 */
//	private void sendNotify() {
//		// when we save the reminder,and set alarm,must sent the notify right now
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
//      //the statue icon should not be clear,and can start the intent to main activity.
//        notification.flags = Notification.FLAG_NO_CLEAR;
//        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//        Intent intent = new Intent(this, NotesListActivity.class);
//        
//        intent.putExtra("from_notify", true);
//        
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(this, this.getResources().getString(R.string.note_reminder),this.getResources().getString(R.string.note_notify_text), pendingIntent);
//        manager.notify(notifyId, notification);
//	}
	//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end
	
	//PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 begin
	boolean playing;
	boolean sentpause;
		private class MusicBrocastReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				SharedPreferences sh = context.getSharedPreferences("music", Context.MODE_WORLD_READABLE);
				
				playing = sh.getBoolean("isplaying", false);
				if(intent.getAction() == "audioplay"){
					if(playing){
						TctLog.d("nb","music brocast");
						Intent it = new Intent("com.android.music.musicservicecommand");
						it.putExtra("command", "pause");
						sendBroadcast(it);
						
						sentpause = true;
					}
				}if(intent.getAction() == "audioplayend"){
					if(sentpause)
					{
						TctLog.d("nb","music brocast go on");
						Intent it = new Intent("com.android.music.musicservicecommand");
						it.putExtra("command", "play");
						sendBroadcast(it);
						
						sentpause = false;
					}
				}
			}
			
		}
	//PR 836616.Music && Record play as the same time. Added by hz_nanbing.zou at 11/11/2014 end

		//PR843458.After "Home" key and back to editor again,white screen flash.Added by hz_nanbing.zou at 18/11/2014 begin
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			TctLog.d("nb","onPause()");
			//this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
		//PR843458.After "Home" key and back to editor again,white screen flash.Added by hz_nanbing.zou at 18/11/2014 end
		
		//PR839898. After change configuration,popup window display error.Added by hz_nanbing.zou at 19/11/2014 begin
		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			// TODO Auto-generated method stub
			super.onConfigurationChanged(newConfig);
			if(popupwindow!=null)
				popupwindow.dismiss();
			mHandler.sendEmptyMessage(STATE_CHANGED_SCREEN);
		}
		//PR839898. After change configuration,popup window display error.Added by hz_nanbing.zou at 19/11/2014 end		

		//PR844471.After home key --> enter from widget,can save empty. Added by hz_nanbing.zou at 19/11/2014 begin
		private class HomeKeyReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
                final String SYSTEM_REASON = "reason";
                final String SYSTEM_HOME_KEY = "homekey";
                final String SYSTEM_RESCENT_APPS_KEY = "recentapps";//PR1045815 down the recentapp key  similar the home key process mode .update by lxl july 16 2015
                String action = intent.getAction();
                TctLog.d("lxl", "HomeKeyReceiver.." + action);
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_REASON);
                    if (reason != null) {
                        if (reason.equals(SYSTEM_HOME_KEY) || reason.equals(SYSTEM_RESCENT_APPS_KEY)) {//PR1045815 down the recentapp key  similar the home key process mode .update by lxl july 16 2015
                            TctLog.d("nb","flag.."+isOtherActivity);
							  //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
							  if(isOtherActivity)
								  return;
							  //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
							  
							  isHomeKey = true;

							  //PR861930 The record can not direct saved when tap Home/Recent key.Added by hz_nanbing.zou at 04/12/2014 begin
							  if(Recordstate == STATE_START_RECORD){
//								  stopAudio.performClick();
								  	if(d !=null)
								  		d.dismiss();
									if (mMediaRecorder != null){
									onStopRecord();
									TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
									list.setVisibility(View.VISIBLE);
									setRecorderList();
									Recordstate = STATE_STOP_RECORD;
									}
								
							  }
							//PR861930 The record can not direct saved when tap Home/Recent key.Added by hz_nanbing.zou at 04/12/2014 end
//							  saveAll();
							  mHandler.sendEmptyMessage(MSG_WHAT_SAVE_FROM_HOMEKEY);//For PR984546
						  }
					  }

					//PR850747 Notes window displays blank after restart phone.Added by hz_nanbing.zou at 04/12/2014 begin
				  }
				  else if(action.equals(Intent.ACTION_DEVICE_STORAGE_LOW)){//For PR980628
					  if(Recordstate == STATE_START_RECORD){
						  	if(d !=null)
						  		d.dismiss();
							if (mMediaRecorder != null){
							onStopRecord();
							TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
							list.setVisibility(View.VISIBLE);
							setRecorderList();
							Recordstate = STATE_STOP_RECORD;
							Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.msg_devices_no_space), Toast.LENGTH_SHORT).show();
							}
					  }
				  }
				  else{
					  
					  if(Recordstate == STATE_START_RECORD){
//						  stopAudio.performClick();
						  	if(d !=null)
						  		d.dismiss();
							if (mMediaRecorder != null){
							onStopRecord();
							TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
							list.setVisibility(View.VISIBLE);
							setRecorderList();
							Recordstate = STATE_STOP_RECORD;
							}
						
					  }
					  saveAll();
				  }
				  //PR850747 Notes window displays blank after restart phone.Added by hz_nanbing.zou at 04/12/2014 end
			}	
		}
		//PR844471.After home key --> enter from widget,can save empty. Added by hz_nanbing.zou at 19/11/2014 end

		//PR848153.After recent key,keyboard not popup.Added by hz_nanbing.zou at 25/11/2014 begin
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			// TODO Auto-generated method stub
			super.onWindowFocusChanged(hasFocus);
			TctLog.d("nb","focus.."+hasFocus);
			if(hasFocus){
				if(!isView){
					this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//					isView = false;
				}else{
//					isView = false;
				}
			}
				
		}
		//PR848153.After recent key,keyboard not popup.Added by hz_nanbing.zou at 25/11/2014 end


		//PR853175  After delete the latest note when had only one note,widget not update.Added by hz_nanbing.zou at 26/11/2014 begin
		private void updatewidget(long noteId){

	        SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",
	                  Context.MODE_PRIVATE);
	          widgetNoteMap.edit().putLong("widget", noteId).apply();
	          Intent widgetIntent = new Intent("com.tct.note.widget.UPDATE");
	          widgetIntent.setComponent(NoteMiniAppProvider.COMPONENT);
	          widgetIntent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
	          widgetIntent.putExtra("from_editor", true);
	          sendBroadcast(widgetIntent);
		}
		//PR853175  After delete the latest note when had only one note,widget not update.Added by hz_nanbing.zou at 26/11/2014 begin

		
		//PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 begin
		private class MediaFocusListener implements OnAudioFocusChangeListener{

			@Override
			public void onAudioFocusChange(int focusChange) {
				// TODO Auto-generated method stub
				TctLog.d("nb","focusChange.."+focusChange);
				switch(focusChange){
				case AudioManager.AUDIOFOCUS_GAIN:
					break;
				case AudioManager.AUDIOFOCUS_LOSS:
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://for PR958102.
		            if (Playstate == STATE_START_PLAY) {
		            	if(alv.audioPlay!=null)
		            		alv.audioPlay.performClick();
		            }
		            
		            if(Recordstate == STATE_START_RECORD){//For defect287859
		            	if( vRecordTap2 !=null ){
		            		vRecordTap2.performClick();
		            	}
		            }
		            
					break;
				}
				
			}
			
		}
		//PR862032 Music and FM radio are still playing on background when play voice record in Notes.Added by hz_nanbing.zou at 04/12/2014 end
		
		Menu menus;
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			setMenuIconUtils.setIconEnable(menu, true);

			
			//For defect 252817
			SubMenu sm1 = menu.addSubMenu(Menu.NONE, Menu.FIRST-2, Menu.NONE,this.getResources().getString(R.string.attachments)).setIcon(R.drawable.ic_attach_file_white);//.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			SubMenu sm2 = menu.addSubMenu(Menu.NONE, Menu.FIRST-1, Menu.NONE,this.getResources().getString(R.string.colours)).setIcon(R.drawable.ic_palette_white);//.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			menu.findItem(Menu.FIRST-2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.findItem(Menu.FIRST-1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			
			
			sm1.add(Menu.NONE, Menu.FIRST, Menu.NONE, this.getResources().getString(R.string.camera)).setIcon(R.drawable.ic_photo_camera_grey600);
			sm1.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, this.getResources().getString(R.string.album)).setIcon(R.drawable.ic_photo_grey600);
			sm1.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, this.getResources().getString(R.string.record)).setIcon(R.drawable.ic_mic_grey600);
			sm1.add(Menu.NONE, Menu.FIRST+3, Menu.NONE, this.getResources().getString(R.string.reminder)).setIcon(R.drawable.ic_access_alarm_grey600_24dp);
			
			sm2.add(Menu.NONE, Menu.FIRST+4, Menu.NONE, this.getResources().getString(R.string.color_white)).setIcon(R.drawable.ic_color_white_24dp);
			sm2.add(Menu.NONE, Menu.FIRST+5, Menu.NONE, this.getResources().getString(R.string.color_blue)).setIcon(R.drawable.ic_color_blue_24dp);
			sm2.add(Menu.NONE, Menu.FIRST+6, Menu.NONE, this.getResources().getString(R.string.color_green)).setIcon(R.drawable.ic_color_green_24dp);
			sm2.add(Menu.NONE, Menu.FIRST+7, Menu.NONE, this.getResources().getString(R.string.color_yellow)).setIcon(R.drawable.ic_color_yellow_24dp);
			
			sm2.add(Menu.NONE, Menu.FIRST+8, Menu.NONE, this.getResources().getString(R.string.color_red)).setIcon(R.drawable.ic_color_red_24dp);
			sm2.add(Menu.NONE, Menu.FIRST+9, Menu.NONE, this.getResources().getString(R.string.color_purple)).setIcon(R.drawable.ic_color_purple_24dp);
			
			menus = menu;

			//Added for Ergo 5.1.0 for View mode.
			if(isView)
			 menu.setGroupVisible(Menu.NONE, false);
			//Added for Ergo 5.1.0 for View mode.

			
			return super.onCreateOptionsMenu(menu);
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			
			switch(item.getItemId()){
			case Menu.FIRST:

				//PR874608 Keyboard pop up when enter into camera.Added by hz_nanbing.zou at 19/12/2014 begin
				InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(editorView.getWindowToken(), 0);
					
				if(!isEnoughSpace(0)){//for defect 291566
					Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.msg_devices_no_space), Toast.LENGTH_SHORT).show();
					break;
				}
				 takeImage();
				break;
			case Menu.FIRST+1:
				InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editorView.getWindowToken(), 0);
				//PR874608 Keyboard pop up when enter into camera.Added by hz_nanbing.zou at 19/12/2014 end

				pickPhoto();
				break;
			case Menu.FIRST+2:

//				takeRecord();
				
				

/*//				edit_add.setVisibility(View.GONE);
				//menus.clear();
//				menus.setGroupVisible(Menu.NONE, false);
			
				//PR906202.Can add record same time.Added by hz_nanbing.zou at 19/01/2015 begin
				if(stopAudio.isShown()){
					break;
				}
				//PR906202.Can add record same time.Added by hz_nanbing.zou at 19/01/2015 end
				
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                //colorItem.setVisibility(View.GONE);
                //attachItem.setVisibility(View.GONE);
                
				mRecord_Layout.setVisibility(View.VISIBLE);
				
				//date_view.setVisibility(View.VISIBLE);
				date_view.setText(R.string.start_record);
				tap_view.setVisibility(View.VISIBLE);
				tap_view.setText(R.string.start_record);
                imageview.setVisibility(View.VISIBLE);
                //[BUGFIX]-DELETE-BEGIN by wenlu.wu,11/26/2013,PR-558068
                //date_view.setVisibility(View.VISIBLE);
                //[BUGFIX]-DELETE_END by wenlu.wu,11/26/2013,PR-558068
//                stopAudio.setVisibility(View.VISIBLE);
*/
            //PR952991 Add by Gu Feilong start
                if(mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
                    Toast.makeText(NoteEditorActivity.this, R.string.record_incall, Toast.LENGTH_SHORT).show();
                   break;
                }
            //PR952991 Add by Gu Feilong end
				setDialog();
				break;
			case Menu.FIRST+3:

                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                // [BUGFIX]-Mod-BEGIN by TCTNB.YuTao.Yang,09/24/2013,PR527679,
                // [Notes]The reminder display abnormity after modify it
                if (mRminder == null) {
                    mRminder = new Note.Reminder();
                    mRminder.mReminderTime = NO_SET_REMINDER;
                    TctLog.i(TAG, "mRminder == null");
                }
                // [BUGFIX]-Mod-END by TCTNB.YuTao.Yang
                
                //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
                isOtherActivity = true;
                //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
                
//                Intent intent = new Intent();
//                intent.setClass(NoteEditorActivity.this, ReminderSettingActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_PICK_REMINDER);
                
                setDatePicker(0);
                
  
				break;
			case Menu.FIRST+4:

	            {
//	                AllLayout.setBackgroundResource(R.drawable.note_bg_1);
	                
					AllLayout.setBackgroundColor(this.getResources().getColor(R.color.theme_bg_white));
	                
	                if (mNote.mBgImageResId != Constants.NOTE_BG_White_ID)
	                mNote.mBgImageResId = Constants.NOTE_BG_White_ID;
//	                mNoteEditor.setTextColor(NoteEditorActivity.this.getResources().getColor(
//	                        R.color.text_theme_one));
	                mNote.mGroupId = 1;

	            }
	        
				break;
			case Menu.FIRST+5:

	             {
//	                AllLayout.setBackgroundResource(R.drawable.note_bg_2);
	                
	                AllLayout.setBackgroundColor(this.getResources().getColor(R.color.theme_bg_blue));
	                
	                if (mNote.mBgImageResId != Constants.NOTE_BG_Blue_ID)

	                mNote.mBgImageResId = Constants.NOTE_BG_Blue_ID;
//	                mNoteEditor.setTextColor(NoteEditorActivity.this.getResources().getColor(
//	                        R.color.text_theme_two));
	                mNote.mGroupId = 2;

	            }
	        
				break;
			case Menu.FIRST+6:

	             {
//	                AllLayout.setBackgroundResource(R.drawable.note_bg_3);
				
					AllLayout.setBackgroundColor(this.getResources().getColor(R.color.theme_bg_green));
				
	                if (mNote.mBgImageResId != Constants.NOTE_BG_Green_ID)

	                mNote.mBgImageResId = Constants.NOTE_BG_Green_ID;
	                mNote.mGroupId = 3;
//	                mNoteEditor.setTextColor(NoteEditorActivity.this.getResources().getColor(
//	                        R.color.text_theme_three));

	            }
	        
				
				break;
			case Menu.FIRST+7:
				{
//	                AllLayout.setBackgroundResource(R.drawable.note_bg_4);
				
					AllLayout.setBackgroundColor(this.getResources().getColor(R.color.theme_bg_yellow));
				
	                if (mNote.mBgImageResId != Constants.NOTE_BG_Yellow_ID)

	                mNote.mBgImageResId = Constants.NOTE_BG_Yellow_ID;
	                mNote.mGroupId = 4;
//	                mNoteEditor.setTextColor(NoteEditorActivity.this.getResources().getColor(
//	                        R.color.text_theme_four));

	            }
	        
				break;
			case Menu.FIRST+8:
			{
//                AllLayout.setBackgroundResource(R.drawable.note_bg_4);
			
				AllLayout.setBackgroundColor(this.getResources().getColor(R.color.theme_bg_red));
			
                if (mNote.mBgImageResId != Constants.NOTE_BG_Red_ID)

                mNote.mBgImageResId = Constants.NOTE_BG_Red_ID;
                mNote.mGroupId = 5;
//                mNoteEditor.setTextColor(NoteEditorActivity.this.getResources().getColor(
//                        R.color.text_theme_four));

            }
        
			break;
			case Menu.FIRST+9:
			{
//                AllLayout.setBackgroundResource(R.drawable.note_bg_4);
			
				AllLayout.setBackgroundColor(this.getResources().getColor(R.color.theme_bg_purple));
			
                if (mNote.mBgImageResId !=Constants.NOTE_BG_Purple_ID)

                mNote.mBgImageResId = Constants.NOTE_BG_Purple_ID;
                mNote.mGroupId = 6;
//                mNoteEditor.setTextColor(NoteEditorActivity.this.getResources().getColor(
//                        R.color.text_theme_four));

            }
        
			break;
			}
			
			return super.onOptionsItemSelected(item);
		}

		
		//PR879516 .The note editor will response slowly after dome some operations.Added by hz_nanbing.zou at 23/12/2014 begin
	    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
	        public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString) {
	            if ((paramAnonymousInt != 0)
	                    && (paramAnonymousInt != mInitialCallState))
	            	if(stopAudio!=null  && Recordstate == STATE_START_RECORD)
	            		stopAudio.performClick();
        //PR945404 It pop up FC after reject a call.Modified by Gu Feilong start
	            if(null!=d&&d.isShowing()){
                     d.dismiss();
	            }
        //PR945404 It pop up FC after reject a call.Modified by Gu Feilong end
	            //add by mingyue.wang for pr945611 begin
	            if(mTelephonyManager != null) {
	            int callState = mTelephonyManager.getCallState();
	            if (callState == TelephonyManager.CALL_STATE_RINGING) {
	                audioManager.abandonAudioFocus(mfl);

                    for(int i=0;i<itemView.size();i++){//For PR1002229
                        audioListView audiolistview = (audioListView) itemView.get(i).getTag();
                        audiolistview.audioPlay.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
                        audiolistview.start_time.setVisibility(View.GONE);
                        audiolistview.total_time.setVisibility(View.GONE);
                        audiolistview.total_time_before.setVisibility(View.VISIBLE);
                        }
	                MediaPause = true;
	                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
	                    mMediaPlayer.pause();
//	                    sendBroadcast(new Intent("audioplayend"));
	                }
	                Playstate = STATE_STOP_OR_PAUSE_PLAY;
	            }
	           }
	           //add by mingyue.wang for pr945611 end
	        }
	    };
		
		//PR879516 .The note editor will response slowly after dome some operations.Added by hz_nanbing.zou at 23/12/2014 end		

	    //Added for scroll record list.Added by hz_nanbing.zou at 24/12/2014
		DisplayMetrics dm;
	    private void setRecorderList(){
			if(dm == null){
				dm = new DisplayMetrics();
				this.getWindowManager().getDefaultDisplay().getMetrics(dm);
			}
			
			
			
	    	list.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (adapter.getCount()*50*(dm.density))));
	    	list.setAdapter(adapter);
	    }   
	    //Added for scroll record list.Added by hz_nanbing.zou at 24/12/2014
		
	    //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
	    @Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			isOtherActivity = false;
		}
	    //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
	    
	 //Added for View Mode at Ergo5.1.0.Added by hz_nanbing.zou at 15/01/2015   
	 private void setViewMode(boolean isViewMode){
		 
//		 view_pre = (ImageButton)findViewById(R.id.view_pre);
//		 view_next = (ImageButton)findViewById(R.id.view_next);
		 
		 adapter.notifyDataSetChanged();
		 if(isViewMode){//true is ViewMode
			 mNoteEditor.setViewMode(false);//disable EditText;
			 saveItem.setImageResource(R.drawable.ic_ab_arrow_back_24dp);
			 
			 deleteReminder.setVisibility(View.GONE);
//			 findViewById(R.id.delete_item).setVisibility(View.GONE);
			 edit_add.setText(getNoteTime());//change text as Ergo5.1.0
//			 view_pre.setVisibility(View.VISIBLE);
//			 view_next.setVisibility(View.VISIBLE);
			 
			 ((ImageView) findViewById(R.id.edit_modeImv))
			 	.setVisibility(View.VISIBLE);
			 ((ImageView) findViewById(R.id.edit_modeImv)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setViewMode(false);
					isView = false;
				}
			});
			 ((ImageView) findViewById(R.id.view_delete))
			 	.setVisibility(View.VISIBLE);
			 ((ImageView) findViewById(R.id.view_delete)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					delItselft();
				}
			});
		 }else{//false is EditMode
			 mNoteEditor.setViewMode(true);//disable EditText;
//			 saveItem.setVisibility(View.VISIBLE);//disable do save btn;
			 
			 saveItem.setImageResource(R.drawable.save);
			 
			 edit_add.setText(R.string.edit_add);//change text as Ergo5.1.0
			 deleteReminder.setVisibility(View.VISIBLE);
//			 findViewById(R.id.delete_item).setVisibility(View.VISIBLE);
			 if(menus !=null)
				 menus.setGroupVisible(Menu.NONE, true); 
//			 view_pre.setVisibility(View.GONE);
//			 view_next.setVisibility(View.GONE);
			 ((ImageView) findViewById(R.id.edit_modeImv))
			 	.setVisibility(View.GONE);
			 ((ImageView) findViewById(R.id.view_delete))
			 	.setVisibility(View.GONE);
//		        (findViewById(R.id.reminder_layout)).setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//		                // [BUGFIX]-Mod-BEGIN by TCTNB.YuTao.Yang,09/24/2013,PR527679,
//		                // [Notes]The reminder display abnormity after modify it
//		                if (mRminder == null) {
//		                    mRminder = new Note.Reminder();
//		                    mRminder.mReminderTime = NO_SET_REMINDER;
//		                    TctLog.i(TAG, "mRminder == null");
//		                }
//		                // [BUGFIX]-Mod-END by TCTNB.YuTao.Yang
//		                
//		                //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 begin
//		                isOtherActivity = true;
//		                //PR894260.After home key--recent key,not back to edit.Added by hz_nanbing.zou at 07/01/2015 end
//		                
////		                Intent intent = new Intent();
////		                intent.setClass(NoteEditorActivity.this, ReminderSettingActivity.class);
////		                startActivityForResult(intent, REQUEST_CODE_PICK_REMINDER);
//		                
//		                setDatePicker(2);
//					}
//				});
			 
			 
			 findViewById(R.id.reminder_date).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 setDatePicker(2);
				}
			});
			findViewById(R.id.reminder_time).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setTimePicker(2);
				}
			});

		 }
	 }
	//Added for View Mode at Ergo5.1.0.Added by hz_nanbing.zou at 15/01/2015
	 
	 
	 //Added new Datepicker/TimePicker for new Ergo
	 private Time t;
	 /**
	 * @param flag For tell which call this function.
	 * 			0 : means it is normal add a new;
	 * 			1 : means it is change screen call;
	 * 			2 : means it is from reminder
	 */
	private void setDatePicker(int flag){
		 if(t == null){
			 t = new Time();
		 }
		 t.setToNow();
		 switch(flag){
		 case 0:
			 dpd = new DatePickerDialog(this, new OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					// TODO Auto-generated method stub
					
					t.year = year;
					t.month = monthOfYear;
					t.monthDay = dayOfMonth;
					setTimePicker(0);
				}
			}, t.year, t.month, t.monthDay);
			 setMaxDate(dpd);
			 dpd.show();
			 break;
		 case 1:
			 if(dpd!=null){
			 if(dpd.isShowing()){
			 dpd.dismiss();
			 dpd = new DatePickerDialog(this, new OnDateSetListener() {
					
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					// TODO Auto-generated method stub
					
					t.year = year;
					t.month = monthOfYear;
					t.monthDay = dayOfMonth;
					if(!isFrom_Reminder)
						setTimePicker(0);
					else{
//						t.hour = hourOfDay;
//						t.minute = minute;
						newReminderTime = t.toMillis(true);
						
						 final long mil = System.currentTimeMillis();
						 
						 if(newReminderTime < mil){
			                    Toast.makeText(NoteEditorActivity.this, R.string.reminder_error,
			                            Toast.LENGTH_LONG).show();
			                    newReminderTime = 0;
							 return;
						 }
						
						(findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);
						setDate((TextView) findViewById(R.id.reminder_date), newReminderTime);
//						setTime((TextView) findViewById(R.id.reminder_time), newReminderTime);
					}
						
				}
			}, dpd.getDatePicker().getYear(), dpd.getDatePicker().getMonth(), dpd.getDatePicker().getDayOfMonth());	
			 setMaxDate(dpd);
			 dpd.show();
			 }
			 }
			 break;
		 case 2:
			 if(newReminderTime!=-1){
				 t.set(newReminderTime);
			 }else
				 t.set(mRminder.mReminderTime);
			 
			 if(newReminderTime == 0 || mRminder.mReminderTime == 0){
				 t.setToNow();
			 }
			 
			 dpd = new DatePickerDialog(this, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					// TODO Auto-generated method stub
					
					t.year = year;
					t.month = monthOfYear;
					t.monthDay = dayOfMonth;
//					setTimePicker(0);
					isFrom_Reminder = false;
//					t.hour = hourOfDay;
//					t.minute = minute;
					newReminderTime = t.toMillis(true);
					
					 final long mil = System.currentTimeMillis();
					 
					 if(newReminderTime < mil){
		                    Toast.makeText(NoteEditorActivity.this, R.string.reminder_error,
		                            Toast.LENGTH_LONG).show();
		                    newReminderTime = 0;
						 return;
					 }
					
					(findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);
					setDate((TextView) findViewById(R.id.reminder_date), newReminderTime);
//					setTime((TextView) findViewById(R.id.reminder_time), newReminderTime);
				}
			}, t.year, t.month, t.monthDay);
			 dpd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					isFrom_Reminder = false;
				}
			});
			 setMaxDate(dpd);
			 dpd.show();
			 break;
		 }
	 }
	 /**
	 * @param flag For which call this function
	 * 		  0:means from setDate normal;
	 * 		  1:means from change screen call;
	 * 		  2:means from change remidner
	 */
	private void setTimePicker(int flag){
		 TctLog.d("nb", "setTimePicker");
		 
		 switch(flag){
		 case 0:
			 tpd = new TimePickerDialog(this, new OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// TODO Auto-generated method stub
					
					t.hour = hourOfDay;
					t.minute = minute;
					newReminderTime = t.toMillis(true);
					
					 final long mil = System.currentTimeMillis();
					 
					 if(newReminderTime < mil){
		                    Toast.makeText(NoteEditorActivity.this, R.string.reminder_error,
		                            Toast.LENGTH_LONG).show();
		                    newReminderTime = 0;
						 return;
					 }
					
					(findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);
					setDate((TextView) findViewById(R.id.reminder_date), newReminderTime);
					setTime((TextView) findViewById(R.id.reminder_time), newReminderTime);
                 //PR948178 Add by Gu Feilong start
                      isOtherActivity = false;
                 //PR948178 Add by Gu Feilong end
				}
			}, t.hour, t.minute, DateFormat.is24HourFormat(this));
			 tpd.show();
			 break;
		 case 1:
			 if(tpd!=null){
			 if(tpd.isShowing()){
			 tpd.dismiss();
			 tpd = new TimePickerDialog(this, new OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// TODO Auto-generated method stub
					
					t.hour = hourOfDay;
					t.minute = minute;
					newReminderTime = t.toMillis(true);
					(findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);
					setDate((TextView) findViewById(R.id.reminder_date), newReminderTime);
					setTime((TextView) findViewById(R.id.reminder_time), newReminderTime);
				}
			}, t.hour, t.minute, DateFormat.is24HourFormat(this));
			 tpd.show();
			
		 }
			 }
			 break;
		 case 2:
			 if(t == null){
				 t = new Time();
			 }
//			 t.setToNow();
			 if(newReminderTime!=-1){
				 t.set(newReminderTime);
			 }else
				 t.set(mRminder.mReminderTime);
			 if(newReminderTime == 0 || mRminder.mReminderTime == 0){
				 t.setToNow();
			 }
			 tpd = new TimePickerDialog(this, new OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// TODO Auto-generated method stub
					
					t.hour = hourOfDay;
					t.minute = minute;
					newReminderTime = t.toMillis(true);
					
					 final long mil = System.currentTimeMillis();
					 
					 if(newReminderTime < mil){
		                    Toast.makeText(NoteEditorActivity.this, R.string.reminder_error,
		                            Toast.LENGTH_LONG).show();
		                    newReminderTime = 0;
						 return;
					 }
					
					(findViewById(R.id.reminder_layout)).setVisibility(View.VISIBLE);
//					setDate((TextView) findViewById(R.id.reminder_date), newReminderTime);
					setTime((TextView) findViewById(R.id.reminder_time), newReminderTime);
				}
			}, t.hour, t.minute, DateFormat.is24HourFormat(this));
			 tpd.show();
			 break;
		 }
}
	
	
	private String getNoteTime(){
		Cursor mCursor=this.getContentResolver().query(Note.CONTENT_URI, null, Note.COLUMN_ID+ " = " + mNote.mId, null,
	               null);
	        //PR841643 DateFormat as same as setting.Modified by hz_nanbing.zou at 19/11/2014 begin
	        String s_time = "";
	        SimpleDateFormat sdf;
	        if(mCursor!=null&& mCursor.getCount() > 0){
	                mCursor.moveToFirst();
	                Note mNote = NoteUtils.toNote(mCursor);
	                Date d = new Date(mNote.mModifyTime);
	               if (isToday(d)) {
	                 //PR860384 The time icon'：'looks so close to hour.Modified by hz_nanbing.zou at 03/12/2014 begin
	                 if(DateFormat.is24HourFormat(this)){
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
	               mCursor.close();
	}
	        return s_time;
	}
    private boolean isToday(Date a) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        return sdf2.format(today).equals(sdf2.format(a));
    }	
	private void delItselft(){
		//PR856793 The dialog is different from other.Modified by hz_nanbing.zou at 28/11/2014 begin
        AlertDialog.Builder builder = new AlertDialog.Builder(this/*,R.style.myDialog*/); 
        //PR856793 The dialog is different from other.Modified by hz_nanbing.zou at 28/11/2014 end

        builder.setMessage(getString(R.string.delete_this_item_title))
                .setPositiveButton(getString(R.string.btn_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            	NoteUtils.deleteNote(mNote.mId,
                                        NoteEditorActivity.this);
                            	NoteEditorActivity.this.finish();
                            }
                        }).setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
	}
	

	//Added for new Record func with Ergo5.1.2.Added bu hz_nanbing.zou at 2015/02/27
	boolean isFromPause;
	boolean isDiscard;
    //PR945404 It pop up FC after reject a call.Modified by Gu Feilong start
    Dialog d;
    private void setDialog(){
        View v = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        d = new AlertDialog.Builder(this).create();
    //PR945404 It pop up FC after reject a call.Modified by Gu Feilong end
		//d.create();
		d.setContentView(v);
		d.setCanceledOnTouchOutside(false);
		d.show();
		d.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				//For PR999702
				audioManager.abandonAudioFocus(mfl);
				
				tempTime = 0;
				if(isTimeout)
					return;
				
				if(!isDiscard){
					if (mMediaRecorder != null){
					onStopRecord();
					TctLog.e(TAG, "mMediaRecorder: " + mMediaRecorder);
					list.setVisibility(View.VISIBLE);
					setRecorderList();
					Recordstate = STATE_STOP_RECORD;
					}
				}else{
					//cancel the record
			        if (mMediaRecorder != null) {
			            try {
			                mMediaRecorder.stop();
			            } catch (Exception e){//modify by mingyue.wang for pr924240
			                TctLog.e(TAG, e.toString(), e);
			                return;
			            } finally {
			            	// 这里之前Activity是间接的被强引用，这里置空
			            	mMediaRecorder.setOnInfoListener(null);//added by xinyao.ye
			            	mMediaRecorder.reset();
			                mMediaRecorder.release();
			                mMediaRecorder = null;
			                if(mNewAudio.mUri!=null){
			                	File f= new File(mNewAudio.mUri);
			                	f.delete();
			                }
			            }
			        }
				}
			}
		});
		
		isFromPause = false;
		isDiscard = false;
		vRecordTime = (TextView) v.findViewById(R.id.recordtime);
		vRecordTips = (TextView) v.findViewById(R.id.recordtips);
		vRecordTips.setText(R.string.start_record);
		vRecordDiscard = (TextView) v.findViewById(R.id.recorddiscard);
		vRecordSave = (TextView) v.findViewById(R.id.recordsave);
		vRecordTap = (ImageButton) v.findViewById(R.id.recordtap);
		vRecordTap2 = (ImageButton) v.findViewById(R.id.recordtap2);

		vRecordDiscard.setText(" ");
		vRecordSave.setText(" ");
		
		vRecordTap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vRecordTap.setVisibility(View.GONE);
				vRecordTap2.setVisibility(View.VISIBLE);
				vRecordTips.setText(R.string.pause_record);
				
//				vRecordDiscard.setText(R.string.discard_record);
//				vRecordSave.setText(R.string.save_diarecord);
				{
					//start record
	                //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-7,PR888129
	                if(Playstate == STATE_START_PLAY){
	                      Playstate=STATE_STOP_OR_PAUSE_PLAY;
	                      for(int i=0;i<itemView.size();i++){
	                        audioListView audiolistview = (audioListView) itemView.get(i).getTag();
	                        audiolistview.audioPlay.setImageResource(R.drawable.ic_play_arrow_grey600_24dp);
	                        audiolistview.start_time.setVisibility(View.GONE);
	                        audiolistview.total_time.setVisibility(View.GONE);
	                        audiolistview.total_time_before.setVisibility(View.VISIBLE);
	                        }
	                      mHandler.removeMessages(MSG_WHAT_UPDATE_PLAY_PROGRESS);
	                      MediaPause = true;
	                       if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
	                           mMediaPlayer.pause();
	                           }
	                }
	              //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-7,PR888129
					
					
					
	                //[BUGFIX]-ADD-BEGIN by wenlu.wu,11/26/2013,PR-558068
	                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	                    date_view.setVisibility(View.GONE);
	                    Toast.makeText(NoteEditorActivity.this, R.string.insert_sd_card, Toast.LENGTH_LONG).show();
	                    return;
	                }
	                
	                //PR900585 FC at Add record when get a call.Added by hz_nanbing.zou at 14/01/2015 begin 
	                if(mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
	                	Toast.makeText(NoteEditorActivity.this, R.string.record_incall, Toast.LENGTH_SHORT).show();
	                	return;
	                }
	                //PR900585 FC at Add record when get a call.Added by hz_nanbing.zou at 14/01/2015 end
	                if(!isFromPause)
	                	onStartRecord();
	                else{
	                	audioManager.requestAudioFocus(mfl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
	                	try{
	                	mMediaRecorder.start();
	                    Message msg = new Message();
	                    msg.what = MSG_WHAT_SWITCH_RECORD_ICON;
	                    msg.arg1 = 0;
	                    mHandler.sendMessage(msg);
	                	}catch(IllegalStateException e){//For PR963390
		                    Message msg = new Message();
		                    msg.what = STATE_RECORD_ERROR;
		                    msg.arg1 = 0;
		                    mHandler.sendMessage(msg);
	                	}
	                }
	                Recordstate = STATE_START_RECORD;
	                // TctLog.e(TAG, "state222: " + Recordstate);	
				}
			}
		});
		vRecordTap2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vRecordTap.setVisibility(View.VISIBLE);
				vRecordTap2.setVisibility(View.GONE);
				
				vRecordDiscard.setVisibility(View.VISIBLE);
				vRecordSave.setVisibility(View.VISIBLE);
				vRecordDiscard.setText(R.string.discard_record);
				vRecordSave.setText(R.string.save_diarecord);
				
				vRecordTips.setText(R.string.continue_record);	
				isFromPause = true;
				//pause record
				onPauseRecord();
			}
		});
		vRecordDiscard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isDiscard = true;
				d.dismiss();
				//cancel the record
			}
		});
		vRecordSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				onContinueRecord();
				d.dismiss();
				//save the record
			}
		});
	}
	
	private String getRealPath(Uri uri){
		
		Cursor c = this.getContentResolver().query(uri, null, null, null, null);
		String s = null;
		if(c!=null && c.moveToFirst()){
			
			int index = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			s = c.getString(index);
		}
		c.close();
		TctLog.d("nb","record path:"+s);
		return s;
	}

	private void setMaxDate(DatePickerDialog dpDialog){//for PR958920
        Calendar c = Calendar.getInstance();
        c.set(2037, 11, 31, 23, 59, 59);//For PR1021166
        dpDialog.getDatePicker().setMaxDate( c.getTimeInMillis());
        
        c.set(1970, 0, 1, 0, 0, 0);//For PR1022085
        dpDialog.getDatePicker().setMinDate( c.getTimeInMillis());
	}
	
	private ProgressDialog mprogressd;
	private void showLoading(boolean flag){
		if((getWindow() != null && getWindow().isActive()) && !isFinishing()){//For PR980247
		
		if(flag){
			if(mprogressd ==null)
				mprogressd = new ProgressDialog(this);
			mprogressd.setCancelable(false);
			mprogressd.setCanceledOnTouchOutside(false);
			mprogressd.setMessage(getResources().getString(R.string.loading));
			mprogressd.show();
				
		}else{
			if(mprogressd !=null && mprogressd.isShowing()){
				mprogressd.dismiss();
				mprogressd = null;
              }
		}
		}
	}
	private class InsertImageAsyncTask extends AsyncTask<Intent, Integer, Boolean>{

		private Uri uri;
		@Override
		protected Boolean doInBackground(Intent... params) {
			// TODO Auto-generated method stub
	        uri = params[0].getData();
	        if (Intent.ACTION_SEND.equals(params[0].getAction()))
	            if (params[0].getExtras().containsKey(Intent.EXTRA_STREAM)) {
	                uri = (Uri) params[0].getExtras().getParcelable(Intent.EXTRA_STREAM);
	            }
	        //[BUGFIX]-Add-BEGIN by yang.zhang3,2014-12-18,PR871586
	        if (uri != null){
	            File fpath=Environment.getDataDirectory();
	            long availableBlocks=fpath.getUsableSpace();
	            long fileSize=0;
	            try {
	                fileSize = getContentResolver().openInputStream(uri).available();
	            } catch (FileNotFoundException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	                return false;
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	                return false;
	            }
	            Log.d(TAG, "memoryinfo---devices space="+availableBlocks+"-------fileSize="+fileSize);
	            if(availableBlocks<=fileSize){
	                Log.d(TAG, "devices has no space");
	                Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.msg_devices_no_space), Toast.LENGTH_SHORT).show();
	                return false;
	            }
	          //[BUGFIX]-Add-END by yang.zhang3,2014-12-18,PR871586
//	            mNoteEditor.insertImage(uri);
	        }
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if(result){
				 mNoteEditor.insertImage(uri);
			}else{
				Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.toast_loadPicture), Toast.LENGTH_SHORT).show();
			}
			mHandler.sendEmptyMessage(MSG_WHAT_END_INSERT_IMAGE);
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.toast_loadPicture), Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessage(MSG_WHAT_END_INSERT_IMAGE);
		}
		
	}

	private boolean isEnoughSpace(int flag) {//For PR980628

	    File path ;
	    if(flag == 0)
	    	path = Environment.getExternalStorageDirectory();// Environment.getDataDirectory();
	    else
	    	path = Environment.getDataDirectory();

	    Log.d(TAG, "path == " + path);

	    StatFs stat = new StatFs(path.getPath());

	    long blockCount = stat.getBlockCount();

	    long blockSize = stat.getBlockSize();

	    long availableBlocks = stat.getAvailableBlocks();
//	    double rate = (double)availableBlocks / (double)blockCount;
	    long rate = availableBlocks * blockSize;
	    Log.d("nb", "total size = " + (blockCount * blockSize) + ", available size = " + (availableBlocks * blockSize) + ", rate == " + rate);
	    
	    if(flag == 0)
	    	return rate >= 1024*1024*30;
	    else
	    	return rate >= 1024*1024*5;

	  }

	
}

