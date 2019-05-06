package za.co.tcg.touchtutorlauncher.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class FileItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    private String originalName;
    private String newName;
    private String path;
    private Long parentFolderID;
    private Boolean isFolder;

    public FileItem() {}

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public Long getParentFolderID() {
        return parentFolderID;
    }

    public void setParentFolderID(Long parentFolderID) {
        this.parentFolderID = parentFolderID;
    }

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
