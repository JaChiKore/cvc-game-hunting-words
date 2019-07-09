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
    private final boolean win;
    public DifferenceService(String user, boolean win) {
        this.user = user;
        this.win = win;
    }

    public void run (List<ClusterDifferentResult> values, String level, String startDate, String stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user, "2",String.valueOf(scoreEnd), level};
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
