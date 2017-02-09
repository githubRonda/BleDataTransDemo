package com.ronda.bledatatransdemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.socks.library.KLog;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public final static String ACTION_GATT_CONNECTED           = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED        = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE           = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA                      = "com.example.bluetooth.le.EXTRA_DATA";

    public final static String ACTION_DATA_READ  = "com.example.bluetooth.le.ACTION_DATA_READ";
    public final static String ACTION_DATA_WRITE = "com.example.bluetooth.le.ACTION_DATA_WRITE";


    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING   = 1;
    private static final int STATE_CONNECTED    = 2;

    private int mConnectionState = STATE_DISCONNECTED;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt    mBluetoothGatt;
    private String           mBluetoothDeviceAddress;


    /**
     * 调用 device.connectGatt() 连接时的回调方法
     */
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            KLog.w("onConnectionStateChange --> status: " + status + ", newState: " + newState);

            // 发送广播
            if (newState == BluetoothProfile.STATE_CONNECTED) { // 蓝牙连接成功
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                mBluetoothGatt.discoverServices(); // 发现服务（必须要蓝牙先连接成功）
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 蓝牙连接中断
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            KLog.w("onServicesDiscovered --> status: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            broadcastUpdate(ACTION_DATA_READ, characteristic);
            try {
                KLog.w("onCharacteristicRead --> status: " + status + ", characteristic.value:" + new String(characteristic.getValue(), "US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            broadcastUpdate(ACTION_DATA_WRITE, characteristic);
            try {
                KLog.w("onCharacteristicWrite --> status: " + status + ", characteristic.value:" + new String(characteristic.getValue(), "US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            try {
                KLog.w("onCharacteristicChanged --> characteristic.value:" + new String(characteristic.getValue(), "US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };

    public BluetoothLeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public void initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            if (mBluetoothAdapter == null) {
                mBluetoothAdapter = mBluetoothManager.getAdapter();
            }
        }

    }

    public boolean connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            KLog.w("BluetoothAdapter not initialized or address is null.");
            return false;
        }

        // 判断是否是再次连接的(可以复用 BluetoothGatt)，还是需要重新连接
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            KLog.w("Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            KLog.w("Device not found");
            return false;
        }

        // 连接远程蓝牙
        mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        KLog.w("device.connectGatt");
        return true;
    }

    // 获取 Service
    public List<BluetoothGattService> getSupportGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothGattService getGattService(String uuid) {
        return mBluetoothGatt.getService(UUID.fromString(uuid));
    }

    private void broadcastUpdate(String action) {
        sendBroadcast(new Intent(action));
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            try {
                intent.putExtra(EXTRA_DATA, new String(data, "US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        sendBroadcast(intent);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
    }

    // 当点击某 Service 中的 Characteristic 的 properties 为 read 时，可调用这个方法
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            KLog.w("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] byteArray) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        characteristic.setValue(byteArray);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }


    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
