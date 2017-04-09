package tj.com.safe_project.beam;

import android.graphics.drawable.Drawable;

/**
 * Created by Jun on 17/4/9.
 */

public class AppInfo {
    public String name;
    public String packageNmae;
    public Drawable icon;
    public boolean isSdCard;
    public boolean isSystem;

    public String getPackageNmae() {
        return packageNmae;
    }

    public void setPackageNmae(String packageNmae) {
        this.packageNmae = packageNmae;
    }

    public boolean isSystem() {

        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSdCard() {

        return isSdCard;
    }

    public void setSdCard(boolean sdCard) {
        isSdCard = sdCard;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
