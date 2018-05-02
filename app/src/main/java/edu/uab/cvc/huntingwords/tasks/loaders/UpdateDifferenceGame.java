package edu.uab.cvc.huntingwords.tasks.loaders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.uab.cvc.huntingwords.tasks.CheckNumGame_Images;
import edu.uab.cvc.huntingwords.tasks.difference.CheckDifferenceGameImages;
import edu.uab.cvc.huntingwords.tasks.difference.CheckNewDifferenceGameFixClusters;
import edu.uab.cvc.huntingwords.tasks.difference.GetDifferenceGameFixInfo;
import edu.uab.cvc.huntingwords.tasks.difference.GetDifferenceGameInfo;
import edu.uab.cvc.huntingwords.tasks.difference.GetImagesDifferenceGame;

/**
 * Created by carlosb on 16/04/18.
 */

public class UpdateDifferenceGame {
    private final Integer batchDiffImages;
    private String differenceGameInfoFilename = "differenceGameInfo.txt";
    private String differenceGameFixInfoFilename = "differenceGameFixInfo.txt";

    public UpdateDifferenceGame(Integer batchDiffImages) {
        this.batchDiffImages = batchDiffImages;
    }

    public void cleanFiles(Context context) {
        File file =  context.getFilesDir();
        File [] files = file.listFiles();
        for (File fil : files) {
                if (fil.getName().equals(differenceGameInfoFilename))  {
                    fil.delete();
                }
                if (fil.getName().equals(differenceGameFixInfoFilename))  {
                    fil.delete();
                }
        }

    }

