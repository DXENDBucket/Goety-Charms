# Goety Charms · 诡厄符咒

Independent Goety addon by EndXiom. Mod ID `goety_charms`, version **0.1.1**, MIT License. Available for Minecraft **1.21.1 / NeoForge** and **1.20.1 / Forge**. Goety is required; Ars Nouveau, Goety & Nouveau and Ars Conflux are not required.

## 制作与使用

1. 用纸 ×1、骨粉 ×1、线 ×1，无序合成空白符咒 ×4。
2. 按原仪式搭建黑暗祭坛、诅咒之笼和环境。原激活物与所有辅材都放在周围基座，每份占一个基座。
3. 手持空白符咒右键祭坛，开始封存。需要的研究、环境、灵魂费用、仪式时间和献祭条件沿用原配方。
4. 同一组材料匹配多个仪式时，潜行右键祭坛切换，普通右键确认。其他物品在副手时也可通过物品交互切换。
5. 完成后在祭坛取出符咒。长按右键一秒释放；转化符需要在释放时瞄准符合条件的生物，召唤符在选定位置产生原生召唤结果。

制作转化符时无需把目标生物带到祭坛。释放时检查目标、当前源配方、原生召唤数量限制与可用空间；成功消耗一张，失败保留。创造模式成功使用不消耗。使用符咒不会再支付已在制作时付过的灵魂费用。

召唤符与转化符分别显示使用说明；转化符另行列出与 Goety JEI 一致的可用实体目标名称。

例如：野火转化符使用专家下界仪式，基座放熔岩桶、壁垒聚晶、下界合金碎片（或原配方标签认可的替代物）、觉醒绿宝石、恶魂之泪；手持空白符咒激活。

召出的生物按原仪式初始化归属、变体、装备和原生生命周期。使徒符咒仍产生原生的降临过程：和平难度拒绝释放，在下界保留原生警告／再次使用确认；不会把使徒变成友军。环境布置只在封存时检查。

## 自动兼容

从实际加载的仪式自动生成真正的 Goety 封存配方，使用原生祭坛，进入 Goety 原生 JEI 仪式分类；没有另一套材料支付、灵魂扣费或仪式进度实现。

- 支持原生三种召唤类型、三种转化类型，配方命名空间不限。
- 新类型注册若直接使用未修改的原生 SummonRitual／ConvertRitual，也可自动识别。
- 自定义执行类需通过 `RitualCharms.register(factoryId, adapter)` 接入，避免只根据实体字段猜测而遗漏特殊逻辑。
- 重载数据包后重建派生配方；既有符咒引用原配方ID。源配方删除、被禁用或不再受支持时拒绝释放并保留符咒。
- 首版范围为召唤和转化，包含以原生召唤类型实现的天气仪式。传送、附魔和其他结果类型尚未接入。

## 配置与管理

`config/goety_charms-common.toml`：`enabled` 总开关、`use_ticks` 长按时长（默认20tick）、`excluded_recipes` 源配方排除列表。配方策略改变后需要 `/reload`；排除和关闭也会阻止现有符咒释放。多人服务器由服务端执行配方和释放判断；客户端长按时长建议保持与服务器一致。

测试指令（权限2）：

```mcfunction
/goety_charms give @p goety:convert_blazes_to_wildfire
/goety_charms give @p goety:thrall/summon_zombie_thrall
```

指令会提供已经封存的测试物品；创造页里的无数据样本和JEI输出示例不能直接释放，需制作或使用上述指令。

## 素材与开发

[三种符咒预览](docs/art/charms-preview.png)。32×32共用底材，召唤／转化仅改变中央印记。底稿由内置imagegen生成，再使用落日之下的像素整理工具；实际提示词与来源哈希保存在 `art/pixel/records/goety_charms.blank_charm.json`，后处理脚本为 `tools/make_charm_variants.py`。原始生成图和中间版本留在本地忽略目录，不随模组打包。

[实现、验证与边界](docs/development.md)。本批只编译、打包并检查发行文件，**没有启动游戏、服务端或GameTest**。回归用例已编译，原生祭坛完整扣费、客户端画面和共存环境仍待运行验证。
