package com.example.myapplication1;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.geo.type.Viewport;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.TextView;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import android.app.Activity;
import android.graphics.Color;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static java.lang.Float.floatToIntBits;
import static java.lang.Integer.parseInt;

public class Main_Window extends Fragment{
    ViewGroup viewGroup;
    private LineChart lineChart;

    Timer timer;

    ArrayList<String> ndaq_arraylist = new ArrayList<String>();
    ArrayList<String> spy_arraylist = new ArrayList<String>();
    ArrayList<String> dji_arraylist = new ArrayList<String>();
    ArrayList<String> stoxx50_arraylist = new ArrayList<String>();
    ArrayList<String> topix_arraylist = new ArrayList<String>();
    ArrayList<String> nikkei_arraylist = new ArrayList<String>();
    ArrayList<String> ssec_arraylist = new ArrayList<String>();
    ArrayList<String> hsi_arraylist = new ArrayList<String>();

    String ndaq_url = "https://finance.naver.com/world/sise.nhn?symbol=NAS@IXIC";
    String spy_url = "https://finance.naver.com/world/sise.nhn?symbol=SPI@SPX";
    String dji_url = "https://finance.naver.com/world/sise.nhn?symbol=DJI@DJI";
    String stoxx50_url = "https://finance.naver.com/world/sise.nhn?symbol=STX@SX5E";
    String topix_url = "https://finance.naver.com/item/main.nhn?code=101280";
    String nikkei_url = "https://finance.naver.com/world/sise.nhn?symbol=NII@NI225";
    String ssec_url = "https://finance.naver.com/world/sise.nhn?symbol=SHS@000001";
    String hsi_url = "https://finance.naver.com/world/sise.nhn?symbol=HSI@HSI";

    TextView what;

    TextView ndaq_textview, spy_textview, dji_textview, stoxx50_textview, topix_textview, nikkei_textview, ssec_textview, hsi_textview;

    private Thread thread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        viewGroup=(ViewGroup) inflater.inflate(R.layout.main_fragment, container, false);
        lineChart = (LineChart)viewGroup.findViewById(R.id.chart);
        ndaq_textview = (TextView)viewGroup.findViewById(R.id.ndaq_value);
        spy_textview = (TextView)viewGroup.findViewById(R.id.spy_value);
        dji_textview = (TextView)viewGroup.findViewById(R.id.dji_value);
        stoxx50_textview = (TextView)viewGroup.findViewById(R.id.stoxx50_value);
        topix_textview = (TextView)viewGroup.findViewById(R.id.topix_value);
        nikkei_textview = (TextView)viewGroup.findViewById(R.id.nikkei_value);
        ssec_textview = (TextView)viewGroup.findViewById(R.id.ssec_value);
        hsi_textview = (TextView)viewGroup.findViewById(R.id.hsi_value);

        //checkInternetState();

        //차트
        showChart();

        //표
        tempTask();

