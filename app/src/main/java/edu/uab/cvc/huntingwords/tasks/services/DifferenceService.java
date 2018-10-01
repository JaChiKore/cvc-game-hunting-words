package edu.uab.cvc.huntingwords.tasks.services;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.uab.cvc.huntingwords.models.ClusterDifferentResult;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;
import edu.uab.cvc.huntingwords.tasks.difference.UpdateClusters;

/**
 * Created by carlosb on 24/04/18.
 */

public class DifferenceService {
    private final DateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
    private final String user;
    private final float scoreMatch;
    private final int levelMatch;
    public DifferenceService(String user, float scoreMatch, int levelMatch) {
        this.user = user;
        this.scoreMatch = scoreMatch;
        this.levelMatch = levelMatch;
    }

    public void run (List<ClusterDifferentResult> values, String level, Date startDate, Date stopDate, long usedTime, float scoreIni, float scoreEnd, float maxScore) {
        String [] argsScore = {user, String.valueOf(scoreMatch),String.valueOf(scoreEnd), String.valueOf(levelMatch), level};
        if (scoreEnd > maxScore) {
            new UpdateScore().execute(argsScore);
        } else {
            argsScore[2] = String.valueOf(maxScore);
            new UpdateScore().execute(argsScore);
        }
        for (ClusterDifferentResult result: values) {
            String [] args = {result.getImageName(),result.getClusterName(), user, level, df.format(startDate), df.format(stopDate), String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
            new UpdateClusters().execute(args);
        }
    }
}
