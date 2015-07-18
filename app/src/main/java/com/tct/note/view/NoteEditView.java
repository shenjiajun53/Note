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
/* 06/06/2014|    nenghai.huang     |        697850        |Note auto scroll  */
/*           |                      |                      |to align each te- */
/*           |                      |                      |xtline within ba- */
/*           |                      |                      |ckground          */
/* ----------|----------------------|----------------------|----------------- */
/* 12/09/2014|     feilong.gu       |        860014        | Cursor beyond the*/  
/*           |                      |                      | borders in land- */
/*           |                      |                      | scape/portrait   */
/*           |                      |                      | mode.            */
/* ----------|----------------------|----------------------|----------------- */
/* 12/16/2014|     feilong.gu       |        841564        | Characters below */
/*           |                      |                      |the picture won't */
/*           |                      |                      |automatic moving  */
/*           |                      |                      |upward aftre      */
/*           |                      |                      |delete picture    */
/* ----------|----------------------|----------------------|----------------- */
/* 12/29/2014|     feilong.gu       |        882677        |"AT_"is displayed */
/*           |                      |                      |in note after     */
/*           |                      |                      |cutting a picture */
/* ----------|----------------------|----------------------|----------------- */
/* 01/08/2015|     feilong.gu       |        896814        |Can save the blank*/
/*           |                      |                      |note after done   */
/*           |                      |                      |some operations   */
/* ----------|----------------------|----------------------|----------------- */
/* 01/09/2015|     feilong.gu       |        898358        |The picture will  */
/*           |                      |                      |change to codes   */
/*           |                      |                      |after shutdown    */
/*           |                      |                      |note              */
/* ----------|----------------------|----------------------|----------------- */
/* 02/03/2015|     feilong.gu       |        914610        |It will automatic */
/*           |                      |                      |back to notes list*/
/*           |                      |                      | screen when      */
/*           |                      |                      |creating note     */
/* ----------|----------------------|----------------------|----------------- */
/* 02/09/2015|     feilong.gu       |        922669        |Delete picture    */
/*           |                      |                      |failure           */
/* ----------|----------------------|----------------------|----------------- */
/* 02/28/2015|     feilong.gu       |        936271        |The picture still */
/*           |                      |                      |be selected after */
/*           |                      |                      |tap "cancel"      */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.view;


import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.tct.note.R;
import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.util.AttachmentUtils;
import com.tct.note.util.EditorUtils;
import com.tct.note.util.TctLog;

//PR821313 edit too long will fore close.Add by hz_nanbing.zou at 28/10/2014 end

public class NoteEditView extends EditText {
    public long mNoteId;
    private static final String TAG = "NoteEditView";

    // PR821313 edit too long will fore close.Add by hz_nanbing.zou at 28/10/2014 begin
    private static final int ADD_LIMIT_COUNT = 20*250;//no more than 5,000 char.
    // PR821313 alarm icon not on statues bar.Add by hz_nanbing.zou at 28/10/2014 end

    // PR818868 char not rank according to the lines.Add by hz_nanbing.zou at 29/10/2014 begin
    private Rect mRect;
    private Paint mPaint;
    // PR818868 char not rank according to the lines.Add by hz_nanbing.zou at 29/10/2014 end

    private boolean isView = false;
    
    private int begin = 0;
    private int end = 0;
    //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
    private boolean isSelectIndexbeforeImagespan;//whether touch to before imagespan
    //PR412869 END special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015

    //private Pattern mUserQueryPattern;
  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
    private OnPreDispatchTouchEventListener mPreDispatchListener;
    private boolean flag = true;
    private Context mTmpContext;
  //[BUGFIX]-Add-END by TCTNB.Yan.Teng,08/01/2013,589177

    //[BUGFIX]-Add-BEGIN by TCTNB.nenghai.huang,06/06/2014,697850,
    //Note auto scroll to align each textline within background
    private OnActionUpListener mActionUpListener = null;
    
    private ImageTextWatcher mImageTextWatcher;
    private boolean isShowing=false;//add by mingyue.wang for pr940141
    
    private int insertImage_count = 0;
    private boolean isCustomMode =false;
    private boolean isSelectAll = false;//For PR964848
    private boolean isCut = false;
    private ArrayList<String> tempCut = new ArrayList<String>();
    private String stemp;
    private boolean currentInputCompos;
    interface OnActionUpListener{
        public void onActionUp();
    }
    public void setOnActionUpListener(OnActionUpListener listener){
        mActionUpListener = listener;
    }
    //[BUGFIX]-Add-END by TCTNB.nenghai.huang
    //PR882677 Modified by Gu Feilong at 2014-12-29 start
    public NoteEditView(Context context) {
        this(context, null);
        mTmpContext = context;
    }

    private void init(){
        addTextChangedListener(mImageTextWatcher);
//        mPreDispatchListener = new OnPreDispatchTouchEventListener();  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177

        // PR818868 char not rank according to the lines.Add by hz_nanbing.zou at 29/10/2014 begin
        mRect = new Rect();
        mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setAlpha(50);
		mPaint.setColor(0x72727100);
        // PR818868 char not rank according to the lines.Add by hz_nanbing.zou at 29/10/2014 end

/*		//PR 825452 Long press image should show delete dialog.Add by hz_nanbing.zou at 06/11/2014 begin
		this.setCustomSelectionActionModeCallback(new Callback() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				//[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-19,PR873936
				final int selectedIndex=getSelectionStart();
				//[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-19,PR873936
				if(isImageBeginAt(getSelectionStart())){
					NoteEditView.this.clearFocus();
					final int start = getSelectionStart();
					final int end = getSelectionEnd();
					final Editable localEditable = NoteEditView.this.getText();
					new AlertDialog.Builder(mTmpContext)
					.setMessage(R.string.delete_item)
					.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-19,PR873936
							    SmartImageSpan selectedImageSpan=getSelectedImageSpan(selectedIndex);
							    if(selectedImageSpan!=null){
								int start=localEditable.getSpanStart(selectedImageSpan);
								int end =localEditable.getSpanEnd(selectedImageSpan);
								localEditable.delete(start,end);
	                            //PR841564 added by feilong.gu start
					            NoteEditView.this.append(" ");
	                            //PR841564 added by feilong.gu end
								 AttachmentUtils.deleteAttachment(NoteEditView.this.mTmpContext,
										 selectedImageSpan.nContent);
								//[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-19,PR873936
							}
						}
					})
					.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub		

							//PR849083 Text cannot be input after cancel deleting picture and switching device to landscape mode.Added by hz_nanbing.zou at 10/12/2014 begin
							NoteEditView.this.requestFocus();
							//PR849083 Text cannot be input after cancel deleting picture and switching device to landscape mode.Added by hz_nanbing.zou at 10/12/2014 end
	
						}
					})
					.show();
					
					
					return false;
				}
				
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				//PR 833906 copy&&paste not work.Modified by hz_nanbing.zou at 07/11/2014 begin
				return false;
				//PR 833906 copy&&paste not work.Modified by hz_nanbing.zou at 07/11/2014 end
			}
		});
		//PR 825452 Long press image should show delete dialog.Add by hz_nanbing.zou at 06/11/2014 end
*/    }

