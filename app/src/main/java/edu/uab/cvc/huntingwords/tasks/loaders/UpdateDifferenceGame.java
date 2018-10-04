package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import edu.uab.cvc.huntingwords.tasks.difference.GetDifferenceGameInfo;
import edu.uab.cvc.huntingwords.tasks.difference.GetImagesDifferenceGame;
import timber.log.Timber;

/**
 * Created by carlosb on 16/04/18.
 */

public class UpdateDifferenceGame {
    private String differenceGameInfoFilename = "differenceGameInfo.txt";

    public UpdateDifferenceGame() {}

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
        File file =  context.getFilesDir();
        deleteRecursive(file);

        try {
            new GetDifferenceGameInfo(context).execute(differenceGameInfoFilename, username).get();
        } catch (Exception e) {
            Timber.e("Failed downloading txt of difference game images.");
        }

        File fDown = new File(context.getFilesDir(), differenceGameInfoFilename);

        try {
            BufferedReader br = new BufferedReader(new FileReader(fDown));
            String row;
            String[] columns;

            String num = br.readLine();
            int numWords = Integer.valueOf(num);

            if (numWords > 0) {

                GetImagesDifferenceGame getImagesDifferenceGame;

                while ((row = br.readLine()) != null) {
                    columns = row.split(";");

                    getImagesDifferenceGame = new GetImagesDifferenceGame(context);
                    getImagesDifferenceGame.doInForeground(columns[0], "0");
                }
            }

            br.close();
        } catch (Exception e) {
            Timber.e("Failed downloading images of difference game");
        }

        return true;
    }
}
