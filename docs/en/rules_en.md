# Rule list

[中文](../rules.md) | **English** | [HOME](../../README.md)

---

## usingItemSlowDownDisabled

Disable the slow effect when using items.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## sneakingSlowDownDisabled

Disable the slow effect while sneaking.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`

## hurtShakeDisabled

Disable the jitter of the first-person view when the player is injured.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## renderHandDisabled

Disable the rendering of the arm.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`

## bedRockFlying

Removed the drift effect while flying, just like Bedrock Edition.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## blockOutlineColor

Modify the color of the block outline, Use Hex RGB codes.

[rainbow] - Dynamic RGB

- Type: `String`



- Default: `false`



- Suggested options: `false`, `#FFFFFF`, `#FF88C2`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## blockOutlineAlpha

Modify the alpha of the block outline, Need enable `blockOutlineColor` function.

- Type: `int`



- Default: `-1`



- Suggested options: `-1`, `0`, `255`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## blockOutlineWidth

Modify the line width of the block outline.

- Type: `double`



- Default: `-1`



- Suggested options: `-1.0`, `0.0`, `10.0`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## skyColor

Modify the color of the sky, Use Hex RGB codes.

- Type: `String`



- Default: `false`



- Suggested options: `false`, `#FF88C2`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## fogColor

Modify the color of the fog, Use Hex RGB codes.

- Type: `String`



- Default: `false`



- Suggested options: `false`, `#FF88C2`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## waterColor

Modify the color of the water, Use Hex RGB codes.

- Type: `String`



- Default: `false`



- Suggested options: `false`, `#FF88C2`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## waterFogColor

Modify the color of the water fog, Use Hex RGB codes.

- Type: `String`



- Default: `false`



- Suggested options: `false`, `#FF88C2`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `RENDER`



## campfireSmokeParticleDisabled

Disable campfire smoke particles.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## quickKickFakePlayer

Use the hotkeys to kick out the fake player pointed to by the crosshair.

Specify hotkeys in the game settings

**Requires carpet mod**

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`, `CARPET`





## quickDropFakePlayerAllItemStack

Use hotkeys to throw out all item stacks of the fake player pointed by the crosshair.

Specify hotkeys in the game settings

**Requires carpet mod**

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`, `CARPET`



## letFluidInteractLikeAir

Let the player interact with the fluid like air.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`, `EXPERIMENTAL`



## fluidPushDisabled

Let fluid can't push player.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## slimeBlockSlowDownDisabled

Prevents the player from slowing and bouncing from slime blocks.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## cobwebSlowDownDisabled

Prevents the player from being slowed by cobwebs.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## iceSlipperinessDisabled

Prevents the player from slipping from ice blocks.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## soulSandBlockSlowDownDisabled

Prevents the player from slowing from soul sands.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## honeyBlockSlowDownDisabled

Prevents the player from slowing and jumping falloff from honey blocks.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## bubbleColumnInteractDisabled

Prevents the player from being affected by the bubble column.

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## pickFluidBucketItemInCreative

In Creative mode, you can target a fluid and use the middle mouse button to pick up the corresponding bucket.

[false] - Turn off the feature

[true] - Enable the feature

[sneaking] - Middle-click when sneaking is required

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `FUZZ`, `FEATURE`, `SURVIVAL`, `QOL`



## commandHighLightEntities

Use command to highlight one or more specified entities.

Use /highlightEntity help command to view the user guide

- Type: `boolean`



- Default: `false`



- Suggested options: `false`, `true`



- Categroies: `AMS`, `FEATURE`, `SURVIVAL`, `COMMAND`



## fuzzCommandAlias

Specify an alias for the /fuzz command.

- Type: `String`



- Default: `false`



- Suggested options: `false`



- Categroies: `FUZZ`, `COMMAND`
