package com.jxd.fc.fuzhu.Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.CommonUtils.FileUtils;
import com.jxd.fc.fuzhu.R;
import java.util.List;
import java.util.Map;

public class MyListAdapter_Backup extends BaseAdapter {
    private List<Map<String, Object>> ListData;
    private Context context;

    public MyListAdapter_Backup(Context context, List<Map<String, Object>> ListData) {
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
        view = mInflater.inflate(R.layout.list_view_backup, null);

        //标题
        TextView title = (TextView) view.findViewById(R.id.list_item_title);
        //文件名称
        String name = ListData.get(position).get("fileName") + "";
        //取前缀名
        String prefix=name.substring(0, name.lastIndexOf("."));
        title.setText(prefix);

        //类型
        TextView type = (TextView) view.findViewById(R.id.list_item_type);
        //取后缀名
        String suffix=name.substring(name.lastIndexOf(".") + 1);
        if (suffix.equals("all"))
            type.setText("全部数据备份");
        else
            //type.setText("备份角色："+ suffix);
            type.setText(suffix);
        
        //时间
        TextView time = (TextView) view.findViewById(R.id.list_item_time);
        //文件路径
        String filepath = ListData.get(position).get("filePath") + "";
        time.setText(FileUtils.getLastModTime(filepath));

        return view;

    }


}
