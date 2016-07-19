package com.nativeblemodule.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by joelwasserman on 7/5/16.
 */
public class bleModule extends ReactContextBaseJavaModule {

    public BluetoothManager btManager;
    public BluetoothAdapter btAdapter;
    public BluetoothLeScanner btScanner;
    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();
    BluetoothGatt bluetoothGatt;
    int deviceIndex = 0;

    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public Map<String, String> uuids = new HashMap<String, String>();
    public Map<String, BluetoothGattCharacteristic> characteristics = new HashMap<String, BluetoothGattCharacteristic>();


    public bleModule(ReactApplicationContext reactContext) {
        super(reactContext);

        btManager = (BluetoothManager)getReactApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        uuids.put("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", "Status");
    }

    @Override
    public String getName() {
        return "BLE";
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            devicesDiscovered.add(result.getDevice());
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("DeviceDiscovered", deviceIndex + ": " + result.getDevice().getName());

            deviceIndex++;
        }
    };

    // Device connect call back
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("Event", "Characteristic Updated");

            readCharacteristic();
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            System.out.println(newState);
            switch (newState) {
                case 0:
                    // connected
                    getReactApplicationContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("DeviceStateChanged", "0, disconnected");
                    break;
                case 2:
                    // disconnected
                    getReactApplicationContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("DeviceStateChanged", "2, connected");

                    bluetoothGatt.discoverServices();

                    break;
                default:
                    getReactApplicationContext()
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("DeviceStateChanged", "default: shouldn't be hitting");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            } else {
                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("Event", "Something went wrong");
            }
        }
    };

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        String characteristic_to_read = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx";
        if (characteristic == characteristics.get(characteristic_to_read)) {
            final int count = ByteBuffer.wrap(characteristic.getValue()).order(ByteOrder.LITTLE_ENDIAN).getInt();
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("Event", count);
        } else {
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("Event", "Characteristic read");
        }
    }

    @ReactMethod
    public void startScanning() {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("Event", "Started Scanning");
        deviceIndex = 0;
        devicesDiscovered.clear();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    @ReactMethod
    public void stopScanning() {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("Event", "Stopped Scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    @ReactMethod
    public void writeCharacteristic() {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("Event", "wrote characteristic");

        // write value 1
        byte[] value = new byte[1];
        value[0] = (byte) (0 & 0xFF);
        BluetoothGattCharacteristic writeCharacteristicValue = characteristics.get("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        writeCharacteristicValue.setValue(value);
        bluetoothGatt.writeCharacteristic(writeCharacteristicValue);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            final String uuid = gattService.getUuid().toString();
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("ServiceDiscovered", uuids.get(uuid.toUpperCase()));
            new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {

                final String charUuid = gattCharacteristic.getUuid().toString();
                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("CharacteristicDiscovered", charUuid.toUpperCase());
                characteristics.put(charUuid.toUpperCase(), gattCharacteristic);
            }
        }
    }

    @ReactMethod
    public void connectToDeviceSelected(String deviceIndex) {
        int deviceSelected = Integer.parseInt(deviceIndex);
        bluetoothGatt = devicesDiscovered.get(deviceSelected).connectGatt(getReactApplicationContext(), false, btleGattCallback);
    }

    @ReactMethod
    public void disconnectDeviceSelected() {
        bluetoothGatt.disconnect();
    }

    @ReactMethod
    public void readCharacteristic() {
        if (btAdapter == null || bluetoothGatt == null) {
            System.out.println("BluetoothAdapter not initialized");
            return;
        }
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("Event", "Reading Characteristic");
        // replace with characteristic uuid you're trying to read
        bluetoothGatt.readCharacteristic(characteristics.get("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"));
    }

    @ReactMethod
    public void subscribeToCharacteristic() {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("Event", "Subscribing to characteristic");

        // replace with characteristic uuid you're trying to subscribe to
        BluetoothGattCharacteristic characteristicUpdate = characteristics.get("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        BluetoothGattDescriptor descriptor = characteristicUpdate.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        Boolean status = bluetoothGatt.writeDescriptor(descriptor);
        bluetoothGatt.setCharacteristicNotification(characteristicUpdate, true);
    }
}
