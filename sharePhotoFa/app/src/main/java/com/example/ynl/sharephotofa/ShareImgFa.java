package com.example.ynl.sharephotofa;

        import android.annotation.TargetApi;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentSender;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.Signature;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.os.Build;
        import android.provider.MediaStore;
        import android.support.annotation.RequiresApi;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Base64;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.Toast;

        import com.facebook.CallbackManager;
        import com.facebook.FacebookCallback;
        import com.facebook.FacebookException;
        import com.facebook.FacebookSdk;
        import com.facebook.share.Sharer;
        import com.facebook.share.model.ShareLinkContent;
        import com.facebook.share.model.SharePhoto;
        import com.facebook.share.model.SharePhotoContent;
        import com.facebook.share.model.ShareVideo;
        import com.facebook.share.model.ShareVideoContent;
        import com.facebook.share.widget.ShareDialog;
        import com.squareup.picasso.Picasso;
        import com.squareup.picasso.Target;

        import java.lang.annotation.Annotation;
        import java.lang.annotation.ElementType;

        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;

public class ShareImgFa extends AppCompatActivity {

    private Button m_upload_btn;
    //private StorageReference m_storage;
    private static  final int GALLERY_INTENT = 1;
    public static final String TAG = "MESSAGE";
    public String path_image;

    private static final int REQUEST_VIDEO_CODE = 1000;


    //https://i.ytimg.com/vi/anqwEsZatSU/maxresdefault.jpg

    Button btnShareLink,btnSharePhoto,btnShareVideo;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();

                shareDialog.show(content);
            }
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.upload_photo);

        //printKeyHash();
        showImage();

        //Init View
        //btnShareLink = (Button)findViewById(R.id.btnShareLink);
        btnSharePhoto = (Button)findViewById(R.id.btnSharePhoto);
        btnShareVideo = (Button)findViewById(R.id.btnShareVideo);

        //Init FB
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        btnSharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SHARE PHOTO",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><");
                //Create callback

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(ShareImgFa.this, "Share successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(ShareImgFa.this, "Share cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(ShareImgFa.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                //We will fetch photo from link and convert to bitmap
                Picasso.with(getBaseContext()).load("https://i.ytimg.com/vi/anqwEsZatSU/maxresdefault.jpg").into(target);
            }
        });



        btnShareVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //choose Video dialog
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_VIDEO_CODE);

            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode==REQUEST_VIDEO_CODE){

                Uri selectedVideo = data.getData();

                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(selectedVideo)
                        .build();

                ShareVideoContent videoContent = new ShareVideoContent.Builder()
                        .setContentTitle("This is useful video")
                        .setContentDescription("Funny video for moviles cs-unsa")
                        .setVideo(video)
                        .build();

                if(shareDialog.canShow(ShareVideoContent.class))
                    shareDialog.show(videoContent);
            }


        }
    }


    private void printKeyHash() {
        Log.d("KeyHash",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><");
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.ynl.sharephotofa", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }


    public void shareLink(View v){
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote("This is useful link")
                .setContentUrl(Uri.parse("https://youtube.com"))
                .build();

        if(ShareDialog.canShow(ShareLinkContent.class))
        {
            shareDialog.show(linkContent);
        }
    }

    public void showImage(){
        Intent intent = getIntent();
        String imgpath = intent.getStringExtra(MainActivity.IMGPATH);
        path_image = MainActivity.IMGPATH;
        //Log.e("SHOWIMG::",imgpath);
        ImageView imageView = (ImageView) findViewById(R.id.imgChoosed);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgpath));


    }

}