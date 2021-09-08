package net.sourov.kitbox;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                Glide.with(ThumbDownloader.this).load(thumbUrl1).placeholder(R.drawable.ic_launcher_background).into(thumbImage1);
                Glide.with(ThumbDownloader.this).load(thumbUrl2).placeholder(R.drawable.ic_launcher_background).into(thumbImage2);
                Glide.with(ThumbDownloader.this).load(thumbUrl3).placeholder(R.drawable.ic_launcher_background).into(thumbImage3);

            }
        });
    }


}