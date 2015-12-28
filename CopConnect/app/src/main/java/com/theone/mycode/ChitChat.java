package com.theone.mycode;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ChitChat extends Fragment {

    public ChitChat() {
    }
    public static String case_id=null;
    public static int case_scale=0;
    ListView lv;
    View rootView;
    private ProgressDialog pDialog;
    private SessionManger session;
    private List<Message> messagesItems;
    String messa;
    EditText mess;
    ChatAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.mychat, container, false);
        lv = (ListView) rootView.findViewById(R.id.list_view_messages);
        // Progress dialog
        session = new SessionManger(getActivity().getApplicationContext());
        pDialog = new ProgressDialog(getActivity());
        session=new SessionManger(getActivity().getApplicationContext());
        mess=(EditText)rootView.findViewById(R.id.inputMsg);
        final Button send=(Button)rootView.findViewById(R.id.btnSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messa=mess.getText().toString();
                String id=session.user();
                Log.e("user_id",id);

                Log.e("url fire","http://code.theonecoders.in/android/chat.php?mess="+messa+"&id="+id);
                if(!messa.isEmpty())
                    try {
                        new DownloadTask().execute("http://code.theonecoders.in/android/chat.php?mess=" + URLEncoder.encode(messa, "UTF-8") + "&id=" + id);
                    }catch (Exception e){}

            }
        });
        pDialog.setCancelable(false);
        messagesItems=new ArrayList<Message>();


        new DownloadTask().execute("https://api.mongolab.com/api/1/databases/accident_info/collections/chat?f=%7B%22_id%22%3A0%2C%22chat_data%22%3A1%2C%22case_data.progress_scale%22%3A1%2C%22case_data.title%22%3A1%2C%22case_data.case_id%22%3A1%7D&apiKey=fbLkdET5dnRx-7ZZzlcHkvAOnWFeXTuD");
        pDialog.setMessage("Loading Chat history ...");
        showDialog();
        return rootView;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread

            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            hideDialog();


            String aa=result.substring(0,3);
           // Toast.makeText(getActivity(),aa, Toast.LENGTH_LONG).show();
            if(aa.equals("yup"))
            {
               Message m=new Message("ME",messa,true);
                messagesItems.add(m);
                Log.e("yes baby","dafad "+ m.getFromName());
                Toast.makeText(getActivity(), "dasf"+m.getMessage(), Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
            else {
                try {
                    JSONArray jarray = new JSONArray(result);
                    int a = jarray.length();


                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject obj = jarray.getJSONObject(i);
                        JSONArray arr = obj.getJSONArray("chat_data");
                        for (int j = 0; j < arr.length(); j++) {
                            Message mss = new Message();
                            JSONObject mani = arr.getJSONObject(j);
                            String sender = mani.getString("sender");
                            String rec = mani.getString("receiver");
                            if (sender.equals(session.user())) {
                                mss.setFromName("Me");
                                mss.setSelf(true);
                                mss.setMessage(mani.getString("message"));

                                messagesItems.add(mss);
                            } else if(rec.equals(session.user())) {
                                mss.setFromName("Police Id" + sender);
                                mss.setSelf(false);
                                mss.setMessage(mani.getString("message"));

                                messagesItems.add(mss);
                            }

                        }

                    }
                } catch (JSONException e) {
                    //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            }
             adapter = new ChatAdapter(getActivity(), messagesItems);
            lv.setAdapter(adapter);

        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 10000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
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