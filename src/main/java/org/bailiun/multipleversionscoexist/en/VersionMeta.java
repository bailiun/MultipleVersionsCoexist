package org.bailiun.multipleversionscoexist.en;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>版本元信息类 / Version Metadata Class</h2>
 *
 * <p><b>中文说明：</b><br>
 * 用于存储多版本管理系统中每个版本的详细信息。
 * 包括版本名称、作者、描述、启用状态、创建时间以及自定义扩展字段。
 * 该类在框架中用于版本信息展示、配置管理与元数据输出。</p>
 *
 * <p><b>English Description:</b><br>
 * Represents the metadata information for each version managed by the multi-version framework.
 * Contains core attributes such as version name, author, description, status, creation time,
 * and an extendable map for additional information.</p>
 *
 * <p><b>使用场景 / Use Cases:</b></p>
 * <ul>
 *     <li>版本注册与版本状态展示 / Displaying registered version information</li>
 *     <li>版本配置导出 / Exporting version metadata to APIs or logs</li>
 *     <li>团队协作管理 / Tracking authorship and version lifecycle</li>
 * </ul>
 *
 * @author Bailiun
 * @version 1.0.0
 * @since 2025-11
 */
public class VersionMeta {

    /** 版本名称 / Version name */
    private String name;

    /** 版本创建者 / Author or maintainer of the version */
    private String author;

    /** 版本描述 / Description of this version’s purpose or feature set */
    private String desc;

    /** 启用情况 / Whether this version is currently enabled */
    private boolean enabled;

    /** 创建时间 / The creation date of the version */
    private String created;

    /** 自定义信息，可存储扩展字段 / Custom metadata map for extended attributes */
    private Map<String, Object> extra;

    public VersionMeta() {
        this.extra = new HashMap<>();
    }

    /**
     * 添加或更新自定义扩展信息 / Adds or updates a custom metadata entry
     *
     * <p><b>中文说明：</b><br>
     * 向 {@code extra} 字段中插入一个键值对，用于记录自定义信息。</p>
     *
     * <p><b>English Description:</b><br>
     * Inserts or updates a key-value pair into the {@code extra} metadata map.</p>
     *
     * @param key   自定义信息键 / The metadata key
     * @param value 自定义信息值 / The metadata value
     * @return 返回更新后的 {@code extra} Map / The updated {@code extra} map
     */
    public Map<String, Object> put(String key, Object value) {
        this.extra.put(key, value);
        return this.extra;
    }

    /**
     * 自定义输出方法 / Custom toString Output
     *
     * <p><b>中文说明：</b><br>
     * 重写 {@code toString()} 方法，以更直观地显示版本的完整信息。
     * 若存在 {@code extra} 扩展字段，将以键值形式一并输出。</p>
     *
     * <p><b>English Description:</b><br>
     * Overrides the {@code toString()} method to provide a readable
     * representation of version metadata, including all {@code extra} entries if present.</p>
     *
     * @return 包含版本详细信息的字符串 / A formatted string representing version metadata
     */
    @Override
    public String toString() {
        StringBuilder a = new StringBuilder("版本名称='" + name + '\'' +
                ", author='" + author + '\'' +
                ", desc='" + desc + '\'' +
                ", enabled=" + enabled +
                ", created='" + created);
        if (extra != null) {
            for (String key : extra.keySet()) {
                a.append(", ").append(key).append("='").append(extra.get(key)).append('\'');
            }
        }
        return a.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
