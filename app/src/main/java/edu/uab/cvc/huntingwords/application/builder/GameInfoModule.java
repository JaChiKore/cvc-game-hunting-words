package edu.uab.cvc.huntingwords.application.builder;

import android.util.Pair;

import java.util.Hashtable;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.DifferenceGameInformation;
import edu.uab.cvc.huntingwords.models.MatchFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchGameInformation;

/**
 * Created by carlosb on 17/04/18.
 */
@Module
public class GameInfoModule {

    @Singleton
    @Provides
    public DifferenceGameInformation getDifferenceInfo () {
        return new DifferenceGameInformation ();
    }

    @Singleton
    @Provides
    public MatchGameInformation getMatchInfo () {
        return new MatchGameInformation ();
    }

    @Singleton
    @Provides
    public DifferenceFixGameInformation getDifferenceFixInfo () {
        return new DifferenceFixGameInformation ();
    }

    @Singleton
    @Provides
    public MatchFixGameInformation getMatchFixInfo () {
        return new MatchFixGameInformation();
    }

}
