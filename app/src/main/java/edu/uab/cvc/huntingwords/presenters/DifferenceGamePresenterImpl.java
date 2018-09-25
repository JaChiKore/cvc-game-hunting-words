package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;
import android.util.Pair;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.Utils;
import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.ClusterDifferentResult;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.DifferenceGameInformation;
import edu.uab.cvc.huntingwords.presenters.utils.GameLevel;
import edu.uab.cvc.huntingwords.screens.fragments.CallbackPostDialog;
import edu.uab.cvc.huntingwords.screens.views.DifferenceView;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderDifferenceGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateDifferenceGame;
import edu.uab.cvc.huntingwords.tasks.services.DifferenceService;
import timber.log.Timber;

/**
 * Created by carlosb on 4/16/18.
 */

public class DifferenceGamePresenterImpl implements DifferenceGamePresenter {

    @Inject
    DifferenceGameInformation diffInfo;

    @Inject
    DifferenceFixGameInformation diffFixInfo;

    @Inject
    Context appContext;

    private float currentScore;
    private float totalScore;
    private float maxScore;

    private GameLevel level;
    private int numLives;

    private final DifferenceView view;
    private String keyCurrentPlay;
    private int countFixUsed, countUsed;
    private List<String> clustersToPlay;

    private List<String> imagesCurrentRound;
    private List<String> imagesFixCurrentRound;
    private List<String> playedTotalClusters;

    private int info;
    private int fixInfo;

    private Date startedDate;
    private final String username;
    private final float scoreMatch;

    private List<ClusterDifferentResult> results;


    public DifferenceGamePresenterImpl(DifferenceView view, String username, int level, int matchLevel, float maxScore, float scoreMatch) {
        AppController.getComponent().inject(this);
        clustersToPlay = new ArrayList();
        countUsed = 0;
        countFixUsed = 0;
        results = new ArrayList<>();
        imagesFixCurrentRound = new ArrayList<>();
        imagesCurrentRound = new ArrayList<>();
        playedTotalClusters = new ArrayList<>();
        this.view = view;
        this.username = username;
        this.level = new GameLevel(level, matchLevel);
        this.numLives = Utils.NUM_LIVES;
        this.totalScore = 0;
        this.scoreMatch = scoreMatch;
        this.maxScore = maxScore;
        this.info = 0;
        this.fixInfo = 0;
        countUsed = 0;
        countFixUsed = 0;
    }

    @Override
    public void newGame() {
        restartGame();
    }

    @Override
    public void restartGame() {
        playedTotalClusters.addAll(imagesCurrentRound);
        playedTotalClusters.addAll(imagesFixCurrentRound);
        if (checkIfItIsPaused()) return;
         clustersToPlay.clear();
        initGame();
        if (clustersToPlay.size() == 0) {
            this.view.messageNotEnoughImages();
        } else {
            updateGame();
        }
    }


    private void repeatGame() {
        clustersToPlay.clear();
        initGame();
        updateGame();
    }

    private boolean checkIfItIsPaused() {
        if (isItNeedImages()) {
            view.setPause(true);
            view.messageNotEnoughImages();
            return true;
        } else {
            view.setPause(false);
        }
        return false;
    }


    @Override
    public void updateGame() {
        startedDate = Calendar.getInstance().getTime();
        keyCurrentPlay = clustersToPlay.get(0);
        clustersToPlay.remove(0);

        List<Pair<String, Boolean>> imagesInfoToUse;
        imagesInfoToUse = getImageInfo(keyCurrentPlay);

        List<String> filepaths = new ArrayList<>();
        for (Pair<String,Boolean> info: imagesInfoToUse) {
            filepaths.add(info.first);
        }
        Collections.shuffle(filepaths);
        this.view.newRoundPlay(filepaths);
    }


    private void initGame () {
        imagesCurrentRound.clear();
        imagesFixCurrentRound.clear();
        resetValues();


        List<String> listClusters = new ArrayList<>();
        List<String> listFixClusters = new ArrayList<>();

        setUpInfo(level.getNum(), diffInfo,listClusters, false);
        setUpInfo(level.getNumFix(), diffFixInfo,listFixClusters, true);
        clustersToPlay = new ArrayList<>(listClusters);
        clustersToPlay.addAll(listFixClusters);
        Collections.shuffle(clustersToPlay);
        startNewLives();
    }

    private void resetValues() {
        currentScore = 0;
        this.results.clear();
    }

    private void setUpInfo(int sizeForLevel, LinkedHashMap<String, List<Pair<String, Boolean>>> info, List<String> listClusters, Boolean fix) {
        while (listClusters.size() < info.size() && listClusters.size() <sizeForLevel ) {
            String cluster;
            if (fix) {
                cluster = new ArrayList<>(info.keySet()).get(fixInfo);
                fixInfo += 1;
                if (fixInfo >= info.size()) {
                    fixInfo = 0;
                }
            } else {
                cluster = new ArrayList<>(info.keySet()).get(this.info);
                this.info += 1;
                if (this.info >= info.size()) {
                    this.info = 0;
                }
            }
            if (!listClusters.contains(cluster) && !playedTotalClusters.contains(cluster))  {
                listClusters.add(cluster);
            }
        }
    }


    private void startNewLives() {
        view.setUpNumLives(Utils.NUM_LIVES);
        this.numLives = Utils.NUM_LIVES;

    }

    private boolean isItNeedImages() {
        if (countUsed >= diffInfo.size()  ||   (countFixUsed >= diffFixInfo.size() && level.getNumFix() > 0)) {
            return true;
        }

        /* analysed all images with correct mix */
        if ((diffInfo.keySet().size()-countUsed) <  level.getNum()
                ||
                (diffFixInfo.keySet().size()-countFixUsed ) <level.getNumFix()) {
            return true;
        }
        return false;
    }



