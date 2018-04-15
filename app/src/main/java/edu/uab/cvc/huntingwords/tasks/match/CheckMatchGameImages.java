package edu.uab.cvc.huntingwords.tasks.match;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class CheckMatchGameImages extends AsyncTask<String, Void, Integer>{

    public CheckMatchGameImages() {}

    protected void onPreExecute() {}

    @Override
    protected Integer doInBackground(String... arg) {
        String link;
        BufferedReader bufferedReader;
        int correct;

        try { // base link: 158.109.8.50/mobile_games
            link = "http://158.109.8.50/app_mobile/checkMatchGameImages.php?image=" + arg[0];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String[] values = bufferedReader.readLine().split(",");
            if (values[0].contains(arg[1])) {
                correct = 1;
            } else {
                if (values[1].contains("1")) {
                    correct = 2;
                } else {
                    correct = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            correct = -1;
        }

        return correct;
    }
}
