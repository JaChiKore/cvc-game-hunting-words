package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.uab.cvc.huntingwords.tasks.match.GetImagesMatchGame;
import edu.uab.cvc.huntingwords.tasks.match.GetMatchGameInfo;

/**
 * Created by carlosb on 16/04/18.
 */

public class UpdateMatchGame {
    private String matchGameInfoFilename = "matchGameInfo.txt";

    public UpdateMatchGame() {}

    private void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        } else {
            fileOrDirectory.delete();
        }
    }

    public boolean update(Context context, String username) {
        File file = context.getFilesDir();
        deleteRecursive(file);

        try {
            System.out.println(username);
            String a = new GetMatchGameInfo(context).execute(matchGameInfoFilename, username).get();
            System.out.println(a);
        } catch (Exception e) {
            Log.e("Error:", "Failed downloading txt of match game images.");
        }

        File fDown = new File(context.getFilesDir(), matchGameInfoFilename);

        try {
            BufferedReader br = new BufferedReader(new FileReader(fDown));
            String row;
            String[] columns;

            String num = br.readLine();
            int numWords = Integer.valueOf(num);

            if (numWords > 0) {
                GetImagesMatchGame getImagesMatchGame;

                while ((row = br.readLine()) != null) {
                    columns = row.split(";");

                    getImagesMatchGame = new GetImagesMatchGame(context);
                    getImagesMatchGame.doInForeground(columns[0], "0");
                }
            }

            br.close();
        } catch (Exception e) {
            Log.e("Error","Failed downloading images of match game");
            return false;
        }

        return true;
    }

}
