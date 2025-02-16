package com.example.shiju;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;


public class ClassFragment extends Fragment {
    private SuperTextView tip;
    private RadiusImageView classedImage;
    // 定义从相册选择图像的路径
    private String albumImagePath;
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


}