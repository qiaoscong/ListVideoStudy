package text.qiao.com.listvideostudy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import text.qiao.com.listvideostudy.item.VideoListItem;
import text.qiao.com.utilslibrary.utils.log.LogUtil;

/**
 * @project：ListVideoStudy
 * @fileName VideoListAdapter
 * @author：乔少聪
 * @date：2018/11/29 15:53
 * @describe：
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    /**
     * 视频列表
     */
    private final List<VideoListItem> mList;

private Context context;
    public VideoListAdapter(List<VideoListItem> mList,Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent,false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(view);
        view.setTag(videoViewHolder);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoListItem videoListItem = mList.get(position);
        holder.bindTo(videoListItem,position);
        LogUtil.e("position"+position+"videoListItem.getPlayPostion()"+videoListItem.getPlayPostion());
        if (position != videoListItem.getPlayPostion()) {
            holder.getItemVideoIvCover().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        /**
         * 播放控件
         */
        @BindView(R.id.item_video_vpv_player)
        VideoPlayerView itemVideoVpvPlayer;
        /**
         * 覆盖层
         */
        @BindView(R.id.item_video_iv_cover)
        ImageView itemVideoIvCover;
        /**
         * 标题
         */
        @BindView(R.id.item_video_tv_title)
        TextView itemVideoTvTitle;
        /**
         * 百分比
         */
        @BindView(R.id.item_video_tv_percents)
        TextView itemVideoTvPercents;
        private Context mContext;
        private MediaPlayerWrapper.MainThreadMediaPlayerListener mediaPlayerListener;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext().getApplicationContext();
            mediaPlayerListener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
                @Override
                public void onVideoSizeChangedMainThread(int width, int height) {
                    LogUtil.e("onVideoSizeChangedMainThread");
                }

                @Override
                public void onVideoPreparedMainThread() {
                    // 视频播放隐藏前图
                    LogUtil.e("视频播放隐藏前图");
                    itemVideoIvCover.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                    LogUtil.e("onVideoCompletionMainThread");
                }

                @Override
                public void onErrorMainThread(int what, int extra) {
                    LogUtil.e("onErrorMainThread");
                }

                @Override
                public void onBufferingUpdateMainThread(int percent) {
                    LogUtil.e("onBufferingUpdateMainThread");
                }

                @Override
                public void onVideoStoppedMainThread() {
                    // 视频暂停显示前图
                    LogUtil.e("视频暂停显示前图");
                    itemVideoIvCover.setVisibility(View.VISIBLE);
                }
            };
            itemVideoVpvPlayer.addMediaPlayerListener(mediaPlayerListener);
        }

        public void bindTo(VideoListItem videoListItem,int postion) {
            LogUtil.e(videoListItem.getmTitle()+"第几个"+postion);
            itemVideoTvTitle.setText(videoListItem.getmTitle()+"第几个"+postion);
            itemVideoIvCover.setVisibility(View.VISIBLE);
            Picasso.get().load(videoListItem.getmImageResource()).into(itemVideoIvCover);
        }

        public ImageView getItemVideoIvCover() {
            return itemVideoIvCover;
        }

        /**
         * 播放器
         *
         * @return
         */
        public VideoPlayerView getItemVideoVpvPlayer() {
            return itemVideoVpvPlayer;
        }

        /**
         * 百分比
         *
         * @return
         */
        public TextView getItemVideoTvPercents() {
            return itemVideoTvPercents;
        }
    }
}
