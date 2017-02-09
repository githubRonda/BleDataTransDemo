package com.ronda.bledatatransdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;

import java.util.ArrayList;

public class DeviceScanActivity extends AppCompatActivity {

    private static final int  REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD       = 10000; // Stops scanning after 10 seconds.

    private ListView            mListView;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();

    private boolean mScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        KLog.init(true, "liu");

        // 判断手机是否支持蓝牙4.0
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "该设备不支持 Ble", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 判断手机是否支持蓝牙
        if (null == mBluetoothAdapter) {
            Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        mListView = (ListView) findViewById(R.id.list_view);
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mListView.setAdapter(mLeDeviceListAdapter);

       mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
               if (null == device){
                   return;
               }
               Intent intent = new Intent(DeviceScanActivity.this, BleChatActivity.class);
               intent.putExtra(BleChatActivity.EXTRA_DEVICE_NAME, device.getName());
               intent.putExtra(BleChatActivity.EXTRA_DEVICE_ADDRESS, device.getAddress());
               startActivity(intent);

               if (mScanning){
                   mScanning = false;
                   scanLeDevice(false);
               }
           }
       });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        scanLeDevice(true); // 开启扫描蓝牙
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false); // 停止扫描
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ENABLE_BT == requestCode && Activity.RESULT_CANCELED == resultCode){
            finish();
            return;
        }

        // 开始扫描Ble设备
        scanLeDevice(true);
    }


    /**
     * OptionsMenu 的相关回调
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mScanning) {
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_grogress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    /**
     * 开始扫描 和 停止扫描
     *
     * @param enable
     */
    private void scanLeDevice(boolean enable) {
        if (enable) { // 开始扫描
            mScanning = true;
            mBluetoothAdapter.startLeScan(mScanCallback);

            // 10s 后停止扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(false);
                }
            }, SCAN_PERIOD);
        } else { // 停止扫描
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mScanCallback);

        }

        supportInvalidateOptionsMenu(); // 重绘选项菜单
    }

    /**
     * 开始扫描设备和停止扫描设备对应的回调方法
     */
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) { //rssi(Received Signal Strength Indication) 是接收信号强度

            KLog.w("Thread: name: " + Thread.currentThread().getName() + "id: " + Thread.currentThread().getId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KLog.w("Thread: name: " + Thread.currentThread().getName() + "id: " + Thread.currentThread().getId());
                    mLeDeviceListAdapter.addDevice(device);
                }
            });
        }
    };


    /**
     * ListView 的适配器类
     */
    private class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater             mInflater;

        public LeDeviceListAdapter() {
            mLeDevices = new ArrayList<>();
            mInflater = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                notifyDataSetChanged();
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.item_device, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BluetoothDevice device = mLeDevices.get(position);
            String deviceName = device.getName();
            if (null != deviceName && !deviceName.isEmpty()) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("未知的设备");
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            return convertView;
        }
    }

    /**
     * ListView 对应的 ViewHolder
     */
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
