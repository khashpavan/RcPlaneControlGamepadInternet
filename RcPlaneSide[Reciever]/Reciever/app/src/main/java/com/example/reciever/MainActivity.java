package com.example.reciever;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity {
        public final String ACTION_USB_PERMISSION = "com.example.reciever.USB_PERMISSION";
        FirebaseDatabase database;
        DatabaseReference myRef;
        Button startButton, sendButton, clearButton, stopButton;
        TextView textView;
        EditText editText;
        UsbManager usbManager;
        UsbDevice device;
        UsbSerialDevice serialPort;
        UsbDeviceConnection connection;
        long a=0,b=0,c=0,d=0;
        boolean keep = true;

        UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
            @Override
            public void onReceivedData(byte[] arg0) {
                String data = null;
                try {
                    data = new String(arg0, "UTF-8");
                    data.concat("/n");
                    tvAppend(textView, data);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        };
        private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                setUiEnabled(true);
                                serialPort.setBaudRate(9600);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);
                                tvAppend(textView,"Serial Connection Opened!\n");

                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    onClickStart(startButton);
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    onClickStop(stopButton);

                }
            }

            ;
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
            startButton = (Button) findViewById(R.id.buttonStart);
            sendButton = (Button) findViewById(R.id.buttonSend);
            clearButton = (Button) findViewById(R.id.buttonClear);
            stopButton = (Button) findViewById(R.id.buttonStop);
            editText = (EditText) findViewById(R.id.editText);
            textView = (TextView) findViewById(R.id.textView);
            setUiEnabled(false);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(broadcastReceiver, filter);

            database =FirebaseDatabase.getInstance();
            myRef= database.getReference("values");
            // Read from the database

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry mapElement : map.entrySet()) {
                        String key = (String)mapElement.getKey();
                        switch(key) {
                            case "a":
                                a = (long) mapElement.getValue();
                                break;
                            case "b":
                                b = (long) mapElement.getValue();
                                break;
                            case "c":
                                c = (long) mapElement.getValue();
                                break;
                            case "d":
                                d = (long) mapElement.getValue();
                                break;

                        }


                    }
                    tvAppend(textView, "\nData got : "+a+" "+b+" "+c+" "+d+ "\n");

                    if(!keep){
                        String strin=""+a+" "+b+" "+c+" "+d;
                        serialPort.write(strin.getBytes());
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.i("TAG", "Failed to read value.", error.toException());
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setUiEnabled(boolean bool) {
            startButton.setEnabled(!bool);
            sendButton.setEnabled(bool);
            stopButton.setEnabled(bool);
            textView.setEnabled(bool);

        }

        public void onClickStart(View view) {

            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
            if (!usbDevices.isEmpty()) {
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    device = entry.getValue();
                    int deviceVID = device.getVendorId();
                    Toast.makeText(this, "devicevid"+deviceVID, Toast.LENGTH_SHORT).show();
                    if (deviceVID == 0x1A86)//Arduino Vendor ID
                    {
                        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        usbManager.requestPermission(device, pi);
                        keep = false;
                    } else {
                        connection = null;
                        device = null;
                    }

                    if (!keep)
                        break;
                }
            }


        }




        public void onClickSend(View view) {
            String string = editText.getText().toString();
            serialPort.write(string.getBytes());

            tvAppend(textView, "\nData Sent : " + string+" "+"\n");

        }

        public void onClickStop(View view) {
            setUiEnabled(false);
            serialPort.close();
            tvAppend(textView,"\nSerial Connection Closed! \n");

        }

        public void onClickClear(View view) {
            textView.setText(" ");
        }

        private void tvAppend(TextView tv, CharSequence text) {
            final TextView ftv = tv;
            final CharSequence ftext = text;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ftv.append(ftext);
                }
            });
        }

    }
