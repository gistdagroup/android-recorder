package com.wowza.gocoder.sdk.sampleapp.record.viewmodel;

public class VideoRecordModel {

    public boolean isFinish = false;

    public String uuid;
    public String path;
    public String id;

    public VideoRecordModel(boolean isFinish, String uuid, String path) {
        this.isFinish = isFinish;
        this.uuid = uuid;
        this.path = path;
    }

    public VideoRecordModel(boolean isFinish, String uuid, String path, String referenceId) {
        this.isFinish = isFinish;
        this.uuid = uuid;
        this.path = path;
        this.id = referenceId;
    }
}
