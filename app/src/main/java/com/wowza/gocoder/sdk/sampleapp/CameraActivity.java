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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wowza.gocoder.sdk.api.devices.WZCamera;
import com.wowza.gocoder.sdk.sampleapp.http.ApiAdapter;
import com.wowza.gocoder.sdk.sampleapp.http.StreamingGateway;
import com.wowza.gocoder.sdk.sampleapp.location.IUpdateLocationPresenter;
import com.wowza.gocoder.sdk.sampleapp.location.LocationModel;
import com.wowza.gocoder.sdk.sampleapp.location.UpdateLocationPresenter;
import com.wowza.gocoder.sdk.sampleapp.ui.AutoFocusListener;
import com.wowza.gocoder.sdk.sampleapp.ui.MultiStateButton;
import com.wowza.gocoder.sdk.sampleapp.ui.Payload;
import com.wowza.gocoder.sdk.sampleapp.ui.TimerView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends CameraActivityBase
        implements UpdateTimerListener, UpdateGPSListener, RecordListener {
    private final static String TAG = CameraActivity.class.getSimpleName();

    // UI controls
    protected MultiStateButton      mBtnSwitchCamera  = null;
    protected MultiStateButton      mBtnTorch         = null;
    protected TimerView             mTimerView        = null;

    // Gestures are used to toggle the focus modes
    protected GestureDetectorCompat mAutoFocusDetector = null;

    private String currentTime = "";
    private GPSLocation mLocation;

    private StreamingGateway mStreamingGateway;

    private IUpdateLocationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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

        mLocation.getLocation(LocationManager.GPS_PROVIDER);

        presenter = new UpdateLocationPresenter();

//        mStreamingGateway = ApiAdapter.createService(StreamingGateway.class);

    }

    private int sendPacket(String payload) {

        try {
            DatagramSocket udpSocket = new DatagramSocket(5000);
            InetAddress serverAddr = InetAddress.getByName("");
            byte[] buf = payload.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 3000);
            udpSocket.send(packet);
            udpSocket.disconnect();
            udpSocket.close();
        } catch (SocketException e) {
            Log.e(TAG, "Socket Error:", e);
        } catch (IOException e) {
            Log.e(TAG, "IO Error:", e);
        }

        return 0;
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

        /*Log.d(TAG, new Payload.Builder(this)
                .setLat(lat)
                .setLng(lng)
                .build()); */

        presenter.updateLocation(new LocationModel(this, lat, lng));

    }

    @Override
    public void connected() {
        Log.d(TAG, "connected");
        Call<Object> call = mStreamingGateway.record("file00.mp4", "startRecording");
        call.enqueue(callback);
    }

    @Override
    public void disconnected() {
        Log.d(TAG, "disconnected");
        Call<Object> call = mStreamingGateway.record("file00.mp4", "stopRecording");
        call.enqueue(callback);
    }

    Callback<Object> callback = new Callback<Object>() {

        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {
            Log.d(TAG, response.message());
        }

        @Override
        public void onFailure(Call<Object> call, Throwable t) {
            t.printStackTrace();
        }
    };

}
