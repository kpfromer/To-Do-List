package com.example.kylepfromer.homeworktodolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

class arraylist_task_adapter extends BaseAdapter {

    List<List<String>> data;

    Context context;

    //constructor
    public arraylist_task_adapter(Context context, List<List<String>> data) {
        this.context = context;
        this.data = data;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_task, null);
        //Gets the Textviews from the XML file
        TextView taskTextview = (TextView) rowView.findViewById(R.id.list_item_task);
        TextView dateTextview = (TextView) rowView.findViewById(R.id.list_item_task_date);
        //gets info
        String task = data.get(position).get(0);
        String date = data.get(position).get(1);
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYY-MM-dd");

        //Set Text
        taskTextview.setText(task);
        //Checks the due date and sets it
        try {
            final LocalDate aDate = dtf.parseLocalDate(date);
            //Prints the Short Abbreviated form of day, like tue, or mon
            dateTextview.setText(aDate.dayOfWeek().getAsShortText());
        }catch (IllegalArgumentException e){// If not then due date text is nothing
            dateTextview.setText("");
        }

        return rowView;
    }
}