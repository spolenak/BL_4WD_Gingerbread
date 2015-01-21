package com.cxem_car;

import com.cxem_car.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ActivityButtons extends Activity {
	
	private cBluetooth bl = null;
	private ToggleButton LightButton;
	
	private Button btn_forward, btn_backward, btn_left, btn_right;
	
    private int motorLeft = 0;
    private int motorRight = 0;
    private String address;
    private int pwmBtnMotorLeft;
    private int pwmBtnMotorRight;
    private String commandLeft;		// символ команды левого двигателя
    private String commandRight;	// символ команды правого двигателя
    private String commandHorn;		// символ команды для доп. канала (звуковой сигнал)
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buttons);
		
		address = (String) getResources().getText(R.string.default_MAC);
		pwmBtnMotorLeft = Integer.parseInt((String) getResources().getText(R.string.default_pwmBtnMotorLeft));
		pwmBtnMotorRight = Integer.parseInt((String) getResources().getText(R.string.default_pwmBtnMotorRight));
        commandLeft = (String) getResources().getText(R.string.default_commandLeft);
        commandRight = (String) getResources().getText(R.string.default_commandRight);
        commandHorn = (String) getResources().getText(R.string.default_commandHorn);
		
		loadPref();
		
	    bl = new cBluetooth(this, mHandler);
	    bl.checkBTState();
		
		btn_forward = (Button) findViewById(R.id.forward);
		btn_backward = (Button) findViewById(R.id.backward);
		btn_left = (Button) findViewById(R.id.left);
		btn_right = (Button) findViewById(R.id.right);
		       
		btn_forward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_MOVE) {
		        	motorLeft = pwmBtnMotorLeft;
		        	motorRight = pwmBtnMotorRight;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	motorLeft = 0;
		        	motorRight = 0;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        }
				return false;
		    }
		});
		
		btn_left.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_MOVE) {
		        	motorLeft = -pwmBtnMotorLeft;
		        	motorRight = pwmBtnMotorRight;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	motorLeft = 0;
		        	motorRight = 0;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        }
				return false;
		    }
		});
		
		btn_right.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_MOVE) {
		        	motorLeft = pwmBtnMotorLeft;
		        	motorRight = -pwmBtnMotorRight;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	motorLeft = 0;
		        	motorRight = 0;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        }
				return false;
		    }
		});
		
		btn_backward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_MOVE) {
		        	motorLeft = -pwmBtnMotorLeft;
		        	motorRight = -pwmBtnMotorRight;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	motorLeft = 0;
		        	motorRight = 0;
		        	bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
		        }
				return false;
		    }
		});
		
		LightButton = (ToggleButton) findViewById(R.id.LightButton);   
		LightButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(LightButton.isChecked()){
	    			bl.sendData(String.valueOf(commandHorn+"1\r"));
	    		}else{
	    			bl.sendData(String.valueOf(commandHorn+"0\r"));
	    		}
	    	}
	    });
		
	}
		
    private final Handler mHandler =  new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
            case cBluetooth.BL_NOT_AVAILABLE:
               	Log.d(cBluetooth.TAG, "Bluetooth is not available. Exit");
            	Toast.makeText(getBaseContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case cBluetooth.BL_INCORRECT_ADDRESS:
            	Log.d(cBluetooth.TAG, "Incorrect MAC address");
            	Toast.makeText(getBaseContext(), "Incorrect Bluetooth address", Toast.LENGTH_SHORT).show();
                break;
            case cBluetooth.BL_REQUEST_ENABLE:   
            	Log.d(cBluetooth.TAG, "Request Bluetooth Enable");
            	BluetoothAdapter.getDefaultAdapter();
            	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
                break;
            case cBluetooth.BL_SOCKET_FAILED:
            	Toast.makeText(getBaseContext(), "Socket failed", Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
        };
    };
	
    private void loadPref(){
    	SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);  
    	address = mySharedPreferences.getString("pref_MAC_address", address);			// Первый раз загружаем дефолтное значение
    	pwmBtnMotorLeft = Integer.parseInt(mySharedPreferences.getString("pref_pwmBtnMotorLeft", String.valueOf(pwmBtnMotorLeft)));
    	pwmBtnMotorRight = Integer.parseInt(mySharedPreferences.getString("pref_pwmBtnMotorRight", String.valueOf(pwmBtnMotorRight)));
    	commandLeft = mySharedPreferences.getString("pref_commandLeft", commandLeft);
    	commandRight = mySharedPreferences.getString("pref_commandRight", commandRight);
    	commandHorn = mySharedPreferences.getString("pref_commandHorn", commandHorn);
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	bl.BT_Connect(address);
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	bl.BT_onPause();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	loadPref();
    }
}
