package org.bailiun.multipleversionscoexist.en;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2>版本信息列表类 / Version Information List</h2>
 *
 * <p><b>中文说明：</b><br>
 * 本类是对 {@link VersionMeta} 的增强集合实现，用于存储多个版本的元信息，并提供版本启用状态的快速查询功能。
 * 它在内部维护了一个 {@code Map<String, Boolean>}（dp），用于映射每个版本的名称与启用状态，
 * 以便在进行版本校验或控制访问时提高效率。</p>
 *
 * <p><b>English Description:</b><br>
 * An enhanced {@link ArrayList} implementation for managing multiple {@link VersionMeta} instances.
 * This class maintains an additional map (`dp`) that associates version names with their
 * enable/disable states, allowing for quick lookups and version validation.</p>
 *
 * <p><b>主要功能 / Key Features:</b></p>
 * <ul>
 *   <li>快速判断版本是否存在及启用状态 / Quickly check if a version exists and whether it’s enabled.</li>
 *   <li>自动同步版本列表与状态映射 / Automatically keeps version list and state map synchronized.</li>
 *   <li>兼容标准 {@link ArrayList} 方法（add、remove、addAll 等） / Fully compatible with base list methods.</li>
 * </ul>
 *
 * @param <T> {@link VersionMeta} 的子类 / The type parameter extending {@link VersionMeta}
 * @author Bailiun
 * @version 1.0.0
 * @since 2025-11
 */
public class VersionInfoList<T extends VersionMeta> extends ArrayList<T> {

    /**
     * 存储版本状态的映射表。
     * <p>键为版本名称，值为是否启用。</p>
     * <p><b>Map structure:</b> {@code <VersionName, isEnabled>}</p>
     */
    private Map<String, Boolean> dp = new HashMap<>();

    /**
     * 判断版本是否存在及可用性 / Check Version Existence and Availability
     *
     * <p><b>中文说明：</b><br></p>
     * 检查指定名称的版本是否存在于 {@code dp} 中：
     * <ul>
     *   <li>若不存在，返回 {@code 1}（表示可注册或启用）。</li>
     *   <li>若存在且启用，返回 {@code 1}（表示有效）。</li>
     *   <li>若存在但被禁用，返回 {@code 2}（表示已禁用）。</li>
     * </ul>
     *
     * <p><b>English Description:</b><br></p>
     * Checks if the given version name exists in the {@code dp} map and whether it’s enabled:
     * <ul>
     *   <li>Returns {@code 1} → Version does not exist or is enabled.</li>
     *   <li>Returns {@code 2} → Version exists but is disabled.</li>
     * </ul>
     *
     * @param name 版本名称 / Version name to check
     * @return 状态码（1=有效，2=禁用） / Status code (1=OK, 2=Disabled)
     */
    public Integer versionIsOk(String name) {
        if (!dp.containsKey(name)) {
            return 1;
        }
        return dp.get(name) ? 1 : 2;
    }

    /**
     * 添加单个版本信息 / Add a Single Version Entry
     *
     * <p>在添加元素的同时，自动更新 {@code dp} 状态映射。</p>
     *
     * @param o 要添加的版本信息对象 / The {@link VersionMeta} instance to add
     * @return 是否成功添加 / {@code true} if the element was added
     */
    @Override
    public boolean add(T o) {
        dp.put(o.getName(), o.isEnabled());
        return super.add(o);
    }

    /**
     * 按索引移除版本信息 / Remove Version by Index
     *
     * <p>在移除前会同步删除对应的 {@code dp} 状态。</p>
     *
     * @param index 要移除的索引位置 / Index of the version to remove
     * @return 被移除的版本信息对象 / The removed {@link VersionMeta} object
     */
    @Override
    public T remove(int index) {
        dp.remove(this.get(index).getName());
        return super.remove(index);
    }

    /**
     * 获取版本状态映射表。
     *
     * @return {@code Map<String, Boolean>} 版本名与启用状态映射 / The version state map
     */
    public Map<String, Boolean> getDp() {
        return dp;
    }

    /**
     * 设置版本状态映射表。
     *
     * @param dp 版本状态映射表 / The map associating version names with their enable state
     */
    public void setDp(Map<String, Boolean> dp) {
        this.dp = dp;
    }

    /**
     * 批量添加版本信息 / Add All Versions from a Collection
     *
     * <p>将指定集合中的所有版本信息加入当前列表，并更新 {@code dp} 状态映射。</p>
     *
     * @param c 要添加的版本集合 / The collection of {@link VersionMeta} instances
     * @return 是否成功添加所有元素 / {@code true} if all were successfully added
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (VersionMeta v : c) {
            dp.put(v.getName(), v.isEnabled());
        }
        return super.addAll(c);
    }
    /**
     * 获取所有启用的版本名称 / Get All Enabled Version Names
     *
     * <p><b>中文说明：</b><br>
     * 遍历 {@code dp} 映射表，筛选出启用状态为 {@code true} 的版本，
     * 并返回其名称列表。</p>
     *
     * <p><b>English Description:</b><br>
     * Returns a list of all version names that are currently enabled
     * (i.e., those with {@code true} in the dp map).</p>
     *
     * @return 启用状态的版本名称列表 / List of enabled version names
     */
    public List<String> getEnabledVersions() {
        return dp.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }
    /**
     * 获取所有未启用的版本名称 / Get All Unable Version Names
     *
     * <p><b>中文说明：</b><br>
     * 遍历 {@code dp} 映射表，筛选出启用状态为 {@code false} 的版本，
     * 并返回其名称列表。</p>
     *
     * <p><b>English Description:</b><br>
     * Returns a list of all version names that are currently enabled
     * (i.e., those with {@code false} in the dp map).</p>
     *
     * @return 未启用状态的版本名称列表 / List of unable version names
     */
    public List<String> getUnableVersions() {
        return dp.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
