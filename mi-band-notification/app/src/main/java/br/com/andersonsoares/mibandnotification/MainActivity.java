package br.com.andersonsoares.mibandnotification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.NotifyListener;
import com.zhaoxiaodan.miband.model.VibrationMode;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private AlertDialog enableNotificationListenerAlertDialog;
    public static MiBand miband;
    ScanCallback scanCallback;
    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();
    private android.widget.Button buscar;
    private android.widget.RelativeLayout activitymain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activitymain = (RelativeLayout) findViewById(R.id.activity_main);
        this.buscar = (Button) findViewById(R.id.buscar);

        // If the user did not turn the notification listener service on we prompt him to do so
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        miband = new MiBand(this);

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                Log.d(TAG,
                        "Device: name:" + device.getName() + ",uuid:"
                                + device.getUuids() + ",add:"
                                + device.getAddress() + ",type:"
                                + device.getType() + ",bondState:"
                                + device.getBondState() + ",rssi:" + result.getRssi());

                if(device != null &&  device.getName() != null && device.getName().contains("MI")){
                    MiBand.stopScan(scanCallback);
                    String item = device.getName() + "|" + device.getAddress();
                    if (!devices.containsKey(item)) {
                        devices.put(item, device);
                        //adapter.add(item);
                    }
                    connect(device);

                   // Intent intent = new Intent();
                    //intent.putExtra("device", device);
                    //intent.setClass(ScanActivity.this, MainActivity.class);
                    //ScanActivity.this.startActivity(intent);
                    //ScanActivity.this.finish();

                }


            }
        };
        //
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiBand.startScan(scanCallback);
            }
        });
    }

    public void connect(final BluetoothDevice device){

        final ProgressDialog pd = ProgressDialog.show(MainActivity.this, "", "Sincronizando Pulseira");
        miband.connect(device, new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
                pd.dismiss();
                Log.d(TAG, "Conectado!!!");
               // miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
              //  MainActivity.this.finish();
            }

            @Override
            public void onFail(int errorCode, String msg) {
                pd.dismiss();
                Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Erro ao sincronizar");
                alertDialogBuilder.setMessage("Ocorreu um erro ao sincronizar, Tentar novamente?");
                alertDialogBuilder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               connect(device);
                            }
                        });
                alertDialogBuilder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // If you choose to not enable the notification listener
                                // the app. will not work as expected
                            }
                        });

                alertDialogBuilder.create().show();
            }
        });

    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }




    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }
}
