package com.fusionetics.plugins.bodymap;

import com.fusionetics.plugins.bodymap.ThisPlugin;
// import com.fusionetics.plugins.bodymap.AutoFitTextureView;
import com.fusionetics.plugins.bodymap.BodymapEventHandler;
import com.fusionetics.plugins.bodymap.Objects.Video;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
// import android.content.Context;
// import android.content.pm.PackageManager;
// import android.content.res.Configuration;
// import android.graphics.Matrix;
// import android.graphics.RectF;
// import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
// import android.os.HandlerThread;
import android.util.Log;
// import android.util.Size;
// import android.util.SparseIntArray;
import android.view.LayoutInflater;
// import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
// import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
// import android.widget.Toast;

import java.io.File;
// import java.io.IOException;
import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
import java.util.HashMap;

public class PreviewFragment extends Fragment
implements View.OnClickListener
// implements
    //     SurfaceHolder.Callback
    //     , OnPreparedListener
    //     , OnVideoSizeChangedListener
// OnBufferingUpdateListener, OnCompletionListener,
{
    private int _fragmentId = 0;

    private Activity activity = null;
    private BodymapEventHandler eventHandler = null;
    // private SurfaceView mPreview;
    // private SurfaceHolder holder;
    private Bundle extras;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private MediaPlayer mMediaPlayer;
    private Video video;
    private VideoView videoPlayer;
    private int playButtonId = 0;
    private int saveButtonId = 0;
    private int retakeButtonId = 0;
    private int reviewButtonsId = 0;
    private int cancelButtonId = 0;
    private ImageButton cancelButton;
    private View reviewButtons;
    private int determinateBarId = 0;
    private ProgressBar determinateBar;
    private View progressBarLayout;
    private TextView progressBarText;
    ArrayList<String> strings;

    // private MediaController mediaController;

    PreviewFragment(Activity _activity, BodymapEventHandler _eventHandler, Video video) {
        this.activity = _activity;
        this.eventHandler = _eventHandler;
        this.video = video;

        strings = new ArrayList<String>();
        strings.add("Measuring jump height");
        strings.add("Blowing up basket balls");
        strings.add("Painting free throw lines");
        strings.add("Hanging nets");
        strings.add("Testing arena acoustics");
        strings.add("Relacing sneakers");
        strings.add("Getting sponsors");
        strings.add("Washing jerseys");
        strings.add("Printing tickets");
        strings.add("Counting dribbles");
        strings.add("Training referees");
    }

    public void destroyYourself() {
        if(_fragmentId != 0) {
            // derived from https://stackoverflow.com/a/31969693/1757997
            Fragment fragment = activity.getFragmentManager().findFragmentById(_fragmentId);
            activity.getFragmentManager().beginTransaction().remove(fragment).commit();
            _fragmentId = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(ThisPlugin.TAG, "PreviewFragment - onCreateView");

        int _fragmentId = ThisPlugin.getAppResource(activity, "media_player", "layout");
        Log.d(ThisPlugin.TAG, "PreviewFragment's resource ID: " + _fragmentId);

        return inflater.inflate(_fragmentId, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Log.d(ThisPlugin.TAG, "PreviewFragment - onViewCreated");

        int playerId = ThisPlugin.getAppResource(activity, "surface", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - playerId:"+playerId);

        videoPlayer = (VideoView) view.findViewById(playerId);

        playButtonId = ThisPlugin.getAppResource(activity, "play", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - playButtonId:"+playButtonId);
        ImageButton playButton = (ImageButton) view.findViewById(playButtonId);
        playButton.setOnClickListener(this);

        retakeButtonId = ThisPlugin.getAppResource(activity, "retake", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - retakeButtonId:"+retakeButtonId);
        ImageButton retakeButton = (ImageButton) view.findViewById(retakeButtonId);
        retakeButton.setOnClickListener(this);

        saveButtonId = ThisPlugin.getAppResource(activity, "save", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - saveButtonId:"+saveButtonId);
        ImageButton saveButton = (ImageButton) view.findViewById(saveButtonId);
        saveButton.setOnClickListener(this);

        Log.d(ThisPlugin.TAG, "onViewCreated - button handlers set up");

        reviewButtonsId = ThisPlugin.getAppResource(activity, "reviewButtons", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - reviewButtonsId:"+reviewButtonsId);
        reviewButtons = (View) view.findViewById(reviewButtonsId);

        determinateBarId = ThisPlugin.getAppResource(activity, "determinateBar", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - determinateBarId:"+determinateBarId);
        determinateBar = (ProgressBar) view.findViewById(determinateBarId);

        cancelButtonId = ThisPlugin.getAppResource(activity, "cancel", "id");
        Log.d(ThisPlugin.TAG, "onViewCreated - cancelButtonId:"+cancelButtonId);
        cancelButton = (ImageButton) view.findViewById(cancelButtonId);
        cancelButton.setOnClickListener(this);

        int progressBarLayoutId = ThisPlugin.getAppResource(activity, "progressBarLayout", "id");
        progressBarLayout = (View) view.findViewById(progressBarLayoutId);

        int progressBarTextId = ThisPlugin.getAppResource(activity, "progressBarText", "id");
        progressBarText = (TextView) view.findViewById(progressBarTextId);

        videoPlayer.setVideoPath(this.video.fullFilename);
        Log.d(ThisPlugin.TAG, "onViewCreated - video path setup");

        videoPlayer.seekTo( 1 );
        Log.d(ThisPlugin.TAG, "onViewCreated - seeked to 1");
    }



    private void playVideo() {

        Log.d(ThisPlugin.TAG, "playVideo");

        doCleanUp();
        try {
            // videoPlayer.setMediaController(this.mediaController);
            videoPlayer.seekTo( 1 );
            videoPlayer.start();
     

            // // Create a new media player and set the listeners
            // mMediaPlayer = new MediaPlayer();
            // mMediaPlayer.setDataSource(this.mediaPath);
            // mMediaPlayer.setDisplay(holder);
            // mMediaPlayer.prepare();
            // // mMediaPlayer.setOnBufferingUpdateListener(this);
            // // mMediaPlayer.setOnCompletionListener(this);
            // mMediaPlayer.setOnPreparedListener(this);
            // mMediaPlayer.setOnVideoSizeChangedListener(this);
            // // mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            Log.e(ThisPlugin.TAG, "error: " + e.getMessage(), e);
        }
    }


    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == playButtonId) {
            Log.d(ThisPlugin.TAG, "PLAY Button clicked");
            playVideo();
        }
        else if(id == saveButtonId) {
            Log.d(ThisPlugin.TAG, "SAVE Button clicked");
            eventHandler.RecordedVideoAccepted(this.video);
            Log.d(ThisPlugin.TAG, "RecordedVideoAccepted done");
            try {
                reviewButtons.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                progressBarLayout.setVisibility(View.VISIBLE);
                Upload(this.video); 
            } catch(Exception ex) {
                Log.e(ThisPlugin.TAG, "reviewButtons.setVisibility(View.GONE) threw an exception", ex);
            }
        }
        else if(id == retakeButtonId) {
            Log.d(ThisPlugin.TAG, "RETAKE Button clicked");
            eventHandler.RetakeVideoRequested(this.video);
            DeleteFile();
        }
        else if(id == cancelButtonId) {
            Log.d(ThisPlugin.TAG, "CANCEL Button clicked");
            eventHandler.CancelRequested();
            DeleteFile();
        }
        else {
            Log.d(ThisPlugin.TAG, "Something clicked but we don't know what");
        }
    }


    private void Upload(Video video) {
        try {

            Log.d(ThisPlugin.TAG, "Setting form fields map...");

            HashMap<String,String> formFields = new HashMap<String,String>();
            formFields.put("testId", ThisPlugin.exercise.testId != null ? Integer.toString(ThisPlugin.exercise.testId) : null);
            formFields.put("testTypeId", ThisPlugin.exercise.testTypeId != null ? Integer.toString(ThisPlugin.exercise.testTypeId) : null);
            formFields.put("exerciseId", ThisPlugin.exercise.exerciseId != null ? Integer.toString(ThisPlugin.exercise.exerciseId) : null);
            formFields.put("viewId", ThisPlugin.exercise.viewId != null ? Integer.toString(ThisPlugin.exercise.viewId) : null);
            formFields.put("uniqueId", ThisPlugin.exercise.uniqueId);
            formFields.put("version", ThisPlugin.exercise.version);
            Log.d(ThisPlugin.TAG, "bodySideId:" + ((ThisPlugin.exercise.bodySideId != null) ? Integer.toString(ThisPlugin.exercise.bodySideId) : "null"));
            if(ThisPlugin.exercise.bodySideId != null) {
                formFields.put("bodySideId", Integer.toString(ThisPlugin.exercise.bodySideId));
            }
            formFields.put("desiredVideoWidth", ThisPlugin.settings.desiredVideoWidth != null ? Integer.toString(ThisPlugin.settings.desiredVideoWidth) : null);
            formFields.put("desiredVideoHeight", ThisPlugin.settings.desiredVideoHeight != null ? Integer.toString(ThisPlugin.settings.desiredVideoHeight) : null);
            formFields.put("actualVideoWidth", video.actualWidth != null ? Integer.toString(video.actualWidth) : null);
            formFields.put("actualVideoHeight", video.actualHeight != null ? Integer.toString(video.actualHeight) : null);

            Log.d(ThisPlugin.TAG, "Setting files map...");

            HashMap<String,File> files = new HashMap<String,File>();
            File inputFile = new File(video.fullFilename);
            files.put(ThisPlugin.exercise.filePrefix + "-video.mp4", inputFile);

            Log.d(ThisPlugin.TAG, "Instance of video submitter...");
            UploadEventHandler events = new UploadEventHandler() {
                @Override public void OnProgress(int percent) {
                    Log.d(ThisPlugin.TAG, "handled percent: " + percent);
                    determinateBar.setProgress(percent);

                    progressBarText.setText(strings.get(percent/10) + " (" + Integer.toString(percent) + "%)");
                }
                @Override public void OnCompleted() {
                    Log.d(ThisPlugin.TAG, "PreviewFragment - OnCompleted handler");
                    eventHandler.RecordedVideoUploaded(video);
                    DeleteFile();
                    Log.d(ThisPlugin.TAG, "PreviewFragment - OnCompleted - input file deleted");
                }
            };

            VideoSubmitter x = new VideoSubmitter(ThisPlugin.settings.endpointUrl, formFields, files, ThisPlugin.settings.headers, events);

            Log.d(ThisPlugin.TAG, "Video submitter instance execute...");
            x.execute();
        }
        catch(Exception ex) {
            Log.e(ThisPlugin.TAG, "BodyMapEventHandler.RecordedVideoAccepted exception", ex);
        }
    }

    private void DeleteFile() {
        try {
            Log.d(ThisPlugin.TAG, "Deleting file '"+this.video.fullFilename+"'...");
            if(this.video.fullFilename != null) {
                File inputFile = new File(this.video.fullFilename);
                inputFile.delete();    
            }
        } catch(Exception ex) {
            Log.e(ThisPlugin.TAG, "DeleteFile exception, ignoring", ex);
        }
    }

}
