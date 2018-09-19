package edu.uab.cvc.huntingwords.tasks.difference;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import edu.uab.cvc.huntingwords.Utils;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public class GetImagesDifferenceGame {

    private Context context;

    public GetImagesDifferenceGame(Context context) {
        this.context = context;
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
                    link = Utils.BASE_URL_IMAGES+"" + arg[0];  // base link: http://158.109.8.50/app_mobile/
                } else {
                    link = Utils.BASE_URL_IMAGES+"/" + arg[0];  // base link: http://158.109.8.50/app_mobile/
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
                return "-1";
            case "0":
                System.out.println("----------------------------------- U HAVE THE FILE -----------------------------------");
                return "0";
            case "1":
                System.out.println("----------------------------------- CORRECT -----------------------------------");
                return "1";
            default:
                return "-1";
        }
    }
}
