package com.theone.mycode;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyService extends Service {

    Handler handler;
    IBinder mBinder;
    private SessionManger session;
    public MyService() {
    }


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://52.89.16.102:8080");
        } catch (URISyntaxException e) {}
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        handler = new Handler();
        session = new SessionManger(this);
        super.onCreate();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        mSocket.connect();
        mSocket.emit("storeInfo",session.user() );
        mSocket.on("alert", onNewMessage);

        return 1;
    }

    void add(String a)
    {
        Toast.makeText(this, "Get data"+a, Toast.LENGTH_LONG).show();
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {



                    String data = (String) args[0];
                    add(data);
/*
                    String username;
                    String message;
                    try {
                        username = data.getString("firstname");
                        message = data.getString("lastname");
                        add(username);
                    } catch (JSONException e) {

                    }

*/

                }
            });
        }
    };


    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

}
