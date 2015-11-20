package com.example.kylepfromer.homeworktodolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class task_adapter extends ArrayAdapter<String[]> {
    private final Context context;
    private final String[][] values;

    public task_adapter(Context context, String[][] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_task, null);
        //Gets the Textviews from the XML file
        TextView taskTextview = (TextView) rowView.findViewById(R.id.list_item_task);
        TextView dateTextview = (TextView) rowView.findViewById(R.id.list_item_task_date);
        //gets info
        String task = values[position][0];
        String date = values[position][1];
        //Converts to Joda Time object: LOCALTIME
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYY-MM-dd");
        final LocalDate aDate = dtf.parseLocalDate(date);
        //Set Text
        taskTextview.setText(task);
        //Prints the Short Abbreviated form of day, like tue, or mon
        dateTextview.setText("DADADADADADADADADADADADAD");

        return rowView;
    }
}
