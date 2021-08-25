package com.landmark.media.demo.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.landmark.media.R;

/**********************************************
 * Filename：
 * Author:   qiang.chen@landmark-phb.com
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.xx  2021/8/23 14  chenqiang   1) …
 ***********************************************/
public class SearchViewHolder extends RecyclerView.ViewHolder {

    public final TextView name;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tv);
    }
}
