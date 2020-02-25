package com.example.finaltry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class Cloud extends AppCompatActivity {

    EditText editText;
    ImageView imageView1;
    ProgressBar progressBar;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        RelativeLayout layout = findViewById(R.id.relative);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setSubtitle("Pick Image-->");


        editText=findViewById(R.id.editText);
        imageView1=findViewById(R.id.imageView);

        //camera permissions
        cameraPermission = new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permissions
        storagePermission = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        progressBar = new ProgressBar(Cloud.this, null, android.R.attr.progressBarStyleLarge);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        layout.addView(progressBar, params);
//
//        progressBar.setVisibility(View.GONE);
    }

    //Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    //Handle ActionBar item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id == R.id.addImage){

            showImageImportDialog();
        }
        if (id == R.id.settings){

        }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {

        String [] items  ={" Camera","Gallery"};
        AlertDialog.Builder dialoge = new AlertDialog.Builder(this);

        dialoge.setTitle("Select Image");
        dialoge.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which==0){
                    //Camera Option clicked
                    if (!checkCameraPermissions()){
                        //camera permissions not allowed,request it
                        requestCameraPermissions();
                    }
                    else{
                        //Camera permissions allowed,take picture
                        PickCamera();
                    }
                }
                if (which==1){
                    //gallery Option clicked
                    if (!checkStoragePermissions()){
                        //storage permissions not allowed,request it
                        requestStoragePermissions();
                    }
                    else{
                        //Storage permissons allowed,take pic
                        PickGallery();
                    }
                }

            }
        });
        dialoge.create().show();

    }

    private void PickGallery() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void PickCamera() {

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermissions() {

        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermissions() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==(PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==(PackageManager.PERMISSION_GRANTED);



        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        PickCamera();
                    }
                    else{
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                    }

                }

                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0) {
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
        if (resultCode == RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){

                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){

                Uri resulturi1 = result.getUri();
                imageView1.setImageURI(resulturi1);

                BitmapDrawable bitmapDrawable=(BitmapDrawable)imageView1.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();



                /*FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
                recognizer.processImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText texts) {

                                        displayTextFromImage(texts);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception

                                        e.printStackTrace();
                                    }
                                });*/
                /*progressBar.setVisibility(View.VISIBLE);*/
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                        .getCloudDocumentTextRecognizer();
                recognizer.processImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseVisionDocumentText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionDocumentText texts) {


                                        processCloudTextRecognitionResult(texts);
                                        /*progressBar.setVisibility(View.GONE);*/


                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception

                                        e.printStackTrace();
                                    }
                                });

            }
        }
    }

    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText texts) {

        List<FirebaseVisionDocumentText.Block> blocks = texts.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            for (FirebaseVisionDocumentText.Block block: texts.getBlocks())
            {
                String text =block.getText();
                editText.setText(text);
            }
        }
    }




  /* private void displayTextFromImage(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            for (FirebaseVisionText.TextBlock block: texts.getTextBlocks())
            {
                String text =block.getText();
                editText.setText(text);
            }
        }
    }*/


}
