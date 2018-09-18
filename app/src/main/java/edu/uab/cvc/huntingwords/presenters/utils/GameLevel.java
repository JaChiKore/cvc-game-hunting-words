package edu.uab.cvc.huntingwords.presenters.utils;

/**
 * Created by carlosb on 19/04/18.
 */


public class GameLevel {

    public static final int THRESHOLD_LEVEL_EASY = 10;
    public static final int THRESHOLD_LEVEL_MEDIUM = 50;
    public static final int PERCENTAGE_ADD_IMAGE = 10;
    public static final int BASE_NUM_IMAGES = 10;

    private int level;
    private int num;
    private int numFix;
    public GameLevel(int level) {
        this.level = level;
        calculateDifficult();
    }

    private void calculateDifficult() {
        if (level < THRESHOLD_LEVEL_EASY) {
            int totalImages = BASE_NUM_IMAGES;
            this.num  =  (int)(totalImages * .2);
            this.numFix = (int)(totalImages * .8);
        } else if (level < THRESHOLD_LEVEL_MEDIUM) {
            int totalOffset = ((level - THRESHOLD_LEVEL_EASY) % PERCENTAGE_ADD_IMAGE);
            int totalImages = BASE_NUM_IMAGES+ totalOffset;
            this.num  = (int)(totalImages * .5);
            this.numFix = (int)(totalImages * .5);

        } else  {
            int totalOffset = ((level - THRESHOLD_LEVEL_MEDIUM) % PERCENTAGE_ADD_IMAGE );
            int totalImages = BASE_NUM_IMAGES+ totalOffset;
            this.num  = (int)(totalImages * .6);
            this.numFix = (int)(totalImages * .4);

        }
    }

    public void increase() {
        level++;
        calculateDifficult();
    }
    public void decrease() {
        level--;
        if (level <= 0) {
            level = 1;
        }
        calculateDifficult();
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