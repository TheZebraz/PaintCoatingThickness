package by.mastihin.paint.paintcoatingthickness;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by AndrewEvtukhov on 01.12.2017.
 */

public class Presenter {

    private boolean isPlaying = false;

    private float frequencyLeft = 300;
    private float frequencyRight = 400;

    final int SAMPLE_RATE = 44100;

    int minSize;
    AudioTrack audioTrack;

    MainView mView;

    public Presenter(MainView view) {
        minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);
        audioTrack.play();

        mView = view;
    }

    public void play() {
        if (!isPlaying && mSoundThread.getState() == Thread.State.NEW) {
            isPlaying = true;
            mSoundThread.start();
        }
    }

    public void stop() {
        isPlaying = false;
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
                    buffer[i * 2] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleRight)));
                    angleLeft += (float) (Math.PI) * frequencyLeft / SAMPLE_RATE;
                    angleRight += (float) (Math.PI) * frequencyRight / SAMPLE_RATE;
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
        }
    });



   /// private static final int SAMPLE_RATE = 44100;
//    private static final int RECORD_FRAME_SIZE = (int) Math.pow(2, 14);
//    private static final int FRAME_COUNT_IN_DATA = 1;
//    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
//    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
//    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
//
//    private float frequencyLeft = 300;
//    private float frequencyRight = 1000;
//
//    private double phase = (Math.PI)/2;
//
//
//    private Thread mRecordingThread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            AudioRecord audioRecord;
//            short[] buffer;
//
////            audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, internalBufferSize * 4);
////            buffer = new short[this.bufferSize];
////            int minInternalBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
////            while (true) {
////                for (int i = 0; i < buffer.length / 2; i++) {
////                    buffer[i * 2 + 1] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleLeft)));
////                    buffer[i * 2] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleRight)));
////                    angleLeft += (float) (Math.PI) * frequencyLeft / SAMPLE_RATE;
////                    angleRight += (float) (Math.PI) * frequencyRight / SAMPLE_RATE;
////                }
////                audioTrack.write(buffer, 0, buffer.length);
////            }
//        }
//    });
}
