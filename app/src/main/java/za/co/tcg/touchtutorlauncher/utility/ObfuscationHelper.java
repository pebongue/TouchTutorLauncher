package za.co.tcg.touchtutorlauncher.utility;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.UUID;

import za.co.tcg.touchtutorlauncher.database.ItemRepository;
import za.co.tcg.touchtutorlauncher.model.FileItem;

/**
 * This is a helper class, used to tidy up the MainMenuActivity. Coincidentally, it also handles
 * the import & obfuscation process.
 *
 * This is also a subclass of AsyncTask, as it is assumed each time it is used it will involve copying files between directories, so
 * this will be done on a background thread to keep the progress dialog shown on app launch spinning, providing the (hopefully fruitful) illusion
 * of processing progress.
 *
 * The obfuscation and import & obfuscation methods are similar in functionality, yet dissimilar enough to warrant separate methods, since import would need
 * not only to move files between directories but also simultaneously keep track of the corresponding folder so that the current file can be copied over to the correct directory.
 *
 * It is assumed that if a folderPair of filePair object has no parentFolderID it is a direct child of the Main Content Directory, which
 * is searched for on app start.
 */
public class ObfuscationHelper extends AsyncTask<File, Void, Void> {
    // Variables
    private File mMainDirectory;
    private ItemRepository mRepository;
    private OnCompletion mListener;
    private ArrayList<FileItem> mFileItems;

    // Constructor
    public ObfuscationHelper() {

    }

    // Builder Pattern Methods
    public ObfuscationHelper setMainDirectory(File directory) {
        this.mMainDirectory = directory;
        return this;
    }

    public ObfuscationHelper setRepository(ItemRepository repo) {
        this.mRepository = repo;
        return this;
    }

    public ObfuscationHelper setOnCompletedListener(OnCompletion listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnCompletion {
        void onCompleted(File mainDirectory);
    }

    // This is where the fun begins
    @Override
    protected Void doInBackground(final File... params) {

        // Check if obfuscated -> Shared preferences check and local SQLite?
        boolean isObfuscated = false;

        if (isObfuscated) {
            // If not, check import folder

            mRepository.getAllFolderPairs(fileItems -> {

                mFileItems = new ArrayList<>(fileItems);

                if (mMainDirectory.listFiles() != null) {

                    for (File file : mMainDirectory.listFiles()) {

                        if (file.getName().equalsIgnoreCase("import")) {

                            // Check if import folder is actually a folder
                            if (file.isDirectory()) {

                                // Navigate files and folders, obfuscate and copy to the directories specified
                                for (File importFile : file.listFiles()) {

                                    importAndObfuscate(importFile, mMainDirectory);
                                }

                            } else {
                                break;
                            }
                        }
                    }
                }
            });

        } else {
            // If false, obfuscate

            // Null check
            if (mMainDirectory.listFiles() != null) {

                for (File file : mMainDirectory.listFiles()) {

                    // We don't want to obfuscate the import folder
                    if (!file.getName().equalsIgnoreCase("import")) {

                        // This is where the fun begins
                        obfuscateContent(file, mMainDirectory, null);
                    }
                }
            }
        }

        Log.d("Obfuscation Helper", "AsyncTask has completed processing, about to return null...");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mListener != null) {
            mListener.onCompleted(mMainDirectory);
        }
    }

