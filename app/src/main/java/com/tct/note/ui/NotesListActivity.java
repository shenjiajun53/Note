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
/* 07/30/2014|        gerong        |      PR-689446       |Select notes can't marked    */
/*           |                      |                      |after press home key         */
/* ----------|----------------------|----------------------|---------------------------- */
/* 08/07/2014|       lin.peng       |       PR694352       |The "Create" button*/
/*           |                      |                      |keeps in down left */
/*           |                      |                      |corner if press "Search" /
/* ----------|----------------------|----------------------|----------------- */
/* 03/12/2015|       Gu Feilong     |      PR-947952       |After sorting by  */
/*           |                      |                      |colour,press other*/
/*           |                      |                      |space,Notes happen*/
/*           |                      |                      | to force close   */
/* ----------|----------------------|----------------------|---------------------------- */
/******************************************************************************/

package com.tct.note.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.note.Constants;
import com.tct.note.R;
import com.tct.note.data.Note;
import com.tct.note.data.Note.Group;
import com.tct.note.data.NoteProvider;
import com.tct.note.data.NoteUtils;
import com.tct.note.util.TctLog;
import com.tct.note.util.setMenuIconUtils;
//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
//PR 825456 show the search match text.Added by hz_nanbing.zou at 06/11/2014 begin
//PR 825456 show the search match text.Added by hz_nanbing.zou at 06/11/2014 end
//import android.widget.SearchView.SearchAutoComplete;


public class NotesListActivity extends AppCompatActivity implements OnItemClickListener {
    public static final String TAG = "NoteListActivity";
    private TextView count;
    GridView gridview;
    Cursor myCursor = null;
    private final int QUERYID_GRID = 0;
    private final int QUERYID_GRID_GROUP = 1;
    private final int QUERYID_GRID_GROUP_CHID = 2;
    private static String mOrder = Note.COLUMN_MODIFY_TIME + " DESC";
    private ArrayList<Integer> mSelectedItemIds;
    private MenuItem mSearchItem;
    //[BUGFIX]-Add-BEGIN by TSCD.lin.peng,08/7/2014,694532
//    private MenuItem add;
    
    private int GuestMode_Indicator = 0;
    
    
    //[BUGFIX]-Add-END by TSCD.lin.peng
    //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-689446,
    //Select notes can't marked after press home key
    private int delNoteCount = 0;
    //[BUGFIX]-Mod-END by TSCD.gerong
    private SearchView mSearchView;
    private String Search_query;
    private View SipnnerView;
    private ActionBar actionBar;
    private SortDropdownPopup mSortDropdownPopup;
    private NoteCursorAdapter myAdapter;
    public static int DISPLAY_NORMAL = 0;
    public static int DISPLAY_GROUP = 1;
    public static int DISPLAY_GROUP_CHILD = 2;
    private static int sortByColor = DISPLAY_NORMAL;
    private static int display_groupId = 0;
    public static final String[] ITEM_WITH_GROUP_CHILD_PROJECTION = new String[] {
            Note.COLUMN_ID, Note.COLUMN_MODIFY_TIME, Note.COLUMN_HAS_REMINDER,
            Note.COLUMN_HAS_TEXT, Note.COLUMN_SMALL_THUMBNAIL_URI, Note.COLUMN_GROUP_ID,
            Note.COLUMN_HAS_IMAGE, Note.COLUMN_HAS_AUDIO, Note.COLUMN_BG_IMAGE_RES_ID
    };

    //PR866920,Pop up "Touch this button to create your first note" While has a note in Notes.Modified by hz_nanbing.zou at 10/12/2014 begin
    ImageView iv;
    //PR866920,Pop up "Touch this button to create your first note" While has a note in Notes.Modified by hz_nanbing.zou at 10/12/2014 end
    
    //PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 begin
    private boolean mSearchResult_flag = false;
    private ImageButton mFloatingBtn_add;
    private boolean isClick = false;//modify by mingyue.wang for pr942664
    //PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 end
    //modify by mingyue.wang for pr949159 begin
    private ProgressDialog mProgressDialog;
    private ActionMode mMode;
    //modify by mingyue.wang for pr949159 end
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-17,PR898848
        //modify by mingyue.wang for pr948002 begin
        //deleteEmptyNote();
        //modify by mingyue.wang for pr948002 end
        //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-17,PR898848
        //actionBar = getActionBar();
        //setupActionBar();
        setupToolBar();
        setSupportActionBar(toolbar);
        showGridView(0);
//        boolean isfirstEnter = isFirstEnter(this, this.getClass().getName());
//        if (isfirstEnter) {
//            displayDialog();
//        }

        Intent ii = this.getIntent();
        boolean bb = ii.getBooleanExtra("from_notify", false);
        if(bb){
        	Intent it = new Intent("reminder_overwrite");
			sendBroadcast(it);
        }

    }
    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-17,PR898848
    public void  deleteEmptyNote(){
       String selection ="has_image=0 and has_audio=0 and has_reminder=0 and has_text=0";
       Cursor mCursor = getContentResolver().query(Note.CONTENT_URI, null, selection, null,
               null);
       if (mCursor != null && mCursor.getCount() > 0){
           while(mCursor.moveToNext()){
               long id = mCursor.getLong(mCursor.getColumnIndex(Note.COLUMN_ID));
               getContentResolver().delete(Note.CONTENT_URI, Note.COLUMN_ID + " = " + id, null);
           }
       }
       mCursor.close();
    }
    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-17,PR898848

    @SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public boolean isFirstEnter(Context context, String classname) {
        if (context == null || classname == null || "".equals(classname))
            return false;
        @SuppressWarnings("static-access")
		String str = context.getSharedPreferences("my_pref", context.MODE_WORLD_READABLE)
                .getString("guide_activity", "");
        TctLog.e(TAG, "str: " + str);
        if (str.equalsIgnoreCase("false"))
            return false;
        else
            return true;

    }

