package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.models.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@SuppressWarnings("WeakerAccess")
public class GetRanking extends AsyncTask<String, Void, String[]> {

    public GetRanking() {}

    protected void onPreExecute() {}

    @Override
    protected String[] doInBackground(String... arg) {
        String link;
        String next;
        String text = "";
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

            if (arg[0].contentEquals("")) {
                while ((next = bufferedReader.readLine()) != null) {
                        text = text + next;
                }
                rows = text.split("separator<br>");
            } else {
                next = bufferedReader.readLine();
                rows = next.split(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    @Override
    protected void onPostExecute(String[] a) {}


}
