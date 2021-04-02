package dz.utmb.iot.bl_android;

import android.bluetooth.BluetoothDevice;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.UUID;

public class DeviceModel implements Serializable {
    private BluetoothDevice bluetoothDevice;
    private String name;
    private String address;
    private boolean isPaired = false;

    public DeviceModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public DeviceModel(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            this.bluetoothDevice = bluetoothDevice;
            this.name = bluetoothDevice.getName();
            this.address = bluetoothDevice.getAddress();
        }
    }

    public DeviceModel(BluetoothDevice bluetoothDevice, boolean isPaired) {
        if (bluetoothDevice != null) {
            this.bluetoothDevice = bluetoothDevice;
            this.name = bluetoothDevice.getName();
            this.address = bluetoothDevice.getAddress();
            this.isPaired = true;
        }
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, address);
    }
}
