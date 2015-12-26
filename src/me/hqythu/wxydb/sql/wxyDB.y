
%%
SQL         : DDL
            | DML
            ;
            
DDL         : CREATE DATABASE db_name
            | DROP DATABASE db_name
            | USE db_name
            | SHOW TABLES
            | CREATE TABLE table_name '(' NewFields ')'
            | DROP TABLE table_name
            | DESC table_name
            ;

NewFields   : NewFields ',' NewField
            | NewField
            ;

NewField    : new_field_name TYPE NOT NULL
            ;



DML         : INSERT INTO table_name VALUES (Values)
            | DELETE FROM table_name WHERE Conditions
            | UPDATE table_name SET Field'='Value WHERE Conditions
            | SELECT * FROM table_name WHERE Conditions
            | SELECT Fields FROM table_name WHERE Conditions
            | SELECT Fields FROM table_names WHERE Conditions
            ;

Values      : Values ','Value
            | Value
            ;

Value       : INT
            | STRING
            | CHAR
            ;

Conditions  : Conditions LogicOp Conditions
            | '(' Conditions ')'
            | Condition
            ;

Condition   : Field ArithOp Value
            | '(' Condition ')'
            ;

Field       : table_name '.' field_name
            | field_name
            ;

%%
/*
必须实现
    两个表的连接
        SELECT table_name1.Field,table_name2.Field FROM table_name1,table_name2 WHERE Conditions

可选实现
    三个表以上的连接
        SELECT Fields FROM Tables WHERE Conditions

    聚类查询
        SELECT FUNC(Field) FROM table_name

    索引模块
        CREATE INDEX customer(name);
            为 customer 表的 name 字段创建索引。
        DROP INDEX customer(name);
            删除 customer 表的 name 字段的索引。
*/
