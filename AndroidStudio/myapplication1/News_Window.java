package com.example.myapplication1;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;

public class News_Window extends Fragment {
    ViewGroup viewGroup;

    ArrayList<String> contentList = new ArrayList<String>();
    ArrayList<String> titleList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView listView;

    String url1 = "https://vip.mk.co.kr/newSt/news/news_list.php?sCode=21";
    String url2 = "https://vip.mk.co.kr/newSt/news/news_list.php?sCode=108";
    int contentPosition;
    static String contentDetail;
    String [] contents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.news_fragment, container, false);
        Button update = (Button) viewGroup.findViewById(R.id.import_news);
        adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, titleList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(getResources().getColor(R.color.light_gray));
                TextView tv = (TextView)view.findViewById(android.R.id.text1);
                tv.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        listView = (ListView) viewGroup.findViewById(R.id.show_news);
        listView.setAdapter(adapter);
        TextView title = (TextView) viewGroup.findViewById(R.id.title);
        Button change = (Button) viewGroup.findViewById(R.id.change_news);

        title.setText("국내 주식 시장 뉴스");
        change.setText("해외");

        new task1().execute();

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String get_title = (String) title.getText();
                if (get_title.equals("국내 주식 시장 뉴스")) {
                    title.setText("해외 주식 시장 뉴스");
                    change.setText("국내");
                    new task2().execute();
                } else {
                    title.setText("국내 주식 시장 뉴스");
                    change.setText("해외");
                    new task1().execute();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().equals("국내 주식 시장 뉴스"))
                    new task1().execute();
                else
                    new task2().execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                contentPosition = position;

                new task3().execute();
            }
        });

        return viewGroup;
    }

    class task1 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (getActivity()!=null) {
                try {
                    listView.setVisibility(View.INVISIBLE);

                    contentList.clear();
                    titleList.clear();

                    Document doc = Jsoup.connect(url1).get();
                    Elements titles = doc.select("td[class=title]");

                    for (Element e : titles) {
                        String news_title = e.select("a").text();
                        String news_link = e.select("a").attr("href");
                        contentList.add(news_link);
                        titleList.add(news_title);
                    }
                } catch (IOException err1) {
                    err1.printStackTrace();
                }
            }

            if (getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class task2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (getActivity()!=null) {
                try {
                    listView.setVisibility(View.INVISIBLE);

                    contentList.clear();
                    titleList.clear();

                    Document doc = Jsoup.connect(url2).get();
                    Elements titles = doc.select("td[class=title]");

                    for (Element e : titles) {
                        String news_title = e.select("a").text();
                        String news_link = e.select("a").attr("href");
                        contentList.add(news_link);
                        titleList.add(news_title);
                    }
                } catch (IOException err1) {
                    err1.printStackTrace();
                }
            }

            if (getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }
    }

    class task3 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (getActivity()!=null){
                try {
                    Document doc = Jsoup.connect("https:" + contentList.get(contentPosition)).get();
                    contentDetail = doc.select("div[id=Conts]").get(0).text();

                    contents = contentDetail.split("▶");
                    contentDetail = contents[0];
                } catch (IOException err1) {
                    err1.printStackTrace();
                }

                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertStyle);
                        builder.setTitle("기사 내용")
                                .setMessage(contentDetail)
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dig, int sumthin){
                                    }
                                })
                                .setNeutralButton("링크", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dig, int sumthin){
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https:" + contentList.get(contentPosition)));
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(getResources().getColor(R.color.purple_200));
                        pbutton.setTextColor(getResources().getColor(R.color.white));
                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
                        nbutton.setBackgroundColor(getResources().getColor(R.color.purple_200));
                        nbutton.setTextColor(getResources().getColor(R.color.white));



                        /*new AlertDialog.Builder(getContext(), R.style.AlertStyle)
                                .setTitle("기사 내용")
                                .setMessage(contentDetail)
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dig, int sumthin){
                                    }
                                })
                                .setNeutralButton("링크", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dig, int sumthin){
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https:" + contentList.get(contentPosition)));
                                        startActivity(intent);
                                    }
                                })
                                .show();*/
                    }
                }, 0);
            }

            return null;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }
    }
}