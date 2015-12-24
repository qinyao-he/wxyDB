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
    单元测试框架


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
    primary key 的支持
    null的检查
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

可以实现的功能
    CREATE DATABASE orderDB; 创建名为 orderDB 的数据库
    DROP DATABASE orderDB; 删除名为 orderDB 的数据库
    USE orderDB; 当前数据库切换为 orderDB
    SHOW TABLES; 列出当前数据库包含的所有表
    DROP TABLE customer; 删除名为 customer 的表

未能实现的功能
    主键的支持
    null的检查

    CREATE TABLE customer( id int(10) NOT NULL,
    name varchar(25) NOT NULL, gender varchar(1) NOT NULL, PRIMARY KEY(id)); 创建名为 customer 的表,它包含三个字段 id、name 和 gender,其中 id 是主键。这三个 字段的数据类型分别为整型、字符串和字符串,并且都不允许为空。
    DESC customer; 列出该表的信息
    primary key主键的支持

    INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’); 
    如果主键出现重复，应报错
    INSERT INTO orders VALUES (315000,200001,’eight’);
    如果数据类型不符合，应报错
    DELETE FROM publisher WHERE state=’CA’; 删除所有加州的出版商。
    UPDATE book SET title=’Nine Times Nine’ WHERE authors=’Anthony Boucher’; 把作者 Anthony Boucher 的书的书名改为 Nine Times Nine

    SELECT * FROM publisher WHERE nation=’CA’; 列出所有加州出版商的信息。
    SELECT title FROM book WHERE authors is null; 列出 authors 字段为空的记录的书名。
    SELECT book.title,orders.quantity FROM book,orders WHERE book.id=orders.book_id AND orders.quantity>8;
    
----------------------基本框架----------------------
业务逻辑
    1、WXYDB启动数据库软件
    2、输入数据库命令，解析得到ParseResult
    3、ParseResult执行excute，返回一个String说明结果
        对不同的指令执行不同的任务
        调用SystemManager执行DMLType
            CREATE, DROP, USE, SHOW, DESC
        调用RecordManager、QueryEngine执行DDLType
            INSERT, DELETE, UPDATE, SELECT
            涉及Table，Record，querySet来完成相关操作
    4、可能根据执行结果String执行一些简单操作，例如退出

文件框架
    WXYDB
        manager sql
            object
                pagefile

层次框架
    WXYDB
        SQLParser
        SystemManager
        RecordManager
        QueryEngine
    object为manager,engine提供操作对象
    pagefile为object提供底层页面操作
