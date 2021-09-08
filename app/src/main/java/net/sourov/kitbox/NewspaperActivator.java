package net.sourov.kitbox;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NewspaperActivator extends AppCompatActivity {
    long pointInt = 69;
    TextView pointTxtOnNewspaper;
    EditText purchaseCodeOnNA, activationKeyOnNA, serverIdOnNA;
 Sourov sourov = new Sourov();
    LinearLayout containerOnNA;
    AlertDialog progressDialog;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspaper_activator);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbarOnNewspaper);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.newspaper_theme_activator);

        initializeProgressbar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        pointTxtOnNewspaper = findViewById(R.id.pointTxtOnNewspaper);
        purchaseCodeOnNA = findViewById(R.id.purchaseCodeOnNA);
        activationKeyOnNA = findViewById(R.id.activationKeyOnNA);
        containerOnNA = findViewById(R.id.containerOnNA);

        serverIdOnNA = findViewById(R.id.serverIdOnNA);
        getCurrentPoints();

        findViewById(R.id.generateButtonOnNA).setOnClickListener(view -> {
            String serverID = serverIdOnNA.getText().toString().trim();
            if (serverID.length() == 32) {
                if (pointInt == 69) {
                    Toast.makeText(getApplicationContext(), "Please check internet", Toast.LENGTH_SHORT).show();
                    pointTxtOnNewspaper.setText("Points: 69-420");
                } else if (pointInt < 20) {
                    Toast.makeText(getApplicationContext(), "You have only " + pointInt + " points. this is not enough", Toast.LENGTH_SHORT).show();
                } else {
                    doStuff(serverID);
                }
            } else {
                serverIdOnNA.setError(serverID.length() + " অক্ষর হলে হবে না, ৩২ অক্ষর হতে হবে ");
                containerOnNA.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.copyBtn1OnNA).setOnClickListener(view -> {
            ClipData clip = ClipData.newPlainText("purchaseCode", purchaseCodeOnNA.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            purchaseCodeOnNA.requestFocus();
        });
        findViewById(R.id.copyBtn2OnNA).setOnClickListener(view -> {
            ClipData clip = ClipData.newPlainText("purchaseCode", activationKeyOnNA.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            activationKeyOnNA.requestFocus();

        });

        findViewById(R.id.shareImgOnNA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textToShare = "YOUR SERVER ID: " + serverIdOnNA.getText().toString().trim() +
                        "\nENVATO PURCHASE CODE: " + purchaseCodeOnNA.getText().toString().trim() +
                        "\nTAGDIV ACTIVATION KEY: " + activationKeyOnNA.getText().toString().trim();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The title");
                startActivity(Intent.createChooser(shareIntent, "Share..."));

            }
        });

        findViewById(R.id.pointContainerOnNewspaper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourov.showPopup(NewspaperActivator.this);
            }
        });

    }

    private void initializeProgressbar() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View progressView = layoutInflater.inflate(R.layout.progress_bar, null);
        progressDialog = new AlertDialog.Builder(this).create();
        progressDialog.setView(progressView);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
    }


    @SuppressLint("SetTextI18n")
    private void getCurrentPoints() {
        sourov.getCurrentPoints(value -> {
            pointInt = value;
            pointTxtOnNewspaper.setText("Points: " + value);
        });


    }

    private void doStuff(String serverID) {
        progressDialog.show();
        new Handler().postDelayed(() -> {
            containerOnNA.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            DatabaseReference purchaseCodeRef = FirebaseDatabase.getInstance().getReference().child("administrator").child("purchaseCodeRef");
            purchaseCodeRef.keepSynced(true);
            purchaseCodeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String purchaseCode = (String) snapshot.getValue();
                    purchaseCodeOnNA.setText(purchaseCode);
                    if (purchaseCode != null) {
                        String ad = serverID + purchaseCode;
                        activationKeyOnNA.setText(md5(ad));
                        sourov.changePoints(20, "biyog");
                    } else {
                        purchaseCodeOnNA.setText("please call +8801872934185");
                        activationKeyOnNA.setText("there was a error in the app database or you don't have internet");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }, 4000);

    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}