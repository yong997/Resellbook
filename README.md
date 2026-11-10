# 小贩账本（Resellbook）

一款个人轻量级记账 Android App：**极简、离线、本地存储**。用于记录每一单的进货、返利、运费与卖价，自动计算成本与毛利润。

## 功能（v1）

- 订单列表：顶部汇总「总利润 / 待收金额 / 订单数」，订单卡展示卖价与利润
- 新增 / 编辑订单：录入商品名称、数量、进货总价、总返利、快递费、实际卖价、交易日期、备注、结算状态
- 录入页**实时预览**「总成本 / 毛利润」，避免算错
- 一键结算：点列表里的状态标签在【未结 / 已结】间切换
- 长按删除（二次确认）
- 本地 Room 数据库存储，无需联网

## 业务规则

1. **无部分结算**：订单状态只有 `0=未结`、`1=已结` 两种。
2. **无退货，但有盒损**：盒损不单独建字段，录入时直接把「实际卖价」改成扣减后的金额即可。
3. **返利抵扣成本**：返利视为降低进货成本，从总成本中扣除。

## 核心公式（金额单位：分，严格实现于 `domain/Profit.kt`）

```
总成本 = 进货总价 - 总返利 + 快递费
毛利润 = 实际卖价 - 总成本
```

未填写的返利 / 运费按 `0` 处理。

## 技术栈

- Kotlin
- Jetpack Compose + Material 3
- Room（KSP 注解处理）
- MVVM（ViewModel + StateFlow + Repository）
- 单 Activity + Navigation-Compose
- 金额以 `Long`「分」存储，彻底避免浮点误差

> 构建工具链：AGP 9.2.1（内置 Kotlin 2.2.10）/ Gradle 9.4.1 / compileSdk 36 / minSdk 26。

## 目录结构

```
app/src/main/java/com/xiaofan/ledger/
├─ ResellbookApp.kt          # Application + 手动 DI（Repository）
├─ MainActivity.kt           # 单 Activity
├─ data/                     # Room：Entity / Dao / Database / Repository
├─ domain/Profit.kt          # 核心公式（纯函数）
├─ util/                     # Money（元<->分）、Dates
└─ ui/
   ├─ theme/                 # Material3 主题
   ├─ navigation/AppNav.kt   # 列表 / 编辑 路由
   ├─ list/                  # 列表页 + ViewModel
   └─ edit/                  # 编辑页 + ViewModel
```

## 构建与运行

用 Android Studio 打开本目录直接运行，或命令行：

```bash
# Windows（本机 JDK：Android Studio 自带 JBR 21）
set JAVA_HOME=D:\Android\Android Studio\jbr
gradlew.bat :app:assembleDebug
gradlew.bat :app:testDebugUnitTest
```

产物：`app/build/outputs/apk/debug/app-debug.apk`

## 说明

- 数据仅保存在本机 App 私有目录（`resellbook.db`），卸载即清除。
- 后续可扩展：导出/备份 CSV、按月统计、搜索筛选、一单多商品明细等。
