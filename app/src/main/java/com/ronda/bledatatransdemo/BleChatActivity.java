package com.ronda.bledatatransdemo;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ronda.bledatatransdemo.base.AppManager;
import com.ronda.bledatatransdemo.base.BaseActivty;
import com.socks.library.KLog;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class BleChatActivity extends BaseActivty {

    public static final String EXTRA_DEVICE_NAME    = "device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";


    private TextView mTxtName, mTxtAddress, mTxtConnState;


    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;

    private BluetoothLeService mBluetoothLeService;

    private EditText mEditTextData; // 展示接收、发送数据

    private EditText mInputEdit; // 发送数据输入框
    private Button   mBtnSend; // 发送按钮

    private BluetoothGattService        mUSRService;   // USR 服务
    private BluetoothGattCharacteristic mNotifyCharac; // 用于读取模块数据


    private BluetoothGattCharacteristic mWriteCharac;  // 向模块写入数据

    // 启动 BluetoothLeService 服务时所需
    private final ServiceConnection mServiceConnection  = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            mBluetoothLeService.initialize();

            mBluetoothLeService.connect(mDeviceAddress);

            KLog.w("service:" + mBluetoothLeService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    mConnected = true;
                    updateConnectionState("已连接");
                    supportInvalidateOptionsMenu(); // 重绘 optionsMenu
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    mConnected = false;
                    updateConnectionState("未连接");
                    supportInvalidateOptionsMenu();

                    mUSRService = null;
                    mNotifyCharac = null;
                    mWriteCharac = null;
                    break;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED: // 发现 Service
                    //getUSRServicece(mBluetoothLeService.getSupportGattServices());
                    //setupReadAndWriteCharac();
                    // 获取有人提供的 usr 服务
                    mUSRService = mBluetoothLeService.getGattService(GattAttributes.USR_SERVICE);
                    if (mUSRService != null) {
                        // 获取通知操作的characteristic，用于读数据
                        mNotifyCharac = mUSRService.getCharacteristic(UUID.fromString(GattAttributes.NOTIFY_CHARAC));
                        // 获取写操作的characteristic
                        mWriteCharac = mUSRService.getCharacteristic(UUID.fromString(GattAttributes.WRITE_NO_RESPONSE_CHARAC));

                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharac, true); // 读 通知
                    }

                    KLog.w("mNotifyCharac --> uuid: " + mNotifyCharac.getUuid().toString());
                    KLog.w("mWriteCharac --> uuid: " + mWriteCharac.getUuid().toString());

                    setupReadAndWriteCharac();

                    break;
                case BluetoothLeService.ACTION_DATA_READ:
                    Toast.makeText(AppManager.getInstance().currentActivity(), "read data : " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA), Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothLeService.ACTION_DATA_WRITE:
                    //Toast.makeText(AppManager.getInstance().currentActivity(), "write data : " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA), Toast.LENGTH_SHORT).show();
                    mEditTextData.append("send: " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA) + "\n");
                    break;

                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    //Toast.makeText(AppManager.getInstance().currentActivity(), "read or write data : " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA), Toast.LENGTH_SHORT).show();
                    mEditTextData.append("receive: " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA) + "\n");
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_list);

        mDeviceName = getIntent().getStringExtra(EXTRA_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);

        initActionBar("ble Chat");

        initView();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE); // 启动服务
    }

    private void initView() {
        mTxtName = (TextView) findViewById(R.id.txt_device_name);
        mTxtAddress = (TextView) findViewById(R.id.txt_device_address);
        mTxtConnState = (TextView) findViewById(R.id.txt_device_state);
        mEditTextData = (EditText) findViewById(R.id.edit_data);

        mInputEdit = (EditText) findViewById(R.id.edit_input);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        mTxtName.setText(mDeviceName);
        mTxtAddress.setText(mDeviceAddress);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mInputEdit.getText().toString();
                if (text.isEmpty()) return;
                try {
                    if (mBluetoothLeService != null)
                        mBluetoothLeService.writeCharacteristic(mWriteCharac, text.getBytes("US-ASCII"));
                    //mInputEdit.setText("");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);

        registerReceiver(mGattUpdateReceiver, intentFilter);

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            KLog.w("Connect request result=" + result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);

        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtConnState.setText(state);
            }
        });
    }


    private void setupReadAndWriteCharac() {
        if (null == mNotifyCharac || null == mWriteCharac) {
            Toast.makeText(BleChatActivity.this, "未获取到 USR 服务", Toast.LENGTH_SHORT).show();
            return;
        }

        //BluetoothGattCharacteristic.PROPERTY_NOTIFY
        KLog.e("mNotifyCharac --> properties : " + mNotifyCharac.getProperties()); // 16 才对
        // BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE 、 PROPERTY_WRITE
        KLog.e("mWriteCharac --> properties : " + mWriteCharac.getProperties()); // 4 或 8 才对

        mBluetoothLeService.setCharacteristicNotification(mNotifyCharac, true); // 读 通知
//        mBluetoothLeService.setCharacteristicNotification(mWriteCharac, true); // 写 通知
    }
}
