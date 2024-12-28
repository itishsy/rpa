# -*- coding: UTF-8 -*-
import configparser
import os
import json
import BusinessException as be
from Constant import cmd_sub_status_valid, cmd_sub_status

from Constant import base_dir

"""
http://www.361way.com/python-configparser/4631.html
https://blog.csdn.net/zhusongziye/article/details/80024530
"""
# 配置文件的路径
#user_home = os.path.expanduser('~')
#user_home = (user_home + '').replace("\\", "/")
#path = user_home + '/seebon/robot/config.ini'

path = base_dir+"config.ini"
cmds = {}  # 所有指令

__modes = ['test', 'prod']


# 获取 ini的配置
def get_config(key):
    mode, conf = get_mode_conf()
    return conf.get(mode, key)


def get_mode_conf():
    conf = configparser.ConfigParser()
    # 读ini文件
    conf.read(path, encoding='utf-8')  # python3
    mode = conf.get("profiles", "active")
    if mode not in __modes:
        raise be(be.FAIL, "参数mode(test/prod)输入不正确")
    return mode, conf


# 获取json
def get_config_from_json_list(key, jsonKey, jsonValue):
    jsons = json.loads(get_config(key))
    for js in jsons:
        if js[jsonKey] == jsonValue:
            return js
    return None


# 获取执行指令
def __get_cmd_set():
    if len(cmds) > 0:
        return cmds
    # 使用codecs模块打开JSON文件，指定编码为utf-8
    # directory = '../../resources/'
    # directory = user_home + '/seebon/robot'
    # directory ='../../resources/'
    #directory = user_home + '/seebon/robot'
    #directory = "D:/seebon/rpa/python/"
    __mode, __conf = get_mode_conf()
    json_files = [f for f in os.listdir(base_dir) if f.endswith(__mode + '.json')]
    for file in json_files:
        file_path = os.path.join(base_dir, file)
        with open(file=file_path, mode='r', encoding="utf-8") as f:
            data = json.load(f)
            if len(data.keys()) > 0:
                for k in data.keys():
                    if cmd_sub_status_valid == data[k][cmd_sub_status]:  # 判断是否有效命令
                        cmds[k] = data[k]

    return cmds


def get_cmd(key: str, sub_key: str):
    cmd_sets = __get_cmd_set()
    if key in cmd_sets.keys():
        cs = cmd_sets[key]
        if cs is not None:
            if sub_key in cs.keys():
                return cs[sub_key]
    return None


if __name__ == '__main__':
    print(get_cmd('start', 'type'))
    # print(get_cmd('start', 'type1'))
