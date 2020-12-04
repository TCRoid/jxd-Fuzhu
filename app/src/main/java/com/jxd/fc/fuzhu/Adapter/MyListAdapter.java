package com.jxd.fc.fuzhu.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jxd.fc.fuzhu.R;
import java.util.List;
import java.util.Map;

public class MyListAdapter extends BaseAdapter {
    private List<Map<String, Object>> ListData;
    private Context context;

    public MyListAdapter(Context context, List<Map<String, Object>> ListData) {
        this.ListData = ListData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ListData == null ? 0 : ListData.size();
    }

    @Override
    public Object getItem(int position) {
        return ListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = null;
        view = mInflater.inflate(R.layout.list_view_file, null);

        CardView CardBackground = (CardView) view.findViewById(R.id.list_item_background);
        ImageView image = (ImageView) view.findViewById(R.id.list_item_img);

        if (Integer.parseInt(ListData.get(position).get("isDirectory") + "") == 2) {
            //文件夹
            image.setImageResource(R.drawable.ic_folder);
            CardBackground.setCardElevation(16);
        } else if (Integer.parseInt(ListData.get(position).get("isDirectory") + "") == 1) { 
            //文件
            image.setImageResource(R.drawable.ic_file);
        }

        TextView textView = (TextView) view.findViewById(R.id.list_item_text);
        textView.setText(ListData.get(position).get("fileName") + "");

        return view;

    }


}
