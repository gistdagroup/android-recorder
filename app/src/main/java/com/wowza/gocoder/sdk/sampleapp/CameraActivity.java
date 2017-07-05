/**
 *  This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 *  purpose of educating developers, and is not intended to be used in any production environment.
 *
 *  IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 *  OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 *  EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 *  WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *  Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 */

package com.wowza.gocoder.sdk.sampleapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.sampleapp.location.IUpdateLocationPresenter;
import com.wowza.gocoder.sdk.sampleapp.location.LocationModel;
import com.wowza.gocoder.sdk.sampleapp.location.UpdateLocationPresenter;
import com.wowza.gocoder.sdk.sampleapp.record.presenter.IVideoRecordPresenter;
import com.wowza.gocoder.sdk.sampleapp.record.presenter.VideoRecordPresenter;
import com.wowza.gocoder.sdk.sampleapp.record.viewmodel.IVideoRecordView;
import com.wowza.gocoder.sdk.sampleapp.streamer.IStreamerPresenter;
import com.wowza.gocoder.sdk.sampleapp.streamer.StreamerPresenter;
import com.wowza.gocoder.sdk.sampleapp.ui.AutoFocusListener;
import com.wowza.gocoder.sdk.sampleapp.ui.MultiStateButton;
import com.wowza.gocoder.sdk.sampleapp.ui.TimerView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends CameraActivityBase
        implements UpdateTimerListener, UpdateGPSListener, RecordListener, IVideoRecordView {


    private final static String TAG = CameraActivity.class.getSimpleName();

    // UI controls
    protected MultiStateButton      mBtnSwitchCamera  = null;
    protected MultiStateButton      mBtnTorch         = null;
    protected TimerView             mTimerView        = null;

    // Gestures are used to toggle the focus modes
    protected GestureDetectorCompat mAutoFocusDetector = null;

    private String currentTime = "";
    private GPSLocation mLocation;

    private IStreamerPresenter streamerPresenter;
    private IUpdateLocationPresenter presenter;
    private IVideoRecordPresenter videoRecordPresenter;

    private String referenceId;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wz_live_host_address", ApiAdapter.HOST_NAME);
        editor.putString("wz_live_stream_name", Utility.getUUID(this));
        editor.apply();

        mRequiredPermissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };

        // Initialize the UI controls
        mBtnTorch           = (MultiStateButton) findViewById(R.id.ic_torch);
        mBtnSwitchCamera    = (MultiStateButton) findViewById(R.id.ic_switch_camera);
        mTimerView          = (TimerView) findViewById(R.id.txtTimer);
        mTimerView.setUpdateTimerListener(this);

        mLocation = new GPSLocation(this);
        mLocation.setUpdateGPSListener(this);

        requestGPSLocation();

        presenter = new UpdateLocationPresenter();

        videoRecordPresenter = new VideoRecordPresenter(this);

        streamerPresenter = new StreamerPresenter();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case GPSLocation.REQUEST_PERMISSION_CODE : {

                requestGPSLocation();

                break;
            }

        }

    }

    private void requestGPSLocation() {

        mLocation.getLocation(LocationManager.GPS_PROVIDER);

    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (sGoCoderSDK != null && mWZCameraView != null) {
            if (mAutoFocusDetector == null)
                mAutoFocusDetector = new GestureDetectorCompat(this, new AutoFocusListener(this, mWZCameraView));

            WZCamera activeCamera = mWZCameraView.getCamera();
            if (activeCamera != null && activeCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                activeCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);
        }

        if (mStatusView != null) {
            mStatusView.setRecordListener(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Click handler for the switch camera button
     */
    public void onSwitchCamera(View v) {
        if (mWZCameraView == null) return;

        mBtnTorch.setState(false);
        mBtnTorch.setEnabled(false);

        WZCamera newCamera = mWZCameraView.switchCamera();
        if (newCamera != null) {
            if (newCamera.hasCapability(WZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WZCamera.FOCUS_MODE_CONTINUOUS);

            boolean hasTorch = newCamera.hasCapability(WZCamera.TORCH);
            if (hasTorch) {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }
        }
    }

    /**
     * Click handler for the torch/flashlight button
     */
    public void onToggleTorch(View v) {
        if (mWZCameraView == null) return;

        WZCamera activeCamera = mWZCameraView.getCamera();
        activeCamera.setTorchOn(mBtnTorch.toggleState());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Update the state of the UI controls
     */
    @Override
    protected boolean syncUIControlState() {
        boolean disableControls = super.syncUIControlState();

        if (disableControls) {
            mBtnSwitchCamera.setEnabled(false);
            mBtnTorch.setEnabled(false);
        } else {
            boolean isDisplayingVideo = (getBroadcastConfig().isVideoEnabled() && mWZCameraView.getCameras().length > 0);
            boolean isStreaming = getBroadcast().getStatus().isRunning();

            if (isDisplayingVideo) {
                WZCamera activeCamera = mWZCameraView.getCamera();

                boolean hasTorch = (activeCamera != null && activeCamera.hasCapability(WZCamera.TORCH));
                mBtnTorch.setEnabled(hasTorch);
                if (hasTorch) {
                    mBtnTorch.setState(activeCamera.isTorchOn());
                }

                mBtnSwitchCamera.setEnabled(mWZCameraView.getCameras().length > 0);
                //mBtnSwitchCamera.setEnabled(mWZCameraView.isSwitchCameraAvailable());
            } else {
                mBtnSwitchCamera.setEnabled(false);
                mBtnTorch.setEnabled(false);
            }

            if (isStreaming && !mTimerView.isRunning()) {
                mLocation.setUpdateGPSListener(this);
                mTimerView.startTimer();
            } else if (getBroadcast().getStatus().isIdle() && mTimerView.isRunning()) {
                mLocation.setUpdateGPSListener(null);
                mTimerView.stopTimer();
            } else if (!isStreaming) {
                mTimerView.setVisibility(View.GONE);
            }
        }

        return disableControls;
    }

    @Override
    public void update(String currentTime) {
        Log.d(TAG, "update currentTime: "+ currentTime);
        this.currentTime = currentTime;
    }

    @Override
    public void updateLocation(String lat, String lng) {

        presenter.updateLocation(new LocationModel(this, lat, lng));

    }

    @Override
    public void connected() {
        Log.d(TAG, "connected");

        streamerPresenter.start(generateFileName(), Utility.getUUID(this));

        videoRecordPresenter.startRecord(Utility.getUUID(this));
    }

    @Override
    public void disconnected() {
        Log.d(TAG, "disconnected");

        streamerPresenter.stop();

        videoRecordPresenter.stopRecord(Utility.getUUID(this), this.referenceId, this.filename);
    }

    @Override
    public void recordStarted(String referenceId) {

        this.referenceId = referenceId;

    }

    private String generateFileName() {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
            Date data = new Date(System.currentTimeMillis());
            return this.filename = "source1-" + sdf.format(data);
        }catch (Exception ex) {
            return this.filename = "";
        }

    }
}
