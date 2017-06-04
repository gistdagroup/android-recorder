package com.wowza.gocoder.sdk.sampleapp.streamer;

public class StreamerPresenter implements IStreamerPresenter {

    private StreamerRepository repository;

    private static final String DEFAULT_FILENAME = "default.mp4";
    private String filename;

    public StreamerPresenter() {
        this.repository = new StreamerRepository();
        this.filename = DEFAULT_FILENAME;
    }

    @Override
    public void start(String filename) {

        this.filename = filename;

        repository.start(this.filename);

    }

    @Override
    public void stop() {

        repository.stop(filename);

    }
}
