package edu.uab.cvc.huntingwords.models;

/**
 * Created by carlosb on 23/04/18.
 */

public class MatchResult {
    private final String imageName;
    private final String translation;


    public MatchResult(String imageName, String translation) {
        this.imageName = imageName;
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    public String getImageName() {
        return imageName;
    }
}
