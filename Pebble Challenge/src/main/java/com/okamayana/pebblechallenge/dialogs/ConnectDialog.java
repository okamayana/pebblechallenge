package com.okamayana.pebblechallenge.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.okamayana.pebblechallenge.R;
import com.okamayana.pebblechallenge.activities.MainActivity;

public class ConnectDialog extends DialogFragment {

    public static final String TAG = "connect_dialog";

    private EditText mServerIpEditText;
    private EditText mServerPortEditText;

    public ConnectDialog() {
        // Empty constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity) getActivity();

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_connect, null);
        mServerIpEditText = (EditText) view.findViewById(R.id.edit_text_server_ip);
        mServerPortEditText = (EditText) view.findViewById(R.id.edit_text_server_port);
        mServerIpEditText.requestFocus();

        String serverIp = getSavedServer();
        if (!TextUtils.isEmpty(serverIp)) {
            mServerIpEditText.setText(serverIp);
            mServerIpEditText.selectAll();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setTitle(R.string.title_dialog_connect);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String serverIp = mServerIpEditText.getText().toString();
                int serverPort = Integer.parseInt(mServerPortEditText.getText().toString());
                activity.connect(serverIp, serverPort);
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

    private String getSavedServer() {
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String serverIp = prefs.getString(MainActivity.PREFS_KEY_SERVER_IP, "");
        return serverIp;
    }
}
