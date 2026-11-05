# Workshop Native

一个面向 Android 的 Steam 创意工坊下载工具，支持匿名探索公开工坊内容，也支持登录 Steam 后访问“我的内容”和下载已购/共享游戏的创意工坊条目。

> 这是 [`weilund`](https://github.com/weilund) 的 fork，基于上游
> [`cjtestuse/Workshop-Native`](https://github.com/cjtestuse/Workshop-Native)
> 最新代码，修复了 Steam 新版创意工坊页面下「标签筛选为空」的问题。
> 修改说明见 [CHANGELOG.md](CHANGELOG.md)，安装包见
> [Releases](https://github.com/weilund/Workshop-Native/releases)。

## 功能

- 匿名访问公开工坊游戏与条目
- Steam 登录、已保存账号切换
- 我的内容：已购买游戏、家庭共享游戏
- 创意工坊浏览、筛选、搜索、分页（支持按工坊标签「包含 / 排除」筛选）
- 记住上次的工坊筛选条件，重新进入时自动恢复
- 下载中心：排队、暂停、继续、重试、取消、单条删除
- 下载详情：展示下载链路、网络路径、节点地址、源地址等诊断信息
- 收藏常用工坊游戏
- 设置页：下载位置、并发、匿名优先、CDN 策略、更新检查
- GitHub Release 更新检查

## 标签筛选

浏览公开工坊时，点搜索框右侧的筛选按钮，或横向滑动搜索框下方的胶囊行、
点最后一个「高级筛选」，即可打开筛选面板：

- 按工坊标签分组筛选，例如「类型（场景 / 视频 / 应用程序）」「风格
  （动漫 / 卡通 / 抽象）」「分辨率」「分类」「资产类型」等；
- 每个标签支持两种状态：「包含」（条目必须带该标签）和「排除」（条目不能
  带该标签）；同组内只能选一个值的分组（例如「类型」）按单选处理；
- 与关键词搜索、排序、时间范围、发布日期范围可以叠加使用，选好后点「应用」。

筛选条件会保存在本地：退出应用后再次进入同一个游戏的工坊，会自动恢复上次的
标签、排序、时间范围和日期范围（搜索词和页码不恢复；切换到别的游戏时不会套用
上次的标签，避免标签不匹配导致空列表）。

## 技术栈

- Kotlin
- Jetpack Compose
- Hilt
- ViewModel + StateFlow
- Repository
- Room
- DataStore
- WorkManager
- OkHttp

## 项目结构

- `app/src/main/java/com/slay/workshopnative/ui`：界面与页面状态
- `app/src/main/java/com/slay/workshopnative/data`：数据模型、仓储、会话与本地存储
- `app/src/main/java/com/slay/workshopnative/worker`：后台下载任务
- `app/src/main/java/com/slay/workshopnative/core`：下载与通用工具
- `app/src/main/java/com/slay/workshopnative/update`：GitHub 更新检查

## 构建要求

- JDK 17
- Android SDK 35
- Android Studio Ladybug 或兼容版本

## 本地构建

如果本地没有 `local.properties`，可以直接用环境变量指定 SDK：

```bash
ANDROID_HOME=<your-android-sdk> \
ANDROID_SDK_ROOT=<your-android-sdk> \
./gradlew :app:assembleDebug
```

代码检查：

```bash
ANDROID_HOME=<your-android-sdk> \
ANDROID_SDK_ROOT=<your-android-sdk> \
./gradlew :app:lintDebug
```

## 更新配置

项目内已经接入 GitHub Release 更新检查，当前默认仓库已配置为：

- `cjtestuse/Workshop-Native`

如果后续迁移到你自己的 GitHub 仓库，请修改：

- `app/build.gradle.kts`

将下面两个值替换为真实仓库信息：

- `UPDATE_GITHUB_OWNER`
- `UPDATE_GITHUB_REPO`

应用内更新默认面向正式安装包，建议固定使用这个资产名：

- `workshop-native-release.apk`

## GitHub Release 发版

仓库已经包含自动发版工作流：

- `.github/workflows/release.yml`

触发条件：

- push tag `v*`，例如 `v1.0.1`

工作流会自动：

- 从 tag 生成 `versionName`，例如 `v1.0.1 -> 1.0.1`
- 使用 GitHub Actions 的 `run_number` 作为 `versionCode`
- 构建并上传 `workshop-native-release.apk`
- 生成 `SHA256SUMS.txt`
- 使用 annotated tag 的内容作为 Release 更新说明
- 创建对应的 GitHub Release

发版前需要在 GitHub 仓库里配置这些 Secrets：

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

其中 `ANDROID_KEYSTORE_BASE64` 需要是签名 keystore 的 base64 文本，例如：

```bash
base64 -i release-keystore.jks | pbcopy
```

推荐发布流程：

```bash
git tag -a v1.0.1 -m $'v1.0.1

- 优化下载速度
- 修复登录恢复
- 完善更新逻辑'
git push origin v1.0.1
```

本 fork 另外提供了一个 debug 构建工作流：

- `.github/workflows/build-debug-apk.yml`

可以手动触发（`workflow_dispatch`），也可以在 push 到 `fix/workshop-tag-filter`
分支时触发。它会构建 debug APK 并上传 artifact；如果触发时填写了
`release_tag` 输入，还会自动把 APK 发布到对应的 GitHub Release。
注意 debug 包与上游正式签名不同，安装前需要先卸载正式版。

## 说明

- 项目不依赖自建后端。
- 仓库已排除本地构建产物、截图、缓存和机器本地配置。
- 默认下载位置为系统下载目录下的 `steamapps`，也支持用户自定义根目录。

## 数据与隐私

- 登录恢复使用的 `refresh token` 仅保存在当前设备，并通过 Android Keystore 加密后落盘。
- 应用已关闭 Android 系统备份，避免把登录态和本地缓存带入云备份。
- 本地会保存少量运行数据：已保存账号、下载目录设置、收藏的工坊游戏、下载历史、游戏库快照。
- 设置页提供了清理入口，可分别清除登录状态、游戏库缓存、下载诊断信息、下载历史和收藏列表。

## 网络说明

- Steam 浏览、登录和下载请求直接访问 Steam 相关域名，不依赖自建后端。
- `steamcontent.com` 被允许使用明文流量，是为了兼容部分 Steam 内容分发节点。
- 更新检查和 APK 下载仅使用 GitHub 官方 Release 与官方下载地址，不再经过第三方代理。
