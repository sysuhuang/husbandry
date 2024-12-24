package com.example.husbandrycloud.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.husbandrycloud.fragment.HusbandryData;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseTask {
    private static final String URL = "jdbc:mysql://rm-2zek77e992oi6l5fnbo.mysql.rds.aliyuncs.com:3306/husbandrycloud?useSSL=false";
    private static final String USER = "husbandrycloud";
    private static final String PASSWORD = "Njdyswd2010703";

    private ExecutorService executorService;
    private Handler mainHandler;
    private ResultListener listener;

    public interface ResultListener {
        void onqQueryResult(List<HusbandryData> result);

        void onqInsertResult(List<HusbandryData> result);

        void onqInsertFailed();

        void onqDeleteResult(Boolean success);
    }

    public DatabaseTask(ResultListener listener) {
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor(); // 创建单线程执行器
        this.mainHandler = new Handler(Looper.getMainLooper()); // 创建主线程处理器
    }

    public void queryTask(String usernameIndex, boolean getAllData) {
        String query = "";
        if (getAllData) {
            query = "SELECT * FROM `husbandrydata`";
        } else {
            query = String.format("SELECT * FROM `husbandrydata` WHERE `username` = '%s'", usernameIndex); // 替换为实际查询语句
        }


        String finalQuery = query;
        executorService.execute(() -> {
            List<HusbandryData> results = new ArrayList<>();

            try {
                // 加载 JDBC 驱动
                Class.forName("com.mysql.jdbc.Driver");
                // 建立连接
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(finalQuery);

                // 处理结果集
                while (resultSet.next()) {
                    String index = resultSet.getString("index");
                    String age = resultSet.getString("age");
                    String weight = resultSet.getString("weight");
                    String feedType = resultSet.getString("feedtype");
                    String foodIntake = resultSet.getString("foodintake");
                    String excretionRate = resultSet.getString("excretionrate");
                    String healthStatus = resultSet.getString("healthstatus");
                    String uri = resultSet.getString("uri");
                    String heartbeat = resultSet.getString("heartbeat");
                    String bloodPressure = resultSet.getString("bloodpressure");
                    String userName = resultSet.getString("username");
                    String enterpriseAdvice = resultSet.getString("enterpriseadvice");


                    HusbandryData data = new HusbandryData(index, age, weight, feedType, foodIntake,
                            excretionRate, healthStatus, uri, heartbeat,
                            bloodPressure, userName, enterpriseAdvice);
                    Log.e("~~~~", data.toString());

                    results.add(data);
                }

                // 在主线程中处理结果
                mainHandler.post(() -> {
                    if (listener != null) {
                        listener.onqQueryResult(results);
                    }
                });

                // 关闭连接
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                Log.i("~~~~", e.toString());
                e.printStackTrace();
            }


        });
    }

    public void insertData(HusbandryData data) {
        executorService.execute(() -> {
            try {
                // 加载 JDBC 驱动
                Class.forName("com.mysql.jdbc.Driver");
                // 建立连接
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                String insertQuery = "INSERT INTO `husbandrydata` (`index`, `age`, `weight`, `feedtype`, `foodintake`, `excretionrate`, `healthstatus`, `uri`, `heartbeat`, `bloodpressure`, `username` , `enterpriseadvice`)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, data.getIndex());
                    preparedStatement.setString(2, data.getAge());
                    preparedStatement.setString(3, data.getWeight());
                    preparedStatement.setString(4, data.getFeedType());
                    preparedStatement.setString(5, data.getFoodIntake());
                    preparedStatement.setString(6, data.getExcretionRate());
                    preparedStatement.setString(7, data.getHealthStatus());
                    preparedStatement.setString(8, data.getUri());
                    preparedStatement.setString(9, data.getHeartbeat());
                    preparedStatement.setString(10, data.getBloodPressure());
                    preparedStatement.setString(11, data.getUserName());
                    preparedStatement.setString(12, data.getEnterpriseAdvice());

                    // 执行插入操作
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    listener.onqInsertFailed();
                }

                // 关闭连接
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void deleteData(String index) {
        executorService.execute(() -> {
            boolean success = false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                String deleteQuery = "DELETE FROM `husbandrydata` WHERE `index` = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    preparedStatement.setString(1, index);
                    int rowsAffected = preparedStatement.executeUpdate();
                    success = rowsAffected > 0; // 判断是否删除成功
                } catch (Exception e) {
                    Log.e("DatabaseTask", "Delete failed: " + e.getMessage());
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 在主线程中回调删除结果
            boolean finalSuccess = success;
            mainHandler.post(() -> {
                if (listener != null) {
                    listener.onqDeleteResult(finalSuccess);
                }
            });
        });
    }

    public void modifyEnterpriseAdvice(String index, String dataToModify) {
        executorService.execute(() -> {
            boolean success = false;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

                // 使用字符串拼接构造 SQL 语句，确保 column 是安全的
                String updateQuery = "UPDATE `husbandrydata` SET `enterpriseadvice` = ? WHERE `index` = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    // 设置要修改的值
                    preparedStatement.setString(1, dataToModify);
                    preparedStatement.setString(2, index);

                    int rowsAffected = preparedStatement.executeUpdate();
                    success = rowsAffected > 0; // 判断更新是否成功
                } catch (Exception e) {
                    Log.e("DatabaseTask", "Update failed: " + e.getMessage());
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 在主线程中回调更新结果
            boolean finalSuccess = success;
            mainHandler.post(() -> {
                if (listener != null) {
                    listener.onqDeleteResult(finalSuccess); // 可以改为其他回调方法
                }
            });
        });
    }

    // 可选择在适当时机关闭 ExecutorService
    public void shutdown() {
        executorService.shutdown();
    }
}