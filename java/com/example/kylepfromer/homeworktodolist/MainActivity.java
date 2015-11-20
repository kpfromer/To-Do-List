package com.example.kylepfromer.homeworktodolist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private arraylist_task_adapter mTasksAdapter;
    private boolean isEdit = false;
    private String taskFile = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Sets up notification service
        task_notification_handler.context = this;
        task_notification_handler.textFile = taskFile;
        task_notification_handler.task_notification_handlerStart();
        //Sets up listview
        final ArrayList<List<String>> data = getDataFile();
        mTasksAdapter = new arraylist_task_adapter(this, data);
        final ListView listView = (ListView) findViewById(R.id.tasks);
        listView.setAdapter(mTasksAdapter);

        //Sets up selection for mutiple items
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //Creates Selecton Actions
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            //Shows amount of selected items
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(listView.getCheckedItemCount() + " selected items");
                if(listView.getCheckedItemCount()==1) {
                    mode.getMenu().findItem(R.id.menu_edit).setVisible(true);
                } else {
                    mode.getMenu().findItem(R.id.menu_edit).setVisible(false);
                }
            }
            //Creates Action Bar
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.activity_main, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            //If Someone clicks on action bar item
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if(item.getItemId() == R.id.menu_delete) {
                    //Delete Item In ListView
                    SparseBooleanArray checked = listView.getCheckedItemPositions();

                    List<String> deleteList = new ArrayList<String>();
                    deleteList.add("");
                    deleteList.add("");

                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            data.set(checked.keyAt(i), deleteList);
                        }
                    }
                    data.removeAll(Collections.singleton(deleteList));
                    mTasksAdapter.notifyDataSetChanged();
                    setDataFile(data);
                }else if (item.getItemId() == R.id.menu_edit){
                    //Edit An Item in List View
                    isEdit = true;
                    //Show and open EditText
                    EditText entry = (EditText) findViewById(R.id.task_entry);
                    //Deletes Text in EditText
                    entry.selectAll();
                    entry.setText("");
                    entry.setVisibility(View.VISIBLE);
                }
                mode.finish();
                return true;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        //Gets the EditText Entry
        final EditText entry = (EditText) findViewById(R.id.task_entry);
        entry.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            //Checks if the user pushes Send
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //Gets EditText
                    EditText mEdit = (EditText) findViewById(R.id.task_entry);
                    v.setVisibility(View.GONE);
                    if(!isEdit) {
                        //Adds task to data list
                        List<String> task = new ArrayList<String>();
                        //DUEDATE is placeholder for when i get
                        //the duedate text graber working!
                        try {
                            task.add(deleteDateIfFound(mEdit.getText().toString()));
                            task.add(getDate(mEdit.getText().toString()).toString());
                        } catch (NullPointerException e){
                            task.add(mEdit.getText().toString());
                            task.add("");
                        }
                        data.add(task);
                        setDataFile(data);
                    }else{
                        List<String> EditList = new ArrayList<String>();
                        try {
                            EditList.add(deleteDateIfFound(entry.getText().toString()));
                            EditList.add(getDate(entry.getText().toString()).toString());
                        } catch (NullPointerException e){
                            EditList.add(entry.getText().toString());
                            EditList.add("");
                        }
                        SparseBooleanArray checked = listView.getCheckedItemPositions();
                        for(int i = 0; i <listView.getCount(); i++){
                            if(checked.valueAt(i) == true){
                                    data.set(checked.keyAt(i), EditList);
                            }
                        }
                        setDataFile(data);
                    }
                    //Tells adapter data has changed
                    mTasksAdapter.notifyDataSetChanged();
                    //Hides the keyboard
                    try {


                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (NullPointerException e){}
                    handled = true;
                }
                return handled;
            }
        });

    }

    //If data is changed method called
    //to update file
    /*
    private void setDataFile(ArrayList<List<String>> data){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(this.taskFile, Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.setIndent("  ");

                writer.beginArray();
                for (int x = 0; x<data.size();x++) {
                    writer.beginArray();
                    writer.name("text").value(data.get(x).get(0));
                    writer.name("duedate").value(data.get(x).get(1));
                    writer.endArray();
                }
                writer.endArray();

            writer.close();
        } catch (Exception e) {}
    }*/
    private void setDataFile(ArrayList<List<String>> data){
        try{
            FileOutputStream fos = openFileOutput(this.taskFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        }catch (Exception e){}
    }
    //Get data on load up
    private ArrayList<List<String>> getDataFile(){
        FileInputStream fis;
        try {
            fis = openFileInput(this.taskFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<List<String>> returnlist = (ArrayList<List<String>>) ois.readObject();
            ois.close();
            return returnlist;
        } catch (Exception e) {
            return new ArrayList<List<String>>();
        }
    }

    //Checks if user clicks somewhere else
    //While the keyboard is selected,
    //If So, dismisses the keyboard
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }
    public String deleteDateIfFound(String s){
        s = s.toLowerCase();
        s = s.replace("monday", "");
        s = s.replace("tuesday", "");
        s = s.replace("wednesday", "");
        s = s.replace("thursday", "");
        s = s.replace("friday", "");
        s = s.replace("saturday", "");
        s = s.replace("sunday", "");

        s = s.replace("mon", "");
        s = s.replace("tue", "");
        s = s.replace("wed", "");
        s = s.replace("thu", "");
        s = s.replace("fri", "");
        s = s.replace("sat", "");
        s = s.replace("sun", "");

        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase().replaceAll("\\s+", " ");
    }
    public LocalDate getDate(String taskText){
        taskText = taskText.toLowerCase();
        LocalDate Date = new LocalDate();
        if(taskText.contains("mon")){
            if (Date.getDayOfWeek() >= DateTimeConstants.MONDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.MONDAY);
        }else if(taskText.contains("tue")){
            if (Date.getDayOfWeek() >= DateTimeConstants.TUESDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.TUESDAY);
        }else if(taskText.contains("wed")){
            if (Date.getDayOfWeek() >= DateTimeConstants.WEDNESDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.WEDNESDAY);
        }else if(taskText.contains("thu")){
            if (Date.getDayOfWeek() >= DateTimeConstants.THURSDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.THURSDAY);
        }else if(taskText.contains("fri")){
            if (Date.getDayOfWeek() >= DateTimeConstants.FRIDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.FRIDAY);
        }else if(taskText.contains("sat")){
            if (Date.getDayOfWeek() >= DateTimeConstants.SATURDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.SATURDAY);
        }else if(taskText.contains("sun")){
            if (Date.getDayOfWeek() >= DateTimeConstants.SUNDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.SUNDAY);
        }

        //returns null if no day found in text
        return null;
        /*
        day = day.substring(0, Math.min(day.length(), 3)).toLowerCase();
        if(day=="mon") {
            if (Date.getDayOfWeek() >= DateTimeConstants.MONDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.MONDAY);
        }else if (day=="tue"){
            if (Date.getDayOfWeek() >= DateTimeConstants.TUESDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.TUESDAY);
        }else if (day=="wed"){
            if (Date.getDayOfWeek() >= DateTimeConstants.WEDNESDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.WEDNESDAY);
        }else if (day=="thu"){
            if (Date.getDayOfWeek() >= DateTimeConstants.THURSDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.THURSDAY);
        }else if (day=="fri"){
            if (Date.getDayOfWeek() >= DateTimeConstants.FRIDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.FRIDAY);
        }else if (day=="sat"){
            if (Date.getDayOfWeek() >= DateTimeConstants.SATURDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.SATURDAY);
        }else if (day=="sun"){
            if (Date.getDayOfWeek() >= DateTimeConstants.SUNDAY) {
                Date = Date.plusWeeks(1);
            }
            return Date.withDayOfWeek(DateTimeConstants.SUNDAY);

        }
        */
    }
    //When button is pressed
    public void task_add(View view){
        isEdit = false;
        EditText entry = (EditText) findViewById(R.id.task_entry);
        //Deletes Text in EditText
        entry.selectAll();
        entry.setText("");
        //Makes the EditText Visible
        entry.setVisibility(View.VISIBLE);
    }
}
