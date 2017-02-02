package vv.arduino.vaibhav.bluetoothrobotdrive;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arduino.vaibhav.bluetoothrobotdrive.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class Remote extends Activity  {

    int SpeedValue=105, flag = 1;

    Button FrontButton, BackBotton, LeftButton, RightButton, CenterButton, DecreaseButton, IncreaseButton, DetailsPageButton;
    SeekBar SpeedSignal;
    TextView SpeedValueLabel, ConnectionStatusLabel;

    //Memeber Fields
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // UUID service - This is the type of Bluetooth device that the BT module is
    // It is very likely yours will be the same, if not google UUID for your manufacturer
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module
    public String newAddress = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        //Initialising buttons & TextView in the view
        //mDetect = (Button) findViewById(R.id.mDetect);
        FrontButton = (Button) findViewById(R.id.FrontButton);
        BackBotton = (Button) findViewById(R.id.BackButton);
        LeftButton = (Button) findViewById(R.id.LeftButton);
        RightButton = (Button) findViewById(R.id.RightButton);
        CenterButton = (Button) findViewById(R.id.SwitchSpeed);
        DecreaseButton = (Button) findViewById(R.id.DecreaseButton);
        IncreaseButton = (Button) findViewById(R.id.IncreaseButton);
        SpeedValueLabel = (TextView) findViewById(R.id.SpeedValueLabel);

        //getting the bluetooth adapter value and calling checkBTstate function
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBTState();
        makeConnection();

        /**************************************************************************************************************************8
         *  Buttons are set up with onclick listeners so when pressed a method is called
         *  In this case send data is called with a value and a toast is made
         *  to give visual feedback of the selection made
         */

        FrontButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    if(sendData("F"))
                        Toast.makeText(getBaseContext(), "FRONT", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    sendData("S");
                    return true;
                }
                return false;
            }
        });

        BackBotton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    if(sendData("B"))
                        Toast.makeText(getBaseContext(), "BACK", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    sendData("S");
                    return true;
                }
                return false;
            }
        });
        LeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    if(sendData("L"))
                        Toast.makeText(getBaseContext(), "LEFT", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    sendData("S");
                    return true;
                }
                return false;
            }
        });
        RightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    if(sendData("R"))
                        Toast.makeText(getBaseContext(), "RIGHT", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP ) {
                    sendData("S");
                    return true;
                }
                return false;
            }
        });

        CenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpeedValue = 255;
                String SpeedValuePrint = "ANALOG" + SpeedValue;
                SpeedValueLabel.setText(SpeedValuePrint);
                if(sendData("H"))
                    Toast.makeText(getBaseContext(), "FULL SPEED", Toast.LENGTH_SHORT).show();
            }
        });

        DecreaseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SpeedValue>=50) {
                    SpeedValue = SpeedValue - 50;
                }
                else {
                    SpeedValue=0;
                }
                String SpeedValuePrint = "ANALOG" + SpeedValue;
                SpeedValueLabel.setText(SpeedValuePrint);
                if(sendData("D"))
                    Toast.makeText(getBaseContext(), "DECREASE SPEED BY 50", Toast.LENGTH_SHORT).show();
            }
        });

        IncreaseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SpeedValue <= 205) {
                    SpeedValue = SpeedValue + 50;
                } else {
                    SpeedValue = 255;
                }
                String SpeedValuePrint = "ANALOG" + SpeedValue;
                SpeedValueLabel.setText(SpeedValuePrint);
                if(sendData("I"))
                    Toast.makeText(getBaseContext(), "INCREASE SPEED BY 50", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            btSocket.close();
            TextView connectionStatusLabel = (TextView) findViewById(R.id.Connection);
            connectionStatusLabel.setText("Disconnected");
        }
        catch (Exception e){
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    //takes the UUID and creates a comms socket
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void makeConnection(){
        Intent intent = getIntent();
        newAddress = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);
        if (newAddress != null) {
            // Set up a pointer to the remote device using its address.
            try {
                BluetoothDevice device = btAdapter.getRemoteDevice(newAddress);
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                outStream = btSocket.getOutputStream();
                sendData("x");
            } catch (Exception e) {
                flag = 0;
                Toast.makeText(getBaseContext(), "ERROR - NOT CONNECTED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Remote.this, MainActivity.class);
                startActivity(i);
            }
            if (flag != 0) {
                Toast.makeText(getBaseContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
            }
        }
        else flag = 0;
    }

    //same as in device list activity
    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "ERROR - Device does not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // Method to send data
    private boolean sendData(String message) {
        try {
            byte[] msgBuffer = message.getBytes();

            try {
                //attempt to place data on the outstream to the BT device
                outStream.write(msgBuffer);
                return true;
            } catch (IOException e) {
                //if the sending fails this is most likely because device is no longer there
                Toast.makeText(getBaseContext(), "ERROR - Device not found", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        }
        catch(Exception e) {
            Toast.makeText(this.getBaseContext(),"NOT YET CONNECTED.. CONNECT FIRST",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void showDeviceList(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void disconnect(View view){
        try{
            btSocket.close();
            TextView connectionStatusLabel = (TextView) findViewById(R.id.Connection);
            connectionStatusLabel.setText("Disconnected");
        }
        catch(Exception e){

        }
    }

    public void ShowAppDetails(View view) {
        Intent i = new Intent(this, AppWorkingDetails.class);
        startActivity(i);
    }
}