    public NoteEditView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        mTmpContext = paramContext;
        mImageTextWatcher = new ImageTextWatcher();
        init();
    }

    public NoteEditView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        mTmpContext = paramContext;
        init();
    }
  //PR882677 Modified by Gu Feilong at 2014-12-29 end
    public void onSelectionChanged(int paramInt1, int paramInt2) {
        if (paramInt1 == paramInt2) {
            int i = paramInt2;
            if (getImageSpanEndAt(i) != null) {
                do
                    i = getText().toString().indexOf('\n', i + 1);
                while (getImageSpanEndAt(i) != null);
                if (i < 0)
                    i = getText().length();
                setSelection(1 + getText().toString().lastIndexOf('\n', i - 1));
                return;
            }
        }
        
        if(isImageBeginAt(paramInt1) && isCustomMode && !isSelectAll){//For PR964848
//        	if(isImageEndAt(paramInt2))//for PR960153
        	setDelDialog();
        }
        if(isSelectAll == true)//For PR964848
        	isSelectAll = false;
        super.onSelectionChanged(paramInt1, paramInt2);
    }


    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        // 退出View的时候没有释放图片资源（native层)，内存泄漏严重！
        // 这里退出时找到所有人图片进行释放
        // added by xinyao.ye 2015.1.27 begin.
        this.removeTextChangedListener(mImageTextWatcher);
        Editable localEditable = getText();
        SmartImageSpan[] arrayOfSmartImageSpan = (SmartImageSpan[]) localEditable.getSpans(0,
        		localEditable.length(), SmartImageSpan.class);
        for (int i = 0; i < arrayOfSmartImageSpan.length; i++) {
        	SmartImageSpan imageSpan = arrayOfSmartImageSpan[i];
        	localEditable.removeSpan(imageSpan);
        	Drawable drawable = imageSpan.getDrawable();
        	if (drawable instanceof BitmapDrawable) {
            	((BitmapDrawable)drawable).getBitmap().recycle();
            }
        }
        // added by xinyao.ye 2015.1.27 end.
        
  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
        if (mPreDispatchListener != null)
            mPreDispatchListener.dispose();
  //[BUGFIX]-Add-END by TCTNB.Yan.Teng,08/01/2013,589177
    }

