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

        f = new File(context.getFilesDir().getAbsolutePath() + File.separator + arg[0]);

        if (!f.exists() || arg[1].contains("1")) {
            if (arg[1].contains("1") && f.exists()) {
                f.delete();
            }

            try {
                link = Utils.BASE_URL_IMAGES + arg[0];

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

        return onPostExecute(correct, arg[0]);
    }

    private String onPostExecute(String a, String file) {
        switch (a) {
            case "-1":
                System.out.println("----------------------------------- INCORRECT -----------------------------------");
                return "-1";
            case "0":
                System.out.println("----------------------------------- U HAVE THE FILE -----------------------------------");
                System.out.println(file);
                return "0";
            case "1":
                System.out.println("----------------------------------- CORRECT -----------------------------------");
                return "1";
            default:
                return "-1";
        }
    }
}
