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

import edu.uab.cvc.huntingwords.tasks.CheckNumGame_Images;
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

    private final Integer batchMatchImages;
    private String matchGameInfoFilename = "matchGameInfo.txt";
    private String matchGameFixInfoFilename = "matchGameFixInfo.txt";

    public UpdateMatchGame(Integer batchMatchImages) {
        this.batchMatchImages = batchMatchImages;
    }

    public void cleanFiles(Context context) {
        File file =  context.getFilesDir();
        File [] files = file.listFiles();
        for (File fil : files) {
            if (fil.getName().equals(matchGameInfoFilename))  {
                fil.delete();
            }
            if (fil.getName().equals(matchGameFixInfoFilename))  {
                fil.delete();
            }
        }
    }


    public boolean update(Context context) {

        cleanFiles(context);

        /* check  */
        Integer numImagesMatchGameInBBDD = null;
        try {
            /* check it is the DB online with images!! */
            numImagesMatchGameInBBDD = new CheckNumGame_Images().execute("1").get();
        } catch (InterruptedException e ) {
            Timber.e(e);
            return false;
        } catch (ExecutionException e) {
            Timber.e(e);

            return false;
        }
        if (numImagesMatchGameInBBDD == 0) {
            return false;
        }

        Boolean needDownloadFix = false;
        Boolean needDownload = false;

        Boolean downMatchGameFix = false;
        Boolean down = false;
        String downImages = "-2";



        File ffix = new File(context.getFilesDir(), matchGameFixInfoFilename);
        int fFixNumImgs = 0;

        if (ffix.exists()) {
            String row;
            String[] columns;

            /* if it is exists in th DB the image*/
            try {
                BufferedReader br = new BufferedReader(new FileReader(ffix));
                File auxImage;
                ArrayList<String> imageNames = new ArrayList<>();

                if (br.readLine() != null) {
                    while ((row = br.readLine()) != null) {
                        columns = row.split(";");
                        // this function returns 0 if need erase the image and 1 if still need the image
                        /* check for golden images*/
                        int correct = new CheckMatchGameImages().execute(columns[0], "1").get();
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

                /* clean info and rebuild ffix info*/
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

            } catch (Exception e) {
                Log.e("Error", "Failed checking old images for match game.");
                cleanFiles(context);
            }

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

                    needDownloadFix = new CheckNewMatchGameFixImages().execute(list).get();
                } else {
                    needDownloadFix = true;
                }

            } catch (Exception e) {
                Log.e("Error", "Failed checking fix images of match game.");
                cleanFiles(context);

            }
        } else {
            needDownloadFix = true;
        }

        System.out.println("Check ffix need download:" + needDownload + ", " + needDownloadFix);



        File f = new File(context.getFilesDir(), matchGameInfoFilename);
        if (f.exists()) {
            String row;
            String[] columns;

            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                File auxImage;
                ArrayList<String> imageNames = new ArrayList<>();

                /* verify if it exist yet in db*/
                if (br.readLine() != null) {
                    while ((row = br.readLine()) != null) {
                        columns = row.split(";");
                        // this function returns 0 if need erase the image and 1 if still need the image
                        int correct = new CheckMatchGameImages().execute(columns[0], "0").get();
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

                /* rebuild file with image */
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





            /*  start if it is necessary download not verify images */
            if (!needDownload) {
                try {
                    System.out.println("Checking images...");
                    BufferedReader br = new BufferedReader(new FileReader(f));

                    int fNumImgs = Integer.valueOf(br.readLine());

                    if ((fFixNumImgs + fNumImgs) < Math.min(12, numImagesMatchGameInBBDD)) {
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
            //TODO CHECK DOWNLOAD
            //lastTextTimeForDown = System.currentTimeMillis();
        }


        if (needDownloadFix) {
        /* get match info */
            System.out.println("Downloading match game fix images");
            File ffix2 = new File(context.getFilesDir(), matchGameFixInfoFilename);
            try {
                String list;
                if (ffix2.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(ffix2));

                    if (Integer.valueOf(br.readLine()) > 0) {
                        String next;
                        list = "'" + br.readLine().split(";")[0] + "'";
                        while ((next = br.readLine()) != null) {
                            list += ",'" + next.split(";")[0] + "'";
                        }
                    } else {
                        list = "''";
                    }
                } else {
                    list = "''";
                }

                 downMatchGameFix = new GetMatchGameFixInfo(context).execute(matchGameFixInfoFilename, list).get();
            } catch (Exception e) {
                Log.e("Error:", "Failed downloading txt of fix match game images.");
            }
        }



        if (needDownload) {
            System.out.println("Downloading match game images");
            File f2 = new File(context.getFilesDir(), matchGameInfoFilename);
            System.out.println(f.getAbsolutePath());
            try {
                String list;
                int numFImgs = 0;
                if (f2.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(f2));
                    String readLine = br.readLine();
                    if (readLine == null) {
                        numFImgs = 0;
                    } else {
                        numFImgs = Integer.valueOf(readLine);
                    }
                    if (numFImgs > 0) {
                        String next;
                        list = "'" + br.readLine().split(";")[0] + "'";
                        while ((next = br.readLine()) != null) {
                            list += ",'" + next.split(";")[0] + "'";
                        }
                    } else {
                        System.out.println("list void");
                        list = "''";
                    }
                } else {
                    list = "''";
                }
                downImages = new GetMatchGameInfo(context).execute(matchGameInfoFilename, list).get();
            } catch (Exception e) {
                Log.e("Error:", "Failed downloading txt of match game images.");
            }
        }


        if (downImages.contentEquals("1")) {
            down = true;
        }

        if (downImages.contentEquals("1") && down || downMatchGameFix) {
            if (downMatchGameFix) {
                File fDown2 = new File(context.getFilesDir(), matchGameFixInfoFilename);

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
                        GetImagesMatchGame getImagesMatchGame;

                        while ((row = br.readLine()) != null) {
                            columns = row.split(";");

                            getImagesMatchGame = new GetImagesMatchGame(context, downloadedFiles, i);
                            getImagesMatchGame.doInForeground(columns[0], "0");
                            i++;
                        }
                    }

                    br.close();
                } catch (Exception e) {
                    Log.e("Error","Failed downloading fix images of match game");
                }
                downMatchGameFix = false;
            }

            if (down) {
                File fDown = new File(context.getFilesDir(), matchGameInfoFilename);
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
                        GetImagesMatchGame getImagesMatchGame;
                        while ((row = br.readLine()) != null) {
                            columns = row.split(";");

                            getImagesMatchGame = new GetImagesMatchGame(context, downloadedFiles, i);
                            getImagesMatchGame.doInForeground(columns[0], "0");
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
