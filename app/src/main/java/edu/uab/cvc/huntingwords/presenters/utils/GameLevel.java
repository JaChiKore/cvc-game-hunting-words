package edu.uab.cvc.huntingwords.presenters.utils;

/**
 * Created by carlosb on 19/04/18.
 */


public class GameLevel {

    public static final int THRESHOLD_LEVEL_EASY = 10;
    public static final int THRESHOLD_LEVEL_MEDIUM = 50;
    public static final int PERCENTAGE_ADD_IMAGE = 10;
    public static final int BASE_NUM_IMAGES = 10;

    private int gameLevel;
    private int anotherLevel;
    private int num;
    private int numFix;
    public GameLevel(int thisGameLevel, int anotherGameLevel) {
        this.gameLevel = thisGameLevel;
        this.anotherLevel = anotherGameLevel;
        calculateDifficult();
    }

    private void calculateDifficult() {
        if (gameLevel < THRESHOLD_LEVEL_EASY) {
            int totalImages = BASE_NUM_IMAGES;
            this.num  =  (int)(totalImages * .2);
            this.numFix = (int)(totalImages * .8);
        } else if (gameLevel < THRESHOLD_LEVEL_MEDIUM) {
            int totalOffset = ((gameLevel - THRESHOLD_LEVEL_EASY) % PERCENTAGE_ADD_IMAGE);
            int totalImages = BASE_NUM_IMAGES+ totalOffset;
            this.num  = (int)(totalImages * .5);
            this.numFix = (int)(totalImages * .5);

        } else  {
            int totalOffset = ((gameLevel - THRESHOLD_LEVEL_MEDIUM) % PERCENTAGE_ADD_IMAGE );
            int totalImages = BASE_NUM_IMAGES+ totalOffset;
            this.num  = (int)(totalImages * .6);
            this.numFix = (int)(totalImages * .4);

        }
    }

    public void increase() {
        gameLevel++;
        calculateDifficult();
    }
    public void decrease() {
        gameLevel--;
        if (gameLevel <= 0) {
            gameLevel = 1;
        }
        calculateDifficult();
    }
    public int getLevel() {
        return gameLevel;
    }
    public int getAnotherLevel() {return anotherLevel; }

    public int getNum() {
        return num;
    }

    public int getNumFix() {
        return numFix;
    }
}