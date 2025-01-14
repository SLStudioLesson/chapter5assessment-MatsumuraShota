package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.taskapp.model.Log;

public class LogDataAccess {
    private final String filePath;


    public LogDataAccess() {
        filePath = "app/src/main/resources/logs.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public LogDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * ログをCSVファイルに保存します。
     *
     * @param log 保存するログ
     */
    public void save(Log log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            //成形
            List<String> textList = new ArrayList<>(Arrays.asList(
                Integer.toString(log.getTaskCode()),
                Integer.toString(log.getChangeUserCode()),
                Integer.toString(log.getStatus()),
                log.getChangeDate().toString()));
            
            //最後の行に書き込み
            writer.newLine();
            writer.write(String.join(",", textList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * すべてのログを取得します。
     *
     * @return すべてのログのリスト
     */
    public List<Log> findAll() {
        List<Log> log = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //reader line temp
            String line = "";
            //ヘッダー回避
            reader.readLine();

            //""を読むまでループ
            while ((line = reader.readLine()) != null) {
                List<String> value = new ArrayList<>(Arrays.asList(line.split(",")));
                List<String> dateList = new ArrayList<>(Arrays.asList(value.get(3).split("-")));
                log.add(new Log(
                    Integer.parseInt(value.get(0)),
                    Integer.parseInt(value.get(1)),
                    Integer.parseInt(value.get(2)),
                    LocalDate.of(
                        Integer.parseInt(dateList.get(0)),
                        Integer.parseInt(dateList.get(1)),
                        Integer.parseInt(dateList.get(2))
                    )
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log;
    }

    /**
     * 指定したタスクコードに該当するログを削除します。
     *
     * @see #findAll()
     * @param taskCode 削除するログのタスクコード
     */
    public void deleteByTaskCode(int taskCode) {
        //csv内情報取得
        List<Log> log = findAll();
        List<String> textList = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            //ヘッダー書き込み
            writer.write("Task_Code,Change_User_Code,Status,Change_Date");

            //削除するcodeと一致しないlogをadd
            for (Log l : log){
                if (l.getTaskCode() != taskCode) {
                    textList.add(l.getTaskCode() + "," + l.getChangeUserCode() + "," + l.getStatus() + "," + l.getChangeDate());
                }
            }

            //joinして書き込み
            writer.write(String.join("\n", textList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ログをCSVファイルに書き込むためのフォーマットを作成します。
     *
     * @param log フォーマットを作成するログ
     * @return CSVファイルに書き込むためのフォーマット
     */
    // private String createLine(Log log) {
    // }

}