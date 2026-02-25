package com.grape.grape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 投屏功能配置
 */
@Configuration
@ConfigurationProperties(prefix = "screen-mirror")
public class ScreenMirrorConfig {

    // ========== 视频质量配置 ==========

    /**
     * 视频分辨率（格式：宽x高）
     * 推荐值：
     * - 高质量: 1080x1920
     * - 标准: 720x1280 (默认)
     * - 流畅: 540x960
     * - 极速: 360x640
     */
    private String videoSize = "720x1280";

    /**
     * 视频比特率（bps）
     * 推荐值：
     * - 高质量: 8000000 (8Mbps)
     * - 标准: 4000000 (4Mbps，默认)
     * - 流畅: 2000000 (2Mbps)
     * - 极速: 1000000 (1Mbps)
     */
    private int bitRate = 4000000;

    // ========== 性能配置 ==========

    /**
     * 缓冲区大小（字节）
     * 推荐值：
     * - 低延迟: 4096 (4KB)
     * - 默认: 8192 (8KB)
     * - 高吞吐: 16384 (16KB)
     */
    private int bufferSize = 8192;

    /**
     * WebSocket 超时时间（毫秒）
     * 默认：480000 (8分钟)
     */
    private int timeout = 480000;

    /**
     * 端口范围起始
     */
    private int portRangeStart = 20000;

    /**
     * 端口范围大小
     */
    private int portRangeSize = 10000;

    // ========== 功能开关 ==========

    /**
     * 是否使用 ADB screenrecord（支持 Android 15）
     * true: 使用 adb screenrecord（推荐）
     * false: 使用 scrcpy-server（仅支持 Android 14 及以下）
     */
    private boolean useAdbScreenrecord = true;

    // ========== Getter/Setter ==========

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getPortRangeStart() {
        return portRangeStart;
    }

    public void setPortRangeStart(int portRangeStart) {
        this.portRangeStart = portRangeStart;
    }

    public int getPortRangeSize() {
        return portRangeSize;
    }

    public void setPortRangeSize(int portRangeSize) {
        this.portRangeSize = portRangeSize;
    }

    public boolean isUseAdbScreenrecord() {
        return useAdbScreenrecord;
    }

    public void setUseAdbScreenrecord(boolean useAdbScreenrecord) {
        this.useAdbScreenrecord = useAdbScreenrecord;
    }
}
