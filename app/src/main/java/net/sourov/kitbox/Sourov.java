package net.sourov.kitbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.Status;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Objects;

public class Sourov {

    public Sourov() {

    }

    TextView fileSizeTxtOnDownload, downloadedFileSizeOnDownload, percentOnDownloading;
    ProgressBar progressBarOnDownload;
    Button pauseBtnOnDownload;
    FirebaseAuth firebaseAuth;


    //here from the ads system variable
    private RewardedAd mRewardedAd;
    private final String TAG = "MainActivity";
    Boolean earned = false;
    int rewardAmount;
    AlertDialog progressDialogForAds;
    View pointDialogView;



    public void checkUpdate(Context context) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("administrator");


        reference.child("latest_version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long latestVersion = (long) snapshot.getValue();
                long currentVersion = BuildConfig.VERSION_CODE;
                if (currentVersion < latestVersion) {
                    showDialog(context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Update available");
        builder1.setMessage("install the latest version to enjoy our app");
        builder1.setCancelable(false);


        builder1.setPositiveButton("Yes", (dialog, id) -> getTheDownloadLink(context));

        builder1.setNegativeButton("No", (dialog, id) -> System.exit(0));

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void getTheDownloadLink(Context context) {
        Activity activity = (Activity) context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", activity.getPackageName()))), 1234);
            } else {
            }
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("administrator");
        reference.child("latest_version_link").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = (String) snapshot.getValue();
                Toast.makeText(context, "" + link, Toast.LENGTH_SHORT).show();
                StartDownloading(context, "https://d-08.winudf.com/custom/com.apkpure.aegon-3172501.apk?_fn=QVBLUHVyZV92My4xNy4yNV9hcGtwdXJlLmNvbS5hcGs&_p=Y29tLmFwa3B1cmUuYWVnb24&am=iB6H2_nRH47raDER5kS9SA&arg=apkpure%3A%2F%2Fcampaign%2F%3Futm_source%3Dapkpure%26utm_medium%3Dhome-m&at=1630429018&k=3fb42c564c9b1606d9dacddbee0c4603612fb0db&uu=http%3A%2F%2F172.16.62.1%2Fcustom%2Fcom.apkpure.aegon-3172501.apk%3Fk%3De0f19a65fc9d7f3e811c3a1517e2340d612fb0db");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void StartDownloading(Context context, String s) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View downloadingView = layoutInflater.inflate(R.layout.downloading, null);

        fileSizeTxtOnDownload = downloadingView.findViewById(R.id.fileSizeTxtOnDownload);
        downloadedFileSizeOnDownload = downloadingView.findViewById(R.id.downloadedFileSizeOnDownload);
        percentOnDownloading = downloadingView.findViewById(R.id.percentOnDownloading);
        progressBarOnDownload = downloadingView.findViewById(R.id.progressBarOnDownload);
        pauseBtnOnDownload = downloadingView.findViewById(R.id.pauseBtnOnDownload);

        progressBarOnDownload.setMax(100);
        final AlertDialog pointDialog = new AlertDialog.Builder(context).create();
        pointDialog.setCancelable(false);
        pointDialog.setView(downloadingView);
        pointDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pointDialog.show();


// Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(context, config);

        String PATH = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";

        int downloadId = PRDownloader.download(s, PATH, "kitbox.apk")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        pauseBtnOnDownload.setText("Pause");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        pauseBtnOnDownload.setText("Resume");
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long totalBytes = progress.totalBytes;
                        long currentBytes = progress.currentBytes;
                        int percent = (int) ((currentBytes * 100) / totalBytes);

                        progressBarOnDownload.setProgress(percent);

                        fileSizeTxtOnDownload.setText("File Size: " + byteToMb(totalBytes));
                        downloadedFileSizeOnDownload.setText("Downloaded: " + byteToMb(currentBytes));
                        percentOnDownloading.setText((percent + "/100"));
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        installApp(context);
                    }

                    @Override
                    public void onError(Error error) {

                    }


                });