//    public boolean onPreDispatchTouchEvent(MotionEvent paramMotionEvent) {
//        return mPreDispatchListener.handleMotionEvent(paramMotionEvent);  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
//    }
  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
    private class OnPreDispatchTouchEventListener {
        private boolean nDetecting;
        //private boolean nOnlyDisptachDetector;
        private MotionEvent nStartEvent;
        private long nStartTime;

        private OnPreDispatchTouchEventListener() {
        }

        public void dispose() {
            if (this.nStartEvent != null) {
                this.nStartEvent.recycle();
                this.nStartEvent = null;
            }
        }

        public boolean handleMotionEvent(MotionEvent paramMotionEvent) {
            int i = paramMotionEvent.getActionMasked();
            TctLog.i("ty--", "handleMotionEvent================ ====" + i);
            
            /*//PR857340 The hyperlink of URL & phone number in Notes cannot be opened.Added by hz_nanbing.zou at 03/12/2014 begin
            Linkify.addLinks(NoteEditView.this, Linkify.ALL);
            //PR857340 The hyperlink of URL & phone number in Notes cannot be opened.Added by hz_nanbing.zou at 03/12/2014 end
*/            
            switch (i) {
                case MotionEvent.ACTION_DOWN:
                    if (nStartEvent != null)
                        nStartEvent.recycle();
                    nStartEvent = MotionEvent.obtain(paramMotionEvent);
                    nStartTime = System.currentTimeMillis();
                    nDetecting = true;
                    //nOnlyDisptachDetector = false;
                    break;
                case MotionEvent.ACTION_UP:
                    //[BUGFIX]-Add-BEGIN by TCTNB.nenghai.huang,06/06/2014,697850,
                    //Note auto scroll to align each textline within background
                    if(mActionUpListener != null){
                        mActionUpListener.onActionUp();
                    }
                    //[BUGFIX]-Add-END by TCTNB.nenghai.huang
                    if ((Math.abs(paramMotionEvent.getX() - this.nStartEvent.getX()) > 15.0F) && (Math.abs(paramMotionEvent.getY() - this.nStartEvent.getY()) < 15.0F))
                    {
                      //nOnlyDisptachDetector = true;
                      nDetecting = false;
                      break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if ((Math.abs(paramMotionEvent.getY() - this.nStartEvent.getY()) < 15.0F) && (System.currentTimeMillis() - this.nStartTime <= 100L))
                        break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
           if (!nDetecting) {
                return true;
            } else {
                return false;
            }
        }
    }
  //[BUGFIX]-Add-END by TCTNB.Yan.Teng,08/01/2013,589177
    public void setNoteId(long paramLong) {
        if (this.mNoteId != paramLong) {
            this.mNoteId = paramLong;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
        setCursorVisible(true);
        //PR1038393 START try catch to avoid flash back
        try{
            return super.dispatchTouchEvent(paramMotionEvent);
        }catch (Exception e){
            TctLog.e("NoteEditView",e.getMessage());
        }
        return false;
        //PR1038393 END

    }

    public void insertImage(Uri paramUri) {


        TctLog.d("nb","count:"+insertImage_count);
    	if(insertImage_count > 4){
    		Toast.makeText(mTmpContext, mTmpContext.getResources().getString(R.string.add_limit), Toast.LENGTH_SHORT).show();
    		return;
    	}
    	

        Editable localEditable = getText();
        

        
        int i = getSelectionStart();
      //PR945978 Add by Gu Feilong start
        int temp = isImageString(i);
        i= (i!=temp?temp:i);
      //PR945978 Add by Gu Feilong end
        //modify by mingyue.wang for pr898647 begin
        if(i < 0 ) {
           if(localEditable.length() > 0) {
             i = localEditable.length();
           } else {
             i = 0;
           }
        }
       //modify by mingyue.wang for pr898647 end
        
     // PR849611 Notes has some problems after input a long string of characters by jielong.xing at 2014-11-25 begin
        boolean isInsertBreak = (i > 0) && (localEditable.charAt(i - 1) != '\n');
        int len = localEditable.length();
        if (isInsertBreak) {
                 len ++;
        }
        long l = System.currentTimeMillis();
        
        //PR893899.Click image,switch screen,image change to char;Modified by hz_nanbing.zou at 13/01/2015 begin
        //String s = "AT_" + l;
        String s = "_AT" + l;//modify by mingyue.wang for pr930907
        //PR893899.Click image,switch screen,image change to char;Modified by hz_nanbing.zou at 13/01/2015 end
        
        
        if (len + s.length() + 1 > ADD_LIMIT_COUNT) {
                 Toast.makeText(mTmpContext, mTmpContext.getResources().getString(R.string.add_limit), Toast.LENGTH_SHORT).show();
                 return;
        }        
        if (isInsertBreak) {
        // PR849611 Notes has some problems after input a long string of characters by jielong.xing at 2014-11-25 end
            localEditable.insert(i, "\n");
            i++;
        }
        
        String str = AttachmentUtils.saveImage(this.mTmpContext, paramUri, this.mNoteId);
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(mTmpContext, R.string.toast_loadPicture, Toast.LENGTH_SHORT).show();//add by mingyue.wang for PR882378 on 24/12/2014
            TctLog.e(TAG, "str is null");
            return;
        }
        
        TctLog.e("nb", "insert str.."+str);
        
        localEditable.insert(i, str);
        setImageSpan(localEditable, i, i + str.length(), str);
        int j = i + str.length();
        if ((j < localEditable.length()) && (localEditable.charAt(j) != '\n')) {
            localEditable.insert(j, "\n");
        } else if (j == localEditable.length()) {
            localEditable.insert(j, "\n");
            begin = i;
            end = j;
        }
        TctLog.e(TAG, "insert");
        this.setHovered(true);
        setSelection(j + 1);
        setCursorVisible(true);
        //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li
        if(NoteUtils.isMTKPlatform()) {
            if(EditorUtils.isInImageSpan(NoteEditView.this.getText().toString(), NoteEditView.this.getSelectionStart())) {
                NoteEditView.this.setInputComposText(false);//hide compos
            }else{
                NoteEditView.this.setInputComposText(true);//show compos
            }
        }
        //PR412869 END special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li
    }
    //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
    public void changetheSelection() {
        final Editable localEditable = getText();
        int i = getSelectionStart();
        TctLog.i(TAG, "isImageBeginAt  before ====" + isImageBeginAt(i) + "i ===" + i);
        if (isImageBeginAt(i) && flag) {
            flag = false;
            localEditable.insert(i, "\n");
            i = getSelectionStart();
            TctLog.i(TAG, "isImageBeginAt====" + isImageBeginAt(i) + "i ===" + i);
            if (isImageBeginAt(i)) {
                if (i > 0){
                    setSelection(i-1);
                } else {
                    setSelection(0);
                }
            }
        }
        setCursorVisible(true);
    }
    //[BUGFIX]-Add-END by TCTNB.Yan.Teng,08/01/2013,589177

  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
    private boolean isImageBeginAt(int paramInt)
    {
      Editable localEditable = getText();
      if (paramInt >= localEditable.length())
        return false;
      int i = localEditable.toString().indexOf('\n', paramInt);
      if (i < 0)
        return false;
      SmartImageSpan[] arrayOfSmartImageSpan = (SmartImageSpan[])localEditable.getSpans(paramInt, i, SmartImageSpan.class);
      int j = arrayOfSmartImageSpan.length;
      for (int k = 0; k < j; k++){
        if (localEditable.getSpanStart(arrayOfSmartImageSpan[k]) == paramInt)
          return true;
        if(NoteUtils.isMTKPlatform()){//For defect 289756
        	 if (localEditable.getSpanEnd(arrayOfSmartImageSpan[k]) == paramInt)
                 return true;
        }
        
      }
      if(NoteUtils.isMTKPlatform())//For defect 233663
      if(getSelectedImageSpan(paramInt) != null)
    	  return true;
      return false;
    }
    private boolean isImageEndAt(int paramInt)//For PR960153
    {
      Editable localEditable = getText();
      if (paramInt >= localEditable.length())
        return false;
      int i = localEditable.toString().indexOf('\n', paramInt);
      if (i < 0)
        return false;
      SmartImageSpan[] arrayOfSmartImageSpan = (SmartImageSpan[])localEditable.getSpans(paramInt, i, SmartImageSpan.class);
      int j = arrayOfSmartImageSpan.length;
      for (int k = 0; k < j; k++)
        if (localEditable.getSpanEnd(arrayOfSmartImageSpan[k]) == paramInt)
          return true;
      return false;
    }
   //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-19,PR873936
    private SmartImageSpan getSelectedImageSpan(int paramInt){
        Editable localEditable = getText();
        if (paramInt >= localEditable.length())
          return null;
        //PR922669 ADD by Gu Feilong at 2015-02-09 start
        String tempS = localEditable.toString();
        int i = -1;
        int indexAT =tempS.indexOf("_AT", paramInt);//modify by mingyue.wang for pr930907
        int indexEND = tempS.indexOf("END\n", paramInt);
        int sLenght = indexEND-indexAT;
        int timeLenght = (System.currentTimeMillis()+"").length();
        if(sLenght<20&&sLenght>timeLenght){
            if(indexAT>0&&paramInt!=indexAT){
               paramInt=indexAT;
           }
            i = tempS.indexOf('\n', paramInt);
        }
        //PR922669 ADD by Gu Feilong at 2015-02-09 end
        
        if(NoteUtils.isMTKPlatform()){//For defect 289756
        	i = tempS.indexOf('\n', paramInt);
        }
        
        if (i < 0)
          return null;
        SmartImageSpan[] arrayOfSmartImageSpan = (SmartImageSpan[])localEditable.getSpans(paramInt, i, SmartImageSpan.class);
        int j = arrayOfSmartImageSpan.length;
        for (int k = 0; k < j; k++){
          if (localEditable.getSpanStart(arrayOfSmartImageSpan[k]) == paramInt)
            return arrayOfSmartImageSpan[k];
          if(NoteUtils.isMTKPlatform()){//For defect 289756
         	 if (localEditable.getSpanEnd(arrayOfSmartImageSpan[k]) == paramInt)
                  return arrayOfSmartImageSpan[k];
         }
        }
        return null;
    }
    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-19,PR873936
    
  //[BUGFIX]-Add-END by TCTNB.Yan.Teng,08/01/2013,589177
    /*private void revertStrikeSpan(int paramInt) {
        int i = 0;
        Editable localEditable = getText();
        String str = localEditable.toString();
        int j = 0;
        int k = 0;
        while ((j < str.length()) && (k < paramInt)) {
            int i2 = 1 + str.indexOf('\n', j);
            k++;
            j = i2;
        }
        int m = str.indexOf('\n', j + 1);
        if (m > 0) {
            int n = str.length();
            if (n == m) {
                StrikethroughSpan[] arrayOfStrikethroughSpan = (StrikethroughSpan[]) localEditable
                        .getSpans(j, n, StrikethroughSpan.class);
                int i1 = 0;
                if ((arrayOfStrikethroughSpan != null) && (arrayOfStrikethroughSpan.length > 0))
                    i1 = arrayOfStrikethroughSpan.length;
                if (i < i1) {
                    localEditable.removeSpan(arrayOfStrikethroughSpan[i]);
                    i++;
                } else {
                    setStrikeSpan(localEditable, j, n);
                }
            }
        }
    }*/

    private Bitmap adjustBitmapSize(String paramString) {
        TctLog.e(TAG, "paramString: " + paramString);
        DisplayMetrics localDisplayMetrics = this.mTmpContext.getResources().getDisplayMetrics();
        float ff =0;//For defect 289756
        if(NoteUtils.isMTKPlatform()){
        		ff = 0.91F;
        }else{
        		ff = 0.9F;
        }
        
        return resizeImageAttachment((int) (ff * localDisplayMetrics.widthPixels),
                (int) (0.9F * localDisplayMetrics.heightPixels), paramString,
                (NinePatchDrawable) this.mTmpContext.getResources().getDrawable(R.drawable.actionbar));
    }

    private void setStrikeSpan(Editable paramEditable, int paramInt1, int paramInt2) {
        paramEditable.setSpan(new StrikethroughSpan(), paramInt1, paramInt2, 17);
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
        
        
        float rate = 1.000f;
        
        DisplayMetrics localDisplayMetrics = this.mTmpContext.getResources().getDisplayMetrics();
        if(i > j){

        	TctLog.d("nb", "get a landscape pic");

        	if(localDisplayMetrics.widthPixels > localDisplayMetrics.heightPixels){
        		TctLog.d("nb", "It is landscape mode now");
//            	j = paramInt2;
//            	i = (int) (paramInt2*(0.9/0.7));
            	
            	
            	rate = getRate(i, paramInt2);
            	i = paramInt2;
            	j = (int)(j/rate);
            	

        	}else{
        		TctLog.d("nb", "It is portrait mode now");
//        		i = paramInt1;
//        		j = (int) (paramInt1*(0.7/0.9));
        		
        		
        		rate = getRate(i, paramInt1);
        		i = paramInt1;
        		j = (int)(j/rate);
        		
        	}

        }else{
        	TctLog.d("nb","get a portrait pic");
        	if(localDisplayMetrics.widthPixels > localDisplayMetrics.heightPixels){
        		TctLog.d("nb", "It is landscape mode now");
//        		i = (int) (paramInt2*(0.9/0.7));
//        		j = (int) (paramInt1*(0.7/0.9));
        		
        		rate = getRate(i, paramInt2);
        		i = paramInt2;
        		j = (int)(j/rate);
        		

        	}else{
        		TctLog.d("nb", "It is portrait mode now");
//              if (j > paramInt2) {
//              j = paramInt2;
//              TctLog.d("nb","j > p2");
//              }
//              if (i > paramInt1) {
//              i = paramInt1;
//              TctLog.d("nb","i > p1");
//              }
        		
        		rate = getRate(i, paramInt1);
        		i = paramInt1;
        		j = (int)(j/rate);
        		
        	}
        }
        
        TctLog.d("nb", "rate.."+rate);
        
        //When image is small than width,should not scale.
        if(localOptions.outWidth < paramInt1){
        	i = localOptions.outWidth;
        	j = localOptions.outHeight;
        }
//        //For PR988351/PR1009200
//        if(localOptions.outHeight < paramInt2 && localOptions.outHeight != localDisplayMetrics.widthPixels){
//        	TctLog.d("nb", "small");
//        	j = localOptions.outHeight;
//        }
        //When image is small than width,should not scale.
        
        
        int k = localOptions.outWidth / i;
        localOptions.inJustDecodeBounds = false;
        localOptions.inSampleSize = (int)rate;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(paramString, localOptions);
        TctLog.e("ty--", "localBitmap1: " + localBitmap1);
        if (localBitmap1 == null)
            return null;
        Rect localRect = new Rect();
        paramNinePatchDrawable.getPadding(localRect);

        // FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
        //Bitmap localBitmap2 = Bitmap.createBitmap(i + localRect.left + localRect.right, j
        //        + localRect.top + localRect.bottom, Bitmap.Config.ARGB_8888);
        //cancel rectangle
        Bitmap localBitmap2 = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap2);
        paramNinePatchDrawable.setBounds(0, 0, localBitmap2.getWidth(), localBitmap2.getHeight());
        paramNinePatchDrawable.draw(localCanvas);
        //localCanvas.drawBitmap(localBitmap1, null, new Rect(localRect.left, localRect.top, i
        //        + localRect.left, j + localRect.top), null);*/
        //cancel rectangle
        localCanvas.drawBitmap(localBitmap1, null, new Rect(0, 0, i, j ), null);
        // FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

        localBitmap1.recycle();
        localBitmap1 = null;
        return localBitmap2;
    }

    private SmartImageSpan getImageSpanEndAt(int paramInt) {
        Editable localEditable = getText();
        if ((paramInt > 0) && (paramInt < localEditable.length())
                && (localEditable.charAt(paramInt) == '\n')) {
            int i = paramInt - 1;
            if ((i > 0) && (localEditable.charAt(i) == '\n')) {
                if (i > 0)
                    i++;
            }
            SmartImageSpan[] arrayOfSmartImageSpan = (SmartImageSpan[]) localEditable.getSpans(i,
                    paramInt, SmartImageSpan.class);
            if ((arrayOfSmartImageSpan != null) && (arrayOfSmartImageSpan.length > 0)) {
                SmartImageSpan localSmartImageSpan = arrayOfSmartImageSpan[(-1 + arrayOfSmartImageSpan.length)];
                if (localEditable.getSpanEnd(localSmartImageSpan) == paramInt)
                    return localSmartImageSpan;
            }

        }
        return null;
    }

    private void setImageSpan(Editable paramEditable, int paramInt1, int paramInt2,
            String paramString) {
        Bitmap localBitmap = adjustBitmapSize(AttachmentUtils.getAttachmentPath(this.mTmpContext,
                paramString));
        if (localBitmap != null) {
            BitmapDrawable localBitmapDrawable = new BitmapDrawable(this.mTmpContext.getResources(),
                    localBitmap);
            localBitmapDrawable.setBounds(0, 0, localBitmap.getWidth(), localBitmap.getHeight());
            paramEditable.setSpan(new SmartImageSpan(localBitmapDrawable, paramString), paramInt1,
                    paramInt2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            insertImage_count++;
        }
    }

    public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3,
            int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);

    }

    private void clearText() {
        int i = 0;
        Editable localEditable = getText();
        Object[] arrayOfObject = localEditable.getSpans(0, localEditable.length(), Object.class);
        int j = arrayOfObject.length;
        while (i < j) {
            localEditable.removeSpan(arrayOfObject[i]);
            i++;
        }
        setText("");
    }

    public void setRichText(CharSequence paramCharSequence) {
        clearText();
        if (!TextUtils.isEmpty(paramCharSequence)) {
            new EditorUtils.RichTextParser(paramCharSequence.toString(), new RichParserHandler())
                    .parse();
        }
        setSelection(0);
        setCursorVisible(false);
    }

    private void appendAndMarkQueryResult(Editable paramEditable, String paramString) {
        if (!TextUtils.isEmpty(paramString)) {
            //int i = paramEditable.length();
            paramEditable.append(paramString);
        }
    }

    private <T> ArrayList<Integer> getSortedSpanStarts(Class<T> paramClass) {
        return getSortedSpanStarts(paramClass, 0, getText().length());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> ArrayList<Integer> getSortedSpanStarts(Class<T> paramClass, int paramInt1,
            int paramInt2) {
        Editable localEditable = getText();
        Object[] arrayOfObject = localEditable.getSpans(paramInt1, paramInt2, paramClass);
        ArrayList localArrayList = new ArrayList(arrayOfObject.length);
        int i = arrayOfObject.length;
        for (int j = 0; j < i; j++)
            localArrayList.add(Integer.valueOf(localEditable.getSpanStart(arrayOfObject[j])));
        Collections.sort(localArrayList);
        return localArrayList;
    }

    @SuppressWarnings("rawtypes")
	public CharSequence getRichText() {
        String[] arrayOfString = getText().toString().split("\n");
        ArrayList localArrayList1 = getSortedSpanStarts(SmartImageSpan.class);
        ArrayList localArrayList2 = getSortedSpanStarts(StrikethroughSpan.class);
        EditorUtils.RichTextBuilder localRichTextBuilder = new EditorUtils.RichTextBuilder();
        int i = arrayOfString.length;
        int k = 0;
        int m = 0;
        int n = 0;
        for (int j = 0; j < i; ) {
            String str = arrayOfString[j];
            int i2;
            int i1;
            if ((m < localArrayList1.size())
                    && (n == ((Integer) localArrayList1.get(m)).intValue())) {
                localRichTextBuilder.append(3, str);
                int i3 = m + 1;
                int i4 = k;
                i2 = i3;
                i1 = i4;
            } else if ((k < localArrayList2.size())
                    && (n == ((Integer) localArrayList2.get(k)).intValue())) {
                localRichTextBuilder.append(2, str);
                i1 = k + 1;
                i2 = m;
            } else {
                localRichTextBuilder.append(1, str);
                i1 = k;
                i2 = m;
            }

            n += 1 + str.length();
            j++;
            m = i2;
            k = i1;

        }
        return localRichTextBuilder.build();
    }

	// yunchong.zhou-PR857135-begin
	public int getNoteBookMaxLength() {
		return ADD_LIMIT_COUNT;
	}
	// yunchong.zhou-PR857135-end
    @SuppressWarnings("rawtypes")
	public CharSequence getplainText() {
        String[] arrayOfString = getText().toString().split("\n");
        ArrayList localArrayList1 = getSortedSpanStarts(SmartImageSpan.class);
        ArrayList localArrayList2 = getSortedSpanStarts(StrikethroughSpan.class);
        EditorUtils.RichTextBuilder localRichTextBuilder = new EditorUtils.RichTextBuilder();
        int i = arrayOfString.length;
        int k = 0;
        int m = 0;
        int n = 0;
        for (int j = 0; j < i; ) {
            String str = arrayOfString[j];
            int i2;
            int i1;
            if ((m < localArrayList1.size())
                    && (n == ((Integer) localArrayList1.get(m)).intValue())) {
                int i3 = m + 1;
                int i4 = k;
                i2 = i3;
                i1 = i4;
            } else if ((k < localArrayList2.size())
                    && (n == ((Integer) localArrayList2.get(k)).intValue())) {
                i1 = k + 1;
                i2 = m;
            } else {
                localRichTextBuilder.append(1, str);
                i1 = k;
                i2 = m;
            }

            n += 1 + str.length();
            j++;
            m = i2;
            k = i1;

        }
        return localRichTextBuilder.build();
    }
   
    private class SmartImageSpan extends ImageSpan {
        int nBottom;
        String nContent;
        int nDescent;
        int nHeight;
        int nLeft;
        int nWidth;

        public SmartImageSpan(Drawable paramString, String arg3) {
            super(paramString,DynamicDrawableSpan.ALIGN_BASELINE);  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
            nWidth = paramString.getIntrinsicWidth();
            nHeight = paramString.getIntrinsicHeight();
            nContent = arg3;
        }

        public void draw(Canvas paramCanvas, CharSequence paramCharSequence, int paramInt1,
                int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5,
                Paint paramPaint) {
            this.nLeft = ((int) paramFloat);
            this.nBottom = paramInt5;
            
//            TctLog.d("nb","1.."+paramInt1+",2.."+paramInt2+",3.."+paramInt3+",4.."+paramInt4+",5.."+paramInt5);
            
            
            super.draw(paramCanvas, paramCharSequence, paramInt1, paramInt2, paramFloat, paramInt3,
                    paramInt4, paramInt5, paramPaint);
        }

        public int getImageBottom() {
            return this.nBottom - this.nDescent;
        }

        public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1,
                int paramInt2, Paint.FontMetricsInt paramFontMetricsInt) {
            if (paramFontMetricsInt != null)
                this.nDescent = paramFontMetricsInt.descent;
            
            TctLog.d("nb","1.."+paramInt1+",2.."+paramInt2);
            
            return super.getSize(paramPaint, paramCharSequence, paramInt1, paramInt2,
                    paramFontMetricsInt);
        }

        public final boolean isTouchIn(int paramInt1, int paramInt2) {
            return (paramInt1 >= this.nLeft) && (paramInt1 <= this.nLeft + this.nWidth)
                    && (paramInt2 >= getImageBottom() - this.nHeight)
                    && (paramInt2 <= getImageBottom());
        }
    }

    private class ImageTextWatcher implements TextWatcher {
        private int nReplaceType;
        private boolean nSkip;
        private NoteEditView.SmartImageSpan nToBeDeleteImageSpan;
        private boolean nToBeMarkStrike;

        private ImageTextWatcher() {
        }

        private <T> void deleteSpans(int paramInt1, int paramInt2, Class<T> paramClass) {
            Editable localEditable = NoteEditView.this.getText();
            for (Object localObject : localEditable.getSpans(paramInt1, paramInt1 + paramInt2,
                    paramClass)) {
                localEditable.removeSpan(localObject);
                if ((localObject instanceof NoteEditView.SmartImageSpan)) {
                    TctLog.e(TAG, "((NoteEditView.SmartImageSpan)localObject).nContent======="
                            + ((NoteEditView.SmartImageSpan) localObject).nContent);
                    
                    if(isCut)
                    	tempCut.add(((NoteEditView.SmartImageSpan) localObject).nContent);
                    else
                    	AttachmentUtils.deleteAttachment(NoteEditView.this.mTmpContext,
                            ((NoteEditView.SmartImageSpan) localObject).nContent);
                    
                    insertImage_count--;
                    
                  Drawable drawable = ((NoteEditView.SmartImageSpan) localObject).getDrawable();
                   if(drawable instanceof BitmapDrawable){
                	   ((BitmapDrawable)drawable).getBitmap().recycle();   
                   }
                    
                }
            }
            isCut = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
            if(NoteUtils.isMTKPlatform()&&isSelectIndexbeforeImagespan) {
                return;
            }
            //PR412869 END
            // NoteEditView.this.dismissPopupWindowIfNeed();
            nToBeDeleteImageSpan = null;
            nToBeMarkStrike = false;
            int i = s.length();
            boolean bool = false;
            if (i == 0)
                bool = true;
            nSkip = bool;
            if (nSkip)
                return;
            String str = s.toString();
            int j = 1 + str.lastIndexOf('\n', start - 1);
            int k = str.indexOf('\n', j);
            int m = 0;
            if (k < 0) {
                m = str.length();
                if (start != j) {
                    nReplaceType = 2;
                } else if (start + count < m) {
                    nReplaceType = 1;
                } else {
                    nReplaceType = 3;
                }
            } else {
                m = k - 1;
                if (m >= j) {
                    nReplaceType = 1;
                } else {
                    m = j;
                    nReplaceType = 3;
                }
            }
            if ((((StrikethroughSpan[]) NoteEditView.this.getText().getSpans(j, m,
                    StrikethroughSpan.class)).length > 0)
                    && (this.nReplaceType != 3))
                this.nToBeMarkStrike = true;
            if (count > 0) {
                deleteSpans(start, count, NoteEditView.SmartImageSpan.class);
                deleteSpans(start, count, StrikethroughSpan.class);
                nToBeDeleteImageSpan = NoteEditView.this.getImageSpanEndAt(start);
            }
            changetheSelection();  //[BUGFIX]-Add-BEGIN by TCTNB.Yan.Teng,08/01/2013,589177
  
        }

        @Override
        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2,
                int paramInt3) {
             //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
            if(NoteUtils.isMTKPlatform()) {
                if (isSelectIndexbeforeImagespan) {
                    isSelectIndexbeforeImagespan = false;
                    return;
                } else if(!getCurrentInputCompos()){
                    setInputComposText(true);//reset input show compos
                }
            }
            //PR412869 END
            // TODO Auto-generated method stub
            if (nSkip)
                return;
            if (!nToBeMarkStrike) {
                if ((paramInt1 != 0) && (paramCharSequence.charAt(paramInt1 - 1) != '\n'));
                    deleteSpans(paramInt1, paramInt3, StrikethroughSpan.class);
            } else {
                if (nReplaceType == 1)
                    paramInt1 += paramInt3;
                String str = paramCharSequence.toString();
                int i = 1 + str.lastIndexOf('\n', paramInt1 - 1);
                int j = str.indexOf('\n', i);
                if (j < 0)
                    j = str.length();
                NoteEditView.this.setStrikeSpan(NoteEditView.this.getText(), i, j);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            // yunchong.zhou-PR857135-begin
            //PR821313 edit too long will fore close.Add by hz_nanbing.zou at 28/10/2014 begin
            //Editable localEditable1 = NoteEditView.this.getText();
            //if(localEditable1.length() > ADD_LIMIT_COUNT){
            //Toast.makeText(mTmpContext, mTmpContext.getResources().getString(R.string.add_limit),
            //    Toast.LENGTH_LONG).show();
            //NoteEditView.this.setTextKeepState(NoteEditView.this.getText().subSequence(0, ADD_LIMIT_COUNT));
            //    return;
            //}
            //PR821313 edit too long will fore close.Add by hz_nanbing.zou at 28/10/2014 end
            // yunchong.zhou-PR857135-end

            flag = true;
            if (this.nSkip) {
                return;
            }
            if (nToBeDeleteImageSpan == null)
                return;
            Editable localEditable = NoteEditView.this.getText();
            localEditable.delete(localEditable.getSpanStart(this.nToBeDeleteImageSpan),
                    localEditable.getSpanEnd(this.nToBeDeleteImageSpan));
            
            AttachmentUtils.updateAfterDele(mTmpContext.getApplicationContext(),NoteEditView.this);
        }
    }


    private class RichParserHandler implements EditorUtils.ParserHandler {
        private SpannableStringBuilder nBuilder = new SpannableStringBuilder();

        private RichParserHandler() {
        }

        public void handleComplete() {
            NoteEditView.this.setText(nBuilder);
        }

        public boolean handleContent(int paramInt, String paramString) {
            int i = nBuilder.length();
            switch (paramInt) {
                case 2:
                    NoteEditView.this.appendAndMarkQueryResult(nBuilder, paramString);
                    nBuilder.append('\n');
                    int k = nBuilder.length();
                    NoteEditView.this.setStrikeSpan(this.nBuilder, i, k - 1);
                    return false;
                case 3:
                    nBuilder.append(paramString + "\n");
                    int j = nBuilder.length();
                    NoteEditView.this.setImageSpan(this.nBuilder, i, j - 1, paramString);
                    return false;
                    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-31,PR889326
                case 4:
                    NoteEditView.this.appendAndMarkQueryResult(nBuilder, paramString);
                    return false;
                    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-31,PR889326
                default:
                    NoteEditView.this.appendAndMarkQueryResult(nBuilder, paramString);
                    this.nBuilder.append('\n');
                    break;
            }
            return true;
        }
    }

    // PR818868 char not rank according to the lines.Add by hz_nanbing.zou at 29/10/2014 begin
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//PR857340 The hyperlink of URL & phone number in Notes cannot be opened.Added by hz_nanbing.zou at 03/12/2014 begin
        //PR914610 Modified by Gu Feilong at 2015-02-03 start
		if(isView){
//			this.setAutoLinkMask(Linkify.ALL);
			this.setMovementMethod(LinkMovementMethod.getInstance());
		}
		else{
			this.setAutoLinkMask(0);
			this.setMovementMethod(getDefaultMovementMethod());
		}
        //PR914610 Modified by Gu Feilong at 2015-02-03 end
        //PR857340 The hyperlink of URL & phone number in Notes cannot be opened.Added by hz_nanbing.zou at 03/12/2014 end	
        
