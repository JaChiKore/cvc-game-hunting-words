package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;
import android.util.Pair;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.ClusterDifferentResult;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.DifferenceGameInformation;
import edu.uab.cvc.huntingwords.models.MatchResult;
import edu.uab.cvc.huntingwords.presenters.utils.GameLevel;
import edu.uab.cvc.huntingwords.screens.views.DifferenceView;
import edu.uab.cvc.huntingwords.tasks.services.DifferenceService;

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

    private GameLevel level = GameLevel.EASY;
    private int numRounds = 0;


    private final DifferenceView view;
    private String keyCurrentPlay;
    private List<String> usedClusters;
    private List<String> usedFixClusters;
    private List<String> clustersToPlay;


    private Date startedDate;
    private final String username;

    private List<ClusterDifferentResult> results;



    public DifferenceGamePresenterImpl(DifferenceView view, String username) {
        AppController.getComponent().inject(this);
        clustersToPlay = new ArrayList();
        usedClusters = new ArrayList<>();
        usedFixClusters = new ArrayList<>();
        results = new ArrayList<>();
        this.view = view;
        this.username = username;
    }

    @Override
    public void newGame() {
        currentScore = 0;
        updateGame();
    }


    private void setUpCluster(List<String> listClusters, int sizeForLevel, SecureRandom random) {
        //TODO CHECK SIZE
        while (listClusters.size() < diffInfo.size() && listClusters.size() <sizeForLevel ) {
            int randomIndex = random.nextInt(diffInfo.keySet().size());
            String cluster = new ArrayList<>(diffInfo.keySet()).get(randomIndex);
            if (!listClusters.contains(cluster))  {
                listClusters.add(cluster);
            }
        }
    }


    private void setUpFixCluster(List<String> listClusters, int sizeForLevel, SecureRandom random) {
        //TODO CHECK SIZE
        while (listClusters.size() < diffFixInfo.size() && listClusters.size() <sizeForLevel ) {
            int randomIndex = random.nextInt(diffFixInfo.keySet().size());
            String cluster = new ArrayList<>(diffFixInfo.keySet()).get(randomIndex);
            if (!listClusters.contains(cluster))  {
                listClusters.add(cluster);
            }
        }
    }
    private void initGame () {
        updateLevel();
        numRounds++;
        List<String> listClusters = new ArrayList<>();
        List<String> listFixClusters = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        if(usedClusters.size() >= diffInfo.size()  ||   usedFixClusters.size() >= diffFixInfo.size()) {
                this.view.notAvailableImages();
        }

        //TODO APPLY DELETE IMAGES TESTED

        setUpCluster(listClusters,level.getNum(), random);
        setUpFixCluster(listFixClusters,level.getNumFix(), random);

        clustersToPlay = new ArrayList<>(listClusters);
        clustersToPlay.addAll(listFixClusters);
        Collections.shuffle(clustersToPlay);
        view.startCountdown();

    }

    @Override
    public void restartGame() {
        clustersToPlay.clear();
        updateGame();

    }


    @Override
    public void updateGame() {
        if (clustersToPlay.size()==0) {
            initGame();
        }
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

    private void updateImageInfo(String keyCurrentPlay, List<Pair<String, Boolean>> newInfo) {
        assert(this.diffInfo.containsKey(keyCurrentPlay) || this.diffFixInfo.containsKey(keyCurrentPlay));
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            this.diffInfo.put(keyCurrentPlay,newInfo);
        } else {
            this.diffFixInfo.put(keyCurrentPlay,newInfo);
        }
    }

    @Override
    public void finishRound() {
        this.view.runPlayAgainDialog(currentScore);
    }

    @Override
    public void checkImage(String tag) {
        List<Pair<String, Boolean>> imagesInfoToUse = getImageInfo(keyCurrentPlay);
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            results.add(ClusterDifferentResult.newImageDifferent(keyCurrentPlay,tag));
            usedClusters.add(keyCurrentPlay);
            executeOk();
            //TODO ADD cluster to not play more
            //TODO Update with another round
        } else {
            boolean isCorrect = true;
            for (Pair<String, Boolean> imageInfo : imagesInfoToUse) {
                if (imageInfo.first.equals(tag) && !(imageInfo.second==true)) {
                    isCorrect = false;
                    break;
                } else if (!imageInfo.first.equals(tag) && !(imageInfo.second==false)) {
                    isCorrect = false;
                    break;
                }
            }
            if (isCorrect) {
                usedFixClusters.add(keyCurrentPlay);
                executeOk();
                //TODO ADD cluster to not play more
                //TODO Update with another round
            } else {
                view.updateFail();
                //TODO wrong!!!
            }
        }

    }

    private void executeOk() {
        currentScore++;
        this.updateGame();
        view.updateOK(currentScore);
    }

    @Override
    public void uploadResult(Integer oldScore, Integer newTotalPoints) {
        List<ClusterDifferentResult> newResults = new ArrayList<ClusterDifferentResult>(this.results);
/*
        Date stoppedDate = Calendar.getInstance().getTime();
        String level = "4";

        //TODO fix this!!
        //4 -> easy
        //6 -> medium
        //8 -> hard
        new DifferenceService(this.username).run(this.results,level,startedDate,stoppedDate,oldScore,newTotalPoints);
*/

    }

    @Override
    public void checkDifferent() {
        List<Pair<String, Boolean>> imagesInfoToUse = getImageInfo(keyCurrentPlay);
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            results.add(ClusterDifferentResult.newAllDifferent(keyCurrentPlay));
            usedClusters.add(keyCurrentPlay);
            executeOk();
        } else {
            boolean diff = true;
            for (Pair<String, Boolean> imageInfo : imagesInfoToUse) {
                if (imageInfo.second!=true) {
                        diff  = false;
                        break;
                }
            }
            if (diff) {
                usedFixClusters.add(keyCurrentPlay);
                executeOk();
                //TODO ADD cluster to not play more
                //TODO Update with another round
            } else {
                view.updateFail();
                //TODO wrong!!
            }
        }
    }

    @Override
    public void checkSame() {
        List<Pair<String, Boolean>> imagesInfoToUse = getImageInfo(keyCurrentPlay);
        if (this.diffInfo.containsKey(keyCurrentPlay)) {
            results.add(ClusterDifferentResult.newSameImage(keyCurrentPlay));
            usedClusters.add(keyCurrentPlay);
            executeOk();
            //TODO ADD cluster to not play more
            //TODO Update with another round
        } else {
            boolean diff = false;
            for (Pair<String, Boolean> imageInfo : imagesInfoToUse) {
                if (imageInfo.second!=false) {
                    diff  = true;
                    break;
                }
            }
            if (diff) {
                view.updateFail();
                //TODO wrong!!
            } else {
                usedFixClusters.add(keyCurrentPlay);
                executeOk();
                //TODO ADD cluster to not play more
                //TODO Update with another round
            }
        }
    }



    private void updateLevel() {
        if (numRounds < 4) {
            level = GameLevel.EASY;

        } else if (numRounds < 8) {
            level = GameLevel.MEDIUM;

        } else {
            level = GameLevel.HARD;
        }
    }


}
