package edu.uab.cvc.huntingwords.tasks.services;

import java.util.List;

import edu.uab.cvc.huntingwords.models.MatchResult;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;
import edu.uab.cvc.huntingwords.tasks.match.UpdateTranscriptions;

/**
 * Created by carlosb on 23/04/18.
 */

public class MatchService {
    private final String user;
    private final String pass;
    private final float scoreDifference;
    private final int levelDiff;
    public MatchService(String user, String pass, float scoreDifference, int levelDiff) {
        this.user = user;
        this.pass = pass;
        this.scoreDifference = scoreDifference;
        this.levelDiff = levelDiff;
    }

    public void run (List<MatchResult> values, String level, String startDate, String stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user, pass,String.valueOf(scoreEnd),String.valueOf(scoreDifference), level, String.valueOf(levelDiff)};
        if (scoreEnd > maxScore) {
            new UpdateScore().execute(argsScore);
        } else {
            argsScore[1] = String.valueOf(maxScore);
            new UpdateScore().execute(argsScore);
        }
        for (MatchResult result: values) {
            String [] args = {result.getImageName(),result.getTranslation(), user, pass, level, startDate, stopDate, String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
            new UpdateTranscriptions().execute(args);
        }

    }

}