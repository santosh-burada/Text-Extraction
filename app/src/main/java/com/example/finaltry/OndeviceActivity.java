package com.example.finaltry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;



import android.annotation.SuppressLint;


import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class OndeviceActivity extends AppCompatActivity {
    private static final String TAG = "RESPONSE_DATA";
    EditText editText;
    ImageView imageView1;
    protected CommonTasks commonTasks;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ondevice);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        commonTasks = new CommonTasks(OndeviceActivity.this);
        editText = findViewById(R.id.editText2);
        editText.setEnabled(false);
        imageView1 = findViewById(R.id.imageView2);
        progressBar = findViewById(R.id.progressBar);
    }

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

            commonTasks.showImageImportDialog();

        } else if (id == android.R.id.home) {

            onBackPressed();
        }else if (id == R.id.translate ){
            String edi = editText.getText().toString();

            if (edi.equals("")) {
                Toast.makeText(this, "please get the text first", Toast.LENGTH_SHORT).show();
            } else{
                String query=editText.getText().toString();
                /* translate();*/
                Intent intent = new Intent(OndeviceActivity.this, Translate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("edittext",query);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CommonTasks.IMAGE_PICK_GALLERY_CODE) {

                assert data != null;
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
            if (requestCode == CommonTasks.IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(commonTasks.image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);

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
            editText.setText("" + block);
        }


    }


}