        return viewGroup;
    }

    private void showChart() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("data").document("dailykospi_android");

        ArrayList<String> real_date = new ArrayList<String>();
        ArrayList<Float> real_kospi_index = new ArrayList<Float>();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && getActivity()!=null) {
                    DocumentSnapshot document = task.getResult();
                    real_date.clear();
                    real_kospi_index.clear();

                    if (document.exists()) {
                        HashMap<String,Object> kospi = new HashMap<String,Object>(document.getData());

                        Object[] mapkey = kospi.keySet().toArray();
                        Arrays.sort(mapkey);

                        int count = 0;

                        for(Object nkey : mapkey) {
                            real_date.add(count, nkey.toString());
                            real_kospi_index.add(count, Float.valueOf(kospi.get(nkey).toString()));
                            count++;
                        }
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        DocumentReference docRef3 = db.collection("data").document("origin");

        docRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && getActivity() != null) {
                    DocumentSnapshot document = task.getResult();
                    String date1 = new String();
                    String up_down1 = new String();

                    if (document.exists()) {
                        HashMap<String, Object> kospi = new HashMap<String, Object>(document.getData());

                        Object[] mapkey = kospi.keySet().toArray();
                        Arrays.sort(mapkey);

                        for (Object nkey : mapkey) {
                            date1 = nkey.toString();
                            up_down1 = kospi.get(nkey).toString();
                        }

                        what = new TextView(getContext());
                        what.setText(Html.fromHtml("[예측] " + date1 + " "  + "<FONT color=" + "#FF9800" + ">" + up_down1 + "</FONT>"));
                       }
                }
            }
        });

        DocumentReference docRef2 = db.collection("data").document("predictedkospi_test");

        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if (task.isSuccessful() && getActivity()!=null) {
                    DocumentSnapshot document = task.getResult();
                    String date = new String();
                    String up_down = new String();
                    TextView show = (TextView)viewGroup.findViewById(R.id.show_predict);

                    if (document.exists()) {
                        HashMap<String,Object> kospi = new HashMap<String,Object>(document.getData());

                        Object[] mapkey = kospi.keySet().toArray();
                        Arrays.sort(mapkey);

                        for(Object nkey : mapkey) {
                            date = nkey.toString();
                            up_down = kospi.get(nkey).toString();
                        }



                        if (up_down.equals("1")){
                            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                                @Override
                                public Drawable getDrawable(String source) {
                                    if (source.equals("up_triangle")){
                                        Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                        return drawable;
                                    }
                                    return null;
                                }
                            };
                            Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                            if (real_date.size()>0){
                                show.setText(what.getText());
                                show.append(" 기준 : " + date + " (");
                                show.append(htmlText);
                                show.append(")");
                                show.setTextColor(Color.parseColor("#FF555555"));
                            }
                        }
                        else if (up_down.equals("0")){
                            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                                @Override
                                public Drawable getDrawable(String source) {
                                    if (source.equals("down_triangle")){
                                        Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                        drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                        return drawable;
                                    }
                                    return null;
                                }
                            };
                            Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                            if (real_date.size()>0){
                                show.setText(what.getText());
                                show.append(" 기준 : " + date + " (");
                                show.append(htmlText);
                                show.append(")");
                                show.setTextColor(Color.parseColor("#FF555555"));
                            }
                        }

                        //차트 생성
                        ArrayList<Entry> values = new ArrayList<>();
                        for (int i=0;i<real_kospi_index.size();i++){
                            values.add(new Entry(real_kospi_index.get(i), i));
                        }

                        LineDataSet lineDataSet = new LineDataSet(values, "KOSPI");
                        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.red));
                        lineDataSet.setDrawCircles(false);
                        lineDataSet.setValueTextSize(10);

                        String[] xaxes = new String[real_date.size()];
                        for (int i=0;i<real_date.size();i++)
                            xaxes[i] = real_date.get(i);

                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.purple_500));
                        xAxis.setTextSize(11);

                        YAxis yAxisLeft = lineChart.getAxisLeft();
                        yAxisLeft.setDrawLabels(false);
                        yAxisLeft.setDrawAxisLine(false);
                        yAxisLeft.setDrawGridLines(false);

                        YAxis yAxisRight = lineChart.getAxisRight();
                        yAxisRight.setTextColor(ContextCompat.getColor(getContext(), R.color.purple_700));
                        yAxisRight.setTextSize(13);

                        lineChart.setDescription(null);
                        lineChart.setTouchEnabled(true);

                        lineChart.getAxisLeft().setLabelCount(10, true);
                        lineChart.getAxisRight().setLabelCount(3,true);

                        Legend legend = lineChart.getLegend();
                        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.red));

                        lineChart.setData(new LineData(xaxes, lineDataSet));
                        lineChart.invalidate();
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });

        if (getActivity()!=null && real_date==null)
            showChart();
    }

    public void tempTask(){
        if (getActivity()!=null){
            TimerTask t = new TimerTask(){
                @Override
                public void run(){
                    get_data();
                }
            };
            timer = new Timer();
            timer.schedule(t, 0, 60000);
        }
    }

    public void get_data(){
        if (getActivity()!=null){
            try{
                ndaq_arraylist.clear();

                Document doc1 = Jsoup.connect(ndaq_url).get();
                Elements contents1 = doc1.select("td[class=tb_td2]");

                for (Element e : contents1){
                    String content = e.select("span").text();
                    ndaq_arraylist.add(content);
                }

                spy_arraylist.clear();

                Document doc2 = Jsoup.connect(spy_url).get();
                Elements contents2 = doc2.select("td[class=tb_td2]");

                for (Element e : contents2){
                    String content = e.select("span").text();
                    spy_arraylist.add(content);
                }

                dji_arraylist.clear();

                Document doc3 = Jsoup.connect(dji_url).get();
                Elements contents3 = doc3.select("td[class=tb_td2]");

                for (Element e : contents3){
                    String content = e.select("span").text();
                    dji_arraylist.add(content);
                }

                stoxx50_arraylist.clear();

                Document doc4 = Jsoup.connect(stoxx50_url).get();
                Elements contents4 = doc4.select("td[class=tb_td2]");

                for (Element e : contents4){
                    String content = e.select("span").text();
                    stoxx50_arraylist.add(content);
                }

                topix_arraylist.clear();

                Document doc5 = Jsoup.connect(topix_url).get();
                Elements contents5 = doc5.select("dl[class=blind]");

                for (Element e : contents5){
                    String content = e.select("dd").text();
                    topix_arraylist.add(content);
                }

                nikkei_arraylist.clear();

                Document doc6 = Jsoup.connect(nikkei_url).get();
                Elements contents6 = doc6.select("td[class=tb_td2]");

                for (Element e : contents6){
                    String content = e.select("span").text();
                    nikkei_arraylist.add(content);
                }

                ssec_arraylist.clear();

                Document doc7 = Jsoup.connect(ssec_url).get();
                Elements contents7 = doc7.select("td[class=tb_td2]");

                for (Element e : contents7){
                    String content = e.select("span").text();
                    ssec_arraylist.add(content);
                }

                hsi_arraylist.clear();

                Document doc8 = Jsoup.connect(hsi_url).get();
                Elements contents8 = doc8.select("td[class=tb_td2]");

                for (Element e : contents8){
                    String content = e.select("span").text();
                    hsi_arraylist.add(content);
                }
            }catch(IOException err){
                err.printStackTrace();
            }
        }

        if (getActivity()!=null && ndaq_arraylist!=null && spy_arraylist!=null && dji_arraylist!=null && stoxx50_arraylist!=null && topix_arraylist!=null && nikkei_arraylist!=null && ssec_arraylist!=null && hsi_arraylist!=null
        && ndaq_arraylist.size()!=0 && spy_arraylist.size()!=0 && dji_arraylist.size()!=0 && stoxx50_arraylist.size()!=0 && topix_arraylist.size()!=0 && nikkei_arraylist.size()!=0 && ssec_arraylist.size()!=0 && hsi_arraylist.size()!=0){
            this.getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    String temp1 = ndaq_arraylist.get(0);
                    String temp1_1 = temp1.replace(",", "");
                    String temp1_2 = ndaq_arraylist.get(1);
                    temp1_2 = temp1_2.replace(",", "");
                    float compare1 = Float.parseFloat(temp1_1) - Float.parseFloat(temp1_2);

                    if (compare1>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        ndaq_textview.setText(temp1 + "(" );
                        ndaq_textview.append(htmlText);
                        ndaq_textview.append(")");
                    }
                    else if (compare1<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        ndaq_textview.setText(temp1 + "(" );
                        ndaq_textview.append(htmlText);
                        ndaq_textview.append(")");
                    }
                    else
                        ndaq_textview.setText(temp1 + "( - )");

                    String temp2 = spy_arraylist.get(0);
                    String temp2_1 = temp2.replace(",", "");
                    String temp2_2 = spy_arraylist.get(1);
                    temp2_2 = temp2_2.replace(",", "");
                    float compare2 = Float.parseFloat(temp2_1) - Float.parseFloat(temp2_2);

                    if (compare2>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        spy_textview.setText(temp2 + "(" );
                        spy_textview.append(htmlText);
                        spy_textview.append(")");
                    }
                    else if (compare2<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        spy_textview.setText(temp2 + "(" );
                        spy_textview.append(htmlText);
                        spy_textview.append(")");
                    }
                    else
                        spy_textview.setText(temp2 + "( - )");

                    String temp3 = dji_arraylist.get(0);
                    String temp3_1 = temp3.replace(",", "");
                    String temp3_2 = dji_arraylist.get(1);
                    temp3_2 = temp3_2.replace(",", "");
                    float compare3 = Float.parseFloat(temp3_1) - Float.parseFloat(temp3_2);

                    if (compare3>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        dji_textview.setText(temp3 + "(" );
                        dji_textview.append(htmlText);
                        dji_textview.append(")");
                    }
                    else if (compare3<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        dji_textview.setText(temp3 + "(" );
                        dji_textview.append(htmlText);
                        dji_textview.append(")");
                    }
                    else
                        dji_textview.setText(temp3 + "( - )");

                    String temp4 = stoxx50_arraylist.get(0);
                    String temp4_1 = temp4.replace(",", "");
                    String temp4_2 = stoxx50_arraylist.get(1);
                    temp4_2 = temp4_2.replace(",", "");
                    float compare4 = Float.parseFloat(temp4_1) - Float.parseFloat(temp4_2);

                    if (compare4>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        stoxx50_textview.setText(temp4 + "(" );
                        stoxx50_textview.append(htmlText);
                        stoxx50_textview.append(")");
                    }
                    else if (compare4<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        stoxx50_textview.setText(temp4 + "(" );
                        stoxx50_textview.append(htmlText);
                        stoxx50_textview.append(")");
                    }
                    else
                        stoxx50_textview.setText(temp4 + "( - )");

                    String temp5_0 = topix_arraylist.get(0);
                    String[] temp5_00 = temp5_0.split(" ");
                    String temp5 = temp5_00[14];
                    String temp5_1 = temp5.replace(",", "");
                    String temp5_2 = temp5_00[22];
                    temp5_2 = temp5_2.replace(",", "");
                    float compare5 = Float.parseFloat(temp5_1) - Float.parseFloat(temp5_2);

                    if (compare5>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        topix_textview.setText(temp5 + "(" );
                        topix_textview.append(htmlText);
                        topix_textview.append(")");
                    }
                    else if (compare5<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        topix_textview.setText(temp5 + "(" );
                        topix_textview.append(htmlText);
                        topix_textview.append(")");
                    }
                    else
                        topix_textview.setText(temp5 + "( - )");

                    String temp6 = nikkei_arraylist.get(0);
                    String temp6_1 = temp6.replace(",", "");
                    String temp6_2 = nikkei_arraylist.get(1);
                    temp6_2 = temp6_2.replace(",", "");
                    float compare6 = Float.parseFloat(temp6_1) - Float.parseFloat(temp6_2);

                    if (compare6>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        nikkei_textview.setText(temp6 + "(" );
                        nikkei_textview.append(htmlText);
                        nikkei_textview.append(")");
                    }
                    else if (compare6<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        nikkei_textview.setText(temp6 + "(" );
                        nikkei_textview.append(htmlText);
                        nikkei_textview.append(")");
                    }
                    else
                        nikkei_textview.setText(temp6 + "( - )");

                    String temp7 = ssec_arraylist.get(0);
                    String temp7_1 = temp7.replace(",", "");
                    String temp7_2 = ssec_arraylist.get(1);
                    temp7_2 = temp7_2.replace(",", "");
                    float compare7 = Float.parseFloat(temp7_1) - Float.parseFloat(temp7_2);

                    if (compare7>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        ssec_textview.setText(temp7 + "(" );
                        ssec_textview.append(htmlText);
                        ssec_textview.append(")");
                    }
                    else if (compare7<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        ssec_textview.setText(temp7 + "(" );
                        ssec_textview.append(htmlText);
                        ssec_textview.append(")");
                    }
                    else
                        ssec_textview.setText(temp7 + "( - )");

                    String temp8 = hsi_arraylist.get(0);
                    String temp8_1 = temp8.replace(",", "");
                    String temp8_2 = hsi_arraylist.get(1);
                    temp8_2 = temp8_2.replace(",", "");
                    float compare8 = Float.parseFloat(temp8_1) - Float.parseFloat(temp8_2);

                    if (compare8>0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("up_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.up_triangle);
                                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"up_triangle\" width=20 height=20>", imageGetter, null);

                        hsi_textview.setText(temp8 + "(" );
                        hsi_textview.append(htmlText);
                        hsi_textview.append(")");
                    }
                    else if (compare8<0){
                        Html.ImageGetter imageGetter = new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (source.equals("down_triangle")){
                                    Drawable drawable = getResources().getDrawable(R.drawable.down_triangle);
                                    drawable.setBounds( 0, 0, drawable.getIntrinsicWidth()/2, drawable.getIntrinsicHeight()*5/13);
                                    return drawable;
                                }
                                return null;
                            }
                        };
                        Spanned htmlText = Html.fromHtml("<img src=\"down_triangle\" width=20 height=20>", imageGetter, null);

                        hsi_textview.setText(temp8 + "(" );
                        hsi_textview.append(htmlText);
                        hsi_textview.append(")");
                    }
                    else
                        hsi_textview.setText(temp8 + "( - )");
                }
            });
        }
    }
}