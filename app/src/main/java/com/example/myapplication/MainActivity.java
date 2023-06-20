package com.example.myapplication;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.pytorch.IValue;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;


public class MainActivity extends AppCompatActivity {

    Bitmap bitmap = null;
    String modelPath = null;
    private static final String TAG = "测试";
    // 创建pytorch模型
//    private Module mobileNet;

    // 定义ImageView对象show_image展示的图片的路径
    private String showImagePath;
    // 定义相机拍摄图像的路径
    private String cameraImagePath;
    // 定义从相册选择图像的路径
    private String albumImagePath;

    // 开启相机和调用相册的请求码
    private static final int START_CAMERA_CODE = 1111;
    private static final int START_ALBUM_CODE = 1112;
    // 请求权限的请求码
    private static final int REQUIRE_PERMISSION_CODE = 111;

    // 创建用于开启相机的按钮
    private Button btnStartCamera;
    // 创建用于调用系统相册的按钮
    private Button btnStartAlbum;
    // 创建用于启动pytorch前向推理的按钮
    private Button btnStartPredict;

    // 创建ImageView对象
    private ImageView showOriginalImageView;

    private TextView showClsResultTextView;
    private Tensor TensorinTensor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        setContentView(R.layout.activity_main);
        loadModel(this);
        // 绑定开启相机的按钮
        btnStartCamera = (Button) findViewById(R.id.button1);
        btnStartCamera.setOnClickListener(new StartCameraOnClickListener());
        // 绑定调用相册的按钮
        btnStartAlbum = (Button) findViewById(R.id.button2);
        btnStartAlbum.setOnClickListener(new StartAlbumOnClickListener());

        btnStartPredict = (Button) findViewById(R.id.button3);
        btnStartPredict.setOnClickListener(new StartPredictOnClickListener());


        //  绑定我们在activity_main.xml中定义的Image_View
        showOriginalImageView = (ImageView) findViewById(R.id.img);

