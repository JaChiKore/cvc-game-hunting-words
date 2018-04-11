package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.models.UserInfo;

@SuppressWarnings("WeakerAccess")
public class Login extends AsyncTask<String, Void, Boolean> {

    private  Boolean logged;
    public Login(Boolean logged) {
        this.logged = logged;
    }

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        BufferedReader bufferedReader;
        boolean correct;

        try {
            Log.d("DRAAGU/login","login:"+arg[0]+":"+arg[1]);
            link = "http://158.109.8.50/app_mobile/login.php?username=" + arg[0] + "&password=" + arg[1];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            next = bufferedReader.readLine();

            correct = next.contentEquals("true");
            Log.d("DRAAGU/login","result:"+correct);
//            logged;


        } catch (Exception e) {
            e.printStackTrace();
            correct = false;
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}
