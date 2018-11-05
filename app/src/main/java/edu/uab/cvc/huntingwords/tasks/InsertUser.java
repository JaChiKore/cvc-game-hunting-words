package edu.uab.cvc.huntingwords.tasks;

import android.os.AsyncTask;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;

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
import edu.uab.cvc.huntingwords.presenters.utils.Token;

import static edu.uab.cvc.huntingwords.Utils.SUCCESS;
import static edu.uab.cvc.huntingwords.Utils.TOKEN;

@SuppressWarnings("WeakerAccess")
public class InsertUser extends AsyncTask<String, Void, Boolean> {

    private ConnectCallback onResult;
    public InsertUser(ConnectCallback onResult) {
        this.onResult = onResult;
    }

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        BufferedReader bufferedReader;
        StringBuilder buffer = new StringBuilder();
        boolean correct;
        Token key = Token.getInstance();
        PostSendBuilder psb = PostSendBuilder.getInstance();

        try {
            link = Utils.BASE_URL+"/insertUser.php";  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            HashMap<String, String> values = new HashMap<>();
            values.put("username", arg[0]);
            values.put("password", arg[1]);

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

            String suc = jObj.getString(SUCCESS);
            String tok = jObj.getString(TOKEN);

            correct = suc.contentEquals("true");
            if (correct) {
                key.setToken(tok);
                Answers.getInstance().logSignUp(new SignUpEvent()
                        .putMethod("Normal signin")
                        .putSuccess(true));
                onResult.updateLogin(arg[0], arg[1]);
            } else {
                Answers.getInstance().logSignUp(new SignUpEvent()
                    .putMethod("Normal signin")
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
