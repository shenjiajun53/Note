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
/* 05/30/2014|     yanmei.zhang     |       PR692484       |the display of the*/
/*           |                      |                      |note edit is error*/
/* ----------|----------------------|----------------------|----------------- */
/* 06/06/2014|    nenghai.huang     |        697850        |Note auto scroll  */
/*           |                      |                      |to align each te- */
/*           |                      |                      |xtline within ba- */
/*           |                      |                      |ckground          */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.view;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.text.Spanned;

import com.tct.note.data.NoteUtils;
import com.tct.note.util.EditorUtils;
import com.tct.note.util.TctLog;
import com.tct.note.view.NoteEditView.OnActionUpListener;
import com.tct.note.R;
//[BUGFIX]-Add-END by TCTNB.nenghai.huang
public class NoteEditorView extends ScrollView {
    public NoteEditView mEdit;
    private Handler mHandler = new Handler();
    private MotionEvent mLastDownEvent;
    private Runnable mTouchExpireCheck;

    /*add by jiehao.li for text adaption begin*/
    private static int HEIGHT_EACH_LINE ;
    private static int mLineSpaceAdd;
    private static int mLineSpaceMul;
    private static int mEditTextSize;
    /*add by jiehao.li for text adaption end*/
    
    //[BUGFIX]-Add-BEGIN by TCTNB.nenghai.huang,06/06/2014,697850,
    //Note auto scroll to align each textline within background
    private int mLastOffsetY = 0;
    private int mRange = 0;

    private static final int MSG_TEST_SCROLL = 1;
    private static final int DELAY_TEST_TIME = 200;
//    private static final int HEIGHT_EACH_LINE = 31;
//    private Handler mHandlerScroll = new Handler(){
//        public void handleMessage(Message message){
//            if(message.what == MSG_TEST_SCROLL){
//                int curScrollY = getScrollY();
//                if(mLastOffsetY == curScrollY){
//                    int offset = curScrollY % HEIGHT_EACH_LINE;
//                    if(offset != 0){
//                        if(offset > HEIGHT_EACH_LINE / 2 && curScrollY <= (mRange - (HEIGHT_EACH_LINE - offset))){
//                            smoothScrollBy(0, HEIGHT_EACH_LINE - offset);
//                        }
//                        else{
//                            smoothScrollBy(0, -offset);
//                        }
//                    }
//                }
//                else{
//                    mHandlerScroll.sendMessageDelayed(mHandlerScroll.obtainMessage(MSG_TEST_SCROLL), DELAY_TEST_TIME);
//                    mLastOffsetY = curScrollY;
//                }
//            }
//        }
//    };
    //[BUGFIX]-Add-END by TCTNB.nenghai.huang
    public NoteEditorView(Context context) {
        this(context, null);
    }

    public NoteEditorView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, R.style.myScrollView);
    }

	// yunchong.zhou-PR857135-begin
	// / M: Use to constraint the max word number that user can input.
	public static void setupLengthFilter(EditText inputText,
			final Context context, final int maxLength, final boolean showToast) {
		// Create a new filter
		InputFilter.LengthFilter filter = new InputFilter.LengthFilter(
				maxLength) {
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (source != null
						&& source.length() > 0
						&& (((dest == null ? 0 : dest.length()) + dstart - dend) == maxLength)) {
					if (showToast) {
						Toast.makeText(context,
								context.getString(R.string.add_limit),
								Toast.LENGTH_SHORT).show();
					}
					return "";
				}
				return super.filter(source, start, end, dest, dstart, dend);
			}
		};

		// Find exist lenght filter.
		InputFilter[] filters = inputText.getFilters();
		int length = 0;
		for (int i = 0; i < filters.length; i++) {
			if (!(filters[i] instanceof InputFilter.LengthFilter)) {
				length++;
			}
		}

		// Only one length filter.
		InputFilter[] contentFilters = new InputFilter[length + 1];
		for (int i = 0; i < filters.length; i++) {
			if (!(filters[i] instanceof InputFilter.LengthFilter)) {
				contentFilters[i] = filters[i];
			}
		}
		contentFilters[length] = filter;
		inputText.setFilters(contentFilters);
	}
	// yunchong.zhou-PR857135-end

    @SuppressWarnings("deprecation")
	public NoteEditorView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
