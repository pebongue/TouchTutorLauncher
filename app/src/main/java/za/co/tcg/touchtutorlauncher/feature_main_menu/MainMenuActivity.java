package za.co.tcg.touchtutorlauncher.feature_main_menu;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;
import za.co.tcg.touchtutorlauncher.feature_apps.AppsActivity;
import za.co.tcg.touchtutorlauncher.feature_explorer.FileFragment;
import za.co.tcg.touchtutorlauncher.utility.StringUtils;

public class MainMenuActivity extends AppCompatActivity implements FileFragment.OnFragmentInteractionListener {

    // UI Elements
    @BindView(R.id.toolbar) Toolbar mToolBar;
    @BindView(R.id.main_menu_frame_layout) FrameLayout mFrameLayout;

    // Data Elements
    private boolean isExtraLayoutShowing = true;
    private boolean hasShownFirst = false;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 789;
    private Integer mCurrentFragmentCount = 0;
    private ArrayList<String> mTitles = new ArrayList<>();

    // Permissions
    final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("gamma Tutor: Launcher");
        mTitles.add(StringUtils.getFontSafeString("gamma Tutor: Launcher"));

        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(MainMenuActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(MainMenuActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if(writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED || readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(MainMenuActivity.this, EXTERNAL_PERMS, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initView();

            } else {
                Toast.makeText(getApplicationContext(), "You denied read external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Top Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (menu != null && menu.getItem(0) != null) {
            if (mCurrentFragmentCount > 1) {
                menu.getItem(0).setVisible(true);
            } else {
                menu.getItem(0).setVisible(false);
            }

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                if (hasShownFirst) {
                    onBackPressed();
                } else {
                    if (getSupportActionBar() != null) {
                        // Back button
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                }

                return true;
            case R.id.go_to_home:

                startActivity(new Intent(MainMenuActivity.this, MainMenuActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        // Find Content
        File mainDirectory = getMainDirectory();

        // On completion, start main activity

        if (mainDirectory != null) {

            initFile(mainDirectory, true);
        } else {
            Toast.makeText(this, "Content files not found, please contact an administrator", Toast.LENGTH_LONG).show();
        }
    }

    private File getMainDirectory(){

        List<File> test = new ArrayList<>();

        test.addAll(Arrays.asList(Environment.getExternalStorageDirectory().listFiles()));
        test.addAll(Arrays.asList(new File("/mnt/").listFiles()));
        test.addAll(Arrays.asList(new File("/storage/").listFiles()));

        File sdCard = new File("/sdcard/");

        if(sdCard != null) {
            test.addAll(Arrays.asList(sdCard.listFiles()));
        }

        if (test.isEmpty()) {
            return null;
        }

        for (File file : test) {

            if (file.getAbsolutePath().contains("TouchTutorContent")) {

                Log.d("MainMenuActivity: ", "Path to content configured successfully");
                return file;
            }
        }

        return null;
    }

    private void initFile(File file, boolean isMainMenu){

        if(isExtraLayoutShowing && !hasShownFirst){

            isExtraLayoutShowing = false;
        }

        if (hasShownFirst) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.main_menu_frame_layout, FileFragment.newInstance(file, isMainMenu))
                    .addToBackStack(null)
                    .commit();

            if (getSupportActionBar() != null) {
                // Back button
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            mToolBar.setTitle(StringUtils.getFontSafeString(file.getName()));
            mTitles.add(StringUtils.getFontSafeString(file.getName()));

        } else {

            hasShownFirst = true;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_menu_frame_layout, FileFragment.newInstance(file, isMainMenu))
                    .commit();
        }
        invalidateOptionsMenu();

        mCurrentFragmentCount++;
    }

    @Override
    public void onBackPressed() {

        mCurrentFragmentCount--;
        mTitles.remove(mTitles.size() - 1);
        if (mCurrentFragmentCount == 1) {
            mToolBar.setTitle("gamma Tutor: Launcher");

            if (getSupportActionBar() != null) {
                // Back button
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            invalidateOptionsMenu();

        } else {
            if (!mTitles.isEmpty()) {
                mToolBar.setTitle(mTitles.get(mTitles.size() - 1));
            } else {
                mToolBar.setTitle("gamma Tutor: Launcher");
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onDirectorySelected(File file) {
        initFile(file, false);
    }

    @Override
    public void touchTutorPackageSelected() {

        String touchTutorPackageName = "touchtutormobile.android";

        if(isInstalled(touchTutorPackageName)) {

            Intent intent = getPackageManager().getLaunchIntentForPackage(touchTutorPackageName);

            if(intent != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onFileSelected(File file) {

        // Check File Type, open in appropriate app
        String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

        switch (extension.toLowerCase()) {

            case ".xml":
                openAppsActivity(file);
                break;
            // PDF
            case ".pdf":
                openPDF(file);
                break;

            // Power Point
            case ".doc":
                openRTF(file);
                break;
            case ".docx":
                openRTF(file);
                break;
            case ".rtf":
                openRTF(file);
                break;

            // Power Point
            case ".ppt":
                openPowerPoint(file);
                break;
            case ".pptx":
                openPowerPoint(file);
                break;

            // Geogebra
            case ".ggb":
                openGeoGebra(file);
            case ".html":
                openUnknown(file);
                break;
            case ".xhtml":
                openUnknown(file);
                break;
            case ".htm":
                openUnknown(file);
                break;
            default:
                openUnknown(file);
                break;
        }
    }

    private void openUnknown(File file) {

        String appType = "application/*";

        openIntent(file, appType);
    }

    private void openRTF(File file) {

        String wpsOfficePackageName = "cn.wps.moffice_eng";


        if (isInstalled(wpsOfficePackageName)){

            openPackageIntent(file, wpsOfficePackageName);
        } else {
            launchPlayStore(wpsOfficePackageName);
        }
    }

    private void openPDF(File file) {
        //String packageName = "cn.wps.moffice_eng";
        String packageName = "com.adobe.reader";

        if (isInstalled(packageName)){

            openPackageIntent(file, packageName);
        } else {
            launchPlayStore(packageName);
        }
    }

    private void openPowerPoint(File file) {

        String wpsOfficePackageName = "cn.wps.moffice_eng";

        if (isInstalled(wpsOfficePackageName)){

            openPackageIntent(file, wpsOfficePackageName);
        } else {
            launchPlayStore(wpsOfficePackageName);
        }
    }

    private void openGeoGebra(File file) {

        /*
        String geogebraPackageName = "org.geogebra";

        if (isInstalled(geogebraPackageName)){

            openPackageIntent(file, geogebraPackageName);
        } else {
            launchPlayStore(geogebraPackageName);
        }
        */

        String appType = "application/*";
        openIntent(file, appType);
    }

    private void launchPlayStore(String packageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private void openIntent(File file, String appType){

        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri fileURI = FileProvider.getUriForFile(MainMenuActivity.this, MainMenuActivity.this.getPackageName() + ".file.provider", file);

        intent.setDataAndType(fileURI, appType);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void openAppsActivity(File file){

        Intent intent = new Intent(MainMenuActivity.this, AppsActivity.class);
        intent.putExtra("FILE", file.getAbsolutePath());
        startActivity(intent);
    }

    private void openPackageIntent(File file, String packageName) {

        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            Uri fileURI = FileProvider.getUriForFile(MainMenuActivity.this, MainMenuActivity.this.getPackageName() + ".file.provider", file);
            intent.setData(fileURI);
            intent.setPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            launchPlayStore(packageName);
        }
    }

    private boolean isInstalled(String packageName){

        PackageManager pm = getPackageManager();

        try {

            pm.getPackageInfo(packageName, 0);
            return true;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
