## 项目上下文
这是一个 Android 原生项目（Kotlin/Java + Gradle），不需要自行判断技术栈或框架。

## 代码生成规则
只需要编写代码。禁止尝试运行、构建、测试或执行任何命令来验证代码。

## Command Output
Any command with unknown or potentially large output must be byte-capped.
Default pattern: `COMMAND 2>&1 | head -c 4000`

SCAN_ONLY: ./*.gradle, ./*.gradle.kts, ./*.properties, app/, gradle/libs.versions.toml

