package text.qiao.com.listvideostudy;


import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoItem;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import text.qiao.com.listvideostudy.item.LocalVideoListItem;
import text.qiao.com.listvideostudy.item.OnlineVideoListItem;
import text.qiao.com.listvideostudy.item.VideoListItem;
import text.qiao.com.utilslibrary.utils.log.LogUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoListFragment extends Fragment {
    public static final String VIDEO_TYPE_ARG = "me.chunyu.spike.video_list_fragment.video_type_arg";
    @BindView(R.id.video_list_rv_list)
    RecyclerView videoListRvList;
    Unbinder unbinder;

    // 网络视频地址
    private static final String URL =
            "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4";

    // 本地资源文件名
    private static final String[] LOCAL_NAMES = new String[]{
            "chunyu-local-1.mp4",
            "chunyu-local-2.mp4",
            "chunyu-local-3.mp4",
            "chunyu-local-4.mp4"
    };

    // 在线资源名
    private static final String ONLINE_NAME = "chunyu-online";
    /**
     * 视频列表
     */
    private List<VideoListItem> mList;
    /**
     * 可视估值器 用于判断列表中那个视频是最佳播放项
     */
    private ListItemsVisibilityCalculator mVisibilityCalculator;

    /**
     *  视频播放管理器
     */
    private VideoPlayerManager mVideoPlayerManager;
    private LinearLayoutManager linearLayoutManager;
    private int mScrollerState;
    private VideoListAdapter videoListAdapter;
    /**
     * 位置提取器
     */
    private ItemsPositionGetter itemsPositionGetter;

    private boolean iskai=false;
    public VideoListFragment() {
//        CustomDialog
        mList = new ArrayList<>();
        mVisibilityCalculator = new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);
        mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
            @Override
            public void onPlayerItemChanged(MetaData currentItemMetaData) {

            }
        });
        //暂停滚动状态
        mScrollerState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    }

    public static VideoListFragment newInstance(int type) {
        VideoListFragment videoListFragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putInt(VIDEO_TYPE_ARG, type);
        videoListFragment.setArguments(args);
        return videoListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLocalVideoList();
        Bundle args = getArguments();
        if (args != null && args.getInt(VIDEO_TYPE_ARG) == MainActivity.ONLINE) {
            initOnlineVideoList();
        }
        videoListRvList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        videoListRvList.setLayoutManager(linearLayoutManager);
         videoListAdapter = new VideoListAdapter(mList,getContext());
        videoListRvList.setAdapter(videoListAdapter);
        itemsPositionGetter = new RecyclerViewItemPositionGetter(linearLayoutManager, videoListRvList);
        videoListRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                LogUtil.e("onScrollStateChanged");
                mScrollerState = newState;
                if (mScrollerState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()) {
                    mVisibilityCalculator.onScrollStateIdle(itemsPositionGetter, linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition());
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                LogUtil.e("onScrolled");
                if (!mList.isEmpty()) {
                    mVisibilityCalculator.onScroll(itemsPositionGetter, linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollerState);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("onResume");
        if(iskai){
            videoListAdapter.notifyDataSetChanged();
        }
        if(!mList.isEmpty()){
            videoListRvList.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("linearLayoutManager.findFirstVisibleItemPosition()"+linearLayoutManager.findFirstVisibleItemPosition());
                    LogUtil.e("linearLayoutManager.findLastVisibleItemPosition()"+linearLayoutManager.findLastVisibleItemPosition());
                    mVisibilityCalculator.onScrollStateIdle(itemsPositionGetter, linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition());
                }
            });
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.e("onStop");
        mVideoPlayerManager.resetMediaPlayer();
        iskai=true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // 初始化在线视频, 需要缓冲
    private void initOnlineVideoList() {
        final int count = 10;
        for (int i = 0; i < count; ++i) {
            mList.add(new OnlineVideoListItem(mVideoPlayerManager, ONLINE_NAME, R.drawable.cover, URL));
        }
    }

    // 初始化本地视频
    private void initLocalVideoList() {
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[0], R.drawable.cover, getFile(LOCAL_NAMES[0])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[1], R.drawable.cover, getFile(LOCAL_NAMES[1])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[2], R.drawable.cover, getFile(LOCAL_NAMES[2])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[3], R.drawable.cover, getFile(LOCAL_NAMES[3])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[0], R.drawable.cover, getFile(LOCAL_NAMES[0])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[1], R.drawable.cover, getFile(LOCAL_NAMES[1])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[2], R.drawable.cover, getFile(LOCAL_NAMES[2])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[3], R.drawable.cover, getFile(LOCAL_NAMES[3])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[0], R.drawable.cover, getFile(LOCAL_NAMES[0])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[1], R.drawable.cover, getFile(LOCAL_NAMES[1])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[2], R.drawable.cover, getFile(LOCAL_NAMES[2])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[3], R.drawable.cover, getFile(LOCAL_NAMES[3])));
    }

    // 获取资源文件
    private AssetFileDescriptor getFile(String name) {
        try {
            return getActivity().getAssets().openFd(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
