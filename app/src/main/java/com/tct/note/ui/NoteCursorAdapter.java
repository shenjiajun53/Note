/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
/* 07/30/2014|        gerong        |      PR-723075       |Long press note   */
/*           |                      |                      |display wrong     */
/*----------------------------------------------------------------------------*/
/* 12/10/2014|     feilong.gu       |        868064        |The format of     */
/*           |                      |                      |creating time is  */
/*           |                      |                      |not perfect after */
/*           |                      |                      |unchecked the     */
/*           |                      |                      |option"Use 24-hour*/
/*           |                      |                      |format" from      */
/*           |                      |                      |settings          */
/* ----------|----------------------|----------------------|----------------- */
/* 12/11/2014|     feilong.gu       |        866274        |There has some    */
/*           |                      |                      |overlap on note   */
/*           |                      |                      |when set data     */
/*           |                      |                      |format as Wed,Dec */
/*           |                      |                      |31,2014.          */
/* ----------|----------------------|----------------------|----------------- */
/******************************************************************************/

package com.tct.note.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;

//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
//PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;

//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tct.note.data.Note;
import com.tct.note.data.NoteUtils;
import com.tct.note.util.AttachmentUtils;
import com.tct.note.util.TctLog;
import com.tct.note.Constants;
import com.tct.note.R;


import android.text.format.DateFormat;
import android.util.DisplayMetrics;

/**
 * An easy adapter to map columns from a cursor to TextViews or ImageViews
 * defined in an XML file. You can specify which columns you want, which views
 * you want to display the columns, and the XML file that defines the appearance
 * of these views. Binding occurs in two phases. First, if a
 * {@link android.widget.SimpleCursorAdapter.ViewBinder} is available,
 * {@link ViewBinder#setViewValue(android.view.View, android.database.Cursor, int)}
 * is invoked. If the returned value is true, binding has occured. If the
 * returned value is false and the view to bind is a TextView,
 * {@link #setViewText(TextView, String)} is invoked. If the returned value is
 * false and the view to bind is an ImageView,
 * {@link #setViewImage(ImageView, String)} is invoked. If no appropriate
 * binding can be found, an {@link IllegalStateException} is thrown. If this
 * adapter is used with filtering, for instance in an
 * {@link android.widget.AutoCompleteTextView}, you can use the
 * {@link android.widget.SimpleCursorAdapter.CursorToStringConverter} and the
 * {@link android.widget.FilterQueryProvider} interfaces to get control over the
 * filtering process. You can refer to
 * {@link #convertToString(android.database.Cursor)} and
 * {@link #runQueryOnBackgroundThread(CharSequence)} for more information.
 */
public class NoteCursorAdapter extends CursorAdapter {

    private ArrayList<Integer> mId = new ArrayList<Integer>();
    private int DisplayWithGroup;
    private int mLayout;
    Context mContext;
    protected LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private final static int ONLY_HAS_IMAGE = 1;
    private final static int ONLY_HAS_AUDIO = 2;
    private final static int ONLY_HAS_REMINDER = 3;
    private final static int HAS_IMAGE_AUDIO = 4;
    private final static int HAS_IMAGE_REMINDER = 5;
    private final static int HAS_AUDIO_REMINDER = 6;
    private final static int HAS_ALL = 7;
    private final static int HAS_NOTHING = 0;

	// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
	private boolean isSetStatuesBar = false;
	// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

	private long Groupcount;
	
	private static Map<String, Bitmap> cacheBmp = Collections
            .synchronizedMap(new WeakHashMap<String, Bitmap>());
	
	
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
		//FR789597 modified the code for standalone APK development by bing.wang.hz 2014.09.15 begin
        if (!this.getCursor().moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        if (convertView != null && convertView.getTag() != null) {
            v = convertView;
        } else {
            v = newView(mContext, this.getCursor(), parent);
        }
        bindView(v, mContext, this.getCursor());
		//FR789597 modified the code for standalone APK development by bing.wang.hz 2014.09.15 end
        return v;
    }

