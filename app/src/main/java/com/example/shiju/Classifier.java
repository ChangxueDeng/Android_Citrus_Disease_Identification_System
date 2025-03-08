package com.example.shiju;

import android.graphics.Bitmap;
import android.util.Pair;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

public class Classifier {
    private static volatile Classifier instance;
    private static String modelPathUsed;
    private final Module module;

    private Classifier(String modelPath) {
        this.module = Module.load(modelPath);
    }

    public static Classifier getInstance(String modelPath) {
        if (instance == null) {
            synchronized (Classifier.class) {
                if (instance == null) {
                    instance = new Classifier(modelPath);
                    modelPathUsed = modelPath;
                }
            }
        } else {
            if (!modelPath.equals(modelPathUsed)) {
                throw new IllegalStateException("Classifier already initialized with model path: " + modelPathUsed);
            }
        }
        return instance;
    }

    private Tensor imgToTensor(Bitmap bitmap, int size) {
        Bitmap processedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        return TensorImageUtils.bitmapToFloat32Tensor(
                processedBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB,
                MemoryFormat.CHANNELS_LAST
        );
    }
    private static void softmax1(float[] arr){
        float sumExp = 0.00001f;
        for(int i=0;i<arr.length;i++){
            sumExp += (float) Math.exp(arr[i]);
        }

        for(int i=0;i<arr.length;i++){
            arr[i] = (float) (Math.exp(arr[i])/sumExp);
            //保留四位有效数字
            arr[i] = ((int )(arr[i]*10000))/10000.0f;
            if(arr[i]<0.0001f){
                arr[i] = 0.0001f;
            }
        }
    }


    private static float findMax(float[] array) {
        float max = -Float.MAX_VALUE;
        for (float value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private static Pair<Integer, Float> getMaxProbability(float[] probabilities) {
        int maxIndex = 0;
        float maxProbability = probabilities[0];

        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                maxIndex = i;
            }
        }
        return new Pair<>(maxIndex, maxProbability);
    }

    public PredictionResult predictWithConfidence(Bitmap bitmap) {
        Tensor inputTensor = imgToTensor(bitmap, 224);
        Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
        float[] logits = outputTensor.getDataAsFloatArray();

        softmax1(logits);
        Pair<Integer, Float> result = getMaxProbability(logits);

        return new PredictionResult(
                result.first,
                result.second
        );
    }

    public static class PredictionResult {
        private final int label;
        private final float confidence;

        public PredictionResult(int label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }

        public int getLabel() {
            return label;
        }

        public float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f%%)", label, confidence * 100);
        }
    }
}