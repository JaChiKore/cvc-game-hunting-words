package edu.uab.cvc.huntingwords.tasks.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.uab.cvc.huntingwords.models.MatchResult;
import edu.uab.cvc.huntingwords.tasks.match.UpdateTranscriptions;

/**
 * Created by carlosb on 23/04/18.
 */

public class MatchService {
    private final DateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
    private final String user;
    public MatchService(String user) {
        this.user = user;
    }

    public void run (List<MatchResult> values, String level, Date startDate, Date stopDate, float scoreIni, float scoreEnd) {
        for (MatchResult result: values) {
            String [] args = {result.getImageName(),result.getTranslation(), user, level, df.format(startDate), df.format(stopDate), String.valueOf(scoreIni), String.valueOf(scoreEnd)};
            new UpdateTranscriptions().execute(args);
        }

    }

}