    public boolean update(Context context) {
        cleanFiles(context);
        Boolean needDownloadFix = false;
        Boolean needDownload = false;

        String downImages = "-2";
        Boolean down = false;
        Boolean downDifferenceGameFix = false;

        Integer  numClusterDifferenceGameBBDD;
        try {
            numClusterDifferenceGameBBDD = new CheckNumGame_Images().execute("0").get();
            if (numClusterDifferenceGameBBDD == 0) {
                return false;
            }
        } catch (Exception e) {
            Log.e("Error", "Failed checking num of images.");
            return false;
        }


        File ffix = new File(context.getFilesDir(), differenceGameFixInfoFilename);
        int fFixNumImgs = 0;

        if (ffix.exists()) {
            String row;
            String[] columns;

            try {
                BufferedReader br = new BufferedReader(new FileReader(ffix));
                File auxImage;
                ArrayList<String> imageNames = new ArrayList<>();

                if (!br.readLine().contains("0")) {
                    while ((row = br.readLine()) != null) {/*
                                                    if (row.contains("0")) {
                                                        break;
                                                    }*/
                        columns = row.split(";");
                        // this function returns 0 if need erase the image and 1 if still need the image
                        int correct = new CheckDifferenceGameImages().execute(columns[0], "1").get();
                        if (correct != -1) {
                            if (correct == 1) {
                                imageNames.add(row);
                            } else {
                                if (correct != 2) {
                                    auxImage = new File(context.getFilesDir(), columns[0]);

                                    if (auxImage.exists()) {
                                        System.out.println("Deleting file: " + auxImage.getAbsolutePath());
                                        auxImage.delete();
                                    }
                                }
                            }
                        }
                    }

                    if (ffix.exists()) {
                        ffix.delete();
                        ffix.createNewFile();
                    }

                    BufferedWriter bw = new BufferedWriter(new FileWriter(ffix));

                    System.out.println("Check fix imageNames size: " + imageNames.size());
                    if (imageNames.size() > 0) {
                        bw.write(String.valueOf(imageNames.size()));
                        bw.write(System.getProperty("line.separator"));

                        for (int i = 0; i < imageNames.size(); i++) {
                            bw.write(imageNames.get(i));
                            bw.write(System.getProperty("line.separator"));
                        }

                        bw.close();
                    } else {
                        needDownloadFix = true;
                    }
                }
                br.close();


            } catch (Exception e) {
                Log.e("Error", "Failed checking old fix images.");
                cleanFiles(context);
            }

            if (ffix.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(ffix));
                    String next;

                    fFixNumImgs = Integer.valueOf(br.readLine());

                    System.out.println("num of fix images: " + fFixNumImgs);

                    if (fFixNumImgs > 0) {
                        String list = "'" + br.readLine().split(";")[0] + "'";
                        while ((next = br.readLine()) != null) {
                            list += ",'" + next.split(";")[0] + "'";
                        }

                        needDownloadFix = new CheckNewDifferenceGameFixClusters().execute(list).get();
                    } else {
                        needDownloadFix = true;
                    }

                } catch (Exception e) {
                    Log.e("Error", "Failed checking fix images of difference game.");
                    cleanFiles(context);
                }
            } else {
                needDownloadFix = true;
            }
        } else {
            needDownloadFix = true;
        }

        System.out.println("Check ffix need download:" + needDownload + ", " + needDownloadFix);

        File f = new File(context.getFilesDir(), differenceGameInfoFilename);
        if (f.exists()) {
            String row;
            String[] columns;

            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                File auxImage;
                ArrayList<String> imageNames = new ArrayList<>();

                if (br.readLine() != null) {
                    while ((row = br.readLine()) != null) {
                        columns = row.split(";");
                        // this function returns 0 if need erase the image and 1 if still need the image
                        int correct = new CheckDifferenceGameImages().execute(columns[0], "0").get();
                        if (correct != -1) {
                            if (correct == 1) {
                                imageNames.add(row);
                            } else {
                                if (correct != 2) {
                                    auxImage = new File(context.getFilesDir(), columns[0]);

                                    if (auxImage.exists()) {
                                        System.out.println("Deleting file: " + auxImage.getAbsolutePath());
                                        auxImage.delete();
                                    }
                                }
                            }
                        }
                    }
                }
                br.close();

                if (f.exists()) {
                    f.delete();
                    f.createNewFile();
                }

                BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                System.out.println("Check imageNames size: " + imageNames.size());
                if (imageNames.size() > 0) {
                    bw.write(String.valueOf(imageNames.size()));
                    bw.write(System.getProperty("line.separator"));

                    for (int i = 0; i < imageNames.size(); i++) {
                        bw.write(imageNames.get(i));
                        bw.write(System.getProperty("line.separator"));
                    }

                    bw.close();
                } else {
                    needDownload = true;
                }

            } catch (Exception e) {
                Log.e("Error", "Failed checking old images.");
            }

            if (!needDownload) {
                try {
                    System.out.println("Checking images...");
                    BufferedReader br = new BufferedReader(new FileReader(f));

                    int fNumImgs = Integer.valueOf(br.readLine());

                    if ((fFixNumImgs + fNumImgs) < Math.min(12, numClusterDifferenceGameBBDD)) {
                        needDownload = true;
                    } else {
                        while ((row = br.readLine()) != null) {
                            columns = row.split(";");
                            f = new File(context.getFilesDir(), columns[0]);
                            if (!f.exists()) {
                                needDownload = true;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error:", "Failed checking images.");
                }
            }
        } else {
            needDownload = true;
           // lastTextTimeForDown = System.currentTimeMillis();
        }

        System.out.println("Check f need download:" + needDownload + ", " + needDownloadFix);


        if (needDownloadFix) {
            System.out.println("Downloading difference game fix images");
            ffix = new File(context.getFilesDir(), differenceGameFixInfoFilename);
            String list = "''";
            try {
                if (ffix.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(ffix));

                    if (Integer.valueOf(br.readLine()) > 0) {
                        String next;
                        list = "'" + br.readLine().split(";")[0] + "'";
                        while ((next = br.readLine()) != null) {
                            list += ",'" + next.split(";")[0] + "'";
                        }
                    } else {
                        list = "''";
                    }
                }
            } catch (Exception e) {
                Log.e("Error:", "Failed reading the difference game fix txt.");
            }

            try {
                downDifferenceGameFix = new GetDifferenceGameFixInfo(context).execute(differenceGameFixInfoFilename, list).get();
            } catch (Exception e) {
                Log.e("Error:", "Failed downloading txt of fix difference game images.");
            }
        }

        if (needDownload) {
            System.out.println("Downloading difference game images");
            f = new File(context.getFilesDir(), differenceGameInfoFilename);
            String list = "''";
            int numImgs = 0;
            try {
                if (f.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    numImgs = Integer.valueOf(br.readLine());
                    if (numImgs > 0) {
                        String next;
                        list = "'" + br.readLine().split(";")[0] + "'";
                        while ((next = br.readLine()) != null) {
                            list += ",'" + next.split(";")[0] + "'";
                        }
                    } else {
                        list = "''";
                    }
                }
            } catch (Exception e) {
                Log.e("Error:", "Failed reading the difference game txt.");
            }

            try {
                int limit = 50;
                if (ffix.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(ffix));
                    String num;
                    if ((num = br.readLine()) != null) {
                        limit = 50 - (numImgs + Integer.valueOf(num));
                    } else {
                        limit = 50 - numImgs;
                    }
                }

                System.out.println("the limit is: " + limit);

                if (limit > 0) {
                    downImages = new GetDifferenceGameInfo(context).execute(differenceGameInfoFilename, list, String.valueOf(limit)).get();
                } else {
                    downImages = "3";
                }
            } catch (Exception e) {
                Log.e("Error:", "Failed downloading txt of difference game images.");
            }
        }

        if (downImages.contentEquals("3")) {
            down = true;
        }


        if (downImages.contentEquals("3") && down || downDifferenceGameFix) {
            if (downDifferenceGameFix) {
                File fDown2 = new File(context.getFilesDir(), differenceGameFixInfoFilename);

                try {
                    BufferedReader br = new BufferedReader(new FileReader(fDown2));
                    String row;
                    String[] columns;

                    String num = br.readLine();
                    int numWords = Integer.valueOf(num);

                    if (numWords > 0) {
                        int[] downloadedFiles = new int[numWords];

                        for (int i = 0; i < downloadedFiles.length; i++) {
                            downloadedFiles[i] = -2;
                        }
                        int i = 0;
                        GetImagesDifferenceGame getImagesDifferenceGame;

                        while ((row = br.readLine()) != null) {
                            columns = row.split(";");

                            getImagesDifferenceGame = new GetImagesDifferenceGame(context, downloadedFiles, i);
                            getImagesDifferenceGame.doInForeground(columns[0], "0");
                            i++;
                        }
                    }

                    br.close();
                } catch (Exception e) {
                    Log.e("Error","Failed downloading fix images of match game");
                }
                downDifferenceGameFix = false;
            }

            if (down) {
                File fDown = new File(context.getFilesDir(), differenceGameInfoFilename);
                try {
                    BufferedReader br = new BufferedReader(new FileReader(fDown));
                    String row;
                    String[] columns;

                    String num = br.readLine();
                    int numWords = Integer.valueOf(num);

                    if ((numWords > 0) && down) {
                        int[] downloadedFiles = new int[numWords];

                        for (int i = 0; i < downloadedFiles.length; i++) {
                            downloadedFiles[i] = -2;
                        }
                        int i = 0;
                        GetImagesDifferenceGame getImagesDifferenceGame;

                        while ((row = br.readLine()) != null) {
                            columns = row.split(";");

                            getImagesDifferenceGame = new GetImagesDifferenceGame(context, downloadedFiles, i);
                            getImagesDifferenceGame.doInForeground(columns[0], "0");
                            i++;
                        }
                    }

                    br.close();

                } catch (Exception e) {
                    Log.e("Error:", "Failed downloading image");
                }
                down = false;
            }
        }



        return true;

    }
}
