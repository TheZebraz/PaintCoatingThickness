package by.mastihin.paint.paintcoatingthickness;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

/**
 * Created by AndrewEvtukhov on 01.12.2017.
 */

public class Presenter {

    public static final int RECORDER_BUFFER_SIZE = 2000;
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final int EXPERIMENT_LENGHT = 200000;
    public static final int EXPERIMENT_FREQUENCY = 12000;
    private final Handler handler;
    private boolean isPlaying = false;

    private float frequencyLeft = 0;
    private float frequencyRight = 0;

    private double phase = 0;

    private static final int SAMPLE_RATE = 44100;

    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;

    private int minSize;
    private AudioTrack audioTrack;

    AudioRecord audioRecord;

    private MainView view;
    private Context context;

    public Presenter(Context context, MainView view, Handler handler) {
        this.context = context;
        this.view = view;
        this.handler = handler;
        initAudio();
    }

    private void initAudio() {
        initPlayer();
        initRecorder();
    }

    private void initRecorder() {
        int minInternalBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, minInternalBufferSize);
        audioRecord.startRecording();
    }

    private void initPlayer() {
        minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
        audioTrack.play();
    }

    public void play() {
        if (!isPlaying && mSoundThread.getState() == Thread.State.NEW && mRecordingThread.getState() == Thread.State.NEW) {
            isPlaying = true;
            mSoundThread.start();
            mRecordingThread.start();
            updateState();
        }
    }

    public void stop() {
        isPlaying = false;
        updateState();
    }

    private void updateState() {
        view.setStateText(isPlaying ? context.getString(R.string.on) : context.getString(R.string.off));
    }

    private Thread mSoundThread = new Thread(new Runnable() {
        @Override
        public void run() {
            float angleLeft = 0;
            float angleRight = 0;

            while (isPlaying) {
                short[] buffer = new short[minSize];
                for (int i = 0; i < buffer.length / 2; i++) {

                    buffer[i * 2 + 1] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleLeft)));
                    buffer[i * 2] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleRight + phase)));

                    float deltaLeft = (float) (Math.PI) * frequencyLeft / SAMPLE_RATE;
                    float deltaRight = (float) (Math.PI) * frequencyRight / SAMPLE_RATE;
                    angleLeft += deltaLeft;
                    angleRight += deltaRight;
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
        }
    });

    public void setRightFrequency(Integer value) {
        frequencyRight = value;
    }

    public void setLeftFrequency(Integer value) {
        frequencyLeft = value;
    }

    private Thread mRecordingThread = new Thread(new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            while (isPlaying) {
                short[] buffer = new short[RECORDER_BUFFER_SIZE];
                audioRecord.read(buffer, 0, RECORDER_BUFFER_SIZE);
                showData(buffer);
            }
        }
    });

    private void showData(short[] buffer) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putShortArray(EXTRA_DATA, buffer);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public  void setPhase(int i) {
        phase = (float)i / (float) 100;
        view.setValue(phase);
    }

    void startExperiment(){
        isPlaying = true;
        mExperimentThread.start();
        mRecordingThread.start();
    }

    private Thread mExperimentThread = new Thread(new Runnable() {
        @Override
        public void run() {
            float angleLeft = 0;
            float angleRight = 0;

            while (isPlaying) {
                short[] buffer = new short[EXPERIMENT_LENGHT];
                for (int i = 0; i < EXPERIMENT_LENGHT/2; i++) {
                    phase = (double) (i*2/(EXPERIMENT_LENGHT/628))/(float)100;
                    buffer[i * 2 + 1] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleLeft)));
                    buffer[i * 2] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleRight + phase)));

                    float deltaLeft = (float) (Math.PI) * EXPERIMENT_FREQUENCY / SAMPLE_RATE;
                    float deltaRight = (float) (Math.PI) * EXPERIMENT_FREQUENCY / SAMPLE_RATE;
                    angleLeft += deltaLeft;
                    angleRight += deltaRight;
                }
                audioTrack.write(buffer, 0, EXPERIMENT_LENGHT);
            }
        }
    });
}
