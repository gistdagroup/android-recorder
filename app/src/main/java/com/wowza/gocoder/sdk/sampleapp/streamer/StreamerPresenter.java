package com.wowza.gocoder.sdk.sampleapp.streamer;

public class StreamerPresenter implements IStreamerPresenter {

    private StreamerRepository repository;

    private static final String DEFAULT_FILENAME = "default";
    private static final String DEFAULT_STREAM_NAME = "myStream";

    private String filename;
    private String streamName;

    public StreamerPresenter() {
        this.repository = new StreamerRepository();
        this.filename = DEFAULT_FILENAME;
        this.streamName = DEFAULT_STREAM_NAME;
    }

    @Override
    public void start(String filename, String streamName) {

        this.filename = filename;
        this.streamName = streamName;

        repository.start(this.filename, this.streamName);

    }

    @Override
    public void stop() {

        repository.stop(filename, this.streamName);

    }
}
