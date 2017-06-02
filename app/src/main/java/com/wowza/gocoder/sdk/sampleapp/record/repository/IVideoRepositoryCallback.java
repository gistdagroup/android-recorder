package com.wowza.gocoder.sdk.sampleapp.record.repository;

import com.wowza.gocoder.sdk.sampleapp.record.viewmodel.VideoRecordModel;

public interface IVideoRepositoryCallback {

    void recordStarted(VideoRecordModel videoRecordModel);

}
