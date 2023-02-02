package com.example.pharmacieapplication.Dialogs;

import static com.example.pharmacieapplication.Models.StaticVariable.IMG_REQUEST_CAMERA;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pharmacieapplication.Activities.AddOffreActivity;
import com.example.pharmacieapplication.Models.StaticVariable;
import com.example.pharmacieapplication.R;

public class DialogChoose extends Dialog {

    private AddOffreActivity activity;
    ImageView gallery, camera;

    private static final int PERMISSION_CODE1 = 1000;

    public DialogChoose(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose);
        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraCap();
                dismiss();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, StaticVariable.IMG_REQUEST_GALLERY);
    }

    void cameraCap() {
//        boolean b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                System.out.println("in permission denied");
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};//, Manifest.permission.WRITE_EXTERNAL_STORAGE
                activity.requestPermissions(permission, PERMISSION_CODE1);
            } else {
                System.out.println("in permission garanted");
                openCamera();
            }
        } else {
            System.out.println("in permission garanted");
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "desc");
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, IMG_REQUEST_CAMERA);
    }

    public AddOffreActivity getActivity() {
        return activity;
    }

    public void setActivity(AddOffreActivity activity) {
        this.activity = activity;
    }
}
