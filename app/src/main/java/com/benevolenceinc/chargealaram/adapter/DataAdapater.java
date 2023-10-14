package com.benevolenceinc.chargealaram.adapter;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benevolenceinc.chargealaram.R;
import com.benevolenceinc.chargealaram.database.DatabaseHelper;
import com.benevolenceinc.chargealaram.model.Data;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;

import java.util.List;


/**
 * Created by Hitesh on 3/4/19.
 */
public class DataAdapater extends RecyclerView.Adapter<DataAdapater.ViewHolderClass> {
    private List<Data> list;
    private Context context;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public DataAdapater(List<Data> list, Context context) {
        this.list = list;
        this.context = context;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.swipe_delete_layout, viewGroup, false);
        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass viewHolderClass, int position) {
        final DatabaseHelper databaseHelper = new DatabaseHelper(context);
        viewBinderHelper.bind(viewHolderClass.swipeRevealLayout, String.valueOf(list.get(position).get_id()));
        viewHolderClass.tvPercentage.setText(String.valueOf(list.get(position).getPercentage()));
        viewHolderClass.jellyToggleButton.setChecked(list.get(position).getStatus() == 1 ? true : false);
        viewHolderClass.jellyToggleButton.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state.equals(State.LEFT)) {
                    list.get(position).setStatus(0);
                    databaseHelper.updateStatus(list.get(position));
                }
                if (state.equals(State.RIGHT)) {
                    if(list.get(position).getStatus() == 0) {
                        list.get(position).setOnce_notified(13);
                        databaseHelper.updateOnceNotified(list.get(position));
                    }
                    list.get(position).setStatus(1);
                    databaseHelper.updateStatus(list.get(position));
                }

            }
        });

        viewHolderClass.rlImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.delete(list.get(position));
                list.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView tvPercentage;
        RelativeLayout rlImage;
        JellyToggleButton jellyToggleButton;
        SwipeRevealLayout swipeRevealLayout;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            tvPercentage = (TextView) itemView.findViewById(R.id.tvPercentage);
            rlImage = (RelativeLayout) itemView.findViewById(R.id.rlImage);
            jellyToggleButton = (JellyToggleButton) itemView.findViewById(R.id.jellyButton);
            swipeRevealLayout = (SwipeRevealLayout)itemView.findViewById(R.id.swipeLayout);
        }
    }
}
