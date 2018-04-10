package edu.uab.cvc.huntingwords.tasks.difference;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class GetDifferenceGameFixInfo extends AsyncTask<String, Void, Boolean>{

    private Context context;
    private String text;

    public GetDifferenceGameFixInfo(Context context) {
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
            link = "http://158.109.8.50/app_mobile/differenceGameFixInfo.php?list=" + arg[1];  // base link: http://158.109.8.50/app_mobile/
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
                        System.out.println("Directory created (difference game fix): " + f.getAbsolutePath());
                    }
                }

                fos.flush();
                fos.close();

                f = new File(context.getFilesDir(), "differenceGameFix/");
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

                // the first read have the items number of this txt
                int num = Integer.valueOf(br.readLine());
                System.out.println("num of fix images: " + num);

                // now in next we have imatge_nom;cluster_id;different;validat
                if (num > 0) {
                    next = br.readLine();
                    System.out.println("fix image: " + next);
                    split = next.split(";");
                    clusterName = split[1];
                    imgNames.add(next);
                    while ((next = br.readLine()) != null) {
                        split = next.split(";");
                        if (!split[1].equals(clusterName)) {
                            File auxF = new File(context.getFilesDir(), "differenceGameFix/" + String.valueOf(imgNames.size()) + "-" + clusterName + ".txt");
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

                    File auxF = new File(context.getFilesDir(), "differenceGameFix/" + String.valueOf(imgNames.size()) + "-" + clusterName + ".txt");
                    fos = new FileOutputStream(auxF);
                    for (int i = 0; i < imgNames.size(); i++) {
                        fos.write(imgNames.get(i).getBytes());
                        fos.write(System.getProperty("line.separator").getBytes());
                    }
                    fos.flush();
                    fos.close();

                    correct = true;
                } else {
                    correct = false;
                }
            } else {
                f = new File(context.getFilesDir(), arg[0]);
                FileOutputStream fos = new FileOutputStream(f);

                fos.write("0".getBytes());
                fos.write(System.getProperty("line.separator").getBytes());
                correct = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            correct = false;
        }

        return correct;
    }
}
