package za.co.tcg.touchtutorlauncher.model;

import java.io.Serializable;

public class AppModel implements Serializable {

    private String name;
    private String packageName;

    public AppModel(){ }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
