package me.hqythu;

import me.hqythu.exception.SQLExecException;
import me.hqythu.exception.SQLParserException;
import me.hqythu.record.BufPageManager;
import me.hqythu.sql.SQL;
import me.hqythu.sql.SQLParser;
import me.hqythu.system.SystemManager;

import java.util.Scanner;

public class WXYDB {

    private BufPageManager bufPageManager;
    private SystemManager systemManager;

    public WXYDB() {
        bufPageManager = BufPageManager.getInstance();
        systemManager = new SystemManager();
    }

    public void go() {
        Scanner scanner = new Scanner(System.in);
        while(true) {

            // 读命令
            String sqlString = scanner.nextLine();
            String result;

            // 解析执行命令
            try {
                SQL sql = SQLParser.parse(sqlString);
                result = sql.execute();
            } catch (SQLParserException e) {
                result = e.getMessage();
                e.printStackTrace();
            } catch (SQLExecException e) {
                result = e.getMessage();
                e.printStackTrace();
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
