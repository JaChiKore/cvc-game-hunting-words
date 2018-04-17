package edu.uab.cvc.huntingwords.tasks;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Hashtable;
import java.util.List;

import edu.uab.cvc.huntingwords.tasks.loaders.LoaderDifferenceGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderMatchGameInformation;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by carlosb on 17/04/18.
 */
@RunWith(AndroidJUnit4.class)
public class LoadGameInformationTest {

    @Test
    public void loadMatchGameInformation() throws Exception {
        LoaderMatchGameInformation loadGame = new LoaderMatchGameInformation();
        Hashtable<String, Pair<List<String>, String>> matchGameInfo = new Hashtable<>();
        loadGame.load(getTargetContext(),matchGameInfo);
    }
    @Test
    public void loadMatchFixGameInformation() throws Exception {
        LoaderMatchGameInformation loadGame = new LoaderMatchGameInformation();
        Hashtable<String, Pair<List<String>, String>> matchGameInfo = new Hashtable<>();
        loadGame.loadFix(getTargetContext(),matchGameInfo);
    }

    @Test
    public void loadDifferenceGameInformation() throws Exception {
        LoaderDifferenceGameInformation loadGame = new LoaderDifferenceGameInformation();
        Hashtable<String, List<Pair<String, Boolean>>> info  = new Hashtable<>();
        loadGame.load(getTargetContext(),info);
    }
    @Test
    public void loadDifferenceFixGameInformation() throws Exception {
        LoaderDifferenceGameInformation loadGame = new LoaderDifferenceGameInformation();
        Hashtable<String, List<Pair<String, Boolean>>> info  = new Hashtable<>();
        loadGame.loadFix(getTargetContext(),info);
    }

}
