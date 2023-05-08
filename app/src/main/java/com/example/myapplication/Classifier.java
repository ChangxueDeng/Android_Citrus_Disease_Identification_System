package com.example.myapplication;

import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

//分类器
public class Classifier {
    //模型
    Module module;

    //构造函数 加载模型
    public Classifier(String model_Path){
        this.module = Module.load(model_Path);
    }

    //图片处理 Bitmap:图对象
    public Tensor imgToTensor(Bitmap bitmap, int size){
        //进行图片大小剪切
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        //转化为Tensor并进行归一化处理
        return TensorImageUtils.bitmapToFloat32Tensor(bitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);
    }

    //获取最大预测值下标
    public int preMax(float[] inputTensor){
        int maxIndex = -1;
        float maxTempValue = -Float.MAX_VALUE;

        for(int i = 0; i < inputTensor.length; i++){
            if(inputTensor[i] > maxTempValue) {
                maxIndex = i;
                maxTempValue = inputTensor[i];
                //System.out.println(inputTensor[i] + " " + i);
            }
        }
        return maxIndex;
    }
//    public static void softmax(float arr[]){
//        float sumExp = 0.00001f;
//        for(int i = 0; i < arr.length; i++){
//            sumExp += Math.exp(arr[i]);
//        }
//
//        for(int i = 0; i < arr.length; i++){
//            arr[i] = (float) (Math.exp(arr[i]) / sumExp);
//            arr[i] = ((int)(arr[i] * 1000)) / 10000.0f;
//            if(arr[i] < 0.0001f){
//                arr[i] = 0.0001f;
//            }
//        }
//        System.out.println(arr[0]);
//    }


    //预测函数
    public String imgPredict(Bitmap bitmap){
        Tensor tensor = imgToTensor(bitmap, 224);
        IValue inputs = IValue.from(tensor);
        Tensor outputs = module.forward(inputs).toTensor();
        float[] inputTensor = outputs.getDataAsFloatArray();
        //softmax(inputTensor);
        int classIndex = preMax(inputTensor);
        return Classed.IMAGENET_CLASSES[classIndex];
    }

}
