# 命令

**中文** | [English](./en/commands_en.md) | [HOME](../README.md)

---

- 主命令

    - /fuzz

        - /fuzz list <分类标签>

        - /fuzz <规则> <选项>

  /fuzz: 查看当前已启用的规则列表

  /fuzz list <分类标签>: 查看指定分类标签下的规则列表

  /fuzz <规则> <选项>: 修改指定规则的选项

---

## 高亮实体（commandHighlightEntities）

- /highlightEntity add <实体ID> 

  添加高亮实体。

- /highlightEntity remove <实体ID> 

  移除高亮实体。

- /highlightEntity clear 

  清除所有高亮实体。

- /highlightEntity list 

  列出所有高亮实体。

- /highlightEntity help

  显示帮助信息。

## 坐标罗盘（commandCoordCompass）

- /coordCompass set &lt;x&gt; &lt;y&gt; &lt;z&gt;

  设置目标坐标。

- /coordCompass clear

  清除目标坐标。

- /coordCompass help

  显示帮助信息。

## 动画冻结（commandAnimatedFreeze）

- /animatedFreeze add &lt;纹理&gt;

  添加一个需要禁用动画的纹理。

- /animatedFreeze remove &lt;纹理&gt;

  移除一个已被禁用动画的纹理。

- /animatedFreeze removeAll

  解禁所有被禁用动画的纹理。
  
- /animatedFreeze list

  查看被禁用动画的纹理列表。

- /animatedFreeze help

  显示帮助信息。
