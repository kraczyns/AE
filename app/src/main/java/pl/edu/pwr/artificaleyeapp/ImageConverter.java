package pl.edu.pwr.artificaleyeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by nieop on 04.06.2016.
 */
public class ImageConverter {
//Gaussian Blur
    int radius;
    Bitmap image;
    protected final Context context;
//Canny Edge Detector
private final static float GAUSSIAN_CUT_OFF = 0.005f;
    private final static float MAGNITUDE_SCALE = 10F;
    private final static float MAGNITUDE_LIMIT = 100F;
    private final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);

    private int height;
    private int width;
    private int picSize;
    private int[] data;
    private int[] magnitude;
    private Bitmap sourceImg;
    private Bitmap edgesImg;

    private float gaussianKernelRadius;
    private float lowThreshold;
    private float highThreshold;
    private int gaussianKernelWidth;
    private boolean contrastNormalised;

    private int mFollowStackDepth = 100;

    //Image size
    private int newHeight;
    private int newWidth;

    ImageConverter(Context context) {
        this.context = context;
    }

    public void setImageConverter(Bitmap img) {
        this.image = img;
        this.radius = 10;
        this.lowThreshold = 2.5f;
        this.highThreshold = 7.5f;
        this.gaussianKernelRadius = 3f;
        this.gaussianKernelWidth = 18;
        this.contrastNormalised = false;
    }

    public void setThreshold(int _lowThreshold, int _highThreshold) {
        this.lowThreshold = _lowThreshold;
        this.highThreshold = _highThreshold;
    }

    public void setNewSize(int _height, int _width) {
        newHeight = _height;
        newWidth = _width;
    }

    public Bitmap resize() {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                image, 0, 0, width, height, matrix, false);
        //image.recycle();
        return resizedBitmap;
    }

    public Bitmap blur() {
        if (null == image)
            return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public Bitmap getSourceImg () {
        return sourceImg;
    }

    public Bitmap getEdgesImg () {
        return edgesImg;
    }

    public void setSourceImg (Bitmap sourceImg_) {
        sourceImg = sourceImg_.copy(Bitmap.Config.ARGB_8888, true);
    }

    public void setEdgesImg (Bitmap edgesImg_) {
        edgesImg = edgesImg_;
    }

    public void setContrastNormalised(boolean contrastNormalised) {
        this.contrastNormalised = contrastNormalised;
    }

    public boolean isContrastNormalized() {
        return contrastNormalised;
    }

    public void process () {
        long start = System.nanoTime();
        height = sourceImg.getHeight();
        width = sourceImg.getWidth();
        picSize = height*width;
        initArrs();
        readLuminance();
        if (contrastNormalised) {
            normalizeContrast();
        }
      /*  computeGradients(gaussianKernelRadius, gaussianKernelWidth);
        int low = Math.round (lowThreshold * MAGNITUDE_SCALE);
        int high = Math.round (highThreshold * MAGNITUDE_SCALE);
        performHysteresis(low, high);
        thresholdEdges();*/
        writeEdges(data);
        Log.i("Processing", "Processing terminated, time required: " + (System.nanoTime() - start));
    }

    private void initArrs() {
        if (data == null || picSize != data.length) {
            data = new int[picSize];
            magnitude = new int[picSize];
        }
    }

    private void readLuminance() {
        int [] pixels = new int[picSize];
        sourceImg.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0 ; i < picSize ; i++) {
            int p = pixels[i];
            int r = (p & 0xff0000) >> 16;
            int g = (p & 0xff00) >> 8;
            int b = p & 0xff;
            data[i] = luminance(r, g, b);
        }
    }

    private int luminance (int R, int G, int B) {
        return Math.round (0.299f * R + 0.587f * G + 0.114f * B);
    }

    private void normalizeContrast() {
        int[] histogram = new int[256];
        for (int i = 0 ; i < data.length ; i++) {
            histogram[data[i]]++;
        }

        int[] remap = new int[256];
        int sum = 0;
        int j = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            int target = sum*255/picSize;
            for (int k = j+1; k <=target; k++) {
                remap[k] = i;
            }
            j = target;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = remap[data[i]];
        }
    }

    private void computeGradients(float kernelRadius, int kernelWidth) {
        float[] xConv = new float[picSize];
        float[] yConv = new float[picSize];

        float kernel[] = new float[kernelWidth];
        float diffKernel[] = new float[kernelWidth];
        int kwidth;
        for (kwidth = 0 ; kwidth < kernelWidth ; kwidth++) {
            float g1 = gaussian(kwidth, kernelRadius);
            if (g1 <= GAUSSIAN_CUT_OFF && kwidth >= 2) break;
            float g2 = gaussian(kwidth - 0.5f, kernelRadius);
            float g3 = gaussian(kwidth + 0.5f, kernelRadius);
            kernel[kwidth] = (g1 + g2 + g3) / 3f / (2f * (float) Math.PI * kernelRadius * kernelRadius);
            diffKernel[kwidth] = g3 - g2;
        }

        int initX = kwidth - 1;
        int maxX = width - (kwidth - 1);
        int initY = width * (kwidth - 1);
        int maxY = width * (height - (kwidth - 1));

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                float sumX = data[index] * kernel[0];
                float sumY = sumX;
                int xOffset = 1;
                int yOffset = width;
                for( ; xOffset < kwidth ; ) {
                    sumY += kernel[xOffset] * (data[index - yOffset] + data[index + yOffset]);
                    sumX += kernel[xOffset] * (data[index - xOffset] + data[index + xOffset]);
                    yOffset += width;
                    xOffset++;
                }
                yConv[index] = sumY;
                xConv[index] = sumX;
            }
        }

        float[] xGradient = new float[picSize];
        float[] yGradient = new float[picSize];

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                float sum = 0f;
                int index = x + y;
                for (int i = 1; i < kwidth; i++)
                    sum += diffKernel[i] * (yConv[index - i] - yConv[index + i]);

                xGradient[index] = sum;
            }
        }

        for (int x = kwidth; x < width - kwidth; x++) {
            for (int y = initY; y < maxY; y += width) {
                float sum = 0.0f;
                int index = x + y;
                int yOffset = width;
                for (int i = 1; i < kwidth; i++) {
                    sum += diffKernel[i] * (xConv[index - yOffset] - xConv[index + yOffset]);
                    yOffset += width;
                }
                yGradient[index] = sum;
            }
        }

        initX = kwidth;
        maxX = width - kwidth;
        initY = width * kwidth;
        maxY = width * (height - kwidth);
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                int indexN = index - width;
                int indexS = index + width;
                int indexW = index - 1;
                int indexE = index + 1;
                int indexNW = indexN - 1;
                int indexNE = indexN + 1;
                int indexSW = indexS - 1;
                int indexSE = indexS + 1;


                float xGrad = xGradient[index];
                float yGrad = yGradient[index];
                float gradMag = hypot(xGrad, yGrad);

                float nMag = hypot(xGradient[indexN], yGradient[indexN]);
                float sMag = hypot(xGradient[indexS], yGradient[indexS]);
                float wMag = hypot(xGradient[indexW], yGradient[indexW]);
                float eMag = hypot(xGradient[indexE], yGradient[indexE]);
                float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
                float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
                float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
                float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
                float tmp;

                if (xGrad * yGrad <= (float) 0
                        ? Math.abs(xGrad) >= Math.abs(yGrad)
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * neMag - (xGrad + yGrad) * eMag)
                        && tmp > Math.abs(yGrad * swMag - (xGrad + yGrad) * wMag)
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * neMag - (yGrad + xGrad) * nMag)
                        && tmp > Math.abs(xGrad * swMag - (yGrad + xGrad) * sMag)
                        : Math.abs(xGrad) >= Math.abs(yGrad)
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad * seMag + (xGrad - yGrad) * eMag)
                        && tmp > Math.abs(yGrad * nwMag + (xGrad - yGrad) * wMag)
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad * seMag + (yGrad - xGrad) * sMag)
                        && tmp > Math.abs(xGrad * nwMag + (yGrad - xGrad) * nMag)
                        ) {
                    magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX : (int) (MAGNITUDE_SCALE * gradMag);
                } else {
                    magnitude[index] = 0;
                }
            }
        }
    }

    private float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    private float gaussian (float x, float sigma) {
        return (float) Math.exp(-(x * x) / (2f * sigma * sigma));
    }

    private void performHysteresis(int low, int high) {
        Arrays.fill(data, 0);

        int offset = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (data[offset] == 0 && magnitude[offset] >= high) {
                    follow(x, y, offset, low, 0);
                }
                offset++;
            }
        }
    }

    private void follow(int x1, int y1, int i1, int threshold, int depth) {
        if( depth > mFollowStackDepth)
            return;
        int x0 = x1 == 0 ? x1 : x1 - 1;
        int x2 = x1 == width - 1 ? x1 : x1 + 1;
        int y0 = y1 == 0 ? y1 : y1 - 1;
        int y2 = y1 == height -1 ? y1 : y1 + 1;

        data[i1] = magnitude[i1];
        for (int x = x0; x <= x2; x++) {
            for (int y = y0; y <= y2; y++) {
                int i2 = x + y * width;
                if ((y != y1 || x != x1)
                        && data[i2] == 0
                        && magnitude[i2] >= threshold) {
                    follow(x, y, i2, threshold, depth+1);
                    return;
                }
            }
        }
    }

    private void thresholdEdges() {
        for (int i = 0 ; i < picSize ; i++) {
            data[i] = data[i] > 0 ? -1 : 0xff000000;
        }
    }

    private void writeEdges (int pixels[]) {
        if (edgesImg == null) {
            edgesImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        edgesImg.setPixels(pixels, 0, width, 0, 0, width, height);
    }

}
