package com.example.abhishek.cop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.util.ArrayList;


public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword,phone,voter;
    private ProgressDialog pDialog;
    String name,email,password,phones,voters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        voter = (EditText) findViewById(R.id.voterId);
        phone = (EditText) findViewById(R.id.mobile);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);




        if(isConnectingToInternet()) {
            // Register Button Click event
            btnRegister.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    name = inputFullName.getText().toString().trim();
                    email = inputEmail.getText().toString().trim();
                    password = inputPassword.getText().toString().trim();
                    phones = phone.getText().toString().trim();
                    voters = voter.getText().toString().trim();

                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !voters.isEmpty() && !phones.isEmpty() &&isValidEmail(email)) {
                        pDialog.setMessage("Registering ...");
                        showDialog();

                        new DownloadTask().execute();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your details Correctly!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }
        else
            showAlertDialog(this,"Internet Connection","You Dont have internet connection");

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent startIntent = new Intent(RegisterActivity.this, login_activity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        });

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            // MultipartEntity reqEntity = new MultipartEntity();

            // reqEntity.addPart("picture", "contentPart");
            if(registerUser())
                return "success";
            else
                return "fail";

        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task

            if(result=="success")
            {
                showAlertDialog(RegisterActivity.this, "Registered Successfully", "Please login to continoue");
                Intent startIntent = new Intent(RegisterActivity.this, login_activity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);

                finish();
            }
            else
                showAlertDialog(RegisterActivity.this,"Error Occured","Please try again");

        }
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private boolean registerUser() {
        // Tag used to cancel the request
        String tag_string_req = "req_register";


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("mobile", phones));
        nameValuePairs.add(new BasicNameValuePair("voter", voters));


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://code.theonecoders.in/android/register.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String st = EntityUtils.toString(response.getEntity());
            Log.v("log_tag", "In the try Loop : " + st.length());
            if(st.length()==3)
            {
                return true;
            }


        } catch (Exception e) {

            Log.v("log_tag",  e.toString());
        }
        hideDialog();
        return false;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);



        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}