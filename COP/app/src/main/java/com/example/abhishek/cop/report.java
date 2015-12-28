package com.example.abhishek.cop;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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

public class report extends Activity {


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://52.89.16.102:8080");
        } catch (URISyntaxException e) {}
    }

    private ProgressDialog pDialog;
    private SessionManager session;
    EditText title,desc,location;
    String t,l,d,n;
    boolean val;
    Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        session = new SessionManager(report.this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(report.this);

        tb=(Toolbar)findViewById(R.id.toolbar2);
        tb.setTitle("Update FIR");
        tb.setLogo(R.drawable.up);
        title=(EditText)findViewById(R.id.editText);
        desc=(EditText)findViewById(R.id.editText2);
        location=(EditText)findViewById(R.id.editText3);
        Button reg=(Button)findViewById(R.id.button);
        final CheckBox cb = (CheckBox)findViewById(R.id.checkBox);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t = title.getText().toString().trim();
                d = desc.getText().toString().trim();
                l = location.getText().toString().trim();
                val = cb.isChecked();
                if(val)
                    n="Yes";
                else
                    n="No";
                if(!t.isEmpty()&& !d.isEmpty()&&!l.isEmpty())
                {
                    new DownloadTask().execute("rohit");
                    pDialog.setMessage("Report registering ...");
                    showDialog();
                }
                else
                    Toast.makeText(report.this, "Pls Fill all details...!!", Toast.LENGTH_LONG).show();
            }
        });
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

            Toast.makeText(report.this, "FIR Updated successfully", Toast.LENGTH_LONG).show();
            Intent startIntent = new Intent("HomeScreen");
            startActivity(startIntent);
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("user_id", session.user()));
        nameValuePairs.add(new BasicNameValuePair("title", t));
        nameValuePairs.add(new BasicNameValuePair("location", l));
        nameValuePairs.add(new BasicNameValuePair("desc", d));
        nameValuePairs.add(new BasicNameValuePair("value",n));
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
