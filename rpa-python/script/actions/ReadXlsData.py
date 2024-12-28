# -*- encoding: utf-8 -*-
"""
通过传入xls地址返回json格式，键为列头名称

作者: liuhongchang
日期: 2024-08-13

模块说明:
本模块提供了读取 XLS 格式的 Excel 文件并将其数据转换为 JSON 格式的功能。
包含两个主要函数：

1. read_xls_xlrd(file_path):
   读取指定路径的 XLS 文件，并将数据转换为包含列头作为键的字典列表。
   参数:
     - file_path (str): XLS 文件的路径
   返回:
     - dict: 包含两个键的字典：
       - "code": 返回状态码，成功为 "200"，失败为 "500"
       - "data": 包含数据的列表，每个元素是一个字典，字典的键为列头名称

2. readxl():
   从命令行获取文件路径，调用 read_xls_xlrd 函数读取数据，并将结果转换为 JSON 格式后打印输出。
   不接收任何参数。
"""
import json
import xlrd
import sys
import uiautomation as auto


def read_xls_xlrd(file_path):
    try:
        # 打开 Excel 文件
        xlrd.Book.encoding = 'gbk'
        workbook = xlrd.open_workbook(file_path)

        # 选择工作簿中的一个工作表
        worksheet = workbook.sheet_by_index(0)  # 通过索引选择，0 表示第一个工作表

        # 获取工作表的行数和列数
        rows = worksheet.nrows
        cols = worksheet.ncols

        # 读取列头
        column_headers = worksheet.row_values(0)

        # 读取数据
        datas = []
        for i in range(1, rows):
            row_data = worksheet.row_values(i)
            data = {column_headers[j]: row_data[j] for j in range(cols)}
            datas.append(data)

        return {"code": "200", "data": datas}
    except Exception as e:
        print("read_xls_xlrd异常 {}".format(e))
        return {"code": "500", "data": []}


def readxl():
    # 超时时间
    auto.SetGlobalSearchTimeout(10)
    # 获取参数（文件路径）
    file_path = sys.argv[1]
    # 读取表格
    req_datas = read_xls_xlrd(file_path)
    # 将数据转换为 JSON 格式
    print(json.dumps(req_datas, ensure_ascii=False, indent=4))


readxl()
