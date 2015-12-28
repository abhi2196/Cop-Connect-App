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
import android.widget.GridView;
import android.widget.ImageView;
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

public class PhotosFragment extends Fragment {

    public PhotosFragment() {
    }
    public static String case_id=null;
    public static int case_scale=0;
    GridView grid;
    String[] web =null;
    int[] imageId = null;
    int[] web_progress = null;
    String[] posi = null;
    View rootView;
    private ProgressDialog pDialog;
    private SessionManger session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.mycase, container, false);
        grid = (GridView) rootView.findViewById(R.id.grid);
        // Progress dialog
        session = new SessionManger(getActivity().getApplicationContext());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        new DownloadTask().execute("https://api.mongolab.com/api/1/databases/accident_info/collections/case_info?f=%7B%22_id%22%3A0%2C%22case_data%22%3A1%2C%22case_data.progress_scale%22%3A1%2C%22case_data.title%22%3A1%2C%22case_data.case_id%22%3A1%7D&q=%7B%22case_data.Reported_Person_Id%22%3A%22" + session.user() + "%22%7D&apiKey=fbLkdET5dnRx-7ZZzlcHkvAOnWFeXTuD");
        pDialog.setMessage("Loading Case ...");
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
          //  Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            try {
                JSONArray jarray = new JSONArray(result);
                int a=jarray.length();
                web=new String[a];
                imageId=new int[a];
                posi=new String[a];
                web_progress=new int[a];

                for(int i=0; i<jarray.length();i++)
                {
                    JSONObject obj=jarray.getJSONObject(i);
                    JSONArray arr=obj.getJSONArray("case_data");
                    for(int j=0; j<arr.length();j++)
                    {
                        JSONObject mani=arr.getJSONObject(j);
                        String case_n=mani.getString("title");
                        posi[i]=mani.getString("case_id");
                        web[i]=case_n;
                        imageId[i]= R.drawable.case_study_icon;
                       web_progress[i]=mani.getInt("progress_scale");

                    }

                }
            }
            catch (JSONException e)
            {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }
          if(web!=null) {
              CustomGrid adapter = new CustomGrid(getActivity(), web, imageId);

              grid.setAdapter(adapter);

              grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                  @Override
                  public void onItemClick(AdapterView<?> parent, View view,
                                          int position, long id) {

                      case_id = posi[position];
                      case_scale = web_progress[position];
                      Fragment fragment = new TimeLine();
                      if (fragment != null) {
                          FragmentManager fragmentManager = getFragmentManager();
                          fragmentManager.beginTransaction()
                                  .replace(R.id.container_body, fragment).commit();
                      }

                  }
              });
          }
            else
              Toast.makeText(getActivity(), "Network Problem .. pls reload", Toast.LENGTH_LONG).show();

        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 2000;

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