        showClsResultTextView = (TextView)findViewById(R.id.classed);
        //Log.i(TAG, cameraImagePath);
    }

    class StartCameraOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(showClsResultTextView.getTextSize() > 0){
                showClsResultTextView.setText("等待分类");
            }
            // 开启相机，返回相机拍摄图像的路径，传入的请求码是START_CAMERA_CODE
            cameraImagePath = Utils.startCamera(MainActivity.this, START_CAMERA_CODE);
            Log.i(TAG, "onClick: " + cameraImagePath);
        }
    }

    class StartAlbumOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(showClsResultTextView.getTextSize() > 0){
                showClsResultTextView.setText("等待分类");
            }
            // 调用相册，传入的请求码是START_ALBUM_CODE
            Utils.startAlbum(MainActivity.this, START_ALBUM_CODE);
        }
    }

    class StartPredictOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!check_Img_Selected(showImagePath)){
                Toast.makeText(MainActivity.this, "未拍摄照片或选择照片",Toast.LENGTH_LONG).show();
            }
            else classing(showImagePath);
        }
    }

    // 请求本案例需要的三种权限(添加对于安卓13的适配)
    private void requestPermissions() {
        // 定义容器，存储我们需要申请的权限
        List<String> permissionList = new ArrayList<>();
        List<String> permissionList_33 = new ArrayList<>();
        // 检测应用是否具有CAMERA的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
            permissionList_33.add(Manifest.permission.CAMERA);
        }
        // 检测应用是否具有READ_EXTERNAL_STORAGE权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        // 检测应用是否具有WRITE_EXTERNAL_STORAGE权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // 检测应用是否具有READ_MEDIA_IMAGES权限,安卓13特性
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 33){
            permissionList_33.add(Manifest.permission.READ_MEDIA_IMAGES);
        }
        Log.i(TAG, "测试权限" + permissionList);
        // 如果permissionList不为空，则说明前面检测的多个权限中至少有一个是应用不具备的
        // 则需要向用户申请使用permissionList中的权限 一个用于安卓13以下，一个用于安卓13
        if (Build.VERSION.SDK_INT < 33 && !permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), REQUIRE_PERMISSION_CODE);
        }
        else if(!permissionList_33.isEmpty() && Build.VERSION.SDK_INT >= 33){
            ActivityCompat.requestPermissions(this, permissionList_33.toArray(new String[permissionList_33.size()]), REQUIRE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 判断请求码
        switch (requestCode) {
            // 如果请求码是我们设定的权限请求代码值，则执行下面代码
            case REQUIRE_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        // 如果请求被拒绝，则弹出下面的Toast
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, permissions[i] + " was denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "进入选择相机或者相册环节 ");
            Log.i(TAG, "接受到的请求码是" + requestCode);
            switch (requestCode) {
                case START_ALBUM_CODE:
                    if (data == null) {
                        Log.w("LOG", "user photo data is null");
                        return;
                    }

                    Uri albumImageUri = data.getData();
                    albumImagePath = Utils.getPathFromUri(MainActivity.this, albumImageUri);
                    showImagePath = albumImagePath;
                    Bitmap bitmapAlbum = Utils.getScaleBitmapByPath(albumImagePath);
                    //showOriginalImageView.setImageBitmap(Utils.getScaleBitmapByBitmap(bitmapAlbum,512,512));
                    showOriginalImageView.setImageBitmap(bitmapAlbum);
                    Toast.makeText(MainActivity.this, "album start", Toast.LENGTH_LONG).show();
                    break;
                case START_CAMERA_CODE:
                    Log.i(TAG, "使用相机拍摄的照片");
                    showImagePath = cameraImagePath;
                    Bitmap bitmapCamera = Utils.getScaleBitmapByPath(cameraImagePath);
                    showOriginalImageView.setImageBitmap(bitmapCamera);
                    Toast.makeText(MainActivity.this, "camera start", Toast.LENGTH_LONG).show();
                    //showOriginalImageView.setImageBitmap(Utils.getScaleBitmapByBitmap(bitmapCamera, 512, 512));
                    break;
            }
        }
    }
    public void loadModel(Context context){
        try {
            modelPath = assetFilePath(context, "mobile_eff.pt");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //判断进行分类时是否选择图片
    public boolean check_Img_Selected(String showImagePath){
        if(showImagePath == null){
            return false;
        }
        return true;
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
    public void classing(String imgPth){
    // 获取需要分类的图像
        Bitmap bmp= null;
        Bitmap scaledBmp = null;
        // 设定输入维度
        int inDims[] = {224, 224, 3};
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(imgPth));
            bmp = BitmapFactory.decodeStream(bis);
            scaledBmp = Bitmap.createScaledBitmap(bmp, inDims[0], inDims[1], true);
            bis.close();
        } catch (Exception e) {
            Log.d("LOG", "can not read image bitmap");
        }
        Classifier classifier = new Classifier(modelPath);
        String ans = classifier.imgPredict(scaledBmp);
        showClsResultTextView.setText(ans);
    }

//        //显示图片
//        ImageView imageView = findViewById(R.id.image);
//        imageView.setImageBitmap(bitmap);
//        //显示结果
//        TextView textView = findViewById(R.id.text);
//        String tex = "推理结果：" + className;
//        textView.setText(tex);

    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

//    private void classify(String imagePath){
//        //MobileNet 权重文件
//        String ptPath = "mobile_net.pt";
//        //设定输入维度
//        int inDims[] = {150,150,3};
//
//        //省略模型加载与数据获取
//
//        //对输入图像预处理，获得输入张量
//        float[] meanRGB = {0.485f,0.456f,0.406f};
//        float[] stdRGB = {0.229f,0.224f,0.225f};
//        Bitmap scaleBmp = null;
//        TensorinTensor = TensorImageUtils.bitmapToFloat32Tensor(scaleBmp,meanRGB,stdRGB);
//
//        try {
//            //进行分类计算
//            TensorclsTensor = mobileNet.forward(IValue.from(inTensor)).toTensor();
//            float[] clsArray = clsTensor.getDataAsFloatArray();
//            softmax(clsArray);
//
//            //根据分类概率值，解析图像所属类别
//            int[] top3id = getTopThree(clsArray);
//
//            //根据对应的4种图像分类，在文本框中显示前3种图像类别和对应概率值
//            String[] cls = {"Citrus Black spot",
//                    "Citrus Canker",
//                    "Citrus Greening",
//                    "Citrus Healthy"};
//            String result = "Top 1:"+cls[top3id[0]]+","+String.valueOf(clsArray[top3id[0]]);
//            result +="\n"+"Top 2:"+cls[top3id[1]]+","+String.valueOf(clsArray[top3id[1]]);
//            result +="\n"+"Top 3:"+cls[top3id[2]]+","+String.valueOf(clsArray[top3id[2]]);
//
//            showClsResultTextView.setText(result);
//        }catch (Exception e){
//            Log.e("Log","fail to preform classify");
//            e.printStackTrace();
//        }
//
//
//    }

    private static void softmax(float[] arr){
        float sumExp = 0.00001f;
        for(int i=0;i<arr.length;i++){
            sumExp += Math.exp(arr[i]);
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

}
