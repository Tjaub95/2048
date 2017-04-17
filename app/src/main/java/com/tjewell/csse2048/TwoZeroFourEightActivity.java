package com.tjewell.csse2048;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

/**
 * Created by Tyler_Jewell on 4/13/17.
 */

public class TwoZeroFourEightActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_zero_four_eight);

        TwoZeroFourEightFragment frag = new TwoZeroFourEightFragment();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.grid_twofourzeroeight, frag)
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
