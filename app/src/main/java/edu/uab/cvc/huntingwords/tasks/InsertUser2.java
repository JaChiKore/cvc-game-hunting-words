package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.subjects.Subject;

@SuppressWarnings("WeakerAccess")
public class InsertUser2 extends AsyncTask<String, Void, Boolean> {

    private Subject signedIn;
    public InsertUser2(Subject signedIn) {
        this.signedIn = signedIn;
    }

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        BufferedReader bufferedReader;
        boolean correct;

        try {
            link = "http://158.109.8.50/app_mobile/insertUser.php?username=" + arg[0] + "&password=" + arg[1];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));


            next = bufferedReader.readLine();

            correct = next.contentEquals("true");
            signedIn.onNext(correct);

        } catch (Exception e) {
            e.printStackTrace();
            correct = false;
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}