//        mEdit = new NoteEditView(paramContext);
        LinearLayout ll = (LinearLayout) LayoutInflater.from(paramContext).inflate(R.layout.edit, null);
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT);
        

        
        addView(ll, localLayoutParams);
        
        mEdit = (NoteEditView)findViewById(R.id.editview);

        mEdit.setAutoLinkMask(Linkify.ALL);
        //PR887025 At the new version ,can not popup the dialog.Modified by hz_nanbing.zou at 29/12/2014 begin        
        mEdit.setListener();
        //PR887025 At the new version ,can not popup the dialog.Modified by hz_nanbing.zou at 29/12/2014 end
        //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
        mEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NoteUtils.isMTKPlatform()&&!mEdit.isView()) {
                    if(EditorUtils.isInImageSpan(mEdit.getText().toString(), mEdit.getSelectionStart())) {
                        mEdit.setSelectIndexbeforeImagespan(true);//if just select don't deleteSpan
                        mEdit.setInputComposText(false);//hide compos
                    }else{
                        mEdit.setSelectIndexbeforeImagespan(false);
                        mEdit.setInputComposText(true);//show compos
                    }
                }

            }
        });
        //PR412869 END special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
        /*add by jiehao.li for text adaption begin*/
        HEIGHT_EACH_LINE = getResources().getInteger(R.integer.hight_each_line);
        mLineSpaceAdd = getResources().getInteger(R.integer.line_space_add);
        mLineSpaceMul = getResources().getInteger(R.integer.line_space_mul);
        mEditTextSize = getResources().getInteger(R.integer.edit_text_size);
        float lineSpaceAdd = (float)mLineSpaceAdd/10;
        float lineSpaceMul = (float)mLineSpaceMul/10;
        
        /*add by jiehao.li for text adaption end*/
        //mEdit.setAutoLinkMask(Linkify.ALL);
//        mEdit.setLinksClickable(true);
        mEdit.setGravity(Gravity.TOP);
        mEdit.setBackgroundDrawable(null);

        // PR 850871 give suggestion word. Modified by hz_nanbing_zou at 26/11/2014 begin
        // PR 824185 auto capitalization. Modified by hz_nanbing_zou at 30/10/2014 begin
        mEdit.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        // PR 824185 auto capitalization. Modified by hz_nanbing_zou at 30/10/2014 end
        // PR 850871 give suggestion word. Modified by hz_nanbing_zou at 26/11/2014 end

        mEdit.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        
        
//[BUGFIX]-ADD-BEGIN by TSCD(yanmei.zhang),05/30/2014 PR-692484

        // PR 824291 overload borders. Modified by hz_nanbing_zou at 30/10/2014 begin
        mEdit.setPadding(mEdit.getPaddingLeft(), 0, 0, mEdit.getPaddingBottom());
        // PR 824291 overload borders. Modified by hz_nanbing_zou at 30/10/2014 end

        mEdit.setLineSpacing(lineSpaceAdd, lineSpaceMul);
        mEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mEditTextSize);
        
        
        mEdit.setTextColor(paramContext.getResources().getColor(R.color.content_font_color));
        
        //[BUGFIX]-Add-BEGIN by TCTNB.nenghai.huang,06/06/2014,697850,
//        //Note auto scroll to align each textline within background
//        mEdit.setOnActionUpListener(new OnActionUpListener(){
//            public void onActionUp(){
//                mHandlerScroll.sendMessageDelayed(mHandlerScroll.obtainMessage(MSG_TEST_SCROLL), DELAY_TEST_TIME);
//                mLastOffsetY = getScrollY();
//                mRange = computeVerticalScrollRange() - getHeight();
//            }
//        });
		// yunchong.zhou-PR857135-begin
		setupLengthFilter(mEdit, paramContext, mEdit.getNoteBookMaxLength(), true);
		// yunchong.zhou-PR857135-end
        //[BUGFIX]-Add-END by TCTNB.nenghai.huang
 //[BUGFIX]-ADD-END by TSCD(yanmei.zhang)

    }
    public void setNoteId(long paramLong) {
        mEdit.setNoteId(paramLong);
    }

    public void setRichText(CharSequence paramCharSequence) {
        mEdit.setRichText(paramCharSequence);
    }

    public void setCursorVisible(boolean paramBoolean) {
       mEdit.setCursorVisible(paramBoolean);
    }

    private MotionEvent translateToChildMotionEvent(MotionEvent paramMotionEvent) {
        MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
        localMotionEvent.offsetLocation(0.0F, getScrollY());
        return localMotionEvent;
    }

    private void cancelExpireCheck() {
        if (mTouchExpireCheck != null) {
            mHandler.removeCallbacks(mTouchExpireCheck);
            mTouchExpireCheck = null;
        }
    }
  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
    public void onMeasure(int paramInt1, int paramInt2) {
        int i = View.MeasureSpec.getMode(paramInt2);
        if ((i == MeasureSpec.EXACTLY) || (i == MeasureSpec.AT_MOST))
            this.mEdit.setMinHeight(View.MeasureSpec.getSize(paramInt2));
        else {
            this.mEdit.setMinHeight(0);
        }
        super.onMeasure(paramInt1, paramInt2);
    }
  //[BUGFIX]-Add-END by TCTNB.Yan.Teng,08/01/2013,583116

    private void setupExpireCheck() {
        mTouchExpireCheck = new TouchExpireCheck();
        mHandler.postDelayed(mTouchExpireCheck, 100L);
    }

