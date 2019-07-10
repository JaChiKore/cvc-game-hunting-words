package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import edu.uab.cvc.huntingwords.Utils;
import edu.uab.cvc.huntingwords.presenters.utils.Token;
import timber.log.Timber;

import static edu.uab.cvc.huntingwords.Utils.SUCCESS;

public class UpdateScore extends AsyncTask<String, Void, Boolean> {

    public UpdateScore() {}

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        boolean correct = false;
        BufferedReader bufferedReader;
        StringBuilder buffer = new StringBuilder();
        Token key = Token.getInstance();
        PostSendBuilder psb = PostSendBuilder.getInstance();

        try {
            link = Utils.BASE_URL+"/updateScore.php";  // base link: http://158.109.8.50/app_mobile/ http://158.109.9.209/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            HashMap<String, String> values = new HashMap<>();
            values.put("username", arg[0]);
            values.put("task", arg[1]);
            values.put("score", arg[2]);
            values.put("level", arg[3]);
            values.put("token", key.getToken());

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(psb.getPostData(values));
            writer.flush();
            writer.close();
            os.close();
            con.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while((next = bufferedReader.readLine()) != null) {
                buffer.append(next);
                buffer.append("\n");
            }
            try {
                JSONObject jObj = new JSONObject(buffer.toString());

                String suc = jObj.getString(SUCCESS);
                correct = suc.contentEquals("true");
            } catch (Exception e) {
                Timber.e(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}

