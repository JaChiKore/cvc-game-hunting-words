package edu.uab.cvc.huntingwords.screens.games;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class Sounds {
    SoundPool soundPool;
    int fail = -1;
    int pass = -1;
    int won = -1;

    public Sounds(Context context) {
        //noinspection deprecation
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("sounds/fail.wav");
            fail = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/pass.wav");
            pass = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/won.wav");
            won = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("Error:","failed to load sound files.");
        }
    }
}
