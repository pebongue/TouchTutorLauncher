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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;
import za.co.tcg.touchtutorlauncher.feature_apps.AppListener;
import za.co.tcg.touchtutorlauncher.model.AppModel;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

        // Store a member variable for the files/folders
        private List<AppModel> mList;
        // Store the context for easy access
        private Context mContext;
        private AppListener mListener;

    public AppAdapter(Context context, List<AppModel> list, AppListener listener) {
        this.mList = list;
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.main_menu_item_layout_2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        AppModel appModel = mList.get(position);

        // Set item views based on your views and model
        holder.textView.setText(appModel.getName());
        holder.imageView.setVisibility(View.GONE);
        //holder.imageView.setImageResource(R.mipmap.ic_launch);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.appSelected(mList.get(holder.getAdapterPosition()));
            }
        });

        if (position%2 == 0) {
            // Even
            holder.textView.setTextColor(Color.WHITE);
            holder.backgroundView.setBackgroundColor(mContext.getResources().getColor(R.color.gamma_blue));
        } else {
            // Odd
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.gamma_blue));
            holder.backgroundView.setBackgroundColor(mContext.getResources().getColor(R.color.gamma_yellow));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //@BindView(R.id.menu_item_image_view) ImageView imageView;
        @BindView(R.id.menu_item_text_view) TextView textView;
        @BindView(R.id.row_background) RelativeLayout backgroundView;
        @BindView(R.id.menu_item_image_view) ImageView imageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
