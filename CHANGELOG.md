# 更新日志

## v1.0.9-tagfilter (2026-11-05)

本版本基于上游 `cjtestuse/Workshop-Native` 的最新代码（v1.0.8 之后），
修复 Steam 创意工坊页面改版导致的「标签筛选消失」问题。

### 修复

**Steam 新版工坊页下，高级筛选里没有任何标签**

Steam 在 2026 年把创意工坊浏览页切换到了新版 SSR 页面：页面数据从旧的
`window.SSR.loaderData = [...]` 挪进了
`<script type="application/json" id="valve-ssr-data">{...}</script>`。

App 的标签组（Type / Genre / Resolution 等）只认旧结构，在新页面上解析结果
为空，所以「高级筛选」弹窗里只有日期筛选、看不到任何标签。

改动内容：

- `PublicWorkshopBrowseParser`
  - 新增对新版 `valve-ssr-data` 内嵌 JSON 的解析，作为 `loaderData` 的第二来源；
    旧结构仍然优先，旧页面行为不变。
  - 标签组（`declaredTags`）和「兼容性」开关（`workshopNumbers`）恢复。
  - 补新版页面的总数文案（中文「X 个条目符合筛选条件」/ 英文
    `items matching your filters`），筛选后总数与空结果状态显示正确。
  - 一些健壮性处理：数字匹配必须以数字开头、避免 `data-id` 误匹配、
    页面没有该 script 时直接跳过、兼容 `loaderData` 为单个对象。
- `WorkshopScreen`
  - 浏览模式下，搜索框右侧新增筛选图标按钮，点击打开同一个「高级筛选」弹窗；
    有筛选条件时图标高亮。
  - 打开「高级筛选」时默认展开第一个标签组，避免标签藏在折叠区里。

### 测试

- `PublicWorkshopBrowseParserTest` 新增 3 个用例：
  1. 新格式 `valve-ssr-data` 能解析出标签组、总数、兼容性开关；
  2. 旧格式和新格式同时存在时优先用旧格式；
  3. 新格式 JSON 损坏时不崩溃，回退 DOM 解析。
- 全部 13 个解析器测试通过（原本 10 个 + 新增 3 个）。
- 用实时 Steam 页面验证：Wallpaper Engine 解析出 9 个标签组 / 99 个标签，
  RimWorld 解析出 17 个标签；选择标签后无结果时标签组仍然保留。

### 安装说明

- 本包是**使用 debug 签名构建**的，与官方 v1.0.8 签名不同。
- 安装前需要先卸载官方版本；卸载会清除 App 内的登录状态和下载记录。
- 构建环境：GitHub Actions（JDK 17 + Android Gradle Plugin 8.10.1，
  compileSdk 35），产物为 `app-debug.apk`。

### 已知限制

- 上游 `release.yml` 需要作者的签名 secrets 才能出正式签名版；本仓库的
  `build-debug-apk.yml` 只构建 debug 包。
- 新版工坊页忽略 `numperpage` 参数（服务端每页返回 30 条），翻页功能正常。
