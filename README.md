# React-Native-Android-BLE-Module

React Native proof of concept for react native ble android module.

uses Android (M) API 23. Can scan and connect/disconnect to devices as well as read/write characteristics.

<b> This is only for Android and only supports API 23 (M). </b>

This is assuming you have react-native installed and have followed the <a href="https://facebook.github.io/react-native/docs/getting-started.html">Getting Started Guide</a> by facebook.

# Screen Shot

<p align="center">
    <img src="https://cloud.githubusercontent.com/assets/7344422/16934536/f5c56cb0-4d0b-11e6-9aff-9ec21748d6af.png">
</p>

# Setup

Getting up and running should be fairly easy. Steps to scan and view peripherals in your area/connect to said peripherals:
* clone repo: $ ```git clone git@github.com:Versame/React-Native-BLE-Bridge.git```
* run in your project directory: $ ```npm install```
* run application: from within your project directory run: $ ```react-native run-android```
* Tap Start Scanning to start scanning and Stop Scanning to stop scanning. The output should be printed in the Output Log
* The output will be the peripheral (or gatt server) name, as specified on line 73 of bleModule.java
* If you'd like to connect, type in the index of the peripheral you'd like to connect to and tap the Connect To Device With Index: Button

Steps to read/write to peripherals you're connected to:
* clone repo: $ ```git clone git@github.com:Versame/React-Native-BLE-Bridge.git```
* Navigate to your_project_directory/android/app/src/main/java/com/nativeblemodule/ble/bleModule.java
* Find and replace all instances of ```"xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"``` with the different characteristic UUID values that you will be reading/writing/subscribing to. 
* Use the Read Characteristic, Subscribe to Characteristic, and Write Characteristic buttons to do the appropriate task. 

# Documentation

* To get started with BLE, the following permissions will need to be granted in the your_project_directory/android/app/src/main/AndroidManifest.xml file
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-feature android:name="android.hardware.bluetooth_le"  android:required="true" />
```
* Keep in mind we're supporting Android API 23 (M), so we'll need to make sure that both the minSdkVersion and the targetSdkVersion in your_project_directory/android/app/src/main/AndroidManifest.xml file are set to 23
```
<uses-sdk
        android:minSdkVersion="23"
        android:targetSdkVersion="23" />
```
* Within the ble package (the files in your_project_directory/android/app/src/main/java/com/nativeblemodule/ble/) we see the bleModule.java and the blePackage.java files. The bleModule.java file is where the meat of BLE bridging is done. Here we can see generic BluetoothGatt API calls. 
* Our bridge communicates to the react native application through the RCTDeviceEventEmitter. We have listeners set up in our react native application to listen to series of events emit by our bluetooth api methods. 
```
componentDidMount() {
    DeviceEventEmitter.addListener('DeviceDiscovered', (result) => {this.setState({textViewText: this.state.textViewText + result + "\n"})});
    DeviceEventEmitter.addListener('DeviceStateChanged', (stateChange) => {this.setState({textViewText: this.state.textViewText + "State Changed: " + stateChange + "\n"})});
    DeviceEventEmitter.addListener('Event', (eventDescription) => {this.setState({textViewText: this.state.textViewText + eventDescription + "\n"})});
    DeviceEventEmitter.addListener('ServiceDiscovered', (serviceDiscovered) => {this.setState({textViewText: this.state.textViewText + "Service Discovered: " + serviceDiscovered + "\n"})});
    DeviceEventEmitter.addListener('CharacteristicDiscovered', (characteristicDiscovered) => {this.setState({textViewText: this.state.textViewText + "Characteristic Discovered: " + characteristicDiscovered + "\n"})});
  }
```
* If you want to abstract out the API calls even further, you can pass in the characteristic string values from react-native to the bridge. 

# Support

Feel free to create an issue and we'll happily walk you through any setup/diagnose any issues.

# License

MIT



