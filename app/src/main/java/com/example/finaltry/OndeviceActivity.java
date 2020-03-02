package com.example.finaltry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public class OndeviceActivity extends AppCompatActivity {

    private static final String TAG = "RESPONSE_DATA";
    EditText editText;
    ImageView imageView1;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String[] cameraPermission;
    String[] storagePermission;

    Uri image_uri;


    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondevice);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editText = findViewById(R.id.editText2);
        editText.setEnabled(false);
        imageView1 = findViewById(R.id.imageView2);
        progressBar = findViewById(R.id.progressBar);

        //camera permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permissions
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }

    //Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Handle ActionBar item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.addImage) {

            showImageImportDialog();

        } else if (id == android.R.id.home) {

            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {

        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialoge = new AlertDialog.Builder(this);

        dialoge.setTitle("Choose Image From");
        dialoge.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //Camera Option clicked
                    if (!checkCameraPermissions()) {
                        //camera permissions not allowed,request it
                        requestCameraPermissions();
                    } else {
                        //Camera permissions allowed,take picture
                        PickCamera();
                    }
                }
                if (which == 1) {
                    //gallery Option clicked
                    if (!checkStoragePermissions()) {
                        //storage permissions not allowed,request it
                        requestStoragePermissions();
                    } else {
                        //Storage permissons allowed,take pic
                        PickGallery();
                    }
                }

            }
        });
        dialoge.create().show();

    }

    private void PickGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void PickCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermissions() {

        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                assert data != null;
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                assert result != null;
                Uri resulturi1 = result.getUri();
                imageView1.setImageURI(resulturi1);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView1.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                try {

                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    //FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(OndeviceActivity.this, resulturi1);
                    FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                            .getOnDeviceTextRecognizer();
                    recognizer.processImage(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<FirebaseVisionText>() {
                                        @Override
                                        public void onSuccess(FirebaseVisionText texts) {
                                            displayTextFromImage(texts);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception

                                            progressBar.setVisibility(View.GONE);
                                            e.printStackTrace();
                                        }
                                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void displayTextFromImage(FirebaseVisionText texts) {
       /* List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
        } else {
            for (FirebaseVisionText.TextBlock block : texts.getTextBlocks()) {
                String text = block.getText();
                editText.setText("The text in the above image was : " + text);
            }
        }*/

        String block = texts.getText();
        if (block.length()==0){
            editText.setText("No Text Found in the above Image");
        }else {
            Log.d(TAG, "displayTextFromImage: " + block);
            editText.setEnabled(true);
            editText.setText("The text in the above image was : \n\n" + block);
        }


    }


}