//		int count = getLineCount();
//		//Rect r = mRect;
//		//Paint paint = mPaint;
//		// PR 860014 Cursor beyond the borders in landscape/portrait mode. start 
//		int base = getLineBounds(0, mRect);
//		int len = getHeight() / getLineHeight();
//		len = (count > len ? count : len);
//		int tmp =  getLineHeight();//base;
//		// PR 860014 Cursor beyond the borders in landscape/portrait mode. end
//		float temw = mRect.right-mRect.left;
//		float temc = temw/100;
//
//		for (int i = 0; i < len; i++) {
//
//			for(int j = 1;j <= 51;j++){
//				if(j!=1){
//					canvas.drawLine(temc*2*j, tmp + 5, temc*(2*j-1), tmp + 5, mPaint);
//				}
//			}
//			//canvas.drawLine(r.left, tmp + 5, r.right, tmp + 5, paint);
//			tmp += getLineHeight();
//		}
		
		//PR861854.Line and picture on the same.Modified by hz_nanbing.zou at 04/12/2014 begin
		super.onDraw(canvas);
		//PR861854.Line and picture on the same.Modified by hz_nanbing.zou at 04/12/2014 end
	}
	// PR818868 char not rank according to the lines.Add by hz_nanbing.zou at 29/10/2014 end
	
