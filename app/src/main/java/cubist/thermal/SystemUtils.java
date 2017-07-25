package cubist.thermal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 15:50:31 - 14.07.2010
 */
public class SystemUtils {
    // ===========================================================
    // Constants
    // ===========================================================

    public static final boolean SDK_VERSION_ECLAIR_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
    public static final boolean SDK_VERSION_FROYO_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    public static final boolean SDK_VERSION_GINGERBREAD_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    public static final boolean SDK_VERSION_HONEYCOMB_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static final boolean SDK_VERSION_ICE_CREAM_SANDWICH_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    private static final String BOGOMIPS_PATTERN = "BogoMIPS[\\s]*:[\\s]*(\\d+\\.\\d+)[\\s]*\n";
    private static final String MEMTOTAL_PATTERN = "MemTotal[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";
    private static final String MEMFREE_PATTERN = "MemFree[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public static boolean isGoogleTV(final Context pContext) {
        return SystemUtils.hasSystemFeature(pContext, "com.google.android.tv");
    }

    public static int getPackageVersionCode(final Context pContext) {
        return SystemUtils.getPackageInfo(pContext).versionCode;
    }

    public static String getPackageVersionName(final Context pContext) {
        return SystemUtils.getPackageInfo(pContext).versionName;
    }

    public static String getPackageName(final Context pContext) {
        return pContext.getPackageName();
    }

    public static String getApkFilePath(final Context pContext) throws PackageManager.NameNotFoundException {
        final PackageManager packMgmr = pContext.getPackageManager();
        return packMgmr.getApplicationInfo(SystemUtils.getPackageName(pContext), 0).sourceDir;
    }

    private static PackageInfo getPackageInfo(final Context pContext) {
        try {
            return pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            // Debug.e(e);
            return null;
        }
    }

    public static boolean hasSystemFeature(final Context pContext, final String pFeature) {
        try {
            final Method PackageManager_hasSystemFeatures = PackageManager.class.getMethod("hasSystemFeature", new Class[] { String.class });
            return (PackageManager_hasSystemFeatures == null) ? false : (Boolean) PackageManager_hasSystemFeatures.invoke(pContext.getPackageManager(), pFeature);
        } catch (final Throwable t) {
            return false;
        }
    }

    /**
     * @param pBuildVersionCode taken from {@link Build.VERSION_CODES}.
     */
    public static boolean isAndroidVersionOrLower(final int pBuildVersionCode) {
        return Build.VERSION.SDK_INT <= pBuildVersionCode;
    }

    /**
     * @param pBuildVersionCode taken from {@link Build.VERSION_CODES}.
     */
    public static boolean isAndroidVersionOrHigher(final int pBuildVersionCode) {
        return Build.VERSION.SDK_INT >= pBuildVersionCode;
    }

    /**
     * @param pBuildVersionCodeMin taken from {@link Build.VERSION_CODES}.
     * @param pBuildVersionCodeMax taken from {@link Build.VERSION_CODES}.
     */
    public static boolean isAndroidVersion(final int pBuildVersionCodeMin, final int pBuildVersionCodeMax) {
        return (Build.VERSION.SDK_INT >= pBuildVersionCodeMin) && (Build.VERSION.SDK_INT <= pBuildVersionCodeMax);
    }

    public static float getCPUBogoMips() throws SystemUtilsException {
        final MatchResult matchResult = SystemUtils.matchSystemFile("/proc/cpuinfo", SystemUtils.BOGOMIPS_PATTERN, 1000);

        try {
            if(matchResult.groupCount() > 0) {
                return Float.parseFloat(matchResult.group(1));
            } else {
                throw new SystemUtilsException();
            }
        } catch (final NumberFormatException e) {
            throw new SystemUtilsException(e);
        }
    }

    /**
     * @return in kiloBytes.
     * @throws SystemUtilsException
     */
    public static int getMemoryTotal() throws SystemUtilsException {
        final MatchResult matchResult = SystemUtils.matchSystemFile("/proc/meminfo", SystemUtils.MEMTOTAL_PATTERN, 1000);

        try {
            if(matchResult.groupCount() > 0) {
                return Integer.parseInt(matchResult.group(1));
            } else {
                throw new SystemUtilsException();
            }
        } catch (final NumberFormatException e) {
            throw new SystemUtilsException(e);
        }
    }

    /**
     * @return in kiloBytes.
     * @throws SystemUtilsException
     */
    public static int getMemoryFree() throws SystemUtilsException {
        final MatchResult matchResult = SystemUtils.matchSystemFile("/proc/meminfo", SystemUtils.MEMFREE_PATTERN, 1000);

        try {
            if(matchResult.groupCount() > 0) {
                return Integer.parseInt(matchResult.group(1));
            } else {
                throw new SystemUtilsException();
            }
        } catch (final NumberFormatException e) {
            throw new SystemUtilsException(e);
        }
    }

    /**
     * @return in degree of Celsius
     * @throws SystemUtilsException
     */
    public static int getCPUTemperatureCurrent() throws SystemUtilsException {
        return SystemUtils.readSystemFileAsInt("/sys/class/thermal/thermal_zone9/temp");
    }

    /**
     * @return in kiloHertz.
     * @throws SystemUtilsException
     */
    public static int getCPUFrequencyCurrent(int k) throws SystemUtilsException {
        int f = 0;
        try {
            f = SystemUtils.readSystemFileAsInt(
                    String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", k));
        } catch (SystemUtils.SystemUtilsException e) {
            // e.printStackTrace();
        }
        return f;
    }

    /**
     * @return in kiloHertz.
     * @throws SystemUtilsException
     */
    public static int getCPUFrequencyMin() throws SystemUtilsException {
        return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
    }

    /**
     * @return in kiloHertz.
     * @throws SystemUtilsException
     */
    public static int getCPUFrequencyMax() throws SystemUtilsException {
        return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
    }

    /**
     * @return in kiloHertz.
     * @throws SystemUtilsException
     */
    public static int getCPUFrequencyMinScaling() throws SystemUtilsException {
        return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
    }

    /**
     * @return in kiloHertz.
     * @throws SystemUtilsException
     */
    public static int getCPUFrequencyMaxScaling() throws SystemUtilsException {
        return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
    }

    private static MatchResult matchSystemFile(final String pSystemFile, final String pPattern, final int pHorizon) throws SystemUtilsException {
        InputStream in = null;
        try {
            final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

            in = process.getInputStream();
            final Scanner scanner = new Scanner(in);

            final boolean matchFound = scanner.findWithinHorizon(pPattern, pHorizon) != null;
            if(matchFound) {
                return scanner.match();
            } else {
                throw new SystemUtilsException();
            }
        } catch (final IOException e) {
            throw new SystemUtilsException(e);
        } finally {
            StreamUtils.close(in);
        }
    }

    private static int readSystemFileAsInt(final String pSystemFile) throws SystemUtilsException {
        InputStream in = null;
        try {
            final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

            in = process.getInputStream();
            final String content = StreamUtils.readFully(in);
            return Integer.parseInt(content.trim().replaceAll("\"", "").trim());
        } catch (final IOException | NumberFormatException e) {
            throw new SystemUtilsException(e);
        } finally {
            StreamUtils.close(in);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static class SystemUtilsException extends Exception {
        // ===========================================================
        // Constants
        // ===========================================================

        private static final long serialVersionUID = -7256483361095147596L;

        // ===========================================================
        // Methods
        // ===========================================================

        public SystemUtilsException() {

        }

        public SystemUtilsException(final Throwable pThrowable) {
            super(pThrowable);
        }
    }
}
