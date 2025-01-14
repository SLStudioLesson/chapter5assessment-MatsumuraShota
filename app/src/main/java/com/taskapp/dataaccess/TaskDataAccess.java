package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.taskapp.model.Task;

public class TaskDataAccess {

    private final String filePath;

    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> task = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //reader line temp
            String line = "";
            //ヘッダー回避
            reader.readLine();

            //""を読むまでループ
            while ((line = reader.readLine()) != null) {
                List<String> value = new ArrayList<>(Arrays.asList(line.split(",")));
                task.add(new Task(
                    Integer.parseInt(value.get(0)),
                    value.get(1),
                    Integer.parseInt(value.get(2)),
                    userDataAccess.findByCode(Integer.parseInt(value.get(3)))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return task;
    }

    /**
     * タスクをCSVに保存します。
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            //成形
            List<String> textList = new ArrayList<>(Arrays.asList(
                Integer.toString(task.getCode()),
                task.getName(),
                Integer.toString(task.getStatus()),
                Integer.toString(task.getRepUser().getCode())));
            
            //最後の行に書き込み
            writer.newLine();
            writer.write(String.join(",", textList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //read Line temp
            String line = "";
            //ヘッダー回避
            reader.readLine();

            //""を読むまでループ
            while ((line = reader.readLine()) != null) {
                //分解
                List<String> value = new ArrayList<>(Arrays.asList(line.split(",")));
                //codeが一致した場合Task型に成形して返す
                if (Integer.parseInt(value.get(0)) == code) {
                    return new Task(
                        Integer.parseInt(value.get(0)),
                        value.get(1),
                        Integer.parseInt(value.get(2)),
                        userDataAccess.findByCode(Integer.parseInt(value.get(3))));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return null;
    }

    /**
     * タスクデータを更新します。
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) {
        //csv内情報取得
        List<Task> task = findAll();
        List<String> textList = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            //ヘッダー書き込み
            writer.write("Code,Name,Status,Rep_User_Code");

            task.forEach(t -> {
                if (t.getCode() == updateTask.getCode()) {
                    textList.add(updateTask.getCode() + "," + updateTask.getName() + "," + updateTask.getStatus() + "," + updateTask.getRepUser().getCode());
                } else {
                    textList.add(t.getCode() + "," + t.getName() + "," + t.getStatus() + "," + t.getRepUser().getCode());
                }
            });

            //joinして書き込み
            writer.newLine();
            writer.write(String.join("\n", textList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * @param code 削除するタスクのコード
     */
    public void delete(int code) {
        //csv内情報取得
        List<Task> task = findAll();
        List<String> textList = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            //ヘッダー書き込み
            writer.write("Code,Name,Status,Rep_User_Code");

            //削除するcodeと一致しないTaskをadd
            for (Task t : task){
                if (t.getCode() != code) {
                    textList.add(t.getCode() + "," + t.getName() + "," + t.getStatus() + "," + t.getRepUser().getCode());
                }
            }

            //joinして書き込み
            writer.write(String.join("\n", textList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    // private String createLine(Task task) {
    // }
}