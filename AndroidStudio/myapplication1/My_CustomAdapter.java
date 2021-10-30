package com.example.myapplication1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;


public class My_CustomAdapter extends BaseExpandableListAdapter {
    private ArrayList<GroupData> groupDatas;
    private ArrayList<ArrayList<ChildData>> childDatas;
    private LayoutInflater inflater;

    public My_CustomAdapter(Context c, ArrayList<GroupData> groupDatas, ArrayList<ArrayList<ChildData>> childDatas){
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
            convertView = inflater.inflate(R.layout.my_parent_row, null);

        TextView list = (TextView)convertView.findViewById(R.id.my_parent);
        Button button_list = (Button)convertView.findViewById(R.id.my_parent_button);

        button_list.setTextColor(My_Window.my_resource.getColor(R.color.white));

        list.setText(groupDatas.get(groupPosition).getGroupName());

        button_list.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*My_Window.listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
                    @Override
                    public void onGroupExpand(int groupPosition){
                        int groupCount = My_Window.adapter.getGroupCount();
                        for (int i=0;i<groupCount;i++)
                            My_Window.listView.collapseGroup(i);
                    }
                });*/

                for (int i=0;i<My_Window.my_list_title.size();i++)
                    My_Window.listView.collapseGroup(i);

                My_Window.nowPosition--;

                My_Window.my_list_title = My_Window.ReadTitleData(My_Window.ctt);
                My_Window.my_list_code = My_Window.ReadCodeData(My_Window.ctt);

                for (int i=0;i<My_Window.my_list_title.size();i++){
                    if (list.getText().equals(My_Window.my_list_title.get(i))){
                        My_Window.listView.setVisibility(View.INVISIBLE);

                        My_Window.my_list_title.remove(i);
                        My_Window.my_list_code.remove(i);

                        My_Window.SaveTitleData(My_Window.ctt, My_Window.my_list_title);
                        My_Window.SaveCodeData(My_Window.ctt, My_Window.my_list_code);

                        My_Window.settingList();
                        My_Window.setData();

                        notifyDataSetChanged();
                        My_Window.listView.setVisibility(View.VISIBLE);

                        break;
                    }
                }
            }
        });

        return convertView;
    }
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        if (convertView==null)
            convertView = inflater.inflate(R.layout.my_child_row, null);

        TextView current = (TextView)convertView.findViewById(R.id.my_child_current);
        TextView previous_day_compare = (TextView)convertView.findViewById(R.id.my_child_previous_day_compare);
        TextView previous_day_price = (TextView)convertView.findViewById(R.id.my_child_previous_day_price);
        TextView market_cost = (TextView)convertView.findViewById(R.id.my_child_market_cost);
        TextView high_cost = (TextView)convertView.findViewById(R.id.my_child_high_cost);
        TextView low_cost = (TextView)convertView.findViewById(R.id.my_child_low_cost);
        TextView upper_limit = (TextView)convertView.findViewById(R.id.my_child_upper_limit);
        TextView lower_limit = (TextView)convertView.findViewById(R.id.my_child_lower_limit);
        TextView transaction_volume = (TextView)convertView.findViewById(R.id.my_child_transaction_volume);
        TextView transaction_amount = (TextView)convertView.findViewById(R.id.my_child_transaction_amount);

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
