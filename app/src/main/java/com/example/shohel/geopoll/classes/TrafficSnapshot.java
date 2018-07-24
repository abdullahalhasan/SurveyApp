package com.example.shohel.geopoll.classes;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.HashMap;

/**
 * Created by BDDL-102 on 5/20/2018.
 */

public class TrafficSnapshot {

    TrafficRecord device = null;
    HashMap<Integer, TrafficRecord> apps = new HashMap<>();

    TrafficSnapshot(Context context) {
        device = new TrafficRecord();
        HashMap<Integer, String> appNames = new HashMap<>();
        for (ApplicationInfo app : context.getPackageManager().getInstalledApplications(0)) {
            appNames.put(app.uid,app.packageName);
        }

        for (Integer uid : appNames.keySet()) {
            apps.put(uid, new TrafficRecord(uid,appNames.get(uid)));
        }
    }
}