    @SuppressWarnings("deprecation")
	public NoteCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, c);
        mContext = context;
        DisplayWithGroup = flags;
        mLayout = layout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = ImageLoader.getInstance(context.getApplicationContext());//Modified this for OOM
    }

    public static class ImageLoader {

        private Map<ImageView, String> imageViews = Collections
                .synchronizedMap(new WeakHashMap<ImageView, String>());
        private ExecutorService executorService;
        private static ImageLoader instance;
        private Context mContext;

        public ImageLoader(Context context) {
            this.mContext = context;
            int cpuNums = Runtime.getRuntime().availableProcessors();
            executorService = Executors.newFixedThreadPool(cpuNums + 1);
        }

        public static ImageLoader getInstance(Context context) {
            if (instance == null)
                instance = new ImageLoader(context);
            return instance;
        }

        public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
            imageViews.put(imageView, url);
            if (!isLoadOnlyFromCache) {
                queuePhoto(url, imageView);
            }
        }

        private void queuePhoto(String url, ImageView imageView) {
            PhotoToLoad p = new PhotoToLoad(url, imageView);
            executorService.submit(new PhotosLoader(p, mContext));
        }

        // Task for the queue
        private class PhotoToLoad {
            public String url;
            public ImageView imageView;

            public PhotoToLoad(String u, ImageView i) {
                url = u;
                imageView = i;
            }
        }

        class PhotosLoader implements Runnable {
            PhotoToLoad photoToLoad;
            Context mContext;

            PhotosLoader(PhotoToLoad photoToLoad, Context mContext) {
                this.photoToLoad = photoToLoad;
                this.mContext = mContext;
            }

            @Override
            public void run() {
                if (imageViewReused(photoToLoad))
                    return;
                
                //For Idol3 new Ergo.Added by hz_nanbing.zou at 15/01/2015 begin
                /*Bitmap bmp = clipImageAttachment(mContext,
                        AttachmentUtils.getAttachmentPath(mContext, photoToLoad.url),
                        (NinePatchDrawable) mContext.getResources().getDrawable(
                                R.drawable.actionbar));*/
                Bitmap bmp = getSubBitmap(AttachmentUtils.getAttachmentPath(mContext.getApplicationContext(), photoToLoad.url));
                //For Idol3 new Ergo.Added by hz_nanbing.zou at 15/01/2015 end
                
                if (imageViewReused(photoToLoad) || bmp == null)
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                Activity a = (Activity) photoToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            }

            boolean imageViewReused(PhotoToLoad photoToLoad) {
                String tag = imageViews.get(photoToLoad.imageView);
                if (tag == null || !tag.equals(photoToLoad.url))
                    return true;
                return false;
            }

            class BitmapDisplayer implements Runnable {
                Bitmap bitmap;
                PhotoToLoad photoToLoad;

                public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
                    bitmap = b;
                    photoToLoad = p;
                }

                public void run() {
                    if (imageViewReused(photoToLoad))
                        return;
                    if (bitmap != null){
                        photoToLoad.imageView.setImageBitmap(bitmap);
                        cacheBmp.put(photoToLoad.url,bitmap);
                    }
                }
            }
        }

        //For Idol3 new Ergo.Added by hz_nanbing.zou at 15/01/2015 begin
        private Bitmap getSubBitmap(String path){
            BitmapFactory.Options localOptions = new BitmapFactory.Options();
            localOptions.inJustDecodeBounds = true;//This property is very important
            DisplayMetrics localDisplayMetrics = mContext.getResources().getDisplayMetrics();
            
            int image_w = mContext.getResources().getInteger(R.integer.image_width);
            int image_h = mContext.getResources().getInteger(R.integer.image_height);
            
            int w = (int)(image_w * localDisplayMetrics.density);
            int h = (int) (image_h * localDisplayMetrics.density);
            BitmapFactory.decodeFile(path, localOptions);
            int i = localOptions.outWidth;
            int j = localOptions.outHeight;
            //PR907608.Can not show image from camera.Modified by hz_nanbing.zou at 19/01/2015 begin
            //PR928415.landscape camera pic can not fill the height.Modified by hz_nanbing.zou at 02/10/2015 begin
            if(i > j){
            	TctLog.d("nb", "It is landscape pic");
            	localOptions.inSampleSize = j/h;
            }else{
            	TctLog.d("nb", "It is portrait pic");
            	localOptions.inSampleSize = i/w;
            }
            //PR928415.landscape camera pic can not fill the height.Modified by hz_nanbing.zou at 02/10/2015 end
        	localOptions.inJustDecodeBounds = false;
        	Bitmap bmp1 =  BitmapFactory.decodeFile(path, localOptions);
            //PR907608.Can not show image from camera.Modified by hz_nanbing.zou at 19/01/2015 end
            int x = (bmp1.getWidth()-w)/2;
            int y = (bmp1.getHeight()-h)/2;
            
            //PR914588.Some pic can not show.Added by hz_nanbing.zou at 27/01/2015 begin
            if(x < 0){
            	x = 0;
            	w = bmp1.getWidth();
            }
            if(y < 0){
            	y = 0;
            	h = bmp1.getHeight();
            }
            //PR914588.Some pic can not show.Added by hz_nanbing.zou at 27/01/2015 end
            
            Bitmap bmp2 = Bitmap.createBitmap(bmp1, x, y, w, h);

        	return bmp2;
        }
        //For Idol3 new Ergo.Added by hz_nanbing.zou at 15/01/2015 end

        public static Bitmap clipImageAttachment(Context mContext,String paramString,
                NinePatchDrawable paramNinePatchDrawable) {
        DisplayMetrics localDisplayMetrics = mContext.getResources().getDisplayMetrics();
//        int paramInt1 = (int)(0.6F * localDisplayMetrics.widthPixels);
//        int paramInt2 = (int) (0.2F * localDisplayMetrics.heightPixels);
        
        int paramInt1 = (int)(160 * localDisplayMetrics.density);
        int paramInt2 = (int) (102 * localDisplayMetrics.density);
        
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        int i = localOptions.outWidth;
        int j = localOptions.outHeight;
        if ((i <= 0) || (j <= 0))
            return null;
        
        
        //PR888572.The compress rate not good for diff pic.Modified bz hz_nanbing.zou at 04/01/2015 begin 
        if(i > j){

        	i = paramInt1;
        	j = paramInt2;
        	
        }else{
        	
        	i = (int) (paramInt1*0.5);
        	j = paramInt2;
        }
       //PR888572.The compress rate not good for diff pic.Modified bz hz_nanbing.zou at 04/01/2015 end
        
/*        if (j > paramInt2) {
            //i = j * paramInt2 / i;
            j = paramInt2;
        }
        if (i > paramInt1) {
           // j = j * paramInt1 / i;
            i = paramInt1;
        }
        
//        if(localDisplayMetrics.widthPixels > localDisplayMetrics.heightPixels){
//        	i = paramInt1;
//        	j = paramInt2*3;
//        }
*/        
        int k = localOptions.outHeight / j; 
        localOptions.inJustDecodeBounds = false;
        localOptions.inSampleSize = k;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(paramString, localOptions);
        TctLog.e("NoteCusorAdapter", "clipImageAttachment localBitmap1: " + localBitmap1);
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
    }

    public void setCheckedId(int Id) {
        mId.add(Id);
    }

    public void unsetCheckedId(int id) {
        mId.remove(Integer.valueOf(id));
    }

    private boolean involvedId(int id) {
        boolean equality = false;
        for (int i = 0; i < mId.size(); i++) {
            if (id == mId.get(i)) {
                equality = true;
                break;
            }
        }
        return equality;
    }

    public void ClearListId() {
        mId.clear();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null || (cursor != null && cursor.getCount() <= 0))
            return;
        if (DisplayWithGroup == NotesListActivity.DISPLAY_NORMAL) {
            (view.findViewById(R.id.grid_shadow)).setVisibility(View.VISIBLE);
            displayItem(context, view, cursor);
        } else if (DisplayWithGroup == NotesListActivity.DISPLAY_GROUP_CHILD) {
            long id = cursor.getLong(cursor.getColumnIndex(Note.COLUMN_ID));
            if (id != 0) {
                ((TextView) view.findViewById(R.id.item_count)).setVisibility(View.GONE);
                (view.findViewById(R.id.grid_shadow)).setVisibility(View.VISIBLE);
                displayItem(context, view, cursor);
            } else {
                view.findViewById(R.id.grid_item).setBackgroundDrawable(
                        context.getResources().getDrawable(R.drawable.stack));
                ((TextView) view.findViewById(R.id.item_count)).setVisibility(View.VISIBLE);

                //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//                setFont((TextView) view.findViewById(R.id.item_count));
                //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

                ((TextView) view.findViewById(R.id.item_count)).setText(context.getResources()
                        .getString(R.string.stack));
            }
        } else if (DisplayWithGroup == NotesListActivity.DISPLAY_GROUP) {
        	Groupcount = cursor.getLong(cursor.getColumnIndex(Note.Group.COLUMN_NAME));
            long GroupId = cursor.getLong(cursor.getColumnIndex(Note.Group.COLUMN_ID));
            Cursor c = context.getContentResolver().query(Note.CONTENT_URI, null,
                    Note.COLUMN_GROUP_ID + " == ?", new String[] {
                        String.valueOf(GroupId)
                    }, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                // display the single note if the group is the only one
                if (Groupcount == 1) {
                    (view.findViewById(R.id.grid_shadow)).setVisibility(View.VISIBLE);
                    displayItem(context, view, c);
                } else {
                	/*Delete this for GD 5.1.0.*/
//                    (view.findViewById(R.id.grid_shadow)).setVisibility(View.GONE);
//                    ((TextView) view.findViewById(R.id.item_count)).setVisibility(View.VISIBLE);
                    
                    //Added this for GD 5.1.0.
                	(view.findViewById(R.id.grid_shadow)).setVisibility(View.VISIBLE);
                	c.moveToLast();
                	displayItem(context, view, c);
                	

                    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
//                    setFont((TextView) view.findViewById(R.id.item_count));
                    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

                    // PR 827815 sort_by_color count wrong.Modified by hz_nanbing.zou at 3/11/2014 begin
                    ((TextView) view.findViewById(R.id.item_count)).setText(String
                            .valueOf(c.getCount()));
                    // PR 827815 sort_by_color count wrong.Modified by hz_nanbing.zou at 3/11/2014 end

                    long id = c.getLong(c.getColumnIndex(Note.COLUMN_BG_IMAGE_RES_ID));
                    //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-723075,
                    //Long press note display wrong
                    if (!involvedId((int) GroupId)) {
                        //changeTheme(context, view, (int) id, true);
                        ((ImageView) (view.findViewById(R.id.select_image))).setVisibility(View.GONE);
                    } else {
                        //view.findViewById(R.id.grid_item).setBackgroundDrawable(
                        //        context.getResources().getDrawable(
                        //                R.drawable.list_selector_background_selected));
                        ((ImageView) (view.findViewById(R.id.select_image))).setVisibility(View.VISIBLE);
                        
                        //PR877899 The select box not perfect.Modified by hz_nanbing.zou at 24/12/2014 begin
//                        ((ImageView) (view.findViewById(R.id.select_image))).setImageResource(R.drawable.list_selector_background_selected);
//                        ((ImageView) (view.findViewById(R.id.select_image))).setImageBitmap(getBitmap());
                        ((ImageView) (view.findViewById(R.id.select_image))).setImageResource(R.drawable.long_pressed);
                      //PR877899 The select box not perfect.Modified by hz_nanbing.zou at 24/12/2014 end
                        
                        
                    }
                    changeTheme(context, view, (int) id, true);
                    //[BUGFIX]-Mod-END by TSCD.geron
                }
            }
            if (c != null)
                c.close();
        }
    }
    DisplayMetrics dm;
    @SuppressWarnings("deprecation")
	public void changeTheme(Context context, View view, int id, boolean isGroup) {
        if (isGroup) {
        	
        	view.findViewById(R.id.card1).setVisibility(View.VISIBLE);
        	view.findViewById(R.id.card2).setVisibility(View.VISIBLE);
        	
        	if(dm == null)
        		dm = new DisplayMetrics();
        	((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        	
        	view.findViewById(R.id.grid_item).setTranslationZ(12);
        	view.findViewById(R.id.grid_item).setTranslationX(6*dm.density);
        	view.findViewById(R.id.grid_item).setTranslationY(4*dm.density);
        	
        	view.findViewById(R.id.card1).setTranslationZ(2);
        	view.findViewById(R.id.card2).setTranslationX(3*dm.density);
        	view.findViewById(R.id.card2).setTranslationY(2*dm.density);
            view.findViewById(R.id.card2).setTranslationZ(6);

            setSort(view.findViewById(R.id.card1), true,0);
            setSort(view.findViewById(R.id.card2), true,1);
//            setSort(view.findViewById(R.id.card2), true,2);
            view.findViewById(R.id.card_layout).setBackgroundColor(0);
            
            //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang,2015-1-30,PR917795
            switch (id) {
                case Constants.NOTE_BG_White_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.search_theme_1));
                    
//                    view.findViewById(R.id.grid_item).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_white));
//                    
//                    view.findViewById(R.id.card1).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_white));
//                    
//                    view.findViewById(R.id.card2).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_white));
                    
                  view.findViewById(R.id.grid_item).setBackgroundDrawable(
                  context.getResources().getDrawable(R.drawable.shape_white));
                  view.findViewById(R.id.card1).setBackgroundDrawable(
                  context.getResources().getDrawable(R.drawable.shape_white));
                  view.findViewById(R.id.card2).setBackgroundDrawable(
                  context.getResources().getDrawable(R.drawable.shape_white));
                    
                    
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_one));
                    break;
                }
                case Constants.NOTE_BG_Blue_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.search_theme_2));
                	
