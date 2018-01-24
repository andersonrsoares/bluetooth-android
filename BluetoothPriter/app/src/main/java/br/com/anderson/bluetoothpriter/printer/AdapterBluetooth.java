package br.com.anderson.bluetoothpriter.printer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.anderson.bluetoothpriter.R;

/**
 * Created by DevMaker on 8/12/16.
 */
public class AdapterBluetooth extends BaseAdapter {

    Context context;
    List<Device> bluetoothDevices;
    public AdapterBluetooth(Context context, List<Device> bluetoothDevices){
        this.context = context;
        this.bluetoothDevices = bluetoothDevices;
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Device getItem(int position) {
        return bluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_printer,null);
        TextView tvListItemPrinterName = (TextView) convertView.findViewById(R.id.tvListItemPrinterName);
        TextView tvListItemPrinterMac = (TextView) convertView.findViewById(R.id.tvListItemPrinterMac);

        tvListItemPrinterName.setText(getItem(position).getName());
        tvListItemPrinterMac.setText(getItem(position).getAddress());

        return convertView;
    }
}
