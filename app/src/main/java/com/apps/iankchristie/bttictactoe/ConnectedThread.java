package com.apps.iankchristie.bttictactoe;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by iankc on 11/7/16.
 */

public class ConnectedThread extends Thread {
    private static final String TAG = "Manager";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mhandler;

    public ConnectedThread(BluetoothSocket socket, Handler _handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        mhandler = _handler;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                Log.v(TAG, String.valueOf(bytes));
                int deltaRow = (int) buffer[0];
                int deltaColumn = (int) buffer[1];
                int deltaEntry = (int) buffer[2];
                Log.v(TAG, String.valueOf(deltaRow));
                Log.v(TAG, String.valueOf(deltaColumn));
                Log.v(TAG, String.valueOf(deltaEntry));
                Delta change = new Delta(deltaRow, deltaColumn, deltaEntry);
                // Send the obtained bytes to the UI activity
                mhandler.obtainMessage(MainActivity.UPDATE_REQUEST, change).sendToTarget();
                cancel();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
