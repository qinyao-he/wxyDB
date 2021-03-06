package me.hqythu.wxydb;

import me.hqythu.wxydb.manager.QueryEngine;
import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.pagefile.BufPageManager;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import me.hqythu.wxydb.manager.SystemManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WXYDB {

    public WXYDB() {
        BufPageManager.getInstance(); // 加载类，完成初始化
        SystemManager.getInstance();  // 加载类，完成初始化
        RecordManager.getInstance(); // 加载类，完成初始化
        QueryEngine.getInstance(); // 加载类，完成初始化
    }

    public void console() {
        Scanner scanner = new Scanner(System.in);
        while (true) {

            // 读命令
            String sqlString = scanner.nextLine();
            String result;

            // 执行命令的反馈
            if (sqlString.equals("quit")) {
                break;
            }

            // 解析执行命令
            try {
                ParseResult sql = SQLParser.parse(sqlString);
                result = sql.execute();
            } catch (Exception e) {
                result = e.getMessage();
//                e.printStackTrace();
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
        System.out.println("have fun! :)");
        SystemManager.getInstance().close();
    }

    /**
     * 执行一条SQL
     */
    public String excute(String sqlString) {
        String result;
        try {
            ParseResult sql = SQLParser.parse(sqlString);
            result = sql.execute();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }

    public List<String> excuteFile(String fileName) {
        return excuteFile(fileName, Integer.MAX_VALUE);
    }

    /**
     * 执行一个SQL语句文件
     */
    public List<String> excuteFile(String fileName, int max) {
        List<String> results = new ArrayList<>();
        File file  = new File((fileName));
        if (!file.exists()) return results;
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            int count = 0;
            while (scanner.hasNext()) {
                String temp;
                try {
                    temp = scanner.nextLine();
                } catch (Exception e) {
                    break;
                }
                builder.append(temp);
                if (temp.indexOf(';') != -1) {
                    String sql = builder.toString();
                    ParseResult parseResult = SQLParser.parse(sql);
                    String result = parseResult.execute();
                    results.add(result);
                    builder.setLength(0);
                    count++;
                    if (count >= max) break;
                }
            }
        } catch (Exception e) {
            results.add(e.getMessage());
        }
        return results;
    }

    /**
     * 写回
     * 由于Java的析构比较迷,目前设置为,在结束使用后,手动写回
     */
    public void writeBack() {
        BufPageManager.getInstance().clear();
    }

    public static void main(String[] args) {
        WXYDB db = new WXYDB();
        db.console();
    }
}
