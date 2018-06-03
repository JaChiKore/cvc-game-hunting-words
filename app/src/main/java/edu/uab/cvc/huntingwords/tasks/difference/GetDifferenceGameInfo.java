package edu.uab.cvc.huntingwords.tasks.difference;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.uab.cvc.huntingwords.Utils;

@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
public class GetDifferenceGameInfo extends AsyncTask<String, Void, String> {

    private Context context;
    private String text;

    public GetDifferenceGameInfo(Context context) {
        this.context = context;
        this.text = "";
    }

    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... arg) {
        String link;
        String next;
        BufferedReader br;
        String correct;
        File f;

        try {
            link = Utils.BASE_URL+"/differenceGameInfo.php";  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((next = br.readLine()) != null) {
                text = text + next;
            }

            String[] rows = text.split("<br>");

            if (!text.isEmpty()) {
                int aux = 0;
                ArrayList<String> auxList = null;

                f = new File(context.getFilesDir(), arg[0]);
                if (f.exists()) {
                    String num;
                    br = new BufferedReader(new FileReader(f));
                    auxList = new ArrayList<>();

                    if ((num = br.readLine()) != null) {
                        aux = Integer.valueOf(num);
                    }

                    if (aux > 0) {
                        while ((next = br.readLine()) != null) {
                            auxList.add(next);
                        }
                    }
                    br.close();
                    f.delete();
                }

                FileOutputStream fos = new FileOutputStream(f);

                fos.write(String.valueOf((aux + rows.length)).getBytes());
                fos.write(System.getProperty("line.separator").getBytes());
                //noinspection ForLoopReplaceableByForEach
                if (f.exists() && aux > 0) {
                    for (int i = 0; i < auxList.size(); i++) {
                        fos.write(auxList.get(i).getBytes());
                        fos.write(System.getProperty("line.separator").getBytes());
                    }
                }
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < rows.length; i++) {
                    fos.write(rows[i].getBytes());
                    fos.write(System.getProperty("line.separator").getBytes());

                    String[] split = rows[i].split(";");
                    split = split[0].split("/");
                    String path = context.getFilesDir().getAbsolutePath() + File.separator;

                    for (int j = 0; j < split.length - 1; j++) {
                        path += split[j] + File.separator;
                    }

                    f = new File(path);
                    if (!f.exists()) {
                        f.mkdirs();
                        System.out.println("Directory created (difference game): " + f.getAbsolutePath());
                    }
                }

                fos.flush();
                fos.close();

                f = new File(context.getFilesDir(), "differenceGame/");
                if (f.exists()) {
                    f.delete();
                    f.mkdirs();
                } else {
                    f.mkdirs();
                }

                //separate clusters

                f = new File(context.getFilesDir(), arg[0]);

                br = new BufferedReader(new FileReader(f));
                ArrayList<String> imgNames = new ArrayList<>();
                String[] split;
                String clusterName;

                br.readLine();
                next = br.readLine();
                split = next.split(";");
                clusterName = split[1];
                imgNames.add(next);
                while((next = br.readLine()) != null) {
                    split = next.split(";");
                    if (!split[1].equals(clusterName)) {
                        File auxF = new File(context.getFilesDir(), "differenceGame/" + String.valueOf(imgNames.size()) + "-" + clusterName + ".txt");
                        fos = new FileOutputStream(auxF);
                        for (int i = 0; i < imgNames.size(); i++) {
                            fos.write(imgNames.get(i).getBytes());
                            fos.write(System.getProperty("line.separator").getBytes());
                        }
                        fos.flush();
                        fos.close();

                        clusterName = split[1];
                        imgNames = new ArrayList<>();
                    }
                    imgNames.add(next);
                }

                File auxF = new File(context.getFilesDir(), "differenceGame/" + String.valueOf(imgNames.size()) + "-" + clusterName + ".txt");
                fos = new FileOutputStream(auxF);
                for (int i = 0; i < imgNames.size(); i++) {
                    fos.write(imgNames.get(i).getBytes());
                    fos.write(System.getProperty("line.separator").getBytes());
                }
                fos.flush();
                fos.close();

                correct = "3";
            } else {
                correct = "-1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            correct = "-1";
        }

        return correct;
    }

    @Override
    protected void onPostExecute(String a) {
    }
}
