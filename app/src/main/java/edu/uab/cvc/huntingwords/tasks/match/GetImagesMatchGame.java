package edu.uab.cvc.huntingwords.tasks.match;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class GetImagesMatchGame {

    private Context context;
    private int[] downloadedFiles;
    private int index;

    public GetImagesMatchGame(Context context, int[] downloadedFiles, int index) {
        this.context = context;
        this.downloadedFiles = downloadedFiles;
        this.index = index;
    }

    public String doInForeground(String... arg) {
        String link;
        String correct = "1";
        File f;

        if (arg[0].substring(0, 1).contains("/")) {
            f = new File(context.getFilesDir().getAbsolutePath() + arg[0]);
        } else {
            f = new File(context.getFilesDir().getAbsolutePath() + File.separator + arg[0]);
        }

        if (!f.exists() || arg[1].contains("1")) {
            if (arg[1].contains("1") && f.exists()) {
                f.delete();
            }

            try { // base link: http://158.109.8.50/ + imagename
                if (arg[0].substring(0, 1).contains("/")) {
                    link = "http://158.109.8.50/app_mobile" + arg[0];  // base link: http://158.109.8.50/app_mobile/
                } else {
                    link = "http://158.109.8.50/app_mobile/" + arg[0];  // base link: http://158.109.8.50/app_mobile/
                }

                URL url = new URL(link);
                InputStream is;
                FileOutputStream fos;

                is = url.openConnection().getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                f = new File(context.getFilesDir(), arg[0]);
                fos = new FileOutputStream(f);

                int curr;
                while ((curr = bis.read()) != -1) {
                    fos.write(curr);
                }

                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
                correct = "-1";
            }
        } else {
            correct = "0";
        }

        return onPostExecute(correct);
    }

    private String onPostExecute(String a) {
        switch (a) {
            case "-1":
                System.out.println("----------------------------------- INCORRECT -----------------------------------");
                downloadedFiles[index] = -1;
                return "-1";
            case "0":
                System.out.println("----------------------------------- U HAVE THE FILE -----------------------------------");
                downloadedFiles[index] = 0;
                return "0";
            case "1":
                System.out.println("----------------------------------- CORRECT -----------------------------------");
                downloadedFiles[index] = 1;
                return "1";
            default:
                return "-1";
        }
    }
}