//                    view.findViewById(R.id.grid_item).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_blue));
//                    view.findViewById(R.id.card1).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_blue));
//                    view.findViewById(R.id.card2).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_blue));
                	
                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_blue));
                            view.findViewById(R.id.card1).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_blue));
                            view.findViewById(R.id.card2).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_blue));
                	
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_two));
                    break;
                }
                case Constants.NOTE_BG_Green_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.search_theme_3));
//                    view.findViewById(R.id.grid_item).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_green));
//
//                    view.findViewById(R.id.card1).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_green));
//                    view.findViewById(R.id.card2).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_green));
                	
                	
                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_green));
                            view.findViewById(R.id.card1).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_green));
                            view.findViewById(R.id.card2).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_green));
                	
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_three));
                    break;
                }
                case Constants.NOTE_BG_Yellow_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.search_theme_4));
//                    view.findViewById(R.id.grid_item).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_yellow));
//                    view.findViewById(R.id.card1).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_yellow));
//                    view.findViewById(R.id.card2).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_yellow));
                	
                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_yellow));
                            view.findViewById(R.id.card1).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_yellow));
                            view.findViewById(R.id.card2).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_yellow));
                	
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_four));
                    break;
                }
                case Constants.NOTE_BG_Red_ID: {
//                  view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                          context.getResources().getDrawable(R.drawable.search_theme_4));
//                  view.findViewById(R.id.grid_item).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_red));
//                  view.findViewById(R.id.card1).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_red));
//                  view.findViewById(R.id.card2).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_red));
                	
                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_red));
                            view.findViewById(R.id.card1).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_red));
                            view.findViewById(R.id.card2).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_red));
              	
