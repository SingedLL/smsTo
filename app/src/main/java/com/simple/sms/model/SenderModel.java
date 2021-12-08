package com.simple.sms.model;


import com.simple.sms.R;

public class SenderModel {
    private Long id;
    private String name;

    public static final int STATUS_ON = 1;
    public static final int STATUS_OFF = 0;
    private int status;

    public static final int TYPE_EMAIL = 0;
    public static final int TYPE_MESSAGE = 1;

    private int type;

    private String jsonSetting;

    private long time;

    public SenderModel() {
    }

    public SenderModel(String name, int status, int type, String jsonSetting) {
        this.name = name;
        this.status = status == STATUS_ON ? STATUS_ON : STATUS_OFF;
        this.type = type;
        this.jsonSetting = jsonSetting;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status == STATUS_ON ? STATUS_ON : STATUS_OFF;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJsonSetting() {
        return jsonSetting;
    }

    public void setJsonSetting(String jsonSetting) {
        this.jsonSetting = jsonSetting;
    }

    public int getImageId() {
        switch (type) {
            case TYPE_EMAIL:
                return R.drawable.ic_baseline_email_24;
            case TYPE_MESSAGE:   
                return R.drawable.ic_baseline_email_24;  
            default:
                return R.mipmap.ic_launcher;

        }
    }

    public static int getImageId(int type) {
        switch (type) {
            case TYPE_EMAIL:
                return R.drawable.ic_baseline_email_24;
            case TYPE_MESSAGE:   
                return R.drawable.ic_baseline_email_24;  
            default:
                return R.mipmap.ic_launcher;

        }
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SenderModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", jsonSetting='" + jsonSetting + '\'' +
                ", time=" + time +
                '}';
    }
}
