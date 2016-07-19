/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  TouchableHighlight,
  Text,
  View,
  NativeModules,
  DeviceEventEmitter,
  ScrollView,
  TextInput,
} from 'react-native';

class nativeBLEModule extends Component {

  constructor(props) {
    super(props);
    this.state = {
      deviceIndex: '0',
      textViewText: 'press start scan to start scanning\n',
    };
    this._startScanning = this._startScanning.bind(this);
    this._stopScanning = this._stopScanning.bind(this);
    this._connectToDevice = this._connectToDevice.bind(this);
    this._disconnectFromDevice = this._disconnectFromDevice.bind(this);
    this._readCharacteristic = this._readCharacteristic.bind(this);
    this._subscribeCharacteristic = this._subscribeCharacteristic.bind(this);
    this._writeCharacteristic = this._writeCharacteristic.bind(this);
    this.updateTextView = this.updateTextView.bind(this);
  }

  componentDidMount() {
    DeviceEventEmitter.addListener('DeviceDiscovered', (result) => {this.setState({textViewText: this.state.textViewText + result + "\n"})});
    DeviceEventEmitter.addListener('DeviceStateChanged', (stateChange) => {this.setState({textViewText: this.state.textViewText + "State Changed: " + stateChange + "\n"})});
    DeviceEventEmitter.addListener('Event', (eventDescription) => {this.setState({textViewText: this.state.textViewText + eventDescription + "\n"})});
    DeviceEventEmitter.addListener('ServiceDiscovered', (serviceDiscovered) => {this.setState({textViewText: this.state.textViewText + "Service Discovered: " + serviceDiscovered + "\n"})});
    DeviceEventEmitter.addListener('CharacteristicDiscovered', (characteristicDiscovered) => {this.setState({textViewText: this.state.textViewText + "Characteristic Discovered: " + characteristicDiscovered + "\n"})});
  }

  componentWillUnmount() {
    clearTimeout(this.timer);
  }

  _startScanning() {
    NativeModules.BLE.startScanning();
  }

  _stopScanning() {
    NativeModules.BLE.stopScanning();
  }

  _connectToDevice() {
    this.updateTextView("Connecting to device: " + this.state.deviceIndex + "\n");
    NativeModules.BLE.connectToDeviceSelected(this.state.deviceIndex);
  }

  _disconnectFromDevice() {
    this.updateTextView("Disconnecting from device\n");
    NativeModules.BLE.disconnectDeviceSelected();
  }

  _readCharacteristic() {
    NativeModules.BLE._readCharacteristic();
  }

  _subscribeCharacteristic() {
    NativeModules.BLE.subscribeToCharacteristic();
  }

  _writeCharacteristic() {
    NativeModules.BLE._writeCharacteristic();
  }

  updateTextView(text) {
    this.setState({textViewText: this.state.textViewText + text});
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.buttonContainer}>
          <TouchableHighlight 
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._startScanning}>
            <Text>
              Start Scanning
            </Text>
          </TouchableHighlight>

          <TouchableHighlight 
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._stopScanning}>
            <Text>
              Stop Scanning
            </Text>
          </TouchableHighlight>

          <TouchableHighlight
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._disconnectFromDevice}>
            <Text>
              Disconnect from device
            </Text>
          </TouchableHighlight>
        </View>

        <TouchableHighlight
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._connectToDevice}>
          <Text>
            Connect To Device With Index:
          </Text>
        </TouchableHighlight>

        <TextInput
        style={styles.deviceIndexInput}
        keyboardType={'numeric'}
        onChangeText={(text) => this.setState({deviceIndex: text})}
        value={this.state.deviceIndex} 
        />

        <View style={styles.buttonContainer}>
          <TouchableHighlight 
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._readCharacteristic}>
            <Text>
              Read Characteristic
            </Text>
          </TouchableHighlight>

          <TouchableHighlight
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._subscribeCharacteristic}>
            <Text>
              Subscribe To Characteristic
            </Text>
          </TouchableHighlight>
        </View>

        <View style={styles.buttonContainer}>
          <TouchableHighlight 
          style={styles.button}
          underlayColor={'grey'}
          onPress={this._writeCharacteristic}>
            <Text>
              Write Characteristic
            </Text>
          </TouchableHighlight>
        </View>

        <Text style={styles.welcome}>
          Output Log:
        </Text>

        <ScrollView 
        ref='scrollView'
        style={styles.peripheralContainer}
        onContentSizeChange={(width, height) => {this.refs.scrollView.scrollTo({y: height})}}>
          <Text
          style={styles.textViewText}>
          {this.state.textViewText}
          </Text>
        </ScrollView>

      </View>
    );
  }
}

const styles = StyleSheet.create({
  peripheralContainer: {
    flex: 1,
    width: 300,
    marginBottom: 10,
    borderWidth: 2,
    borderRadius: 5,
    borderColor: 'black',
    paddingHorizontal: 10,
    borderStyle: 'solid',
    backgroundColor: 'lavender',
  },
  buttonContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  textViewText: {
    alignItems: 'center',
    paddingBottom: 20,
  },
  deviceIndexInput: {
    width: 50,
    alignItems: 'center',
  },
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  button: {
    backgroundColor: 'lavender',
    marginVertical: 5,
    borderRadius: 5,
    paddingHorizontal: 10,
    paddingVertical: 10,
    marginHorizontal: 2,
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('nativeBLEModule', () => nativeBLEModule);
