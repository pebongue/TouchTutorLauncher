package za.co.tcg.touchtutorlauncher.database;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import za.co.tcg.touchtutorlauncher.model.FileItem;

public class ItemRepository {

    private FileItemDao mFileItemDao;

    public ItemRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        mFileItemDao = db.fileItemDao();
    }

    /// Insert Methods
    public void insert(FileItem... files) {
        mFileItemDao.insertAll(files);
    }

    public long insertFileItem(FileItem fileItem) {
        return mFileItemDao.insert(fileItem);
    }

    // Fetch Folder
    public FileItem getFileItemByNewName(String name) {
        return mFileItemDao.findByNewName(name);
    }

    public String getOriginalName(String newName) {
        return mFileItemDao.findOriginalName(newName);
    }

    public void getAllFolderPairs(FileItemListListener listener) {
        new GetAllFileItemsAsyncTask(mFileItemDao, listener).execute();
    }

    public void getChildPairsForFolder(Long parentID, FileItemListListener listener) {
        new GetChildFileItemsAsyncTask(mFileItemDao, listener, parentID).execute();
    }

    private static class GetAllFileItemsAsyncTask extends AsyncTask<Void, Void, List<FileItem>> {

        private FileItemDao mAsyncTaskDao;
        private FileItemListListener mListener;

        GetAllFileItemsAsyncTask(FileItemDao dao, FileItemListListener listener) {
            mAsyncTaskDao = dao;
            mListener = listener;
        }

        @Override
        protected List<FileItem> doInBackground(Void... voids) {
            return mAsyncTaskDao.getAll();
        }

        @Override
        protected void onPostExecute(List<FileItem> results) {
            super.onPostExecute(results);

            if (mListener != null) {
                mListener.onFileItemsReturned(results);
            }
        }
    }

    private static class GetChildFileItemsAsyncTask extends AsyncTask<Void, Void, List<FileItem>> {

        private FileItemDao mAsyncTaskDao;
        private FileItemListListener mListener;
        private Long mParentID;

        GetChildFileItemsAsyncTask(FileItemDao dao, FileItemListListener listener, Long id) {
            mAsyncTaskDao = dao;
            mListener = listener;
            mParentID = id;
        }

        @Override
        protected List<FileItem> doInBackground(Void... voids) {
            return mAsyncTaskDao.getChildren(mParentID);
        }

        @Override
        protected void onPostExecute(List<FileItem> results) {
            super.onPostExecute(results);

            if (mListener != null) {
                mListener.onFileItemsReturned(results);
            }
        }
    }

    // Callback interfaces for insertions
    public interface FileItemListListener {
        void onFileItemsReturned(List<FileItem> fileItems);
    }

    public interface FileItemReturnListener {
        void onFileReturn(FileItem fileItem);
    }

    private static class GetFileItemAsyncTask extends AsyncTask<String, Void, FileItem> {

        private FileItemDao mAsyncTaskDao;
        private FileItemReturnListener mListener;
        private boolean isNameObfuscated;

        GetFileItemAsyncTask(FileItemDao dao, FileItemReturnListener listener, boolean nameObfuscated) {
            mAsyncTaskDao = dao;
            mListener = listener;
            isNameObfuscated = nameObfuscated;
        }

        @Override
        protected FileItem doInBackground(final String... params) {
            return isNameObfuscated ? mAsyncTaskDao.findByNewName(params[0]) : mAsyncTaskDao.findByOriginalName(params[0]);
        }

        @Override
        protected void onPostExecute(FileItem result) {
            super.onPostExecute(result);

            if (mListener != null) {
                mListener.onFileReturn(result);
            }
        }
    }
}
