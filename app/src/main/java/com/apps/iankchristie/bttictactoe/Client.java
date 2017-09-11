package com.apps.iankchristie.bttictactoe;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.IOException;

/**
 * Created by iankc on 11/7/16.
 */

public class Client extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Handler mhandler;
    private Delta change;

    public Client(BluetoothDevice device, Handler _mhandler, Delta _change) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        mhandler = _mhandler;
        change = _change;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mmDevice.createRfcommSocketToServiceRecord(BluetoothHelper.APP_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }


    public void run() {

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)

        ConnectedThread ct = new ConnectedThread(mmSocket, mhandler);
        ct.write(change.toBytes());
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}