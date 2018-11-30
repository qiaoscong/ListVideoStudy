package text.qiao.com.listvideostudy;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import text.qiao.com.utilslibrary.utils.log.LogUtil;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_t_toolbar)
    Toolbar mainTToolbar;
    @BindView(R.id.main_fl_container)
    FrameLayout mainFlContainer;

    /**
     * 本地
     */
    public static final int LOCAL = 0;
    /**
     * 在线
     */
    public static final int ONLINE = 1;

    private FragmentManager mFM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_local_video:

                if(!item.isChecked()){
                    mFM.beginTransaction().replace(R.id.main_fl_container, VideoListFragment.newInstance(LOCAL)).commit();
                }
                break;
            case R.id.enable_online_video:
                if(!item.isChecked()){
                    mFM.beginTransaction().replace(R.id.main_fl_container, VideoListFragment.newInstance(ONLINE)).commit();
                }
                break;
            default:

                break;
        }
        item.setChecked(!item.isChecked());
        return true;
    }

    private void init(Bundle savedInstanceState) {
        mainTToolbar.setTitle("播放列表");
        setSupportActionBar(mainTToolbar);
        mFM = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFM.beginTransaction().replace(R.id.main_fl_container, VideoListFragment.newInstance(LOCAL)).commit();
        }

    }
}
