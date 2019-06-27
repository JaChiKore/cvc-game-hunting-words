package edu.uab.cvc.huntingwords.presenters.utils;

import static edu.uab.cvc.huntingwords.Utils.NUM_LIVES;

/**
 * Created by carlosb on 19/04/18.
 */


public class GameLevel {

    private static final int LEVEL_THRESHOLD = 4;
    private static final int BASE_NUM_IMAGES = 10;

    private int gameLevel;
    private int anotherLevel;
    private int num;
    private int numFix;
    public GameLevel(int thisGameLevel, int anotherGameLevel) {
        this.gameLevel = thisGameLevel;
        this.anotherLevel = anotherGameLevel;
        calculateDifficult();
    }

    public int getLives() {
        return ((this.numFix <= NUM_LIVES) ? ((this.numFix <= 0) ? 1 : this.numFix) : NUM_LIVES);
    }

    private void calculateDifficult() {
        if (gameLevel < LEVEL_THRESHOLD) {
            this.numFix = BASE_NUM_IMAGES - gameLevel*2;
            this.num  =  BASE_NUM_IMAGES - this.numFix;
        } else {
            this.numFix = 2;
            this.num = BASE_NUM_IMAGES - this.numFix;
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