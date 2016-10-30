package pl.edu.pwr.artificaleyeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by nieop on 30.10.2016.
 */
public class Binarize {

    private static Bitmap image;
    private static int width;
    private static int height;

    public static Bitmap toBitmap(Context context, String filename) throws FileNotFoundException, UnsupportedEncodingException {
        if (filename != "xxx") {
            openFile(context, filename);
        }
        //;
      return createContrast(convertImageToBlackAndWhite(), 0.5);
     /*   if (isMoreBlack())
            image = revertColors();
        String[] name = filename.split("\\.");
        if (detectBorder()) {
            return cutBorders();
        } else {
           return image;
        }
        */

    }

    public static Bitmap getImage() {
        return image;
    }

    public static void setImage(Bitmap bitmap) {
        image = bitmap;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    public static void openFile(Context context, String filename) {
        try {
            int resourceId = context.getResources().getIdentifier(filename, "drawable", context.getPackageName());
            image = BitmapFactory.decodeResource(context.getResources(), resourceId);
            width = image.getWidth();
            height = image.getHeight();

        } catch (Exception e) {
            System.out.println("Cannot open the file.");
        }
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public static void saveNewFile(Bitmap imageToSave, String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap convertImageToBlackAndWhite() {
        Bitmap bwImage = Bitmap.createBitmap(
                width, height, image.getConfig());

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int pixelColor = image.getPixel(i, j);
                int pixelAlpha = Color.alpha(pixelColor);
                int pixelRed = Color.red(pixelColor);
                int pixelGreen = Color.green(pixelColor);
                int pixelBlue = Color.blue(pixelColor);

                int pixelBW = (pixelRed + pixelGreen + pixelBlue)/3;
                int newPixel = Color.argb(
                        pixelAlpha, pixelBW, pixelBW, pixelBW);

                bwImage.setPixel(i, j, newPixel);
            }
        }

        return bwImage;
    }

    private static void convertImageToGrayScale() {
        Bitmap newBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(newBitmap);
        c.drawBitmap(image, 0, 0, new Paint());

        for(int i = 0; i < image.getWidth(); ++i) {
            for(int j = 0; j < image.getHeight(); ++j) {
                int pixel = image.getPixel(i, j);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);
                red = green = blue = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
                int newColor = Color.argb(alpha, red, green, blue);
                newBitmap.setPixel(i, j, 1);
            }
        }
        image = newBitmap;
    }

    private static Bitmap binarizeImage() {

        int color;
        int newPixel;

        int threshold = threshold();

        Bitmap tmp = Bitmap.createBitmap(image);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // Get pixels
                color = Color.red(image.getPixel(i, j));
                int alpha = Color.alpha(image.getPixel(i, j));
                if (color > threshold) {
                    newPixel = 255;
                } else {
                    newPixel = 0;
                }
                newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
                tmp.setPixel(i, j, newPixel);

            }
        }
        return tmp;
    }

    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    private static int threshold() {

        int[] bitmap = doBitmap();
        int total = width * height;

        float sum = 0;
        for (int i = 0; i < 256; i++) sum += i * bitmap[i];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += bitmap[i];
            if (wB == 0) continue;
            wF = total - wB;

            if (wF == 0) break;

            sumB += (float) (i * bitmap[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }

    public static Bitmap createContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return bmOut;
    }

    private static int[] doBitmap() {

        int[] bitmap = new int[256];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = Color.red(image.getPixel(i, j));
                bitmap[color]++;
            }
        }

        return bitmap;
    }

    private static boolean isMoreBlack() {
        int blackCounter = 0;
        int whiteCounter = 0;

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int value = image.getPixel(j, i);
                if (value == Color.BLACK) {
                    blackCounter++;
                } else {
                    whiteCounter++;
                }
            }
        }

        if (blackCounter > whiteCounter)
            return true;
        else return false;
    }

    public static Bitmap revertColors() {
        Bitmap reverted = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int value = image.getPixel(j, i);
                if (value == Color.BLACK) {
                    reverted.setPixel(j, i, Color.WHITE);
                } else {
                    reverted.setPixel(j, i, Color.BLACK);
                }
            }
        }
        return reverted;
    }

    private static Bitmap cutBorders() {
        int borderWidth = (int) (0.05*width);
        int borderHeight = (int) (0.05*height);
        int newWidth = width - 2*borderWidth;
        int newHeight = height - 2*borderHeight;

        Bitmap imageWithoutBorders = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        int i_b = 0;

        for (int i = borderHeight; i < newHeight+borderHeight; i++) {
            int j_b = 0;
            for (int j = borderWidth; j < newWidth+borderWidth; j++) {
                imageWithoutBorders.setPixel(j_b++, i_b, image.getPixel(j, i));
            }
            i_b++;
        }

        return imageWithoutBorders;
    }

    private static boolean detectBorder() {

        for (int i = 1; i < height*0.1; i++) {
            for (int j = 1; j < width; j++) {
                if (image.getPixel(j, i) != image.getPixel(j-1,i-1))
                    return true;
            }
        }
        return false;
    }

}
