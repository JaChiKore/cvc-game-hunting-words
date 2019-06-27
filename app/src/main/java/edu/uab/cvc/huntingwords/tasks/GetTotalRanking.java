package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.uab.cvc.huntingwords.Utils;
import edu.uab.cvc.huntingwords.presenters.callbacks.TotalRankingCallback;
import timber.log.Timber;
import static edu.uab.cvc.huntingwords.Utils.SUCCESS;

@SuppressWarnings("WeakerAccess")
public class GetTotalRanking extends AsyncTask<String, Void, String[]> {

    public static final String DIFFERENCE = "difference";
    public static final String MATCH = "match";
    public static final String JUMP = "jump";
    private final TotalRankingCallback onResult;
    public GetTotalRanking(TotalRankingCallback onResult) {
        this.onResult = onResult;
    }


    protected void onPreExecute() {}

    @Override
    protected String[] doInBackground(String... arg) {
        String link;
        String next;
        String[] rows = new String[2];
        BufferedReader bufferedReader;
        StringBuilder buffer = new StringBuilder();

        try {
            link = Utils.BASE_URL+"/getRanking.php";  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while((next = bufferedReader.readLine()) != null) {
                buffer.append(next);
                buffer.append("\n");
            }
            JSONObject jObj = new JSONObject(buffer.toString());

            String content = jObj.getString(SUCCESS);
            String [] ranks = content.split("separator<br>");
            String [] infoMatch = ranks[0].split("<br>");
            String [] infoDiff = ranks[1].split("<br>");
            String [] infoJump = ranks[2].split("<br>");

            List<Pair<String, String>> ranking = new ArrayList<>();
            if (arg[0].equals(DIFFERENCE)) {
                for (String line : infoDiff) {
                    String[] values = line.split(",");
                    ranking.add(Pair.create(values[0], values[1]));
                }
            } else if (arg[0].equals(MATCH)) {
                for (String line: infoMatch) {
                    String[] values = line.split(",");
                    ranking.add(Pair.create(values[0], values[1]));
                }
            } else if (arg[0].equals(JUMP)) {
                for (String line: infoJump) {
                    String[] values = line.split(",");
                    ranking.add(Pair.create(values[0], values[1]));
                }
            }

            onResult.updateTotalRanking(ranking);

        } catch (Exception e) {
            Timber.e(e);
        }

        return rows;
    }

    @Override
    protected void onPostExecute(String[] a) {}


}