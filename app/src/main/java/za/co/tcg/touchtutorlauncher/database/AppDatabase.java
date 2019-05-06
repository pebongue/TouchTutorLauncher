package za.co.tcg.touchtutorlauncher.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import za.co.tcg.touchtutorlauncher.model.FileItem;

@Database(entities = {FileItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FileItemDao fileItemDao();
}
