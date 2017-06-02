package com.wowza.gocoder.sdk.sampleapp.record.presenter;

import com.wowza.gocoder.sdk.sampleapp.record.viewmodel.IVideoRecordView;
import com.wowza.gocoder.sdk.sampleapp.record.repository.IVideoRepositoryCallback;
import com.wowza.gocoder.sdk.sampleapp.record.viewmodel.VideoRecordModel;
import com.wowza.gocoder.sdk.sampleapp.record.repository.VideoRecordRepository;

public class VideoRecordPresenter implements IVideoRecordPresenter, IVideoRepositoryCallback {

    private VideoRecordRepository repository;
    private IVideoRecordView view;

    public VideoRecordPresenter(IVideoRecordView view) {
        this.repository = new VideoRecordRepository(this);
        this.view = view;
    }

    @Override
    public void startRecord(String uuid) {

        repository.startRecord(new VideoRecordModel(false, uuid, ""));

    }

    @Override
    public void recordStarted(VideoRecordModel videoRecordModel) {

        view.recordStarted(videoRecordModel.id);

    }

    @Override
    public void stopRecord(String uuid, String referenceId) {

        repository.stopRecord(new VideoRecordModel(true, uuid, "done", referenceId));

    }

}
