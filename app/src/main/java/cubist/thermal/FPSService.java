package cubist.thermal;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Choreographer;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by seanparksy on 17. 7. 27.
 */

public class FPSService extends Service{
    String TAG = FPSService.class.getSimpleName();
    Choreographer mChoreo;
    Choreographer.FrameCallback mFrameCallback;
    long mStartT;
    final long onesec = 1000000000;
    int mCurFPS = 0;
    int mFrameCount = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStartT = System.nanoTime();
        mChoreo = Choreographer.getInstance();
        mFrameCallback = new Choreographer.FrameCallback(){
            @Override
            public void doFrame(long frameTimeNanos) {
                long curT = System.nanoTime();
                mFrameCount += 1;
                if (curT - mStartT >= onesec){
                    mCurFPS = mFrameCount;
                    Log.d("DEBUG", "FPS: " + mCurFPS);
                    mStartT = curT;
                    mFrameCount = 0;
                }
                mChoreo.postFrameCallback(mFrameCallback);
            }
        };
        mChoreo.postFrameCallback(mFrameCallback);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /** # sypark.
     * This is original file logging method.
     * Make another one by referring this.
     */

}
