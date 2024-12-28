# coding:utf-8

import sys

import easyocr

def identify():
    # 模型
    model_path = sys.argv[1]
    # 图片路径
    img_path = sys.argv[2]
    # 识别程度
    ocr_detail_type = sys.argv[3]
     # 识别模型
    lang_list = sys.argv[4]

    # 图片识别文字
    reader = easyocr.Reader(lang_list.split(","), gpu=False, model_storage_directory=r'' + model_path,
                            verbose=True, download_enabled=False)
    # 文字结果
    text_result = reader.readtext(img_path, detail=int(ocr_detail_type))

    print(text_result)


identify()

