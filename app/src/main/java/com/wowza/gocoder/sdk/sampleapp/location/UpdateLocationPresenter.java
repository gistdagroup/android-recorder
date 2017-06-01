package com.wowza.gocoder.sdk.sampleapp.location;

public class UpdateLocationPresenter implements IUpdateLocationPresenter {

    private LocationRepository locationRepository;

    public UpdateLocationPresenter() {
        this.locationRepository = new LocationRepository();
    }

    @Override
    public void updateLocation(LocationModel locationModel) {

        locationRepository.postLocation(locationModel);

    }
}
