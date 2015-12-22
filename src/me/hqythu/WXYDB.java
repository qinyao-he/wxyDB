package me.hqythu;

import me.hqythu.exception.SQLExecException;
import me.hqythu.exception.SQLParserException;
import me.hqythu.PageFile.BufPageManager;
import me.hqythu.sql.ParseResult;
import me.hqythu.sql.SQL;
import me.hqythu.sql.SQLParser;
import me.hqythu.system.SystemManager;

import java.util.Scanner;

public class WXYDB {

    public WXYDB() {
        BufPageManager.getInstance(); // 加载类，完成初始化
        SystemManager.getInstance();  // 加载类，完成初始化
    }

    public void go() {
        Scanner scanner = new Scanner(System.in);
        while(true) {

            // 读命令
            String sqlString = scanner.nextLine();
            String result;

            // 解析执行命令
            try {
                ParseResult sql = SQLParser.parse(sqlString);
                result = sql.execute();
            } catch (Exception e) {
                result = e.getMessage();
                e.printStackTrace();
            }

            // 执行命令的反馈
            if (result.equals("quit")) {
                break;
            }
            switch (result) {
                case "option1":
                    System.out.println("1");
                    break;
                case "option2":
                    System.out.println("2");
                    break;
                default:
                    System.out.println(result);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        WXYDB db = new WXYDB();
        db.go();
    }
}
