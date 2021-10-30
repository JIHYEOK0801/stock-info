# -*- coding: utf-8 -*-
from datetime import datetime
import time
import requests 
import xml.etree.ElementTree as ET 
print('<-----isHoliday.py import complete----------------->')

def isweekend(today): ## 주말 판별 함수
    day = today.strftime('%A')
    weekend = ['Saturday','Sunday']
    return day in weekend


def isholiday(today): ## 한국의 공휴일 판별 함수
    key = '2tVofBBO9mMcilso8vV6pdrpJxpgT8ELImlNFjf%2BMv%2F4C6hiiegJ5RqvWSBpVnmvsdk9XuoAbo%2B6X5M0i%2FnVwQ%3D%3D' ## api key

    year_str = today.strftime('%Y')
    month_str = today.strftime('%m')
    today_str = today.strftime('%Y%m%d')
    
    url = 'http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?solYear='+year_str+'&solMonth='+month_str+'&ServiceKey='+key

    response = requests.get(url)
    contents = response.text
    ecosRoot = ET.fromstring(contents)

    holiday = []
    for i in ecosRoot.findall('./body/items/item/locdate'):
        holiday.append(i.text)
    
    return today_str in holiday


    
