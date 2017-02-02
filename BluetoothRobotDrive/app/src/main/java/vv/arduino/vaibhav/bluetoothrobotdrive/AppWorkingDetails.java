package vv.arduino.vaibhav.bluetoothrobotdrive;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.arduino.vaibhav.bluetoothrobotdrive.R;


public class AppWorkingDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_working_details);
    }

    public void SwitchPage(View view){
        Intent i = new Intent(this, Remote.class);
        startActivity(i);
    }

}