//    public void displayDialog() {
//        final Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);
//        dialog.setContentView(R.layout.translucence_dialog);
//        iv = (ImageView) dialog.findViewById(R.id.ivNavigater_bottom);
//        iv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                com.tct.note.util.TctLog.e(TAG, "full screen dialog onclick");
//                setGuide();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

    @SuppressWarnings("static-access")
	public void setGuide() {
        SharedPreferences settings = this.getSharedPreferences("my_pref", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("guide_activity", "false");
        editor.commit();
    }

    private void setupToolBar() {
//        ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.note_list_actionbar,
//                null);
//        // Deleted by zhaozhao.li at 2014-08-19 begin
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
//                | ActionBar.DISPLAY_SHOW_HOME);
//        // Deleted by zhaozhao.li at 2014-08-19 end
//        actionBar.setCustomView(v, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT));
//        actionBar.setDisplayShowHomeEnabled(false);//For Ergo 5.1.0

        //actionBar.setIcon(R.drawable.ic_description_white);

        ViewGroup view= (ViewGroup) getLayoutInflater().inflate(R.layout.note_list_toolbar,null);
        toolbar= (Toolbar) view.findViewById(R.id.note_list_toolbar);

//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
//        actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
//        actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
        SipnnerView = view.findViewById(R.id.sort_spinner);
//        mSortDropdownPopup = new SortDropdownPopup(this);
//
//        //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//        /*mSortDropdownPopup.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.simple_expandable_list_item_1, getData()));*/
//        mSortDropdownPopup.setAdapter(new SortAdapter(this, getData()));
//        //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
//
        count = (TextView) view.findViewById(R.id.unread_conv_count);
        count.setText("" + getCount());
//        SipnnerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSortDropdownPopup.show();
//            }
//        });
    }
    private void setupActionBar() {
        ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.note_list_actionbar,
                null);
        // Deleted by zhaozhao.li at 2014-08-19 begin
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        // Deleted by zhaozhao.li at 2014-08-19 end
        actionBar.setCustomView(v, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT));
        actionBar.setDisplayShowHomeEnabled(false);//For Ergo 5.1.0
        
        //actionBar.setIcon(R.drawable.ic_description_white);
        
        
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
//        actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
//        actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
        SipnnerView = v.findViewById(R.id.sort_spinner);
//        mSortDropdownPopup = new SortDropdownPopup(this);
//
//        //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//        /*mSortDropdownPopup.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.simple_expandable_list_item_1, getData()));*/
//        mSortDropdownPopup.setAdapter(new SortAdapter(this, getData()));
//        //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
//
        count = (TextView) v.findViewById(R.id.unread_conv_count);
        count.setText("" + getCount());
