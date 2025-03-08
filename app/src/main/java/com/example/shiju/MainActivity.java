package com.example.shiju;
import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.tabbar.EasyIndicator;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yalantis.ucrop.UCrop;

import org.pytorch.Tensor;


public class MainActivity extends AppCompatActivity{

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
    // 新增图片裁切的请求码
    private static final int CROP_IMAGE_CODE = 1113;    // 请求权限的请求码
    private static final int REQUIRE_PERMISSION_CODE = 111;

    // 创建用于开启相机的按钮
    private RoundButton btnStartCamera;
    private RadiusImageView startCamera;
    // 创建用于调用系统相册的按钮
    private RoundButton btnStartAlbum;

    // 创建ImageView对象
    private ImageView showOriginalImageView;

    private TextView showClsResultTextView;
    private Tensor TensorinTensor;

    private final ClassFragment classFragment = new ClassFragment();
    private final HelpFragment helpFragment = new HelpFragment();

    private RadiusImageView radiusImageViewClass;
    private RadiusImageView radiusImageViewHelp;
    private TitleBar mtitleBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestPermissions();
        setContentView(R.layout.activity_main);
        loadModel(this);
        // 绑定开启相机的按钮
//        btnStartCamera = (RoundButton) findViewById(R.id.button1);
//        btnStartCamera.setOnClickListener(new StartCameraOnClickListener());
        startCamera = findViewById(R.id.btn21);
        startCamera.setOnClickListener(new StartCameraOnClickListener());
        // 绑定调用相册的按钮
//        btnStartAlbum = (RoundButton) classFragment.getTip()
//        btnStartAlbum.setOnClickListener(new StartAlbumOnClickListener());

        //  绑定我们在activity_main.xml中定义的Image_Viewa
//        showOriginalImageView = (ImageView)findViewById(R.id.img);
//        SuperTextView superTextView = (SuperTextView)findViewById(R.id.classed);
//        showClsResultTextView = superTextView.getLeftTextView();


        radiusImageViewClass = findViewById(R.id.btn11);
        radiusImageViewHelp = findViewById(R.id.btn31);
        //        一次性添加
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container,classFragment)
                .add(R.id.main_container,helpFragment)
                .hide(helpFragment)
                .commit();

        findViewById(R.id.btn1).setOnClickListener(
                (view)->{

                    //                    显示fragment1,隐藏fragment2
                    getSupportFragmentManager().beginTransaction()
                            .hide(helpFragment)
                            .show(classFragment)
                            .commit();
                    radiusImageViewHelp.setImageResource(R.drawable.light2);
                    radiusImageViewClass.setImageResource(R.drawable.identify1);
                }
        );
        findViewById(R.id.btn3).setOnClickListener(
                (view)->{
                    //                    显示fragment2,隐藏fragment1
                    getSupportFragmentManager().beginTransaction()
                            .hide(classFragment)
                            .show(helpFragment)
                            .commit();
                    radiusImageViewClass.setImageResource(R.drawable.identify2);
                    radiusImageViewHelp.setImageResource(R.drawable.light1);
                }
        );
        mtitleBar = findViewById(R.id.titlebar2);
        mtitleBar.addAction(new TitleBar.ImageAction(R.drawable.question_mark) {
            @Override
            public void performAction(View view) {
                XToastUtils.toast("引导提示");
            }
        });


        //Log.i(TAG, cameraImagePath);
    }

    class StartCameraOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // 开启相机，返回相机拍摄图像的路径，传入的请求码是START_CAMERA_CODE
            cameraImagePath = Utils.startCamera(MainActivity.this, START_CAMERA_CODE);

        }
    }
    public void startAlbumFromFragment(){
        Utils.startAlbum(MainActivity.this, START_ALBUM_CODE);
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

                // 启动uCrop进行裁切
                startCrop(Uri.fromFile(new File(albumImagePath)));

                break;
            case START_CAMERA_CODE:
                Log.i(TAG, "使用相机拍摄的照片");
                showImagePath = cameraImagePath;

                // 启动uCrop进行裁切
                startCrop(Uri.fromFile(new File(cameraImagePath)));

                break;
            // 处理uCrop的结果
            case UCrop.REQUEST_CROP:
                handleCropResult(data);
                break;
        }
    }
}
    public void loadModel(Context context){
        try {
            modelPath = assetFilePath(context, "mobile.pt");
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
//        Classifier.initialize(modelPath);
//        Classifier classifier = new Classifier(modelPath);
//        String ans = classifier.imgPredict(scaledBmp);
//        System.out.println(ans);

        Classifier.PredictionResult result = Classifier.getInstance(modelPath).predictWithConfidence(scaledBmp);
        // 将分类结果传递给ClassFragment
        classFragment.showClassificationResult(result);
        System.out.println("预测结果：" + result.getLabel());
        System.out.println("置信度：" + result.getConfidence());
        System.out.println("格式化输出：" + result.toString());
//        showClsResultTextView.setText(ans);
    }


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
    // 启动uCrop进行裁切
    private void startCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));
        options.setFreeStyleCropEnabled(true);
        UCrop uCrop = UCrop.of(sourceUri, destinationUri)
                .withOptions(options);
        uCrop.start(this);
    }

    // 处理uCrop的结果
    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            showImagePath = resultUri.getPath();
            Bitmap bitmap = Utils.getScaleBitmapByPath(showImagePath);
            if (classFragment.isVisible()) {
                classFragment.showImage(bitmap);
            }

            Toast.makeText(MainActivity.this, "裁切完成", Toast.LENGTH_LONG).show();
            // 图片路径存在则进行分类
            if (check_Img_Selected(showImagePath)) {
                classFragment.resetResult();
                classing(showImagePath);
            } else {
                Toast.makeText(MainActivity.this, "图片为空，请重新操作！", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "裁切失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
