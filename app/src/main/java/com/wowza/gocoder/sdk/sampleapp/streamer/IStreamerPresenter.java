package com.wowza.gocoder.sdk.sampleapp.streamer;

public interface IStreamerPresenter {

    void start(String filename, String streamName);
    void stop();

}
