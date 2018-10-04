package edu.uab.cvc.huntingwords.tasks.difference;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uab.cvc.huntingwords.Utils;


public class UpdateClusters extends AsyncTask<String, Void, Boolean> {

    public UpdateClusters() {}

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        boolean correct = false;
        BufferedReader br;

        try {
            link = Utils.BASE_URL+"/updateCluster.php?filename=" + arg[0]
                                                                        + "&cluster=" + arg[1]
                                                                        + "&user=" + arg[2]
                                                                        + "&level=" + arg[3]
                                                                        + "&startDate=" + arg[4]
                                                                        + "&endDate=" + arg[5]
                                                                        + "&usedTime=" + arg[6]
                                                                        + "&scoreInici=" + arg[7]
                                                                        + "&scoreFinal=" + arg[8];  // base link: http://158.109.8.50/app_mobile/
            for (String a: arg) {
                System.out.println(a);
            }
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();

            while ((next = br.readLine()) != null) {
                sb.append(next);
            }
            next = sb.toString();
            correct = next.contentEquals("True");
            System.out.println(correct);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return correct;
    }

    @Override
    protected void onPostExecute(Boolean a) {}
}
