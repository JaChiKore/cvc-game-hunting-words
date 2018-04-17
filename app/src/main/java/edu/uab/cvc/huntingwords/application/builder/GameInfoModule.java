package edu.uab.cvc.huntingwords.application.builder;

import android.util.Pair;

import java.util.Hashtable;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by carlosb on 17/04/18.
 */
@Module
public class GameInfoModule {

    @Singleton
    @Provides
    public Hashtable<String, List<Pair<String, Boolean>>> differenceGameInformation () {
        return new Hashtable<> ();
    }

    @Singleton
    @Provides
    public Hashtable<String, Pair<List<String>, String>> matchGameInformation () {
        return new Hashtable<> ();
    }
}
