package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");
        inputLogin();
        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(loginUser);
                        selectSubMenu();
                        break;
                    case "2":
                        inputNewInformation();
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    public void inputLogin() {
        while (true) {
            try {
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();
                System.out.print("パスワードを入力してください：");
                String pass = reader.readLine();

                //ログイン処理 失敗時呼び出し先から例外をスローし再認証
                loginUser = userLogic.login(email, pass);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    public void inputNewInformation() {
        try {
            while (true) {
                System.out.print("タスクコードを入力してください：");
                String code = reader.readLine();
                if (isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    continue;
                }

                System.out.print("タスク名を入力してください：");
                String task = reader.readLine();
                if (task.length() > 10) {
                    System.out.println("タスク名は10文字以内で入力してください");
                    continue;
                }

                System.out.print("担当するユーザーのコードを選択してください：");
                String user = reader.readLine();
                if (isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    continue;
                }
                try {
                    taskLogic.save(
                        Integer.parseInt(code),
                        task,
                        Integer.parseInt(user),
                        loginUser);
                    System.out.println("taskEの登録が完了しました。");
                    return;
                } catch (AppException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu() {
        while (true) {
            try {
                System.out.println("以下1~3から好きな選択肢を選んでください。");
                System.out.println("1. タスクのステータス変更, 2. タスク削除, 3. メインメニューに戻る");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        inputChangeInformation();
                        break;
                    case "2":
                        inputDeleteInformation();
                        break;
                    case "3":
                        return;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation() {
        try {
            while (true) {
                //タスクコード入力
                //バリデーション 半角数値
                System.out.print("ステータスを変更するタスクコードを入力してください：");
                String code = reader.readLine();
                if (isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    continue;
                }

                //ステータス入力
                //バリデーション 半角数値 規定数値範囲
                System.out.println("どのステータスに変更するか選択してください。");
                System.out.println("1. 着手中, 2. 完了");
                System.out.print("選択肢：");
                String status = reader.readLine();
                if (isNumeric(status)) {
                    System.out.println("ステータスは半角の数字で入力してください");
                    continue;
                } else if (Integer.parseInt(status) != 1 && Integer.parseInt(status) != 2) {
                    System.out.println("ステータスは1・2の中から選択してください");
                    continue;
                }

                //ステータス変更
                try {
                    taskLogic.changeStatus(
                        Integer.parseInt(code),
                        Integer.parseInt(status),
                        loginUser);
                    System.out.println("ステータスの変更が完了しました。");
                    return;
                } catch (AppException e) {
                    //例外がスローされた場合入力しなおし
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    public void inputDeleteInformation() {
        try {
            while (true) {
                //タスクコード入力
                //バリデーション 半角数値
                System.out.print("削除するタスクコードを入力してください：");
                String code = reader.readLine();
                if (isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    continue;
                }

                //ステータス削除
                try {
                    taskLogic.delete(Integer.parseInt(code));
                    return;
                } catch (AppException e) {
                    //例外がスローされた場合入力しなおし
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    public boolean isNumeric(String inputText) {
        return !inputText.chars().allMatch(c-> Character.isDigit((char) c));
    }
}