package edu.uab.cvc.huntingwords.tasks.match;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class GetMatchGameFixInfo extends AsyncTask<String, Void, Boolean>{

    private Context context;
    private String text;

    public GetMatchGameFixInfo(Context context) {
        this.context = context;
        this.text = "";
    }

    protected void onPreExecute() {}

    @Override
    protected Boolean doInBackground(String... arg) {
        String link;
        String next;
        BufferedReader br;
        boolean correct;
        File f;

        try {
            link = "http://158.109.8.50/app_mobile/matchGameFixInfo.php?list=" + arg[1];  // base link: http://158.109.8.50/app_mobile/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((next = br.readLine()) != null) {
                text = text + next;
            }

            String[] rows = text.split("<br>");
            if (rows.length > 0) {

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
                        System.out.println("Directory created (match game fix): " + f.getAbsolutePath());
                    }
                }

                fos.flush();
                fos.close();

                correct = true;
            } else {
                correct = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            correct = false;
        }

        return correct;
    }
}
