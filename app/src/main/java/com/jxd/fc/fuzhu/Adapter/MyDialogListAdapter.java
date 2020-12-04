package com.jxd.fc.fuzhu.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jxd.fc.fuzhu.R;

public class MyDialogListAdapter extends BaseAdapter 
{
    private String[] texts;
    private int[] images;
    private Context context;

    public MyDialogListAdapter(Context context, int[] images, String[] texts) {
        this.context = context;
        this.images = images;
        this.texts = texts;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = null;
        view = mInflater.inflate(R.layout.dialog_view_list, null);
        
        ImageView image = (ImageView) view.findViewById(R.id.dialog_view_list1);
        image.setImageResource(images[position]);
        
        TextView text = (TextView) view.findViewById(R.id.dialog_view_list2);
        text.setText(texts[position]);
        
        return view;
    }




}
