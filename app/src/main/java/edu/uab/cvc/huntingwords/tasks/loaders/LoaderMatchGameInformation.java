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
import java.util.LinkedHashMap;
import java.util.List;

import edu.uab.cvc.huntingwords.Utils;

/**
 * Created by carlosb on 16/04/18.
 */

public class LoaderMatchGameInformation {
    public void load(Context context, LinkedHashMap<String,Pair<List<String>,String>> matchGameImages, LinkedHashMap<String,Pair<List<String>,String>> matchGameFixImages) throws FileNotFoundException {
        matchGameImages.clear();
        matchGameFixImages.clear();

        File file = new File(context.getFilesDir(),"matchGameInfo.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                if (br.readLine() != null) {
                    String row;
                    while ((row = br.readLine()) != null) {
                        String[] columns = row.split(";");
                        String filename = columns[0];
                        String possibleResults = columns[1];
                        Boolean golden = Integer.valueOf(columns[2])==1;
                        String[] tempResults = possibleResults.split(":");
                        List<String> results = Arrays.asList(tempResults);
                        if (golden) {
                            String outputResult;
                            if (columns[3].equals("1")) {
                                outputResult = results.get(0);
                            } else {
                                outputResult = Utils.ANY_CORRECT;
                            }
                            matchGameFixImages.put(filename, Pair.create(results, outputResult));
                        } else {
                            matchGameImages.put(filename, Pair.create(results, null));
                        }
                    }
                }
                br.close();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

    }
}
