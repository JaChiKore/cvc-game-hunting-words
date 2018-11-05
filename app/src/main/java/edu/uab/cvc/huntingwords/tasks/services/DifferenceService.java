package edu.uab.cvc.huntingwords.tasks.services;

import java.util.List;

import edu.uab.cvc.huntingwords.models.ClusterDifferentResult;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;
import edu.uab.cvc.huntingwords.tasks.difference.UpdateClusters;

/**
 * Created by carlosb on 24/04/18.
 */

public class DifferenceService {
    private final String user;
    private final float scoreMatch;
    private final int levelMatch;
    private final boolean win;
    public DifferenceService(String user, float scoreMatch, int levelMatch, boolean win) {
        this.user = user;
        this.scoreMatch = scoreMatch;
        this.levelMatch = levelMatch;
        this.win = win;
    }

    public void run (List<ClusterDifferentResult> values, String level, String startDate, String stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user, String.valueOf(scoreMatch),String.valueOf(scoreEnd), String.valueOf(levelMatch), level};
        if (scoreEnd > maxScore) {
            new UpdateScore().execute(argsScore);
        } else {
            argsScore[2] = String.valueOf(maxScore);
            new UpdateScore().execute(argsScore);
        }
        if (win) {
            for (ClusterDifferentResult result : values) {
                String[] args = {result.getImageName(), result.getClusterName(), user, level, startDate, stopDate, String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
                new UpdateClusters().execute(args);
            }
        }
    }
}
