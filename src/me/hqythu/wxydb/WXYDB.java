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
        List<String> results = new ArrayList<>();
        File file  = new File((fileName));
        if (!file.exists()) return results;
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNext()) {
                String temp = scanner.nextLine();
                builder.append(temp);
                if (temp.indexOf(';') != -1) {
                    String sql = builder.toString();
//                    System.out.println(sql);
                    ParseResult parseResult = SQLParser.parse(sql);
                    String result = parseResult.execute();
                    results.add(result);
                    builder.setLength(0);
                }
            }
        } catch (Exception e) {
            results.add("error at here");
        }
        return results;
    }

    public void writeBack() {
        BufPageManager.getInstance().clear();
    }

    public static void main(String[] args) {
        WXYDB db = new WXYDB();
        db.console();
    }
}
