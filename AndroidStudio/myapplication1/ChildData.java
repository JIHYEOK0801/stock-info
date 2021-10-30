package com.example.myapplication1;

public class ChildData {
    private String current;
    private String previous_day_compare;
    private String previous_day_price;
    private String market_cost;
    private String high_cost;
    private String low_cost;
    private String upper_limit;
    private String lower_limit;
    private String transaction_volume;
    private String transaction_amount;

    public ChildData(String current, String previous_day_compare, String previous_day_price, String market_cost, String high_cost, String low_cost, String upper_limit, String lower_limit, String transaction_volume, String transaction_amount){
        this.current = current;
        this.previous_day_compare = previous_day_compare;
        this.previous_day_price = previous_day_price;
        this.market_cost = market_cost;
        this.high_cost = high_cost;
        this.low_cost = low_cost;
        this.upper_limit = upper_limit;
        this.lower_limit = lower_limit;
        this.transaction_volume = transaction_volume;
        this.transaction_amount = transaction_amount;
    }

    public String getCurrent(){
        return current;
    }

    public String getPreviousDayCompare(){
        return previous_day_compare;
    }

    public String getPreviousDayPrice(){
        return previous_day_price;
    }

    public String getMarketCost(){
        return market_cost;
    }

    public String getHighCost(){
        return high_cost;
    }

    public String getLowCost(){
        return low_cost;
    }

    public String getUpperLimit(){
        return upper_limit;
    }

    public String getLowerLimit(){
        return lower_limit;
    }

    public String getTransactionVolume(){
        return transaction_volume;
    }

    public String getTransactionAmount(){
        return transaction_amount;
    }
}
