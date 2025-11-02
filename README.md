# Multiple Versions Coexist(部分)

包含:选择性接口注入,多同名接口共存,版本管理

SpringBoot-MultiVersion 是一个为 Spring Boot 项目设计的接口版本管理工具的先前版本的部分源码。它允许在同一个项目中同时存在多个相同路径的接口，通过注解和优先级机制来控制注册和访问的接口版本，实现灵活的版本切换和接口覆盖。

SpringBoot-MultiVersion is a partial source code of an earlier version of an interface version management tool designed for Spring Boot projects. It enables the existence of multiple interfaces with the same path in the same project. Through annotations and priority mechanisms, it controls the registration and access of interface versions, achieving flexible version switching and interface override.

## 项目概述

本项目面向中小型 Spring Boot 项目，提供接口多版本共存与优先级管理的功能。适用于以下场景：

1. **随时切换接口版本**  
   后台部分接口可能更新不稳定，使用本功能可以灵活切换版本，避免线上问题。

2. **解决接口冲突**  
   在多人合作或多项目合并开发时，允许存在多个名称完全相同的接口，通过优先级或自定义规则进行注册，避免接口冲突。

3. **优先级覆盖机制**  
   对于复杂或遗留接口，可以直接通过优先级覆盖旧版本，实现快速替换与调试。或者单纯满足懒惰的程序员习惯。

4. **接口模拟与逆向开发**  
   模拟前人接口行为，方便逆向分析或接口测试。

5. **版本保护**  
   防止跨版本重复调用或滥用，保障项目接口安全性。

---

## 核心功能

### 1. 注解: `@CoexistenceVersion`

- **作用范围**: 类级别  
- **功能**:
  - 将整个控制器内的接口划分到指定版本。
  - 支持指定版本是否开启；未设置版本的接口默认为公共接口。
  - 支持版本白名单和黑名单，选择性注入接口。
  - 可限制最大注册版本数量，避免旧版本接口被误用。
  - 支持通过本地配置文件控制接口访问。
- **后续开发计划**:
  - 将前端访问路径与接口版本进行绑定。

---

### 2. 注解: `@InterfacePriority`

- **作用范围**: 接口方法级别  
- **功能**:
  - 为接口设置优先级，决定是否注册该接口。
  - 默认不允许同名接口存在相同优先级，可通过自定义逻辑修改。
  - 启动后接口注册不可动态修改。
- **后续开发计划**:
  - 支持项目运行中根据优先级动态注册不同版本接口。

### 3. 辅助功能(暂定)

- 为不熟悉统一操作接口注解的开发者提供支持。

- 支持将一个接口设置为另一个接口的“补丁接口”，在原接口出现异常时自动重定向。

- 可对部分接口设置统一检查逻辑，或统一执行后续操作。

  

## 次要功能（暂未实现）

1. 支持为某些值设置定时任务。
2. 通过反射获取或操作类信息（支持注解和类配置）。
3. 支持执行其他语言脚本。
4. 检测类或数据是否已注入，并返回异常提示。
5. 拦截接口调用信息（包含 IP、入参等），生成日志。
6. 格式化前后端交互数据。
7. Bitmap 操作支持。

---

## 未来规划

- 提供基于 IDEA 的可视化操作插件，提升开发效率。
- 增强版本动态管理和接口注入策略。

---

## 效率

- 当前功能已实现核心接口版本管理，但性能和效率尚未经过全面测试。