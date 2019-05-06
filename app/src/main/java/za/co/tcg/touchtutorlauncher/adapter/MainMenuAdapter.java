package za.co.tcg.touchtutorlauncher.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;
import za.co.tcg.touchtutorlauncher.database.ItemRepository;
import za.co.tcg.touchtutorlauncher.feature_main_menu.ItemListener;
import za.co.tcg.touchtutorlauncher.model.FileItem;
import za.co.tcg.touchtutorlauncher.utility.StringUtils;
import za.co.tcg.touchtutorlauncher.views.MainMenuViewHolder;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuViewHolder> {

    // Store a member variable for the files/folders
    private List<File> mList;
    // Store the context for easy access
    private Context mContext;
    private ItemListener mListener;

    public MainMenuAdapter(Context context, List<File> list, ItemListener listener) {
        this.mList = list;
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MainMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.main_menu_item_layout_2, parent, false);

        return new MainMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainMenuViewHolder holder, final int position) {
        File file = mList.get(position);

        //FileItem item = mRepository.getFileItemByNewName(file.getName());

        if (file.isDirectory()) {

            holder.imageView.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(file.getName());
        } else {

            // Set item views based on your views and model
            //String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
            String extension = file.getName().substring(file.getName().lastIndexOf("."));

            if (file.getName().contains("TouchTutor")){
                holder.textView.setText(mContext.getString(R.string.touchTutor));
                holder.imageView.setImageResource(R.drawable.ic_tt_hand_icon);
            } else if (extension.equals(".xml")){
                holder.imageView.setVisibility(View.GONE);
                holder.textView.setVisibility(View.VISIBLE);

                holder.textView.setText(StringUtils.getFileName(file));
                //holder.textView.setText(StringUtils.getFontSafeString(file.getName()));
            } else {
                holder.imageView.setVisibility(View.GONE);
                holder.textView.setVisibility(View.VISIBLE);
                holder.textView.setText(file.getName());
            }
        }

        if (file.isHidden()) {
            holder.textView.setAlpha(0.7f);
        } else {
            holder.textView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.ItemSelected(mList.get(holder.getAdapterPosition()));
                }
            });
        }

        if (file.getName().contains("TouchTutor")) {

            holder.textView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.backgroundView.setBackground(mContext.getResources().getDrawable(R.drawable.row_layout_background));

        } else {

            if (position%2 == 0) {
                // Even
                holder.textView.setTextColor(Color.WHITE);
                holder.backgroundView.setBackground(mContext.getResources().getDrawable(R.drawable.blue_row_layout_background));
            } else {
                // Odd
                holder.textView.setTextColor(mContext.getResources().getColor(R.color.gamma_blue));
                holder.backgroundView.setBackground(mContext.getResources().getDrawable(R.drawable.yellow_row_layout_background));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