//        SipnnerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSortDropdownPopup.show();
//            }
//        });
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        data.add(this.getResources().getString(R.string.sort_by_time));
        data.add(this.getResources().getString(R.string.sort_by_color));
        return data;
    }

    // Based on Spinner.DropdownPopup
    private class SortDropdownPopup extends ListPopupWindow {
        public SortDropdownPopup(Context context) {
            super(context);
            setAnchorView(SipnnerView);
            setModal(true);
            setPromptPosition(POSITION_PROMPT_ABOVE);
            setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    onSpinnerItemClicked(position);
                    dismiss();
                }
            });
        }

        private void onSpinnerItemClicked(int position) {
            // TODO Auto-generated method stub
            if (position == 0) {
                sortByColor = DISPLAY_NORMAL;
                mOrder = Note.COLUMN_MODIFY_TIME + " DESC";
                showGridView(0);
            } else {
                mOrder = Note.Group.COLUMN_ID + " DESC";
                sortByColor = DISPLAY_GROUP;
                showGridView(0);
            }

            //PR845766 After sort by color,count error.Modified by hz_nanbing.zou at 25/11/2014 begin
            count.setText("" + getCount());
            //PR845766 After sort by color,count error.Modified by hz_nanbing.zou at 25/11/2014 end

        }

        @Override
        public void show() {

			//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
			setHorizontalOffset(-(SipnnerView.getRight()));
			setWidth(NotesListActivity.this.getWindowManager().getDefaultDisplay().getWidth()/2+12);
			setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
            super.show();
            // List view is instantiated in super.show(), so we need to do this
            // after...
            
            //PR848145  Sort by,it will show a scroll bar.Added by hz_nanbing.zou at 21/11/2014 begin
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
           // getListView().setDivider(getResources().getDrawable(R.drawable.notes_menu_bg_hline));
            //PR848145  Sort by,it will show a scroll bar.Added by hz_nanbing.zou at 21/11/2014 end
            
            //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

        }
    }

    public int getCount() {
        int count = 0;
        if (sortByColor != DISPLAY_GROUP_CHILD) {
            Cursor c = getCursor(QUERYID_GRID, null, display_groupId);
            if (c != null) {
                count = c.getCount();
                c.close();
            }
        } else {
            Cursor c = getCursor(DISPLAY_GROUP_CHILD, null, display_groupId);
            if (c != null) {
                count = c.getCount();
                c.close();
            }
        }
        return count;
    }
    //add by mingyue.wang for pr948002 begin
    @Override
    protected void onStart() {
       if(!isClick){
           deleteEmptyNote();//for PR960419
           //PR1041997 START It will flash the notes screen after unlock the screen. update by lxl july 13 2015
           if (!(mSearchView != null && mSearchView.isShown())) {//PR1041997 END
               showGridView(display_groupId);//modify by mingyue.wang for pr948466
           }

       }
       super.onStart();
    }
   //add by mingyue.wang for pr948002 end
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
      //PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 begin
        /*if(mSearchItem.isActionViewExpanded()){
        	mSearchItem.collapseActionView();
       }*/
      //PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 end
        
        
        //PR866920,Pop up "Touch this button to create your first note" While has a note in Notes.Modified by hz_nanbing.zou at 10/12/2014 begin
        SharedPreferences send = this.getSharedPreferences("send", this.MODE_PRIVATE);
        if(send.getBoolean("send", false))
        	if(iv!=null)
        	iv.performClick();
        //PR866920,Pop up "Touch this button to create your first note" While has a note in Notes.Modified by hz_nanbing.zou at 10/12/2014 end
        
        //PR846513  After "Home" or "Lock Screen",searchview text can not be save.Modified by hz_nanbing.zou at 20/11/2014 begin
        //invalidateOptionsMenu();
        //PR846513  After "Home" or "Lock Screen",searchview text can not be save.Modified by hz_nanbing.zou at 20/11/2014 end
        
        //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-689446,
        //Select notes can't marked after press home key
        //PR1041997 START It will flash the notes screen after unlock the screen.update by lxl july 13 2015
        if (delNoteCount == 0&&!(mSearchView!=null&&mSearchView.isShown())) {//PR1041997 END
            showGridView(display_groupId);
        }
        //[BUGFIX]-Mod-END by TSCD.gerong
        if(!isClick)//For PR960419
        count.setText("" + getCount());
        
        //PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 begin
        if(mSearchResult_flag){//For back from SearchResultActivity
        	mSearchResult_flag = false;
        	if(mSearchItem!=null && mSearchItem.isActionViewExpanded())	
        	{
        		mSearchItem.collapseActionView();
        	}
        }else{//For back from Home_Key\Recent_Key
        	if(mSearchItem!=null && mSearchItem.isActionViewExpanded())
        		{
        			gridview.setVisibility(View.GONE);
        			//Do not show "add" when search item expand.
        			//mFloatingBtn_add.setVisibility(View.GONE);//New GD should show add when search
        	}
        }
        //PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 end
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myCursor != null) {
            myCursor.close();
        }
      //For PR984232
        NoteProvider.closeDeldb();
        NoteProvider.closeupdatedb();
    }

    @Override
    public void onBackPressed() {
        if (sortByColor == DISPLAY_GROUP_CHILD) {
            sortByColor = DISPLAY_GROUP;
            Search_query = null;
            showGridView(display_groupId);
            count.setText("" + getCount());
        } else {
            Search_query = null;
            showGridView(display_groupId);
            count.setText("" + getCount());
            super.onBackPressed();
        }
    }

    // /*
    // * Show the view in grid mode.
    // */
    private void showGridView(int groupId) {
        setContentView(R.layout.grid_view);
        
//        initAddBtn(true);
        
        if (myCursor != null) {
            myCursor.close();
        }
        gridview = (GridView) findViewById(R.id.grid);
        gridview.setOnItemClickListener(NotesListActivity.this);
        
        
        if(isLandScape()){
        	gridview.setNumColumns(3);
        }else{
        	gridview.setNumColumns(2);
        	if(sortByColor == DISPLAY_GROUP && getCount() != 1)//For PR960442
        	{
        		gridview.setHorizontalSpacing(10);
        		gridview.setPadding(5, gridview.getPaddingTop(), 5, 0);
        	}
        	else{
        		
        	}
        }
        
        
        //PR855412-jin.chen-begin-001
        // if (sortByColor != DISPLAY_GROUP_CHILD) {
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridview.setMultiChoiceModeListener(new ModeCallback());
        // }
        TextView No_Note = (TextView) findViewById(R.id.zeroshowing);
        if (sortByColor == DISPLAY_GROUP) {
            myCursor = getCursor(QUERYID_GRID_GROUP, Group.COLUMN_NAME + " ASC", 0);
        } else if (sortByColor == DISPLAY_GROUP_CHILD) {
            myCursor = getCursor(QUERYID_GRID_GROUP_CHID, mOrder, groupId);
            if (getCount() != 1 && getCount() > 0){
                myCursor = addItemToGroupChild(myCursor);
            } else if (getCount() == 1) {
                sortByColor = DISPLAY_GROUP;
                showGridView(0);
            }
        } else {
            myCursor = getCursor(QUERYID_GRID, mOrder, 0);
        }
        if (myCursor == null || myCursor.getCount() <= 0){
            No_Note.setVisibility(View.VISIBLE);
        }
        //PR855412-jin.chen-end-001
        myAdapter = new NoteCursorAdapter(this, R.layout.cardview_item, myCursor, sortByColor);
        
        //PR 837433 Guest mode can be display.Modified by hz_nanbing.zou at 19/11/2014 begin
        if(NoteUtils.isGuestModeOn()){
        	if(GuestMode_Indicator == 0){
        		GuestMode_Indicator = 1;
        		Toast.makeText(this, R.string.guest_mode_creat, Toast.LENGTH_SHORT).show();
        	}
        	
        }else{
        	gridview.setAdapter(myAdapter);
        	initAddBtn(true);//Guest should can not see the button
        }
        //PR 837433 Guest mode can be display.Modified by hz_nanbing.zou at 19/11/2014 end

    }

    public MatrixCursor addItemToGroupChild(Cursor c) {
        android.database.MatrixCursor mCursor = new android.database.MatrixCursor(
                ITEM_WITH_GROUP_CHILD_PROJECTION);
        mCursor.addRow(new Object[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0
        });
        c.moveToPosition(-1);
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(Note.COLUMN_ID));
            long time = c.getLong(c.getColumnIndex(Note.COLUMN_MODIFY_TIME));
            int hasReminder = c.getInt(c.getColumnIndex(Note.COLUMN_HAS_REMINDER));
            int hasText = c.getInt(c.getColumnIndex(Note.COLUMN_HAS_TEXT));
            int hasAudio = c.getInt(c.getColumnIndex(Note.COLUMN_HAS_AUDIO));
            int hasImage = c.getInt(c.getColumnIndex(Note.COLUMN_HAS_IMAGE));
            String smail_uri = c.getString(c.getColumnIndex(Note.COLUMN_SMALL_THUMBNAIL_URI));
            int groupId = c.getInt(c.getColumnIndex(Note.COLUMN_GROUP_ID));
            int bg_id = c.getInt(c.getColumnIndex(Note.COLUMN_BG_IMAGE_RES_ID));
            mCursor.addRow(new Object[] {
                    id, time, hasReminder, hasText, smail_uri, groupId, hasImage, hasAudio, bg_id
            });
        }
        return mCursor;
    }

    private Cursor getCursor(int queryID, String orderStr, int groupId) {
        String orderString;
        orderString = orderStr;
        Cursor mCursor = null;
        switch (queryID) {
            case QUERYID_GRID: {
                mCursor = getContentResolver().query(Note.CONTENT_URI, null, null, null,
                        orderString);
                break;
            }
            case QUERYID_GRID_GROUP: {
                mCursor = getContentResolver().query(Note.Group.CONTENT_URI, null, Note.Group.COLUMN_NAME +" != 0" , null,
                        orderString);
                break;
            }
            case QUERYID_GRID_GROUP_CHID: {
                mCursor = getContentResolver().query(Note.CONTENT_URI, null,
                        Note.COLUMN_GROUP_ID + " = " + groupId, null, orderString);
                break;
            }
        }
        return mCursor;
    }

    
    static final int MENU_ONE = 1;
    static final int MENU_TWO = 2;
    Menu myMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	
    	setMenuIconUtils.setIconEnable(menu, true);
    	
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_idol3, menu);
//
//        mSearchItem = menu.findItem(R.id.search);
        
    	mSearchItem = menu.add(MENU_ONE, Menu.FIRST, Menu.NONE, R.string.search_label);
    	mSearchItem.setIcon(R.drawable.ic_search_white);
    	mSearchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|
    			MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    	mSearchItem.setActionView(new SearchView(this));
    	
    	SubMenu sm = menu.addSubMenu(MENU_TWO, Menu.FIRST+1, Menu.NONE, R.string.sort);
    	sm.setIcon(R.drawable.ic_ab_sort_white_24dp);
    	sm.add(MENU_TWO, Menu.FIRST+2, Menu.NONE, R.string.sort_by_time);
    	sm.add(MENU_TWO, Menu.FIRST+3, Menu.NONE, R.string.sort_by_color);
    	menu.findItem(Menu.FIRST+1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem arg0) {
                adpaterSelViewAndPpwWidth();//PR1041987 touch the search btn,need change width
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem arg0) {
                //PR 825446 auto show search text.Added by hz_nanbing.zou at 5/11/2014 begin
                setAutoMatch("");
                //PR 825446 auto show search text.Added by hz_nanbing.zou at 5/11/2014 end

                gridview.setVisibility(View.VISIBLE);

                //Do not show "add" when search item expand.
                //mFloatingBtn_add.setVisibility(View.VISIBLE);//New GD should show add when search
                //Do not show "add" when search item expand.

                myMenu.setGroupVisible(MENU_TWO, true);

                return true;
            }
        });

        mSearchView.setOnQueryTextListener(mQueryTextListener);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnSuggestionListener(SuggestionListener);

		//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
		setIcon(mSearchView);
		//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null) {
            SearchableInfo info = searchManager.getSearchableInfo(this.getComponentName());
            mSearchView.setSearchableInfo(info);
        }
        
        myMenu = menu;
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
/*            case R.id.search: 
                onSearchRequested();
                return true;
            case R.id.sort1:
            	sortByColor = DISPLAY_NORMAL;
                mOrder = Note.COLUMN_MODIFY_TIME + " DESC";
                showGridView(0);
                	break;
            case R.id.sort2:
                mOrder = Note.Group.COLUMN_ID + " DESC";
                sortByColor = DISPLAY_GROUP;
                showGridView(0);
                	break;*/
        
        case Menu.FIRST:
        	onSearchRequested();
        	myMenu.setGroupVisible(MENU_TWO, false);
            return true;
        case Menu.FIRST+2:
        	sortByColor = DISPLAY_NORMAL;
            mOrder = Note.COLUMN_MODIFY_TIME + " DESC";
            showGridView(0);
            	break;
        case Menu.FIRST+3:
            mOrder = Note.Group.COLUMN_ID + " DESC";
            sortByColor = DISPLAY_GROUP;
            showGridView(0);
            	break;
            
        }
        return false;
    }

    @Override
    public boolean onSearchRequested() {
        if (mSearchItem != null) {
            mSearchItem.expandActionView();
            gridview.setVisibility(View.GONE);
            
            //Do not show "add" when search item expand.
            //mFloatingBtn_add.setVisibility(View.GONE);//New GD should show add when search
            //Do not show "add" when search item expand.
            
            //[BUG-FIX] 575796 begin
            // Deleted by zhaozhao.li at 2014-08-20 begin 002
//            ImageView iv = (ImageView) mSearchView.findViewById(com.android.internal.R.id.search_mag_icon);
//            iv.setImageResource(R.drawable.ic_search);
            // Deleted by zhaozhao.li at 2014-08-20 end 002
            //[BUG-FIX] 575796 end
        }
        return true;
    }

    SearchView.OnSuggestionListener SuggestionListener = new SearchView.OnSuggestionListener() {

        @Override
        public boolean onSuggestionSelect(int position) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean onSuggestionClick(int position) {
            // TODO Auto-generated method stub
            int noteId = 0;
            String selection2 = Note.Text.COLUMN_CONTENT + " like '%" + Search_query + "%' ";
            Cursor c = getContentResolver().query(Note.Text.CONTENT_URI, null, selection2, null,
                    null);
            if (c != null) {
                c.moveToPosition(position);
                noteId = c.getInt(c.getColumnIndex(Note.Text.COLUMN_NOTE_ID));
                c.close();
            }
            Intent intent = new Intent("com.tct.note.action.VIEW");
            intent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
            startActivity(intent);
            return true;
        }

    };
    SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newText) {
            // TODO Auto-generated method stub
            Search_query = newText;

            if(NoteUtils.isGuestModeOn()){
            	
            }else{

            //PR 825456 show the search match text.Added by hz_nanbing.zou at 06/11/2014 begin
            if(NotesListActivity.this.getWindow().isActive())
            	setAutoMatch(newText);
            //PR 825456 show the search match text.Added by hz_nanbing.zou at 06/11/2014 end
            }

            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            // TODO Auto-generated method stub
        	
        	//PR 837433 Guest mode can be display.Modified by hz_nanbing.zou at 19/11/2014 begin
        	if(!NoteUtils.isGuestModeOn()){
        		
        		//PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 begin
        		mSearchResult_flag = true;
        		//PR895494,Search dismiss after Home\Recent key.Added by hz_nanbing.zou at 08/01/2015 end
        		
                Intent intent = new Intent();
                intent.setClass(NotesListActivity.this, SearchResultActivity.class);
                intent.putExtra(SearchManager.QUERY, query);
                startActivity(intent);

        	}else{
        		
        		//PR 854921 Guest mode prompt.Modified by hz_nanbing.zou at 27/11/2014 end
        		Toast.makeText(NotesListActivity.this, R.string.guest_mode_search, Toast.LENGTH_SHORT).show();
        		//PR 854921 Guest prompt.Modified by hz_nanbing.zou at 27/11/2014 end
        		
        	}
        	//PR 837433 Guest mode can be display.Modified by hz_nanbing.zou at 19/11/2014 end

            return true;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        if (sortByColor == DISPLAY_GROUP) {
            long Groupcount = myCursor.getLong(myCursor.getColumnIndex(Note.Group.COLUMN_NAME));
            long GroupId = myCursor.getLong(myCursor.getColumnIndex(Note.Group.COLUMN_ID));
            display_groupId = (int) GroupId;
            if (Groupcount == 1) {
                Cursor c = getContentResolver().query(Note.CONTENT_URI, null,
                        Note.COLUMN_GROUP_ID + " == ?", new String[] {
                            String.valueOf(GroupId)
                        }, null);
          //PR947952 Add by Gu Feilong start
                try{

                   if(c.getCount()<1){
                        return;
                    }
                    c.moveToFirst();
                    int noteId = c.getInt(c.getColumnIndex(Note.COLUMN_ID));
                    Intent intent = new Intent("com.tct.note.action.VIEW");
                    intent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
                    startActivity(intent);
                }catch(CursorIndexOutOfBoundsException ex){
                    TctLog.e(TAG, "onItemClick has CursorIndexOutOfBoundsException");
                    ex.fillInStackTrace();
                }catch(Exception e){
                    TctLog.e(TAG, "onItemClick has Exception " );
                    e.fillInStackTrace();
                }finally{
                	if(c!=null)
                		c.close();
                }
              //PR947952 Add by Gu Feilong end
            } else {
                sortByColor = DISPLAY_GROUP_CHILD;
                mOrder = Note.COLUMN_CREATE_TIME + " DESC";
                showGridView((int) GroupId);
                count.setText("" + getCount());
            }
            return;
        }
        if (sortByColor == DISPLAY_GROUP_CHILD) {
            if (position == 0) {
                sortByColor = DISPLAY_GROUP;
                mOrder = Note.Group.COLUMN_ID + " DESC";
                showGridView(0);
                count.setText("" + getCount());
            } else {
                myCursor.moveToPosition(position);
                int noteId = myCursor.getInt(myCursor.getColumnIndex(Note.COLUMN_ID));
                Intent intent = new Intent("com.tct.note.action.VIEW");
                intent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
                startActivity(intent);
            }
            return;
        } else {
            myCursor.moveToPosition(position);
            int noteId = myCursor.getInt(myCursor.getColumnIndex(Note.COLUMN_ID));
            Intent intent = new Intent("com.tct.note.action.VIEW");
            intent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
            startActivity(intent);
        }

    }

    private class ModeCallback implements GridView.MultiChoiceModeListener {
        private View mMultiSelectActionBarView;
        private TextView mSelectedCount;
        private int deletecount;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            mSelectedItemIds = new ArrayList<Integer>();
            inflater.inflate(R.menu.note_delete_menu, menu);
            if (mMultiSelectActionBarView == null) {
                mMultiSelectActionBarView = LayoutInflater.from(NotesListActivity.this).inflate(
                        R.layout.delete_item_action_bar, null);

                mSelectedCount = (TextView) mMultiSelectActionBarView
                        .findViewById(R.id.selected_conv_count);
            }
            mode.setCustomView(mMultiSelectActionBarView);
            
            //PR906822.Select mode should not add new.Added by hz_nanbing.zou at 19/01/2015 begin
            mFloatingBtn_add.setVisibility(View.GONE);
            //PR906822.Select mode should not add new.Added by hz_nanbing.zou at 19/01/2015 end
            isClick=true;//modify by mingyue.wang for pr942664
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (mMultiSelectActionBarView == null) {
                ViewGroup v = (ViewGroup) LayoutInflater.from(NotesListActivity.this).inflate(
                        R.layout.delete_item_action_bar, null);
                mode.setCustomView(v);

                mSelectedCount = (TextView) v.findViewById(R.id.selected_conv_count);
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_gridview_item:
                    if (mSelectedItemIds.size() > 0) {
                        String title;
                        if ((mSelectedItemIds.size() == 1)) {
                            title = getString(R.string.delete_this_item_title);
                        }
                        else {
                            title = getString(R.string.delete_these_item_title);
                            //PR856793 The dialog is different from other.Modified by hz_nanbing.zou at 28/11/2014 begin
                            title = title+"                    ";//make 4*5  space for show the dialog title,when the title is too short
                        }
                        
                        //PR871227 The prompt is incorrect when delete all notes.Added by hz_nanbing.zou at 15/12/2014 begin
                        if((mSelectedItemIds.size() == getCount())){
                        	title = getString(R.string.delete_all);
                        	}
                        //PR871227 The prompt is incorrect when delete all notes.Added by hz_nanbing.zou at 15/12/2014 end
                        
                        //PR892567.Delete note by color folder.Added by hz_nanbing.zou at 05/01/2015 begin
                        if ((sortByColor == DISPLAY_GROUP)){
                        	if ((mSelectedItemIds.size() == 1)) {
                        		if(NoteUtils.isEnglishLan())
                                title = getString(R.string.delete_this_color_item_title);
                                
                                int groupId = mSelectedItemIds.get(0);
                                
                                Cursor c = getCursor(QUERYID_GRID_GROUP_CHID,mOrder, groupId);
                                int count = 0;
                                if(c != null){
                                 count  = c.getCount();
                                 c.close();
                                }
                                
                                if(count == 1){
                                	title = getString(R.string.delete_this_item_title);
                                }
                            }
                            else {
                            	if(NoteUtils.isEnglishLan())
                                title = getString(R.string.delete_these_color_item_title);
                                //PR856793 The dialog is different from other.Modified by hz_nanbing.zou at 28/11/2014 begin
                                title = title+"                    ";//make 4*5  space for show the dialog title,when the title is too short
                            }
                        	Cursor c = getCursor(QUERYID_GRID_GROUP, Group.COLUMN_NAME + " ASC", 0);
                        	int count = 0;
                        	if(c !=null){
                        		count = c.getCount();
                        		c.close();
                        	}
                            if((mSelectedItemIds.size() == count)){
                            	title = getString(R.string.delete_all);
                            	}
                        }
                        //PR892567.Delete note by color folder.Added by hz_nanbing.zou at 05/01/2015 end
                        
                        //PR856793 The dialog is different from other.Modified by hz_nanbing.zou at 28/11/2014 begin
                        AlertDialog.Builder builder = new AlertDialog.Builder(NotesListActivity.this/*,R.style.myDialog*/); 
                        //PR856793 The dialog is different from other.Modified by hz_nanbing.zou at 28/11/2014 end
                           
                        
                        builder.setMessage(title)
                                .setPositiveButton(getString(R.string.delete_menu_title),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method
                                                // stub
                                                //modify by mingyue.wang for pr949159 begin
                                            	//PR906453.Mode should be finished after click "ok".Modified by hz_nanbing.zou at 19/01/2015 begin
                                            	/*mode.finish();
                                            	//PR906453.Mode should be finished after click "ok".Modified by hz_nanbing.zou at 19/01/2015 end

                                                if (mSelectedItemIds != null) {
                                                    for (int i = 0; i < mSelectedItemIds.size(); i++) {
                                                        if ((sortByColor == DISPLAY_GROUP)) {
                                                            int groupId = mSelectedItemIds.get(i);
                                                            Cursor c = getCursor(
                                                                    QUERYID_GRID_GROUP_CHID,
                                                                    mOrder, groupId);
                                                            if (c != null) {
                                                                c.moveToPosition(-1);
                                                                while (c.moveToNext()) {
                                                                    int noteId = c.getInt(c
                                                                            .getColumnIndex(Note.COLUMN_ID));
                                                                    NoteUtils.deleteNote(noteId,
                                                                            NotesListActivity.this);
                                                                    
                                                                    //PR851184 After delete notes,widget not update.Added by hz_nanbing.zou at 24/11/2014 begin
                                                                    //PR845362 After delete notes,widget not update.Added by hz_nanbing.zou at 19/11/2014 begin
                                                                    SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",
                                                                            Context.MODE_PRIVATE);
                                                                    long id = widgetNoteMap.getLong("widget",0);
                                                                    int id2 = (int)id;

                                                                    if(id2 == noteId){
                                                                        Cursor ccc = getCursor(QUERYID_GRID, mOrder, 0);
                                                                        if( ccc.getCount() > 0){
                                                                            ccc.moveToFirst();
                                                                            updatewidget(ccc.getInt(ccc.getColumnIndex(Note.COLUMN_ID)));
                                                                        }else{
                                                                              updatewidget(-1);
                                                                         }
                                                                    	
                                                                    }else{
                                                                    	//not change
                                                                    }
                                                                    
                                                                }
                                                                c.close();
                                                            }
                                                        } else {
                                                            int noteId = mSelectedItemIds.get(i);
                                                            NoteUtils.deleteNote(noteId,
                                                                    NotesListActivity.this);
                                                            SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",
                                                                    Context.MODE_PRIVATE);
                                                            long id = widgetNoteMap.getLong("widget",0);
                                                            int id2 = (int)id;
                                                           if(id2 == noteId){
                                                                Cursor ccc = getCursor(QUERYID_GRID, mOrder, 0);
                                                                if( ccc.getCount() > 0){
                                                                    ccc.moveToFirst();
                                                                    updatewidget(ccc.getInt(ccc.getColumnIndex(Note.COLUMN_ID)));
                                                                }else{
                                                                    updatewidget(-1);
                                                            	}

                                                            }else{
                                                            	//not change
                                                            }
                                                          //PR845362 After delete notes,widget not update.Added by hz_nanbing.zou at 19/11/2014 end 
                                                          //PR851184 After delete notes,widget not update.Added by hz_nanbing.zou at 24/11/2014 end
                                                        }
                                                    }
                                                }

                                                // PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
//                                                cancelNotify();
                                                // PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

                                                
                                                //PR855412-jin.chen-begin-001
                                                if (sortByColor == DISPLAY_GROUP_CHILD
                                                        && getCount() > 1) {
                                                    showGridView(display_groupId);
                                                }else{
                                                    showGridView(0);
                                                }
                                                count.setText("" + getCount());
                                                //PR855412-jin.chen-end-001*/
                                                mMode = mode;
                                                new RemoveDataThrread().start();
                                               //modify by mingyue.wang for pr949159 end
                                            }
                                        }).setNegativeButton(getString(R.string.btn_cancel), null)
                                .show();
                    }

                    //PR906453.Mode should be finished after click "ok".Modified by hz_nanbing.zou at 19/01/2015 begin
                    //mode.finish();
                    //PR906453.Mode should be finished after click "ok".Modified by hz_nanbing.zou at 19/01/2015 end

                    break;

                default:
                    break;
            }
            return true;
        }
        //modify by mingyue.wang for pr949159 begin
        public class RemoveDataThrread extends Thread {
            @Override
            public void run() {
              super.run();
              mHandler.sendEmptyMessage(0);
              if (mSelectedItemIds != null) {
                  for (int i = 0; i < mSelectedItemIds.size(); i++) {
                       if ((sortByColor == DISPLAY_GROUP)) {
                            int groupId = mSelectedItemIds.get(i);
                            Cursor c = getCursor(
                                    QUERYID_GRID_GROUP_CHID,
                                    mOrder, groupId);
                            if (c != null) {
                                c.moveToPosition(-1);
                                while (c.moveToNext()) {
                                    int noteId = c.getInt(c
                                            .getColumnIndex(Note.COLUMN_ID));
                                    NoteUtils.deleteNote(noteId,
                                            NotesListActivity.this);
                                }
                                c.close();
                            }
                        } else {
                            int noteId = mSelectedItemIds.get(i);
                            NoteUtils.deleteNote(noteId,
                                    NotesListActivity.this);
                        }
                    }
            }
            mHandler.sendEmptyMessage(1);
          }
        }
        
        private Handler mHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                case 0:
                    showProgressDialog(true);
                    break;
                case 1:
