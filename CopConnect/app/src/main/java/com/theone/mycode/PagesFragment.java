package com.theone.mycode;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class PagesFragment extends Fragment {
	
	public PagesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);
        return rootView;
    }

}
