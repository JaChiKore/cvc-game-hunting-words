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
import java.util.Random;

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
import timber.log.Timber;

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


    private Date startedDate;
    private final String username;

    List<String> imagesCurrentRound;
   List<String> imagesFixCurrentRound;
    List<String> playedTotalTranscriptions;
    List<MatchResult> results;
    private int totalOks;
    private int numLives;
    private float totalScore;

    public MatchGamePresenterImpl(MatchView view, String username, int currentLevel, float totalScore) {
        /* IT MUST BE FIRST */
        AppController.getComponent().inject(this);
        this.view = view;
        this.username = username;
        this.imagesFixCurrentRound = new ArrayList<>();
        this.imagesCurrentRound = new ArrayList<>();
        this.playedTotalTranscriptions = new ArrayList<>();
        this.results = new ArrayList<>();
        this.level = new GameLevel(currentLevel);
        this.numLives = Utils.NUM_LIVES;
        this.totalScore = totalScore;
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
            this.view.updateOK(idImage,currentScore);
            executeOk();
            //update
        } else {
            Pair<List<String>, String> info = this.matchFixInfo.get(filepathImage);
            if (info.second.equals(textSolution)) {
                currentScore+= Utils.VALUE_POINT;
                this.view.updateOK(idImage,currentScore);
                executeOk();
            } else {
                executeFail();
            }
        }

    }

    private void  executeOk()  {
        numOks++;
        //TODO CHANGE SIZE IMAGS TO UPLOAD
        if (numOks == totalOks) {
            //TODO update diff
            playedTotalTranscriptions.addAll(imagesCurrentRound);
            playedTotalTranscriptions.addAll(imagesFixCurrentRound);
            // TODO when to call this method
            updateLevel();
            finishRoundAndUpdate();
        }
    }

    private void executeFail () {
        this.view.updateFail();
        numLives--;
        this.view.setUpNumLives(numLives);
        if (numLives == 0) {
            CallbackPostDialog callback = () ->  repeatGame();
            view.runPlayAgainDialog(0,level.getLevel(), callback);
        }
    }


    public void finishRoundAndUpdate() {
        CallbackPostDialog callback = () -> {
            float oldScore = totalScore;
            totalScore += currentScore;
            view.updateTotalScore(totalScore);
            uploadResult((int)oldScore,(int)totalScore);
            if (!isItNeedImages()) {
                restartGame();
            } else {
                this.view.startDialog();
            }

        };
        this.view.runPlayAgainDialog(currentScore,level.getLevel(),callback);
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


    public boolean checkIfItIsPaused() {
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

    public void repeatGame() {
        initGame();
    }

    private void initGame() {
        startedDate = Calendar.getInstance().getTime();
        resetValues();

        startNewLives();

        imagesCurrentRound.clear();
        imagesFixCurrentRound.clear();

        //TODO APPLY DELETE IMAGES TESTED
        SecureRandom random = new SecureRandom();
        setUpInfo( level.getNum(), random, matchInfo, imagesCurrentRound);
        setUpInfo(level.getNumFix(),random, matchFixInfo,imagesFixCurrentRound);

        countTotalUsed +=imagesCurrentRound.size();
        countTotalFixUsed +=imagesFixCurrentRound.size();

        totalOks = imagesCurrentRound.size()+imagesFixCurrentRound.size();

        List<String> allImages = new ArrayList<>(imagesCurrentRound);
        allImages.addAll(imagesFixCurrentRound);
        /* randomize  */
        Collections.shuffle(allImages);


        Random generator = new Random();
        int randomIndex = generator.nextInt(allImages.size());
        String nameFile = allImages.get(randomIndex);

        List<String> nameWords;
        assert(this.matchInfo.containsKey(nameFile) ||this.matchFixInfo.containsKey(nameFile) );

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
        int numMatchs = level.getNum();
        int numMatchsFix = level.getNumFix();
        if (this.matchInfo.keySet().size() < numMatchs || this.matchFixInfo.keySet().size() < numMatchsFix) {
            return true;
        }

        /* analysed all images with correct mix */
        if ((matchInfo.keySet().size()- countTotalUsed) <numMatchs
                ||
                (matchFixInfo.keySet().size()- countTotalFixUsed) <numMatchsFix
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
        new Thread(() -> {
            new MatchService(username).run(newResults,String.valueOf(level.getLevel()),startedDate,stoppedDate,oldScore,newTotalPoints);
        }).start();
        this.results.clear();
    }

    private void setUpInfo(int sizeForLevel, SecureRandom random, Hashtable<String, Pair<List<String>, String>> info, List<String> imagesToUse) {
        //TODO CHECK SIZE
        while (imagesToUse.size() < info.keySet().size() && imagesToUse.size() < sizeForLevel) {
            int randomIndex = random.nextInt(info.keySet().size());
            String value = new ArrayList<>(info.keySet()).get(randomIndex);
            if (!imagesToUse.contains(value) && !playedTotalTranscriptions.contains(value) ) {
                imagesToUse.add(value);
            }
        }

    }



    private void updateLevel() {
        level.increase();
    }

    @Override
    public void loadMoreInfo() {
        try {
            new UpdateMatchGame(Utils.BATCH_MATCH_IMAGES).update(appContext);
            new LoaderMatchGameInformation().load(appContext,matchInfo);
            new LoaderMatchGameInformation().loadFix(appContext,matchFixInfo);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }

    }


}
