package edu.uab.cvc.huntingwords.screens;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import edu.uab.cvc.huntingwords.R;

@SuppressWarnings("WeakerAccess")
public class Sounds {
    public SoundPool soundPool;
    public int fail;
    public int pass;
    public int won;

    public Sounds(Context context) {
        //noinspection deprecation
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        fail = soundPool.load(context,R.raw.fail, 1);
        pass = soundPool.load(context,R.raw.pass, 1);
        won = soundPool.load(context,R.raw.won, 1);


    }
}
