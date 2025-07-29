# Masterworks

Just another unofficial Tinker's Construct reboot.

## Developer Instructions

Set up a `docs` directory in your project root, and sym-link any documentation directories you want copilot to use.

I recommend at least creating `docs/neoforged` and `docs/neogradle`. Clone them at <https://github.com/neoforged/Documentation>.

## Data Components Format

### Masterworks:Construct

A `Construct` is a dynamic tool's composition, represented by a template, specific variant identifier, and its parts. The parts can be either a material item resource location or another `Construct`.

#### Example: Gold Pickaxe Head

```javascript
"masterworks:construct"={
    "template"="masterworks:pickaxe_head_template",
    "variant"=0,
    "parts"=["minecraft:gold"]
}
```

#### Example: Iron Head + Leather Binding + Wood Handle Axe

```javascript
"masterworks:construct"={
    "template"="masterworks:axe_template",
    "variant"=1,
    "parts"=[
        {
            "template"="masterworks:axe_head_template",
            "variant"=0,
            "parts"=["minecraft:iron_ingot"]
        },
        {
            "template"="masterworks:binding_template",
            "variant"=0,
            "parts"=["minecraft:leather"]
        },
        {
            "template"="masterworks:rod_template",
            "variant"=0,
            "parts"=["minecraft:oak_planks"]
        },
    ]
}
```

#### Example: Diamond-Emerald Blade, Obsidian Guard, Obsidian Handle Sword

```javascript
"masterworks:construct"={
    "template"="masterworks:sword_template",
    "variant"=1,
    "parts"=[
        "blade"={
            "template"="masterworks:sword_blade_template",
            "variant"=1,
            "parts"=[
                {
                    "template"="masterworks:sword_blade_template",
                    "variant"=0,
                    "parts"=["minecraft:diamond"]
                },
                {
                    "template"="masterworks:sword_blade_template",
                    "variant"=0,
                    "parts"=["minecraft:emerald"]
                }
            ],
        },
        "guard"={
            "template"="masterworks:binding_template",
            "variant"=0,
            "parts"=["minecraft:obsidian"]
        },
        "handle"={
            "template"="masterworks:rod_template",
            "variant"=0,
            "parts"=["minecraft:obsidian"]
        }
    ]
}
```
