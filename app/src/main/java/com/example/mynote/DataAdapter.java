package com.example.mynote;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DataAdapter extends BaseAdapter {
    private Activity activity;
    private List<Note> notes;

    public DataAdapter(Activity activity, List<Note> notes) {
        this.activity = activity;
        this.notes = notes;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.note_content, null);
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lbl_title);
        TextView lblDateModified = (TextView) convertView.findViewById(R.id.lbl_date_modified);
        TextView lblContent = (TextView) convertView.findViewById(R.id.lbl_content);


        String title = notes.get(position).getTitle();
        String content = notes.get(position).getContent();
        title = (title.length() > 30) ? title.substring(0, 29) + "..." : title;
        content = (content.length() > 40) ? content.substring(0, 39)  + "..." : content;

        lblTitle.setText(title);
        lblContent.setText(content);
        lblDateModified.setText(notes.get(position).getDateModified());
;       return convertView;
    }
}
