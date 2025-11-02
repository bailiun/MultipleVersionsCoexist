package org.bailiun.multipleversionscoexist.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Configuration
@ConfigurationProperties(prefix = "multi.version.properties")
public class MultiVersionProperties {
    private boolean start;      private List<String> Include;  
    private List<String> UnInclude;      private Integer MaxNum;  
    private String SortingMethod;  
    private boolean FileConfiguration;  
    private String FilePath; 
    private Integer FileRefreshTime;

    public MultiVersionProperties() {
        start = true;
        FileConfiguration = false;
        SortingMethod = "MAX";
        MaxNum = 10;
        FileRefreshTime = 5000;
        FilePath = "";
        Include = new ArrayList<>();
        UnInclude = new ArrayList<>();
    }

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

    public static void ensureConfigFileExists(String relativeFilePath) throws IOException {
        Path fullPath = Paths.get(relativeFilePath);          Path parentDir = fullPath.getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);         }

        if (Files.notExists(fullPath)) {
            Files.createFile(fullPath);
            System.out.println("已创建配置文件: " + fullPath);
        } else {
            System.out.println("配置文件已存在: " + fullPath);
        }
    }


    public boolean VersionIsOk(String version) {
        if (!UnInclude.isEmpty()) {
            System.err.println("以下版本因在黑名单而未被注册:"+version);
            return !UnInclude.contains(version);
        }
        if (!Include.isEmpty()) {
            System.err.println("以下版本应未指定未被注册:"+version);
            return Include.contains(version);
        }
        return true;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public Integer getMaxNum() {
        return MaxNum;
    }

    public Integer getFileRefreshTime() {
        return FileRefreshTime;
    }

    public void setFileRefreshTime(Integer fileRefreshTime) {
        FileRefreshTime = fileRefreshTime;
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

    public List<String> getUnInclude() {
        return UnInclude;
    }

    public void setUnInclude(List<String> unInclude) {
        UnInclude = unInclude;
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

    public boolean isFileConfiguration() {
        return FileConfiguration;
    }

    public void setFileConfiguration(boolean fileConfiguration) {
        FileConfiguration = fileConfiguration;
    }
}
