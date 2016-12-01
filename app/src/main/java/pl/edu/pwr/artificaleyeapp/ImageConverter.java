package pl.edu.pwr.artificaleyeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    public ImageConverter(Bitmap bitmap) throws IOException {
        setImage(bitmap);
        findRectangle();
        setImage(getImage());
            convertImageToBlackAndWhite();
            binarizeImage();
            if (isMoreBlack())
                revertColors();

            if (detectBorder()) {
                cutBorders();
            }
                String tmp = doMagic(getImage());
                    recognizedText = tmp;
    }
    private static String doMagic(Bitmap bitmap) throws IOException {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        File tessdataFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/tessdata");
        if (!tessdataFolder.exists()) {
            getData();
        }
        String path = String.valueOf(Environment.getExternalStorageDirectory()) + "/";
        baseApi.init(path, LANGUAGE);
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();
        return recognizedText;
    }

    private static void getData() throws IOException {
        String filepath = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try
        {
            URL url = new URL("https://github.com/kraczyns/data/raw/master/pol.traineddata");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File tessdataFolder = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/tessdata");
            String filename="pol.traineddata";
            tessdataFolder.mkdir();
            File file = new File(tessdataFolder, filename);
            if(file.createNewFile())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0 )
            {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
            fileOutput.close();
            if(downloadedSize==totalSize) filepath=file.getPath();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            filepath=null;
            e.printStackTrace();
        }
    }

}
