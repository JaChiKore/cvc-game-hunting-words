package edu.uab.cvc.huntingwords.tasks.match;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.Utils;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class CheckNewMatchGameFixImages extends AsyncTask<String, Void, Boolean>{

    public CheckNewMatchGameFixImages() {
    }

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link, next, text = "";
        BufferedReader br;
        String[] rows;

        try {
            link = Utils.BASE_URL+"/matchGameFixInfo.php?list=" + arg[0];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((next = br.readLine()) != null) {
                text = text + next;
            }

            rows = text.split("<br>");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return ((rows.length > 0) && !text.isEmpty());
    }
}
