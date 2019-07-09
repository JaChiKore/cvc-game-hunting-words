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
    private final boolean win;
    public MatchService(String user, boolean win) {
        this.user = user;
        this.win = win;
    }

    public void run (List<MatchResult> values, String level, String startDate, String stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user,"1",String.valueOf(scoreEnd), level};
        if (scoreEnd > maxScore) {
            new UpdateScore().execute(argsScore);
        } else {
            argsScore[1] = String.valueOf(maxScore);
            new UpdateScore().execute(argsScore);
        }
        if (win) {
            for (MatchResult result : values) {
                String[] args = {result.getImageName(), result.getTranslation(), user, level, startDate, stopDate, String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
                new UpdateTranscriptions().execute(args);
            }
        }

    }

}