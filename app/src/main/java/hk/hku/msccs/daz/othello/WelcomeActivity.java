/*
 * @author WANG Dazhi
 * @since 1st Nov, 2015
 * @version 1.0.0
 */
package hk.hku.msccs.daz.othello;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class WelcomeActivity extends Activity {
    public static final int TIME_OUT_SECONDS = 3;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);
        mContext = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        }, TIME_OUT_SECONDS * 1000);


    }



}
