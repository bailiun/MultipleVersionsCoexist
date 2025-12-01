package org.bailiun.multipleversionscoexist.config;

import org.bailiun.multipleversionscoexist.Properties.MultiVersionFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * DynamicFileWatcher — 动态文件监听器 / Dynamic File Watcher
 *
 * <p><b>中文说明：</b><br>
 * 该类用于在启用 “本地文件控制版本访问” 功能后，
 * 定时读取配置文件内容（例如 {@code VersionConfig.txt}），
 * 将其转换为内存缓存（{@code List<String>}）以供版本控制逻辑使用。</p>
 *
 * <p>主要职责包括：</p>
 * <ul>
 *   <li>在程序启动时（实现 {@link CommandLineRunner} 接口）初始化本地文件配置</li>
 *   <li>基于 {@link TaskScheduler} 定期刷新配置内容</li>
 *   <li>支持动态间隔（由 {@code multi.version.file.FileRefreshTime} 控制）</li>
 *   <li>在控制台输出当前状态，如启动成功、未配置、文件刷新等信息</li>
 * </ul>
 *
 * <p><b>English Description:</b><br>
 * This component dynamically watches and reloads local version control files.
 * When local file-based version control is enabled, it periodically reads the
 * configuration file (e.g., {@code VersionConfig.txt}), converts its contents into
 * an in-memory cache, and updates version access states automatically.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *   <li>Initialize file configuration at startup (implements {@link CommandLineRunner})</li>
 *   <li>Refresh file contents periodically using {@link TaskScheduler}</li>
 *   <li>Support dynamic refresh interval via {@code multi.version.file.FileRefreshTime}</li>
 *   <li>Provide console logs for status and diagnostics</li>
 * </ul>
 *
 * <p><b>使用示例 / Example:</b></p>
 * <pre>{@code
 * # application.yml
 * multi:
 *   version:
 *     file:
 *       FileConfiguration: true
 *       FilePath: config/VersionConfig.txt
 *       FileRefreshTime: 5000
 * }</pre>
 *
 * <pre>{@code
 * # VersionConfig.txt
 * v1
 * v2
 * v3
 * }</pre>
 *
 * <pre>{@code
 * ✅ 动态注册定时任务, 间隔: 5000ms
 * [v1, v2, v3]
 * 配置文件已刷新
 * }</pre>
 *
 * @author bailiun
 * @version 1.0.0
 * @since 1.0.0
 */
//@Component
public class DynamicFileWatcher implements CommandLineRunner {

    /**
     * <p><b>中文说明：</b>任务调度器，用于定时执行配置文件刷新任务。</p>
     * <p><b>English Description:</b>Task scheduler used to periodically execute the file refresh task.</p>
     */
    @Resource
    private TaskScheduler taskScheduler;

    /**
     * <p><b>中文说明：</b>配置文件的刷新时间间隔（毫秒），来自 {@code multi.version.file.FileRefreshTime}。</p>
     * <p><b>English Description:</b>Refresh interval in milliseconds, defined in {@code multi.version.file.FileRefreshTime}.</p>
     */
    @Value("${multi.version.file.FileRefreshTime:0}")
    private long refreshTime;

    /**
     * <p><b>中文说明：</b>本地文件版本控制配置类。</p>
     * <p><b>English Description:</b>File control configuration object, injected from {@link MultiVersionFile}.</p>
     */
    @Resource
    private MultiVersionFile mf;

    /**
     * <p><b>中文说明：</b>当前文件中读取到的版本列表缓存。</p>
     * <p><b>English Description:</b>Cached version list read from the local configuration file.</p>
     */
    private List<String> FileConfiguration = new ArrayList<>();

    /**
     * 程序启动入口 / Application Startup Entry
     *
     * <p><b>中文说明：</b>
     * 当程序启动时执行：</p>
     * <ul>
     *   <li>如果启用了文件控制功能，则立即读取配置文件内容</li>
     *   <li>若配置了刷新间隔，则注册定时任务以周期性更新文件内容</li>
     *   <li>否则输出警告信息提示未启用定时刷新</li>
     * </ul>
     *
     *
     * <p><b>English Description:</b>
     * Executed on application startup:</p>
     * <ul>
     *   <li>Reads configuration file if file-based control is enabled</li>
     *   <li>Registers a scheduled refresh task if {@code FileRefreshTime} is set</li>
     *   <li>Otherwise, prints a warning message to console</li>
     * </ul>
     *
     *
     * @param args 启动参数 / Startup arguments
     */
    @Override
    public void run(String... args) {
        if (mf.isFileConfiguration()) {
            Path path = Paths.get(mf.getFilePath());
            try {
                FileConfiguration = Files.readAllLines(path).stream()
                        .filter(line -> !line.trim().isEmpty())
                        .map(line -> line.startsWith("/") ? line : "/" + line.trim())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (refreshTime > 0) {
            taskScheduler.scheduleWithFixedDelay(this::watchConfigFile, Duration.ofMillis(refreshTime));
            System.out.println("✅ 动态注册定时任务, 间隔: " + refreshTime + "ms");
        } else {
            System.out.println("⚠️ 未配置 FileRefreshTime, 不启动定时刷新任务");
        }
    }

    /**
     * 文件刷新逻辑 / File Refresh Logic
     *
     * <p><b>中文说明：</b>
     * 定期检查并重新加载指定路径的配置文件内容：</p>
     * <ul>
     *   <li>忽略空行</li>
     *   <li>为每个版本号自动添加前缀“/”</li>
     *   <li>更新本地缓存 {@code FileConfiguration}</li>
     *   <li>打印刷新结果到控制台</li>
     * </ul>
     *
     *
     * <p><b>English Description:</b>
     * Periodically checks and reloads the file content:</p>
     * <ul>
     *   <li>Ignores blank lines</li>
     *   <li>Prefixes each version with “/”</li>
     *   <li>Updates the {@code FileConfiguration} cache</li>
     *   <li>Prints refresh results to console</li>
     * </ul>
     *
     */
    public void watchConfigFile() {
        if (mf.isFileConfiguration()) {
            Path configPath = Paths.get(mf.getFilePath());
            try {
                if (Files.exists(configPath)) {
                    FileConfiguration = Files.readAllLines(configPath).stream()
                            .filter(line -> !line.trim().isEmpty())
                            .map(line -> line.startsWith("/") ? line : "/" + line.trim())
                            .collect(Collectors.toList());
                    System.out.println(Arrays.toString(FileConfiguration.stream().map(line -> line.substring(1)).toArray()));
                    System.out.println("配置文件已刷新");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
