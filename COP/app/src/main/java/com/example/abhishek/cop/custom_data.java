package com.example.abhishek.cop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class custom_data extends BaseAdapter {

    String[] titles=null;
    String[] descc=null;

    public class Data {
        String title;
        String desc;


    }
    List<Data> cList;
    private Context con;
    public custom_data(Context con1,String[] titles,String[] descc) {
        this.con = con1;
        this.titles = titles;
        this.descc = descc;


        cList = getDataForListView();
    }
    public List<Data> getDataForListView(){
        List<Data> cList = new ArrayList<Data>();

        for(int i=0;i<titles.length;i++)
        {

            Data c = new Data();
            c.title =titles[i];
            c.desc = descc[i];
            cList.add(c);
        }

        return cList;
    }
    @Override
    public int getCount() {
        return cList.size();
    }

    @Override
    public Object getItem(int position) {
        return cList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        if(arg1==null)
        {
            LayoutInflater inflater = (LayoutInflater) this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.feeditem, arg2,false);
        }

        TextView title = (TextView)arg1.findViewById(R.id.textView1);
        TextView desc = (TextView)arg1.findViewById(R.id.textView2);

        Data c = cList.get(arg0);

        title.setText(c.title);
        desc.setText(c.desc);

        return arg1;
    }
}


