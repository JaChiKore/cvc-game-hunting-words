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
import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import timber.log.Timber;

import static edu.uab.cvc.huntingwords.Utils.SUCCESS;

@SuppressWarnings("WeakerAccess")
public class GetRanking extends AsyncTask<String, Void, String[]> {



    private final ConnectCallback onResult;
    private final Boolean playClicked;
    public GetRanking(ConnectCallback onResult, Boolean playClicked) {
        this.onResult = onResult;
        this.playClicked = playClicked;
    }


    protected void onPreExecute() {}

    @Override
    protected String[] doInBackground(String... arg) {
        String link;
        String next;
        String[] rows = new String[2];
        BufferedReader bufferedReader;
        StringBuilder buffer = new StringBuilder();
        PostSendBuilder psb = PostSendBuilder.getInstance();

        try {
            link = Utils.BASE_URL+"/getRanking.php";  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            HashMap<String, String> values = new HashMap<>();
            values.put("username", arg[0]);

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
            JSONObject jObj = new JSONObject(buffer.toString());
            next = jObj.getString(SUCCESS);
            rows = next.split(",");
            onResult.updateScore(Integer.valueOf(rows[0]),Integer.valueOf(rows[1]),Integer.valueOf(rows[2]),Integer.valueOf(rows[3]),Integer.valueOf(rows[4]),Integer.valueOf(rows[5]));
        } catch (Exception e) {
            Timber.e(e);
        }

        return rows;
    }

    @Override
    protected void onPostExecute(String[] a) {
        onResult.setUpScores(playClicked);
    }


}
