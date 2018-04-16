package edu.uab.cvc.huntingwords.tasks;

import android.content.Context;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by carlosb on 16/04/18.
 */

public class LoadGameInformation {
    private final String differenceGameInfoFilename = "differenceGameInfo.txt";
    private final String differenceGameFixInfoFilename = "differenceGameFixInfo.txt";

    private final String matchGameInfoFilename = "matchGameInfo.txt";
    private final String matchGameFixInfoFilename = "matchGameFixInfo.txt";

    private String folderDifferenceGame = "differenceGame";
    private String folderDifferenceGameFix = "differenceGameFix";


    private Hashtable<String,List<Pair<String,String>>> goldenDifferenceGameImages;
    private Hashtable<String,List<Pair<String,String>>> differenceGameImages;

    private Hashtable<String,String> goldenMatchGameImages;
    private Hashtable<String,Pair<List<String>,String>> matchGameImages;

    public void load(Context context) {
        goldenDifferenceGameImages = new Hashtable<>();
        differenceGameImages = new Hashtable<>();
        goldenDifferenceGameImages = new Hashtable<>();
        matchGameImages = new Hashtable<>();

        File file = new File(context.getFilesDir(),matchGameInfoFilename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String strnum;
            int aux = 0;
            if ((strnum = br.readLine()) != null) {
                aux = Integer.valueOf(strnum);
            }
            if (br.readLine() != null) {
                String row;
                while ((row = br.readLine()) != null) {
                    String [] columns = row.split(";");
                    //TODO CHECK SIZE
                    String filename = columns[0];
                    //String values = Arrays.asList(columns[1].split(":"));
                }
            }
            br.close();

        } catch (FileNotFoundException e) {
            //TODO check value
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




}
