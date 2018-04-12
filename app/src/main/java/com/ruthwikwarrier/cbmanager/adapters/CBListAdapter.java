package com.ruthwikwarrier.cbmanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;
import com.ruthwikwarrier.cbmanager.database.DBHelper;
import com.ruthwikwarrier.cbmanager.model.ClipObject;
import com.ruthwikwarrier.cbmanager.services.ClipActionBridge;
import com.ruthwikwarrier.cbmanager.viewholders.CBListViewHolder;

import java.util.ArrayList;

/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */

public class CBListAdapter extends RecyclerView.Adapter<CBListViewHolder> implements Filterable {

    Context context;
    ArrayList<ClipObject> clipDataList;
    ArrayList<ClipObject> clipDataFilterList;
    DBHelper dbHelper;
    boolean isFromNotification;

    public CBListAdapter(Context con, ArrayList<ClipObject> list, DBHelper helper, boolean isNot){
        this.context = con;
        this.clipDataList = list;
        this.clipDataFilterList = list;
        this.isFromNotification = isNot;
        dbHelper = helper;
    }

    @Override
    public CBListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card, parent, false);

        return new CBListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CBListViewHolder holder, int position) {

        final ClipObject clipObject = clipDataList.get(position);
        final String text = clipObject.getText();
        final int clip_id = clipObject.getId();
        holder.textMain.setText(text);
        holder.textTime.setText(AppUtils.getFormatTime(context, clipObject.getDate()));
        holder.textDate.setText(AppUtils.getFormatDate(context, clipObject.getDate()));

        if (clipObject.isStarred()) {
            holder.btnStar.setImageResource(R.drawable.ic_action_star_yellow);
        } else {
            holder.btnStar.setImageResource(R.drawable.ic_action_star_outline_grey600);
        }

        holder.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openIntent = new Intent(context, ClipActionBridge.class)
                        .putExtra(Intent.EXTRA_TEXT, text)
                        .putExtra(ClipActionBridge.ACTION_CODE, ClipActionBridge.ACTION_COPY);
                context.startService(openIntent);

                if (isFromNotification)
                    ((Activity) context).finish();

            }
        });

        holder.btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipObject.setStarred(!clipObject.isStarred());
                dbHelper.updateClipFav(clipObject);

                if(clipObject.isStarred())
                    holder.btnStar.setImageResource(R.drawable.ic_action_star_yellow);
                else
                    holder.btnStar.setImageResource(R.drawable.ic_action_star_outline_grey600);

            }
        });

        holder.textMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openIntent = new Intent(context, ClipActionBridge.class)
                        .putExtra(Intent.EXTRA_TEXT, Integer.toString(clip_id))
                        .putExtra(ClipActionBridge.ACTION_CODE, ClipActionBridge.ACTION_EDIT);
                context.startService(openIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return clipDataFilterList.size();
    }

    public void removeItem(int position) {
        dbHelper.deleteClip(clipDataList.get(position).getId());
        clipDataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, clipDataList.size());

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                Log.e("Adapter","Search filter called");

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    clipDataFilterList = clipDataList;
                } else {
                    ArrayList<ClipObject> filteredList = new ArrayList<>();
                    for (ClipObject clip : clipDataList) {
                        if (clip.getText().toLowerCase().contains(charString)) {

                            filteredList.add(clip);
                        }
                    }

                    clipDataFilterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = clipDataFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clipDataFilterList = (ArrayList<ClipObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
