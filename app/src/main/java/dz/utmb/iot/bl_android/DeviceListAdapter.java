package dz.utmb.iot.bl_android;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView deviceName, deviceAddress;
    }

    Activity activity;
    ArrayList<DeviceModel> bluetoothDevices = new ArrayList<>();
    LayoutInflater layoutInflater = null;
    ViewHolder viewHolder = null;

    public DeviceListAdapter(Activity activity, ArrayList<DeviceModel> bluetoothDevices) {
        this.activity = activity;
        this.bluetoothDevices = bluetoothDevices;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.bluetoothDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return this.bluetoothDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.device_list_row, null);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DeviceModel bluetoothDevice = bluetoothDevices.get(i);
        viewHolder.deviceName.setText(bluetoothDevice.getName());
        viewHolder.deviceAddress.setText(bluetoothDevice.getAddress());
        return view;
    }
}
