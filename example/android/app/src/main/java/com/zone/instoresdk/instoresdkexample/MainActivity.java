package com.zone.instoresdk.instoresdkexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class MainActivity extends FlutterActivity {
    private static final String PLUGIN_CHANNEL = "InStoreSDKPlugin";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
      System.out.println("--------------------");
      System.out.println(getIntent().getStringExtra("payload"));
      System.out.println("--------------------");
    if(getIntent().getStringExtra("payload")!=null){
        new EventChannel( getFlutterView(), PLUGIN_CHANNEL).setStreamHandler(
                new EventChannel.StreamHandler() {
                    private BroadcastReceiver broadcastReceiver;
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        broadcastReceiver = createBroadcastReceiver(events);
                        registerReceiver(
                                broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        unregisterReceiver(broadcastReceiver);
                        broadcastReceiver = null;
                    }
                }
        );
    }
  }



    private BroadcastReceiver createBroadcastReceiver(final EventChannel.EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                events.success("Hey, You clicked on notification");

            }
        };
    }
}
