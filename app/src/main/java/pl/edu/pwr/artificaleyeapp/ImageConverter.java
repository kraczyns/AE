package pl.edu.pwr.artificaleyeapp;

import android.graphics.Bitmap;

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
//        TessBaseAPI baseApi = new TessBaseAPI();
//        baseApi.setDebug(true);
//        baseApi.init(DATA_PATH, LANGUAGE);
//        baseApi.setImage(image);
//        recognizedText = baseApi.getUTF8Text();
//        baseApi.end();
    }

}
