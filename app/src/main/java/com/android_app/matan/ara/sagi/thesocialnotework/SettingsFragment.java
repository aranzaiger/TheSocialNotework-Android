package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class SettingsFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private static final String TAG = "[TSN/Settings]";
    private MainActivity parent;
    private ImageButton cameraBtn;
    private ImageView avatarImage;
    private EditText txt_username, txt_password, txt_email;
    private Uri currentImgUri;
    private TextView lbl_num_of_notes, lbl_num_of_liked;
    private User user;
    private Button btn_save;


    public SettingsFragment() {
        // Required empty public constructor
    }


    /**
     * a function that called when this class created
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * a function that called when view is created
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        this.parent = (MainActivity) getActivity(); // holds the activity
        Utils.showLoadingDialog(parent, "Just a sec...", ""); // loading dialog
        this.user = parent.getUser(); // holds the user

        this.cameraBtn = (ImageButton) view.findViewById(R.id.btn_camera);
        this.cameraBtn.setOnClickListener(this);
        this.avatarImage = (ImageView) view.findViewById(R.id.settings_userAvater_iamgeView);
        this.txt_email = (EditText) view.findViewById(R.id.txt_email);
        this.txt_password = (EditText) view.findViewById(R.id.txt_password);
        this.txt_username = (EditText) view.findViewById(R.id.txt_username);
        this.lbl_num_of_notes = (TextView) view.findViewById(R.id.lbl_num_of_notes);
        this.lbl_num_of_liked = (TextView) view.findViewById(R.id.lbl_num_of_liked);
        this.btn_save = (Button) view.findViewById(R.id.btn_save);
        this.btn_save.setOnClickListener(this);

        this.txt_username.setEnabled(false);

        //Populate The data
        Utils.URLtoImageView(avatarImage, user.getAvatar());
        this.txt_username.setText("" + user.getUsername());
        this.txt_password.setText("" + user.getPassword());
        this.txt_email.setText("" + parent.getUser().getEmail());

        this.lbl_num_of_notes.setText("" + user.getNumber_of_notes()); //TODO
        this.lbl_num_of_liked.setText("" + user.getLiked_notes().size());

        this.txt_password.addTextChangedListener(this);
        this.txt_email.addTextChangedListener(this);

        Utils.dismissLoadingDialog();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * a function that called on click
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                if ((ActivityCompat.checkSelfPermission(parent, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(parent, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    openCamera(view);
                } else {
                    Toast.makeText(getActivity(), "No Camera or Storage Permissions granted.\n\"An App is nothing without its permissions\"", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_save:
                if (txt_password.getText().length() > 3 && txt_email.getText().length() > 0)
                    user.updateUser(parent);
                else
                    Toast.makeText(parent, "Password should be more than 4 chars long, valid email", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * creates a new camera intent
     *
     * @param v
     */
    protected void openCamera(View v) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        createDir();
        Long currentImageTimeStamp = System.currentTimeMillis();
        String timeStamp = Long.toString(currentImageTimeStamp);
        File photo = new File(Utils.PHOTOS_DIR_PATH, timeStamp + ".jpg");
        currentImgUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImgUri);
        Log.d(TAG, "openCamera: Image URI is: " + currentImgUri);
        startActivityForResult(intent, 1);
    }

    /**
     * Creates a folder to hold the photos
     */
    protected void createDir() {
        File f = new File(Utils.PHOTOS_DIR_PATH);
        f.mkdirs();
    }

    /**
     * upon receiving response from the camera
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, requestCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (currentImgUri != null) {
                saveImage();
                Log.d(TAG, "onActivityResult: Image Capured!! - Now Upload That Shit!!");
            } else { //capturing failed
                Toast.makeText(getActivity(), "Failed to Get Photo, Try Again", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onActivityResult: Image URI returned as NULL - Orientation Fail");
            }
        } else {
            Log.i(TAG, "onActivityResult: User Canceled Image taking");

        }
    }

    /**
     * Saves the image
     */
    private void saveImage() {
        Utils.showLoadingDialog(parent, "Saving Image...", "This Can Take a while"); // Loader
        Bitmap b = BitmapFactory.decodeFile(currentImgUri.getPath()); // Original Image
        Bitmap out;

        // Crop image
        if (b.getWidth() >= b.getHeight()) {

            out = Bitmap.createBitmap(
                    b,
                    b.getWidth() / 2 - b.getHeight() / 2,
                    0,
                    b.getHeight(),
                    b.getHeight()
            );
        } else {
            out = Bitmap.createBitmap(
                    b,
                    0,
                    b.getHeight() / 2 - b.getWidth() / 2,
                    b.getWidth(),
                    b.getWidth()
            );
        }
        // Resizing image
        out = Bitmap.createScaledBitmap(out, 320, 320, false);

        File file = new File(currentImgUri.getPath());
        FileOutputStream fOut;

        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {}

        JSONObject payload = new JSONObject();

        // Upload image
        try {
            payload.put("image", ImageToBase64(file.getAbsolutePath()));
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.dismissLoadingDialog();
            Toast.makeText(parent, "Failed to upload image.. Try Again", Toast.LENGTH_LONG).show();
        }
        VolleyUtilSingleton.getInstance(parent).post(Utils.BASE_URL + Utils.UPLOAD_IMAGE_PATH, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: " + response.toString());
                try {
                    user.setAvatar(response.getString("image_url"));
                    //Populate The data
                    Utils.URLtoImageView(avatarImage, user.getAvatar());
                    user.updateUser(parent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.dismissLoadingDialog();
            }
        }, Utils.genericErrorListener);
    }

    /**
     * Converts the file to base 64 bit
     * @param filePath
     * @return
     */
    private String ImageToBase64(String filePath) {
        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        Log.d(TAG, "ImageToBase64: " + b.length / 1000);
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        user.setEmail(txt_email.getText().toString());
        user.setPassword(txt_password.getText().toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
