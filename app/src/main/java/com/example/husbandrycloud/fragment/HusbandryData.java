package com.example.husbandrycloud.fragment;

import java.io.Serializable;

public class HusbandryData implements Serializable {
    private String index;
    private String age;
    private String weight;
    private String feedType;
    private String foodIntake;
    private String excretionRate;
    private String healthStatus;
    private String uri;
    private String heartbeat;
    private String bloodPressure;

    private String userName;

    // 更新构造函数
    public HusbandryData(String index, String age, String weight, String feedType, String foodIntake,
                         String excretionRate, String healthStatus, String uri, String heartbeat,
                         String bloodPressure, String userName) {
        this.index = index;
        this.age = age;
        this.weight = weight;
        this.feedType = feedType;
        this.foodIntake = foodIntake;
        this.excretionRate = excretionRate;
        this.healthStatus = healthStatus;
        this.uri = uri;
        this.heartbeat = heartbeat;
        this.bloodPressure = bloodPressure;
        this.userName = userName;
    }

    public String getIndex() {
        return index;
    }

    public String getAge() {
        return age;
    }

    public String getWeight() {
        return weight;
    }

    public String getFeedType() {
        return feedType;
    }

    public String getFoodIntake() {
        return foodIntake;
    }

    public String getExcretionRate() {
        return excretionRate;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getUri() {
        return uri;
    }

    public String getHeartbeat() {
        return heartbeat;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "HusbandryData{" +
                "index='" + index + '\'' +
                ", age='" + age + '\'' +
                ", weight='" + weight + '\'' +
                ", feedType='" + feedType + '\'' +
                ", foodIntake='" + foodIntake + '\'' +
                ", excretionRate='" + excretionRate + '\'' +
                ", healthStatus='" + healthStatus + '\'' +
                ", uri='" + uri + '\'' +
                ", heartbeat='" + heartbeat + '\'' +
                ", bloodPressure='" + bloodPressure + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}