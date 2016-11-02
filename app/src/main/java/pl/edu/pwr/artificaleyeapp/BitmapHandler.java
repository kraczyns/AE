package pl.edu.pwr.artificaleyeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by nieop on 02.11.2016.
 */
public class BitmapHandler {

    public static Bitmap openFile(Context context, String filename) {
Bitmap image = null;
        try {
            int resourceId = context.getResources().getIdentifier(filename, "drawable", context.getPackageName());
            image = BitmapFactory.decodeResource(context.getResources(), resourceId);
        } catch (Exception e) {
            System.out.println("Cannot open the file.");
        }

        return image;
    }

    public static Bitmap scaleDownBitmap(Bitmap image, int newLength, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int newHeight = (int) (newLength * densityMultiplier);
        int newWidth = (int) (newHeight * image.getWidth()/((double) image.getHeight()));

        image=Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
        return image;
    }

    public static void saveNewFile(Bitmap image, String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
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

}
