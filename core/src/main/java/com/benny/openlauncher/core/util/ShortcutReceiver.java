package com.benny.openlauncher.core.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import com.benny.openlauncher.core.R;
import com.benny.openlauncher.core.activity.Home;
import com.benny.openlauncher.core.interfaces.Item;
import com.benny.openlauncher.core.manager.Setup;

public class ShortcutReceiver extends BroadcastReceiver {

    public ShortcutReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() == null) return;

        String name = intent.getExtras().getString(Intent.EXTRA_SHORTCUT_NAME);
        Intent newIntent = (Intent) intent.getExtras().get(Intent.EXTRA_SHORTCUT_INTENT);
        Drawable shortcutIconDrawable = null;

        try {
            Parcelable iconResourceParcelable = intent.getExtras().getParcelable(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (iconResourceParcelable != null && iconResourceParcelable instanceof Intent.ShortcutIconResource) {
                Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) iconResourceParcelable;
                Resources resources = context.getPackageManager().getResourcesForApplication(iconResource.packageName);
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    shortcutIconDrawable = resources.getDrawable(id);
                }
            }
        } catch (Exception ignore) {
        } finally {
            if (shortcutIconDrawable == null)
                shortcutIconDrawable = new BitmapDrawable(context.getResources(), (Bitmap) intent.getExtras().getParcelable(Intent.EXTRA_SHORTCUT_ICON));
        }

        Item item = Setup.get().createShortcut(newIntent, shortcutIconDrawable, name);
        Point preferredPos = Home.launcher.desktop.pages.get(Home.launcher.desktop.getCurrentItem()).findFreeSpace();
        if (preferredPos == null) {
            Tool.toast(Home.launcher, R.string.toast_not_enough_space);
        } else {
            item.setX(preferredPos.x);
            item.setY(preferredPos.y);
            Home.db.setItem(item, Home.launcher.desktop.getCurrentItem(), 0);
            Home.launcher.desktop.addItemToPage(item, Home.launcher.desktop.getCurrentItem());
        }
    }
}
