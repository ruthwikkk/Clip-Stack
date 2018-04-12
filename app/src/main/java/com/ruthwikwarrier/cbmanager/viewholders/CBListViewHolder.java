package com.ruthwikwarrier.cbmanager.viewholders;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruthwikwarrier.cbmanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */

public class CBListViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_main_carddate) public TextView textDate;
    @BindView(R.id.text_main_cardtime) public TextView textTime;
    @BindView(R.id.text_main_cardtext) public TextView textMain;

    @BindView(R.id.img_main_card_star_button) public ImageButton btnStar;
    @BindView(R.id.imgb_main_card_copy_button) public ImageButton btnCopy;

    public CBListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}
