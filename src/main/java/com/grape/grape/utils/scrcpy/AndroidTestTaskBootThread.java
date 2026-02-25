package com.grape.grape.utils.scrcpy;

/**
 * 简化版 AndroidTestTaskBootThread
 * 只提供线程命名功能
 */
public class AndroidTestTaskBootThread extends Thread {

    public static final String ANDROID_TEST_TASK_BOOT_PRE = "android-test-task-boot-%s-%s-%s";

    private int resultId = 0;
    private int caseId = 0;
    private String udId;

    public AndroidTestTaskBootThread setUdId(String udId) {
        this.udId = udId;
        return this;
    }

    public String formatThreadName(String baseFormat) {
        return String.format(baseFormat, this.resultId, this.caseId, this.udId);
    }

    public String getUdId() {
        return udId;
    }
}
