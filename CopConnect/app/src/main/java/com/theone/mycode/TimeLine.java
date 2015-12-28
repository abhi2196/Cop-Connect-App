package com.theone.mycode;

import android.app.Fragment;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class TimeLine extends Fragment {

    public TimeLine() {
    }

    GridView grid;
    String[] web =null;
    int[] imageId = null;
    View rootView;
    int case_scale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         case_scale=PhotosFragment.case_scale;
        String id=PhotosFragment.case_id;


        Toast.makeText(getActivity(), "scale "+case_scale+"  id :"+id, Toast.LENGTH_LONG).show();
        if(case_scale==1)
            rootView= inflater.inflate(R.layout.timeline_1, container, false);
        else if(case_scale==2)
            rootView= inflater.inflate(R.layout.timeline_2, container, false);
        else if(case_scale==3 || case_scale==4)
            rootView= inflater.inflate(R.layout.timeline3, container, false);

       // rootView= inflater.inflate(R.layout.mycase_time, container, false);
        new DownloadTask().execute("https://api.mongolab.com/api/1/databases/accident_info/collections/case_info?f=%7B%22_id%22%3A0%2C%22case_data%22%3A1%2C%22case_data.case_date%22%3A1%2C%22case_data.title%22%3A1%2C%22case_data.case_id%22%3A1%2C%22case_data.police_station%22%3A1%2C%22case_data.Investigator%22%3A1%2C%22case_data.dov%22%3A1%7D&q=%7B%22case_data.case_id%22%3A%22"+id+"%22%7D&apiKey=fbLkdET5dnRx-7ZZzlcHkvAOnWFeXTuD");
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
            int progress_scale=0;
            View child=null;
            try {
                JSONArray jarray = new JSONArray(result);
                int a=jarray.length();
                web=new String[a];
                imageId=new int[a];
                //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                for(int i=0; i<jarray.length();i++)
                {
                    JSONObject obj=jarray.getJSONObject(i);
                    JSONArray arr=obj.getJSONArray("case_data");
                    for(int j=0; j<arr.length();j++)
                    {
                        JSONObject mani=arr.getJSONObject(j);
                       // progress_scale=mani.getInt("progress_scale");
                        TextView tv=(TextView)rootView.findViewById(R.id.descp1);
                        tv.setText(mani.getString("title"));
                        TextView tv1=(TextView)rootView.findViewById(R.id.time1);
                        tv1.setText(mani.getString("case_date"));
                        if(case_scale==2)
                        {
                            TextView tv2=(TextView)rootView.findViewById(R.id.descp2);
                            tv2.setText(mani.getString("police_station"));
                            TextView tv3=(TextView)rootView.findViewById(R.id.time2);
                            tv3.setText(mani.getString("case_date"));
                        }
                        if(case_scale==3)
                        {
                            TextView tv2=(TextView)rootView.findViewById(R.id.descp2);
                            tv2.setText(mani.getString("police_station"));
                            TextView tv3=(TextView)rootView.findViewById(R.id.time2);
                            tv3.setText(mani.getString("case_date"));

                            TextView tv4=(TextView)rootView.findViewById(R.id.descp3);
                            tv4.setText(mani.getString("Investigator"));
                            TextView tv5=(TextView)rootView.findViewById(R.id.time3);
                            tv5.setText(mani.getString("dov"));
                        }


                    }

                }
            }
            catch (JSONException e)
            {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
           // RelativeLayout jyoti=(RelativeLayout) rootView.findViewById(R.id.jraju);
           // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
           // if(child!=null)
           // jyoti.addView(child);

        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

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


}