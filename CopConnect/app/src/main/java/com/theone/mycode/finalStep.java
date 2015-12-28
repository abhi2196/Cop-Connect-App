package com.theone.mycode;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Abhishek on 02-12-2015.
 */
public class finalStep extends Activity {


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://52.89.16.102:8080");
        } catch (URISyntaxException e) {}
    }

    private ProgressDialog pDialog;
    private SessionManger session;
    String image,longi,lati;
    EditText title,desc,location;
    String t,l,d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finals);
        session = new SessionManger(finalStep.this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManger(finalStep.this);


        title=(EditText)findViewById(R.id.editText);
        desc=(EditText)findViewById(R.id.editText2);
        location=(EditText)findViewById(R.id.editText3);
        Button reg=(Button)findViewById(R.id.button);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               t = title.getText().toString().trim();
                 d = desc.getText().toString().trim();
                 l = location.getText().toString().trim();

                if(!t.isEmpty()&& !d.isEmpty()&&!l.isEmpty())
                {
                    new DownloadTask().execute("rohit");
                    pDialog.setMessage("Report registering ...");
                    showDialog();
                }
                else
                    Toast.makeText(finalStep.this, "Pls Fill all details...!!", Toast.LENGTH_LONG).show();
            }
        });
        Intent intent = getIntent();

         longi=intent.getStringExtra("long");
         lati=intent.getStringExtra("lati");
        image=intent.getStringExtra("image");
        //Toast.makeText(finalStep.this, "rohit "+image, Toast.LENGTH_LONG).show();
        //Log.d("rohit chahar",image);
        //String rohit="http://code.theonecoders.in/android/upload.php?lati="+lati+"&long="+longi+"&image="+image;


    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
           // MultipartEntity reqEntity = new MultipartEntity();
            try {

               // reqEntity.addPart("picture", "contentPart");
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            hideDialog();
            mSocket.connect();
            mSocket.emit("report_fir","new_fir_registered");

            Toast.makeText(finalStep.this, "FIR Register successfully", Toast.LENGTH_LONG).show();
            Intent startIntent = new Intent("home");


            startActivity(startIntent);




        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("rohit", image));
        nameValuePairs.add(new BasicNameValuePair("user_id", session.user()));
        nameValuePairs.add(new BasicNameValuePair("longi", longi));
        nameValuePairs.add(new BasicNameValuePair("lati", lati));
        nameValuePairs.add(new BasicNameValuePair("title", t));
        nameValuePairs.add(new BasicNameValuePair("location", l));
        nameValuePairs.add(new BasicNameValuePair("desc", d));

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://code.theonecoders.in/android/insert.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String st = EntityUtils.toString(response.getEntity());
            Log.v("log_tag", "In the try Loop" + st);

        } catch (Exception e) {
            Log.v("log_tag", "Error in http connection " + e.toString());
        }
        return "Success";

/*
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /);
            conn.setConnectTimeout(15000 );
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Respone", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, length);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    */
    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
