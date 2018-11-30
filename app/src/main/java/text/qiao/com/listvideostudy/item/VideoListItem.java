package text.qiao.com.listvideostudy.item;

import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.volokh.danylo.video_player_manager.manager.VideoItem;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.items.ListItem;

import text.qiao.com.listvideostudy.VideoListAdapter;

/**
 * @project：ListVideoStudy
 * @fileName VideoListItem
 * @author：乔少聪
 * @date：2018/11/29 15:16
 * @describe：
 */

public abstract class VideoListItem implements VideoItem,ListItem {
    /**
     *  当前视图框架
     */
    private final Rect mCurrentViewRect;
    /**
     *  视频播放管理类
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager;
    /**
     *  标题
     */
    private String mTitle;
    /**
     *  图片资源
     */
    @DrawableRes  private final int  mImageResource;

    public VideoListItem( VideoPlayerManager<MetaData> mVideoPlayerManager, String mTitle,@DrawableRes int mImageResource) {
        mCurrentViewRect = new Rect();
        this.mVideoPlayerManager = mVideoPlayerManager;
        this.mTitle = mTitle;
        this.mImageResource = mImageResource;
    }

    public String getmTitle() {
        return mTitle;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    @Override
    public int getVisibilityPercents(View view) {
        int percents=100;
        view.getLocalVisibleRect(mCurrentViewRect);
        int height=view.getHeight();
        if(viewIsPartiallyHiddenTop()){
            percents=(height-mCurrentViewRect.top)*100/height;
        }else if(viewIsPartiallyHiddenBottom(height)){
            percents=mCurrentViewRect.bottom*100/height;
        }
        setVisibilityPercentsText(view,percents);
        return percents;
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        VideoListAdapter.VideoViewHolder videoViewHolder= (VideoListAdapter.VideoViewHolder) newActiveView.getTag();
        playNewVideo(new CurrentItemMetaData(newActiveViewPosition,newActiveView),videoViewHolder.getItemVideoVpvPlayer(),mVideoPlayerManager);
    }


    @Override
    public void deactivate(View currentView, int position) {
        stopPlayback(mVideoPlayerManager);
    }
    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
    private void setVisibilityPercentsText(View currentView, int percents){
        VideoListAdapter.VideoViewHolder vh= (VideoListAdapter.VideoViewHolder) currentView.getTag();
        vh.getItemVideoTvPercents().setText("可视百分比:"+percents);
    }
    // 顶部出现
    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }

    // 底部出现
    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }
}
