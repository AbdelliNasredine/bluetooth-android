package dz.utmb.iot.bl_android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RQ_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private Button bluetoothToggleButton;
    private ListView pairedDevicesListView;
    private DeviceListAdapter deviceListAdapter;
    private final ArrayList<DeviceModel> deviceModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get ui objects
        bluetoothToggleButton = (Button) findViewById(R.id.bluetooth_btn);
        pairedDevicesListView = (ListView) findViewById(R.id.list_view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // stop the app if device does not support bluetooth
        if (bluetoothAdapter == null)
            finish();

        // init ui elements
        setupUI();
    }


    private void setupUI() {
        String buttonText = bluetoothAdapter.isEnabled() ? "disable" : "enable";
        bluetoothToggleButton.setText(buttonText);
        bluetoothToggleButton.setOnClickListener((event) -> {
            if (bluetoothToggleButton.getText().equals("enable")) {
                enableBluetooth();
            } else {
                disableBluetooth();
            }
        });
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, RQ_ENABLE_BT);
        }
    }

    private void disableBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            bluetoothToggleButton.setText("enable");
            Toast.makeText(this, "Bluetooth is OFF", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void loadPairedDevices() {
        deviceListAdapter = new DeviceListAdapter(this, deviceModelList);
        pairedDevicesListView.setAdapter(deviceListAdapter);
    }

    private void queryPairedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceModel deviceModel = new DeviceModel(device);
                deviceModelList.add(deviceModel);
            }
            loadPairedDevices();
            Toast.makeText(this, String.format("%s paired device(s) found", pairedDevices.size())
                    , Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "No paired bluetooth device found", Toast.LENGTH_SHORT)
                    .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth is ON", Toast.LENGTH_SHORT)
                        .show();
                queryPairedDevices();
                bluetoothToggleButton.setText("disable");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth is OFF", Toast.LENGTH_SHORT)
                        .show();
                bluetoothToggleButton.setText("enable");
            }
        }
    }
}