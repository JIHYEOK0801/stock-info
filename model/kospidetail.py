# kospi지수의 시가, 고가, 저가 종가, 거래량과 각 투자처(개인,기관,외국인,연기금)별 순거래금액을 가져온다.

# -*- coding: utf-8 -*-
import time
from pykrx import stock
from datetime import datetime,timedelta

## documents : https://github.com/sharebook-kr/pykrx
def get_kospi_detail():
    day = (datetime.today()).strftime("%Y%m%d")
    df = stock.get_index_ohlcv_by_date(day, day, "1001")
    open = df.iloc[0, 0] # 시가
    high = df.iloc[0, 1] # 고가
    low = df.iloc[0, 2] # 저가
    close = df.iloc[0, 3] # 종가
    volume = int(df.iloc[0, 4] * 0.001) # 거래량
    return open,high,low,close,volume
    
def get_trading_value():
    day = (datetime.today()).strftime("%Y%m%d")
    df = stock.get_market_trading_value_by_date(day, day, "KOSPI")
    association = int(df.iloc[0,0] * 0.000001) # 기관 순매수금
    person = int(df.iloc[0,2]* 0.000001) # 개인 순매수금
    foreign = int(df.iloc[0,3]* 0.000001) # 외국인 순매수금
    
    df = stock.get_market_trading_value_by_date(day, day, "KOSPI",detail=True)
    pension = int(df.iloc[0,6]* 0.000001) # 연기금 순매수금
    
    return association,foreign,person,pension

def get_original_kospi(day):
    day = day.strftime("%Y%m%d")
    df = stock.get_index_ohlcv_by_date(day, day, "1001")
    close = df.iloc[0,3]
    return close


'''
1001 코스피
1028 코스피 200
1034 코스피 100
1035 코스피 50
1167 코스피 200 중소형주
1182 코스피 200 초대형제외 지수
1244 코스피200제외 코스피지수
1150 코스피 200 커뮤니케이션서비스
1151 코스피 200 건설
1152 코스피 200 중공업
1153 코스피 200 철강/소재
1154 코스피 200 에너지/화학
1155 코스피 200 정보기술
1156 코스피 200 금융
1157 코스피 200 생활소비재
1158 코스피 200 경기소비재
1159 코스피 200 산업재
1160 코스피 200 헬스케어
1005 음식료품
1006 섬유의복
1007 종이목재
1008 화학
1009 의약품
1010 비금속광물
1011 철강금속
1012 기계
1013 전기전자
1014 의료정밀
1015 운수장비
1016 유통업
1017 전기가스업
1018 건설업
1019 운수창고업
1020 통신업
1021 금융업
1022 은행
1024 증권
1025 보험
1026 서비스업
1027 제조업
1002 코스피 대형주
1003 코스피 중형주
1004 코스피 소형주
1224 코스피 200 비중상한 30%
1227 코스피 200 비중상한 25%
1232 코스피 200 비중상한 20%
'''
