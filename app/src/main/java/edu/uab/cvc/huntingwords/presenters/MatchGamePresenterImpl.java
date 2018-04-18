package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.Utils;
import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.MatchFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchGameInformation;
import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGamePresenterImpl implements MatchGamePresenter {

    public static final int NUM_IMAGES_FOR_ROUND = 12;

    class MyComparator implements Comparator<Score> {
        public int compare(Score a, Score b) {
            if (a.score == b.score) {
                return -1;
            }
            return Integer.compare(a.score,b.score);
        }
    }

    class Score  {
        int score;
        String name;

        public Score(int score, String name) {
            this.score = score;
            this.name = name;
        }

    }


    private  enum MatchLevel {
        EASY (0,4,8),
        MEDIUM(1,6,6),
        HARD(2,8,4);

        private final int level;
        private final int num;
        private final int numFix;
        MatchLevel(int level, int num, int numFix) {
            this.level = level;
            this.num = num;
            this.numFix = numFix;
        }
        public int getLevel() {
            return level;
        }

        public int getNum() {
            return num;
        }

        public int getNumFix() {
            return numFix;
        }
    }


    @Inject
    MatchGameInformation matchInfo;

    @Inject
    MatchFixGameInformation matchFixInfo;

    @Inject
    Context appContext;

    private float currentScore;

    private MatchLevel level = MatchLevel.EASY;
    private int numRounds = 0;
    private int numOks;
    private final MatchView view;

    TreeSet matchSortedInfo;
    TreeSet matchSortedFixInfo;

    List<String> imagesCurrentRound = new ArrayList<>();
    List<String> imagesFixCurrentRound = new ArrayList<>();


    public MatchGamePresenterImpl(MatchView view) {
        /* IT MUST BE FIRST */
        AppController.getComponent().inject(this);
        this.view = view;

        matchSortedInfo = new TreeSet<>(new MyComparator());
        matchSortedFixInfo = new TreeSet<>(new MyComparator());

        for (String filepath: matchInfo.keySet()) {
            matchSortedInfo.add(new Score(0,filepath));
        }

        for (String filepath: matchFixInfo.keySet()) {
            matchSortedFixInfo.add(new Score(0,filepath));
        }

    }


    @Override
    public void checkSolution(final int idImage, final int idButton, String filepathImage, String textSolution) {
        assert (this.matchInfo.containsKey(filepathImage) || this.matchFixInfo.containsKey(filepathImage));
        if (this.matchInfo.containsKey(filepathImage)) {
            currentScore+= Utils.VALUE_POINT;
            Pair<List<String>, String> info = this.matchInfo.get(filepathImage);
            Pair<List<String>, String> newInfo = Pair.create(info.first,textSolution);
            this.matchInfo.put(filepathImage,newInfo);
            this.view.updateOK(idImage,currentScore);
            numOks++;
            //update
        } else {
            Pair<List<String>, String> info = this.matchFixInfo.get(filepathImage);
            if (info.second.equals(textSolution)) {
                currentScore+= Utils.VALUE_POINT;
                this.view.updateOK(idImage,currentScore);
                numOks++;
            } else {
                this.view.updateFail();
            }
        }
        if (numOks == NUM_IMAGES_FOR_ROUND) {
            finishRound();
        }
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

    @Override
    public void finishRound() {
        this.view.runPlayAgainDialog(currentScore);
    }

    @Override
    public void restartGame() {
        updateLevel();
        numOks = 0;
        numRounds++;
        int numMatchs = level.getNum();
        int numMatchsFix = level.getNumFix();
        if (this.matchSortedInfo.size() < numMatchs || this.matchSortedFixInfo.size() < numMatchsFix) {
            this.view.messageNotEnoughImages();
            return;
        }


        List<String> imagesToUse = new ArrayList<>();
        List<String> imagesFixToUse = new ArrayList<>();

        extractInfo(numMatchs, matchSortedInfo, imagesToUse);
        extractInfo(numMatchsFix,matchSortedFixInfo,imagesFixToUse);

        imagesCurrentRound = imagesToUse;
        imagesFixCurrentRound = imagesFixToUse;

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

    private void extractInfo(int numMatchs, TreeSet sortedInfo, List<String> imagesToUse) {
        List<Score> matchsToSave = new ArrayList<>();
        for (int i=0; i < numMatchs; i++) {
           Score matchInfo= (Score)sortedInfo.pollFirst();
           imagesToUse.add(matchInfo.name);
           matchInfo.score+=1;
           matchsToSave.add(matchInfo);
        }

        /* Add to the tree*/
        for (int i = 0; i < matchsToSave.size(); i++) {
            sortedInfo.add(matchsToSave.get(i));
        }
    }

    private void updateLevel() {
        if (numRounds < 4) {
            level = MatchLevel.EASY;

        } else if (numRounds < 8) {
            level = MatchLevel.MEDIUM;

        } else {
            level = MatchLevel.HARD;
        }
    }
}
