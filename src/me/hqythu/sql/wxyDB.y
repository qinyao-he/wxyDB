
%%
SQL         : DDL
            | DML
            ;
            
DDL         : CREATE DATABASE db_name
            | DROP DATABASE db_name
            | USE dbname
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
附加模块
SELECT FUNC(Field) FROM table_name


SELECT table_name1.Field,table_name2.Field FROM table_name1,table_name2 WHERE Conditions
*/