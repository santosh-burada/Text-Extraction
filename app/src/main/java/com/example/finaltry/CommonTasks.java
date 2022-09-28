package com.example.finaltry;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CommonTasks extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    protected static final int IMAGE_PICK_GALLERY_CODE = 1000;
    protected static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String[] cameraPermission;
    String[] storagePermission;
    Uri image_uri;
    Activity CloudOndevice;

    protected void showImageImportDialog() {

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialoge = new AlertDialog.Builder(CloudOndevice);

        dialoge.setTitle("Choose Image From");
        dialoge.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //Camera Option clicked
                    if (!checkCameraPermissions(CloudOndevice)) {
                        //camera permissions not allowed,request it
                        requestCameraPermissions(CloudOndevice);
                    } else {
                        //Camera permissions allowed,take picture
                        PickCamera();
                    }
                }
                if (which == 1) {
                    //gallery Option clicked
                    if (!checkStoragePermissions(CloudOndevice)) {
                        //storage permissions not allowed,request it
                        requestStoragePermissions(CloudOndevice);
                    } else {
                        //Storage permissons allowed,take pic
                        PickGallery();
                    }
                }

            }
        });
        dialoge.create().show();

    }


    public CommonTasks(Activity context){
        CloudOndevice=context;
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permissions
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    protected void PickGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        CloudOndevice.startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    protected void PickCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri = CloudOndevice.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        CloudOndevice.startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    protected void requestStoragePermissions(Activity activity) {

        ActivityCompat.requestPermissions(activity,storagePermission, STORAGE_REQUEST_CODE);
    }

    protected boolean checkStoragePermissions(Context context) {

        return ContextCompat.checkSelfPermission(context, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    protected void requestCameraPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, cameraPermission, CAMERA_REQUEST_CODE);
    }

    protected boolean checkCameraPermissions(Context context) {

        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);


        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        PickCamera();
                    } else {
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                    }

                }

                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        PickGallery();
                    } else {
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
