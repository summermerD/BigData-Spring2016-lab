package com.example.ting.heartrate;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    SensorManager sensorManager;
    Sensor mStepCounter, mHeartRate;
    TextView heart, step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);//initialization

        mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mHeartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        sensorManager.registerListener(this, mStepCounter, sensorManager.SENSOR_DELAY_NORMAL);//Registration
        sensorManager.registerListener(this, mHeartRate, sensorManager.SENSOR_DELAY_NORMAL);

        heart = (TextView)findViewById(R.id.heart);
        step = (TextView)findViewById(R.id.step);


//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });

        Button wearButton = (Button)findViewById(R.id.wearButton);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 100;
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setContentTitle("Heart Rate:")
                        .setContentText(heart.getText());

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId,notificationBuilder.build());


            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getName() == mStepCounter.getName())
            step.setText(""+event.values[0]);
        if(event.sensor.getName() == mHeartRate.getName())
            heart.setText(""+event.values[0]);
        Log.d("Sensor", event.sensor + "" + event.values[0]);
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy){

    }

    @Override
    protected void onResume(){
        //Register a listener for the sensor
        super.onResume();
        sensorManager.registerListener(this, mStepCounter, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mHeartRate, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        //Be sure to unregister the sensor when the activity pauses
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
