print('------------------------------------------------------------------------------')
# -*- coding: utf-8 -*-
from datetime import datetime
from datetime import timedelta

import schedule
import time
import crawling
import isHoliday
import use_ecos
import fb
import kospidetail
import model
from calendar import monthrange

##########################################일별 데이터 ##########################################
def kospidaebiupdown_update(): ## 대비, 등락률
    return crawling.get_kospi_daebiupdown()

def kospidetail_update(): ## 시가, 고가, 저가, 종가, 거래량
    return kospidetail.get_kospi_detail()

def kospitrading_update(): ## 기관, 외국인, 개인, 연기금
    return kospidetail.get_trading_value()

def hsi_update(): # hsi지수
    return crawling.get_hsi_indice()

def shanghai_update(): # 상하이지수
    return crawling.get_shanghai_indice()

def nikkei_update(): # 닛케이지수
    return crawling.get_nikkei_indice()

def dji_update(): # 다우존스지수
    return crawling.get_dji_indice()

def nas_update(): # 나스닥지수
    return crawling.get_nas_indice()

def spi_update(): # s&p500 지수
    return crawling.get_spi_indice()

def oilprice_update(): # wti 가격
    return crawling.get_oil_price()

def exchange_update(): # 환율
    return use_ecos.get_exchange()

##########################################일별 데이터 ##########################################

#########################################fb 갱신 함수###############################################
## 1차 갱신
def ecosDBupdate():
    day = datetime.today()
    if(isHoliday.isholiday(day) or isHoliday.isweekend(day)): ## 공휴일,주말이면 data갱신 안함
        return
    else:
        ## 환율
        exchanges = exchange_update()
        day_str = day.strftime('%Y-%m-%d')
        day_factors = exchanges

        ## DBupdate
        fb.fb_update_ecos(day_str, day_factors)
        
## 2차 갱신
def crawlingDBupdate():
    day = datetime.today()
    if(isHoliday.isholiday(day) or isHoliday.isweekend(day)): ## 공휴일,주말이면 data갱신 안함 ##
        return
    else:
        ## 시가, 고가, 저가, 종가, 거래량 데이터
        open,high,low,close,volume = kospidetail_update()
        ## 대비, 등락률 데이터
        daebi,updown = kospidaebiupdown_update()

        ## 기관, 외국인, 개인, 연기금 데이터
        association,foreign,person,pension = kospitrading_update()
        ## 각국 지수 업데이트
        ## hsi(홍콩), shanghai(상하이종합), nikkei(닛케이), dji(다우존스), nas(나스닥), spi(S&P 500)
        hsi = hsi_update()
        shanghai = shanghai_update()
        nikkei = nikkei_update()
        dji = dji_update()
        nas = nas_update()
        spi = spi_update()
        ## wti 데이터
        wti = oilprice_update()
        today_str = day.strftime('%Y-%m-%d')
        today_factors =[close, daebi, updown, open, high,
                        low, volume, association, foreign, person, 
                        pension, hsi, shanghai, nas, spi,
                        dji, nikkei, 'NaN', 'NaN', 'NaN',
                        wti]
        
        ## DBupdate
        fb.fb_update_crawling(today_str, today_factors)

def dailykospi_android_update():  ## 어플리케이션 차트에 필요한 데이터 갱신
	## 공휴일,주말이면 data 갱신 X
    if(isHoliday.isholiday(datetime.today()) or isHoliday.isweekend(datetime.today())): 
        return
    else:
        today = datetime.today().strftime('%Y-%m-%d')
        open, high, low, close, volume = kospidetail_update()
        fb.fb_update_daioykospi_android(today, str(close))

def runmodel():
    ## 데이터프레임 생성
    data = fb.make_data()
	## 모델 결과값
    predict_kospi = model.run(data)
    ## 가장 최근의 장 마감 날짜(비교 날짜)
    day = datetime.today()
    while(isHoliday.isweekend(day) or isHoliday.isholiday(day)):
        day = day - timedelta(days=1)
	## 가장 최근의 종가(비교 값)
    origin_kospi = kospidetail.get_original_kospi(day) 
	## DB 갱신
    fb.upload_predict_kospi(predict_kospi,day,origin_kospi)
    

## 1차 갱신 // 한국시간 16시
schedule.every().day.at("07:00").do(ecosDBupdate) 
## 2차 갱신 // 한국시간 8시 -- 한국시간으로는 전날 데이터가 업데이트
schedule.every().day.at("23:00").do(crawlingDBupdate)
## 안드로이드 차트 KOSPI 데이터 갱신 // 한국시간 20시
schedule.every().day.at("11:00").do(dailykospi_android_update) 
## 매주 토요일 모델 실행하여 예측 // 한국시간 12시
schedule.every().saturday.at("03:00").do(runmodel)

## 스케줄러 실행
while True:
    schedule.run_pending()
    time.sleep(1)
