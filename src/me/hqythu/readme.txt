----------------------日志----------------------
2015年10月25日
    数据库系统框架雏形
        四大模块、exception、utils
    完成页式文件系统、页式文件系统缓存操作（未严格测试）

2015年10月26日
    补充框架
    索引能有多个吗？

2015年11月15日
    完成系统管理模块
        创建数据库文件
        删除数据库文件
        切换数据库文件
        创建表
        删除表
        显示所有表

2015年11月16日
    完成
        Record
        TablePageUser
        DbPageUser
        BitSetMask
        Column DataType
        Table
            insert removeAll remove
        DataPageUser
            init write read get remove

2015年11月23日
    设计Where类

2015年12月24日
    单元测试


未完成
    Table
         update
    页式文件系统测试
    Table的测试
    SelectOption
    Where

下次开始
    完成Table的remove(Where where)
    完成Table的update(cols,values,where);

问题
    QuerySet SelectOption 这几个类的设计
    设计query操作的位置
    Where类的设计


----------------------进度----------------------
已完成
    SystemManage测试
        创建、切换、删除数据库
        创建、删除、显示表
    SQL大部分语句的解析
未完成
    PageFile测试
    Record测试
    Query模块
    Table测试
        插入、删除、更新
    Query测试
        查询
    SQL语句的执行
    index


----------------------基本框架----------------------

WXYDB启动数据库软件
输入数据库命令，解析得到ParseResult
ParseResult执行excute，返回一个String说明结果
    对不同的指令执行不同的任务
    调用SystemManager执行DMLType
        CREATE, DROP, USE, SHOW, DESC
    调用RecordManager执行DDLType
        INSERT, DELETE, UPDATE, SELECT
        涉及Table，queryEngine来完成相关操作
可能根据执行结果String执行一些简单操作，例如退出


