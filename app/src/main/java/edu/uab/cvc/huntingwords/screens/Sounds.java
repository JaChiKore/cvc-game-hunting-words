package edu.uab.cvc.huntingwords.screens;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

import edu.uab.cvc.huntingwords.R;

@SuppressWarnings("WeakerAccess")
public class Sounds {
    public SoundPool soundPool;
    public int fail = -1;
    public int pass = -1;
    public int won = -1;

    public Sounds(Context context) {
        //noinspection deprecation
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        fail = soundPool.load(context,R.raw.fail, 1);
        pass = soundPool.load(context,R.raw.pass, 1);
        won = soundPool.load(context,R.raw.won, 1);


    }
}
