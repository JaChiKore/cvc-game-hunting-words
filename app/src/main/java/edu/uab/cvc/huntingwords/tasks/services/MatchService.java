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
    private final float scoreDifference;
    private final int levelDiff;
    public MatchService(String user, float scoreDifference, int levelDiff) {
        this.user = user;
        this.scoreDifference = scoreDifference;
        this.levelDiff = levelDiff;
    }

    public void run (List<MatchResult> values, String level, String startDate, String stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user,String.valueOf(scoreEnd),String.valueOf(scoreDifference), level, String.valueOf(levelDiff)};
        if (scoreEnd > maxScore) {
            try {
                Boolean a = new UpdateScore().execute(argsScore).get();
                if (a) {
                    System.out.println("B: Score updated nicely.");
                } else {
                    System.out.println("B: Score bad update.");
                }
            } catch (Exception e) {
                System.out.println("Error updating score, bigger one.");
            }
        } else {
            argsScore[1] = String.valueOf(maxScore);
            try {
                Boolean a = new UpdateScore().execute(argsScore).get();
                if (a) {
                    System.out.println("S: Score updated nicely.");
                } else {
                    System.out.println("S: Score bad update.");
                }
            } catch (Exception e) {
                System.out.println("Error updating score, smaller one.");
            }
        }
        for (MatchResult result: values) {
            String [] args = {result.getImageName(),result.getTranslation(), user, level, startDate, stopDate, String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
            new UpdateTranscriptions().execute(args);
        }

    }

}
