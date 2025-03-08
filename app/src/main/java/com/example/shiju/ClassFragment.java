package com.example.shiju;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.layout.XUILinearLayout;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;


public class ClassFragment extends Fragment {
    private SuperTextView tip;
    private RadiusImageView classedImage;
    // 定义从相册选择图像的路径
    private String albumImagePath;
    private TextView resultTextView;
    private TextView topTextView;
    private XUILinearLayout cate0;
    private XUILinearLayout cate1;
    private XUILinearLayout cate2;
    private XUILinearLayout cate3;
    private static final int START_ALBUM_CODE = 1112;
    // 请求权限的请求码
    private static final int REQUIRE_PERMISSION_CODE = 111;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tip = view.findViewById(R.id.tip);
        classedImage = view.findViewById(R.id.classedImage);
        resultTextView = view.findViewById(R.id.resultShow);
        topTextView = view.findViewById(R.id.topCent);
        cate0 = view.findViewById(R.id.cate0);
        cate1 = view.findViewById(R.id.cate1);
        cate2 = view.findViewById(R.id.cate2);
        cate3 = view.findViewById(R.id.cate3);
        tip.setRightImageViewClickListener(new SuperTextView.OnRightImageViewClickListener() {
            @Override
            public void onClick(ImageView imageView) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity)getActivity()).startAlbumFromFragment();
                }
            }
        });

    }

    public SuperTextView getTip() {
        return tip;
    }
    public void showImage(Bitmap bitmap){
        classedImage.setImageBitmap(bitmap);
    }
    // 新增方法，用于接收分类结果并显示
    public void showClassificationResult(Classifier.PredictionResult result) {
        if (resultTextView != null) {
            String labelStr = Category.getCategoryLabels().get(result.getLabel());
            int label = result.getLabel();
            switch (label){
                case 0 :
                    setResultColor(label, cate0);
                break;
                case 1:
                    setResultColor(label, cate1);
                    break;
                case 2:
                    setResultColor(label, cate2);
                    break;
                case 3:
                    setResultColor(label, cate3);
                    break;
            }
            resultTextView.setText(labelStr);
        }
        if (topTextView != null) {
            String confidence = String.format("%.2f%%", result.getConfidence() * 100);
            topTextView.setText(confidence);
        }
    }
    private void setResultColor(int label, XUILinearLayout cate){
        if (label >= 0 && label < LABEL_COLORS.length) {
            int color = ContextCompat.getColor(cate.getContext(), LABEL_COLORS[label]);
            cate.setBackgroundColor(color);
        }
    }
    // 在Activity或工具类中预先定义颜色映射关系
    private static final int[] LABEL_COLORS = {
            R.color.black_spot_b,  // label 0
            R.color.canker_b,  // label 1
            R.color.greening_b,   // label 2
            R.color.healthy_b,
    };
    public void resetResult(){
        int commonColor = R.color.white;
        cate0.setBackgroundColor(ContextCompat.getColor(cate0.getContext(), commonColor));
        cate1.setBackgroundColor(ContextCompat.getColor(cate1.getContext(), commonColor));
        cate2.setBackgroundColor(ContextCompat.getColor(cate2.getContext(), commonColor));
        cate3.setBackgroundColor(ContextCompat.getColor(cate3.getContext(), commonColor));
        resultTextView.setText(getResources().getString(R.string.default_result));
        topTextView.setText(getResources().getString(R.string.default_cent));
    }
}