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

package com.tct.note.ui;

import com.tct.note.Constants;
import com.tct.note.data.Note;
import com.tct.note.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;

//PR825433 modify inconsistent with demand.Modified by hz_nanbing.zou at 30/10/2014 begin
import android.view.MenuItem;
import android.view.View.OnClickListener;
//PR825433 modify inconsistent with demand.Modified by hz_nanbing.zou at 30/10/2014 end

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultActivity extends Activity implements OnItemClickListener {
    GridView gridview;
    Cursor searchCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_layout);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);

        //PR825433 modify inconsistent with demand.Modified by hz_nanbing.zou at 30/10/2014 begin
        actionBar.setDisplayHomeAsUpEnabled(true);
        //PR825433 modify inconsistent with demand.Modified by hz_nanbing.zou at 30/10/2014 end

        actionBar.setTitle(R.string.search_title);
        
        actionBar.setIcon(R.drawable.ic_description_white);
        
        actionBar.setDisplayShowCustomEnabled(true);
        
        
        super.findViewById(R.id.newadd_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	              Intent intent = new Intent();
	              intent.setClass(SearchResultActivity.this, NoteEditorActivity.class);
	              intent.putExtra(Constants.EXTRA_NOTE_ID, 0);
	              startActivity(intent);
			}
		});
        
        
//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.note_action_bar_bg));
        /*TextView No_Result = (TextView) findViewById(R.id.no_result);
        Intent intent = getIntent();
        String query = intent.getStringExtra(SearchManager.QUERY);
        gridview = (GridView) findViewById(R.id.grid);
        gridview.setOnItemClickListener(this);

        //PR 827847 search special char,force close.Modified by hz_nanbing_zou at 3/11/2014 begin
        if(query.contains("'")){
            query = query.replace("'", "''");
        }
        //PR 827847 search special char,force close.Modified by hz_nanbing_zou at 3/11/2014 end

        String selection = Note.COLUMN_ID + "  in (select " + Note.Text.COLUMN_NOTE_ID
                + " from  " + Note.Text.TABLE_NAME + " where " + Note.Text.COLUMN_CONTENT
                + " like '%" + query + "%') ";
        searchCursor = getContentResolver().query(Note.CONTENT_URI, null, selection, null, null);
        if (searchCursor == null || searchCursor.getCount() <= 0)
            No_Result.setVisibility(View.VISIBLE);
//        NoteCursorAdapter myAdapter = new NoteCursorAdapter(this, R.layout.grid_item, searchCursor,
//                NotesListActivity.DISPLAY_NORMAL);
        
        NoteCursorAdapter myAdapter = new NoteCursorAdapter(this, R.layout.cardview_item, searchCursor,
                NotesListActivity.DISPLAY_NORMAL);
        
        gridview.setAdapter(myAdapter);*/
    }
    
//PR887041 The content won't refresh after edit notes.Added by hz_nanbing.zou at 29/12/2014 begin
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		TextView No_Result = (TextView) findViewById(R.id.no_result);
        Intent intent = getIntent();
        String query = intent.getStringExtra(SearchManager.QUERY);
        gridview = (GridView) findViewById(R.id.grid);
        gridview.setOnItemClickListener(this);

        //PR 827847 search special char,force close.Modified by hz_nanbing_zou at 3/11/2014 begin
        if(query.contains("'")){
            query = query.replace("'", "''");
        }
        //PR 827847 search special char,force close.Modified by hz_nanbing_zou at 3/11/2014 end
        //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2015-1-7,PR893629
        String selection = Note.COLUMN_ID + "  in (select " + Note.Text.COLUMN_NOTE_ID
                + " from  " + Note.Text.TABLE_NAME + " where " + Note.Text.COLUMN_TEXT
                + " like '%" + query + "%') ";
       //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2015-1-7,PR893629
        searchCursor = getContentResolver().query(Note.CONTENT_URI, null, selection, null, null);
        //modify by mingyue.wang for pr948310 begin
        if (searchCursor == null || searchCursor.getCount() <= 0) {
            No_Result.setVisibility(View.VISIBLE);
        }else{
            No_Result.setVisibility(View.GONE);
        }
        //modify by mingyue.wang for pr948310 end
//        NoteCursorAdapter myAdapter = new NoteCursorAdapter(this, R.layout.grid_item, searchCursor,
//                NotesListActivity.DISPLAY_NORMAL);
        
        NoteCursorAdapter myAdapter = new NoteCursorAdapter(this, R.layout.cardview_item, searchCursor,
                NotesListActivity.DISPLAY_NORMAL);
        
        if(isLandScape()){//For PR1017561
        	gridview.setNumColumns(3);
        }
        
        gridview.setAdapter(myAdapter);
		
	}
  //PR887041 The content won't refresh after edit notes.Added by hz_nanbing.zou at 29/12/2014 end
    
	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        searchCursor.moveToPosition(position);
        int noteId = searchCursor.getInt(searchCursor.getColumnIndex(Note.COLUMN_ID));
        Intent intent = new Intent("com.tct.note.action.VIEW");
        intent.putExtra(Constants.EXTRA_NOTE_ID, noteId);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchCursor != null) {
            searchCursor.close();
        }

    }

    //PR825433 modify inconsistent with demand.Modified by hz_nanbing.zou at 30/10/2014 begin
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		finish();
		return super.onOptionsItemSelected(item);
	}
	//PR825433 modify inconsistent with demand.Modified by hz_nanbing.zou at 30/10/2014 end

	//PR922913 Landscape mode,display not good.Added by hz_nanbing.zou at 03/02/2015 begin
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
			gridview.setNumColumns(3);
		}else{
			gridview.setNumColumns(2);
		}
		super.onConfigurationChanged(newConfig);
	}
	//PR922913 Landscape mode,display not good.Added by hz_nanbing.zou at 03/02/2015 end
	private boolean isLandScape(){//For PR1017561

		return (this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE ? true : false);
	}
}
