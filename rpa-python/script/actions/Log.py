import logging as logger
import logging.config
from Constant import base_dir
#user_home = os.path.expanduser('~')
#user_home = (user_home + '').replace("\\", "/")
#path = user_home + '/seebon/robot/log.ini'
path = base_dir+"log.ini"
# 采用配置文件
# logger.config.fileConfig(fname=path, encoding="utf-8")
logger.config.fileConfig(fname=path)
log = logging.getLogger('root')
