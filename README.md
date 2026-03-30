# Pick Tool

This is a lightweight mod that allows using the "Pick block" key while holding a tool to swap to the effective tool for the targeted block.

Works with all tools, both vanilla and modded ones.

If there are multiple effective tools for the block in the inventory, consequent pick blocks will cycle through them.

Tools can be excluded from being picked by adding them to the file `picktool.json` located in the config folder. The file supports both singular items and item tags, for example:
- Adding `#minecraft:swords` will ignore all swords
- Adding `minecraft:shears` will ignore shears

The mod is server-side. It works for all players when installed on a dedicated server or LAN host without clients having to install it. It also works in singleplayer.

This mod has no dependencies.

![gif](https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExdW56YTJpaDB3enNzamhrY2xzNGwxcGRpbzM2ejZ5bzNkeHF3NzdidSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/rNyj2hjVIbnOn7s72P/giphy.gif)