//    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
//        MotionEvent localMotionEvent = translateToChildMotionEvent(paramMotionEvent);
//        if (mEdit.onPreDispatchTouchEvent(localMotionEvent)) {
//            int i = paramMotionEvent.getActionMasked();
//            switch (i) {
//                case MotionEvent.ACTION_MOVE:
//                    cancelExpireCheck();
//                    localMotionEvent.recycle();
//                    return true;
//                case MotionEvent.ACTION_DOWN:
//                    setupExpireCheck();
//                    localMotionEvent.recycle();
//                    return true;
//                case MotionEvent.ACTION_UP:
//                    if (mLastDownEvent != null)
//                        mLastDownEvent.recycle();
//                    mLastDownEvent = MotionEvent.obtain(paramMotionEvent);
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                    if (mLastDownEvent != null)
//                        mLastDownEvent.recycle();
//                    mLastDownEvent = null;
//                    break;
//                default:
//                    break;
//            }
//        }
//        localMotionEvent.recycle();
//        cancelExpireCheck();
//        if (mLastDownEvent != null) {
//            super.dispatchTouchEvent(mLastDownEvent);
//            mLastDownEvent.recycle();
//            mLastDownEvent = null;
//        }
//        return super.dispatchTouchEvent(paramMotionEvent);
//    }


    public CharSequence getRichText() {
        return mEdit.getRichText();
    }
    public CharSequence getplainText() {
        return mEdit.getplainText();
    }
//    public void setTextColor(int color) {
//        mEdit.setTextColor(color);
//    }

    public void insertImage(Uri paramUri) {
        mEdit.insertImage(paramUri);
    }

    private class TouchExpireCheck implements Runnable {
        private TouchExpireCheck() {
        }

        public void run() {
            if (NoteEditorView.this.mLastDownEvent != null) {
                NoteEditorView.this.dispatchTouchEvent(NoteEditorView.this.mLastDownEvent);
                NoteEditorView.this.mLastDownEvent.recycle();
            }
        }
    }

    // PR 824190 keyboard pop up. Modified by hz_nanbing_zou at 30/10/2014 begin
    public void requestEditFocus(){
        mEdit.requestFocus();
        InputMethodManager imm =(InputMethodManager) mEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
    // PR 824190 keyboard pop up. Modified by hz_nanbing_zou at 30/10/2014 end
    
    //Added for Ergo5.1.0 by hz_nanbing.zou at 15/01/2015 begin
    public void setViewMode(boolean isViewMode){
//    	mEdit.setEnabled(isViewMode);
    	if(!isViewMode){
    		mEdit.setRawInputType(InputType.TYPE_NULL);
    		mEdit.setisView(true);
    	}else{
            mEdit.clearFocus();
            //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
            if(NoteUtils.isMTKPlatform()){
                mEdit.setInputComposText(false);//hide input compos
            }else {
                mEdit.setInputComposText(true);
            }
            //PR412869 END special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
            mEdit.setisView(false);
        }
    }
    //Added for Ergo5.1.0 by hz_nanbing.zou at 15/01/2015 end
    public void updateCut(){
    	mEdit.updateCut();
    }
    public NoteEditView getEditView(){
        return mEdit;
    }


}
