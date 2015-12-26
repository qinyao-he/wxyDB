执行模块

SystemManager 系统管理
    负责所有的DML命令
    创建数据库文件
    删除数据库文件
    切换数据库文件
    创建表
    删除表
    显示所有表
    显示某个表的列信息

RecordManager 记录管理
    负责记录的增,删,改
        调用Table的相关方法

QueryEngine 查询引擎
    负责查询记录
        先调用Table的方法获得所有记录,然后对记录进行选择
