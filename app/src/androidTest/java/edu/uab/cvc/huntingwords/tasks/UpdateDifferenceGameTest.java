package edu.uab.cvc.huntingwords.tasks;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uab.cvc.huntingwords.tasks.loaders.UpdateDifferenceGame;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateMatchGame;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by carlosb on 16/04/18.
 */

@RunWith(AndroidJUnit4.class)
public class UpdateDifferenceGameTest {


    @Test
    public void checkMatchUpdateInformation() throws Exception {
            UpdateMatchGame matchGame = new UpdateMatchGame(100);
            matchGame.update(getTargetContext());
    }

    @Test
    public void checkDifferenceUpdateInformation() throws Exception {
        UpdateDifferenceGame differenceGame = new UpdateDifferenceGame(100);
        differenceGame.update(getTargetContext());
    }

}
