package za.co.tcg.touchtutorlauncher.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;
import za.co.tcg.touchtutorlauncher.feature_main_menu.ItemListener;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    // Store a member variable for the files/folders
    private List<File> mList;
    // Store the context for easy access
    private Context mContext;
    private ItemListener mListener;

    private SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm");

    public FileAdapter(Context context, List<File> list, ItemListener listener) {
        this.mList = list;
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.file_item_layout_2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final File file = mList.get(position);

        // Set item views based on your views and model
        holder.primaryTextView.setText(file.getName());

        Date dateModified = new Date(file.lastModified());

        String dateString = format.format(dateModified);

        holder.secondaryTextView.setText(String.format(mContext.getString(R.string.date_modified_string), dateString));

        if(file.isDirectory()) {
            holder.imageView.setImageResource(R.drawable.ic_folder);
        } else {
            // Check File Type
            String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
            setIconImage(holder.imageView, extension);
        }

        if (file.isHidden()) {
            holder.imageView.setAlpha(0.7f);
            holder.secondaryTextView.setAlpha(0.7f);
            holder.secondaryTextView.setAlpha(0.7f);
        } else {
            holder.imageView.setAlpha(1.0f);
            holder.secondaryTextView.setAlpha(1.0f);
            holder.secondaryTextView.setAlpha(1.0f);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.ItemSelected(mList.get(holder.getAdapterPosition()));
                }
            });
        }

        if (position%2 == 0) {
            // Even
            holder.backgroundView.setBackgroundColor(mContext.getResources().getColor(R.color.gamma_blue));
        } else {
            // Odd
            holder.backgroundView.setBackgroundColor(mContext.getResources().getColor(R.color.gamma_yellow));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void setIconImage(ImageView imageView, String extension){

        switch (extension.toLowerCase()) {
            // PDF
            case ".pdf":
                imageView.setImageResource(R.drawable.ic_tt_pdf);
                break;

            // Power Point
            case ".ppt":
                imageView.setImageResource(R.drawable.ic_tt_ppt);
                break;
            case ".pptx":
                imageView.setImageResource(R.drawable.ic_tt_ppt);
                break;

            // Document
            case ".doc":
                imageView.setImageResource(R.drawable.ic_tt_doc);
                break;
            case ".docx":
                imageView.setImageResource(R.drawable.ic_tt_doc);
                break;

            // Geogebra
            case ".ggb":
                imageView.setImageResource(R.drawable.ic_tt_ggb);
            case ".html":
                imageView.setImageResource(R.drawable.ic_tt_ggb);
                break;
            case ".xhtml":
                imageView.setImageResource(R.drawable.ic_tt_ggb);
                break;
            case ".htm":
                imageView.setImageResource(R.drawable.ic_tt_ggb);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_tt_folder);
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @BindView(R.id.imageView) ImageView imageView;
        @BindView(R.id.primaryTextView) TextView primaryTextView;
        @BindView(R.id.secondaryTextView) TextView secondaryTextView;
        @BindView(R.id.backgroundView) View backgroundView;
        //@BindView(R.id.click_view) View click_view;

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
