package edu.uab.cvc.huntingwords.screens.games;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


@SuppressWarnings("WeakerAccess")
public class Bitmaps {

    private ArrayList<Validation> image_transcription = new ArrayList<>();
    private Bitmap fail;
    private Bitmap pass;

    public int getMaxWords() {
        return image_transcription.size();
    }

    class Validation {
        String imName;
        RectF rect;
        Bitmap image;
        String transcription;
        String[] otherTrans;
        boolean validated;
        boolean correct;
        int alpha;
        int identity;
        private boolean isActive;

        public Validation(String name, Bitmap i, String t, boolean v, boolean c, int id) {
            imName = name;
            rect = null;
            image = i;
            transcription = t;
            validated = v;
            correct = c;
            alpha = 255;
            identity = id;
            isActive = true;

            otherTrans = new String[3];
        }

        public void setAlpha(int a) {
            alpha = a;
        }

        public int getAlpha() {
            return alpha;
        }

        public boolean getStatus() {
            return isActive;
        }

        public void setInactive() {
            isActive = false;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Bitmaps(Context context, int imageWidth, int imageHeight, int num, String filename, String fixFilename, int level) {

        InputStream is;
        try {
            is = context.getAssets().open("sprites/images/fail.png");
            fail = BitmapFactory.decodeStream(is);

            fail = Bitmap.createScaledBitmap(fail,
                    imageHeight,
                    imageHeight,
                    false);

            is = context.getAssets().open("sprites/images/pass.png");
            pass = BitmapFactory.decodeStream(is);

            pass = Bitmap.createScaledBitmap(pass,
                    imageHeight,
                    imageHeight,
                    false);
        } catch (IOException e) {
            Log.e("Error:","Failed loading fail.");
        }

        chargeImagesMatchGame(context, imageWidth, imageHeight, num, filename, fixFilename, level);
    }

    private void chargeImagesMatchGame(Context context, int imageWidth, int imageHeight, int num, String filename, String fixFilename, int level) {

        String[] splitLine;
        BufferedReader reader = null;
        Bitmap image;
        int maxWords;
        Random r = new Random();

        boolean[] images;

        File f = new File(context.getFilesDir(), filename);
        File ffix = new File(context.getFilesDir(), fixFilename);

        try {
            int numFixImages;
            if (level >= 5) {
                numFixImages = 2;
            } else {
                numFixImages = 10 - ((level - 1) * 2);
            }

            reader = new BufferedReader(new FileReader(ffix));
            String mLine;

            mLine = reader.readLine();

            maxWords = Integer.valueOf(mLine);

            if (numFixImages > maxWords) {
                numFixImages = maxWords;
            }

            images = new boolean[maxWords];
            int i;

            for(i = 0; i < maxWords; i++) {
                images[i] = false;
            }

            int ran;
            i = 0;
            while(i < numFixImages) {
                ran = r.nextInt(maxWords);
                if(!images[ran]) {
                    images[ran] = true;
                    i++;
                }
            }

            String[] transcriptions;
            for (i = 0; i < maxWords; i++) {
                mLine = reader.readLine();
                splitLine = mLine.split(";");
                if(images[i]) {
                    ffix = new File(context.getFilesDir(), splitLine[0]);
                    image = BitmapFactory.decodeFile(ffix.getAbsolutePath());

                    image = Bitmap.createScaledBitmap(
                            image,
                            imageWidth,
                            imageHeight,
                            false);
                    Validation a;

                    System.out.println(mLine);
                    if (splitLine[3].contains("1")) {
                        a = new Validation(splitLine[0], image, "", true, true, i);
                    } else {
                        a = new Validation(splitLine[0], image, "", true, false, i);
                    }

                    transcriptions = splitLine[1].split(":");
                    a.transcription = transcriptions[0];
                    image_transcription.add(a);
                    if (!splitLine[1].isEmpty()) {
                        if (transcriptions.length == 4) {
                            System.arraycopy(transcriptions, 1, a.otherTrans, 0, 3);
                        } else {

                            InputStream is = context.getAssets().open("dics/" + splitLine[1].length() + " - " + Character.toLowerCase(splitLine[1].charAt(0)) + ".txt");
                            BufferedReader bw = new BufferedReader(new InputStreamReader(is));

                            String word;
                            ArrayList<String> words = new ArrayList<>();
                            int j = 0;
                            while ((word = bw.readLine()) != null) {
                                if (editionDistance(word, splitLine[1]) <= 3 && !splitLine[1].toLowerCase().contentEquals(word.toLowerCase())) {
                                    words.add(word);
                                    j++;
                                }
                            }

                            if (j >= 4) {

                                int z = 0;
                                boolean[] selecteds = new boolean[j];

                                while (z < 3) {
                                    ran = r.nextInt(j);
                                    if (!selecteds[ran]) {
                                        selecteds[ran] = true;
                                        z++;
                                    }
                                }
                                int index = 0;
                                for (z = 0; z < j; z++) {
                                    if (selecteds[z]) {
                                        word = words.get(z);
                                        if (Character.isUpperCase(splitLine[1].charAt(0))) {
                                            word = Character.toUpperCase(word.charAt(0)) + word.substring(1, word.length());
                                        }
                                        a.otherTrans[index] = word;
                                        index++;
                                        if (index >= 3) {
                                            break;
                                        }
                                    }
                                }

                            } else {
                                word = "";
                                for (int z = 0; z < splitLine[1].length(); z++) {
                                    word = word + "-";
                                }

                                for (int z = 0; z < 3; z++) {
                                    a.otherTrans[z] = word;
                                }
                            }
                        }
                    } else {
                        a.transcription = "None";

                        for (int z = 0; z < 3; z++) {
                            a.otherTrans[z] = "----";
                        }
                    }
                }
            }

            reader = new BufferedReader(new FileReader(f));

            mLine = reader.readLine();

            maxWords = Integer.valueOf(mLine);

            int remaining = num - numFixImages;

            if (remaining > maxWords) {
                remaining = maxWords;
            }

            images = new boolean[maxWords];

            for(i = 0; i < maxWords; i++) {
                images[i] = false;
            }

            i = 0;
            while(i < remaining) {
                ran = r.nextInt(maxWords);
                if(!images[ran]) {
                    images[ran] = true;
                    i++;
                }
            }

            for (i = 0; i < maxWords; i++) {
                mLine = reader.readLine();
                splitLine = mLine.split(";");
                if(images[i]) {
                    f = new File(context.getFilesDir(), splitLine[0]);

                    image = BitmapFactory.decodeFile(f.getAbsolutePath());

                    System.out.println(f.getAbsolutePath());
                    image = Bitmap.createScaledBitmap(
                            image,
                            imageWidth,
                            imageHeight,
                            false);

                    Validation a = new Validation(splitLine[0], image, "", false, false, i);

                    transcriptions = splitLine[1].split(":");
                    a.transcription = transcriptions[0];
                    image_transcription.add(a);
                    if (!splitLine[1].isEmpty()) {
                        if (transcriptions.length == 4) {
                            System.arraycopy(transcriptions, 1, a.otherTrans, 0, 3);
                        } else {
                            InputStream is = context.getAssets().open("dics/" + splitLine[1].length() + " - " + Character.toLowerCase(splitLine[1].charAt(0)) + ".txt");
                            BufferedReader bw = new BufferedReader(new InputStreamReader(is));

                            String word;
                            ArrayList<String> words = new ArrayList<>();
                            int j = 0;
                            while ((word = bw.readLine()) != null) {
                                if (editionDistance(word, splitLine[1]) <= 3 && !splitLine[1].toLowerCase().contentEquals(word.toLowerCase())) {
                                    words.add(word);
                                    j++;
                                }
                            }

                            if (j >= 4) {

                                int z = 0;
                                boolean[] selecteds = new boolean[j];

                                while (z < 3) {
                                    ran = r.nextInt(j);
                                    if (!selecteds[ran]) {
                                        selecteds[ran] = true;
                                        z++;
                                    }
                                }
                                int index = 0;
                                for (z = 0; z < j; z++) {
                                    if (selecteds[z]) {
                                        word = words.get(z);
                                        if (Character.isUpperCase(splitLine[1].charAt(0))) {
                                            word = Character.toUpperCase(word.charAt(0)) + word.substring(1, word.length());
                                        }
                                        a.otherTrans[index] = word;
                                        index++;
                                        if (index >= 3) {
                                            break;
                                        }
                                    }
                                }

                            } else {
                                word = "";
                                for (int z = 0; z < splitLine[1].length(); z++) {
                                    word = word + "-";
                                }

                                for (int z = 0; z < 3; z++) {
                                    a.otherTrans[z] = word;
                                }
                            }
                        }
                    } else {
                        a.transcription = "None";

                        for (int z = 0; z < 3; z++) {
                            a.otherTrans[z] = "----";
                        }
                    }
                }
            }
        } catch(IOException e) {
            Log.e("Error:","Failed reading transcriptions");
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("Error","Failed closing file");
                }
            }
        }
    }

    public ArrayList<Validation> getImage_transcription() {
        return image_transcription;
    }

    public Bitmap getFail() {
        return fail;
    }

    public Bitmap getPass() {
        return pass;
    }

    public int editionDistance(String a, String b) {
        int distance = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                distance++;
            }
        }

        return distance;
    }
}
