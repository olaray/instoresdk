import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

class InStoreSDKPlugin {
  static const MethodChannel _channel = const MethodChannel('InStoreSDKPlugin');
  static const EventChannel eventChannel = EventChannel('InStoreSDKPlugin');

  static Future<String> get startBackgroundNotification async {
    final String version =
        await _channel.invokeMethod('startBackgroundNotification');
    return version;
  }

  static void loop({
    @required Function onEvent,
    Function broadcastError,
  }) {
    eventChannel
        .receiveBroadcastStream()
        .listen(onEvent, onError: broadcastError);
  }
}
