package com.zone.instoresdk.instoresdk;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterView;

/** InStoreSDKPlugin */
public class InStoreSDKPlugin implements MethodCallHandler {
  public  static final String CHANNEL_ID = "zonemobile_foreground_service_channel";
  private static final String PLUGIN_CHANNEL = "InStoreSDKPlugin";
  private final Activity activity;
  private final FlutterView flutterView;
  static MethodChannel channel;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    channel = new MethodChannel(registrar.messenger(), PLUGIN_CHANNEL);
    channel.setMethodCallHandler(new InStoreSDKPlugin(registrar.activity(),registrar.view()));
  }
  private InStoreSDKPlugin(Activity activity, FlutterView view){
    this.activity = activity;
    this.flutterView = view;
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if (call.method.equals("startBackgroundNotification")) {
      startService(activity);

      new EventChannel( flutterView, PLUGIN_CHANNEL).setStreamHandler(
        new EventChannel.StreamHandler() {
          private BroadcastReceiver broadcastReceiver;
          @Override
          public void onListen(Object arguments, EventChannel.EventSink events) {
              broadcastReceiver = createBroadcastReceiver(events);
            activity.registerReceiver(
                    broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
          }

          @Override
          public void onCancel(Object arguments) {
            activity.unregisterReceiver(broadcastReceiver);
              broadcastReceiver = null;
          }
        }
    );
      result.success(null);
    }else if (call.method.equals("loopget")) {
        System.out.print("ping!");
        result.success(call.arguments);
    }else if (call.method.equals("loop")) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                channel.invokeMethod("loopget","hello somebody@");
            }
        },0,1000);
    }else {
      System.out.println("booooom");
      result.notImplemented();
    }
  }

  public  void startService(Activity activity){
      Class mainActivityClass = activity.getClass();
      Context context = activity.getBaseContext();
      Intent notificationIntent = new Intent(context, mainActivityClass);
//      Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
      notificationIntent.setAction("SELECT_NOTIFICATION");
      notificationIntent.putExtra("payload","payyload");
//      notificationIntent.setClass(context, mainActivityClass);

    PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,notificationIntent,0);

    RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.zone_custom_notification);

    Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setContentTitle("Zone Service")
            .setContentText("Click here to launch instore payment")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(remoteViews).build();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel =  new NotificationChannel(
                CHANNEL_ID,
                "Zone Instore Service",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationChannel.setDescription("Sticky Notificaiton");
        notificationChannel.setSound(null,null);
        notificationChannel.enableVibration(false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

      NotificationManagerCompat notificationManager = getNotificationManager(context);
      notificationManager.notify(1, notification);







  }

  private static NotificationManagerCompat getNotificationManager(Context context) {
    return NotificationManagerCompat.from(context);
  }
    private BroadcastReceiver createBroadcastReceiver(final EventChannel.EventSink events) {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                events.success("Hello world!$$$");
            }
        };
    }
}
