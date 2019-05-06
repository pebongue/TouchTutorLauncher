package za.co.tcg.touchtutorlauncher.feature_explorer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;
import za.co.tcg.touchtutorlauncher.adapter.FileAdapter;
import za.co.tcg.touchtutorlauncher.adapter.MainMenuAdapter;

public class FileFragment extends Fragment {

    // UI Elements
    @BindView(R.id.file_fragment_recycler_view) RecyclerView mRecyclerView;

    // Data Elements
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private File mRootFile;
    private Boolean mIsMainMenu = false;

    // On File Select Listener
    private OnFragmentInteractionListener mListener;

    public FileFragment() {
        // Required empty public constructor
    }

    public static FileFragment newInstance(File file, boolean isMainMenu) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, file);
        args.putBoolean(ARG_PARAM2, isMainMenu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRootFile = (File) getArguments().getSerializable(ARG_PARAM1);
            mIsMainMenu = getArguments().getBoolean(ARG_PARAM2, false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.file_fragment, container, false);
        ButterKnife.bind(this, view);

        if (mRootFile != null) {

            try {
                if (mIsMainMenu) {

                    // Grid Layout RecyclerView
                    List<File> fileList = new ArrayList<>(Arrays.asList(mRootFile.listFiles()));

                    MainMenuAdapter adapter = new MainMenuAdapter(getContext(), fileList, file -> {

                        if (mListener != null) {

                            if (file.getName().contains("TouchTutor")){
                                mListener.touchTutorPackageSelected();
                            } else if (file.isDirectory()) {
                                mListener.onDirectorySelected(file);
                            } else {
                                mListener.onFileSelected(file);
                            }
                        }

                    });

                    GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);

                    if(mRecyclerView != null){
                        mRecyclerView.setAdapter(adapter);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                    }
                } else {

                    // Linear Layout RecyclerView
                    List<File> fileList = new ArrayList<>(Arrays.asList(mRootFile.listFiles()));

                    FileAdapter adapter = new FileAdapter(getContext(), fileList, file -> {
                        if (mListener != null) {

                            if (file.isDirectory()) {

                                mListener.onDirectorySelected(file);
                            } else {
                                mListener.onFileSelected(file);
                            }
                        }
                    });

                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

                    if(mRecyclerView != null){
                        mRecyclerView.setAdapter(adapter);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                    }
                }
            } catch (Exception e) {
                Log.e("File Fragment - ", "An exception was thrown while accessing the file content");
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong displaying the content. Please contact an administrator", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

        void onDirectorySelected(File file);
        void onFileSelected(File file);
        void touchTutorPackageSelected();
    }
}