//                    showProgressDialog(false);
                    if(mMode != null){
                       mMode.finish();
                     }
                    SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",Context.MODE_PRIVATE);
                    long id = widgetNoteMap.getLong("widget",0);
                    int id2 = (int)id;
                    /* if(id2 == noteId)*/{
                         Cursor ccc = getCursor(QUERYID_GRID, mOrder, 0);
                         if( ccc.getCount() > 0){
                             ccc.moveToFirst();
                             updatewidget(ccc.getInt(ccc.getColumnIndex(Note.COLUMN_ID)));
                         }else{
                             updatewidget(-1);
                         }
                         ccc.close();
                    }/*else*/{
                      //not change
                    }
                    //PR855412-jin.chen-begin-001
                   if (sortByColor == DISPLAY_GROUP_CHILD
                         && getCount() > 1) {
                        showGridView(display_groupId);
                    }else{
                    	if(sortByColor == DISPLAY_GROUP_CHILD)//For PR962987
                    		sortByColor = DISPLAY_GROUP;
                        showGridView(0);
                    }
                    count.setText("" + getCount());
                    //PR855412-jin.chen-end-001
                    
                    showProgressDialog(false);
                    break;
                }
            }
        };
        
        private void showProgressDialog(boolean flag){
        	SharedPreferences widgetDel = getSharedPreferences("delete_widget",Context.MODE_PRIVATE);
        	if(flag){
               if(mProgressDialog == null) {
                  mProgressDialog = new ProgressDialog(NotesListActivity.this);
               }
               widgetDel.edit().putBoolean("isdelete", true).commit();
               mProgressDialog.setCancelable(false);
               mProgressDialog.setCanceledOnTouchOutside(false);
               //For pr958481
               mProgressDialog.setMessage(NotesListActivity.this.getResources().getString(R.string.deleting));
               mProgressDialog.show();
            }else{
               if(mProgressDialog !=null && mProgressDialog.isShowing()){
                  mProgressDialog.dismiss();
                  mProgressDialog = null;
                  widgetDel.edit().putBoolean("isdelete", false).commit();
                }
            }
       }
        //modify by mingyue.wang for pr949159 end

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            deletecount = 0;
            //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-689446,
            //Select notes can't marked after press home key
            delNoteCount = 0;
            //[BUGFIX]-Mod-END by TSCD.gerong
            myAdapter.ClearListId();
            myAdapter.notifyDataSetChanged();
            
            //PR906822.Select mode should not add new.Added by hz_nanbing.zou at 19/01/2015 begin
            mFloatingBtn_add.setVisibility(View.VISIBLE);
            //PR906822.Select mode should not add new.Added by hz_nanbing.zou at 19/01/2015 end  
            isClick=false;//modify by mingyue.wang for pr942664
        }

        private boolean isCursorValid() {
            return myCursor != null && !myCursor.isClosed();
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            int checkedCount = gridview.getCheckedItemCount();
            int groudCount = 0;
            
            //PR855412-jin.chen-begin-002
            if (position == 0 && sortByColor == DISPLAY_GROUP_CHILD) {
                mode.finish();
                return;
            }
            //PR855412-jin.chen-end-002

            
            if (isCursorValid()) {
                myCursor.moveToPosition(position);
                if ((sortByColor == DISPLAY_GROUP)) {
                    groudCount = myCursor.getInt(myCursor.getColumnIndex(Note.Group.COLUMN_NAME));
                }
            }
            if (checked) {
                deletecount += groudCount;
                mSelectedItemIds.add((int) id);
                myAdapter.setCheckedId((int) id);
                myAdapter.notifyDataSetChanged();
            } else {
                deletecount -= groudCount;
                mSelectedItemIds.remove(Integer.valueOf((int) id));
                myAdapter.unsetCheckedId((int) id);
                myAdapter.notifyDataSetChanged();
            }
            //[BUGFIX]-MOD-BEGIN by wenlu.wu,12/17/2013,PR-573171
            String selected;
            if ((sortByColor == DISPLAY_GROUP)) {
                selected = NotesListActivity.this.getResources().getString(R.string.selected,Integer.toString(deletecount));
                mSelectedCount.setText(selected);
                //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-689446,
                //Select notes can't marked after press home key
                delNoteCount = deletecount;
              //[BUGFIX]-Mod-END by TSCD.gerong
            } else {
                selected = NotesListActivity.this.getResources().getString(R.string.selected,Integer.toString(checkedCount));
                mSelectedCount.setText(selected);
                //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-689446,
                //Select notes can't marked after press home key
                delNoteCount = checkedCount;
                //[BUGFIX]-Mod-END by TSCD.gerong
            }
            //[BUGFIX]-MOD-END by wenlu.wu,12/17/2013,PR-573171
            mode.invalidate();
        }
    }

	// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
