package com.apps.iankchristie.bttictactoe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import android.util.Log;
import java.io.IOException;
import android.os.Handler;


/**
 * Created by iankc on 11/7/16.
 */

public class Server extends Thread {
    private static final String TAG = "SERVER";
    private final BluetoothServerSocket mmServerSocket;
    private final String SERVERNAME = "Bluetooth Server";
    private final Handler mhandler;

    public Server(BluetoothAdapter mBluetoothAdapter, Handler _mhandler) {
        Log.v(TAG, "Server Constructor Called");
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVERNAME,
                    BluetoothHelper.APP_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
        mhandler = _mhandler;
    }

    public void run() {
        Log.v(TAG, "Server Starting Up");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                Log.v(TAG, "Searchnig for Socket");
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            Log.v(TAG, "Found socket");
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                Log.v(TAG, "Found Valid socket");
                ConnectedThread ct = new ConnectedThread(socket, mhandler);
                ct.start();

                try {
                    mmServerSocket.close();
                }
                catch(IOException e) {
                    Log.v(TAG, "Cannot Close Server?");
                }
                finally {
                    break;
                }
            }
        }
        Log.v(TAG, "Server Shutting Down");
        Server newServer = new Server(BluetoothAdapter.getDefaultAdapter(), mhandler);
        newServer.start();
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}
