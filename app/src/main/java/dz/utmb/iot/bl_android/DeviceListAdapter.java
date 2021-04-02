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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

    private static final String PAIRED_TXT = "PAIRED";

    private static class ViewHolder {
        TextView deviceName, deviceAddress, deviceIsPaired;
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
            viewHolder.deviceIsPaired = (TextView) view.findViewById(R.id.device_is_paired);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DeviceModel bluetoothDevice = bluetoothDevices.get(i);
        viewHolder.deviceName.setText(bluetoothDevice.getName());
        viewHolder.deviceAddress.setText(bluetoothDevice.getAddress());
        viewHolder.deviceIsPaired.setText(bluetoothDevice.isPaired() ? PAIRED_TXT : "");
        return view;
    }
}
