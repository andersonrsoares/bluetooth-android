package br.com.anderson.bluetoothpriter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.pos.Cmd;
import com.lvrenyang.utils.DataUtils;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.com.anderson.bluetoothpriter.printer.DrawerService;
import br.com.anderson.bluetoothpriter.printer.Global;

public class MainActivity extends AppCompatActivity {

    private static Handler mHandler = null;
    Button button;
    TextView dispositivo;

    private static int nFontSize, nTextAlign, nScaleTimesWidth,
            nScaleTimesHeight, nFontStyle, nLineHeight = 32, nRightSpace;

    String strEnglish = "~!@#$%^&*()_+`[]{}\\|;',./:\"<>?1234567890-=abcdefghijklmnopqrstuvwxyz\n\n\n\n\n\n\n\n\n\n";


    private static int nBarcodetype, nStartOrgx, nBarcodeWidth = 1,
            nBarcodeHeight = 3, nBarcodeFontType, nBarcodeFontPosition = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button imprimir1 = (Button) findViewById(R.id.imprimir);
        imprimir1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] setHT = {0x1b,0x44,0x18,0x00};
                byte[] HT = {0x09};
                byte[] LF = {0x0d,0x0a};
                byte[][] allbuf = new byte[][]{
                        setHT,"FOOD".getBytes(),HT,"PRICE".getBytes(),LF,LF,
                        setHT,"DECAF16".getBytes(),HT,"30".getBytes(),LF,
                        setHT,"ISLAND BLEND".getBytes(),HT,"180".getBytes(),LF,
                        setHT,"FLAVOR SMALL".getBytes(),HT,"30".getBytes(),LF,
                        setHT,"Kenya AA".getBytes(),HT,"90".getBytes(),LF,
                        setHT,"CHAI".getBytes(),HT,"15.5".getBytes(),LF,
                        setHT,"MOCHA".getBytes(),HT,"20".getBytes(),LF,
                        setHT,"BREVE".getBytes(),HT,"1000".getBytes(),LF,LF,LF
                };
                byte[] buf = DataUtils.byteArraysToBytes(allbuf);
                if (DrawerService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putByteArray(Global.BYTESPARA1, buf);
                    data.putInt(Global.INTPARA1, 0);
                    data.putInt(Global.INTPARA2, buf.length);
                    DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                } else {
                    Toast.makeText(getBaseContext(), "Dispositivo não conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button imprimir2 = (Button) findViewById(R.id.imprimir2);
        imprimir2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Charset> charsetMap = Charset.availableCharsets();
                Collection<Charset> charsetColl = charsetMap.values();
                Iterator<Charset> iter = charsetColl.iterator();
                for (int i = 0; i < charsetColl.size(); i++) {
                    Log.v("", iter.next().displayName());
                }
                if (DrawerService.workThread.isConnected()) {
                    int charset = 0, codepage = 0;
                    String text = "";
                    String encoding = "";
                    byte[] addBytes = new byte[0];

                    text = "teste de inmpressao\n";//strEnglish;
                    encoding = "US-ASCII";
                    charset = 0;
                    codepage = 0;

                    Bundle dataCP = new Bundle();
                    Bundle dataAlign = new Bundle();
                    Bundle dataRightSpace = new Bundle();
                    Bundle dataLineHeight = new Bundle();
                    Bundle dataTextOut = new Bundle();
                    Bundle dataWrite = new Bundle();
                    dataCP.putInt(Global.INTPARA1, charset);
                    dataCP.putInt(Global.INTPARA2, codepage);
                    dataAlign.putInt(Global.INTPARA1, nTextAlign);
                    dataRightSpace.putInt(Global.INTPARA1, nRightSpace);
                    dataLineHeight.putInt(Global.INTPARA1, nLineHeight);
                    dataTextOut.putString(Global.STRPARA1, text);
                    dataTextOut.putString(Global.STRPARA2, encoding);
                    dataTextOut.putInt(Global.INTPARA1, 0);
                    dataTextOut.putInt(Global.INTPARA2, nScaleTimesWidth);
                    dataTextOut.putInt(Global.INTPARA3, nScaleTimesHeight);
                    dataTextOut.putInt(Global.INTPARA4, nFontSize);
                    dataTextOut.putInt(Global.INTPARA5, nFontStyle);
                    dataWrite.putByteArray(Global.BYTESPARA1, addBytes);
                    dataWrite.putInt(Global.INTPARA1, 0);
                    dataWrite.putInt(Global.INTPARA2, addBytes.length);

                    DrawerService.workThread.handleCmd(
                            Global.CMD_POS_SETCHARSETANDCODEPAGE, dataCP);
                    DrawerService.workThread.handleCmd(Global.CMD_POS_SALIGN,
                            dataAlign);
                    DrawerService.workThread.handleCmd(
                            Global.CMD_POS_SETRIGHTSPACE, dataRightSpace);
                    DrawerService.workThread.handleCmd(
                            Global.CMD_POS_SETLINEHEIGHT, dataLineHeight);
                    DrawerService.workThread.handleCmd(Global.CMD_POS_STEXTOUT,
                            dataTextOut);
                    DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE,
                            dataWrite);

                    DrawerService.workThread.handleCmd(Global.CMD_POS_FEEDLINE,
                            new Bundle());

                    DrawerService.workThread.handleCmd(Global.CMD_POS_FEEDLINE,
                            new Bundle());

                    DrawerService.workThread.handleCmd(Global.CMD_POS_FEEDLINE,
                            new Bundle());
                } else {
                    Toast.makeText(getBaseContext(), "Dispositivo não conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button imprimir3 = (Button) findViewById(R.id.imprimir3);
        imprimir3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strBarcode ="1234567890";

                int nOrgx = nStartOrgx * 12;
                int nType = Cmd.Constant.BARCODE_TYPE_CODE128 + nBarcodetype;
                int nWidthX = nBarcodeWidth + 2;
                int nHeight = (nBarcodeHeight + 1) * 24;
                int nHriFontType = nBarcodeFontType;
                int nHriFontPosition = nBarcodeFontPosition;

                if (DrawerService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putString(Global.STRPARA1, strBarcode);
                    data.putInt(Global.INTPARA1, nOrgx);
                    data.putInt(Global.INTPARA2, nType);
                    data.putInt(Global.INTPARA3, nWidthX);
                    data.putInt(Global.INTPARA4, nHeight);
                    data.putInt(Global.INTPARA5, nHriFontType);
                    data.putInt(Global.INTPARA6, nHriFontPosition);
                    DrawerService.workThread.handleCmd(Global.CMD_POS_SETBARCODE,
                            data);
                } else {
                    Toast.makeText(getBaseContext(), "Dispositivo não conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),BluetoothScanActivity.class);
                startActivity(intent);
            }
        });
        dispositivo = (TextView) findViewById(R.id.dispositivo);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null != adapter) {
            if (!adapter.isEnabled()) {
                if (adapter.enable()) {
                    Log.v("MainActivity", "Enable BluetoothAdapter");
                } else {
                    finishAffinity();
                    return;
                }
            }
        }

        dispositivosconectados();

        Intent intent = new Intent(this, DrawerService.class);
        startService(intent);

        mHandler = new MHandler(this);
        DrawerService.addHandler(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DrawerService.delHandler(mHandler);
        mHandler = null;
    }

    public void dispositivosconectados(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                dispositivo.setText(device.getName()==null?"": device.getName() + " " +device.getAddress() + "\n");
            }
        }
    }




    static class MHandler extends Handler {

        WeakReference<MainActivity> mActivity;

        MHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            switch (msg.what) {

                case Global.CMD_POS_WRITERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? "OK" : "ERRO",
                            Toast.LENGTH_SHORT).show();
                    Log.v("MainActivity", "Result: " + result);
                    break;
                }
                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? "Conexão é bem sucedida" : "Falha de conexão",
                            Toast.LENGTH_SHORT).show();
                    Log.v("MainActivity", "Connect Result: " + result);

                 //   theActivity.dialog.cancel();

                    if (result == 1)
                        theActivity.finish();
                    break;
                }

                case Global.CMD_POS_SETBARCODERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? "OK" : "ERRO",
                            Toast.LENGTH_SHORT).show();
                    Log.v("MainActivity", "Result: " + result);
                    break;
                }

                case Global.CMD_POS_FEEDLINERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? "OK" : "ERRO",
                            Toast.LENGTH_SHORT).show();
                    Log.v("MainActivity", "Result: " + result);
                    break;
                }

            }
        }
    }
}
