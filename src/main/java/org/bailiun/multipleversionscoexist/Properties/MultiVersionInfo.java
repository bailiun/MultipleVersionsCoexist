package org.bailiun.multipleversionscoexist.Properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bailiun.multipleversionscoexist.en.VersionInfoList;
import org.bailiun.multipleversionscoexist.en.VersionMeta;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>多版本信息配置类 / Multi-Version Information Configuration</h2>
 *
 * <p><b>中文说明：</b><br>
 * 该类用于加载并管理多版本系统中的版本信息配置。它通过
 * {@link ConfigurationProperties} 注解从配置文件中读取以
 * <code>multi.version.info</code> 为前缀的属性，并封装为
 * {@link VersionMeta} 对象列表。
 * <br>配置加载完成后，会在初始化阶段（{@link PostConstruct}）
 * 自动转换为自定义集合 {@link VersionInfoList}，方便后续逻辑使用。</p>
 *
 * <p><b>English Description:</b><br>
 * This class loads and manages version information for the multi-version system.
 * It is bound to properties with the prefix <code>multi.version.info</code> and
 * maps them into {@link VersionMeta} objects. After initialization, the data is
 * wrapped into a {@link VersionInfoList} for enhanced management and lookup support.</p>
 *
 * <p><b>Usage Example / 使用示例：</b></p>
 * <pre>
 * multi:
 *   version:
 *     info:
 *       start: true
 *       version-info-list:
 *         - name: v1.0
 *           author: bailiun
 *           desc: 初始版本
 *           enabled: true
 *           created: 2025-01-01
 * </pre>
 *
 * @author bailiun
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "multi.info")
public class MultiVersionInfo {

    /**
     * <p><b>中文：</b>是否开启版本信息显示功能。</p>
     * <p><b>English:</b> Whether to enable version information display.</p>
     */
    private boolean start;

    /**
     * <p><b>中文：</b>从配置文件中加载的版本信息列表。</p>
     * <p><b>English:</b> The version information list loaded from the configuration file.</p>
     */
    private List<VersionMeta> versionInfoList;

    /**
     * <p><b>中文：</b>包装后的版本信息容器，用于更方便地进行版本管理。</p>
     * <p><b>English:</b> A wrapped {@link VersionInfoList} containing all version metadata.</p>
     */
    @JsonIgnore
    private VersionInfoList<VersionMeta> versionsInfo;

    /**
     * 获取包装后的版本信息列表 / Get the wrapped version info list
     */
    public VersionInfoList<VersionMeta> getVersionsInfo() {
        return versionsInfo;
    }

    /**
     * 初始化逻辑 / Initialization Logic
     *
     * <p><b>中文说明：</b><br>
     * 在 Spring 初始化阶段执行，将原始的 {@code versionInfoList}
     * 自动封装为 {@link VersionInfoList}，以便后续统一管理。</p>
     *
     * <p><b>English Description:</b><br>
     * Executed during Spring's initialization phase to wrap
     * {@code versionInfoList} into a {@link VersionInfoList} for consistent access.</p>
     */
    @PostConstruct
    public void init() {
        versionsInfo = new VersionInfoList<>();
        versionsInfo.addAll(versionInfoList);
    }

    /**
     * 默认构造函数 / Default Constructor
     *
     * <p>初始化默认配置，关闭信息展示功能，并创建空的版本列表。</p>
     */
    public MultiVersionInfo() {
        this.start = false;
        this.versionInfoList = new ArrayList<>();
    }

    /**
     * 打印所有版本信息 / Print All Version Information
     *
     * <p><b>中文说明：</b><br>
     * 遍历 {@link VersionInfoList} 中的所有版本并打印其详细信息。</p>
     *
     * <p><b>English Description:</b><br>
     * Prints all version metadata contained in {@link VersionInfoList}.</p>
     */
    public void print() {
        for (VersionMeta a : versionsInfo) {
            System.out.println(a);
        }
    }

    public boolean isStart() {
        return start;
    }
    public void setStart(boolean start) {
        this.start = start;
    }
    public List<VersionMeta> getVersionInfoList() {
        return versionInfoList;
    }
    public void setVersionsInfo(VersionInfoList<VersionMeta> versionsInfo) {
        this.versionsInfo = versionsInfo;
    }
    public void setVersionInfoList(List<VersionMeta> versionInfoList) {
        this.versionInfoList = versionInfoList;
    }
}
