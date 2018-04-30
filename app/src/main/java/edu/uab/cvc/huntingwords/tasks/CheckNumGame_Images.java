package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.Utils;
import timber.log.Timber;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class CheckNumGame_Images extends AsyncTask<String, Void, Integer>{

    public CheckNumGame_Images() {}

    protected void onPreExecute() {}

    @Override
    protected Integer doInBackground(String... arg) {
        String link;
        BufferedReader bufferedReader;
        int correct;

        try { // base link: 158.109.8.50/mobile_games
            Timber.v("Trying to get num of available images");
            link = Utils.BASE_URL+"/checkNumGame_Images.php?game=" + arg[0];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            correct = Integer.valueOf(bufferedReader.readLine());
        } catch (Exception e) {
            Timber.e(e);
            correct = -1;
        }

        return correct;
    }
}
