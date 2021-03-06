package com.example.krid.scanble;
/*test merge*/
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

public class MyActivity extends ActionBarActivity {
    private int count;
    private String filename;
    private RadioGroup radioGroup;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device,final int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device.getName().equals("HMSoft")){
                                TextView text = (TextView) findViewById(R.id.text);
                                text.setText("Scanning\n" + device.getName() + " rssi = " + rssi + z"\n");
                                /*TextView range = (TextView) findViewById(R.id.editText);*/
                                write(filename, "\n" + device.getName() + ":" + rssi +
                                        " count=" + count);
                                count++;
                                if (count == 101) {
                                    Button btn = (Button) findViewById(R.id.stop);
                                    btn.callOnClick();
                                }
                            }
                        }
                    });
                }
            };
    public void start(View view) {
        Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();
        RadioButton range=(RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
        filename="BLE "+range.getText().toString();
        write(filename,"Log:");
        count=1;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    public void stop (View view){
        Toast.makeText(this,"STOP",Toast.LENGTH_SHORT).show();
        TextView text=(TextView)findViewById(R.id.text);
        text.setText("Stop");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        view.playSoundEffect(0);

    }
    public void write(String fname,String content){
        String path="/sdcard/"+fname+".txt";
        File file=new File(path);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean new_file=true;
        if (content.equals("Log:"))
            new_file=false;
        try {
            FileWriter fw= new FileWriter(file.getAbsoluteFile(),new_file);
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        radioGroup=(RadioGroup)findViewById(R.id.distance_group);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE NOT SUPPORT", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
