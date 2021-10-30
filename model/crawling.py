# -*- coding: utf-8 -*-
from bs4 import BeautifulSoup
from datetime import datetime
import requests
import time

def get_kospi_daebiupdown(): ##kospi 대비, 등락률
    url= 'https://finance.naver.com/sise/sise_index_day.nhn?code=KOSPI'
    result=requests.get(url)
    result.encoding = 'utf-8'
    
    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.number_1")
    updownpercent = float(indice[1].text.replace('%','').strip()) ## 등락률
    
    indice = soup.select("td.rate_down")
    daebi = float(indice[0].text.strip()) ## 대비
    updown = soup.select("img")[0]['alt'] 
    if updown == '하락':
        daebi = daebi * -1
        
    return daebi, updownpercent

def get_hsi_indice():##홍콩 hsi 지수

    url = 'https://finance.naver.com/world/sise.nhn?symbol=HSI@HSI'
    result=requests.get(url)
    result.encoding = 'utf-8'

    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.tb_td2")
    return indice[0].text.replace(',','')

def get_shanghai_indice(): ## 상하이 상해지수

    url = 'https://finance.naver.com/world/sise.nhn?symbol=SHS@000001'
    result=requests.get(url)
    result.encoding = 'utf-8'

    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.tb_td2")
    return indice[0].text.replace(',','')
    

def get_nikkei_indice():## 일본 니케이

    url = 'https://finance.naver.com/world/sise.nhn?symbol=NII@NI225'
    result=requests.get(url)
    result.encoding = 'utf-8'

    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.tb_td2")
    return indice[0].text.replace(',','')


def get_dji_indice(): ## 미국 다우존스

    url = 'https://finance.naver.com/world/sise.nhn?symbol=DJI@DJI'
    result=requests.get(url)
    result.encoding = 'utf-8'

    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.tb_td2")
    return indice[0].text.replace(',','')

def get_nas_indice():  ## 미국 나스닥

    url = 'https://finance.naver.com/world/sise.nhn?symbol=NAS@IXIC&fdtc=0'
    result=requests.get(url)
    result.encoding = 'utf-8'

    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.tb_td2")
    return indice[0].text.replace(',','')

def get_spi_indice(): ## 미국 s&p 500

    url = 'https://finance.naver.com/world/sise.nhn?symbol=SPI@SPX'
    result=requests.get(url)
    result.encoding = 'utf-8'

    soup=BeautifulSoup(result.content, "html.parser")
    indice = soup.select("td.tb_td2")
    return indice[0].text.replace(',','')


def get_oil_price(): ##wti 유가가격
    
    wti_url = 'https://finance.naver.com/marketindex/worldDailyQuote.nhn?marketindexCd=OIL_CL&fdtc=2'
    
    
    result=requests.get(wti_url)
    result.encoding = 'utf-8'
    
    soup=BeautifulSoup(result.content, "html.parser")
    
    indice = soup.select("td.num")
    wti = indice[0].text.strip()

    return wti