//PR887025 At the new version ,can not popup the dialog.Modified by hz_nanbing.zou at 29/12/2014 begin
	public void setListener(){
		//PR 825452 Long press image should show delete dialog.Add by hz_nanbing.zou at 06/11/2014 begin
		this.setCustomSelectionActionModeCallback(new Callback() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				isCustomMode = false;
			}
			
			//PR903378.Dialog show several times.Modified by hz_nanbing.zou at 14/01/2015 begin
            //PR922669 ADD by Gu Feilong at 2015-02-09 start
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				
            	//CR925175 Added Vibrator for long press.Added by hz_nanbing.zou at 05/02/2015 begin
            	EditorUtils.setVibration(mTmpContext);
            	//CR925175 Added Vibrator for long press.Added by hz_nanbing.zou at 05/02/2015 end
				
				//[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-19,PR873936
				final int selectedIndex=getSelectionStart();
				//[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-19,PR873936
				if(isImageBeginAt(getSelectionStart())){
					setDelDialog();
					return false;
				}
				isCustomMode =true;
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				
				if(android.R.id.selectAll == item.getItemId()){//For PR964848
					isSelectAll = true;
				}
				if(android.R.id.cut == item.getItemId()){//For PR964848
					isCut = true;
				}
				
				//PR 833906 copy&&paste not work.Modified by hz_nanbing.zou at 07/11/2014 begin
				return false;
				//PR 833906 copy&&paste not work.Modified by hz_nanbing.zou at 07/11/2014 end
			}
		});
		//PR 825452 Long press image should show delete dialog.Add by hz_nanbing.zou at 06/11/2014 end
	}
