import 'package:flutter/material.dart';

import 'package:instoresdk/instoresdk.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    startService();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  void startService() {
    InStoreSDKPlugin.startBackgroundNotification;
    InStoreSDKPlugin.loop(onEvent: (object) {
      print("--------------");
      print(object);
      print("--------------");
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
      ),
    );
  }
}
