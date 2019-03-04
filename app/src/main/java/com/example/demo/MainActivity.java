package com.example.demo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static int CAMERA_REQUEST;                          // To detect and identify camera request source
    private static final int MY_CAMERA_PERMISSION_CODE = 100;   // To perform permission request
    JSONObject photoPresentFlag = new JSONObject();
    JSONArray dataArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAppTitle));

        try {
            // get the hardcoded JSON Array
            dataArray = getHarcodedArray();

            // Dynamically create the UI using the harcoded JSON
            LinearLayout dynamicView = findViewById(R.id.llmain);
            LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            // Cycle through all items and create UI based on the type. ToDO: New types can be added via the switch statement.
            for (int i = 0; i < dataArray.length(); i++) {
                switch (dataArray.getJSONObject(i).getString("type")) {
                    // Dynamically create UI for type PHOTO
                    case "PHOTO":
                        RelativeLayout photoFrame;
                        photoFrame = createDynamicPhoto(i);
                        dynamicView.addView(photoFrame);
                        break;

                    // Dynamically create UI for type SINGLE_CHOICE
                    case "SINGLE_CHOICE":
                        RadioGroup choiceGroup;
                        choiceGroup = createDynamicChoice(i, dataArray, lprams);
                        dynamicView.addView(choiceGroup);
                        break;

                    // Dynamically create UI for type COMMENT
                    case "COMMENT":
                        LinearLayout commentLinearLayout;
                        commentLinearLayout = createDynamicComment(i, dataArray, lprams);
                        dynamicView.addView(commentLinearLayout);
                        break;

                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Oops. Something wrong with hardcoded JSON!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Oops. Something went terribly wrong!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    // Function to create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Function to perform onclick functionality of options menu - SUBMIT button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // SUBMIT button logic
        if (id == R.id.action_submit) {
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    switch (dataArray.getJSONObject(i).getString("type")) {
                        // Use id and get data | photo id = (i+1) | delete button id = (i+1)*1000
                        case "PHOTO":
                            int relativeIdPhoto = i + 1;
                            String photoValue = "";
                            if (photoPresentFlag.getBoolean(String.valueOf(relativeIdPhoto))) {
                                photoValue = findViewById(relativeIdPhoto).getBackground().toString();
                            }
                            Log.d(dataArray.getJSONObject(i).getString("id"), photoValue);
                            break;

                        // Use id and get data | group id = (i+1) | btn id = (i + j + total dataArray items)
                        case "SINGLE_CHOICE":
                            int relativeIdGroup = i + 1;
                            RadioGroup logChoiceRadioGroup = findViewById(relativeIdGroup);
                            String radioValue = "";
                            if ((findViewById(logChoiceRadioGroup.getCheckedRadioButtonId())) != null) {
                                radioValue = ((RadioButton) findViewById(logChoiceRadioGroup.getCheckedRadioButtonId())).getText().toString();
                            }
                            Log.d(dataArray.getJSONObject(i).getString("id"), radioValue);
                            break;

                        // Use id and get data | switch id (i + (total items in dataArray*100)) | comment box id (i + (dataArray.length()*100) + 1)
                        case "COMMENT":
                            int relativeIdSwitch = (i + (dataArray.length() * 100));
                            Switch logCommentSwitch = findViewById(relativeIdSwitch);
                            EditText logCommentEditText = findViewById(relativeIdSwitch + 1);
                            String commentValue = "";
                            if (logCommentSwitch.isChecked()) {
                                commentValue = logCommentEditText.getText().toString();
                            }
                            Log.d(dataArray.getJSONObject(i).getString("id"), commentValue);
                            break;

                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Function to create the hardcoded JSONArray
    public JSONArray getHarcodedArray() {

        //Main JSON array
        JSONArray dataArray = new JSONArray();
        try {
            //First item in array of type PHOTO
            JSONObject photoOne = new JSONObject();
            JSONObject photoOneDataMap = new JSONObject();
            photoOne.put("type", "PHOTO");
            photoOne.put("id", "pic1");
            photoOne.put("title", "Photo 1");
            photoOne.put("dataMap", photoOneDataMap);

            //Second item in array of type SINGLE_CHOICE
            JSONObject choiceOne = new JSONObject();
            JSONObject choiceOneDataMap = new JSONObject();
            JSONArray choiceOneOptions = new JSONArray();
            choiceOneOptions.put(0, "Good");
            choiceOneOptions.put(1, "OK");
            choiceOneOptions.put(2, "Bad");
            choiceOneDataMap.put("options", choiceOneOptions);
            choiceOne.put("type", "SINGLE_CHOICE");
            choiceOne.put("id", "choice1");
            choiceOne.put("title", "Photo 1 choice");
            choiceOne.put("dataMap", choiceOneDataMap);

            //Third item in array of type COMMENT
            JSONObject commentOne = new JSONObject();
            JSONObject commentOneDataMap = new JSONObject();
            commentOne.put("type", "COMMENT");
            commentOne.put("id", "comment1");
            commentOne.put("title", "Photo 1 comments");
            commentOne.put("dataMap", commentOneDataMap);

            //Fourth item in array of type PHOTO
            JSONObject photoTwo = new JSONObject();
            JSONObject photoTwoDataMap = new JSONObject();
            photoTwo.put("type", "PHOTO");
            photoTwo.put("id", "pic2");
            photoTwo.put("title", "Photo 2");
            photoTwo.put("dataMap", photoTwoDataMap);

            //Fifth item in array of type SINGLE_CHOICE
            JSONObject choiceTwo = new JSONObject();
            JSONObject choiceTwoDataMap = new JSONObject();
            JSONArray choiceTwoOptions = new JSONArray();
            choiceTwoOptions.put(0, "Good");
            choiceTwoOptions.put(1, "OK");
            choiceTwoOptions.put(2, "Bad");
            choiceTwoDataMap.put("options", choiceOneOptions);
            choiceTwo.put("type", "SINGLE_CHOICE");
            choiceTwo.put("id", "choice2");
            choiceTwo.put("title", "Photo 2 choice");
            choiceTwo.put("dataMap", choiceTwoDataMap);

            //Sixth item in array of type COMMENT
            JSONObject commentTwo = new JSONObject();
            JSONObject commentTwoDataMap = new JSONObject();
            commentTwo.put("type", "COMMENT");
            commentTwo.put("id", "comment2");
            commentTwo.put("title", "Photo 2 comments");
            commentTwo.put("dataMap", commentTwoDataMap);

            dataArray.put(0, photoOne);
            dataArray.put(1, choiceOne);
            dataArray.put(2, commentOne);
            dataArray.put(3, photoTwo);
            dataArray.put(4, choiceTwo);
            dataArray.put(5, commentTwo);

            // Log the hardcoded JSON
            Log.d("dataArray", dataArray.toString());
        } catch (JSONException e) {
            Toast.makeText(this, "Oops. Something wrong with hardcoded JSON!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return dataArray;
    }

    // Function to dynamically create PHOTO | photo id = (i+1) | delete button id = (i+1)*1000
    public RelativeLayout createDynamicPhoto(int i) {
        RelativeLayout photoFrame = new RelativeLayout(this);
        try {
            // Create the Relative Layout in which photo and delete button reside
            RelativeLayout.LayoutParams RpramsPhotoFrame = new RelativeLayout.LayoutParams(325, 325);
            photoFrame.setLayoutParams(RpramsPhotoFrame);

            // Set photopresent flag false on creation
            int id = i + 1;
            photoPresentFlag.put(String.valueOf(id), false);

            // Create ImageButton to hold thumbnail
            ImageButton photo = new ImageButton(this);
            photo.setId(id);
            RelativeLayout.LayoutParams RpramsPhoto = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            RpramsPhoto.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            photo.setLayoutParams(RpramsPhoto);
            photo.setMaxWidth(300);
            photo.setMinimumWidth(300);
            photo.setMaxHeight(300);
            photo.setMinimumHeight(300);
            photo.setBackground(getResources().getDrawable(android.R.drawable.gallery_thumb));
            photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Check photo present flag
                        if (photoPresentFlag.getBoolean(String.valueOf(v.getId()))) {
                            final Dialog imageDialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setCancelable(true);
                            imageDialog.setContentView(R.layout.preview_image);
                            Button btnClose = imageDialog.findViewById(R.id.btnIvClose);
                            ImageView ivPreview = imageDialog.findViewById(R.id.iv_preview_image);
                            ivPreview.setBackground(v.getBackground());

                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    imageDialog.dismiss();
                                }
                            });
                            imageDialog.show();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                            } else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                CAMERA_REQUEST = v.getId();
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Create delete button
            ImageButton photoDelBtn = new ImageButton(this);
            photoDelBtn.setBackground(getResources().getDrawable(R.drawable.ic_remove_photo));
            RelativeLayout.LayoutParams RpramsBtn = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            RpramsBtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            RpramsBtn.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            photoDelBtn.setLayoutParams(RpramsBtn);
            // keeping delete button id relative to photo
            photoDelBtn.setId(id * 1000);
            photoDelBtn.setVisibility(View.GONE);
            photoDelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int id = (Integer) v.getId() / 1000;
                        photoPresentFlag.put(String.valueOf(id), false);
                        ImageButton photo = findViewById(id);
                        photo.setBackground(getResources().getDrawable(android.R.drawable.gallery_thumb));
                        v.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Add the photo and the delete button to the relative layout
            photoFrame.addView(photo);
            photoFrame.addView(photoDelBtn);
        } catch (Exception e) {
            Toast.makeText(this, "Oops. Something wrong when creating Photo!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return photoFrame;
    }

    // Function to dynamically create CHOICE | group id = (i+1) | btn id = (i + j + total dataArray items)
    public RadioGroup createDynamicChoice(int i, JSONArray dataArray, LinearLayout.LayoutParams lprams) {
        // Create Radio Group
        RadioGroup choiceGroup = new RadioGroup(this);
        try {
            choiceGroup.setId(i + 1);
            choiceGroup.setLayoutParams(lprams);
            choiceGroup.setOrientation(LinearLayout.HORIZONTAL);
            JSONArray options = dataArray.getJSONObject(i).getJSONObject("dataMap").getJSONArray("options");

            // Add radio buttons to radio group
            for (int j = 0; j < options.length(); j++) {
                RadioButton choice = new RadioButton(this);
                // keeping radio button id relative to radio group
                choice.setId(i + j + dataArray.length());
                choice.setText(options.getString(j));
                choiceGroup.addView(choice);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Oops. Something wrong with JSON when creating Radio Group!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Oops. Something wrong when creating Radio Group!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return choiceGroup;
    }

    // Function to dynamically create COMMENT | switch id (i + (total items in dataArray*100)) | comment box id (i + (dataArray.length()*100) + 1)
    public LinearLayout createDynamicComment(int i, JSONArray dataArray, LinearLayout.LayoutParams lprams) {
        LinearLayout commentLinearLayout = new LinearLayout(this);
        try {
            // Text View that will hold the "provide comment?" question
            TextView commentTextView = new TextView(this);
            commentTextView.setText(getString(R.string.comment_question));
            commentLinearLayout.setLayoutParams(lprams);

            // Switch that can be toggled on and off to display comment box
            Switch showComment = new Switch(this);
            // Set switch id
            showComment.setId(i + dataArray.length() * 100);
            showComment.setChecked(false);
            showComment.setLayoutParams(lprams);

            // Secondary layout that will hold the "provide comment?" question and the switch
            LinearLayout switchLinearLayout = new LinearLayout(this);
            switchLinearLayout.setLayoutParams(lprams);
            switchLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            switchLinearLayout.addView(commentTextView);
            switchLinearLayout.addView(showComment);

            // Todo Border the comment section
            //GradientDrawable border = new GradientDrawable();
            //border.setColor(0xFFFFFFFF); //white background
            //border.setStroke(1, 0xFF000000); //black border with full opacity
            //commentLinearLayout.setBackground(border);

            // Edit Text that
            EditText commentEditText = new EditText(this);
            // Set ediText id one more than switch id
            commentEditText.setId(i + (dataArray.length() * 100) + 1);
            commentEditText.setHint("Type comment");
            commentEditText.setVisibility(View.GONE);
            showComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // If Switch is ON show comment box
                    int id = ((Integer) buttonView.getId()) + 1;
                    if (isChecked) {
                        findViewById(id).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(id).setVisibility(View.GONE);
                    }
                }
            });

            // Primary layout that will hold the entire comment section - Secondary Layout and the Edit Text
            commentLinearLayout.setLayoutParams(lprams);
            commentLinearLayout.setOrientation(LinearLayout.VERTICAL);
            commentLinearLayout.setPadding(0, 0, 0, 16);
            commentLinearLayout.addView(switchLinearLayout);
            commentLinearLayout.addView(commentEditText);

        } catch (Exception e) {
            Toast.makeText(this, "Oops. Something wrong when creating Comment!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return commentLinearLayout;
    }

    // Function to request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Function to get camera result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), photo);
                findViewById(CAMERA_REQUEST).setBackground(bitmapDrawable);                          // Set background image for photo
                findViewById(CAMERA_REQUEST * 1000).setVisibility(View.VISIBLE);                       // Show remove photo icon
                photoPresentFlag.put(String.valueOf(CAMERA_REQUEST), true);
            } catch (Exception e) {
                Toast.makeText(this, "Oops. Something wrong with camera output!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
