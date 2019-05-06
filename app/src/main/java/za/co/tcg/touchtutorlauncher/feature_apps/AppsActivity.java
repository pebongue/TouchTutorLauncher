package za.co.tcg.touchtutorlauncher.feature_apps;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;
import za.co.tcg.touchtutorlauncher.adapter.AppAdapter;
import za.co.tcg.touchtutorlauncher.feature_main_menu.MainMenuActivity;
import za.co.tcg.touchtutorlauncher.model.AppModel;
import za.co.tcg.touchtutorlauncher.utility.StringUtils;

public class AppsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar mToolBar;
    @BindView(R.id.apps_recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        if (getSupportActionBar() != null) {
            // Back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        File appsFile = null;

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("FILE")) {
                appsFile = new File(getIntent().getExtras().getString("FILE"));
            }
        }
        mToolBar.setTitle(StringUtils.getFileName(appsFile));

        ArrayList<AppModel> apps = readAppList(appsFile);

        AppAdapter adapter = new AppAdapter(AppsActivity.this, apps, new AppListener() {
            @Override
            public void appSelected(AppModel app) {

                if (isInstalled(app.getPackageName())){

                    openIntent(app.getPackageName());
                } else {
                    launchPlayStore(app.getPackageName());
                }
            }
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(AppsActivity.this, 3);

        if(mRecyclerView != null){
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                finish();

                return true;
            case R.id.go_to_home:

                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openIntent(String packageName) {

        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            launchPlayStore(packageName);
        }
    }

    private void launchPlayStore(String packageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        }
        catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
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

    private ArrayList<AppModel> readAppList(File appsFile){

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(appsFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("app");

            //now XML is loaded as Document in memory, lets convert it to Object List
            ArrayList<AppModel> appList = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                appList.add(getApp(nodeList.item(i)));
            }

            return appList;
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static AppModel getApp(Node node) {
        //XMLReaderDOM domReader = new XMLReaderDOM();
        AppModel emp = new AppModel();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            emp.setName(getTagValue("name", element));
            emp.setPackageName(getTagValue("package_name", element));
        }

        return emp;
    }


    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}
