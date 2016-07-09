package com.android_app.matan.ara.sagi.thesocialnotework;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class SettingsFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private static final String TAG = "[TSN/Settings]" ;
    private MainActivity parent;
    private ImageButton cameraBtn;
    private ImageView avatarImage;
    private EditText txt_username, txt_password, txt_email;
    private Uri currentImgUri;
    private TextView lbl_num_of_notes, lbl_num_of_liked;
    private User user;


    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        this.parent = (MainActivity)getActivity();
        Utils.showLoadingDialog(parent, "Just a sec...", "");
        this.user = parent.getUser();
        this.cameraBtn = (ImageButton) view.findViewById(R.id.btn_camera);
        this.cameraBtn.setOnClickListener(this);
        this.avatarImage = (ImageView) view.findViewById(R.id.settings_userAvater_iamgeView);
        this.txt_email = (EditText)view.findViewById(R.id.txt_email);
        this.txt_email.addTextChangedListener(this);
        this.txt_password = (EditText)view.findViewById(R.id.txt_password);
      this.txt_password.addTextChangedListener(this);
        this.txt_username = (EditText)view.findViewById(R.id.txt_username);
        this.lbl_num_of_notes = (TextView)view.findViewById(R.id.lbl_num_of_notes);
        this.lbl_num_of_liked = (TextView)view.findViewById(R.id.lbl_num_of_liked);

        this.txt_username.setEnabled(false);

        //Populate The data
        Utils.URLtoImageView(avatarImage, user.getAvatar());
        this.txt_username.setText(user.getUsername());
        this.txt_password.setText(user.getPassword());
        this.txt_email.setText(user.getEmail());

//        this.lbl_num_of_notes.setText(user.getNumber_of_notes()); //TODO
//      this.lbl_num_of_notes.setText(user.getLiked_notes().size());


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


  @Override
  public void onClick(View view) {
    switch(view.getId()){
      case R.id.btn_camera:
        //check for permission
        ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.CAMERA}, 1);
        openCamera(view);
        break;
    }
  }

  /**
   * creates a new camera intent
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

  protected void createDir() {
    File f = new File(Utils.PHOTOS_DIR_PATH);
    f.mkdirs();
  }

  @Override
  public void onActivityResult(int requestCode, int resaultCode, Intent intent) {
    super.onActivityResult(requestCode, requestCode, intent);
    if (resaultCode == Activity.RESULT_OK) {
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

  private void saveImage() {
    Utils.showLoadingDialog(parent, "Saving Image...", "This Can Take a while");
    File myFile = new File(currentImgUri.getPath());

    JSONObject payload = new JSONObject();
    try {
      payload.put("image", ImageToBase64(myFile.getAbsolutePath()));
    } catch (JSONException e) {
      e.printStackTrace();
      Utils.dismissLoadingDialog();
      Toast.makeText(parent, "Failed to upload image.. Try Again", Toast.LENGTH_LONG).show();
    }
    VolleyUtilSingleton.getInstance(parent).post(Utils.BASE_URL + Utils.UPLOAD_IMAGE_PATH, payload, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Log.d(TAG, "onResponse: "+response.toString());
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

  private String ImageToBase64(String filePath){
    Bitmap bm = BitmapFactory.decodeFile(filePath);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
    byte[] b = baos.toByteArray();
    Log.d(TAG, "ImageToBase64: "+b.length/1000);
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
