package com.example.shohel.geopoll.classes;

import android.net.TrafficStats;
import android.support.v4.net.TrafficStatsCompat;


public class TrafficRecord {

    long tx = 0;
    long rx = 0;
    long mtx = 0;
    long mrx = 0;
    long wtx = 0;
    long wrx = 0;

    int kb = 1024;
//  int mb = 2048;
//  int gb = 4096;

    String tag = null;

    TrafficRecord() {
        mtx = TrafficStats.getMobileTxBytes();
        mrx = TrafficStats.getMobileRxBytes();
        tx = TrafficStats.getTotalTxBytes();
        rx = TrafficStats.getTotalRxBytes();
        // if(mtx != 0 && mrx != 0) {
        wtx = tx - mtx;
        wrx = rx - mrx;
        wtx = wtx / kb;
        wrx = wrx / kb;
        // }

        mtx = mtx / kb;
        mrx = mrx / kb;
        tx = tx / kb;
        rx = rx / kb;
    }

    TrafficRecord(int uid, String tag) {
        tx = TrafficStats.getUidTxBytes(uid);
        rx = TrafficStats.getUidRxBytes(uid);
        this.tag = tag;
    }

}
