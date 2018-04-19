package edu.uab.cvc.huntingwords.presenters.utils;

/**
 * Created by carlosb on 19/04/18.
 */


public enum GameLevel {
    EASY (0,4,8),
    MEDIUM(1,6,6),
    HARD(2,8,4);

    private final int level;
    private final int num;
    private final int numFix;
    GameLevel(int level, int num, int numFix) {
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