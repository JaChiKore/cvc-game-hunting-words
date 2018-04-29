package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;
import android.util.Pair;

import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.Utils;
import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.ClusterDifferentResult;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.DifferenceGameInformation;
import edu.uab.cvc.huntingwords.presenters.utils.GameLevel;
import edu.uab.cvc.huntingwords.screens.views.DifferenceView;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderDifferenceGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateDifferenceGame;
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

    private GameLevel level;


    private final DifferenceView view;
    private String keyCurrentPlay;
    private List<String> usedClusters;
    private List<String> usedFixClusters;
    private List<String> clustersToPlay;

    private List<String> playedClusters;
    private  int countUsed,countFixUsed;


    private Date startedDate;
    private final String username;

    private List<ClusterDifferentResult> results;



    public DifferenceGamePresenterImpl(DifferenceView view, String username, int level) {
        AppController.getComponent().inject(this);
        clustersToPlay = new ArrayList();
        usedClusters = new ArrayList<>();
        usedFixClusters = new ArrayList<>();
        results = new ArrayList<>();
        playedClusters = new ArrayList<>();
        countUsed = 0;
        countFixUsed = 0;
        this.view = view;
        this.username = username;
        this.level = new GameLevel(level);
    }

    @Override
    public void newGame() {
        currentScore = 0;
        restartGame();
    }


        private void setUpInfo(int sizeForLevel, SecureRandom random, Hashtable<String, List<Pair<String, Boolean>>> info, List<String> listClusters) {
        //TODO CHECK SIZE
        while (listClusters.size() < info.size() && listClusters.size() <sizeForLevel ) {
            int randomIndex = random.nextInt(info.keySet().size());
            String cluster = new ArrayList<>(info.keySet()).get(randomIndex);
            if (!listClusters.contains(cluster) && !playedClusters.contains(cluster))  {
                listClusters.add(cluster);
            }
        }
    }

    private void initGame () {
        List<String> listClusters = new ArrayList<>();
        List<String> listFixClusters = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        //TODO CHECK NOT MORE AVAILABLE IMAGES

        if(usedClusters.size() >= diffInfo.size()  ||   usedFixClusters.size() >= diffFixInfo.size()) {
                return;
        }

        /* not more available clusters */
        if (playedClusters.size() >= (diffInfo.size()+diffFixInfo.size())) {
            return;
        }


        /* analysed all images with correct mix */
        if ((diffInfo.keySet().size()-countUsed) <  level.getNum()
                ||
                (diffFixInfo.keySet().size()-countFixUsed) <level.getNumFix()
                ) {
            return;

        }

        //TODO APPLY DELETE IMAGES TESTED

        setUpInfo(level.getNum(), random, diffInfo,listClusters);
        setUpInfo(level.getNumFix(), random, diffFixInfo,listFixClusters);

        countUsed+=listClusters.size();
        countFixUsed+=listFixClusters.size();

        clustersToPlay = new ArrayList<>(listClusters);
        clustersToPlay.addAll(listFixClusters);
        Collections.shuffle(clustersToPlay);
        view.startCountdown();

    }

    @Override
    public void restartGame() {
        clustersToPlay.clear();
        initGame();
        if (clustersToPlay.size() == 0) {
            this.view.messageNotEnoughImages();
            //TODO finish level
        } else {
            updateGame();
        }
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


    @Override
    public void finishRound() {
        this.view.runPlayAgainDialog(currentScore,level.getLevel());
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
        view.updateOK(currentScore);
        if (clustersToPlay.size()==0) {
            updateLevel();
            checkForMoreImages();
            view.runPlayAgainDialog(currentScore,level.getLevel());
        } else {
            keyCurrentPlay = clustersToPlay.get(0);
            //FIXME change to repeat same clusters if not pass the level
            playedClusters.add(keyCurrentPlay);
            this.updateGame();

        }

    }


    @Override
    public void uploadResult(Integer oldScore, Integer newTotalPoints) {
        if (this.username.equals(appContext.getString(R.string.anonym))) {
            return;
        }

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
        level.increase();
    }

    private void checkForMoreImages() {
    }

    public void loadDifferenceInfo() {
        try {
            new UpdateDifferenceGame(Utils.BATCH_DIFF_IMAGES).update(appContext);
            new LoaderDifferenceGameInformation().load(appContext,diffInfo);
            new LoaderDifferenceGameInformation().loadFix(appContext,diffFixInfo);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }


    }



}
