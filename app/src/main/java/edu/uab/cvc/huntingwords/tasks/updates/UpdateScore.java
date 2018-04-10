package edu.uab.cvc.huntingwords.tasks.updates;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("WeakerAccess")
public class UpdateScore extends AsyncTask<String, Void, Boolean> {

    public UpdateScore() {}

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        boolean correct = false;
        BufferedReader bufferedReader;

        try {
            link = "http://158.109.8.50/app_mobile/updateScore.php?username=" + arg[0] + "&match_game=" + arg[1] + "&difference_game=" + arg[2];  // base link: http://158.109.8.50/app_mobile/ http://158.109.9.209/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            next = bufferedReader.readLine();

            correct = next.contentEquals("true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}

