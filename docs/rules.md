# 规则列表

**中文** | [English](./en/rules_en.md) | [HOME](../README.md)

---

## 语言（language）

设置Fuzz Mod的语言。

- 类型: `boolean`



- 默认值: `none`



- 参考选项: `none`, `en_us`, `zh_cn`



- 分类: `FUZZ`, `QOL`



## 禁用使用物品减速（usingItemSlowDownDisabled）

禁用使用物品时的减速效果。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用潜行减速效果（sneakingSlowDownDisabled）

禁用潜行时的减速效果。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用受伤抖动效果（hurtShakeDisabled）

禁用玩家受伤时第一人称视角的抖动。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用手臂渲染（renderHandDisabled）

禁用手臂的渲染。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`

## 基岩版飞行（bedRockFlying）

移除飞行时的漂移效果，就像基岩版一样。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 方块轮廓线颜色（blockOutlineColor）

修改方块轮廓线颜色，使用十六进制RGB代码。

[rainbow] - 动态RGB

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`, `rainbow`, `#FFFFFF`, `#FF88C2`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 彩虹方块轮廓线闪烁速度（rainbowBlockOutlineBlinkSpeed）

修改彩虹方块轮廓线的闪烁速度，需开启`blockOutlineColor`规则。

- 类型: `Double`



- 默认值: `1.024`



- 参考选项: `0.1024`, `1.024`, `5.20`, `10.0`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 方块轮廓线透明度（blockOutlineAlpha）

修改方块轮廓线透明度，需开启`blockOutlineColor`规则。

- 类型: `int`



- 默认值: `-1`



- 参考选项: `-1`, `0`, `255`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 方块轮廓线宽度（blockOutlineWidth）

修改方块轮廓线宽度。

- 类型: `double`



- 默认值: `-1.0`



- 参考选项: `-1.0`, `0.0`, `10.0`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 天空颜色（skyColor）

修改天空颜色，使用十六进制RGB代码。

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`, `#FF88C2`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 雾颜色（fogColor）

修改雾颜色，使用十六进制RGB代码。

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`, `#FF88C2`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 水颜色（waterColor）

修改水颜色，使用十六进制RGB代码。

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`, `#FF88C2`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 修改水雾颜色（waterFogColor）

修改水雾颜色，使用十六进制RGB代码。

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`, `#FF88C2`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## 禁用篝火烟雾粒子（campfireSmokeParticleDisabled）

禁用篝火的烟雾粒子。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 快捷踢出假人（quickKickFakePlayer）

使用快捷键踢出准星指向的假人。

在游戏设置中指定快捷键

**需要Carpet模组**

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`, `CARPET`





## 快捷扔出假人背包（quickDropFakePlayerAllItemStack）

使用快捷键扔出准星指向假人的所有物品堆栈。

在游戏设置中指定快捷键

**需要Carpet模组**

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`, `CARPET`



## 让流体交互像空气一样（letFluidInteractLikeAir）

让玩家与流体的交互像空气一样。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`, `EXPERIMENTAL`



## 禁用流体推动（fluidPushDisabled）

让流体无法推动玩家。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用粘液块减速（slimeBlockSlowDownDisabled）

让玩家不会受到来自粘液块的减速与弹跳。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用蛛网减速（cobwebSlowDownDisabled）

让玩家不会受到来自蛛网的减速。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用冰块打滑（iceSlipperinessDisabled）

让玩家不会受到来自冰块的打滑。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用灵魂沙减速（soulSandBlockSlowDownDisabled）

让玩家不会受到来自灵魂沙的减速。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用蜜块减速（honeyBlockSlowDownDisabled）

让玩家不会受到来自蜜块的减速以及跳跃衰减。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用气泡柱交互（bubbleColumnInteractDisabled）

让玩家不会受到来自气泡柱的效果。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 创造模式中键取得流体桶（pickFluidBucketItemInCreative）

在创造模式下可以瞄准流体用鼠标中键取得对应的桶。

[false] - 关闭功能

[true] - 启用功能

[sneaking] - 需潜行时中键

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 高亮实体（commandHighlightEntities）

使用指令高亮一个或多个指定实体。

使用 /highlightEntity help 指令查看使用指南

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `COMMAND`



## fuzz命令别名（fuzzCommandAlias）

为fuzz命令指定一个别名。

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`



- 分类: `FUZZ`, `COMMAND`



## 跳跃延迟移除（jumpDelayDisabled）

消除了玩家落地后的跳跃延迟。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## 禁用雾渲染（fogRenderDisabled）

清除所有雾气。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `RENDER`



## 坐标罗盘（commandCoordCompass）

使用指令来指定一个坐标，在屏幕上会显示一个箭头指引你到目的坐标处。 

使用 /coordCompass help 指令查看使用指南。

可以搭配"解析消息中坐标 (parseCoordInMessage)"规则使用。

- 类型: `boolean`



- 默认值: `false`



- 参考选项: `false`, `true`



- 分类: `FUZZ`, `SURVIVAL`, `COMMAND`



## 解析消息中坐标（parseCoordInMessage）

解析聊天消息中的坐标，显示为绿色带下划线，且可以通过点击它来联动"坐标罗盘 (commandCoordCompass)"规则。

[fuzz] - 使用\"坐标罗盘 (commandCoordCompass)\"规则的坐标高亮。

[carpetorgaddition] - 使用Carpet Org Addition模组的坐标高亮 (Minecraft >= 1.21.3)。

- 类型: `String`



- 默认值: `false`



- 参考选项: `false`, `fuzz`, `carpetorgaddition`



- 分类: `FUZZ`, `QOL`
