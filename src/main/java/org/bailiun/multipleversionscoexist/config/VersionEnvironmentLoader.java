package org.bailiun.multipleversionscoexist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
/**
 * VersionEnvironmentLoader — 版本环境加载器 / Version Environment Loader
 *
 * <p><b>中文说明：</b><br>
 * 该类用于根据当前 Spring Boot 运行环境（例如 <code>dev</code>、<code>test</code>、<code>prod</code>）加载对应的版本控制策略，
 * 并结合 {@link org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties} 中的白名单（include）与黑名单（exclude）配置，
 * 自动计算出当前环境下最终生效的版本列表。</p>
 *
 * <p>主要功能包括：</p>
 * <ul>
 *   <li>读取当前应用的激活环境（<code>spring.profiles.active</code>）</li>
 *   <li>加载白名单与黑名单版本配置</li>
 *   <li>根据未启动的版本动态剔除</li>
 *   <li>在控制台输出完整的版本过滤流程</li>
 * </ul>
 *
 * <p><b>English Description:</b><br>
 * This component loads version control configurations according to the current Spring Boot profile
 * (e.g., <code>dev</code>, <code>test</code>, <code>prod</code>).
 * It combines whitelist (<code>include</code>) and blacklist (<code>exclude</code>) settings defined in
 * {@link org.bailiun.multipleversionscoexist.Properties.MultiVersionProperties}, and determines
 * which versions are active in the current environment.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *   <li>Read the current Spring profile (<code>spring.profiles.active</code>)</li>
 *   <li>Load version include/exclude configurations</li>
 *   <li>Remove unactivated versions dynamically</li>
 *   <li>Print version activation information to console</li>
 * </ul>
 *
 * <p><b>使用示例 / Example:</b></p>
 * <pre>{@code
 * VersionEnvironmentLoader loader = new VersionEnvironmentLoader();
 * loader.refreshActiveVersions(List.of("v1", "v3"));
 * // 控制台输出:
 * // 🌍 当前环境：dev
 * // 📦 包含版本：[v1, v2, v3]
 * // 🚫 排除版本：[v4]
 * // ⚠️ 以下版本因未启动被排除：[v1, v3]
 * // ✅ 最终生效版本：[v2]
 * }</pre>
 *
 * @author bailiun
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class VersionEnvironmentLoader {
    /**
     * <p><b>中文说明：</b>当前应用所使用的 Spring Boot 环境配置（例如 dev、test、prod）。</p>
     * <p><b>English Description:</b>The currently active Spring Boot environment (e.g., dev, test, prod).</p>
     */
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    /**
     * <p><b>中文说明：</b>版本白名单，由 {@code multi.version.include} 属性定义。</p>
     * <p><b>English Description:</b>Whitelist of versions, defined by {@code multi.version.include}.</p>
     */
    @Value("${multi.version.include:}")
    private String[] includeVersions;
    /**
     * <p><b>中文说明：</b>版本黑名单，由 {@code multi.version.exclude} 属性定义。</p>
     * <p><b>English Description:</b>Blacklist of versions, defined by {@code multi.version.exclude}.</p>
     */
    @Value("${multi.version.exclude:}")
    private String[] excludeVersions;
    /**
     * <p><b>中文说明：</b>最终计算得到的有效版本集合，用于后续版本注册与展示。</p>
     * <p><b>English Description:</b>The final set of active versions after all filtering and exclusions.</p>
     */
    private final Set<String> activeVersions = new HashSet<>();
    /**
     * 刷新当前生效版本 / Refresh Active Versions
     *
     * <p><b>中文说明：</b><br>
     * 重新计算当前环境下的最终生效版本：</p>
     * <ul>
     *   <li>先加载白名单版本</li>
     *   <li>剔除黑名单版本</li>
     *   <li>如果存在未启动版本（参数传入），则将其从结果中移除</li>
     *   <li>在控制台输出版本计算的完整过程</li>
     * </ul>
     *
     *
     * <p><b>English Description:</b><br>
     * Rebuilds the active version set by:</p>
     * <ul>
     *   <li>Loading whitelisted versions first</li>
     *   <li>Removing blacklisted ones</li>
     *   <li>Excluding unactivated versions (if provided)</li>
     *   <li>Printing detailed process logs to console</li>
     * </ul>
     *
     *
     * @param unactivatedVersions 未启动的版本列表，可为 null / The list of unactivated versions, may be null
     */
    public void refreshActiveVersions(List<String> unactivatedVersions) {
        activeVersions.clear();
        activeVersions.addAll(Arrays.asList(includeVersions));
        Arrays.asList(excludeVersions).forEach(activeVersions::remove);

        System.out.println("🌍 当前环境：" + activeProfile);
        System.out.println("📦 白名单包含版本：" + Arrays.toString(includeVersions));
        System.out.println("🚫 黑名单排除版本：" + Arrays.toString(excludeVersions));
        if (unactivatedVersions == null || unactivatedVersions.isEmpty()) {
            System.out.println("✅ 最终生效版本：" + activeVersions);
            return;
        }else {
            System.out.println("✅ 初始生效版本：" + activeVersions);
        }
        unactivatedVersions.forEach(activeVersions::remove);
        System.out.println("⚠️ 以下版本因未启动被排除：" + unactivatedVersions);
        System.out.println("✅ 最终生效版本：" + activeVersions);
    }


    public Set<String> getActiveVersions() {
        return activeVersions;
    }
}
