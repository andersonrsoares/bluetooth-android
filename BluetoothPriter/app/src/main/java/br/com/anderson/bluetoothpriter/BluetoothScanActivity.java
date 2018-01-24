package br.com.anderson.bluetoothpriter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import br.com.anderson.bluetoothpriter.printer.AdapterBluetooth;
import br.com.anderson.bluetoothpriter.printer.Device;
import br.com.anderson.bluetoothpriter.printer.DrawerService;
import br.com.anderson.bluetoothpriter.printer.Global;

public class BluetoothScanActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private ProgressDialog dialog;
    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;

    private static Handler mHandler = null;
    private static String TAG = "BluetoothScanActivity";
    BluetoothAdapter adapter;


    Button button;
    ListView listView;
    AdapterBluetooth adapterBluetooth;
    List<Device> devices = new ArrayList<>();

    RelativeLayout progressBarSearchStatus;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);


        dialog = new ProgressDialog(this);
        initBroadcast();

        mHandler = new MHandler(this);
        DrawerService.addHandler(mHandler);

        listView = (ListView) findViewById(R.id.listView);
        adapterBluetooth = new AdapterBluetooth(this,devices);
        listView.setAdapter(adapterBluetooth);
        listView.setOnItemClickListener(this);

        button = (Button) findViewById(R.id.buttonSearch);
        button.setOnClickListener(this);

        progressBarSearchStatus = (RelativeLayout)  findViewById(R.id.progressBarSearchStatus);
        progressBar = (ProgressBar)  findViewById(R.id.progressBar);
        progressBarSearchStatus.setVisibility(View.GONE);
        //progressBar.setIndeterminate(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DrawerService.delHandler(mHandler);
        mHandler = null;
        uninitBroadcast();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.buttonSearch: {
                adapter = BluetoothAdapter.getDefaultAdapter();
                if (null == adapter) {
                    finish();
                    break;
                }

                if (!adapter.isEnabled()) {
                    if (adapter.enable()) {
                        while (!adapter.isEnabled())
                            ;
                        Log.v(TAG, "Enable BluetoothAdapter");
                    } else {
                        finish();
                        break;
                    }
                }

                adapter.cancelDiscovery();
                devices.clear();
                adapterBluetooth.notifyDataSetChanged();
                adapter.startDiscovery();
                break;
            }
        }
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null)
                        return;
                    final String address = device.getAddress();
                    String name = device.getName();
                    if (name == null)
                        name = "?";
                    else if (name.equals(address))
                        name = "?";
                    Button button = new Button(context);
                    button.setText(name + ": " + address);
                    button.setGravity(android.view.Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            // 只有没有连接且没有在用，这个才能改变状态
                            dialog.setMessage("正在连接 " + address);
                            dialog.setIndeterminate(true);
                            dialog.setCancelable(false);
                            dialog.show();
                            DrawerService.workThread.connectBt(address);
                        }
                    });
                    button.getBackground().setAlpha(100);
                    Device d = new Device();
                    d.setName(name);
                    d.setAddress(address);
                    devices.add(d);
                    adapterBluetooth.notifyDataSetChanged();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    devices.clear();
                    progressBarSearchStatus.setVisibility(View.VISIBLE);
                    //progressBar.setIndeterminate(true);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    progressBarSearchStatus.setVisibility(View.GONE);
                    //progressBar.setIndeterminate(false);
                }

            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    static class MHandler extends Handler {

        WeakReference<BluetoothScanActivity> mActivity;

        MHandler(BluetoothScanActivity activity) {
            mActivity = new WeakReference<BluetoothScanActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothScanActivity theActivity = mActivity.get();
            switch (msg.what) {


                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? "Conexão é bem sucedida" : "Falha de conexão",
                            Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Connect Result: " + result);

                    theActivity.dialog.cancel();

                    if (result == 1)
                        theActivity.finish();
                    break;
                }

            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Device d = (Device) parent.getItemAtPosition(position);
        dialog.setMessage("Conectando com " +  d.getAddress());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        DrawerService.workThread.connectBt(d.getAddress());
    }
}
