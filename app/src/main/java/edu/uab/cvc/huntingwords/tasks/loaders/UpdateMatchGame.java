package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.uab.cvc.huntingwords.tasks.CheckNumBatches;
import edu.uab.cvc.huntingwords.tasks.match.CheckMatchGameImages;
import edu.uab.cvc.huntingwords.tasks.match.CheckNewMatchGameFixImages;
import edu.uab.cvc.huntingwords.tasks.match.GetImagesMatchGame;
import edu.uab.cvc.huntingwords.tasks.match.GetMatchGameFixInfo;
import edu.uab.cvc.huntingwords.tasks.match.GetMatchGameInfo;
import timber.log.Timber;

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
        }

        fileOrDirectory.delete();
    }


    public boolean update(Context context) {
        File file = context.getFilesDir();
        System.out.println(file.getAbsolutePath());
        deleteRecursive(file);

        try {
            new GetMatchGameInfo(context).execute(matchGameInfoFilename).get();
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
