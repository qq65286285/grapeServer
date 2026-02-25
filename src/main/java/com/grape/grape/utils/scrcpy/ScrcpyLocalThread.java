package com.grape.grape.utils.scrcpy;

import com.alibaba.fastjson.JSONObject;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.grape.grape.utils.common.BytesTool.sendText;

/**
 * 启动 scrcpy 等服务的线程（来自 screen-tools）
 */
public class ScrcpyLocalThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(ScrcpyLocalThread.class);

    public final static String ANDROID_START_MINICAP_SERVER_PRE = "android-scrcpy-start-scrcpy-server-task-%s-%s-%s";

    private IDevice iDevice;
    private int finalC;
    private Session session;
    private String udId;
    private AndroidTestTaskBootThread androidTestTaskBootThread;
    private Semaphore isFinish = new Semaphore(0);

    public ScrcpyLocalThread(IDevice iDevice, int finalC, Session session, AndroidTestTaskBootThread androidTestTaskBootThread) {
        this.iDevice = iDevice;
        this.finalC = finalC;
        this.session = session;
        this.udId = iDevice.getSerialNumber();
        this.androidTestTaskBootThread = androidTestTaskBootThread;
        this.setDaemon(true);
        this.setName(androidTestTaskBootThread.formatThreadName(ANDROID_START_MINICAP_SERVER_PRE));
    }

    public IDevice getiDevice() {
        return iDevice;
    }

    public int getFinalC() {
        return finalC;
    }

    public Session getSession() {
        return session;
    }

    public String getUdId() {
        return udId;
    }

    public AndroidTestTaskBootThread getAndroidTestTaskBootThread() {
        return androidTestTaskBootThread;
    }

    public Semaphore getIsFinish() {
        return isFinish;
    }

    @Override
    public void run() {
        // 使用 sonic-android-scrcpy.jar（支持 Android 15 之前的版本）
        File scrcpyServerFile = findScrcpyJar();
        if (scrcpyServerFile == null || !scrcpyServerFile.exists()) {
            log.error("未找到 sonic-android-scrcpy.jar");
            return;
        }

        try {
            log.info("推送 scrcpy jar 到设备: {}", scrcpyServerFile.getAbsolutePath());
            iDevice.pushFile(scrcpyServerFile.getAbsolutePath(), "/data/local/tmp/sonic-android-scrcpy.jar");
        } catch (Exception e) {
            log.error("推送失败", e);
        }

        AtomicBoolean isRetry = new AtomicBoolean(false);
        try {
            log.info("启动 scrcpy 服务，设备: {}", udId);
            iDevice.executeShellCommand("CLASSPATH=/data/local/tmp/sonic-android-scrcpy.jar app_process / com.genymobile.scrcpy.Server 1.23 log_level=info max_size=0 max_fps=60 tunnel_forward=true send_frame_meta=false control=false show_touches=false stay_awake=false power_off_on_close=false clipboard_autosync=false",
                    new IShellOutputReceiver() {
                        @Override
                        public void addOutput(byte[] bytes, int i, int i1) {
                            String res = new String(bytes, i, i1);
                            log.info("scrcpy: {}", res);
                            if (res.contains("Device")) {
                                isFinish.release();
                                isRetry.set(true);
                            } else if (!isRetry.get() && res.contains("ERROR")) {
                                log.error("scrcpy 服务启动失败！");
                                JSONObject support = new JSONObject();
                                support.put("msg", "support");
                                support.put("text", "scrcpy 服务启动失败！");
                                sendText(session, support.toJSONString());
                            }
                        }

                        @Override
                        public void flush() {
                        }

                        @Override
                        public boolean isCancelled() {
                            return false;
                        }
                    }, 0, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.info("{} scrcpy service stopped.", iDevice.getSerialNumber());
            log.error(e.getMessage());
        }
    }

    /**
     * 查找 scrcpy jar 文件（跨平台）
     */
    private File findScrcpyJar() {
        String basePath = System.getProperty("user.dir");
        String[] possiblePaths = {
                basePath + "/grape/src/main/resources/scrcpy/sonic-android-scrcpy.jar",
                basePath + "/src/main/resources/scrcpy/sonic-android-scrcpy.jar",
                "plugins/sonic-android-scrcpy.jar",
                "./sonic-android-scrcpy.jar"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                log.info("找到 scrcpy jar: {}", file.getAbsolutePath());
                return file;
            }
        }

        log.error("未找到 sonic-android-scrcpy.jar，尝试的路径：");
        for (String path : possiblePaths) {
            log.error("  - {}", path);
        }
        return null;
    }
}
