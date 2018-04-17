package zplh_android_yk.zplh.com.yk_20.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yong hao zeng on 2018/4/16/016.
 */
public abstract class BaseUI extends AppCompatActivity {

    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        bind = ButterKnife.bind(this);
        initView();
        initData();
    }

    protected abstract void initData();

    protected abstract void initView();



    abstract @LayoutRes int getLayoutID();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null) bind.unbind();
    }
}
