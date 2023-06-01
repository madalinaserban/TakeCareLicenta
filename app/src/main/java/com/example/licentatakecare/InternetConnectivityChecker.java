package com.example.licentatakecare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class InternetConnectivityChecker {

    private Context context;
    private InternetConnectivityListener listener;
    private ConnectivityBroadcastReceiver receiver;

    public InternetConnectivityChecker(Context context, InternetConnectivityListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        this.receiver = new ConnectivityBroadcastReceiver();
    }

    public void start() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
    }

    public void stop() {
        context.unregisterReceiver(receiver);
    }

    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (listener != null) {
                    listener.onInternetConnectivityChanged(isConnected);
                }
            }
        }
    }

    public interface InternetConnectivityListener {
        void onInternetConnectivityChanged(boolean isConnected);
    }
}

