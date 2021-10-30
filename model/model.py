## 모델 실행에 필요한 라이브러리 import
from copy import deepcopy
import pandas as pd
from pandas import Series,DataFrame
import numpy as np
import tensorflow as tf
from tensorflow import keras
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint
from tensorflow.keras.layers import LSTM
from tensorflow.keras.layers import Dropout
from tensorflow.keras.losses import Huber
from tensorflow.keras.optimizers import Adam
from tensorflow.keras import optimizers
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint
from sklearn.metrics import r2_score
from sklearn.metrics import mean_squared_error
from tensorflow.keras.models import load_model
import os

## 모델 실행 함수
def run(model_data):
    ## 모델 입력데이터 model_data에 추가 데이터 컬럽 삽입
    model_data['EMA_close5'] = model_data['close'].ewm(5).mean()
    model_data['EMA_close10'] = model_data['close'].ewm(10).mean()  
    model_data['EMA_close20'] = model_data['close'].ewm(20).mean()
    model_data['EMA_close60'] = model_data['close'].ewm(60).mean()
    model_data['EMA_close120'] = model_data['close'].ewm(120).mean()
    model_data['disp5'] = (model_data['close']/model_data['EMA_close5']) * 100
    model_data['after4days_close'] = model_data['close'].shift(-4)
    model_data = model_data.interpolate(method = 'values', limit_direction = 'both')
    model_data = model_data[120:]
    today_k=model_data['close'].iloc[-1]
    
    ## 데이터 전처리
    new_scaler = MinMaxScaler()
    new_scaler.fit(model_data['close'].values.reshape(-1,1))
    for i in model_data.columns:
        if i == 'date': continue
        model_data[i] = MinMaxScaler().fit_transform(model_data[i].values.reshape(-1, 1)).round(4)
	
    
    feature_cols = ['volume','shanghai','dji', 'nikkei', 'hsi',
                    'won/US dollar', 'won/100en', 'won/euro','association','person',
                    'daebi', 'EMA_close5', 'EMA_close10', 'EMA_close20', 'EMA_close60',
                    'EMA_close120', 'disp5', 'updown','after4days_close']
    
    label_cols = ['close']

    base_dir = '' #모델 저장 위치 서버용
    file_name = '75_LSTM.h5'  #모델 파일명
    dir = os.path.join(base_dir, file_name)
    model = load_model(dir)  #모델 load
    
	## 하루 뒤의 KOSPI지수를 예측하는 모델을 5번 실행하여 5일 뒤를 예측
    def find_pred():    
        df = model_data[-24:] 
        
        for i in range(5):
            df_tmp = df[i:i+20] #20일치 데이터로 예측
            my_final_x_test = df_tmp[feature_cols]
            my_final_x_test = np.array(my_final_x_test)
            my_final_x_test = my_final_x_test.reshape(1, 20, 19)
            my_final_y_pred = model.predict(my_final_x_test)

            if i == 4: 
                return new_scaler.inverse_transform(my_final_y_pred)[0][0]
            else: # 예측한 값을 입력값에 추가해서 다음 예측을 위해 사용
                df['after4days_close'].iloc[i+20] = my_final_y_pred[0][0]

    # return : 오늘의 코스피 값, 오늘의 지수들로 예측한 5일 뒤 코스피 값
    pred = find_pred()
    # 예측 값 - 오늘의 코스피지수 >0 이면 상승이므로 1, 유지 또는 하락의 경우 0
    ud_5days = 0
    if pred - today_k > 0:
        ud_5days = 1
    else:
        ud_5days = 0
    return ud_5days