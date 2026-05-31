# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

FlowMate（包名：`com.github.ezziy`）是一个 Android 原生个人效率助理 App。MVP 范围：漂亮的"今日面板"桌面 + 桌面小组件 + Agent 工具调用，帮助用户管理待办、邮件、日程和每日优先级。

当前项目是 Android Studio "Empty Activity" 模板生成的初始状态，尚无业务逻辑。

## 构建与运行

```bash
# 构建 Debug APK
./gradlew.bat assembleDebug

# 构建 Release APK
./gradlew.bat assembleRelease

# 运行所有单元测试
./gradlew.bat test

# 运行单个单元测试类
./gradlew.bat testDebugUnitTest --tests "com.github.ezziy.ExampleUnitTest"

# 运行 Instrumented 测试（需要连接设备或模拟器）
./gradlew.bat connectedAndroidTest

# 清理构建
./gradlew.bat clean
```

## 技术栈

| 层级 | 技术 |
|---|---|
| 语言 | Kotlin 1.9.0 |
| UI 框架 | Jetpack Compose (BOM 2024.04.01) + Material3 |
| 构建工具 | Gradle 8.7，Kotlin DSL，AGP 8.6.0 |
| 最低 SDK | 26（Android 8.0） |
| 目标/编译 SDK | 34（Android 14） |
| Compose 编译器 | 1.5.1 |

依赖通过 Gradle Version Catalog 统一管理，配置文件：`gradle/libs.versions.toml`。新增依赖必须在此文件中声明，不要在 `build.gradle.kts` 中硬编码版本号。

## 架构

单模块（`:app`）项目。源码根目录：`app/src/main/java/com/github/ezziy/`。

当前结构：
```
com.github.ezziy/
├── MainActivity.kt          # 唯一的 ComponentActivity，Compose 入口
└── ui/theme/
    ├── Color.kt             # 颜色定义
    ├── Theme.kt             # FlawMateTheme 主题组件（Material3 + 动态取色）
    └── Type.kt              # 字体排版定义
```

目标架构：单 Activity + Compose Navigation 导航，每个页面一个 ViewModel，Repository 模式管理数据。暂无 DI 框架，按需引入 Hilt 或手动依赖注入。

## 视觉设计：水彩风格

所有 UI 必须遵循 `.claude/skills/watercolor-style/SKILL.md` 中定义的水彩设计规范。以下是 Compose 中对应的设计 Token：

| Token | 值 | Compose 对应 |
|---|---|---|
| 主背景色 | `#faf8f5` | Surface / background |
| 次背景色 | `#f0ebe3` | 卡片 / 浮层 surface |
| 主文字色 | `#3a3a3a` | OnSurface |
| 主按钮色 | `#4a6fa5` 80% 透明度 | Primary container |
| 玫瑰晕染 | `#b4788c` 10% 透明度 | 强调卡片变体 |
| 鼠尾草晕染 | `#78a082` 10% 透明度 | 强调卡片变体 |
| 边框色 | `#4a6fa5` 20% 透明度 | Outline |
| 圆角半径 | 16dp–24dp | 对应 `rounded-2xl` 到 `rounded-3xl` |
| 阴影 | 柔和漫射，低透明度 | `ambientColor` / `spotColor` 约 12-15% |

**UI 禁忌：** 直角（0dp 圆角）、纯黑背景、硬边投影、粗体/重字重、粗边框。始终使用柔和、圆润、半透明的风格。

字体：标题使用衬线风格（轻字重、宽字距），正文使用无衬线轻字重。

## 关键约定

- **Compose 优先：** 不使用 XML 布局，所有 UI 均为 `@Composable` 函数。
- **全面屏：** `MainActivity` 调用了 `enableEdgeToEdge()`，必须尊重 Scaffold 的 `innerPadding`。
- **主题：** 所有内容必须包裹在 `FlawMateTheme {}` 中。主题在 API 31+ 支持动态取色。
- **预览：** 可视化组件应添加 `@Preview` Composable。
- **版本目录：** Gradle 文件中通过 `libs.xxx` 引用依赖。
- **文档语言：** 所有文档用中文编写。


