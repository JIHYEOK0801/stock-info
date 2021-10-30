package com.example.myapplication1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class CustomAdapter extends BaseExpandableListAdapter {
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

    private ArrayList<GroupData> groupDatas;
    private ArrayList<ArrayList<ChildData>> childDatas;
    private LayoutInflater inflater;

    public CustomAdapter(Context c, ArrayList<GroupData> groupDatas, ArrayList<ArrayList<ChildData>> childDatas){
        this.groupDatas = groupDatas;
        this.childDatas = childDatas;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Object getGroup(int groupPosition){
        return groupDatas.get(groupPosition);
    }

    public Object getChild(int groupPosition, int childPosition){ return childDatas.get(groupPosition).get(childPosition); }

    public int getGroupCount(){
        return groupDatas.size();
    }

    public int getChildrenCount(int groupPosition){
        return childDatas.get(groupPosition).size();
    }

    public long getGroupId(int groupPosition){
        return groupPosition;
    }

    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public boolean hasStableIds(){
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return true;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        if (convertView==null)
            convertView = inflater.inflate(R.layout.parent_row, null);

        TextView list = (TextView)convertView.findViewById(R.id.parent);
        Button button_list = (Button)convertView.findViewById(R.id.parent_button);

        button_list.setTextColor(List_Window.list_resource.getColor(R.color.white));

        list.setText(groupDatas.get(groupPosition).getGroupName());

        button_list.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ArrayList<String> temp = new ArrayList<String>();
                int check=0;

                temp = My_Window.ReadTitleData(My_Window.ctt);
                if (temp!=null){
                    for (int a=0;a<temp.size();a++){
                        if (temp.get(a).equals(list.getText()))
                            check=1;
                    }
                }

                if (My_Window.temp_title.size()!=0){
                    for (int a=0;a<My_Window.temp_title.size();a++){
                        if (My_Window.temp_title.get(a).equals(list.getText()))
                            check=1;
                    }
                }

                if (check==0){
                    for (int i=0;i<list_title.length;i++){
                        if (list.getText().equals(list_title[i])){
                            My_Window.temp_title.add(list_title[i]);
                            My_Window.temp_code.add(list_code[i]);
                            break;
                        }
                    }
                }
            }
        });

        return convertView;
    }
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        if (convertView==null)
            convertView = inflater.inflate(R.layout.child_row, null);

        TextView current = (TextView)convertView.findViewById(R.id.child_current);
        TextView previous_day_compare = (TextView)convertView.findViewById(R.id.child_previous_day_compare);
        TextView previous_day_price = (TextView)convertView.findViewById(R.id.child_previous_day_price);
        TextView market_cost = (TextView)convertView.findViewById(R.id.child_market_cost);
        TextView high_cost = (TextView)convertView.findViewById(R.id.child_high_cost);
        TextView low_cost = (TextView)convertView.findViewById(R.id.child_low_cost);
        TextView upper_limit = (TextView)convertView.findViewById(R.id.child_upper_limit);
        TextView lower_limit = (TextView)convertView.findViewById(R.id.child_lower_limit);
        TextView transaction_volume = (TextView)convertView.findViewById(R.id.child_transaction_volume);
        TextView transaction_amount = (TextView)convertView.findViewById(R.id.child_transaction_amount);

        current.setText("현재가 : " + childDatas.get(groupPosition).get(childPosition).getCurrent());
        previous_day_compare.setText("전일대비 : " + childDatas.get(groupPosition).get(childPosition).getPreviousDayCompare());
        previous_day_price.setText("전일가 : " + childDatas.get(groupPosition).get(childPosition).getPreviousDayPrice());
        market_cost.setText("시가 : " + childDatas.get(groupPosition).get(childPosition).getMarketCost());
        high_cost.setText("고가 : " + childDatas.get(groupPosition).get(childPosition).getHighCost());
        low_cost.setText("저가 : " + childDatas.get(groupPosition).get(childPosition).getLowCost());
        upper_limit.setText("상한가 : " + childDatas.get(groupPosition).get(childPosition).getUpperLimit());
        lower_limit.setText("하한가 : " + childDatas.get(groupPosition).get(childPosition).getLowerLimit());
        transaction_volume.setText("거래량 : " + childDatas.get(groupPosition).get(childPosition).getTransactionVolume());
        transaction_amount.setText("거래대금 : " + childDatas.get(groupPosition).get(childPosition).getTransactionAmount());

        current.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        previous_day_compare.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        previous_day_price.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        market_cost.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        high_cost.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        low_cost.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        upper_limit.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        lower_limit.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        transaction_volume.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));
        transaction_amount.setTextColor(List_Window.list_resource.getColor(R.color.dark_gray));

        return convertView;
    }
}
