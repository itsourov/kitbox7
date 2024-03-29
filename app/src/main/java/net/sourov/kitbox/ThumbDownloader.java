package net.sourov.kitbox;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThumbDownloader extends AppCompatActivity {

    Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb_downloader);

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarOnYTD);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Youtube Thumbnail Downloader");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        EditText urlInputOnTD = findViewById(R.id.urlInputOnTD);

        ImageView thumbImage1 = findViewById(R.id.thumbImage1);
        ImageView thumbImage2 = findViewById(R.id.thumbImage2);
        ImageView thumbImage3 = findViewById(R.id.thumbImage3);

        findViewById(R.id.buttonOnTD).setOnClickListener(view -> {
            String youtubeUrl = urlInputOnTD.getText().toString().trim();
            final String regex = "http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\\n]+)";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.find()) {

                String thumbUrl1 = "https://img.youtube.com/vi/" + matcher.group(1) + "/maxresdefault.jpg";
                String thumbUrl2 = "https://img.youtube.com/vi/" + matcher.group(1) + "/sddefault.jpg";
                String thumbUrl3 = "https://img.youtube.com/vi/" + matcher.group(1) + "/hqdefault.jpg";
                Glide.with(ThumbDownloader.this).load(thumbUrl1).placeholder(R.drawable.loading).error(R.drawable.image_not_found).into(thumbImage1);
                Glide.with(ThumbDownloader.this).load(thumbUrl2).placeholder(R.drawable.loading).error(R.drawable.image_not_found).into(thumbImage2);
                Glide.with(ThumbDownloader.this).load(thumbUrl3).placeholder(R.drawable.loading).error(R.drawable.image_not_found).into(thumbImage3);



                findViewById(R.id.downloadThumb1).setOnClickListener(view1 -> downloadFile(thumbUrl1));
                findViewById(R.id.downloadThumb2).setOnClickListener(view1 -> downloadFile(thumbUrl2));
                findViewById(R.id.downloadThumb3).setOnClickListener(view1 -> downloadFile(thumbUrl3));
            }
        });
    }
    private void downloadFile(String fileUrl){


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        String title = URLUtil.guessFileName(fileUrl, null, null);
        request.setDescription("Downloading file. please wait...");
        String cookie = CookieManager.getInstance().getCookie(fileUrl);
        request.addRequestHeader("cookie", cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title );

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        //set BroadcastReceiver
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                myToast.setText("Ding!!!");
                myToast.show();
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}