//                  ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                          .getResources().getColor(R.color.text_theme_four));
                  break;
              }
                case Constants.NOTE_BG_Purple_ID: {
//                  view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                          context.getResources().getDrawable(R.drawable.search_theme_4));
//                  view.findViewById(R.id.grid_item).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_purple));
//                  view.findViewById(R.id.card1).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_purple));
//                  view.findViewById(R.id.card2).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_purple));
                	
                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_purple));
                            view.findViewById(R.id.card1).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_purple));
                            view.findViewById(R.id.card2).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_purple));
              	
//                  ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                          .getResources().getColor(R.color.text_theme_four));
                  break;
              }
            }
           //[BUGFIX]-ADD-END by AMNJ.rurong.zhang,2015-1-30,PR917795
        } else {
        	
        	view.findViewById(R.id.card1).setVisibility(View.GONE);
        	view.findViewById(R.id.card2).setVisibility(View.GONE);
        	view.findViewById(R.id.card_layout).setTranslationZ(4);
            //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang,2015-1-30,PR917795
            switch (id) {
                case Constants.NOTE_BG_White_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.preview_theme_1));
                    
                    view.findViewById(R.id.card_layout).setBackgroundDrawable(
                          context.getResources().getDrawable(R.drawable.shape_white));
                    
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_one));
                    break;
                }
                case Constants.NOTE_BG_Blue_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.preview_theme_2));
                    
