package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import edu.uab.cvc.huntingwords.Utils;

/**
 * Created by carlosb on 16/04/18.
 */

public class LoaderMatchGameInformation {

    private final String matchGameInfoFilename = "matchGameInfo.txt";
    private final String matchGameFixInfoFilename = "matchGameFixInfo.txt";


    public void load(Context context, Hashtable<String,Pair<List<String>,String>> matchGameImages) throws FileNotFoundException {
        matchGameImages.clear();

        File file = new File(context.getFilesDir(),matchGameInfoFilename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                if (br.readLine() != null) {
                    String row;
                    while ((row = br.readLine()) != null) {
                        String[] columns = row.split(";");
                        String filename = columns[0];
                        String possibleResults = columns[1];
                        String[] tempResults = possibleResults.split(":");
                        List<String> results = Arrays.asList(tempResults);
                        matchGameImages.put(filename, Pair.create(results, new String()));
                    }
                }
                br.close();
            } catch (IOException e){

            } finally {
                try {
                    br.close();
                } catch(Exception e) {
                }
            }

    }

    public void loadFix(Context context, Hashtable<String,Pair<List<String>,String>> matchGameImages) throws FileNotFoundException {
        matchGameImages.clear();

        File file = new File(context.getFilesDir(),matchGameFixInfoFilename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            if (br.readLine() != null) {
                String row;
                while ((row = br.readLine()) != null) {
                    String[] columns = row.split(";");
                    String filename = columns[0];
                    String possibleResults = columns[1];
                    String[] tempResults = possibleResults.split(":");
                    List<String> results = Arrays.asList(tempResults);
                    String outputResult = new String();
                    if (columns[3].equals("1")) {
                        outputResult = results.get(0);
                    } else {
                        outputResult = Utils.ANY_CORRECT;
                    }
                    matchGameImages.put(filename, Pair.create(results, outputResult));
                }
            }
            br.close();
        } catch (IOException e){

        } finally {
            try {
                br.close();
            } catch(Exception e) {
            }
        }

    }




}
