package pl.edu.pwr.artificaleyeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Environment;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.connectedComponentsWithStats;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.line;
import static org.opencv.imgproc.Imgproc.minAreaRect;
import static org.opencv.imgproc.Imgproc.rectangle;

/**
 * Created by nieop on 30.10.2016.
 */
public class BitmapConverter {

    private static Bitmap image;
    private static int width;
    private static int height;

    public static Bitmap getImage() {
        return image;
    }

    public static void setImage(Bitmap bitmap) {
        image = bitmap;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    protected static void convertImageToBlackAndWhite() {
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

        image = bwImage;
    }

    private static void convertImageToGrayScale() {
        Bitmap newBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(),image.getConfig());
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

    protected static void binarizeImage() {

        int color;
        int newPixel;

        int threshold = 100;
        //threshold();

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
        image = tmp;
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

    protected static boolean isMoreBlack() {
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

    protected static void revertColors() {
        Bitmap reverted = Bitmap.createBitmap(width, height, image.getConfig());

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
        image = reverted;
    }

    protected static void cutBorders() {
        int borderWidth = (int) (0.05*width);
        int borderHeight = (int) (0.05*height);
        int newWidth = width - 2*borderWidth;
        int newHeight = height - 2*borderHeight;

        Bitmap imageWithoutBorders = Bitmap.createBitmap(newWidth, newHeight, image.getConfig());

        int i_b = 0;

        for (int i = borderHeight; i < newHeight+borderHeight; i++) {
            int j_b = 0;
            for (int j = borderWidth; j < newWidth+borderWidth; j++) {
                imageWithoutBorders.setPixel(j_b++, i_b, image.getPixel(j, i));
            }
            i_b++;
        }

        image = imageWithoutBorders;
    }

    protected static boolean detectBorder() {

        for (int i = 1; i < height*0.1; i++) {
            for (int j = 1; j < width; j++) {
                if (image.getPixel(j, i) != image.getPixel(j-1,i-1))
                    return true;
            }
        }
        return false;
    }

    protected static void findRectangle() {
//Bitmap to Mat
        Mat mat = new Mat();
        Utils.bitmapToMat(image, mat);
        //Mat to gray
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(gray, gray, 50, 200);
        double max = 0;
        //Find contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours( gray, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        int x = 1;
        Bitmap bmp = null;
        Mat copy = new Mat();
        //For each contour found
        for (int i=0; i<contours.size(); i++)
        {
            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);

if (rect.area() > max) {
    Rect roi = new Rect(rect.x, rect.y, rect.width, rect.height);
    bmp = Bitmap.createBitmap(rect.width, rect.height, Bitmap.Config.ARGB_8888);
    copy = new Mat(mat, roi);
max = rect.area();
            }
        }
        Utils.matToBitmap(copy, bmp);
        image = bmp;
    }
}
