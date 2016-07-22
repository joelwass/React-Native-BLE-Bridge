# React-Native-Android-BLE-Module

React Native proof of concept for react native ble android module.

uses Android (M) API 23. Can scan and connect/disconnect to devices as well as read/write characteristics.

<b> This is only for Android and only supports API 23 (M). </b>

This is assuming you have react-native installed and have followed the <a href="https://facebook.github.io/react-native/docs/getting-started.html">Getting Started Guide</a> by facebook.

# Screen Shot

![screenshot_20160718-170004](https://cloud.githubusercontent.com/assets/7344422/16934536/f5c56cb0-4d0b-11e6-9aff-9ec21748d6af.png)

# Setup

Getting up and running should be fairly easy. Steps to scan and view peripherals in your area/connect to said peripherals:
* clone repo: $ ```git clone git@github.com:Versame/React-Native-BLE-Bridge.git```
* run in your project directory: $ npm install
* run application: from within your project directory run: $ react-native run-android
* Tap Start Scanning to start scanning and Stop Scanning to stop scanning. The output should be printed in the Output Log
* The output will be the peripheral (or gatt server) name, as specified on line 73 of bleModule.java
* If you'd like to connect, type in the index of the peripheral you'd like to connect to and tap the Connect To Device With Index: Button

Steps to read/write to peripherals you're connected to:
* clone repo: ```$ git clone git@github.com:Versame/React-Native-BLE-Bridge.git```
* Navigate to your_project_directory/android/app/src/main/java/com/nativeblemodule/ble/bleModule.java
* Find and replace all instances of ```"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"``` with the different characteristic UUID values that you will be reading/writing/subscribing to. 
* Use the Read Characteristic, Subscribe to Characteristic, and Write Characteristic buttons to do the appropriate task. 

# Documentation


# License

MIT



