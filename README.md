```java
<dependency>
    <groupId>io.github.bailiun</groupId>
    <artifactId>multiple-versions-coexist</artifactId>
    <version>1.3.2</version>
</dependency>
```
# Multiple Versions Coexist

### ⭐ A flexible multi-version coexistence & interface-priority engine for Spring Boot

`Multiple Versions Coexist` 是一个专为 **Spring Boot** 生态打造的接口多版本管理组件，旨在解决多人协作、项目演化、客户端差异化需求中频繁出现的 **接口版本冲突、优先级混乱、环境切换繁琐** 等问题。

该组件以 **注解驱动、反射增强式 AOP 模型** 为核心，让开发者可以在不修改原有业务的前提下，为项目提供：

- 多版本接口共存
- 接口优先级覆盖
- 版本切换与版本隔离
- 灵活的接口注入逻辑
- 高扩展性的 Hook、Event、AOP 增强能力（反射完成，无侵入）
- 多语言脚本兼容能力

------

## ⭐ 为什么你需要这个组件？

如果你正在遇到：

- **多人开发导致接口冲突**
- **旧逻辑不敢删除**
- **项目拆成免费版/Pro 版维护成本高**
- **有灰度发布或快速切换版本的需求**
- **想要一个比 Spring AOP 更灵活的增强体系**

那么 `Multiple Versions Coexist` 将极大减少你的复杂度，使项目更加可控与可演化。
------

## 🎯 适用场景

| 场景                            | 传统做法                       | 使用 Multiple Versions Coexist   |
| ------------------------------- | ------------------------------ | -------------------------------- |
| **多人协作接口版本不统一**      | 修改 Controller 路径；复制代码 | 自动注册不同版本路径；无冲突共存 |
| **旧接口无法重构但必须保留**    | 新开模块；复制粘贴             | 使用优先级覆盖旧版本；安全替换   |
| **免费版 / Pro 版功能拆分困难** | 多仓库维护多个项目             | 单项目支持多版本组，按需选择     |
| **客户定制化接口**              | 为每个客户复制控制器           | 同名接口按版本组隔离，无需复制   |
| **灰度发布/Beta 测试**          | 部署多个包                     | 通过配置动态启用指定版本         |
| **复杂切面/拦截器/事件增强**    | 自行实现复杂 AOP/Hook          | 使用反射增强机制，降低复杂度     |
| **跨环境配置繁琐**              | 多套配置文件手动切换           | 统一管理版本的访问策略           |

------

## 🚀 核心优势

### ✓ **1. 全面的多版本接口共存能力**

通过注解 `@CoexistenceVersion` 控制类级别版本逻辑，包括：

- 指定版本号
- 设置版本开关
- 版本白名单/黑名单
- 最大注册版本数量
- 配置文件驱动的动态启用逻辑

未设置版本的接口自动视为公共接口。

------

### ✓ **2. 接口优先级覆盖（@InterfacePriority）**

- 为接口方法设置优先级
- 默认禁止同名 + 同优先级（可自定义规则）
- 允许高优先级接口覆盖旧版本逻辑
- 运维或调试时可快速替换业务行为
- 启动后保持稳定性（不可动态篡改）

未来计划：支持**运行中动态更换版本**。

------

### ✓ **3. 反射驱动的 AOP & 同步机制（@SynchronousOperation）**

- 牺牲少量性能换取极高灵活性
- 无需编写复杂切面，即可实现增强逻辑
- 内置**同步 / 异步执行模型**
- 适用于 Hook、Event、增强行为等需求

------

### ✓ **4. 统一配置管理（multi.info.version-info-list）**

使用配置即可定义：

- 各版本信息
- 启用开关
- 适用环境
- 路径绑定
- 根据 `MultiVersionInfo` 导出版本状态

非常适用于：

- 灰度发布
- 客户定制化
- 多环境隔离管理

------

## 📦 功能结构

```
Multiple Versions Coexist
├─ 注解体系
│  └─ @CoexistenceVersion —— 控制器版本注入
│      └─ @NotIncCoexistenceVersion —— 禁止某接口版本注入
│  └─ @InterfacePriority —— 接口优先级管理
│  └─ @SynchronousOperation —— 反射 AOP 增强
│      ├─ @BeforeOperation —— 改变aop的执行顺序
│      ├─ @AfterOperation —— 改变aop的执行顺序
│      ├─ @SynchronousOperations —— 支持多个切面
│      └─ @UnSynchronousOperation —— 在类被标记的情况下禁止某接口执行AOP
│
├─ 配置体系
│  ├─ multi.file —— 本地文件控制接口开放管理配置
│  ├─ multi.version —— 功能总配置
│  └─ multi.info —— 版本信息配置中心
│
├─ 动态注册引擎
│  ├─ Controller 版本隔离
│  ├─ 接口优先级覆盖
│  └─ 自动生成访问路径
│
└─ 扩展能力
   ├─ Hook / Event 驱动
   ├─ 多语言脚本兼容（Python 等）
   └─ 环境定制化逻辑
```

------

## 🧩 典型使用示例（简化版）

```
@CoexistenceVersion(
    version = "v2"
)
@RestController
public class UserControllerV2 {
    @InterfacePriority(10)
    @GetMapping("/user/info")
    public Object infoV2() {
        return "v2 version";
    }
}
```

------

## 🏗 开发路线（Roadmap）

-  运行中动态切换版本
-  前端访问路径与版本绑定
-  自动生成接口版本文档
-  多租户版本隔离体系
-  增强版事件驱动（反射异步调度器）
-  IDE 插件支持（可视化调试版本行为）

------
