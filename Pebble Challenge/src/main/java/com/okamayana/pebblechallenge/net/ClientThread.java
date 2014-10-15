package com.okamayana.pebblechallenge.net;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ClientThread extends Thread {

    public static final int TIMEOUT = 5000; // 5 sec timeout
    public static final String KEY_MESSAGE = "message";

    private Activity mActivity;

    private Handler mHandler;
    private Socket mSocket;
    private InetSocketAddress mServerAddress;

    private boolean mIsRunning = false;

    public ClientThread(String serverIp, int serverPort, Handler handler, Activity activity) {
        mActivity = activity;
        mServerAddress = new InetSocketAddress(serverIp, serverPort);
        mHandler = handler;
        mSocket = new Socket();
    }

    @Override
    public void run() {
        super.run();
        mIsRunning = true;

        try {
            if (!mSocket.isConnected()) {
                mSocket.connect(mServerAddress);
            }

            InputStream socketStream = mSocket.getInputStream();

            byte[] data = new byte[7];
            while (mIsRunning) {
                socketStream.read(data);

                if (data[0] == 0x01 || data[0] == 0x02) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();

                    bundle.putByteArray(KEY_MESSAGE, data);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }

            socketStream.close();
            mSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            showErrorToast(e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (mIsRunning) {
            mIsRunning = false;
        }

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showErrorToast(final ConnectException e) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isRunning() {
        return mIsRunning;
    }
}
