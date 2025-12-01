package org.bailiun.multipleversionscoexist.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


import java.util.ArrayList;
import java.util.List;

/**
 * <h2>多版本管理配置 / Multi-Version Management Configuration</h2>
 *
 * <p><b>中文说明：</b><br>
 * 该配置类用于控制多版本接口注册行为，包括启用状态、版本白名单/黑名单、
 * 最大注册数量、排序方式等。通过在 <code>application.properties</code>
 * 或 <code>application.yml</code> 中设置 <code>multi.version.properties.*</code> 前缀的属性进行配置。</p>
 *
 * <p><b>English Description:</b><br>
 * This configuration class controls the multi-version API registration behavior.
 * It defines whether versioning is enabled, which versions to include or exclude,
 * how many versions can be registered at most, and the sorting rule of version order.
 * Configure it using properties under prefix <code>multi.version.properties.*</code>
 * in <code>application.properties</code> or <code>application.yml</code>.</p>
 *
 * @author Bailiun
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "multi.version")
public class MultiVersionProperties {
    /**
     * 是否启用多版本注册。
     * <br>Whether to enable multi-version registration.
     */
    private boolean start;
    /**
     * 指定允许注册的版本白名单。
     * <br>Specify the list of versions allowed to be registered (whitelist).
     */
    private List<String> Include;
    /**
     * 指定禁止注册的版本黑名单。
     * <br>Specify the list of versions to be excluded (blacklist).
     * <b>Note:</b> If a version exists in both {@link #Include} and {@link #exclude},
     * the blacklist takes higher priority.
     */
    private List<String> exclude;
    /**
     * 限制最多注册的版本数量（默认 10）。
     * <br>Defines the maximum number of versions allowed to register (default: 10).
     */
    private Integer MaxNum;
    /**
     * 设置版本导入排序方式：MAX 为升序，MIN 为降序（不区分大小写）。
     * <br>Defines version import sorting method: <code>MAX</code> for ascending,
     * <code>MIN</code> for descending, case-insensitive.
     */
    private String SortingMethod;

    public MultiVersionProperties() {
        start = true;
        SortingMethod = "MAX";
        MaxNum = 10;
        Include = new ArrayList<>();
        exclude = new ArrayList<>();
    }

    /**
     * 判断某版本是否允许注册,优先处理黑名单
     */
    public boolean VersionIsOk(String version,String path) {
        if (!exclude.isEmpty() & exclude.contains(version)) {
            System.err.println("以下版本的接口因在黑名单而未被注册:'" + version + "'的'" + path + "'");
            return !exclude.contains(version);
        }
        if (!Include.isEmpty() & Include.contains(version)) {
            return true;
        }
        System.err.println("以下版本的接口应未指定未被注册:" + version + "'的'" + path + "'");
        return false;
    }


    public Integer getMaxNum() {
        return MaxNum;
    }


    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public List<String> getInclude() {
        return Include;
    }

    public void setInclude(List<String> include) {
        Include = include;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    public void setMaxNum(Integer maxNum) {
        MaxNum = maxNum;
    }

    public String getSortingMethod() {
        return SortingMethod;
    }

    public void setSortingMethod(String sortingMethod) {
        SortingMethod = sortingMethod;
    }


}
