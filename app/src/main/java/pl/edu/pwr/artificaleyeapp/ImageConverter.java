package pl.edu.pwr.artificaleyeapp;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by nieop on 02.11.2016.
 */
public class ImageConverter extends BitmapConverter {

    private static String recognizedText;
    private final static String DATA_PATH = "";
    private final static String LANGUAGE = "pol";

    public static String getRecognizedText() {
        return recognizedText;
    }

    public ImageConverter(Bitmap bitmap) {
        setImage(bitmap);
        convertImageToBlackAndWhite();
        binarizeImage();

        //WYCIĘCIE PROSTOKĄTA ZE ZNAKIEM

        if (isMoreBlack())
            revertColors();

        if (detectBorder()) {
            cutBorders();
        }

        doMagic();
    }

    private static void doMagic() {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        String path = "/mnt/sdcard/";
        baseApi.init(path, LANGUAGE);
        baseApi.setImage(getImage());
        recognizedText = baseApi.getUTF8Text();
        baseApi.end();
    }

}
