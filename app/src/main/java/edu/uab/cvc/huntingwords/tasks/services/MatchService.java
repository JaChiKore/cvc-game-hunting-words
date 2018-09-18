package edu.uab.cvc.huntingwords.tasks.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.uab.cvc.huntingwords.models.MatchResult;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;
import edu.uab.cvc.huntingwords.tasks.match.UpdateTranscriptions;

/**
 * Created by carlosb on 23/04/18.
 */

public class MatchService {
    private final DateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
    private final String user;
    private final float scoreDifference;
    private final int levelDiff;
    public MatchService(String user, float scoreDifference, int levelDiff) {
        this.user = user;
        this.scoreDifference = scoreDifference;
        this.levelDiff = levelDiff;
    }

    public void run (List<MatchResult> values, String level, Date startDate, Date stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user,String.valueOf(scoreEnd),String.valueOf(scoreDifference), level, String.valueOf(levelDiff)};
        if (scoreEnd > maxScore) {
            new UpdateScore().execute(argsScore);
        } else {
            argsScore[2] = String.valueOf(maxScore);
            new UpdateScore().execute(argsScore);
        }
        for (MatchResult result: values) {
            String [] args = {result.getImageName(),result.getTranslation(), user, level, df.format(startDate), df.format(stopDate), String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
            new UpdateTranscriptions().execute(args);
        }

    }

}
