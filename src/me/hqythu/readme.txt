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
    测试了原有的大部分内容
        系统管理模块,插入
    重新构思update,query
    设计query操作的位置

2015年12月25日
    实现primary key 检查
    实现null检查

下次开始
    Table的remove
        Where
    Table的update
        Where SetValue
    QueryEngine的query
        Where Tables

----------------------进度----------------------
已完成
    SystemManage及其测试
        创建、切换、删除数据库
        创建、删除、显示表
    Table及其测试
        insert,remove,update
    TablePage测试
    Record测试
    NULL的检查,主键的支持
    Where类的设计
    SQL大部分语句的解析
    表的联合TableJoin
        支持任意多表,但机器性能可能无法支持

    命令
        CREATE DATABASE orderDB; 创建名为 orderDB 的数据库
        DROP DATABASE orderDB; 删除名为 orderDB 的数据库
        USE orderDB; 当前数据库切换为 orderDB
        SHOW TABLES; 列出当前数据库包含的所有表
        DROP TABLE customer; 删除名为 customer 的表
        DESC customer; 列出该表的信息

未完成
    测试 INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’);
        如果主键出现重复，应报错
    测试 INSERT INTO orders VALUES (315000,200001,’eight’);
        如果数据类型不符合，应报错
    Query模块
        query, function, TableJoin
    主键的支持的测试
    Query测试
        query, function
    SQL语句的执行
    SQL基本功能展示
        三个表的联合查询例程
    GUI界面

    命令
        CREATE TABLE customer( id int(10) NOT NULL,
        name varchar(25) NOT NULL, gender varchar(1) NOT NULL, PRIMARY KEY(id));
            创建名为 customer 的表,它包含三个字段 id、name 和 gender,其中 id 是主键。
            这三 字段的数据类型分别为整型、字符串和字符串,并且都不允许为空。

        DELETE FROM publisher WHERE state=’CA’;
            删除所有加州的出版商
        UPDATE book SET title=’Nine Times Nine’ WHERE authors=’Anthony Boucher’;
            把作者 Anthony Boucher 的书的书名改为 Nine Times Nine

        SELECT * FROM publisher WHERE nation=’CA’; 列出所有加州出版商的信息。
        SELECT title FROM book WHERE authors is null; 列出 authors 字段为空的记录的书名。
        SELECT book.title,orders.quantity FROM book,orders WHERE book.id=orders.book_id AND orders.quantity>8;

可能存在的问题
    页式文件系统PageFile测试

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

技术细节
    DML
        Create DataBase, Drop DataBase, Use DataBase
            文件的创建,删除,打开
        ShowTables ShowTableColumns
            将SystemManager的tables字符串化
            将某个Table的columns字符串化
        CreateTable, DrowTable
    DDL
        Insert
            从第一个数据页开始找,找到空位置就插入
        remove
            需要Where
        update
            需要Where,setValue
        query
            需要Where

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