        pauseBtnOnDownload.setOnClickListener(view -> {
            Status status = PRDownloader.getStatus(downloadId);
            if (status.equals(Status.PAUSED)) {
                PRDownloader.resume(downloadId);
            } else {
                PRDownloader.pause(downloadId);

            }

        });



    }

    private void installApp(Context context) {
        String PATH = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "kitbox.apk";
        File file = new File(PATH);
        if (file.exists()) {
            // Get length of file in bytes
            long fileSizeInBytes = file.length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;

            if (fileSizeInMB > 1) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uriFromFile(context, new File(PATH)), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in opening the file!", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Error in opening the file!");
                }
            } else {
                Toast.makeText(context, "File wasnt downloaded successfully", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, "File dont exists", Toast.LENGTH_LONG).show();
        }
    }


    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }

    }

    public static String byteToMb (long size){
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public void getCurrentPoints(MyCallback myCallback) {
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        databaseReference.keepSynced(true);
        databaseReference.child("points").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    long   pointInt = (long) snapshot.getValue();
                    myCallback.onCallback(pointInt);
                } catch (Exception e) {
                    e.printStackTrace();
                    databaseReference.child("points").setValue(50);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface MyCallback {
        void onCallback(long value);
    }

    public void changePoints(long pointsTOChange,String jog_na_biyog){
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        databaseReference.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentPt = (long) snapshot.getValue();
                if (jog_na_biyog.equals("jog")){
                    databaseReference.child("points").setValue(currentPt+pointsTOChange);
                }if (jog_na_biyog.equals("biyog")){
                    databaseReference.child("points").setValue(currentPt-pointsTOChange);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }




    public void showPopup(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
         pointDialogView = layoutInflater.inflate(R.layout.point_popup, null);
        TextView PointTextOnPopUp = pointDialogView.findViewById(R.id.PointTextOnPopUp);
        getCurrentPoints(value -> PointTextOnPopUp.setText("points: " + value));

        pointDialogView.findViewById(R.id.getMoreCoin).setOnClickListener(view -> loadAds(context));
        final AlertDialog pointDialog = new AlertDialog.Builder(context).create();
        pointDialog.setView(pointDialogView);
        pointDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pointDialog.show();

    }

    private void loadAds(Context context) {
        showSpinner(context);
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Toast.makeText(context, "Ads failed to load", Toast.LENGTH_SHORT).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        showAds(context);
                    }
                });
    }

    private void showAds(Context context) {

        if (mRewardedAd != null) {
            Activity activityContext = (Activity) context;
            mRewardedAd.show(activityContext, rewardItem -> {
                Log.d(TAG, "The user earned the reward.");
                rewardAmount = rewardItem.getAmount();
                changePoints(rewardAmount, "jog");
                earned = true;
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad was shown.");
                progressDialogForAds.dismiss();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.d(TAG, "Ad failed to show.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.");
                mRewardedAd = null;
                if (earned) {
                    updateUI(rewardAmount);
                } else {
                    updateUI(0);
                }
                earned = false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(int rewardAmount) {
        TextView congratulationTxt = pointDialogView.findViewById(R.id.congratulationOnPointsPop);
        if (rewardAmount == 0) {
            congratulationTxt.setVisibility(View.GONE);
        } else {
            congratulationTxt.setVisibility(View.VISIBLE);
        }
        TextView yourBalanceTxt = pointDialogView.findViewById(R.id.yourbalanceOnPointsPop);
        yourBalanceTxt.setText("You just earned " + rewardAmount + " points. your balance: ");
    }

    public void showSpinner(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View progressView = layoutInflater.inflate(R.layout.progress_bar, null);
        progressDialogForAds = new AlertDialog.Builder(context).create();
        progressDialogForAds.setView(progressView);
        progressDialogForAds.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialogForAds.show();

        new Handler().postDelayed(() -> {

            progressDialogForAds.dismiss();
        }, 1000);

    }

    public String getPrivacyPolicy(String siteName, String siteUrl, String siteEmail) {



        String templatePP = "<h1>Privacy Policy for spaname</h1>\n" +
                "\n" +
                "  <p>At spaname, accessible from spaurl, one of our main priorities is the privacy of our visitors. This Privacy Policy document contains types of information that is collected and recorded by spaname and how we use it.</p>\n" +
                "\n" +
                "  <p>If you have additional questions or require more information about our Privacy Policy, do not hesitate to contact us.</p>\n" +
                "\n" +
                "  <p>This Privacy Policy applies only to our online activities and is valid for visitors to our website with regards to the information that they shared and/or collect in spaname. This policy is not applicable to any information collected offline or via channels other than this website.\n" +
                "\n" +
                "  <h2>Consent</h2>\n" +
                "\n" +
                "  <p>By using our website, you hereby consent to our Privacy Policy and agree to its terms.</p>\n" +
                "\n" +
                "  <h2>Information we collect</h2>\n" +
                "\n" +
                "  <p>The personal information that you are asked to provide, and the reasons why you are asked to provide it, will be made clear to you at the point we ask you to provide your personal information.</p>\n" +
                "  <p>If you contact us directly, we may receive additional information about you such as your name, email address, phone number, the contents of the message and/or attachments you may send us, and any other information you may choose to provide.</p>\n" +
                "  <p>When you register for an Account, we may ask for your contact information, including items such as name, company name, address, email address, and telephone number.</p>\n" +
                "\n" +
                "  <h2>How we use your information</h2>\n" +
                "\n" +
                "  <p>We use the information we collect in various ways, including to:</p>\n" +
                "\n" +
                "  <ul>\n" +
                "  <li>Provide, operate, and maintain our webste</li>\n" +
                "  <li>Improve, personalize, and expand our webste</li>\n" +
                "  <li>Understand and analyze how you use our webste</li>\n" +
                "  <li>Develop new products, services, features, and functionality</li>\n" +
                "  <li>Communicate with you, either directly or through one of our partners, including for customer service, to provide you with updates and other information relating to the webste, and for marketing and promotional purposes</li>\n" +
                "  <li>Send you emails</li>\n" +
                "  <li>Find and prevent fraud</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <h2>Log Files</h2>\n" +
                "\n" +
                "  <p>spaname follows a standard procedure of using log files. These files log visitors when they visit websites. All hosting companies do this and a part of hosting services' analytics. The information collected by log files include internet protocol (IP) addresses, browser type, Internet Service Provider (ISP), date and time stamp, referring/exit pages, and possibly the number of clicks. These are not linked to any information that is personally identifiable. The purpose of the information is for analyzing trends, administering the site, tracking users' movement on the website, and gathering demographic information.</p>\n" +
                "\n" +
                "  <h2>Cookies and Web Beacons</h2>\n" +
                "\n" +
                "  <p>Like any other website, spaname uses 'cookies'. These cookies are used to store information including visitors' preferences, and the pages on the website that the visitor accessed or visited. The information is used to optimize the users' experience by customizing our web page content based on visitors' browser type and/or other information.</p>\n" +
                "\n" +
                "  <p>For more general information on cookies.</p>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <h2>Advertising Partners Privacy Policies</h2>\n" +
                "\n" +
                "  <P>You may consult this list to find the Privacy Policy for each of the advertising partners of spaname.</p>\n" +
                "\n" +
                "  <p>Third-party ad servers or ad networks uses technologies like cookies, JavaScript, or Web Beacons that are used in their respective advertisements and links that appear on spaname, which are sent directly to users' browser. They automatically receive your IP address when this occurs. These technologies are used to measure the effectiveness of their advertising campaigns and/or to personalize the advertising content that you see on websites that you visit.</p>\n" +
                "\n" +
                "  <p>Note that spaname has no access to or control over these cookies that are used by third-party advertisers.</p>\n" +
                "\n" +
                "  <h2>Third Party Privacy Policies</h2>\n" +
                "\n" +
                "  <p>spaname's Privacy Policy does not apply to other advertisers or websites. Thus, we are advising you to consult the respective Privacy Policies of these third-party ad servers for more detailed information. It may include their practices and instructions about how to opt-out of certain options. </p>\n" +
                "\n" +
                "  <p>You can choose to disable cookies through your individual browser options. To know more detailed information about cookie management with specific web browsers, it can be found at the browsers' respective websites.</p>\n" +
                "\n" +
                "  <h2>CCPA Privacy Rights (Do Not Sell My Personal Information)</h2>\n" +
                "\n" +
                "  <p>Under the CCPA, among other rights, California consumers have the right to:</p>\n" +
                "  <p>Request that a business that collects a consumer's personal data disclose the categories and specific pieces of personal data that a business has collected about consumers.</p>\n" +
                "  <p>Request that a business delete any personal data about the consumer that a business has collected.</p>\n" +
                "  <p>Request that a business that sells a consumer's personal data, not sell the consumer's personal data.</p>\n" +
                "  <p>If you make a request, we have one month to respond to you. If you would like to exercise any of these rights, please contact us.</p>\n" +
                "\n" +
                "  <h2>GDPR Data Protection Rights</h2>\n" +
                "\n" +
                "  <p>We would like to make sure you are fully aware of all of your data protection rights. Every user is entitled to the following:</p>\n" +
                "  <p>The right to access – You have the right to request copies of your personal data. We may charge you a small fee for this service.</p>\n" +
                "  <p>The right to rectification – You have the right to request that we correct any information you believe is inaccurate. You also have the right to request that we complete the information you believe is incomplete.</p>\n" +
                "  <p>The right to erasure – You have the right to request that we erase your personal data, under certain conditions.</p>\n" +
                "  <p>The right to restrict processing – You have the right to request that we restrict the processing of your personal data, under certain conditions.</p>\n" +
                "  <p>The right to object to processing – You have the right to object to our processing of your personal data, under certain conditions.</p>\n" +
                "  <p>The right to data portability – You have the right to request that we transfer the data that we have collected to another organization, or directly to you, under certain conditions.</p>\n" +
                "  <p>If you make a request, we have one month to respond to you. If you would like to exercise any of these rights, please contact us.</p>\n" +
                "\n" +
                "  <h2>Children's Information</h2>\n" +
                "\n" +
                "  <p>Another part of our priority is adding protection for children while using the internet. We encourage parents and guardians to observe, participate in, and/or monitor and guide their online activity.</p>\n" +
                "\n" +
                "  <p>spaname does not knowingly collect any Personal Identifiable Information from children under the age of 13. If you think that your child provided this kind of information on our website, we strongly encourage you to contact us immediately and we will do our best efforts to promptly remove such information from our records.</p>\n";


        String newString = templatePP.replace("spaname", siteName).replace("spaurl",siteUrl).replace("spamail@spa.com",siteEmail);

      return newString;

    }


    public String getTermsAndConditions(String siteName,String siteUrl, String siteEmail){

        String templateTAC = "<h2><strong>Terms and Conditions</strong></h2>\n" +
                "\n" +
                "  <p>Welcome to spaname!</p>\n" +
                "\n" +
                "  <p>These terms and conditions outline the rules and regulations for the use of spaname's Website, located at spaurl.</p>\n" +
                "\n" +
                "  <p>By accessing this website we assume you accept these terms and conditions. Do not continue to use spaname if you do not agree to take all of the terms and conditions stated on this page.</p>\n" +
                "\n" +
                "  <p>The following terminology applies to these Terms and Conditions, Privacy Statement and Disclaimer Notice and all Agreements: 'Client', 'You' and 'Your' refers to you, the person log on this website and compliant to the Company’s terms and conditions. 'The Company', 'Ourselves', 'We', 'Our' and 'Us', refers to our Company. 'Party', 'Parties', or 'Us', refers to both the Client and ourselves. All terms refer to the offer, acceptance and consideration of payment necessary to undertake the process of our assistance to the Client in the most appropriate manner for the express purpose of meeting the Client’s needs in respect of provision of the Company’s stated services, in accordance with and subject to, prevailing law of Netherlands. Any use of the above terminology or other words in the singular, plural, capitalization and/or he/she or they, are taken as interchangeable and therefore as referring to same.</p>\n" +
                "\n" +
                "  <h3><strong>Cookies</strong></h3>\n" +
                "\n" +
                "  <p>We employ the use of cookies. By accessing spaname, you agreed to use cookies in agreement with the spaname's Privacy Policy. </p>\n" +
                "\n" +
                "  <p>Most interactive websites use cookies to let us retrieve the user’s details for each visit. Cookies are used by our website to enable the functionality of certain areas to make it easier for people visiting our website. Some of our affiliate/advertising partners may also use cookies.</p>\n" +
                "\n" +
                "  <h3><strong>License</strong></h3>\n" +
                "\n" +
                "  <p>Unless otherwise stated, spaname and/or its licensors own the intellectual property rights for all material on spaname. All intellectual property rights are reserved. You may access this from spaname for your own personal use subjected to restrictions set in these terms and conditions.</p>\n" +
                "\n" +
                "  <p>You must not:</p>\n" +
                "  <ul>\n" +
                "      <li>Republish material from spaname</li>\n" +
                "      <li>Sell, rent or sub-license material from spaname</li>\n" +
                "      <li>Reproduce, duplicate or copy material from spaname</li>\n" +
                "      <li>Redistribute content from spaname</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <p>This Agreement shall begin on the date hereof. </p>\n" +
                "\n" +
                "  <p>Parts of this website offer an opportunity for users to post and exchange opinions and information in certain areas of the website. spaname does not filter, edit, publish or review Comments prior to their presence on the website. Comments do not reflect the views and opinions of spaname,its agents and/or affiliates. Comments reflect the views and opinions of the person who post their views and opinions. To the extent permitted by applicable laws, spaname shall not be liable for the Comments or for any liability, damages or expenses caused and/or suffered as a result of any use of and/or posting of and/or appearance of the Comments on this website.</p>\n" +
                "\n" +
                "  <p>spaname reserves the right to monitor all Comments and to remove any Comments which can be considered inappropriate, offensive or causes breach of these Terms and Conditions.</p>\n" +
                "\n" +
                "  <p>You warrant and represent that:</p>\n" +
                "\n" +
                "  <ul>\n" +
                "      <li>You are entitled to post the Comments on our website and have all necessary licenses and consents to do so;</li>\n" +
                "      <li>The Comments do not invade any intellectual property right, including without limitation copyright, patent or trademark of any third party;</li>\n" +
                "      <li>The Comments do not contain any defamatory, libelous, offensive, indecent or otherwise unlawful material which is an invasion of privacy</li>\n" +
                "      <li>The Comments will not be used to solicit or promote business or custom or present commercial activities or unlawful activity.</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <p>You hereby grant spaname a non-exclusive license to use, reproduce, edit and authorize others to use, reproduce and edit any of your Comments in any and all forms, formats or media.</p>\n" +
                "\n" +
                "  <h3><strong>Hyperlinking to our Content</strong></h3>\n" +
                "\n" +
                "  <p>The following organizations may link to our Website without prior written approval:</p>\n" +
                "\n" +
                "  <ul>\n" +
                "      <li>Government agencies;</li>\n" +
                "      <li>Search engines;</li>\n" +
                "      <li>News organizations;</li>\n" +
                "      <li>Online directory distributors may link to our Website in the same manner as they hyperlink to the Websites of other listed businesses; and</li>\n" +
                "      <li>System wide Accredited Businesses except soliciting non-profit organizations, charity shopping malls, and charity fundraising groups which may not hyperlink to our Web site.</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <p>These organizations may link to our home page, to publications or to other Website information so long as the link: (a) is not in any way deceptive; (b) does not falsely imply sponsorship, endorsement or approval of the linking party and its products and/or services; and (c) fits within the context of the linking party’s site.</p>\n" +
                "\n" +
                "  <p>We may consider and approve other link requests from the following types of organizations:</p>\n" +
                "\n" +
                "  <ul>\n" +
                "      <li>commonly-known consumer and/or business information sources;</li>\n" +
                "      <li>dot.com community sites;</li>\n" +
                "      <li>associations or other groups representing charities;</li>\n" +
                "      <li>online directory distributors;</li>\n" +
                "      <li>internet portals;</li>\n" +
                "      <li>accounting, law and consulting firms; and</li>\n" +
                "      <li>educational institutions and trade associations.</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <p>We will approve link requests from these organizations if we decide that: (a) the link would not make us look unfavorably to ourselves or to our accredited businesses; (b) the organization does not have any negative records with us; (c) the benefit to us from the visibility of the hyperlink compensates the absence of spaname; and (d) the link is in the context of general resource information.</p>\n" +
                "\n" +
                "  <p>These organizations may link to our home page so long as the link: (a) is not in any way deceptive; (b) does not falsely imply sponsorship, endorsement or approval of the linking party and its products or services; and (c) fits within the context of the linking party’s site.</p>\n" +
                "\n" +
                "  <p>If you are one of the organizations listed in paragraph 2 above and are interested in linking to our website, you must inform us by sending an e-mail to spaname. Please include your name, your organization name, contact information as well as the URL of your site, a list of any URLs from which you intend to link to our Website, and a list of the URLs on our site to which you would like to link. Wait 2-3 weeks for a response.</p>\n" +
                "\n" +
                "  <p>Approved organizations may hyperlink to our Website as follows:</p>\n" +
                "\n" +
                "  <ul>\n" +
                "      <li>By use of our corporate name; or</li>\n" +
                "      <li>By use of the uniform resource locator being linked to; or</li>\n" +
                "      <li>By use of any other description of our Website being linked to that makes sense within the context and format of content on the linking party’s site.</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <p>No use of spaname's logo or other artwork will be allowed for linking absent a trademark license agreement.</p>\n" +
                "\n" +
                "  <h3><strong>iFrames</strong></h3>\n" +
                "\n" +
                "  <p>Without prior approval and written permission, you may not create frames around our Webpages that alter in any way the visual presentation or appearance of our Website.</p>\n" +
                "\n" +
                "  <h3><strong>Content Liability</strong></h3>\n" +
                "\n" +
                "  <p>We shall not be hold responsible for any content that appears on your Website. You agree to protect and defend us against all claims that is rising on your Website. No link(s) should appear on any Website that may be interpreted as libelous, obscene or criminal, or which infringes, otherwise violates, or advocates the infringement or other violation of, any third party rights.</p>\n" +
                "\n" +
                "  <h3><strong>Your Privacy</strong></h3>\n" +
                "\n" +
                "  <p>Please read Privacy Policy</p>\n" +
                "\n" +
                "  <h3><strong>Reservation of Rights</strong></h3>\n" +
                "\n" +
                "  <p>We reserve the right to request that you remove all links or any particular link to our Website. You approve to immediately remove all links to our Website upon request. We also reserve the right to amen these terms and conditions and it’s linking policy at any time. By continuously linking to our Website, you agree to be bound to and follow these linking terms and conditions.</p>\n" +
                "\n" +
                "  <h3><strong>Removal of links from our website</strong></h3>\n" +
                "\n" +
                "  <p>If you find any link on our Website that is offensive for any reason, you are free to contact and inform us any moment. We will consider requests to remove links but we are not obligated to or so or to respond to you directly.</p>\n" +
                "\n" +
                "  <p>We do not ensure that the information on this website is correct, we do not warrant its completeness or accuracy; nor do we promise to ensure that the website remains available or that the material on the website is kept up to date.</p>\n" +
                "\n" +
                "  <h3><strong>Disclaimer</strong></h3>\n" +
                "\n" +
                "  <p>To the maximum extent permitted by applicable law, we exclude all representations, warranties and conditions relating to our website and the use of this website. Nothing in this disclaimer will:</p>\n" +
                "\n" +
                "  <ul>\n" +
                "      <li>limit or exclude our or your liability for death or personal injury;</li>\n" +
                "      <li>limit or exclude our or your liability for fraud or fraudulent misrepresentation;</li>\n" +
                "      <li>limit any of our or your liabilities in any way that is not permitted under applicable law; or</li>\n" +
                "      <li>exclude any of our or your liabilities that may not be excluded under applicable law.</li>\n" +
                "  </ul>\n" +
                "\n" +
                "  <p>The limitations and prohibitions of liability set in this Section and elsewhere in this disclaimer: (a) are subject to the preceding paragraph; and (b) govern all liabilities arising under the disclaimer, including liabilities arising in contract, in tort and for breach of statutory duty.</p>\n" +
                "\n" +
                "  <p>As long as the website and the information and services on the website are provided free of charge, we will not be liable for any loss or damage of any nature.</p>\n";


        String newString = templateTAC.replace("spaname", siteName).replace("spaurl",siteUrl).replace("spamail@spa.com",siteEmail);
        return newString;
    }

    public String getDisclaimer(String siteName,String siteUrl, String siteEmail){

        String templateDC = "<h1>Disclaimer for spaname</h1>\n" +
                "\n" +
                "  <p>If you require any more information or have any questions about our site's disclaimer, please feel free to contact us by email at <a href='mailto:spamail@spa.com'>spamail@spa.com</a>. Our Disclaimer was generated with the help of the <a href='https://www.kitbox.org'>Disclaimer Generator</a>.</p>\n" +
                "\n" +
                "  <h2>Disclaimers for spaname</h2>\n" +
                "\n" +
                "  <p>All the information on this website - spaurl - is published in good faith and for general information purpose only. spaname does not make any warranties about the completeness, reliability and accuracy of this information. Any action you take upon the information you find on this website (spaname), is strictly at your own risk. spaname will not be liable for any losses and/or damages in connection with the use of our website.</p>\n" +
                "\n" +
                "  <p>From our website, you can visit other websites by following hyperlinks to such external sites. While we strive to provide only quality links to useful and ethical websites, we have no control over the content and nature of these sites. These links to other websites do not imply a recommendation for all the content found on these sites. Site owners and content may change without notice and may occur before we have the opportunity to remove a link which may have gone 'bad'.</p>\n" +
                "\n" +
                "  <p>Please be also aware that when you leave our website, other sites may have different privacy policies and terms which are beyond our control. Please be sure to check the Privacy Policies of these sites as well as their <b>Terms of Service</b> before engaging in any business or uploading any information. </p>\n" +
                "\n" +
                "  <h2>Consent</h2>\n" +
                "\n" +
                "  <p>By using our website, you hereby consent to our disclaimer and agree to its terms.</p>\n" +
                "\n" +
                "  <h2>Update</h2>\n" +
                "\n" +
                "  <p>Should we update, amend or make any changes to this document, those changes will be prominently posted here.</p>\n" +
                "  ";

        String newString = templateDC.replace("spaname", siteName).replace("spaurl",siteUrl).replace("spamail@spa.com",siteEmail);
        return newString;
    }

    public String getDMCA(String siteName,String siteUrl, String siteEmail){

        String templateDMCA = "We are currently working on this template";

        String newString = templateDMCA.replace("spaname", siteName).replace("spaurl",siteUrl).replace("spamail@spa.com",siteEmail);
        return newString;
    }

}



