package com.example.myapplication1;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ExpandableListView;
import android.content.Context;
import android.widget.SimpleExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.rpc.context.AttributeContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class List_Window extends Fragment {
    ViewGroup viewGroup;

    Timer timer;
    TimerTask t;
    ExpandableListView listView;
    CustomAdapter adapter;
    ArrayList<GroupData> groupListDatas;
    ArrayList<ArrayList<ChildData>> childListDatas;
    private List<String> list;
    private EditText editSearch;
    int nowPosition;
    static Resources list_resource;
    String[] list_title = {"삼성전자", "SK하이닉스", "LG화학", "NAVER", "삼성바이오로직스", "카카오", "현대차", "삼성SDI", "셀트리온",
            "기아", "POSCO", "현대모비스", "LG전자", "삼성물산", "SK텔레콤", "LG생활건강", "SK이노베이션", "KB금융", "신한지주",
            "SK", "엔씨소프트", "삼성생명", "아모레퍼시픽", "한국전력", "삼성에스디에스", "삼성전기", "하나금융지주", "HMM", "KT&G",
            "포스코케미칼", "넷마블", "한국조선해양", "S-Oil", "롯데케미칼", "삼성화재", "대한항공", "하이브", "한온시스템", "한화솔루션",
            "LG디스플레이", "고려아연", "SK바이오팜", "금호석유", "우리금융지주", "KT", "현대제철", "현대글로비스", "기업은행", "미래에셋증권",
            "CJ제일제당", "한국타이어앤테크놀로", "한국금융지주", "아모레G", "LG유플러스", "현대중공업지주", "현대건설", "강원랜드", "코웨이", "SKC",
            "두산중공업", "이마트", "삼성중공업", "두산밥캣", "오리온", "LG이노텍", "맥쿼리인프라", "한미사이언스", "유한양행", "대우조선해양",
            "GS", "녹십자", "삼성카드", "한미약품", "쌍용C&E", "CJ대한통운", "GS건설", "삼성증권", "롯데지주", "호텔신라",
            "DB손해보험", "삼성엔지니어링", "롯데쇼핑", "NH투자증권", "한진칼", "키움증권", "신풍제약", "한국항공우주", "에스원", "일진머티리얼즈",
            "한국가스공사", "동서", "SK케미칼", "만도", "CJ", "휠라홀딩스", "GS리테일", "더존비즈온", "두산퓨얼셀", "대웅"};
    String[] list_code = {"005930", "000660", "051910", "035420", "207940", "035720", "005380", "006400", "068270",
            "000270", "005490", "012330", "066570", "028260", "017670", "051900", "096770", "105560", "055550",
            "034730", "036570", "032830", "090430", "015760", "018260", "009150", "086790", "011200", "033780",
            "003670", "251270", "009540", "010950", "011170", "000810", "003490", "352820", "018880", "009830",
            "034220", "010130", "326030", "011780", "316140", "030200", "004020", "086280", "024110", "006800",
            "097950", "161390", "071050", "002790", "032640", "267250", "000720", "035250", "021240", "011790",
            "034020", "139480", "010140", "241560", "271560", "011070", "088980", "008930", "000100", "042660",
            "078930", "006280", "029780", "128940", "003410", "000120", "006360", "016360", "004990", "008770",
            "005830", "028050", "023530", "005940", "180640", "039490", "019170", "047810", "012750", "020150",
            "036460", "026960", "285130", "204320", "001040", "081660", "007070", "012510", "336260", "003090"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.list_fragment, container, false);
        groupListDatas = new ArrayList<GroupData>();
        childListDatas = new ArrayList<ArrayList<ChildData>>();
        list = new ArrayList<String>();
        list_resource = getResources();

        listView = (ExpandableListView)viewGroup.findViewById(R.id.all_list);

        settingList();
        Collections.sort(list, cmpAsc);
        setData();

        adapter = new CustomAdapter(container.getContext(), groupListDatas, childListDatas);
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

        editSearch = (EditText) viewGroup.findViewById(R.id.edit_search);
        editSearch.setBackgroundColor(getResources().getColor(R.color.white));

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                //t.cancel();

                for (int i=0;i<list_title.length;i++)
                    listView.collapseGroup(i);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String text = editSearch.getText().toString();
                search(text);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        return viewGroup;
    }

    public void tempTask(){
        if (getActivity()!=null){
            t = new TimerTask(){
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

        if (getActivity() != null && nowPosition<adapter.getGroupCount() && nowPosition>=0 && adapter.getGroupCount()!=0) {
            try {
                for (int i = 0; i < list_title.length; i++) {
                    if (list.get(nowPosition).equals(list_title[i])) {
                        url = url + list_code[i];
                        break;
                    }
                }
                int size = 0;

                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("dl[class=blind]");
                Elements elements1 = elements.select("dd");

                for (Element e : elements1) {
                    temp[size] = e.text();
                    size++;
                }


            } catch (IOException err) {
                err.printStackTrace();
            }
        }

        if (getActivity()!=null && nowPosition<adapter.getGroupCount() && nowPosition>=0 && adapter.getGroupCount()!=0){
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

    private void setData(){
        if (getActivity()!=null){
            for(int i=0;i<list.size();i++) {
                groupListDatas.add(new GroupData(list.get(i)));
                childListDatas.add(new ArrayList<ChildData>());
                childListDatas.get(i).add(new ChildData("wait", "wait", "wait", "wait", "wait", "wait", "wait", "wait", "wait", "wait"));
            }
        }
    }

    Comparator<String> cmpAsc = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) { return o1.compareTo(o2); }
    };

    private void settingList() {
        if (getActivity()!=null){
            list.clear();

            for (int i=0;i<list_title.length;i++){
                list.add(list_title[i]);
            }
        }
    }

    public void search(String charText) {
        if (getActivity()!=null){
            charText = charText.toLowerCase(Locale.getDefault());

            list.clear();
            childListDatas.clear();
            groupListDatas.clear();

            if (charText.length() == 0){
                settingList();
            }

            else {
                for(int i = 0;i<list_title.length;i++) {
                    if (list_title[i].toLowerCase().contains(charText))
                        list.add(list_title[i]);
                }
            }
            Collections.sort(list, cmpAsc);
            setData();
            adapter.notifyDataSetChanged();
        }
    }
}