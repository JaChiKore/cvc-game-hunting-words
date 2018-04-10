package edu.uab.cvc.huntingwords.tasks.updates;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateTranscriptions extends AsyncTask<String, Void, Boolean> {

    public UpdateTranscriptions() {}

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        boolean correct = false;
        BufferedReader bufferedReader;

        try {
            link = "http://158.109.8.50/app_mobile/updateTranscription.php?filename=" + arg[0]
                                                                        + "&trans=" + arg[1]
                                                                        + "&user=" + arg[2]
                                                                        + "&level=" + arg[3]
                                                                        + "&startDate=" + arg[4]
                                                                        + "&endDate=" + arg[5]
                                                                        + "&scoreInici=" + arg[6]
                                                                        + "&scoreFinal=" + arg[7];  // base link: http://158.109.8.50/app_mobile/ http://158.109.9.209/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            next = bufferedReader.readLine();

            correct = next.contentEquals("True");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}
