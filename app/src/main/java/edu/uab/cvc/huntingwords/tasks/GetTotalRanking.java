package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.uab.cvc.huntingwords.presenters.callbacks.TotalRankingCallback;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class GetTotalRanking extends AsyncTask<String, Void, String[]> {

    public static final String DIFFERENCE = "difference";
    public static final String MATCH = "match";
    private final TotalRankingCallback onResult;
    public GetTotalRanking(TotalRankingCallback onResult) {
        this.onResult = onResult;
    }


    protected void onPreExecute() {}

    @Override
    protected String[] doInBackground(String... arg) {
        String link;
        String next;
        String text = "";
        String[] rows = new String[2];
        BufferedReader bufferedReader;

        try {
            link = "http://158.109.8.50/app_mobile/getRanking.php?username=";  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer content = new StringBuffer();
            while ((next = bufferedReader.readLine()) != null) {
                content.append(next);
            }

            String [] ranks = content.toString().split("separator<br>");
            String [] infoDiff = ranks[0].split("<br>");
            String [] infoMatch = ranks[1].split("<br>");

            if (arg[0].equals(DIFFERENCE)) {
                List<Pair<String, String>> rankingDifference = new ArrayList<>();
                for (String line : infoDiff) {
                    String[] values = line.split(",");
                    rankingDifference.add(Pair.create(values[0], values[1]));
                }
                onResult.updateTotalRanking(rankingDifference);
            } else if (arg[0].equals(MATCH)) {
                List<Pair<String,String>> rankingMatch = new ArrayList<>();
                for (String line: infoMatch) {
                    String[] values = line.split(",");
                    rankingMatch.add(Pair.create(values[0], values[1]));
                }
                onResult.updateTotalRanking(rankingMatch);
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        return rows;
    }

    @Override
    protected void onPostExecute(String[] a) {}


}