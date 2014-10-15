package com.okamayana.pebblechallenge.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.okamayana.pebblechallenge.R;
import com.okamayana.pebblechallenge.adapters.CommandsAdapter;
import com.okamayana.pebblechallenge.dialogs.ConnectDialog;
import com.okamayana.pebblechallenge.models.Command;
import com.okamayana.pebblechallenge.models.Command.CommandType;
import com.okamayana.pebblechallenge.net.ClientThread;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnItemClickListener {

    private static final byte COMMAND_ABSOLUTE = 0x02;
    private static final byte COMMAND_RELATIVE = 0x01;

    private static final int INITIAL_RED = 127;
    private static final int INITIAL_GREEN = 127;
    private static final int INITIAL_BLUE = 127;

    public static final String PREFS_NAME = "com.okamayana.pebblechallenge.prefs";
    public static final String PREFS_KEY_SERVER_IP = "server_ip";

    private View mCanvasView;
    private ListView mCommandsListView;
    private TextView mColorStatusTextView;

    private CommandsAdapter mAdapter;
    private List<Command> mCommandsList = new ArrayList<Command>();

    private ClientThread mClientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCanvasView = findViewById(R.id.view_canvas);
        mColorStatusTextView = (TextView) findViewById(R.id.text_view_color_status);
        resetCanvasColor();

        mCommandsListView = (ListView) findViewById(R.id.list_view_commands);
        mAdapter = new CommandsAdapter(MainActivity.this, R.layout.list_item_command, mCommandsList);
        mCommandsListView.setAdapter(mAdapter);
        mCommandsListView.setOnItemClickListener(MainActivity.this);

        DialogFragment dialog = new ConnectDialog();
        dialog.show(getFragmentManager(), ConnectDialog.TAG);
    }

    public void connect(String serverIp, int serverPort) {
        mClientThread = new ClientThread(serverIp, serverPort, mHandler);
        mClientThread.start();
        saveServer(serverIp);
    }

    private void saveServer(String serverIp) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREFS_KEY_SERVER_IP, serverIp).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mClientThread != null && !mClientThread.isRunning()) {
            Log.d("Activity", "Starting ClientThread...");
            mClientThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClientThread != null) {
            mClientThread.disconnect();
        }
    }

    private void updateAbsoluteCanvasColor(Command command) {
        int newRed = command.getRed();
        int newGreen = command.getGreen();
        int newBlue = command.getBlue();

        mCanvasView.setBackgroundColor(Color.rgb(newRed, newGreen, newBlue));
        updateColorStatusTextView(newRed, newGreen, newBlue);
    }

    private void updateRelativeCanvasColor(Command command, boolean isUndo) {
        ColorDrawable canvasBackground = (ColorDrawable) mCanvasView.getBackground();
        int oldColor = canvasBackground.getColor();

        int oldRed = Color.red(oldColor);
        int oldGreen = Color.green(oldColor);
        int oldBlue = Color.blue(oldColor);

        int newRed, newGreen, newBlue = 0;
        if (!isUndo) {
            newRed = (((oldRed + command.getRed()) % 255) + 255) % 255;
            newGreen = (((oldGreen + command.getGreen()) % 255) + 255) % 255;
            newBlue = (((oldBlue + command.getBlue()) % 255) + 255) % 255;
        } else {
            newRed = (((oldRed - command.getRed()) % 255) + 255) % 255;
            newGreen = (((oldGreen - command.getGreen()) % 255) + 255) % 255;
            newBlue = (((oldBlue - command.getBlue()) % 255) + 255) % 255;
        }

        mCanvasView.setBackgroundColor(Color.rgb(newRed, newGreen, newBlue));
        updateColorStatusTextView(newRed, newGreen, newBlue);
    }

    private void resetCanvasColor() {
        mCanvasView.setBackgroundColor(Color.rgb(INITIAL_RED, INITIAL_GREEN, INITIAL_BLUE));
        updateColorStatusTextView(INITIAL_RED, INITIAL_GREEN, INITIAL_BLUE);
    }

    private void updateColorStatusTextView(int red, int green, int blue) {
        int total = red + green + blue;
        String hex = getHexRGB(red, green, blue);
        String text = String.format("R: %d, G: %d, B: %d\nHex: %s\nTotal: %d",
                red, green, blue, hex, total);
        mColorStatusTextView.setText(text);
    }

    private Command buildAbsoluteCommand(byte[] message) {
        int red = message[1] & 0xFF;
        int green = message[2] & 0xFF;
        int blue = message[3] & 0xFF;

        return new Command(CommandType.ABSOLUTE, red, green, blue);
    }

    private Command buildRelativeCommand(byte[] message) {
        int red = concatenateBytes(message[1], message[2]);
        int green = concatenateBytes(message[3], message[4]);
        int blue = concatenateBytes(message[5], message[6]);

        return new Command(CommandType.RELATIVE, red, green, blue);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg != null) {
                byte[] message = msg.getData().getByteArray(ClientThread.KEY_MESSAGE);
                byte messageType = message[0];

                Command command = null;
                if (messageType == COMMAND_ABSOLUTE) {
                    command = buildAbsoluteCommand(message);
                    updateAbsoluteCanvasColor(command);
                } else if (messageType == COMMAND_RELATIVE) {
                    command = buildRelativeCommand(message);
                    updateRelativeCanvasColor(command, false);
                }

                if (command.getCommandType() == CommandType.ABSOLUTE) {
                    mAdapter.uncheckAllItems();
                }

                mCommandsList.add(command);
                mAdapter.setItems(mCommandsList);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_command);
        checkBox.toggle();

        boolean isChecked = checkBox.isChecked();
        mAdapter.checkItem(position, isChecked);

        Command command = mAdapter.getItem(position);
        if (command.getCommandType() == CommandType.RELATIVE) {
            if (isChecked) {
                updateRelativeCanvasColor(command, false);
            } else {
                updateRelativeCanvasColor(command, true);
            }
        } else if (command.getCommandType() == CommandType.ABSOLUTE) {
            if (isChecked) {
                updateAbsoluteCanvasColor(command);
                mAdapter.uncheckAllItemsExcept(position);
            } else {
                resetCanvasColor();
                mAdapter.uncheckAllItems();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private String getHexRGB(int red, int green, int blue) {
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    private int concatenateBytes(byte msb, byte lsb) {
        return (msb << 8) | (lsb & 0xFF);
    }
}