/*	private int notifyId = R.string.note_reminder;
	*//**cancel a notify when delete a alarm
	 * return void
	 *//*
	private void cancelNotify() {
		NotificationManager manager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(notifyId);
	}*/
	// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

	//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
	/** loop source code ,get the View which to change
	 * @param v the v which to find a child
	 */
	private int indicator = 0;
	private void setIcon(View v){
		if(v instanceof SearchView){
			SearchView sv = (SearchView) v;
			for(int i = 0;i < sv.getChildCount();++i ){
				setIcon(sv.getChildAt(i));
			}
		}else if (v instanceof LinearLayout){//find the child at linearlayout
			LinearLayout ll = (LinearLayout) v;
			for(int i = 0;i < ll.getChildCount();++i ){
				setIcon(ll.getChildAt(i));
			}
		}else if(v instanceof ImageView){//when find the image,change icon.
			if(indicator==1){
				((ImageView) v).setImageResource(R.drawable.ic_search_white);
			}if(indicator==2){
				//((ImageView) v).setImageResource(R.drawable.ic_search);
				((ImageView) v).setColorFilter(Color.WHITE);
			}
			indicator++;
			if(indicator > 4){
				indicator = 0;
			}
		}else if(v instanceof TextView){//when find the image,change icon.
			((TextView) v).setHintTextColor(Color.WHITE);
			((TextView) v).setTextColor(Color.WHITE);
		}
		}
	//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

	//PR 825446 auto show search text.Added by hz_nanbing.zou at 5/11/2014 begin
	PopupWindow ppw;
	View contentView;
	ListView lv;
	AutoCursorAdapter adapter;
	DisplayMetrics dm;
	private void setAutoMatch(String newText){
		
		if(isMaxquery(newText)){//For PR1021314
			if(ppw != null)
				ppw.dismiss();
			return;
			}
		
		if(dm == null){
			dm = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		}

		//PR851993 Search auto mach view ugly.Modified by hz_nanbing.zou at 25/11/2014 begin
		//PR877950 Alto_5 Search auto mach view ugly.Modified by hz_nanbing.zou at 23/12/2014 begin
		TctLog.d("nb","density.."+dm.density);
		if(ppw==null){
			if(dm.density == 3)
				ppw =new PopupWindow((int) (this.getWindowManager().getDefaultDisplay().getWidth()-30*(dm.density)),LayoutParams.WRAP_CONTENT);
			else if(dm.density == 1)
				ppw =new PopupWindow((int) (this.getWindowManager().getDefaultDisplay().getWidth()-25*(dm.density)),LayoutParams.WRAP_CONTENT);
			else if(dm.density == 1.5)
				ppw =new PopupWindow((int) (this.getWindowManager().getDefaultDisplay().getWidth()-20*(dm.density)),LayoutParams.WRAP_CONTENT);
			else if(dm.density >1.3 && dm.density < 1.5)
				ppw =new PopupWindow((int) (this.getWindowManager().getDefaultDisplay().getWidth()-40*(dm.density)),LayoutParams.WRAP_CONTENT);
			else
				ppw =new PopupWindow((int) (this.getWindowManager().getDefaultDisplay().getWidth()-30*(dm.density)),LayoutParams.WRAP_CONTENT);
		}
		//PR851993 Search auto mach view ugly.Modified by hz_nanbing.zou at 25/11/2014 end


		if(newText.length()==0){
			ppw.dismiss();
		}else{

			if(contentView==null)
				contentView = LayoutInflater.from(this).inflate(R.layout.auto_list, null);
			ppw.setContentView(contentView);
			ppw.setSplitTouchEnabled(true);
			ppw.setIgnoreCheekPress();
			ppw.setOutsideTouchable(true);
			ppw.setTouchable(true);
			ppw.setClippingEnabled(true);

			if(lv==null){
				lv = (ListView) contentView.findViewById(R.id.autolsit);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						String query = ((Cursor)(lv.getItemAtPosition(position))).getString(((Cursor)(lv.getItemAtPosition(position)))
			                    .getColumnIndex(Note.Text.COLUMN_TEXT));
			            Intent intent = new Intent();
			            intent.setClass(NotesListActivity.this, SearchResultActivity.class);
			            intent.putExtra(SearchManager.QUERY, query);
			            startActivity(intent);
						ppw.dismiss();
					}
				});
			}

	        if(newText.contains("'")){
	        	newText = newText.replace("'", "''");
	        }
	         //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-7,PR893629
			 String selection2 = Note.Text.COLUMN_TEXT + " like '%" + newText + "%' ";
             //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-7,PR893629
	         Cursor c = getContentResolver().query(Note.Text.CONTENT_URI, null, selection2, null,
	                 null);

	         //PR908520.High light the query text.Added by hz_nanbing.zou at 20/01/2015 begin
	         adapter = new AutoCursorAdapter(this, c,newText);
	         //PR908520.High light the query text.Added by hz_nanbing.zou at 20/01/2015 end
	         
	         //PR846507 .If has too much content,it will overlap the screen.Added by hz_nanbing.zou at 21/11/2014 begin
	         

	         
	         if(adapter.getCount() <= 4){
	        	 
	        	 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	        	 
	        	 /*if(dm.density == 1.5){
	        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, adapter.getCount()*42*(int)(dm.density))); 
	        	 }else if(dm.density >1.3 && dm.density < 1.5)
	        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));//for alto5 
	        	 else
	        	   lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, adapter.getCount()*28*(int)(dm.density))); */
	         }else{
	        	 //lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*28*(int)(dm.density)));

	        	 if(dm.density == 1.5){
	        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*42*(int)(dm.density))); 
	        	 }else if(dm.density >1.3 && dm.density < 1.5)
	        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*43*(int)(dm.density)));//for alto5 
	        	 else
	        	   lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*28*(int)(dm.density))); 
	         
	        	 
	         }
	         
	         //PR846507 .If has too much content,it will overlap the screen.Added by hz_nanbing.zou at 21/11/2014 end
	         
	         if(isLandScape()){
		         if(adapter.getCount() <= 2){
		        	 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		         }else{
		        	 //lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*28*(int)(dm.density)));
		        	 if(dm.density == 1.5){
		        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 2*42*(int)(dm.density))); 
		        	 }else if(dm.density >1.3 && dm.density < 1.5)
		        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 2*43*(int)(dm.density)));//for alto5 
		        	 else
		        	   lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 2*28*(int)(dm.density))); 
		         }
	         }
	         
	         lv.setAdapter(adapter);
	         if(dm.density == 1.5){
	        	 ppw.showAsDropDown(mSearchView, -60, 0);
	         }
	          else if(dm.density < 1.5 && dm.density > 1.3)//for alto 5
	          {
	        	  ppw.showAsDropDown(mSearchView, -50, 10);
	        	  
	          }
	          else if(dm.density == 2){
	        	  ppw.showAsDropDown(mSearchView, -80, 10);
	          }
	          else if(dm.density == 3){
	        	  ppw.showAsDropDown(mSearchView, -120, 20);
	          }
	         else
	        	 ppw.showAsDropDown(mSearchView, -80, 0);
	         if(lv.getCount()<1){
	        	 ppw.dismiss();
	        	 adapter.getCursor().close();//PR877950 Alto_5 Search auto mach view ugly.Modified by hz_nanbing.zou at 23/12/2014 end
	         }else{
                 adpaterSelViewAndPpwWidth();//PR1041987 popWindow search content not null, so change width
             }
		}
	}
	private class AutoCursorAdapter extends CursorAdapter{

		Cursor c;
		String query;
		public AutoCursorAdapter(Context context, Cursor c,String query) {
			super(context, c);
			// TODO Auto-generated constructor stub
			this.c = c;
			this.query = query;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			if(view == null){
				view = LayoutInflater.from(context).inflate(R.layout.auto_list_adapter, null);
			}

			String content = cursor.getString(cursor
                    .getColumnIndex(Note.Text.COLUMN_TEXT));
			
			//PR908520.High light the query text.Added by hz_nanbing.zou at 20/01/2015 begin
			SpannableStringBuilder ssb = new SpannableStringBuilder(content);
			String tempContent = content.toLowerCase();
			String tempQuery = query.toLowerCase();
			int start = tempContent.indexOf(tempQuery, 0);
			if(start !=-1)
			ssb.setSpan(new ForegroundColorSpan(Color.RED), start, start+query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			//PR908520.High light the query text.Added by hz_nanbing.zou at 20/01/2015 end
			
			((TextView)view.findViewById(R.id.autotext)).setText(ssb);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

	        if (!this.getCursor().moveToPosition(position)) {
	            throw new IllegalStateException("couldn't move cursor to position " + position);
	        }

			if(convertView == null){
				convertView = LayoutInflater.from(NotesListActivity.this).inflate(R.layout.auto_list_adapter, null);
			}
			bindView(convertView,NotesListActivity.this,this.getCursor());
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return c.getCount();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}
	}
	//PR 825446 auto show search text.Added by hz_nanbing.zou at 5/11/2014 end

	private void updatewidget(int noteId){

        SharedPreferences widgetNoteMap = getSharedPreferences("widget_note_map",
                  Context.MODE_PRIVATE);
          widgetNoteMap.edit().putLong("widget", noteId).apply();
      // PR817193 widget not sync. Modified by hz_nanbing_zou at 30/10/2014 begin

          Intent widgetIntent = new Intent("com.tct.note.widget.UPDATE");
//          widgetIntent.setComponent(NoteAppWidgetProvider.COMPONENT);
          widgetIntent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
          widgetIntent.putExtra("from_editor", true);
          sendBroadcast(widgetIntent);
	}
	
	//Added for L sdk.Added by hz_nanbing.zou at 18/12/2014 begin
	WindowManager wm;
	View v;
	private void initAddBtn(boolean show){
		//Do not show "add" when search item expand.
		mFloatingBtn_add = (ImageButton)super.findViewById(R.id.newadd_btn2);
		mFloatingBtn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
              //modify by mingyue.wang for pr942664 begin
              if(!isClick){
                 Intent intent = new Intent();
                 intent.setClass(NotesListActivity.this, NoteEditorActivity.class);
                 intent.putExtra(Constants.EXTRA_NOTE_ID, 0);
                 startActivity(intent);
                 //PR868138 Serarch bar doesn't disappear after create new note.Added by hz_nanbing.zou at 09/12/2014 begin
                 mSearchResult_flag = true;
                 //mSearchItem.collapseActionView();//make this at onResume();
                 //PR868138 Serarch bar doesn't disappear after create new note.Added by hz_nanbing.zou at 09/12/2014 end
              }
             //modify by mingyue.wang for pr942664 end
			}
		});
}
	//Added for L sdk.Added by hz_nanbing.zou at 18/12/2014 end

	
	//Added for Android L, landscape mode need show 3 columns.Added by hz_nanbing.zou at 25/12/2014 begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			
			gridview.setNumColumns(3);
			if(lv!=null){
		         if(adapter.getCount() <= 2){
		        	 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		         }else{
		        	 //lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*28*(int)(dm.density)));
		        	 if(dm.density == 1.5){
		        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 2*42*(int)(dm.density))); 
		        	 }else if(dm.density >1.3 && dm.density < 1.5)
		        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 2*43*(int)(dm.density)));//for alto5 
		        	 else
		        	   lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 2*28*(int)(dm.density))); 
		         }          
	         lv.setAdapter(adapter);
				}
		}else{
			gridview.setNumColumns(2);
			if(lv!=null){
		         if(adapter.getCount() <= 4){
		        	 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		         }else{
		        	 //lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*28*(int)(dm.density)));
		        	 if(dm.density == 1.5){
		        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*42*(int)(dm.density))); 
		        	 }else if(dm.density >1.3 && dm.density < 1.5)
		        		 lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*43*(int)(dm.density)));//for alto5 
		        	 else
		        	   lv.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 4*28*(int)(dm.density))); 
		         }          
	         lv.setAdapter(adapter);
				}
		}

        adpaterSelViewAndPpwWidth();//PR1041987 every change oritent, need change width

		super.onConfigurationChanged(newConfig);
	}
	//Added for Android L, landscape mode need show 3 columns.Added by hz_nanbing.zou at 25/12/2014 end	
	
	private boolean isLandScape(){

		return (this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE ? true : false);
	}

    /**
     * PR1041987 START The search box display too short after rotate the DUT update by xlli 10/7/
     * change selectView And PopWindow‘ width
     */
	private void adpaterSelViewAndPpwWidth(){
        //chang searchview width
        if(mSearchView!=null) {
            LayoutParams searchViewLP = mSearchView.getLayoutParams();
            if(searchViewLP!=null) {
                searchViewLP.width = LayoutParams.FILL_PARENT;
                mSearchView.setLayoutParams(searchViewLP);
            }
        }
        if (ppw != null&&ppw.isShowing()) {
            //change popwindow width
            float ppwWidth = this.getWindowManager().getDefaultDisplay().getWidth();
            if (dm.density == 3) {
                ppwWidth = ppwWidth - 30 * (dm.density);
            } else if (dm.density == 1) {
                ppwWidth = ppwWidth - 25 * (dm.density);
            } else if (dm.density == 1.5) {
                ppwWidth = ppwWidth - 20 * (dm.density);
            } else if (dm.density > 1.3 && dm.density < 1.5) {
                ppwWidth = ppwWidth - 40 * (dm.density);
            } else {
                ppwWidth = ppwWidth - 30 * (dm.density);
            }
            ppw.update((int) ppwWidth, LayoutParams.WRAP_CONTENT);
        }
        //PR1041987 END
    }
	private static final int ADD_LIMIT_COUNT = 20*250;//no more than 5,000 char.
	private boolean isMaxquery(String query){//For PR1021314	
		if(query != null){
		if(query.length() > ADD_LIMIT_COUNT){
			return true;
		}else{
			return false;
		}
		}
		return false;
	}
}
