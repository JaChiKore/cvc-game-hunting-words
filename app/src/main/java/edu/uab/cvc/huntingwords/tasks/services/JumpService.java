package edu.uab.cvc.huntingwords.tasks.services;

import java.util.ArrayList;
import edu.uab.cvc.huntingwords.models.JumpClusterResult;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;
import edu.uab.cvc.huntingwords.tasks.jump.UpdateJumpClusters;

/**
 * Created by carlosb on 24/04/18.
 */

public class JumpService {
    private final String user;
    private final boolean win;
    public JumpService(String user, boolean win) {
        this.user = user;
        this.win = win;
    }

    public void run (ArrayList<JumpClusterResult> values, String level, String startDate, String stopDate, long usedTime, float scoreIni, float scoreEnd) {
        if (win) {
            for (JumpClusterResult result : values) {
                String[] args = {result.getImageName(), result.getAnswer(), result.getClusterName().split("-")[0], user, level, startDate, stopDate, String.valueOf(usedTime), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
                new UpdateJumpClusters().execute(args);
            }
        }
        String [] argsScore = {user, "3",String.valueOf(scoreEnd), level};
        new UpdateScore().execute(argsScore);
    }
}
