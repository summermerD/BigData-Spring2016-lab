package com.example.ting.notificationexample;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mAcc, mLight, mProximity, mMagnetic;
    TextView textView, Acc, Lig, Pro, Mag, LocationText;
    Button cameraClick;
    ImageView displayImage;
    LocationManager locationManager;
    String provider;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_CAMERA=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);

        textView = (TextView) findViewById(R.id.text);
        Acc = (TextView) findViewById(R.id.textView);
        Lig = (TextView) findViewById(R.id.textView3);
        Pro = (TextView) findViewById(R.id.textView5);
        Mag = (TextView) findViewById(R.id.textView7);


        Button wearButtonMag = (Button) findViewById(R.id.wearButtonMag);
        wearButtonMag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 100;
                NotificationCompat.Builder notificatonBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Magnetic information")
                        .setContentText(Mag.getText());
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId, notificatonBuilder.build());

            }
        });


        Button buttonWearLight= (Button) findViewById(R.id.buttonWearLight);
        buttonWearLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 100;
                NotificationCompat.Builder notificatonBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Light information")
                        .setContentText(Lig.getText());
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId, notificatonBuilder.build());

            }
        });

        Button buttonWearPro = (Button) findViewById(R.id.buttonWearPro);
        buttonWearPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 100;
                NotificationCompat.Builder notificatonBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Proximity information")
                        .setContentText(Pro.getText());
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId, notificatonBuilder.build());

            }
        });


        Button buttonWearAcc = (Button) findViewById(R.id.buttonWearAcc);
        buttonWearAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 100;
                NotificationCompat.Builder notificatonBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Accelerometer information")
                        .setContentText(Acc.getText());
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId, notificatonBuilder.build());

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAcc)
            Acc.setText(event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
        if (event.sensor == mLight)
            Lig.setText(event.values[0] + "");
        if (event.sensor == mMagnetic)
            Mag.setText(event.values[0] + "");
        if (event.sensor == mProximity)
            Pro.setText(event.values[0] + "");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }


}
