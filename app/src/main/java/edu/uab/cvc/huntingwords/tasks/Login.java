package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.Utils;
import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;

@SuppressWarnings("WeakerAccess")
public class Login extends AsyncTask<String, Void, Boolean> {

    private ConnectCallback onResult;
    public Login(ConnectCallback onResult) {
        this.onResult = onResult;
    }





    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        BufferedReader bufferedReader;
        boolean correct;

        try {
            link = Utils.BASE_URL+"/login.php?username=" + arg[0] + "&password=" + arg[1];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            next = bufferedReader.readLine();

            correct = next.contentEquals("true");
            if (correct ) {
                onResult.updateLogin(arg[0], arg[1]);
            } else {
                onResult.error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            correct = false;
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}
