package com.bdth.bdjk;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bdth.bean.Vehicle;

public class MyAdapter extends BaseAdapter {
    // 上下文对象
    @SuppressWarnings("unused")
    private Context context;

    private List<Vehicle> list;

    /**
     * 解析器
     */
    private LayoutInflater mInflater;

    MyAdapter(Context context, List<Vehicle> list) {
	this.context = context;
	this.list = list;
	mInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
	if(list==null)
	    return 0;
	return list.size();
    }

    public Object getItem(int item) {
	return item;
    }

    public long getItemId(int id) {
	return id;
    }

    // 创建View方法
    public View getView(int position, View convertView, ViewGroup parent) {
	ViewHolder holder = null;
	if (convertView == null) {
	    holder = new ViewHolder();
	    convertView = mInflater.inflate(R.layout.carinfo, null);
	    holder.img = (ImageView) convertView.findViewById(R.id.imageStatus);
	    holder.tv = (TextView) convertView.findViewById(R.id.textInfo);
	    convertView.setTag(holder);
	} else {
	    holder = (ViewHolder) convertView.getTag();
	}

	holder.img
		.setImageResource((list.get(position).getState() == 0) ? R.drawable.main_car
			: R.drawable.main_car);

	holder.tv.setText(list.get(position).getPlateNumber());

	return convertView;
    }

}

class ViewHolder {
	
	public ImageView img;

	public TextView tv;
	
}