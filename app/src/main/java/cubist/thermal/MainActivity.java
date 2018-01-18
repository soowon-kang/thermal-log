package cubist.thermal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Choreographer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.codemonkeylabs.fpslibrary.FrameDataCallback;
import com.codemonkeylabs.fpslibrary.TinyDancer;

public class MainActivity extends AppCompatActivity {

    // public BatteryInfoReceiver mBatInfoReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.i("action", "temp");
                getFPSandCpuFrequency();
                //BackgroundTask bgTask = new BackgroundTask();
                //bgTask.execute(mBatInfoReceiver.get_temp());
                // Log.i("batTemp", Double.toString(mBatInfoReceiver.get_temp()));
            }
        });

        /*
        mBatInfoReceiver = new BatteryInfoReceiver();

        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        */

        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        TempService.class);
                startService(intent);
                //startService(new Intent(getApplicationContext(), FPSService.class));
                Log.d("service", "start");
            }
        });

        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        TempService.class);
                stopService(intent);
                //stopService(new Intent(getApplicationContext(), FPSService.class));
                Log.d("service", "stop");
            }
        });
        //TinyDancer.create().show(this);

        //alternatively
        /*
        TinyDancer.create()
                .redFlagPercentage(.1f) // set red indicator for 10%....different from default
                .startingXPosition(200)
                .startingYPosition(600)
                .show(this);
        */
        //you can add a callback to get frame times and the calculated
        //number of dropped frames within that window
        Log.d("DEBUG", "It's new");
        /*
        TinyDancer.create()
                .addFrameDataCallback(new FrameDataCallback() {
                    @Override
                    public void doFrame(long previousFrameNS, long currentFrameNS, int droppedFrames) {
                        //collect your stats here
                        Log.d("DEBUG", "frame got");
                    }
                })
                .show(this);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** # sypark.
     * This is a test function for Nexus5X.
     * You can get cpu frequency of each cpu by using SystemUtils.getCPUFrequencyCurrent(k)
     *
     * To get the FPS info, refer to here.
     * https://developer.android.com/training/testing/performance.html
     * http://www.kmshack.kr/2013/12/android-dumpsys-gfxinfo%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%ED%94%84%EB%A0%88%EC%9E%84-%EC%B8%A1%EC%A0%95/
     *
     */
    private Double[] getFPSandCpuFrequency() {
        try {
            Log.i("cpuFreq0", Integer.toString(SystemUtils.getCPUFrequencyCurrent(0)));
            Log.i("cpuFreq1", Integer.toString(SystemUtils.getCPUFrequencyCurrent(1)));
            Log.i("cpuFreq2", Integer.toString(SystemUtils.getCPUFrequencyCurrent(2)));
            Log.i("cpuFreq3", Integer.toString(SystemUtils.getCPUFrequencyCurrent(3)));
            Log.i("cpuFreq4", Integer.toString(SystemUtils.getCPUFrequencyCurrent(4)));
            Log.i("cpuFreq5", Integer.toString(SystemUtils.getCPUFrequencyCurrent(5)));
        } catch (SystemUtils.SystemUtilsException e) {
            e.printStackTrace();
        }
        Double[] data = new Double[7];

        return data;
    }
}
