package com.example.socketclient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends Activity {

    TextView info, infoip, msg, msg2,info2;
    String message = "", imgString = "", message2 = "";
    ServerSocket serverSocket, serverSocket2;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    boolean checkUpdate = true;
    ImageView imageView;
    byte[] img = new byte[4*1024]; // TODO change to get image byte array from device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (TextView) findViewById(R.id.info);
        info2 = (TextView) findViewById(R.id.info2);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        msg2 = (TextView) findViewById(R.id.textView);


        infoip.setText(getIpAddress());


        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(getString(R.string.app_name), "CHANGE IMAGE");
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });


        final Button image_classify_button = (Button) findViewById(R.id.send);
        image_classify_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                img = baos.toByteArray();
            }
        });

        Thread socketServerThread = new Thread(new SocketServerThread());
        Thread socketServerThread2 = new Thread(new SocketServerThread2());

        socketServerThread.start();
        socketServerThread2.start();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (serverSocket2 != null) {
            try {
                serverSocket2.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 9999;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here for sending: "
                                + serverSocket.getLocalPort());
                    }
                });

                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;


                    imgString = Base64.encodeToString(img, Base64.DEFAULT);


                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {

            if (checkUpdate) {

                OutputStream outputStream;


                try {
                    outputStream = hostThreadSocket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    printStream.print(imgString);
                    printStream.close();

                    imgString = "";
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    message += "Something wrong! " + e.toString() + "\n";
                }

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });
            }
        }

    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK){
            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            try
            {
                Bitmap imgBitmap = getScaledBitmap(picturePath, 400, 400);

                // Images from the filesystem might be rotated...
                ExifInterface exif = new ExifInterface(picturePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                Log.d(getString(R.string.app_name), "Orientation: "+orientation);

                switch (orientation) {
                    case 3:
                    {
                        Matrix matrix = new Matrix();

                        matrix.postRotate(90);
                        imgBitmap = Bitmap.createBitmap(
                                imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
                    }
                    break;
                    case 6:
                    {
                        Matrix matrix = new Matrix();

                        matrix.postRotate(90);
                        imgBitmap = Bitmap.createBitmap(
                                imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), matrix, true);
                    }
                    break;

                }

                imageView.setImageBitmap(imgBitmap);
            }
            catch (Exception e)
            {
                msg.setText("Error loading image: " + e.getMessage());
            }
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private class SocketServerThread2 extends Thread {

        static final int SocketServerPORT2 = 1234;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket2 = new ServerSocket(SocketServerPORT2);
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info2.setText("I'm waiting here for receiving: "
                                + serverSocket2.getLocalPort());
                    }
                });

                while (true) {
                    Socket socket = serverSocket2.accept();
                    count++;


                    message2 += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";


                    int red = -1;
                    byte[] buffer = new byte[5*1024]; // a read buffer of 5KiB
                    byte[] redData;
                    StringBuilder clientData = new StringBuilder();
                    String redDataText;
                    while ((red = socket.getInputStream().read(buffer)) > -1) {
                        redData = new byte[red];
                        System.arraycopy(buffer, 0, redData, 0, red);
                        redDataText = new String(redData,"UTF-8"); // assumption that client sends data UTF-8 encoded
                        System.out.println("message part recieved:" + redDataText);
                        clientData.append(redDataText);
                    }
                    // System.out.println("Data From Client :" + clientData.toString());

                    message2+=clientData;

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg2.setText(message);
                        }
                    });

                    SocketServerReplyThread2 socketServerReplyThread2 = new SocketServerReplyThread2(
                            socket, count);
                    socketServerReplyThread2.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread2 extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread2(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {

            if (checkUpdate) {

                OutputStream outputStream;
                String msgReply = "Hello from Android, you are #" + cnt;

                try {
                    outputStream = hostThreadSocket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    printStream.print(msgReply);
                    printStream.close();

                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg2.setText(message2);
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    message2 += "Something wrong! " + e.toString() + "\n";
                }

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg2.setText(message2);
                    }
                });
            }
        }

    }

}