package dz.utmb.iot.bl_android;

import android.bluetooth.BluetoothDevice;

public class DeviceModel {
    private String name;
    private String address;

    public DeviceModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public DeviceModel(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            this.name = bluetoothDevice.getName();
            this.address = bluetoothDevice.getAddress();
        }
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
}
