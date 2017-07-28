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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by seanparksy on 17. 7. 27.
 */

public class FPSService extends Service{
    final static String TAG = FPSService.class.getSimpleName();
    Choreographer mChoreo;
    Choreographer.FrameCallback mFrameCallback;
    long mStartT;
    final long onesec = 1000000000;
    volatile float mCurFPS = 0;
    volatile int mFrameCount = 0;
    volatile boolean running = true;
    Thread thread;


    final int timeWindow = 2000; //ms
    final int timeWindowSec = timeWindow/1000; //s
    private TimerTask mTimerTask;
    private Timer mTimer;
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
                //long curT = System.nanoTime();
                mFrameCount += 1;
                /*
                if (curT - mStartT >= onesec){
                    mCurFPS = mFrameCount;
                    Log.d("DEBUG", "FPS: " + mCurFPS);
                    mStartT = curT;
                    mFrameCount = 0;
                }
                */
                //Log.d(TAG, "adding: framecount = " + mFrameCount);
                mChoreo.postFrameCallback(mFrameCallback);
            }
        };
        mChoreo.postFrameCallback(mFrameCallback);
        mTimerTask = new calcFPS();
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, timeWindow);
        logging();
    }
    class calcFPS extends TimerTask {
        @Override
        public void run(){
            //Log.d(TAG, "calculating: framecount = " + mFrameCount);
            mCurFPS = (float) mFrameCount/timeWindowSec;
            mFrameCount = 0;
        }
    }

    public float getFPS(){
        return mCurFPS;
    }

    private void logging() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (running) {
                        Thread.sleep(1000);
                        Log.d(TAG, "" + getFPS());
                    }
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        running = false;
        super.onDestroy();
    }

    /** # sypark.
     * This is original file logging method.
     * Make another one by referring this.
     */

}
