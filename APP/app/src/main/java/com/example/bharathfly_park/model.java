package com.example.bharathfly_park;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProcessor;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class model {
    Interpreter tfLite;


    int[] intValues;
    boolean closed = false;
    int inputSize=100;  //Input size for model
    boolean isModelQuantized=false;
    float[] preds;
    float IMAGE_MEAN = 1;
    float IMAGE_STD = 127.5F;
    public float embeds[];
    int OUTPUT_SIZE=192; //Output size of model

    String modelFile="mobile_face_net.tflite"; //model name
    Activity activity;

    public model(String model_name, Activity activity){
        this.modelFile = model_name;
        this.activity = activity;
        try {
            tfLite=new Interpreter(loadModelFile(this.activity,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPredictions(Bitmap img_bitmap){
//        Bitmap img_bitmap = BitmapFactory.decodeResource(activity.getResources(), id);
        Bitmap scaled = getResizedBitmap(img_bitmap, inputSize, inputSize);

        recognizeImage(scaled); //Send scaled bitmap to create face embeddings.

    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
//        bm.recycle();
        return resizedBitmap;
    }
    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.i("DECLARED LENGTH: ", Long.toString(declaredLength));
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


//    public void classify(Bitmap image, int imageRotation) {
//
//        // Inference time is the difference between the system time at the start
//        // and finish of the process
//        long inferenceTime = SystemClock.uptimeMillis();
//
//        // Create preprocessor for the image.
//        // See https://www.tensorflow.org/lite/inference_with_metadata/
//        //            lite_support#imageprocessor_architecture
//        ImageProcessor imageProcessor =
//                new ImageProcessor.Builder().add(new Rot90Op(-imageRotation / 90)).build();
//
//        // Preprocess the image and convert it into a TensorImage for classification.
//        TensorImage tensorImage =
//                imageProcessor.process(TensorImage.fromBitmap(image));
//
//        // Classify the input image
//        imageClassifier.classify(tensorImage);
//
//        List<Classifications> result = imageClassifier.classify(tensorImage);
//
//        inferenceTime = SystemClock.uptimeMillis() - inferenceTime;
//        imageClassifierListener.onResults(result, inferenceTime);
//    }

    private void recognizeImage(final Bitmap bitmap) {
        //Create ByteBuffer to store normalized image

        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * bitmap.getWidth() + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        float[][] preds_ = new float[1][2]; //output of model will be stored in this variable
        outputMap.put(0, preds_);

        if (closed == false) {
            tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model
//        Log.i("PREDICTION: ", Arrays.deepToString(preds_));
            this.preds = preds_[0];
        }
    }



}
