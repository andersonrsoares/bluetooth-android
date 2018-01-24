package com.prtsdkapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.prtsdkapp.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class DeviceListActivity extends Activity {

	public static final String TAG = "DeviceListActivity";
    public static final boolean D = true;
    // 返回  Intent的extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    // 成员字段
    public BluetoothAdapter mBtAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
	private OutputStream mmOutStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    public List<String> pairedDeviceList=null;
    public List<String> newDeviceList=null;
    public ArrayAdapter<String> mPairedDevicesArrayAdapter;
    public ArrayAdapter<String> mNewDevicesArrayAdapter;
    public static String toothAddress=null;
    public static String toothName=null;
    private Context thisCon=null;
    private String strAddressList="";
    
    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启用窗口拓展功能，方便调用
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.devicelistactivity); 
        setResult(Activity.RESULT_CANCELED);        
        // 初始化按钮来执行设备发现
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
           public void onClick(View v) {
        	   strAddressList="";
                doDiscovery();
                v.setVisibility(View.GONE);
        }
        });
        
        thisCon=this.getApplicationContext();
        
        // 初始化 arryadapter 已经配对的设备和新扫描到得设备
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getPairedData());
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST"; 
        IntentFilter intent = new IntentFilter(); 
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果 
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); 
        intent.addAction(ACTION_PAIRING_REQUEST);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); 
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); 
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intent); 
        try
        {
	        pairedListView.setOnItemClickListener(mDeviceClickListener);
	        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        }catch(Exception excpt)
        {
  	       Toast.makeText(this, thisCon.getString(R.string.get_device_err)+excpt,Toast.LENGTH_LONG).show();
        }
     }
     //取得已经配对的蓝牙信息,用来加载到ListView中去
     public List<String> getPairedData(){
        List<String> data = new ArrayList<String>();
        //默认的蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // 得到当前的一个已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		if (pairedDevices.size() > 0) 
		{
		    findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);        
		    for (BluetoothDevice device : pairedDevices) //遍历
		    {
		       data.add(device.getName() + "\n" + device.getAddress());
		    }
		} 
		else
		  {
		      String noDevices = getResources().getText(R.string.none_paired).toString();
		      data.add(noDevices);       
		  }
        return data;
    }
    
     @Override
	  protected void onDestroy() {
	        super.onDestroy();
	        // 确认是否还需要做扫描
	        if (mBtAdapter != null)
	        {
	           mBtAdapter.cancelDiscovery();
	        }
	    }

    /**
     * 启动装置发现的BluetoothAdapter
     */
    public void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");
        // 在标题中注明扫描
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        // 打开子标题的新设备
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // 若启动了扫描，关闭扫描
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //扫描        
        int intStartCount=0;
        while (!mBtAdapter.startDiscovery() && intStartCount<5)
        {
            Log.e("BlueTooth", "扫描尝试失败");
            intStartCount++;
            try 
            {
                Thread.sleep(100);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }

    // 给列表的中的蓝牙设备创建监听事件
    public OnItemClickListener mDeviceClickListener = new OnItemClickListener() 
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) 
        {
        	boolean hasConnected=false;
        	try 
        	{ 
	        	if(mBtAdapter.isDiscovering())
	        	{
	        		mBtAdapter.cancelDiscovery();    
	        	}
	        	
	            //取得蓝牙mvc地址
	            String info = ((TextView) v).getText().toString();
	            toothAddress = info.substring(info.length() - 17);
	            if(!toothAddress.contains(":"))
	            { 
	            	return;
	            }
	            
	            hasConnected= ConnectDevice();
	            if (hasConnected)
	            {	            	
	            	DisConnect();
	            }
	            
	            Intent intent = new Intent();                
	            intent.putExtra("is_connected", (hasConnected)?"OK":"NO"); 
	            intent.putExtra("BTAddress", toothAddress); 
	            setResult(10, intent);  
	            finish();                              	            
            }
            catch (Exception e)
            {  
            	 e.printStackTrace();
            } 
        	finally
        	{ 
        		finish();
        	}          	
        }
    };
    // 扫描完成时候，改变按钮text
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override 
        public void onReceive(Context context, Intent intent) {
        	
            String action = intent.getAction(); 
            BluetoothDevice device = null; 
            // 搜索设备时，取得设备的MAC地址 
            if (BluetoothDevice.ACTION_FOUND.equals(action)) { 
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
                if (device.getBondState() == BluetoothDevice.BOND_NONE) 
                { 
                	if(device.getBluetoothClass().getMajorDeviceClass()==1536)
                	{
                		if(!strAddressList.contains(device.getAddress()))
                		{
	                		strAddressList+=device.getAddress()+",";
	                		mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                		}
                	}
                } 
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){ 
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
                switch (device.getBondState()) { 
                case BluetoothDevice.BOND_BONDING: 
                    Log.d("BlueToothTestActivity", "正在配对......"); 
                    break; 
                case BluetoothDevice.BOND_BONDED: 
                    Log.d("BlueToothTestActivity", "完成配对");                     
                    break; 
                case BluetoothDevice.BOND_NONE: 
                    Log.d("BlueToothTestActivity", "取消配对"); 
                default: 
                    break; 
                } 
            } 
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            { 
	            setProgressBarIndeterminateVisibility(false);
	            setTitle(R.string.select_device);
	            if (mNewDevicesArrayAdapter.getCount() == 0) { }
            }           
        }
    };

    private boolean ConnectDevice()
	{
    	boolean bRet=false;
		boolean isOldVersion=false;
						
		if (Build.VERSION.SDK_INT < 15) 
		{
			isOldVersion=true;			
		}
		
		try 
		{
			Thread.sleep(500);
		} 
		catch (InterruptedException e) 
		{			
			e.printStackTrace();
		}
		
		try 
		{			
			// 4.0.3版本 经测试，高版本兼容低版本
			mmDevice = mBtAdapter.getRemoteDevice(toothAddress);			
			if(isOldVersion)
			{
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
			}
			else
			{
				mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			}
			
			/*try {  
	            // 连接建立之前的先配对  
	            if (mmDevice.getBondState() == BluetoothDevice.BOND_NONE) {  
	                Method creMethod = BluetoothDevice.class  
	                        .getMethod("createBond");  
	                Log.e("TAG", "开始配对");  
	                creMethod.invoke(mmDevice);  
	            } else {  
	            }  
	        } catch (Exception e) {  
	            // TODO: handle exception  
	            //DisplayMessage("无法配对！");  
	            e.printStackTrace();  
	        }  */
			
			mBtAdapter.cancelDiscovery();
			Thread.sleep(200);
			if(mBtAdapter.isDiscovering())
        	{
				int iCnt=0;
				while (iCnt<5)
	    		{	  
					Thread.sleep(500);
					iCnt++;
					if(mBtAdapter.cancelDiscovery())
					{					
						break;
					}
	    		}
        	}
			mmSocket.connect(); 
        	bRet=GetIOInterface();    
        	return bRet;
		} 
		catch (Exception e) 
		{			
			try
			{
				Method m;
				m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
				mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
				if(mBtAdapter.isDiscovering())
	        	{
					int iCnt=0;
					while (iCnt<5)
		    		{	  
						Thread.sleep(500);
						iCnt++;
						if(mBtAdapter.cancelDiscovery())
						{					
							break;
						}
		    		}
	        	}
				
				mmSocket.connect(); 
	        	bRet=GetIOInterface();    
	        	return bRet;
			}
			catch (Exception e1) 
			{
				if (mmDevice.getBondState() == BluetoothDevice.BOND_NONE) 
				{
					Toast.makeText(thisCon, R.string.BondError, Toast.LENGTH_LONG).show();
				}
				Log.d("PRTLIB", (new StringBuilder("BTO_ConnectDevice --> create ")).append(e1.getMessage()).toString());			
				return false;	
			}
		}
	}    
    
    private boolean DisConnect()
	{
		boolean bRet = true;
		try 
		{
			Thread.sleep(500);
		} 
		catch (InterruptedException e) 
		{			
			e.printStackTrace();
		}
		try 
		{			
			if(mmInStream!=null)
			{
				mmInStream.close();
				mmInStream=null;
			}
			if(mmOutStream!=null)
			{
				mmOutStream.close();
				mmOutStream=null;
			}	
			mmSocket.close();
			mmSocket=null;
			mmDevice=null;
		} 
		catch (IOException e) 
		{
			System.out.println((new StringBuilder(
					"BTO_ConnectDevice close ")).append(e.getMessage())
					.toString());
			bRet = false;
		}
		return bRet;
	}
    
    private boolean GetIOInterface()
    {
        Log.d("PRTLIB", "BTO_GetIOInterface...");
        try
        {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        }
        catch(IOException e)
        {
            Log.d("PRTLIB", (new StringBuilder("BTO_GetIOInterface ")).append(e.getMessage()).toString());
            return false;
        }
        return true;
    }  
}


