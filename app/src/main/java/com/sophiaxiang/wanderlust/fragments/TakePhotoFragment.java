package com.sophiaxiang.wanderlust.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sophiaxiang.wanderlust.R;
import com.sophiaxiang.wanderlust.databinding.FragmentTakePhotoBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class TakePhotoFragment extends Fragment {
    public static final String TAG = "ComposeProfilePicFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int PICK_PHOTO_CODE = 1046;
    private FragmentTakePhotoBinding mBinding;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private StorageReference mPhotoLocationRef;
    private File mPhotoFile;
    public String mPhotoFileName = "photo.jpg";
    private String mCurrentUserId;
    private String mImageNumber;

    public TakePhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_take_photo, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentUserId = getArguments().getString("current user id");
        mImageNumber = getArguments().getString("image number");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mPhotoLocationRef = mStorageRef.child(mCurrentUserId).child(mImageNumber + ".jpg");

        setUpButtons();
    }

    private void setUpButtons() {
        mBinding.btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        mBinding.btnImportPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });

        // if button is clicked, attempt to save image
        mBinding.btnSubmitPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.ivCameraPhoto.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveImage();
            }
        });
    }

    // launch camera application with implicit intent
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri(mPhotoFileName);
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void launchGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }


    // after image is taken/selected, load Bitmap into the image view in Main Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // take photo activity result
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                // Load the taken image into a preview
                mBinding.ivCameraPhoto.setImageBitmap(getSquareCropBitmap(takenImage));
                mBinding.btnSubmitPhoto.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        // import photo from gallery activity result
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            // Load the selected image into a preview
            mBinding.ivCameraPhoto.setImageBitmap(getSquareCropBitmap(selectedImage));
            mBinding.btnSubmitPhoto.setVisibility(View.VISIBLE);
        }
    }

    // for photo from camera roll
    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // save image to Firebase Storage
    private void saveImage(){
        // Get the data from an ImageView as bytes
        Bitmap bitmap = ((BitmapDrawable) mBinding.ivCameraPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mPhotoLocationRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Unable to save image", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Image upload failure: ", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(getContext(), "Success saving image!", Toast.LENGTH_SHORT).show();
                downloadImageUri();
            }
        });
    }

    // after saving image to Firebase Storage, download image Uri and save to Realtime Database
    private void downloadImageUri() {
        mPhotoLocationRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                mDatabase.child("users").child(mCurrentUserId).child(mImageNumber).setValue(uri.toString());
                Log.i(TAG, "successfully downloaded image Uri: " + uri.toString());
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "failure to download image Uri: ", exception);
            }
        });
    }

    public Bitmap getSquareCropBitmap(Bitmap bitmap) {
        int dimension = Math.min(bitmap.getWidth(), bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
        return bitmap;
    }
}