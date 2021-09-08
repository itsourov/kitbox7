package net.sourov.kitbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.NestedScrollingChild;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

public class PageGenerator extends AppCompatActivity {

    Sourov sourov = new Sourov();
    String siteName,siteUrl,siteEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_generator);

        Toolbar toolbar = findViewById(R.id.toolbarOnPageGenerator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        EditText siteNameInputOnPageGenerator = findViewById(R.id.siteNameInputOnPageGenerator);
        EditText siteUrlInputOnPageGenerator = findViewById(R.id.siteUrlInputOnPageGenerator);
        EditText adminEmailInputOnPageGenerator = findViewById(R.id.adminEmailInputOnPageGenerator);
        EditText myCodeOnPageGenerator = findViewById(R.id.myCodeOnPageGenerator);

        TextView pageTitle = findViewById(R.id.pageTitleOnPageGenerator);
        TextView pointTxtOnPageGenerator = findViewById(R.id.pointTxtOnPageGenerator);

        sourov.getCurrentPoints(value -> pointTxtOnPageGenerator.setText("points: " + value));
        findViewById(R.id.pointContainerOnPageGenerator).setOnClickListener(view -> sourov.showPopup(PageGenerator.this));




        String intention = getIntent().getStringExtra("intention");
        switch (intention) {
            case "ppg":
                getSupportActionBar().setTitle("Privacy Policy Generator");
                pageTitle.setText("Privacy Policy Generator");
                break;
            case "tac":
                getSupportActionBar().setTitle("Terms and Conditions Generator");
                pageTitle.setText("Terms and Conditions Generator");
                break;
            case "disclaimer":
                getSupportActionBar().setTitle("Disclaimer Generator");
                pageTitle.setText("Disclaimer Generator");
                break;
            case "dmca":
                getSupportActionBar().setTitle("DMCA Generator");
                pageTitle.setText("DMCA Generator");
                break;
        }

        findViewById(R.id.generateCodeBtnOnPageGenerator).setOnClickListener(view -> {

            siteName = siteNameInputOnPageGenerator.getText().toString();
            siteUrl = siteUrlInputOnPageGenerator.getText().toString();
            siteEmail = adminEmailInputOnPageGenerator.getText().toString();

            if (siteName.isEmpty()){
                siteNameInputOnPageGenerator.setError("This Cant Be Empty");
            }else if (siteUrl.isEmpty()){
                siteUrlInputOnPageGenerator.setError("This Cant Be Empty");
            }else if (siteEmail.isEmpty()){
                adminEmailInputOnPageGenerator.setError("This Cant Be Empty");
            }else {

                sourov.changePoints(10,"biyog");
                sourov.showSpinner(PageGenerator.this);
                if (intention.equals("ppg")){

                    new Handler().postDelayed(() -> {
                        myCodeOnPageGenerator.setText(sourov.getPrivacyPolicy(siteName,siteUrl,siteEmail));
                    }, 1000);

                }else if (intention.equals("tac")){
                    new Handler().postDelayed(() -> {
                        myCodeOnPageGenerator.setText(sourov.getTermsAndConditions(siteName,siteUrl,siteEmail));
                    }, 1000);

                }else if (intention.equals("disclaimer")){
                    new Handler().postDelayed(() -> {
                        myCodeOnPageGenerator.setText(sourov.getDisclaimer(siteName,siteUrl,siteEmail));
                    }, 1000);
                }else if (intention.equals("dmca")){
                    new Handler().postDelayed(() -> {
                        myCodeOnPageGenerator.setText(sourov.getDMCA(siteName,siteUrl,siteEmail));
                    }, 1000);
                }
            }


        });


        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        findViewById(R.id.copyCodeBtnOnPageGenerator).setOnClickListener(view -> {
            ClipData clip = ClipData.newPlainText("mycode", myCodeOnPageGenerator.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            myCodeOnPageGenerator.requestFocus();
        });




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}