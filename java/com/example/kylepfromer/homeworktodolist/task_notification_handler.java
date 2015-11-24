package com.example.kylepfromer.homeworktodolist;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class task_notification_handler {
    static public Context context;
    static public String textFile;
    private static ArrayList<List<String>> data;

    public static void task_notification_handlerStart(){
        Intent intent = new Intent(context, notificationTime.class);
        context.startService(intent);
    }

    private static void sendNotification(String title){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle("Homework: ");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(title));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[]{0, 1500});

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(8, notification);
    }

    private static ArrayList<List<String>> getDataFile(){
        FileInputStream fis;
        try {
            String taskFile = textFile;
            fis = context.openFileInput(taskFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<List<String>> returnlist = (ArrayList<List<String>>) ois.readObject();
            ois.close();
            return returnlist;
        } catch (Exception e) {
            return new ArrayList<List<String>>();
        }
    }//Work on data sorting by date

    private static String sortDate(){
        data = getDataFile();
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYY-MM-dd");
        String finalStr = "You have these tasks due:\n";
        try{
            for(List<String> x : data) {//Goes through List
                LocalDate date = dtf.parseLocalDate(x.get(1));
                LocalDate cDate = new LocalDate();
                try {
                    if (Days.daysBetween(cDate, date).getDays() <= 7) {//If the task is due less than a week
                        finalStr += x.get(0)+" due on "+dtf.parseLocalDate(x.get(1)).dayOfWeek().getAsShortText()+"\n";
                    }
                } catch (Exception e){}//Fix
            }
            return finalStr;
        } catch (Exception e) {
            return "";
        }
    }

    public static class notificationTime extends IntentService {

        public notificationTime(){
            super("notificationTime");
        }

        protected void onHandleIntent(Intent intent) {
            /*
            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                //Millis Second time
                                LocalTime result = new LocalTime();
                                //Checks if the time is the morning
                                //Simplfy THIS
                                if (result.getHourOfDay() == 8 && result.getMinuteOfHour() == 45) {
                                    String s;
                                    sendNotification(sortDate());
                                } else if (result.getHourOfDay() == 15 && result.getMinuteOfHour() == 20) {
                                    String s;
                                    sendNotification(sortDate());
                                } else if (result.getHourOfDay() == 13 && result.getMinuteOfHour() == 39){//FOR TESTING, REMOVE LATER
                                    String s;
                                    sendNotification(sortDate());
                                }
                            } catch (Exception e) {}
                        }
                    });
                }
            };
            //Check if the minute of hour is a mutiple of 15
            while(true) {
                int minuteofhour = new LocalTime().getMinuteOfHour();
                if (minuteofhour == 0 || minuteofhour == 15 || minuteofhour == 30 || minuteofhour == 45) {//FIX
                    timer.scheduleAtFixedRate(doAsynchronousTask, new Date(), 900000); //execute in now and every 15 min
                    break;
                }
            }
        */




            class sN extends TimerTask
            {
                public void run()
                {
                    sendNotification(sortDate());
                }
            }

            final DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy kk:mm");
            Timer timer = new Timer();
            TimerTask notificationScheduler = new TimerTask() {
                final Handler handler = new Handler();
                @Override
                public void run() {
                    handler.post(new Runnable() {
                         public void run() {
                             //Now create the time and schedule it
                             Timer timer = new Timer();
                             //Use this if you want to execute it once
                             LocalDateTime lc = new LocalDateTime();
                             timer.schedule(new sN(), (fmt.parseLocalDateTime(
                                     Integer.toString(lc.getMonthOfYear()) + "/" +
                                     Integer.toString(lc.getDayOfMonth()) + "/" +
                                     Integer.toString(lc.getYear()) + " " +
                                     Integer.toString(7) + ":" +
                                     Integer.toString(0)
                             )).toDate());
                             timer.schedule(new sN(), (fmt.parseLocalDateTime(
                                     Integer.toString(lc.getMonthOfYear()) + "/" +
                                     Integer.toString(lc.getDayOfMonth()) + "/" +
                                     Integer.toString(lc.getYear()) + " " +
                                     Integer.toString(15) + ":" +
                                     Integer.toString(20)
                             )).toDate());
                         }
                    });
                }
            };
            LocalDateTime nowDate = new LocalDateTime();
            LocalDateTime schDate = fmt.parseLocalDateTime(
                    Integer.toString(nowDate.getMonthOfYear()) + "/" +
                            Integer.toString(nowDate.getDayOfMonth()) + "/" +
                            Integer.toString(nowDate.getYear()) + " " +
                            Integer.toString(1) + ":" +
                            Integer.toString(0)
            );
            timer.scheduleAtFixedRate(notificationScheduler, schDate.toDate(), 86400000);//Run everyday starting at 1am
            notificationScheduler.run();//Run the notification today
        }
    }
}
