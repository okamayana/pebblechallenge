package com.okamayana.pebblechallenge.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        builder.setPositiveButton(R.string.connect, null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String serverIp = mServerIpEditText.getText().toString();
                        String serverPortString = mServerPortEditText.getText().toString();

                        if (!TextUtils.isEmpty(serverIp) && !TextUtils.isEmpty(serverPortString)) {
                            int serverPort = Integer.parseInt(serverPortString);
                            activity.connect(serverIp, serverPort);
                            dismiss();
                        } else {
                            Toast.makeText(activity, "Please enter valid server IP and port number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private String getSavedServer() {
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String serverIp = prefs.getString(MainActivity.PREFS_KEY_SERVER_IP, "");
        return serverIp;
    }
}
