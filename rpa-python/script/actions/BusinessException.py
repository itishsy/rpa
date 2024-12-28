# coding:utf-8

class BusinessException(Exception):
    SUCC ="200"
    FAIL ="500"
    def __init__(self,code,message):
        code=code
        message=message