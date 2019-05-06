package za.co.tcg.touchtutorlauncher.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import za.co.tcg.touchtutorlauncher.model.FileItem;

@Dao
public interface FileItemDao {

    @Query("SELECT * FROM FileItem")
    List<FileItem> getAll();

    @Query("SELECT * FROM FileItem WHERE parentFolderID = :parentID")
    List<FileItem> getChildren(long parentID);

    @Query("SELECT * FROM FileItem WHERE newName = :name")
    FileItem findByNewName(String name);

    @Query("SELECT * FROM FileItem WHERE originalName = :name")
    FileItem findByOriginalName(String name);

    @Query("SELECT originalName FROM FileItem WHERE newName = :newName")
    String findOriginalName(String newName);

    @Insert
    List<Long> insertAll(FileItem... folderPairs);

    @Insert
    long insert(FileItem folderPair);

    @Delete
    void delete(FileItem folder);
}
