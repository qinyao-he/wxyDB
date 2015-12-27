查询解析模块

输入一个字符串，返回一个SQL

照着《数据库课程项目测试用例》，写出语法

DML
    SystemManager里函数参数，解析获得
DDL
    RecordManager里函数参数，解析获得

查询结果说明：
    type：该语句的类型
    对于每种类型：
    INSERT：
        tableNames.get(0)：表名
        data：表示其中的数据，类型为Object
    DELETE：
        tableNames.get(0)：表名
        conditions：条件树的根节点
        具体内容为：
        例：对于a<0 or b>1 and c is null：
                    OR
                  /     \
                AND     c is null
              /     \
            a<0     b>1
        其中的条件为Condition类型，left为左标识符，middle为符号，right为内容
    UPDATE：
        values：表示其中的set后面的值，类型为Value，left为标识符，right为值
        conditions：条件树的根节点
    SELECT：
        rowNames：表示列名，[0]为*的话表示全选
        tableNames：表名
        conditions：条件树的根节点
    CREATE_DATABASE：
        dataBaseName：数据库名
    DROP_DATABASE：
        dataBaseName：数据库名
    USE：
        dataBaseName：数据库名
    SHOW_TABLES：
        无
    DROP_TABLE:
        tableNames.get(0)：表名
    DESC:
        tableNames.get(0)：表名
    ERROR:
        语句错误

查询结果说明 by lxhlxhlxh555：
    ParseResult
    type：该语句的类型
    对于每种类型：
    INSERT：
        tableName：表名
        List<Object> data：表示其中的数据
    DELETE：
        tableName：表名
        Where
            public List<Boolean> isExprs; // 这个容易漏!!!
            public List<Object> boolExprsAndOps;
                BoolExpr
                BoolOp
    UPDATE：
        tableName：表名
        List<SetValue>: 用于设置值
            赋值常数: public SetValue(String columnName, Object value)
            带参数: public SetValue(String columnName, CalcOp op, Object value, boolean left)
        Where
    SELECT：
        SelectOption:查询的列
            List<String> tableNames
            List<String> columnNames
        Where
    CREATE_DATABASE：
        dataBaseName：数据库名
    DROP_DATABASE：
        dataBaseName：数据库名
    USE：
        dataBaseName：数据库名
    SHOW_TABLES:
        无
    CREATE_TABLE:
        tableName：表名
        List<Column> columns
    DROP_TABLE:
        tableName：表名
    DESC:
        tableNames：表名
    ERROR:
        语句错误


Where的结构
    where条件
    内部类
        boolOp 布尔操作:and,or
        CalcOp 计算操作:==,!=,<,<=,>,>=,is
        BoolExpr 布尔表达式 LValue, CompareOp, RValue
    解析得到一个逆波兰表达式
        List<BoolExpr> boolExprs = new ArrayList<>();
        List<boolOp> boolOps = new ArrayList<>();

setValue
    用于update
    解析得到List<setValue>

完成测试
    CREATE_DATABASE
    DROP_DATABASE
    USE
    DROP_TABLE
    SHOW_TABLES
    DESC
    INSERT

未测试
    DELETE
    UPDATE
    SELECT
    ERROR
