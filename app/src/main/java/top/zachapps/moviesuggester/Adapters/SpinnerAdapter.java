package top.zachapps.moviesuggester.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import top.zachapps.moviesuggester.R;
import top.zachapps.moviesuggester.UIAssets.Font;

/**
 * Created by zach on 04-Mar-16.
 */
public class SpinnerAdapter extends BaseAdapter {

    public SpinnerAdapter(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    ArrayList<String> data;
    Context context;
    LayoutInflater mInflater;

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_item, parent, false);
            holder = new Holder();
            holder.txtView = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.txtView.setText(data.get(position));
        Font.setFace(context, holder.txtView);
        return convertView;
    }

    static class Holder
    {
        TextView txtView;
    }
}
