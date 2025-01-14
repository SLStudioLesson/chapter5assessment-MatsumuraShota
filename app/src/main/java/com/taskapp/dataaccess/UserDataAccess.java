package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * @param email メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //read Line temp
            String line = "";
            //ヘッダー回避
            reader.readLine();

            //""を読むまでループ
            while ((line = reader.readLine()) != null) {
                //分解
                List<String> value = new ArrayList<>(Arrays.asList(line.split(",")));
                //email・passが一致した場合User型に成形して返す
                if (value.get(2).equals(email) && value.get(3).equals(password)) {
                    return new User(
                        Integer.parseInt(value.get(0)),
                        value.get(1),
                        value.get(2),
                        value.get(3));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return null;
    }

    /**
     * コードを基にユーザーデータを取得します。
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //read Line temp
            String line = "";
            //ヘッダー回避
            reader.readLine();

            //""を読むまでループ
            while ((line = reader.readLine()) != null) {
                //分解
                List<String> value = new ArrayList<>(Arrays.asList(line.split(",")));
                //codeが一致した場合User型に成形して返す
                if (Integer.parseInt(value.get(0)) == code) {
                    return new User(
                        Integer.parseInt(value.get(0)),
                        value.get(1),
                        value.get(2),
                        value.get(3));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();;
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return null;
    }
}