    private List<Pair<String, Boolean>> getImageInfo(String keyCurrentPlay) {
        assert(this.diffInfo.containsKey(keyCurrentPlay) || this.diffFixInfo.containsKey(keyCurrentPlay));
        List<Pair<String, Boolean>> imagesInfoToUse;
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            imagesInfoToUse = this.diffInfo.get(keyCurrentPlay);
        } else {
            imagesInfoToUse = this.diffFixInfo.get(keyCurrentPlay);
        }
        return imagesInfoToUse;
    }




    private void executeFail() {
        view.updateFail();
        numLives--;
        this.view.setUpNumLives(numLives);
        if (numLives == 0) {
            float oldScore = totalScore;
            totalScore += currentScore;
            updateLevel(false);
            CallbackPostDialog okay = () -> {
                uploadResult((int)oldScore,(int)totalScore);
                repeatGame();
            };
            CallbackPostDialog cancel = () -> uploadResult((int)oldScore,(int)totalScore);
            view.runPlayAgainDialog(false, totalScore,level.getLevel(), okay, cancel);
        }
    }

    private void executeOk() {
        currentScore += Utils.VALUE_POINT;
        view.updateOK(totalScore + currentScore);
        if (clustersToPlay.size()==0) {
            updateLevel(true);
            float oldScore = totalScore;
            totalScore += currentScore;
            if (totalScore > maxScore) {
                view.updateTotalScore(totalScore);
            }
            CallbackPostDialog okay = () -> {

                if (!isItNeedImages()) {
                    restartGame();
                } else {
                    this.view.startDialog();
                }
               uploadResult((int)oldScore,(int)totalScore);
            };
            CallbackPostDialog cancel = () -> uploadResult((int)oldScore,(int)totalScore);
            view.runPlayAgainDialog(true, totalScore,level.getLevel(), okay, cancel);
        } else {
            this.updateGame();
        }
    }


    public void uploadResult(Integer oldScore, Integer newTotalPoints) {
        if (this.username.equals(appContext.getString(R.string.anonym))) {
            return;
        }
        List<ClusterDifferentResult> newResults = new ArrayList<>(this.results);
        Date stoppedDate = Calendar.getInstance().getTime();
        long diffInMs = stoppedDate.getTime() - startedDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        new Thread (() -> new DifferenceService(username,scoreMatch, level.getAnotherLevel()).run(newResults,String.valueOf(level.getLevel()),startedDate,stoppedDate, diffInSec,oldScore,newTotalPoints, maxScore)).start();
        this.results.clear();
    }

    @Override
    public void checkImage(String tag) {
        List<Pair<String, Boolean>> imagesInfoToUse = getImageInfo(keyCurrentPlay);
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            results.add(ClusterDifferentResult.newImageDifferent(keyCurrentPlay,tag));
            countUsed++;
            imagesFixCurrentRound.add(keyCurrentPlay);
            executeOk();
        } else {
            boolean isCorrect = true;
            for (Pair<String, Boolean> imageInfo : imagesInfoToUse) {
                if (imageInfo.first.equals(tag) && !(imageInfo.second)) {
                    isCorrect = false;
                    break;
                } else if (!imageInfo.first.equals(tag) && (imageInfo.second)) {
                    isCorrect = false;
                    break;
                }
            }
            if (isCorrect) {
                countFixUsed++;
                imagesCurrentRound.add(keyCurrentPlay);
                executeOk();
            } else {
                executeFail();
            }
        }

    }

    @Override
    public void checkDifferent() {
        List<Pair<String, Boolean>> imagesInfoToUse = getImageInfo(keyCurrentPlay);
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            results.add(ClusterDifferentResult.newAllDifferent(keyCurrentPlay));
            countUsed++;
            imagesFixCurrentRound.add(keyCurrentPlay);
            executeOk();
        } else {
            boolean diff = false;
            int count = 0;
            for (Pair<String, Boolean> imageInfo : imagesInfoToUse) {
                if (imageInfo.second) {
                    count += 1;
                }
            }
            if (count > 1) {
                diff = true;
            }
            if (diff) {
                countFixUsed++;
                imagesCurrentRound.add(keyCurrentPlay);
                executeOk();
            } else {
                executeFail();
            }
        }
    }

    @Override
    public void checkSame() {

        List<Pair<String, Boolean>> imagesInfoToUse = getImageInfo(keyCurrentPlay);
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            results.add(ClusterDifferentResult.newSameImage(keyCurrentPlay));
            countUsed++;
            imagesFixCurrentRound.add(keyCurrentPlay);
            executeOk();
        } else {
            boolean diff = false;
            for (Pair<String, Boolean> imageInfo : imagesInfoToUse) {
                if (imageInfo.second) {
                    diff  = true;
                    break;
                }
            }
            if (diff) {
                executeFail();
            } else {
                countFixUsed++;
                imagesCurrentRound.add(keyCurrentPlay);
                executeOk();
            }
        }
    }



    private void updateLevel(boolean win) {
        if (win) {
            level.increase();
        } else {
            level.decrease();
        }
    }

    @Override
    public void loadMoreInfo() {
        info = 0;
        fixInfo = 0;
        countUsed = 0;
        countFixUsed = 0;
        try {
            new UpdateDifferenceGame().update(appContext, username);
            new LoaderDifferenceGameInformation().load(appContext,diffInfo, diffFixInfo);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }


    }



}
