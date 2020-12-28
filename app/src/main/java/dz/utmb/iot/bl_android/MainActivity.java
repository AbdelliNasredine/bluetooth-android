package dz.utmb.iot.bl_android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int DISCOVERY_DURATION = 600; // 10 minutes
    private static final int RQ_BT_ENABLE = 1;
    private static final int RQ_BT_DISCOVERABLE = 2;

    /**
     * all attributes (ui widgets + utils class)
     */
    private LinearLayout pairedDevicesLinearLayout;
    private Button scanButton;
    private Button discoveryButton;
    private BluetoothAdapter bluetoothAdapter;
    private ListView pairedDevicesListView;
    private DeviceListAdapter deviceListAdapter;
    private final ArrayList<DeviceModel> deviceModelList = new ArrayList<>();

    /**
     * Broadcast Receivers
     */
    private final BroadcastReceiver discoveryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "discovery BroadcastReceiver: found device" + device);
                deviceModelList.add(new DeviceModel(device));
                deviceListAdapter.notifyDataSetChanged();
            }
        }
    };

//    private final BroadcastReceiver selfDiscoverableBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
//                String mode = intent.getStringExtra(BluetoothAdapter.EXTRA_SCAN_MODE);
//                if(mode.equals(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
//
//                }else if(mode.equals(BluetoothAdapter.SCAN_MODE_NONE)) {
//
//                }
//            }
//        }
//    };



    /**
     * OnCreate LifeCycle Method
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init ui elements
        setupUI();

        // stop the app if device does not support bluetooth
        if (bluetoothAdapter == null) {
            finish();
        }
    }

    /**
     * Methods for initializing ui functions & widgets
     */

    private void setupUI() {
        // get ui objects
        Switch bluetoothSwitch = (Switch) findViewById(R.id.bt_switch);
        scanButton = (Button) findViewById(R.id.bt_scan);
        discoveryButton = (Button) findViewById(R.id.bt_discoverability);
        pairedDevicesListView = (ListView) findViewById(R.id.list_view);
        pairedDevicesLinearLayout = (LinearLayout) findViewById(R.id.paired_devices_layout);

        // get bluetooth adapter object
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // setting up bluetooth switch
        bluetoothSwitch.setChecked(bluetoothAdapter.isEnabled());
        bluetoothSwitch.setOnCheckedChangeListener((switchBtn, isChecked) -> {
            Log.i(TAG, "bl switch button : clicking");
            if (isChecked) {
                enableBluetooth();
            } else {
                disableBluetooth();
            }
        });

        // setting up bluetooth scan button
        scanButton.setOnClickListener((listener) -> {
            Log.i(TAG, "scan button onclick : start discovery");
            bluetoothAdapter.startDiscovery();
        });

        // setting up discoverability button
        discoveryButton.setOnClickListener((listener) -> {
            Log.i(TAG, "setupUI: enabling self discovery for " + DISCOVERY_DURATION + " seconds");
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERY_DURATION);
            startActivityForResult(discoverableIntent, RQ_BT_DISCOVERABLE);
        });

        // setting up ListViewAdapter
        deviceListAdapter = new DeviceListAdapter(this, deviceModelList);
        pairedDevicesListView.setAdapter(deviceListAdapter);

        // setting up intent filter for discovery / self discoverable  broadcast receiver
        IntentFilter discoveryIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter discoverableIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter);
//        registerReceiver(selfDiscoverableBroadcastReceiver, discoverableIntentFilter);
    }

    private void enableBluetooth() {
        Log.i(TAG, "enableBluetooth: called");
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, RQ_BT_ENABLE);
        }
    }

    private void disableBluetooth() {
        Log.i(TAG, "disableBluetooth: called");
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast.makeText(this, "Bluetooth is OFF", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void showPairedDevices(final Set<BluetoothDevice> devices) {
        Log.i(TAG, "showPairedDevices: paired devices size " + devices.size());
        deviceModelList.clear();
        for (BluetoothDevice device : devices) {
            deviceModelList.add(new DeviceModel(device));
        }
        deviceListAdapter.notifyDataSetChanged();
    }

    private void queryPairedDevices() {
        Log.i(TAG, "queryPairedDevices: getting all paired devices");
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            showPairedDevices(pairedDevices);
            Toast.makeText(this, String.format("%s paired device(s) found", pairedDevices.size())
                    , Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "queryPairedDevices: no paired device found");
            Toast.makeText(this, "No paired bluetooth device found", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * OnActivityResult Callback Method
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handling the result of bluetooth enabling call
        if (requestCode == RQ_BT_ENABLE && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: Bluetooth is ON");
            Toast.makeText(this, "Bluetooth is ON", Toast.LENGTH_SHORT)
                    .show();
            queryPairedDevices();
        }
        if (requestCode == RQ_BT_ENABLE && resultCode == RESULT_CANCELED) {
            Log.i(TAG, "onActivityResult: Bluetooth enable request CANCELED");
            Toast.makeText(this, "Bluetooth is CANCELED", Toast.LENGTH_SHORT)
                    .show();
        }

        // handling the result of bluetooth discoverable call
        if (requestCode == RQ_BT_DISCOVERABLE && resultCode == DISCOVERY_DURATION) {
            Log.i(TAG, "onActivityResult: bluetooth is being discoverable for " + DISCOVERY_DURATION);
        }
        if (requestCode == RQ_BT_DISCOVERABLE && resultCode == RESULT_CANCELED) {
            Log.i(TAG, "onActivityResult: bluetooth self discovery is CANCELED");
        }
    }

    /**
     * OnDestroy LifeCycle Method
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoveryBroadcastReceiver);
    }
}