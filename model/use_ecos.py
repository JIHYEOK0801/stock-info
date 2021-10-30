# 한국은행의 환율 정보를 가져오는데 사용
# -*- coding: utf-8 -*-
import requests 
import xml.etree.ElementTree as ET 
from datetime import datetime
from datetime import timedelta
import time
import isHoliday

key = 'API_KEY'

## API 호출
def runAPI(url):
    response = requests.get(url)  ## http 요청이 성공했을때 API의 리턴값을 가져옵니다.
    
    if response.status_code == 200:
        try:
            contents = response.text
            ecosRoot = ET.fromstring(contents)
            
            if ecosRoot[0].text[:4] in ("INFO","ERRO"):  ## 오류 확인
                print(ecosRoot[0].text + " : " + ecosRoot[1].text)  ## 오류메세지를 확인하고 처리합니다.
                
            else:
                return(ecosRoot[1][10].text)    ## 결과값 확인

        except Exception as e:    ##예외 프린트
            print(str(e))

def get_exchange(): ## 환율

    d = datetime.today().strftime('%Y%m%d')
    statisticcode = '036Y001'
    ## exchange_items = 미국달러, 일본엔, 유럽유로
    exchange_items = ['0000001', '0000002', '0000003']  
    exchanges = []
    for itemcode in exchange_items:
            url = "http://ecos.bok.or.kr/api/StatisticSearch/"+key+"/xml/kr/1/5/"+statisticcode+"/DD/"+d+"/"+d+"/" + itemcode
            exchanges.append(runAPI(url))

    return exchanges