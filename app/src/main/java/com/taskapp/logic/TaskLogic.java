package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        //全task取得
        List<Task> task = taskDataAccess.findAll();

        //出力
        task.forEach(t -> {
            //変換
            String reqUser = "あなたが担当しています";
            if (!t.getRepUser().equals(loginUser)) {
                reqUser = t.getRepUser().getName() + "が担当しています";
            }
            String status = switch (t.getStatus()) {
                case 0 -> "未着手";
                case 1 -> "着手中";
                case 2 -> "完了";
                default -> "";
            };
            
            //出力
            System.out.print(t.getCode() + ". ");
            System.out.print("タスク名：" + t.getName());
            System.out.print(", 担当者名：" + reqUser);
            System.out.println(", ステータス：" + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        
        //存在するユーザーか確認
        User user = userDataAccess.findByCode(repUserCode);
        if (user == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        //セーブ
        taskDataAccess.save(new Task(code, name, 0, user));
        //ロガー
        logDataAccess.save(new Log(code, loginUser.getCode(), 0, LocalDate.now()));
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
        
        
        //タスクコード存在・ステータス確認
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        } else if (task.getStatus() + 1 != status) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        //アップデート
        taskDataAccess.update(new Task(code, task.getName(), status, task.getRepUser()));
        //ロガー
        logDataAccess.save(new Log(code, loginUser.getCode(), status, LocalDate.now()));
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    public void delete(int code) throws AppException {
        
        //タスクコード存在・ステータス完了確認
        Task task = taskDataAccess.findByCode(code);
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        } else if (task.getStatus() != 2) {
            throw new AppException("ステータスが完了のタスクを選択してください");
        }

        //アップデート
        taskDataAccess.delete(code);
        //ログから該当コードのlogを削除
        logDataAccess.deleteByTaskCode(code);
    }
}