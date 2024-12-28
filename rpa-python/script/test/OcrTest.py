# coding:utf-8

import uuid

import easyocr
from paddleocr import PaddleOCR

def text():
    ocr = PaddleOCR(use_angle_cls=True, lang="ch")
    img_path = '22.png'
    result = ocr.ocr(img_path, det=True,rec=True,cls=False)
    print(result)

    file_name = str(uuid.uuid1()).replace("-", "") + ".png"
    print(file_name)

    reader = easyocr.Reader(['ch_sim', 'en'], gpu=False, model_storage_directory=r'D:\seebon\rpa\python\model',user_network_directory =r'D:\seebon\rpa\python',
                            verbose=True, download_enabled=False)
    result = reader.readtext('22.png', detail=1)
    for res in result:
        print(res)


text()
