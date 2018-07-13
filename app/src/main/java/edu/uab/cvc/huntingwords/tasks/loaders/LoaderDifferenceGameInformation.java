package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by carlosb on 17/04/18.
 */

public class LoaderDifferenceGameInformation {
    private final String differenceGameInfoFilename = "differenceGameInfo.txt";
    private final String differenceGameFixInfoFilename = "differenceGameFixInfo.txt";


    public void load(Context context, Hashtable<String,List<Pair<String,Boolean>>> gameInfo) throws FileNotFoundException {
        gameInfo.clear();


        List<String> notSyncronizedClusters = new ArrayList<>();

        File file = new File(context.getFilesDir(),differenceGameInfoFilename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            if (br.readLine() != null) {
                String row;
                while ((row = br.readLine()) != null) {
                    String[] columns = row.split(";");
                    //TODO CHECK SIZE
                    String filename = columns[0];
                    String numCluster = columns[1];
                    Boolean validated = Integer.valueOf(columns[3])==1;

                    List<Pair<String,Boolean>> listFilenames;
                    if (!gameInfo.containsKey(numCluster)) {
                        listFilenames = new ArrayList<>();
                    } else {
                        listFilenames = gameInfo.get(numCluster);
                    }

                    if (validated) {
                        notSyncronizedClusters.add(numCluster);
                    }

                    listFilenames.add(Pair.create(filename,false));
                    gameInfo.put(numCluster, listFilenames);

                    /* delete not syncronized cluster */
                    for (String nameCluster: notSyncronizedClusters) {
                        gameInfo.remove(nameCluster);
                    }


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


    public void loadFix(Context context, Hashtable<String,List<Pair<String,Boolean>>> gameInfo) throws FileNotFoundException {
        gameInfo.clear();


        List<String> notSyncronizedClusters = new ArrayList<>();

        File file = new File(context.getFilesDir(),differenceGameFixInfoFilename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            if (br.readLine() != null) {
                String row;
                while ((row = br.readLine()) != null) {
                    String[] columns = row.split(";");
                    String filename = columns[0];
                    String numCluster = columns[1];
                    String result = columns[2];
                    Boolean validated = Integer.valueOf(columns[3])==1;
                    System.out.println(filename + " - " + numCluster + " - " + validated);

                    List<Pair<String,Boolean>> listFilenames;
                    if (!gameInfo.containsKey(numCluster)) {
                            listFilenames = new ArrayList<>();
                    } else {
                        listFilenames = gameInfo.get(numCluster);
                    }

                    if (!validated) {
                        notSyncronizedClusters.add(numCluster);
                    }

                    listFilenames.add(Pair.create(filename,result.equals("1")));
                    gameInfo.put(numCluster, listFilenames);

                    /* delete not syncronized cluster */
                    for (String nameCluster: notSyncronizedClusters) {
                        gameInfo.remove(nameCluster);
                    }


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
