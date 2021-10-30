package com.example.myapplication1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.reflect.Type;
import com.google.common.reflect.TypeToken;
import android.content.Context;

public class My_Window extends Fragment{
    ViewGroup viewGroup;

    static Timer timer;
    static ExpandableListView listView;
    static My_CustomAdapter adapter;
    static ArrayList<GroupData> groupListDatas;
    static ArrayList<ArrayList<ChildData>> childListDatas;
    static List<String> list;
    static int nowPosition;

    static public ArrayList<String> my_list_title = new ArrayList<String>();
    static public ArrayList<String> my_list_code = new ArrayList<String>();
    static public ArrayList<String> temp_title = new ArrayList<String>();
    static public ArrayList<String> temp_code = new ArrayList<String>();

    int cnt = 0;
    static Context ctt;
    static Activity act;
    static Resources my_resource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        viewGroup=(ViewGroup) inflater.inflate(R.layout.my_fragment, container, false);
        groupListDatas = new ArrayList<GroupData>();
        childListDatas = new ArrayList<ArrayList<ChildData>>();
        list = new ArrayList<String>();
        ctt = getContext();
        act = getActivity();
        my_resource = getResources();


        if (temp_title.size()!=0){
            for (int i=0;i<temp_title.size();i++){
                my_list_title.add(temp_title.get(i));
                my_list_code.add(temp_code.get(i));
            }

            temp_title.clear();
            temp_code.clear();

            SaveTitleData(getContext(), my_list_title);
            SaveCodeData(getContext(), my_list_code);
            my_list_title = ReadTitleData(getContext());
            my_list_code = ReadCodeData(getContext());
        }
        else{
            my_list_title = ReadTitleData(getContext());
            my_list_code = ReadCodeData(getContext());
        }

        listView = (ExpandableListView)viewGroup.findViewById(R.id.my_list);
        settingList();
        setData();

        adapter = new My_CustomAdapter(getContext(), groupListDatas, childListDatas);
        listView.setAdapter(adapter);

        listView.setGroupIndicator(null);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id){
                nowPosition = groupPosition;
                tempTask();

                return false;
            }
        });

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
            @Override
            public void onGroupExpand(int groupPosition){
                int groupCount = adapter.getGroupCount();
                for (int i=0;i<groupCount;i++){
                    if (!(i==groupPosition)) {
                        listView.collapseGroup(i);
                    }
                }
            }
        });

        return viewGroup;
    }

    public static Context getAppContext(){
        return ctt;
    }

    public void tempTask(){
        if (getActivity()!=null){
            TimerTask t = new TimerTask(){
                @Override
                public void run(){
                    get_real_time_data();
                }
            };
            timer = new Timer();
            timer.schedule(t, 0, 1000);
        }
    }

    public void get_real_time_data(){
        String[] temp = new String[15];
        String url = "https://finance.naver.com/item/main.nhn?code=";

        if (getActivity()!=null && nowPosition<my_list_title.size() && nowPosition>=0 && adapter.getGroupCount()!=0){
            try{
                for(int i=0;i<my_list_title.size();i++){
                    if (list.get(nowPosition).equals(my_list_title.get(i))) {
                        url = url + my_list_code.get(i);
                        break;
                    }
                }
                int size = 0;

                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("dl[class=blind]");
                Elements elements1 = elements.select("dd");

                for (Element e : elements1){
                    temp[size] = e.text();
                    size++;
                }


            }catch(IOException err){
                err.printStackTrace();
            }
        }

        if (getActivity()!=null && temp!=null && nowPosition<my_list_title.size() && nowPosition>=0 && adapter.getGroupCount()!=0){
            this.getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    String[] temp1 = temp[3].split(" ");
                    String[] temp2 = temp[4].split(" ");
                    String[] temp3 = temp[5].split(" ");
                    String[] temp4 = temp[6].split(" ");
                    String[] temp5 = temp[7].split(" ");
                    String[] temp6 = temp[8].split(" ");
                    String[] temp7 = temp[9].split(" ");
                    String[] temp8 = temp[10].split(" ");
                    String[] temp9 = temp[11].split(" ");

                    childListDatas.get(nowPosition).clear();
                    childListDatas.get(nowPosition).add(new ChildData(temp1[1],temp1[3] + " " + temp1[4],temp2[1],temp3[1],temp4[1],temp6[1],temp5[1],temp7[1],temp8[1],temp9[1]));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    static void setData(){
        if (act!=null){
            groupListDatas.clear();
            childListDatas.clear();

            for(int i=0;i<list.size();i++) {
                groupListDatas.add(new GroupData(list.get(i)));
                childListDatas.add(new ArrayList<ChildData>());
                childListDatas.get(i).add(new ChildData("wait", "wait", "wait", "wait", "wait", "wait", "wait", "wait", "wait", "wait"));
            }
        }
    }

    static void settingList() {
        if (act!=null){
            list.clear();
            my_list_code.clear();

            list = ReadTitleData(ctt);
            my_list_title = ReadTitleData(ctt);
            my_list_code = ReadCodeData(ctt);
        }
    }

    static void SaveTitleData(Context context, ArrayList<String> my_list_title){
        if (act!=null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            JSONArray a = new JSONArray();

            for (int i=0;i<my_list_title.size();i++){
                a.put(my_list_title.get(i));
            }

            if (!my_list_title.isEmpty()){
                editor.putString("titles_json", a.toString());
            }
            else{
                editor.putString("titles_json", null);
            }

            editor.apply();
        }
    }

    static void SaveCodeData(Context context, ArrayList<String> my_list_code){
        if (act!=null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            JSONArray a = new JSONArray();

            for (int i=0;i<my_list_code.size();i++){
                a.put(my_list_code.get(i));
            }

            if (!my_list_code.isEmpty()){
                editor.putString("codes_json", a.toString());
            }
            else{
                editor.putString("codes_json", null);
            }

            editor.apply();
        }
    }

    static ArrayList<String> ReadTitleData(Context context){
        if (act!=null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String json = preferences.getString("titles_json", null);
            ArrayList<String> temp = new ArrayList<String>();

            if (json!=null){
                try{
                    JSONArray a = new JSONArray(json);

                    for (int i=0;i<a.length();i++){
                        String tmp = a.optString(i);
                        temp.add(tmp);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            return temp;
        }

        return null;
    }

    static ArrayList<String> ReadCodeData(Context context){
        if (act!=null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String json = preferences.getString("codes_json", null);
            ArrayList<String> temp = new ArrayList<String>();

            if (json!=null){
                try{
                    JSONArray a = new JSONArray(json);

                    for (int i=0;i<a.length();i++){
                        String tmp = a.optString(i);
                        temp.add(tmp);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            return temp;
        }

        return null;
    }
}