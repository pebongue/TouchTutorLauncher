package za.co.tcg.touchtutorlauncher.database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class AppDatabaseSingleton {

    private static final String DB_NAME = "ttlauncher";

    // Shared Instance
    private static volatile AppDatabase mInstance;

    // private constructor
    private AppDatabaseSingleton(){

        if (mInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public synchronized static AppDatabase getInstance(final Context context){

        if (mInstance == null){ // first check

            synchronized (AppDatabaseSingleton.class) {
                if (mInstance == null) mInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).build();
            }
        }

        return mInstance;
    }
}
