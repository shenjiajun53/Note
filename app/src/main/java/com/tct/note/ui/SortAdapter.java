
//FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 begin
package com.tct.note.ui;

import java.util.List;

import com.tct.note.R;

import android.content.Context;
import android.graphics.Typeface;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> list;

	public SortAdapter(Context context,List<String> list) {
		mContext = context;
		this.list =list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_expandable_list_item_1,null);
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		
		//PR848145  Sort by,it will show a scroll bar.Added by hz_nanbing.zou at 21/11/2014 begin
//		View v = (View) convertView.findViewById(R.id.dividerbg);
//		if(position == 1){
//			v.setVisibility(View.GONE);
//		}
		//PR848145  Sort by,it will show a scroll bar.Added by hz_nanbing.zou at 21/11/2014 end
		
//		setFont(tv);
		tv.setText(list.get(position));
		return convertView;
	}
	
	
    /**set text font
     * @param tv,the TextView which will set typeface
     */
    private void setFont(TextView tv){
    	Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
    	tv.setTypeface(tf);
    }
    //FR816175 modify issue from UE team.Modified by hz_nanbing.zou at 21/10/2014 end

}
