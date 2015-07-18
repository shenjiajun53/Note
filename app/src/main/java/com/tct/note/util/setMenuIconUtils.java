
package com.tct.note.util;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Menu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class setMenuIconUtils {
	
	public static void setIconEnable(Menu menu, boolean enable){
		
		try {
			Class<?> cla = Class.forName("com.android.internal.view.menu.MenuBuilder");
			try {
				Method m =cla.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
				m.setAccessible(true);
				m.invoke(menu, enable);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

/*
 * Location: /home/likewise-open/SAGEMWIRELESS/93416/Desktop/classes_dex2jar.jar
 * Qualified Name: com.miui.notes.editor.EditorUtils JD-Core Version: 0.6.2
 */