//PR887025 At the new version ,can not popup the dialog.Modified by hz_nanbing.zou at 29/12/2014 end	
    //PR898358 added by feilong.gu start
    private void saveTextForDb() {
         if(this.mNoteId>0){
            ContentResolver resolver =mTmpContext.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(Note.Text.COLUMN_NOTE_ID, mNoteId);
                values.put(Note.Text.COLUMN_CONTENT, getRichText().toString());
             values.put(Note.Text.COLUMN_TEXT, getplainText().toString());
             resolver.update(Note.Text.CONTENT_URI, values, Note.Text.COLUMN_NOTE_ID + " = "
                     + this.mNoteId, null);
         }
    }
    //PR898358 added by feilong.gu end
    
    //PR899405.Phone number not links well.Added by hz_nanbing.zou at 21/01/2015 begin
    private boolean isSets = false;
    private void setLinkfy(){
    	if(!isSets){
    		isSets = true;
    		Linkify.addLinks(this, Pattern.compile(
                "(^[0-9]{3,15}$)" +
                "|(^(([0-9]{1,7})[\\-]([0-9]{3,8}))$)", 
                Pattern.MULTILINE), "tel:");
    	}
    }
    //PR899405.Phone number not links well.Added by hz_nanbing.zou at 21/01/2015 end
    
    private float getRate(int p1,int p2){
		String tempS = p1/p2+"."+(p1%p2)*10/p2+""+(p1%p2)*100/p2;
		TctLog.d("nb", "tempS.."+tempS);
		return Float.parseFloat(tempS);
    }
    public void setisView(boolean isview){
    	this.isView = isview ;
    }
    public boolean isView(){
        return isView;
    }
    //PR945978 Add by Gu Feilong start
	private int isImageString(int paramInt){
        Editable localEditable = getText();
        if (paramInt >= localEditable.length())
          return paramInt;
        int i = localEditable.toString().indexOf('\n', paramInt);
        if (i < 0)
          return paramInt;
        SmartImageSpan[] arrayOfSmartImageSpan = (SmartImageSpan[])localEditable.getSpans(paramInt, i, SmartImageSpan.class);
        int j = arrayOfSmartImageSpan.length;
        for (int k = 0; k < j; k++){
            int start=localEditable.getSpanStart(arrayOfSmartImageSpan[k]);
            int end = localEditable.getSpanEnd(arrayOfSmartImageSpan[k]);
          if (start< paramInt&&end>paramInt){
            return end;
            }
    }
        return paramInt;
    }
	 //PR945978 Add by Gu Feilong end
	
	private void setDelDialog(){

		
    	//CR925175 Added Vibrator for long press.Added by hz_nanbing.zou at 05/02/2015 begin
    	EditorUtils.setVibration(mTmpContext);
    	//CR925175 Added Vibrator for long press.Added by hz_nanbing.zou at 05/02/2015 end
		
		//[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-19,PR873936
		final int selectedIndex=getSelectionStart();
		//[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-19,PR873936
		if(isImageBeginAt(getSelectionStart())){
			
			String title = mTmpContext.getResources().getString(R.string.delete_item);
			String ok = mTmpContext.getResources().getString(R.string.btn_ok);
			if(NoteUtils.isEnglishLan()){
				title = mTmpContext.getResources().getString(R.string.remove_pic);
				ok = mTmpContext.getResources().getString(R.string.btn_remove);
			}
			
			if(!isCustomMode)
				NoteEditView.this.clearFocus();
			else{
//				NoteEditView.this.performClick();
//				NoteEditView.this.clearFocus();
			}
			final int start = getSelectionStart();
			final int end = getSelectionEnd();
			final Editable localEditable = NoteEditView.this.getText();
			AlertDialog d = null;
//			if(d == null)
            //PR922669 ADD by Gu Feilong at 2015-02-09 end
			d = new AlertDialog.Builder(mTmpContext)
			.setMessage(title)
			.setPositiveButton(ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-19,PR873936
					    SmartImageSpan selectedImageSpan=getSelectedImageSpan(selectedIndex);
					    if(selectedImageSpan!=null){
						int start=localEditable.getSpanStart(selectedImageSpan);
						int end =localEditable.getSpanEnd(selectedImageSpan);
						localEditable.delete(start,end);
						
		                 Drawable drawable = selectedImageSpan.getDrawable();
		                   if(drawable instanceof BitmapDrawable){
		                	   ((BitmapDrawable)drawable).getBitmap().recycle();   
		                   }
						
                        //PR898358 added by feilong.gu start
                        saveTextForDb();
                       //PR898358 added by feilong.gu end
                        //PR841564 added by feilong.gu start
                        //PR896814 added by feilong.gu start
                        String text = NoteEditView.this.getText().toString().trim();
                        if(null!=text&&!"".equals(text)){
                        NoteEditView.this.append(" ");
                        }
                        //PR896814 added by feilong.gu end
                        //PR841564 added by feilong.gu end
						 AttachmentUtils.deleteAttachment(NoteEditView.this.mTmpContext,
								 selectedImageSpan.nContent);
						//[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-19,PR873936
						 
						 //PR956433.Add more than 5 pictures in a note.Modified by hz_nanbing.zou at 2015/03/23 begin
//						 insertImage_count--;// Delete image will call deleteSpans() function,so ,not need this count;
						 //PR956433.Add more than 5 pictures in a note.Modified by hz_nanbing.zou at 2015/03/23 end
						 
					}
                        isShowing=false;//add by mingyue.wang for pr940141
				}
			})
			.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub		

					//PR849083 Text cannot be input after cancel deleting picture and switching device to landscape mode.Added by hz_nanbing.zou at 10/12/2014 begin
					NoteEditView.this.requestFocus();
					//PR849083 Text cannot be input after cancel deleting picture and switching device to landscape mode.Added by hz_nanbing.zou at 10/12/2014 end
                    //modify by mingyue.wang for pr910315 begin
                    int selectionEnd = getSelectionEnd();
                    //For PR999948/999899
                    if(selectionEnd < 0 || !isImageEndAt(selectionEnd) ) {
                       selectionEnd=getText().length();
                       setSelection(selectionEnd);
                    }
                    //PR936271 modify by Gu Feilong star
                    else{
                         setSelection(selectionEnd);
                     }
                    //PR936271 modify by Gu Feilong end
                   //modify by mingyue.wang for pr910315 end
                    isShowing=false;//add by mingyue.wang for pr940141
				}
			})
             //modify by mingyue.wang for pr940141 begin
            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                   isShowing=false;
                   NoteEditView.this.requestFocus();//PR956074 Add by Gu Feilong
                //PR945978 Add by Gu Feilong start
                   int selectionEnd = getSelectionEnd();
                   if(selectionEnd < 0 || !isImageEndAt(selectionEnd)) {
                      selectionEnd=getText().length();
                      setSelection(selectionEnd);
                   }else{
                         setSelection(selectionEnd);
                   }
