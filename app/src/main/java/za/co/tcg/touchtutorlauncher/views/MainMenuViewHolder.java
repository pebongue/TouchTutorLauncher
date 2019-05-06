package za.co.tcg.touchtutorlauncher.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.tcg.touchtutorlauncher.R;

public class MainMenuViewHolder extends RecyclerView.ViewHolder {

    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    //@BindView(R.id.menu_item_image_view) ImageView imageView;
    @BindView(R.id.menu_item_text_view) public TextView textView;
    @BindView(R.id.menu_item_image_view) public ImageView imageView;
    @BindView(R.id.row_background) public RelativeLayout backgroundView;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public MainMenuViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
