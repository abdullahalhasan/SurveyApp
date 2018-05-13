package com.example.shohel.geopoll.classes;

/**
 * Created by BDDL-102 on 4/22/2018.
 */

public class Config {

    //Global Push Notification
    public  static final String TOPIC_GLOBAL = "/topics/global";

    //Broadcast receiver intents
    public static final String REG_COMPLETE = "registration complete";
    public static final String PUSH_NOTIFICATION = "push notification";

    //Id handling notification in notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String SHARED_OREF = "ah_firebase";
}
