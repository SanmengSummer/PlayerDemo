package com.landmark.media.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.landmark.media.R;
import com.landmark.media.common.MetadataTypeValue;
import com.landmark.media.demo.viewholder.SearchViewHolder;
import com.landmark.media.model.MediaDataModel;
import com.landmark.media.utils.LogUtils;

import java.util.List;

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
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MediaDataModel> models;
    private Context context;

    private final int TYPE_ALBUM = 0;
    private final int TYPE_MUSIC = 1;
    private final int TYPE_ARTIST = 2;
    private final int TYPE_FOLDER = 3;
    private final int TYPE_GENRE = 4;
    private final int TYPE_RECORD = 5;

    public SearchAdapter(List<MediaDataModel> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchViewHolder searchViewHolder = null;
        View inflate = null;
        if (TYPE_ALBUM == viewType) {
            inflate = View.inflate(context, R.layout.item_album, null);
        } else if (TYPE_MUSIC == viewType) {
            inflate = View.inflate(context, R.layout.item_music, null);
        } else if (TYPE_ARTIST == viewType) {
            inflate = View.inflate(context, R.layout.itema_artist, null);
        } else if (TYPE_FOLDER == viewType) {
            inflate = View.inflate(context, R.layout.item_folder, null);
        } else if (TYPE_GENRE == viewType) {
            inflate = View.inflate(context, R.layout.item_genre, null);
        } else if (TYPE_RECORD == viewType) {
            inflate = View.inflate(context, R.layout.item_history, null);
        }
        searchViewHolder = new SearchViewHolder(inflate);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaDataModel mediaDataModel = models.get(position);
        LogUtils.debug("TAG", " ITEMTYPE: " + mediaDataModel.toString());

        String itemType = mediaDataModel.getItemType();
        SearchViewHolder holder1 = null;
        if (MetadataTypeValue.TYPE_ALBUM.getType().equals(itemType)) {
            holder1 = (SearchViewHolder) holder;
            holder1.name.setText(mediaDataModel.getName());
        } else if (MetadataTypeValue.TYPE_ARTIST.getType().equals(itemType)) {
            holder1 = (SearchViewHolder) holder;
            holder1.name.setText(mediaDataModel.getName());
        } else if (MetadataTypeValue.TYPE_FOLDER.getType().equals(itemType)) {
            holder1 = (SearchViewHolder) holder;
            holder1.name.setText(mediaDataModel.getName());
        } else if (MetadataTypeValue.TYPE_GENRE.getType().equals(itemType)) {
            holder1 = (SearchViewHolder) holder;
            holder1.name.setText(mediaDataModel.getName());
        } else if (MetadataTypeValue.TYPE_MUSIC.getType().equals(itemType)) {
            holder1 = (SearchViewHolder) holder;
            holder1.name.setText(mediaDataModel.getName());
        } else {
            holder1 = (SearchViewHolder) holder;
            holder1.name.setText(mediaDataModel.getName());
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (MetadataTypeValue.TYPE_MUSIC.getType().equals(itemType)) {
                    listener.onLongClickMusicListener(MetadataTypeValue.valueOf(itemType), mediaDataModel);
                    return true;
                }
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.debug("TAG", " ITEMTYPE: " + itemType);
                if (listener == null) {
                    LogUtils.debug("TAG", " ITEMTYPE listener null");
                    return;
                }
                String itemType = mediaDataModel.getItemType();
                Long id = 0l;
                if (MetadataTypeValue.TYPE_MUSIC.getType().equals(itemType)) {
                    LogUtils.debug("TAG", " ITEMTYPE listener TYPE_MUSIC" + listener);
                    listener.onClickMusicListener(models, position);
                } else {
                    if (MetadataTypeValue.TYPE_ALBUM.getType().equals(itemType)) {
                        id = mediaDataModel.getAlbumId();
                    } else if (MetadataTypeValue.TYPE_ARTIST.getType().equals(itemType)) {
                        id = mediaDataModel.getSingerId();
                    } else if (MetadataTypeValue.TYPE_FOLDER.getType().equals(itemType)) {
                        id = mediaDataModel.getFolderId();
                    } else if (MetadataTypeValue.TYPE_GENRE.getType().equals(itemType)) {
                        id = mediaDataModel.getGenreId();
                    } else if (MetadataTypeValue.TYPE_RECORD.getType().equals(itemType)) {
                        id = mediaDataModel.getRecordId();
                    }
                    listener.onClickListener(MetadataTypeValue.valueOf(itemType), String.valueOf(id));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models == null || models.size() == 0 ? 0 : models.size();
    }

    @Override
    public int getItemViewType(int position) {
        String itemType = models.get(position).getItemType();
        if (MetadataTypeValue.TYPE_ALBUM.getType().equals(itemType)) {
            return TYPE_ALBUM;
        } else if (MetadataTypeValue.TYPE_MUSIC.getType().equals(itemType)) {
            return TYPE_MUSIC;
        } else if (MetadataTypeValue.TYPE_ARTIST.getType().equals(itemType)) {
            return TYPE_ARTIST;
        } else if (MetadataTypeValue.TYPE_FOLDER.getType().equals(itemType)) {
            return TYPE_FOLDER;
        } else if (MetadataTypeValue.TYPE_GENRE.getType().equals(itemType)) {
            return TYPE_GENRE;
        } else {
            return TYPE_RECORD;
        }
    }

    public interface IOnClicklistener {
        void onClickListener(MetadataTypeValue value, String id);

        void onClickMusicListener(List<MediaDataModel> models, int model);

        default void onLongClickMusicListener(MetadataTypeValue value, MediaDataModel id) {

        }
    }

    IOnClicklistener listener;

    public void setOnClickListener(IOnClicklistener listener) {
        this.listener = listener;
    }

}
