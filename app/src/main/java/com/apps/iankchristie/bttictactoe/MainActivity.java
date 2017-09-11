package com.apps.iankchristie.bttictactoe;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Handler;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    private static final String TAG = "MainActivity";
    public static final int UPDATE_REQUEST = 1;
    public static final int BLUETOOTH_ERROR = 2;
    public static final int BLUETOOTH_VALID = 3;
    private static final int GAME_SIZE = 3;

    private TextView result;
    private TextView playingInfo;
    private String IdPrefix = "imageButton";
    public int turn;
    private final static int REQUEST_ENABLE_BT = 1;
    public TicTacToe game;
    private boolean active;


    private Handler mhandler;
    private BluetoothHelper btHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        setUpGame();
        drawBoard();
        try {
            btHelper = new BluetoothHelper(mhandler);
            btHelper.startServer(mhandler);
        } catch (BluetoothAccessException e) {
            Log.e(TAG, "BluetoothAccessException");
            showAlert(getString(R.string.NO_BLUETOOTH_CAPABILITY), getString(R.string.NO_BLUETOOTH_CAPABILITY_MESSAGE));
            active = false;
        }
    }

    private void initViews() {
        adjustButtonSizes();
        this.result = (TextView) findViewById(R.id.winner);
        this.playingInfo = (TextView) findViewById(R.id.PlayingWith);
        mhandler = new Handler(this);
    }

    public void adjustButtonSizes() {
        int frameWidth = getFrameWidth();
        Log.v(TAG, "Button Width: " + frameWidth);
        int buttonWidth = frameWidth / 3;
        Log.v(TAG, "Button Width: " + buttonWidth);
    }

    public int getFrameWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private void setUpGame() {
        active = true;
        this.turn = 1;
        this.game = new TicTacToe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void drawBoard() {
        for (int i = 0; i < this.game.board.length; i++) {
            for (int j = 0; j < this.game.board[0].length; j++) {
                String buttonID = IdPrefix + i + "" + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                ImageButton ib = (ImageButton) findViewById(resID);
                if (this.game.board[i][j] == game.getEMPTY_SPOT()) {
                    ib.setImageResource(R.drawable.blank_space);
                }
                if (this.game.board[i][j] == game.getPLAYER_1()) {
                    ib.setImageResource(R.drawable.x);
                }
                if (this.game.board[i][j] == game.getPLAYER_2()) {
                    ib.setImageResource(R.drawable.o);
                }
            }
        }
    }

    public void coordinateClick(View view) {
        if (btHelper == null) {
            return;
        }
        if (!active) {
            return;
        }
        Delta change = getChange((String) view.getTag());
        if (!game.acceptsChange(change)) {
            return;
        }
        game.updateBoard(change);
        drawBoard();
        btHelper.makeClientRequest(mhandler, change);
        active = checkGameOver(true);
    }

    public Delta getChange(String id) {
        int row = Integer.parseInt(id.substring(0, 1));
        int column = Integer.parseInt(id.substring(1, 2));
        return new Delta(row, column, turn);
    }

    public boolean checkGameOver(boolean currentDeviceTurn) {
        if (this.game.hasWinner()) {
            Log.v(TAG, "Winner Found");
            if (currentDeviceTurn) {
                this.result.setText(getString(R.string.win));
            } else {
                this.result.setText(getString(R.string.lose));
            }
            return false;
        }
        if (this.game.isCatsGame()) {
            Log.v(TAG, "Cats Game");
            this.result.setText(getString(R.string.cats_game));
            return false;
        }
        return !currentDeviceTurn;
    }

    @Override
    public boolean handleMessage(Message inputMessage) {
        Log.v(TAG, "Recieved Message");
        if (inputMessage.what == UPDATE_REQUEST) {
            Delta change = (Delta) inputMessage.obj;
            Log.v(TAG, "Update message received with\t" + change.toString());
            if (change.getEntry() == 0) {
                newGame();
            } else {
                processRequest(change);
            }
        }
        if (inputMessage.what == BLUETOOTH_ERROR) {
            if (inputMessage.arg1 == BluetoothHelper.NO_OPPONENT) {
                showAlert(getString(R.string.NO_CONNECTED_DEVICE), getString(R.string.CONNECTED_DEVICES_MESSAGE));
            }
            if (inputMessage.arg1 == BluetoothHelper.MULTIPLE_OPPONENTS) {
                showAlert(getString(R.string.MULTIPLE_CONNECTED_DEVICE), getString(R.string.CONNECTED_DEVICES_MESSAGE));
            }
            if (inputMessage.arg1 == BluetoothHelper.BLUETOOTH_NOT_ENABLED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            active = false;
        }
        if (inputMessage.what == BLUETOOTH_VALID) {
            String player = (String) inputMessage.obj;
            Log.v(TAG, "*********" + player);
            playingInfo.setText(player);
        }
        return true;
    }

    public void showAlert(String title, String message) {
        Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(getString(R.string.ok), null);
        alert.show();
    }

    public void processRequest(Delta change) {
        turn = game.updateBoard(change);
        drawBoard();
        active = checkGameOver(false);
    }

    public void newGameClick(View view) {
        Log.v(TAG, "New Game Clicked");
        if (btHelper == null) {
            Log.v(TAG, "No btHelper though.");
            return;
        }
        newGame();
        Delta newGameRequest = new Delta(0, 0, 0); //arbitrary message to signal as new game request
        btHelper.makeClientRequest(mhandler, newGameRequest);
    }

    public void newGame() {
        Log.v(TAG, "New Game method");
        setUpGame();
        drawBoard();
        result.setText("");
    }
}
