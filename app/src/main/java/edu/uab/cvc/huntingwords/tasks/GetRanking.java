package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class GetRanking extends AsyncTask<String, Void, String[]> {



    private final ConnectCallback onResult;
    public GetRanking(ConnectCallback onResult) {
        this.onResult = onResult;
    }


    protected void onPreExecute() {}

    @Override
    protected String[] doInBackground(String... arg) {
        String link;
        String next;
        String[] rows = new String[2];
        BufferedReader bufferedReader;

        try {
            link = "http://158.109.8.50/app_mobile/getRanking.php?username=" + arg[0];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            next = bufferedReader.readLine();
            rows = next.split(",");
            onResult.updateScore(Integer.valueOf(rows[1]),Integer.valueOf(rows[0]));
        } catch (Exception e) {
            Timber.e(e);
        }

        return rows;
    }

    @Override
    protected void onPostExecute(String[] a) {}


}
