package edu.uab.cvc.huntingwords.tasks.services;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.uab.cvc.huntingwords.models.ClusterDifferentResult;
import edu.uab.cvc.huntingwords.tasks.difference.UpdateClusters;

/**
 * Created by carlosb on 24/04/18.
 */

public class DifferenceService {
    private final DateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
    private final String user;
    public DifferenceService(String user) {
        this.user = user;
    }

    public void run (List<ClusterDifferentResult> values, String level, Date startDate, Date stopDate, float scoreIni, float scoreEnd) {
        for (ClusterDifferentResult result: values) {
            String [] args = {result.getImageName(),result.getClusterName(), user, level, df.format(startDate), df.format(stopDate), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
            new UpdateClusters().execute(args);
        }
    }
}