//                   NoteEditView.this.requestFocus();//PR956074 Add by Gu Feilong
               //PR945978 Add by Gu Feilong end
                }
            })
			.create();
			/*if(!d.isShowing()){
				d.show();
			}*/
			if(!isShowing){
                d.show();
            }else{
                TctLog.d(TAG, "Dialog was already displayed");
            }
            isShowing=true;
            //modify by mingyue.wang for pr940141 end
			//PR903378.Dialog show several times.Modified by hz_nanbing.zou at 14/01/2015 begin
		}
	
	}
	public void updateCut(){
        if(tempCut.size() > 0){
        	TctLog.d("nb", "delete cut?");;
        	for(int i = 0;i < tempCut.size();i++){
        		AttachmentUtils.deleteAttachment(NoteEditView.this.mTmpContext,
        				tempCut.get(i));
        	}
        }

	}
    //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
    public void setSelectIndexbeforeImagespan(boolean isSelectIndexbeforeImagespan){
        this.isSelectIndexbeforeImagespan=isSelectIndexbeforeImagespan;
    }

    /**
     * set input whethor compos word
     * @param isCompos   true:show input compose
     *                   false:hide input compse
     */
    public void setInputComposText(boolean isCompos){
        if (isCompos) {
            //show compos
            NoteEditView.this.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    | InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            NoteEditView.this.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    | InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        } else {
            //hide compos
            NoteEditView.this.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    | InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);//change input mode
            NoteEditView.this.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    | InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        currentInputCompos=isCompos;
    }
    public boolean getCurrentInputCompos() {
        return currentInputCompos;
    }

    //PR412869 END
}
