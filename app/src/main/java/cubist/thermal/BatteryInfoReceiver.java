package cubist.thermal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by CubePenguin on 2017. 5. 7..
 */

public class BatteryInfoReceiver extends BroadcastReceiver {

    int temp = 0;

    double get_temp(){
        return temp / 10.;
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {

        temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);

    }

};
