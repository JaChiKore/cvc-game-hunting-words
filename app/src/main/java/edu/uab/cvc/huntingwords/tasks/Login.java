package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

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
import edu.uab.cvc.huntingwords.presenters.utils.Token;

@SuppressWarnings("WeakerAccess")
public class Login extends AsyncTask<String, Void, Boolean> {

    private final static String SUCCESS = "res";
    private final static String TOKEN = "token";
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
        StringBuffer buffer = new StringBuffer();
        JSONArray jsonArr;
        boolean correct;
        Token key = Token.getInstance();
        PostSendBuilder psb = PostSendBuilder.getInstance();

        key.generateToken();

        try {
            link = Utils.BASE_URL+"/login.php";  // base link: http://158.109.8.50/app_mobile/ ?username=" + arg[0] + "&password=" + arg[1]
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            HashMap<String, String> values = new HashMap<>();
            values.put("username", arg[0]);
            values.put("password", arg[1]);
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

            jsonArr = new JSONArray(buffer.toString());

            String suc = jsonArr.getJSONObject(0).getString(SUCCESS);
            String tok = jsonArr.getJSONObject(0).getString(TOKEN);

            correct = suc.contentEquals("true");
            if (correct) {
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Normal login")
                        .putSuccess(true));
                onResult.updateLogin(arg[0], arg[1]);
                key.setToken(tok);
            } else {
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Normal login")
                        .putSuccess(false));
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
