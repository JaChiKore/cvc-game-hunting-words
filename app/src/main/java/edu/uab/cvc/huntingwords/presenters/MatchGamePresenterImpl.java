package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;
import android.util.Pair;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
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
import edu.uab.cvc.huntingwords.models.MatchFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchGameInformation;
import edu.uab.cvc.huntingwords.models.MatchResult;
import edu.uab.cvc.huntingwords.presenters.utils.GameLevel;
import edu.uab.cvc.huntingwords.screens.fragments.CallbackPostDialog;
import edu.uab.cvc.huntingwords.screens.views.MatchView;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderMatchGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateMatchGame;
import edu.uab.cvc.huntingwords.tasks.services.MatchService;

import static edu.uab.cvc.huntingwords.Utils.EMPTY_BUTTON;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGamePresenterImpl implements MatchGamePresenter {

    @Inject
    MatchGameInformation matchInfo;

    @Inject
    MatchFixGameInformation matchFixInfo;

    @Inject
    Context appContext;

    private float currentScore;

    private GameLevel level;
    private int numOks;
    private final MatchView view;

    private  int countTotalUsed;
    private  int countTotalFixUsed;

    private int info;
    private int fixInfo;

    private Date startedDate;
    private String startDate;
    private final String username;
    private final String password;

    private List<String> imagesCurrentRound;
    private List<String> imagesFixCurrentRound;
    private List<String> playedTotalTranscriptions;
    private List<MatchResult> results;
    private int totalOks;
    private int numLives;
    private float maxScore;
    private final float scoreDifference;
    private float totalScore;

    private SimpleDateFormat sdf;

    public MatchGamePresenterImpl(MatchView view, String username, String password, int currentLevel, int diffLevel, float totalScore, float  scoreDifference) {
        /* IT MUST BE FIRST */
        AppController.getComponent().inject(this);
        sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        this.view = view;
        this.username = username;
        this.password = password;
        this.imagesFixCurrentRound = new ArrayList<>();
        this.imagesCurrentRound = new ArrayList<>();
        this.playedTotalTranscriptions = new ArrayList<>();
        this.results = new ArrayList<>();
        this.level = new GameLevel(currentLevel, diffLevel);
        this.numLives = Utils.NUM_LIVES;
        this.maxScore = totalScore;
        this.totalScore = 0;
        this.scoreDifference = scoreDifference;
        this.info = 0;
        this.fixInfo = 0;
        countTotalUsed = 0;
        countTotalFixUsed = 0;
    }


    @Override
    public void checkSolution(final int idImage, final int idButton, String filepathImage, String textSolution) {

        if (textSolution.equals(EMPTY_BUTTON)) {
            return;
        }
        assert (this.matchInfo.containsKey(filepathImage) || this.matchFixInfo.containsKey(filepathImage));
        if (this.matchInfo.containsKey(filepathImage)) {
            currentScore+= Utils.VALUE_POINT;
            this.results.add(new MatchResult(filepathImage,textSolution));
            this.view.updateOK(idImage,totalScore + currentScore);
            executeOk();
            //update
        } else {
            Pair<List<String>, String> info = this.matchFixInfo.get(filepathImage);
            if (info.second.equals(textSolution)) {
                currentScore+= Utils.VALUE_POINT;
                this.view.updateOK(idImage,totalScore + currentScore);
                executeOk();
            } else {
                executeFail();
            }
        }

    }

    private void  executeOk()  {
        numOks++;
        if (numOks == totalOks) {
            playedTotalTranscriptions.addAll(imagesCurrentRound);
            playedTotalTranscriptions.addAll(imagesFixCurrentRound);
            updateLevel(true);
            finishRoundAndUpdate();
        }
    }

    private void executeFail () {
        this.view.updateFail();
        numLives--;
        this.view.setUpNumLives(numLives);
        if (numLives == 0) {
            float oldScore = totalScore;
            totalScore += currentScore;
            updateLevel(false);
            CallbackPostDialog okay = () ->  {
                uploadResult((int)oldScore,(int)totalScore);
                repeatGame();
            };
            CallbackPostDialog cancel = () -> uploadResult((int)oldScore,(int)totalScore);
            view.runPlayAgainDialog(false,totalScore,level.getLevel(), okay, cancel);
        }
    }


    private void finishRoundAndUpdate() {
        float oldScore = totalScore;
        totalScore += currentScore;
        if (totalScore > maxScore) {
            view.updateTotalScore(totalScore);
        }
        CallbackPostDialog okay = () -> {
            uploadResult((int)oldScore,(int)totalScore);
            if (!isItNeedImages()) {
                restartGame();
            } else {
                this.view.startDialog();
            }

        };
        CallbackPostDialog cancel = () -> uploadResult((int)oldScore,(int)totalScore);
        this.view.runPlayAgainDialog(true,totalScore,level.getLevel(),okay, cancel);
    }

    @Override
    public void newGame() {
        currentScore = 0;
        restartGame();
    }

    @Override
    public void updateButtonsByImage(String nameFile) {
        List<String> nameWords;
        assert(this.matchInfo.containsKey(nameFile) ||this.matchFixInfo.containsKey(nameFile) );
        if (this.matchInfo.containsKey(nameFile)) {
            nameWords = this.matchInfo.get(nameFile).first;
        } else  {
            nameWords = this.matchFixInfo.get(nameFile).first;
        }
        Collections.shuffle(nameWords);
       this.view.updateButtons(nameWords);
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
    public void restartGame() {
        if (checkIfItIsPaused()) return;
        initGame();
    }

    private void repeatGame() {
        totalScore = 0;
        initGame();
    }

    private void initGame() {
        startedDate = Calendar.getInstance().getTime();
        startDate = sdf.format(startedDate);
        resetValues();

        startNewLives();

        imagesCurrentRound.clear();
        imagesFixCurrentRound.clear();

        setUpInfo( level.getNum(), matchInfo, imagesCurrentRound, false);
        setUpInfo(level.getNumFix(), matchFixInfo,imagesFixCurrentRound, true);

        countTotalUsed +=imagesCurrentRound.size();
        countTotalFixUsed +=imagesFixCurrentRound.size();

        totalOks = imagesCurrentRound.size()+imagesFixCurrentRound.size();

        List<String> allImages = new ArrayList<>(imagesCurrentRound);
        allImages.addAll(imagesFixCurrentRound);
        /* randomize  */
        Collections.shuffle(allImages);


        String nameFile = allImages.get(0);
        assert(this.matchInfo.containsKey(nameFile) ||this.matchFixInfo.containsKey(nameFile) );
        List<String> nameWords;
        if (this.matchInfo.containsKey(nameFile)) {
            nameWords = this.matchInfo.get(nameFile).first;
        } else  {
            nameWords = this.matchFixInfo.get(nameFile).first;
        }
        Collections.shuffle(nameWords);
        this.view.newRoundPlay(allImages,nameWords);
    }

    private void startNewLives() {
        view.setUpNumLives(Utils.NUM_LIVES);
        this.numLives = Utils.NUM_LIVES;

    }

    private boolean isItNeedImages() {
        int numMatch = level.getNum();
        int numMatchFix = level.getNumFix();
        if (this.matchInfo.keySet().size() < numMatch || this.matchFixInfo.keySet().size() < numMatchFix) {
            return true;
        }

        /* analysed all images with correct mix */
        if ((matchInfo.keySet().size()- countTotalUsed) <numMatch
                ||
                (matchFixInfo.keySet().size()- countTotalFixUsed) <numMatchFix
                ) {
            return true;

        }

        if ((countTotalUsed+countTotalFixUsed) >= (matchInfo.keySet().size() +matchFixInfo.keySet().size())) {
            return true;
        }
        return false;
    }

    private void resetValues() {
        this.results.clear();
        numOks = 0;
        currentScore = 0;
    }

    @Override
    public void uploadResult(Integer oldScore, Integer newTotalPoints) {
        if (this.username.equals(appContext.getString(R.string.anonym))) {
            return;
        }
        List<MatchResult> newResults = new ArrayList<>(this.results);
        Date stoppedDate = Calendar.getInstance().getTime();
        long diffInMs = stoppedDate.getTime() - startedDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
        String start = startDate;
        new Thread (() -> new MatchService(username, password,scoreDifference, level.getAnotherLevel()).run(newResults,String.valueOf(level.getLevel()),start,sdf.format(stoppedDate), diffInSec,oldScore,newTotalPoints, maxScore)).start();
        this.results.clear();
    }

    private void setUpInfo(int sizeForLevel, LinkedHashMap<String, Pair<List<String>, String>> info, List<String> imagesToUse, Boolean fix) {
        while (imagesToUse.size() < info.keySet().size() && imagesToUse.size() < sizeForLevel) {
            String value;
            if (fix) {
                value = new ArrayList<>(info.keySet()).get(fixInfo);
                fixInfo += 1;
                if (fixInfo >= info.keySet().size()) {
                    fixInfo = 0;
                }
            } else {
                value = new ArrayList<>(info.keySet()).get(this.info);
                this.info += 1;
                if (this.info >= info.keySet().size()) {
                    this.info = 0;
                }
            }
            if (!imagesToUse.contains(value) && !playedTotalTranscriptions.contains(value) ) {
                imagesToUse.add(value);
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
        countTotalUsed = 0;
        countTotalFixUsed = 0;
        info = 0;
        fixInfo = 0;
        new UpdateMatchGame().update(appContext, username);
        try {
            new LoaderMatchGameInformation().load(appContext,matchInfo, matchFixInfo);
        } catch (FileNotFoundException e) {
            System.out.println("Error in loading the images for the match game.");
        }

    }


}