//                    view.findViewById(R.id.card_layout).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_blue));
                	
                	view.findViewById(R.id.card_layout).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_blue));
                    
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_two));
                    break;
                }
                case Constants.NOTE_BG_Green_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.preview_theme_3));
                	
//                	view.findViewById(R.id.card_layout).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_green));
                	
                	view.findViewById(R.id.card_layout).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_green));
                	
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_three));
                    break;
                }
                case Constants.NOTE_BG_Yellow_ID: {
//                    view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                            context.getResources().getDrawable(R.drawable.preview_theme_4));
                	
//                	view.findViewById(R.id.card_layout).setBackgroundColor(context
//                            .getResources().getColor(R.color.theme_bg_yellow));
                	
                	view.findViewById(R.id.card_layout).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_yellow));
                	
//                    ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                            .getResources().getColor(R.color.text_theme_four));
                    break;
                }
                case Constants.NOTE_BG_Red_ID: {
//                  view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                          context.getResources().getDrawable(R.drawable.preview_theme_4));
              	
//              	view.findViewById(R.id.card_layout).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_red));
                	
                	view.findViewById(R.id.card_layout).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_red));
              	
//                  ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                          .getResources().getColor(R.color.text_theme_four));
                  break;
              }
                case Constants.NOTE_BG_Purple_ID: {
//                  view.findViewById(R.id.grid_item).setBackgroundDrawable(
//                          context.getResources().getDrawable(R.drawable.preview_theme_4));
              	
//              	view.findViewById(R.id.card_layout).setBackgroundColor(context
//                          .getResources().getColor(R.color.theme_bg_purple));
                	
                	view.findViewById(R.id.card_layout).setBackgroundDrawable(
                            context.getResources().getDrawable(R.drawable.shape_purple));
              	
//                  ((TextView) view.findViewById(R.id.display_content)).setTextColor(context
//                          .getResources().getColor(R.color.text_theme_four));
                  break;
              }
            }
          //[BUGFIX]-ADD-END by AMNJ.rurong.zhang,2015-1-30,PR917795
        }
    }

    public void displayItem(Context context, View view, Cursor c) {
    	
    	//PR841643 DateFormat as same as setting.Modified by hz_nanbing.zou at 19/11/2014 begin
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
        		sdf = new SimpleDateFormat("hh:mm aa");
        	}
        	//PR860384 The time icon'：'looks so close to hour.Modified by hz_nanbing.zou at 03/12/2014 end

        	s_time = sdf.format(d);
        } else {
        	//PR866274 There has some overlap on note when set data format as Wed,Dec 11,2014. added by feilong.gu at 12/11/2014 start 
            //PR921151 Change dateformat.Modified by hz_nanbing.zou at 02/02/2015 begin  
        	sdf = new SimpleDateFormat("MM/dd/yyyy");

        	//PR932190 CTS,change the date format for Russian.Added by hz_nanbing.zou at 02/13/2015 begin
        	String language = Locale.getDefault().getLanguage();
        	TctLog.d("nb", "lang:"+language);
        	if(language.equals("ru")){
        		sdf = new SimpleDateFormat("dd.MM.yyyy");	
        	}
        	//PR932190 CTS,change the date format for Russian.Added by hz_nanbing.zou at 02/13/2015 end

            s_time = sdf.format(d);
            //PR921151 Change dateformat.Modified by hz_nanbing.zou at 02/02/2015 end
           //PR866274 There has some overlap on note when set data format as Wed,Dec 31,2014. added by feilong.gu at 12/11/2014 end
        }
        
        //PR841643 DateFormat as same as setting.Modified by hz_nanbing.zou at 19/11/2014 end 
        
        boolean Unchecked = false;
        if (DisplayWithGroup == NotesListActivity.DISPLAY_GROUP) {
            Unchecked = involvedId((int) mNote.mGroupId);
        } else {
            Unchecked = involvedId((int) mNote.mId);
        }
        //[BUGFIX]-Mod-BEGIN by TSCD.gerong,07/30/2014,PR-723075,
        //Long press note display wrong
        if (!Unchecked) {
            //changeTheme(context, view, mNote.mBgImageResId, false);
            ((ImageView) (view.findViewById(R.id.select_image))).setVisibility(View.GONE);
        } else {
            //view.findViewById(R.id.grid_item).setBackgroundDrawable(
            //        context.getResources()
            //                .getDrawable(R.drawable.list_selector_background_selected));
           ((ImageView) (view.findViewById(R.id.select_image))).setVisibility(View.VISIBLE);
           
           	//PR877899 The select box not perfect.Modified by hz_nanbing.zou at 24/12/2014 begin           
//           ((ImageView) (view.findViewById(R.id.select_image))).setImageResource(R.drawable.list_selector_background_selected);
           
           
//           ((ImageView) (view.findViewById(R.id.select_image))).setImageBitmap(getBitmap());
           ((ImageView) (view.findViewById(R.id.select_image))).setImageResource(R.drawable.long_pressed);
           //PR877899 The select box not perfect.Modified by hz_nanbing.zou at 24/12/2014 end     
           
        }
        changeTheme(context, view, mNote.mBgImageResId, false);
        //[BUGFIX]-Mod-END by TSCD.gerong

       // TctLog.e("ty--", "mNote.mHasImage: " + mNote.mHasImage);
        if (mNote.mHasImage == true) {
            String ImageStr = null;
            ((ImageView) view.findViewById(R.id.grid_item_image)).setVisibility(View.VISIBLE);
            Cursor Imagecursor = context.getContentResolver().query(Note.Image.CONTENT_URI, null,
                    Note.Image.COLUMN_NOTE_ID + " = " + mNote.mId, null, null);
            if (Imagecursor != null && Imagecursor.getCount() > 0) {
                Imagecursor.moveToFirst();
                ImageStr = Imagecursor.getString(Imagecursor.getColumnIndex(Note.Image.COLUMN_Content));
            }
            if (Imagecursor != null) {
                Imagecursor.close();
            }
            
            Bitmap b = cacheBmp.get(ImageStr);
            
            if(b !=null){
            	((ImageView) view.findViewById(R.id.grid_item_image)).setImageBitmap(b);
            }else
            	mImageLoader.DisplayImage(ImageStr,
                    ((ImageView) view.findViewById(R.id.grid_item_image)), false);
            if(ImageStr == null)
            	((ImageView) view.findViewById(R.id.grid_item_image)).setVisibility(View.GONE);
        }
        if (mNote.mHasText == true) {
            ((TextView) view.findViewById(R.id.display_content)).setVisibility(View.VISIBLE);
            Cursor Textcursor = context.getContentResolver().query(Note.Text.CONTENT_URI, null,
                    Note.Text.COLUMN_NOTE_ID + " = " + mNote.mId, null, null);
            if (Textcursor != null && Textcursor.getCount() > 0) {
                Textcursor.moveToFirst();
                String content = Textcursor.getString(Textcursor
                        .getColumnIndex(Note.Text.COLUMN_TEXT));
                ((TextView) view.findViewById(R.id.display_content)).setText(content);
            }
            if (Textcursor != null)
                Textcursor.close();
        }
        int AttachmentFlag = IsOnlyOneAttachment(mNote.mHasImage, mNote.mHasAudio,
                mNote.mHasReminder);
            
        ConfirmDisplayPlace(AttachmentFlag, view);

		// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
		if (mNote.mHasReminder) {
			//If has a Reminder,should sent a Notify to statue bar
			if (isSetStatuesBar) {
				// If set a notify,do not set again.
			} else {
				isSetStatuesBar = true;
//				sendNotify();
			}
		} else {
			//If set a notify,but then delete the Reminder,should cancel it
			if (!isSetStatuesBar) {
//				cancelNotify();
			}
		}
		// PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

        TextView mGName = ((TextView) view.findViewById(R.id.group_name));
        mGName.setVisibility(View.VISIBLE);
        mGName.setText(s_time);
    }

    public void ConfirmDisplayPlace(int flag, View view) {
        switch (flag) {
            case ONLY_HAS_IMAGE: {
                ((ImageView) view.findViewById(R.id.star_mark))
                        .setBackgroundResource(R.drawable.ic_image_grey600_18dp);
                
              //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 begin
//                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.GONE);
                //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 end
                
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

              //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 begin
//                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
              //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 end

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
                
                //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 begin
//              ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
              ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
            //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 end
              
                ((ImageView) view.findViewById(R.id.audio_mark)).setVisibility(View.GONE);
                break;
            }
            case HAS_ALL: {
                ((ImageView) view.findViewById(R.id.star_mark)).setVisibility(View.VISIBLE);
                
                //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 begin
//              ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.VISIBLE);
              ((ImageView) view.findViewById(R.id.image_mark)).setVisibility(View.GONE);
              //PR925534.New GD not show the image icon.Modified by hz_nanbing.zou at 06/02/2015 end
              
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
        
        //PR907335.After sort by color,icon display incomplete.Added by hz_nanbing.zou at 22/01/2015 begin
        setMargin((ImageView) view.findViewById(R.id.star_mark));
        //PR907335.After sort by color,icon display incomplete.Added by hz_nanbing.zou at 22/01/2015 end
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

    public boolean isToday(Date a) {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        return sdf2.format(today).equals(sdf2.format(a));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = null;
        v = mInflater.inflate(mLayout, parent, false);
        return v;
    }

    // PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 begin
/*    private int notifyId = R.string.note_reminder;
    *//**cancel a notify
     * return void
     *//*
    private void cancelNotify() {
    	NotificationManager manager = (NotificationManager) mContext
    			.getSystemService(Context.NOTIFICATION_SERVICE);
    	manager.cancel(notifyId);
    }
	*//**set alarm icon on statue bar
	 * return void
	 *//*
    private void sendNotify() {
    	ContentResolver resolver = mContext.getContentResolver();
    	Cursor c = resolver.query(Note.Reminder.CONTENT_URI, null, null, null,null);
    	//get all the reminder Cursor,then get the reminder time
    	while (c.moveToNext()) {
    		String time = c.getString(c
    				.getColumnIndex(Note.Reminder.COLUMN_TIME));
    		//the reminder time must be later than current time,it means the reminder not arrived
    		if (Long.parseLong(time) > System.currentTimeMillis()) {
    			NotificationManager manager = (NotificationManager) mContext
    					.getSystemService(Context.NOTIFICATION_SERVICE);
    			Notification notification = new Notification.Builder(mContext)
    					.setContentTitle("alert").setContentText("select ")
    					.setSmallIcon(R.drawable.reminder_mark).setOngoing(true)
    					.setAutoCancel(false)
    					.setPriority(Notification.PRIORITY_MAX)
    					.setDefaults(Notification.DEFAULT_LIGHTS).setWhen(0)
    					.build();
    			//the statue icon should not be clear,and can start the intent to main activity.
    			notification.flags = Notification.FLAG_NO_CLEAR;
    			Intent intent = new Intent(mContext, NotesListActivity.class);
    			
    			intent.putExtra("from_notify", true);
    			
    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
    			PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    			notification.setLatestEventInfo(mContext,mContext.getResources().getString(R.string.note_reminder),
    					mContext.getResources().getString(R.string.note_notify_text), pendingIntent);
    			manager.notify(notifyId, notification);
			break;
    		}
    	}
    }*/
    // PR804283 alarm icon not on statues bar.Add by hz_nanbing.zou at 13/10/2014 end

    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
    /**set text font
     * @param tv,the TextView which will set typeface
     */
//    private void setFont(TextView tv){
//			Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Thin.ttf");
//			tv.setTypeface(tf);
//    }
    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end
    
    //PR877899 The select box not perfect.Modified by hz_nanbing.zou at 24/12/2014 begin   
    public Bitmap getBitmap(){
    	
    	return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.long_pressed);
    }
    //PR877899 The select box not perfect.Modified by hz_nanbing.zou at 24/12/2014 end
    
    //PR907335.After sort by color,icon display incomplete.Added by hz_nanbing.zou at 22/01/2015 begin
    private void setMargin(ImageView img){
    	
//    	DisplayMetrics localDisplayMetrics = mContext.getResources().getDisplayMetrics();
//    	RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(img.getLayoutParams());
//    	llp.alignWithParent = true;
//    	llp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//    	llp.addRule(RelativeLayout.CENTER_VERTICAL);
//    	if(DisplayWithGroup == NotesListActivity.DISPLAY_GROUP && Groupcount > 1)
//    		llp.setMargins(llp.leftMargin, llp.topMargin, (int) (8*localDisplayMetrics.density), (int) (2*localDisplayMetrics.density));
//    	else
//    		llp.setMargins(llp.leftMargin, llp.topMargin, (int) (2*localDisplayMetrics.density), (int) (2*localDisplayMetrics.density));
//    	img.setLayoutParams(llp);
    }
    //PR907335.After sort by color,icon display incomplete.Added by hz_nanbing.zou at 22/01/2015 end
    
    private void setSort(View view,boolean flag,int indicator){
    	
    	DisplayMetrics localDisplayMetrics = mContext.getResources().getDisplayMetrics();
    	FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(view.getLayoutParams());
    	if(flag){
    		int x = 0;
    		int y = 0;
    		switch(indicator){
    		case 0:
    			x = 9;
    			y = 6;
    			break;
    		case 1:
    			x = 6;
    			y = 3;
    			break;
    		case 2:
    			break;
    		default:
    			break;
    		}
    		llp.setMargins(llp.leftMargin, llp.topMargin, (int) (x*localDisplayMetrics.density), (int) (y*localDisplayMetrics.density));
    	}
    	else
    		llp.setMargins(0, 0, 0, 0);
    	view.setLayoutParams(llp);
    }
    
}
