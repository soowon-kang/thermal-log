package cubist.thermal;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by CubePenguin on 2017. 5. 7..
 */

class BackgroundTask extends AsyncTask<Double, Void, String> {

    private long rxBytes = 0;
    private long txBytes = 0;
    private double batTemp = 0.0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        rxBytes = android.net.TrafficStats.getTotalRxBytes();
        txBytes = android.net.TrafficStats.getTotalTxBytes();
    }

    @Override
    protected String doInBackground(Double... params) {
        batTemp = params[0];
        int[] temp = getCpuUsageStatistic();
        return Arrays.toString(temp);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i("cpu", s);
        // Log.d("bytes Rx", Long.toString(android.net.TrafficStats.getTotalRxBytes() - rxBytes));
        // Log.d("bytes Tx", Long.toString(android.net.TrafficStats.getTotalTxBytes() - txBytes));
        // Log.d("bat Temp", Double.toString(batTemp));
        super.onPostExecute(s);
    }

    /**
     * @return integer Array with 4 elements: user, system, idle and other cpu
     *         usage in percentage.
     */
    private int[] getCpuUsageStatistic() {

        String[] temp = executeTop();
        String tempString = temp[0];
        int size = 0;

        tempString = tempString.replaceAll(",", "");
        tempString = tempString.replaceAll("User", "");
        tempString = tempString.replaceAll("System", "");
        tempString = tempString.replaceAll("IOW", "");
        tempString = tempString.replaceAll("IRQ", "");
        tempString = tempString.replaceAll("%", "");
        for (int i = 0; i < 10; i++) {
            tempString = tempString.replaceAll("  ", " ");
        }
        tempString = tempString.trim();
        String[] myString = tempString.split(" ");
        size += myString.length;
        int[] cpu0 = new int[myString.length];
        for (int i = 0; i < myString.length; i++) {
            myString[i] = myString[i].trim();
            cpu0[i] = Integer.parseInt(myString[i]);
        }
        // ex) User 31%, System 10%, IOW 0%, IRQ 0%

        tempString = temp[1];
        tempString = tempString.replaceAll("\\+", "=");
        tempString = tempString.trim();
        myString = tempString.split("=");
        size += myString.length;
        int[] cpu1 = new int[myString.length];
        for (int i = 0; i < myString.length-1; i++) {
            myString[i] = myString[i].trim().split(" ")[1].trim();
            cpu1[i] = Integer.parseInt(myString[i]);
        }
        cpu1[myString.length-1] = Integer.parseInt(myString[myString.length-1].trim());
        // ex) User 211 + Nice 0 + Sys 156 + Idle 1424 + IOW 0 + IRQ 7 + SIRQ 3 = 1801

        tempString = temp[2];
        size += 4;
        int[] cpu2 = new int[4];
        myString = tempString.split("%");
        tempString = myString[1];
        myString = myString[0].split(" ");
        cpu2[0] = Integer.parseInt(myString[myString.length-1].trim());
        myString = tempString.split("K");
        cpu2[3] = Integer.parseInt(myString[1].trim());
        tempString = myString[0];
        for (int i = 0; i < 10; i++) {
            tempString = tempString.replaceAll("  ", " ");
        }
        myString = tempString.split(" ");
        cpu2[2] = Integer.parseInt(myString[myString.length-1].trim());
        cpu2[1] = Integer.parseInt(myString[myString.length-2].trim());
        // Log.i("cpu1", Arrays.toString(cpu1));

        // ex)  PID PR CPU% S  #THR     VSS     RSS PCY UID      Name
        // ex) 3031  4   0% R     1   6000K   1424K  fg shell    top
        // Log.i("cpu2", Arrays.toString(cpu2));

        size += 5;
        int[] data = new int[size];
        int idx = 0;
        for (;idx < cpu0.length; idx++) {
            data[idx] = cpu0[idx];
        }
        for (int t = idx; idx < t + cpu1.length; idx++) {
            data[idx] = cpu1[idx-t];
        }
        for (int t = idx; idx < t + cpu2.length; idx++) {
            data[idx] = cpu2[idx-t];
        }
        try {
            // Log.i("cpuFreq", Integer.toString(SystemUtils.getCPUFrequencyCurrent()));
            data[idx++] = SystemUtils.getCPUFrequencyCurrent();
        } catch (SystemUtils.SystemUtilsException e) {
            e.printStackTrace();
        }
        data[idx++] = (int)(android.net.TrafficStats.getTotalTxBytes() - txBytes);
        data[idx++] = (int)(android.net.TrafficStats.getTotalRxBytes() - rxBytes);
        data[idx++] = (int) batTemp;
        try {
            data[idx] = SystemUtils.getCPUTemperatureCurrent();
        } catch (SystemUtils.SystemUtilsException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String[] executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String[] returnString = new String[3];
        try {
            p = Runtime.getRuntime().exec("top -n 1 -m 1 -s cpu");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString[0] == null || returnString[0].contentEquals("")) {
                returnString[0] = in.readLine();
                // Log.d("net", returnString);
            }
            while (returnString[1] == null || returnString[1].contentEquals("")) {
                returnString[1] = in.readLine();
            }
            while (returnString[2] == null || returnString[2].contentEquals("")) {
                returnString[2] = in.readLine();
            }
            returnString[2] = in.readLine();
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
    }


}
