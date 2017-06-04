package com.wowza.gocoder.sdk.sampleapp.record.presenter;

public interface IVideoRecordPresenter {

    void startRecord(String uuid);
    void stopRecord(String uuid, String referenceId, String filename);

}
