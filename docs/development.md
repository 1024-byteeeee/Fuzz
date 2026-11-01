# 开发

**中文** | [English](./en/development_en.md) | [HOME](../README.md)

---

## 多版本开发

构建结构与 Carpet AMS Addition 保持一致：`src/main` 为共享源码，
`minecraftVersions.json` 声明目标版本，`versions/<版本>/gradle.properties` 配置版本依赖，
所有版本共用 `common.gradle`。主开发版本为 `versions/mainProject` 指定的 1.21.11。

支持 1.16.5、1.17.1、1.18.2、1.19.4、1.20.6、1.21.8、1.21.11、26.1.2、26.2、26.3。
1.x 使用官方 Mojang mappings 和 Loom remap，26.x 使用非混淆 Loom。
`fabric_api_mod_id` 必须与实际 Fabric API 包一致：1.16.5、1.17.1 为 `fabric`，其余目标版本为 `fabric-api`。
Minecraft 依赖精确声明已验证的目标版本，避免把某个版本的 jar 加载到不兼容的新版本。
Gradle 使用 JDK 25；编译 toolchain 根据版本选择 JDK 21 或 25，并用 `--release` 同时限制目标字节码和 JDK API 到各游戏版本要求，防止旧版误用新 JDK 方法。

版本间名称变化放在相邻节点的 `versions/mapping-*.txt`；签名或逻辑变化使用
`//#if`、`//#elseif`、`//#else`、`//$$`。重复的简单调用转换集中放在
`utils/compat` 的 `@PreprocessPattern` 模板中，业务代码直接调用原版 API。
不要修改 `versions/*/build/preprocessed` 下的生成文件。

常用任务：

- `./gradlew :1.21.11:build`：构建指定版本。
- `./gradlew buildAndGather`：构建并收集所有版本的 jar。
- `./gradlew :1.21.11:runClient`：启动指定版本，运行目录为 `run/1.21.11`。
- `./gradlew :1.21.11:runMixinAudit`：启动客户端并执行 Mixin 审计。
- `./gradlew :版本:setCoreVersion`：通过预处理器切换共享源码的主开发版本；提交前切回 1.21.11。

IDEA 占用 Loom 缓存时，可附加 `--project-cache-dir build/verification-cache`，
并使用独立的 `GRADLE_USER_HOME` 验证，避免与 IDE 的构建争用缓存。

## 依赖列表
|         依赖          |                        链接                         | 要求 |
|:-------------------:|:-------------------------------------------------:|:--:|
|     Fabric API      |        https://github.com/FabricMC/fabric         | 必须 |
|     Carpet Mod      |     https://github.com/gnembon/fabric-carpet      | 可选 |
| Carpet Org Addition | https://github.com/fcsailboat/Carpet-Org-Addition | 可选 |

## 许可
此项目在 [ LGPL-v3.0 ](https://choosealicense.com/licenses/lgpl-3.0/) 许可证下可用,您可以随意从中学习并将其纳入您自己的项目中。
