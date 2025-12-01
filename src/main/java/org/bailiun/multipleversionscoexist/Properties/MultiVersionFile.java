package org.bailiun.multipleversionscoexist.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
/**
 * <h2>多版本文件控制配置 / Multi-Version File Control Configuration</h2>
 *
 * <p><b>中文说明：</b><br>
 * 本类用于通过本地文件控制多版本接口的可访问性。
 * 当 {@code FileConfiguration=true} 时，系统会自动检测 {@code FilePath} 指定的路径，
 * 如果文件不存在，则会自动创建；并按照 {@code FileRefreshTime} 设置的间隔定时刷新文件内容。</p>
 *
 * <p><b>English Description:</b><br>
 * This class manages the accessibility of multi-versioned APIs using a local configuration file.
 * When {@code FileConfiguration=true}, the system checks the specified {@code FilePath}.
 * If the file does not exist, it will be automatically created.
 * The file content will be periodically refreshed based on {@code FileRefreshTime}.</p>
 *
 * <p><b>Configuration Prefix:</b> <code>multi.version.file</code></p>
 *
 * <p><b>Example (application.yml):</b></p>
 * <pre>
 * multi:
 *   version:
 *     file:
 *       file-configuration: true
 *       file-path: config/VersionConfig.txt
 *       file-refresh-time: 5000
 * </pre>
 *
 * @author Bailiun
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "multi.file")
public class MultiVersionFile {
    /**
     * 是否启用本地文件配置。
     * <br>Whether to enable local file configuration.
     */
    private boolean FileConfiguration;

    /**
     * 本地文件路径，用于控制多版本访问。
     * <br>The file path used to control access to registered versions.
     * <p><b>Example:</b> {@code config/VersionConfig.txt}</p>
     */
    private String FilePath;

    /**
     * 文件内容刷新时间间隔（单位：毫秒）。
     * <br>Time interval (in milliseconds) for reading and refreshing the configuration file.
     * <p>Default: 5000 ms (5 seconds)</p>
     */
    private Integer FileRefreshTime;

    /**
     * 构造函数：设置默认值。
     * <br>Constructor: initializes default property values.
     */
    public MultiVersionFile() {
        FileConfiguration = false;
        FileRefreshTime = 5000;
        FilePath = "";
    }

    /**
     * 初始化方法：在配置启用时自动执行。
     * <br>Initialization method: executed automatically when configuration is enabled.
     *
     * <p><b>中文：</b><br>
     * 如果 {@code FileConfiguration} 被启用，则检测 {@code FilePath} 是否存在。
     * 若不存在，则自动创建文件及其目录结构。</p>
     *
     * <p><b>English:</b><br>
     * If {@code FileConfiguration} is enabled, the system checks whether the {@code FilePath} exists.
     * If not, it creates the file and any missing parent directories automatically.</p>
     */
    @PostConstruct
    public void init() {
        if (FileConfiguration) {
            try {
                String[] temp = this.FilePath.split("\\.");
                if(!Objects.equals(temp[temp.length - 1], "txt")){
                    this.FilePath+="VersionConfig.txt";
                }
                ensureConfigFileExists(this.FilePath);
            } catch (IOException e) {
                throw new RuntimeException("配置文件创建失败，路径：" + this.FilePath, e);
            }
        }
    }
    /**
     * 确保配置文件存在，如果不存在则创建它。
     * <br>Ensure that the configuration file exists; if not, create it.
     *
     * @param relativeFilePath 相对路径，例如 {@code "config/VersionConfig.txt"} 或 {@code "src/main/resources/mvconfig.txt"}
     *                         <br>Relative file path, e.g. {@code "config/VersionConfig.txt"} or {@code "src/main/resources/mvconfig.txt"}
     * @throws IOException 当文件或目录创建失败时抛出。
     *                     <br>Thrown when file or directory creation fails.
     */
    private static void ensureConfigFileExists(String relativeFilePath) throws IOException {
        Path fullPath = Paths.get(relativeFilePath);  // 使用相对路径构造 Path
        Path parentDir = fullPath.getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);  // 创建所有必要的目录
        }

        if (Files.notExists(fullPath)) {
            Files.createFile(fullPath);
            System.out.println("已创建配置文件: " + fullPath);
        } else {
            System.out.println("配置文件已存在: " + fullPath);
        }
    }

    public boolean isFileConfiguration() {
        return FileConfiguration;
    }

    public void setFileConfiguration(boolean fileConfiguration) {
        FileConfiguration = fileConfiguration;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public Integer getFileRefreshTime() {
        return FileRefreshTime;
    }

    public void setFileRefreshTime(Integer fileRefreshTime) {
        FileRefreshTime = fileRefreshTime;
    }
}