    private void obfuscateContent(final File file, File parentFile, @Nullable Long parentFolderID) {

        // If file is folder which may have files
        if (file.isDirectory()) {

            String currentName = file.getName();
            String uuid = UUID.randomUUID().toString();
            File newFile = new File(parentFile, uuid);

            // Folder Pair to insert
            FileItem fileItem = new FileItem();
            fileItem.setNewName(uuid);
            fileItem.setOriginalName(currentName);
            if (parentFolderID != null) {
                fileItem.setParentFolderID(parentFolderID);
            }

            // Renaming, which returns a boolean
            if (file.renameTo(newFile)) {

                Log.d("ObfuscationHelper", "Folder with name " + currentName + " obfuscated successfully");

                // Inserting item pair -- Might need to do differently, add them all to a list and add them all in one go at the end of this operation
                if (mRepository != null) {

                    long parentID = mRepository.insertFileItem(fileItem);

                    // Null check
                    if (newFile.listFiles() != null) {

                        // For each file present in this current, folder file, obfuscate
                        for (File file1 : newFile.listFiles()) {
                            obfuscateContent(file1, newFile, parentID);
                        }
                    }
                }
            } else {
                Log.e("ObfuscationHelper", "Folder with name " + currentName + " obfuscation failed!");
            }

        } else {
            // Single file, obfuscate
            // We can assume the file passed here as a parameter, so we need to rename the file, using this file as the directory

            // Getting new, obfuscated name and current file name
            String currentName = file.getName();

            //if (repository.)

            // For renaming method
            String uuid = UUID.randomUUID().toString();
            File newFile = new File(parentFile, uuid);

            // File Pair to insert
            FileItem fileItem = new FileItem();
            fileItem.setNewName(uuid);
            fileItem.setOriginalName(currentName);

            if (parentFolderID != null) {
                fileItem.setParentFolderID(parentFolderID);
            }

            // Renaming, which returns a boolean
            if (file.renameTo(newFile)) {

                Log.d("ObfuscationHelper", "File with name " + currentName + " obfuscated successfully");

                // Inserting item pair -- Might need to do differently, add them all to a list and add them all in one go at the end of this operation
                if (mRepository != null) {
                    mRepository.insert(fileItem);
                }
            } else {
                Log.e("ObfuscationHelper", "File with name " + currentName + " obfuscation failed!");
            }
        }
    }

    // First time we run this, the parent folder should always be mMainDirectory
    private void importAndObfuscate(final File file, final File parentFolder) {

        // We have the main directory, we have the import folder.

        // When a folder is found in the import folder, we need to simultaneously navigate through it and it's corresponding folder in the main directory

        if (file.isDirectory()) {

            // We need to navigate the parentFolder, to see if it has a name in Room that coincides with this one
            // Null check
            if (parentFolder.listFiles() != null && file.listFiles() != null) {

                // Count check
                if (parentFolder.listFiles().length > 0 && file.listFiles().length > 0) {

                    for (File currentFolderFile : file.listFiles()) {

                        // Parent folder's child folder's obfuscated name must match an entry in the mFileItemsList where we can get the original name that should match the Parent folder's child folder's original name
                        FileItem temp = null;

                        for (FileItem mFileItem : mFileItems) {
                            if (mFileItem.getOriginalName().equalsIgnoreCase(currentFolderFile.getName())) {
                                temp = mFileItem;
                                break;
                            }
                        }

                        if (temp != null) {
                            // Folder already exists
                            for (File parentFolderFile : parentFolder.listFiles()) {
                                if (parentFolderFile.getName().equalsIgnoreCase(temp.getNewName())) {
                                    // parentFolderFile is the corresponding directory in mMainDirectory for the current folder we have, so we need to now go one level deeper
                                    importAndObfuscate(currentFolderFile, parentFolderFile);
                                }
                            }
                        } else {
                            // Folder does not already exist, copy over
                            try {
                                Log.d("ObfuscationHelper", "Folder with name " + currentFolderFile.getName() + " not found in Main directory, attempting copy...");
                                copyFile(file, parentFolder);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        } else {
            // Single File

            // Obfuscate
            String currentName = file.getName();
            String uuid = UUID.randomUUID().toString();

            // File Pair to insert
            final FileItem fileItem = new FileItem();
            fileItem.setNewName(uuid);
            fileItem.setOriginalName(currentName);

            if (!parentFolder.getName().equalsIgnoreCase(mMainDirectory.getName())) {

                FileItem parentFileItem = mRepository.getFileItemByNewName(parentFolder.getName());

                fileItem.setParentFolderID(parentFileItem.getUid());
                mRepository.insert(fileItem);

                // Copy over to parent folder
                try {
                    copyFile(file, parentFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                // We are still in mMainDirectory, just copy
                try {
                    copyFile(file, parentFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            Log.d("ObfuscationHelper", "Copy for folder with name " + sourceFile.getName() + " successful!");

        }catch (Exception e){

            Log.e("ObfuscationHelper", "Copy for folder with name " + sourceFile.getName() + " failed!");

        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
