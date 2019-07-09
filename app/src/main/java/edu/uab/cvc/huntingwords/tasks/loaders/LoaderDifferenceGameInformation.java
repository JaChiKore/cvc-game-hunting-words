package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by carlosb on 17/04/18.
 */

public class LoaderDifferenceGameInformation {
    public void load(Context context, LinkedHashMap<String,List<Pair<String,Boolean>>> gameInfo, LinkedHashMap<String,List<Pair<String,Boolean>>> gameFixInfo) throws FileNotFoundException {
        gameInfo.clear();
        gameFixInfo.clear();

        File file = new File(context.getFilesDir(),"differenceGameInfo.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            if (br.readLine() != null) {
                String row;
                while ((row = br.readLine()) != null) {
                    String[] columns = row.split(";");
                    String filename = columns[0];
                    String numCluster = columns[1];
                    String result = columns[2];
                    boolean golden = Integer.valueOf(columns[3])==1;

                    if (golden) {
                        List<Pair<String,Boolean>> listFilenames;
                        if (!gameFixInfo.containsKey(numCluster)) {
                            listFilenames = new ArrayList<>();
                        } else {
                            listFilenames = gameFixInfo.get(numCluster);
                        }
                        listFilenames.add(Pair.create(filename,result.equals("1")));
                        gameFixInfo.put(numCluster, listFilenames);
                    } else {
                        List<Pair<String,Boolean>> listFilenames;
                        if (!gameInfo.containsKey(numCluster)) {
                            listFilenames = new ArrayList<>();
                        } else {
                            listFilenames = gameInfo.get(numCluster);
                        }
                        listFilenames.add(Pair.create(filename,false));
                        gameInfo.put(numCluster, listFilenames);
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
