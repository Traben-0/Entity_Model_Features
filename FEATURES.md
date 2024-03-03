<div align="center">

<img src="icon.png" alt="EMF icon" width=300>

# Entity Model Features
 
[![Modrinth downloads](https://img.shields.io/modrinth/dt/entity-model-features?color=00AF5C&label=downloads&style=round&logo=modrinth)](https://modrinth.com/mod/entity-model-features)
[![CurseForge downloads](https://cf.way2muchnoise.eu/full_844662_downloads.svg)](https://curseforge.com/minecraft/mc-mods/entity-model-features)

[![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)](https://modrinth.com/mods?e=client)
[![Discord](https://img.shields.io/discord/950942125225283634?color=blue&logo=discord&label=Discord)](https://discord.com/invite/rURmwrzUcz)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/traben)


</div>


# EMF features list:

### Please note "CEM" refers to OptiFine's *"Custom Entity Models"* feature
For all features that are described as matching OptiFine you may follow the OptiFine documentation here:
- [OptiFine CEM pretty documentation](https://optifine.readthedocs.io/cem.html)
- [OptiFine CEM plaintext documentation](https://github.com/sp614x/optifine/tree/master/OptiFineDoc/doc)

# EMF _exclusive_ features

### Custom armor models for all biped mobs
File names for armor: `"MOBNAME_inner_armor.jem" & "MOBNAME_outer_armor.jem"`

E.G. `"zombie_inner_armor.jem" & "zombie_outer_armor.jem"`

The inner armor is the thinner armor layer used by leggings, whereas the outer armor layer is the model used by the rest of the armor

Armor jem models have the following model parts
 - `head, headwear, body, left_arm, right_arm, left_leg, right_leg`

### Player model CEM support

#### *please note that BlockBench's player model found in it's "unsupported model" section incorrectly names the arm_sleeve parts as arm_sleve, you need to change it via a text editor*

Player models are fully supported by EMF and can be animated and varied using the random model feature of OptiFine

`File names: "player.jem" & "player_slim.jem"`

Player jem models have the following model parts
- `head, headwear, body, left_arm, right_arm, left_leg, right_leg, ear, left_sleeve, right_sleeve, left_pants, right_pants, jacket, cloak`
  
*(yes "ear" is a valid part,all players technically have the DeadMau5 ears in their model)*

cloak does indeed refer to the player cape however does not animate currently

### EMF only animation variables

- The `is_climbing` animation variable, true when the entity is climbing.
- The `move_forward` & `move_strafing` animation variables actually have a function, they function as a measurement of the directionality of a mobs movement relevant to it's facing direction.
  - `move_forward` & `move_strafing` are the `Y` & `X` co-ordinates for where the entities current horizontal movement vector, compared from their head angle, intersects with the circumference of a unit circle. *(with positive Y being the entities facing direction and positive X being 90 degrees to the right of that )*
  - `move_forward` ranges from -1 to 1 and is a measure of how much of an entities movement is directed in their looking direction. 
    - *(A player holding only `w` will have a `move_forward` of 1)* 
    - *(A player holding only `a` or `d` will have a `move_forward` of 0)*
    - *(A player holding only `s` will have a `move_forward` of -1)*
  - `move_strafing` ranges from -1 to 1 and is a measure of how much of an entities movement is directed 90 degrees to the right of their looking direction.
    - *(A player holding only `d` will have a `move_strafing` of 1)*
    - *(A player holding only `w` or `s` will have a `move_strafing` of 0)*
    - *(A player holding only `a` will have a `move_strafing` of -1)*
  - A player moving diagonally from their moving direction will have both `move_forward` & `move_strafing` cap out around 0.707.
  - **TL;DR** if you add both a "moving forwards" & a "strafing to the right" animation you can effectively multiply them by `move_forward` & `move_strafing` respectively to get a smooth blend between the two when the models moves around.
- The `nan` variable will resolve to Float.NaN at runtime and is useful for debugging maths expressions
- The `e` variable referring to the base of the natural logarithm

### EMF only animation functions

- The `keyframe()` & `keyframeloop()` animation functions simplify keyframe format animations, the format is
  `keyframe(k, a, b, c,...)` with `k` being the linear progress of the keyframes (typically a timer), and all further values being the individual keyframes value.
  In practise, k=0 will output the value of keyframe `a`,
  k=1 will output the value of keyframe `b`, and k=1.5 will give a linear output halfway between `b` & `c`. In effect `k` is the timer
  playing through the keyframes, with each keyframe value at a whole number with `a` being 0. `keyframeloop()` will wrap around
  from the final frame back to frame `a` and so on as `k` increases past the last frame. whereas `keyframe()` will only display the last frame for higher `k` values.
  `k` will be treated as a positive number even if negative.
- dozens of interpolation animation functions, functioning just like `lerp()` but with different easing curves.
  - `easeinoutexpo() easeinexpo() easeoutexpo() easeinoutcirc() easeincirc()  easeoutcirc()  easeinoutelastic()  easeinelastic()  easeoutelastic()  easeinoutback() easeinback()  easeoutback()  easeinoutbounce()  easeinbounce()  easeoutbounce()  easeinquad()  easeoutquad()  easeinoutquad()  easeincubic()  easeoutcubic()  easeinoutcubic()  easeinquart()  easeoutquart()  easeinoutquart()  easeinquint()  easeoutquint()  easeinoutquint()  easeinsine()  easeoutsine()  easeinoutsine()`
  - all are used in the same way as `lerp()` with the `delta` value being the first argument followed by `start` and `end`.
  - this is a good website to get an idea on what most of these look like https://easings.net/ *(`lerp()` for reference is just a straight line)*
- some more complex interpolation methods `catmullrom() quadbezier() cubicbezier() hermite()`
  - `catmullrom()` has 5 arguments, and is a spline interpolation method that uses the previous and next keyframes to calculate the current value, it is useful for creating smooth curves between keyframes.
  - `quadbezier()` & `cubicbezier()` have 4 arguments, and are used to create quadratic and cubic bezier curves respectively, they are useful for creating smooth curves between keyframes.
  - `hermite()` has 5 arguments, and is a spline interpolation method that uses the previous and next keyframes to calculate the current value, it is useful for creating smooth curves between keyframes.
  - these methods are used similar to `lerp()` with the delta value being the first argument.
- rotational logic functions for radians and degrees `wrapdeg() wraprad() degdiff() raddiff()`
  - the `wrap` functions will wrap a rotation value down to it's smallest identical value, e.g. `wrapdeg(370)` will return `10` & `wrapdeg(350)` will return `-10`
  - the `diff` functions will return the resulting difference between two rotation values factoring in the rotation, e.g. `degdiff(10, 350)` will return `-20` & `degdiff(10, 370)` will return `0`


### EMF modded entity CEM support

EMF supports custom entity models for all modded entities who's model factories appear through the proper channels, this means mods using other custom model mods are not likely to be supported.

To find out if your modded entity is supported follow the instructions in the **"EMF model exporting option"** section found below.

These modded CEM models support all CEM features and can be varied by random models and animated fully. 

Please keep in mind that for all models known to OptiFine CEM their part names are changed from what they use in the game code, 
so you will find that a `modded/cow.jem` will have very different part names to `cow.jem` even if the modded one simply copies the vanilla cow model.

### EMF additional vanilla CEM
 As explained above EMF can identify unknown models and automatically supports them, with that being said a few Vanilla models not present in OptiFine CEM also get captured by this system. 
 
These are: `(modelname # parts)`
- `shield.jem` # *plate, handle*
- `elytra.jem` # *right_wing, left_wing*
- `spin_attack.jem` # *box*
- models not yet released in OptiFine such as the Breeze mob's various models


### EMF model exporting option

This option will export in-game entity models if they are EMF compatible.

open the EMF settings screen and open the `Tools` screen, you will then see a slider labelled `Export models`.
If you enable this option for all or unknown models and then leave the settings screen, the loading screen will then appear as the game reloads all resources *(including models)*
EMF will then write details into the game log of all compatible entity models found during this reload.

Depending on what option you set for the setting EMF will either:
- do this for all models found in the game
- do this for all models found in the game that are not already known to OptiFine CEM *(i.e unknown models)*
- just log the found models information including: part names, .jem file location, possible default pivots & other model values
- log this info **AND** create an example `.jem` model for the model and place it in `MC_DIRECTORY/emf/export/`.
  *(these models will have correct pivots boxes and texture uv's and are immediately ready for use in BlockBench)*


### EMF only random property for model variation (1.2+)

*usable in .properties files used to variate models*
*(technically it also works for .properties used to vary textures but this is almost always useless in practise)*

EMF  adds a random property to make model variation easier in cases that do not typically need properties in ETF.
e.g. you don't need a property to check a cat variant in ETF because you are already working with the black cat texture directly.
the property name is `variant` or `variants`. 
The property allows regex, pattern, or a simple list of variant names.
If the property starts with "print:" it will print the variant found for the entity to the game log, and use the rest of the property text as normal.
This property will work with any modded entity that implements the `VariantHolder` class and will use the string representation of the type, e.g. "black" for a cat, "oak" for a boat.
This property also works uniquely with these block entities: 
- signs (wood)
- bed (color)
- shulkerbox (color)
- Decorated pot (all 4 sherd face types)
- skulls(appending facing direction at the end)

For all other regular & block entities it returns the EntityType or BlockEntityType registry id.
This allows for the separation of different entity types that might use the same model name, such as various modded entities do.

### ETFAnimationApi

EMF has a built-in animation API that allows for the registering of custom animation functions and variables by other mods.

This API can be found [here](common/src/main/java/traben/entity_model_features/EMFAnimationApi.java), and it and some relevant classes have JavaDocs for documentation.



# OptiFine working features

All the listed features below are present in EMF and work just like in OptiFine, with a few exceptions in the next category

- .jem CEM
- .jpm CEM parts
- CEM animations
- CEM random models
- CEM for all entities & block entities

# OptiFine missing features

- some animation variables *(especially for block entities & non-living entities)* might be a bit off from their values in OptiFine
- Sprites are not currently supported
- Non-CEM features. EMF only supports OptiFine CEM. For other features you will need other mods, like ETF which supports OptiFine random and emissive entity textures.
- possibly more that I've forgotten to mention at this time, let me know if you find something that doesn't work and isn't listed here :)


# Known Bugs

These issues are known and do not need to be reported

- trident.jem does not support declaring a texture override in the model at this time and will use the default texture *(This texture is still variable via ETF)*

# Known mod compatibility issues

### cannot be fixed
All or some of EMF's features will not work with these mods and cannot be fixed.
- `OptiFine` - incompatible
- `OptiFabric` - incompatible
- `dorianpb's CEM mod` - incompatible

### have workarounds

- `EBE enhanced block entities` - there is a setting in EMF that will disable certain EBE settings if you are using an EMF model for that block entity
- `Physics mod` - while it will never be perfect there are some settings in EMF to enable some limited compatibility with this mod's death ragdolls



