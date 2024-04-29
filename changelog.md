
2.0.1

- added `is_first_person_hand` variable to detect then the model part being rendered is the first person player hand
- added the `player settings` category in the model settings gui
  - added a setting to force custom player models to only apply to your own player and not others in multiplayer
  - added a setting to forcibly prevent all custom hand animations from playing in first person view
  - both are disabled by default
- fixed the `is_sneaking` variable not working correctly, should now line up with all models that have a sneaking pose
- added missing variables to the animation docs
- fixed block entities, and some other edge cases, with texture overrides in the model not rendering at all

2.0

- texture overrides now give the `missing texture` texture if the texture wasn't found instead of not applying
- fixed first person hand jittering with certain player animation resource-packs
- added the `ifb() randomb() catch()` animation functions
  - `ifb()` is a boolean returning version of `if()` 
  - `randomb()` is a boolean returning version of `random()`
  - `catch(x, c, id)` is a new debug function that will always return `x` unless it had an error in which case it will return `c`. `id` is optional and will make the function print the reason `c` was returned to the log with this `id` to identify the printout
- added the `is_jumping` `is_swimming` `is_gliding` `is_right_handed` animation variables
- added the `modelRule modelSuffix var varb global_var global_varb` random properties
- removed the restriction on entity variables making them only accessible from the declaring model *(OptiFine parity)* *(wolf_collar.jem can now correctly access variables set by wolf.jem on the same wolf)*
- added a model display to the model list in the gui to see the model before exporting its .jem file

- added attachment points
- added arrow & spectral_arrow model support
- added new model parts listed in `OptiFine 1.20.4_HD_U_I8_pre3`
- added variables `distance` `is_blocking` `is_crawling` `height_above_ground` `fluid_depth` `fluid_depth_down` `fluid_depth_up`
- added Global variables, they are specified in the format "global_var.<name>" (float) or "global_varb.<name>" (boolean), they are shared by all mobs and block entities and allow more technical things like counting entities
- improved rule_index, now correctly applies 0 when no rule is met
- fixed the `cannot inherit from final class` forge crash returning in v1.3
- moved the variant property into ETF
- fixed crash `.jem failed to load java.lang.NullPointerException: Cannot read the array length because "textureSize" is null`
- added the model file name to crash messages where it was missing
- added `sizeAdd` to the emf model exporting
- the emf model exporting now does 2 export log passes for each model with known OptiFine part names, one pass with, and one without the OptiFine part names. The one without might reveal some extra parts, that do not get used by OptiFine, or have been added by mods 
- EMF model part boxes may now additionally declare `sizeAddX sizeAddY sizeAddZ`, as opposed to just `sizeAdd` with OptiFine, for per axis model inflation. `sizeAdd` will still work as before, though will be overridden if the others are present.
- added OptiFine part name definitions for `breeze`, `breeze_eyes`, `breeze_wind`, `wind_charge`
- fixed case where "!(arms.visible)" works but "!arms.visible" doesn't in 1.3
- the directory format for modded models has been changed from `assets/minecraft/optifine/cem/modded/<namespace>/<modelname>.jem` to `assets/<namespace>/optifine/cem/<modelname>.jem`
  - the old directory with the /modded/ folder is still supported for now but is considered deprecated 
  - this change makes things more consistent with modded name spaces
  - the `emf/cem/` directory is also valid in these namespaces as well as the ability to put the `<modelname>.jem` file within a folder of the same name, such as `cem/<modelname>/<modelname>.jem`
- added a setting for the animation distance LOD setting, to now factor in an entities size to affect larger entities less
- fixed a crash when methods were directly inverted e.g `!between(....)`
- failed animation expressions will now resolve to `0` when applied to a model part or variable *(OptiFine parity)*
- supports ETF's new config screen builder
  - added a category `Animation math details` which lists and explains all functions and variables registered to the animation math system
  - added a category `All models` which allows applying certain settings per .jem file as well as selectively disable the loading of certain models
  - moved a few settings, mostly for the distance LOD, into the new category `performance settings`
  - added settings into the `per entity settings` category to allows applying certain settings to only those entities

1.3
- added the `EMFAnimationApi` which allows other mods to register their own custom animation variables and functions to EMF
  - variables can be added to the `EMFAnimationApi` 
    - via a `FloatSupplier` for simple number variables 
    - via a `BooleanSupplier` for simple boolean variables
    - via a custom variable factory if your variable requires more complex logic, such as reading complex variable names or giving different results for different contexts
  - functions can be added to the `EMFAnimationApi`
    - via a `Function<Float, Float>` for simple functions witn 1 argument
    - via a `BiFunction<Float, Float, Float>` for simple functions with 2 arguments
    - via a `TriFunction<Float, Float, Float, Float>` for simple functions with 3 arguments
    - via a `Function<List<Float>, Float>` for functions with a variable amount of arguments provided via a List<Float>
    - via a  custom function factory if your function requires more complex logic, such as reading the argument strings as a value other than float
- changed the `unknown model printing` option to `Model exporting` and added options to export all models info to a .jem file or the log, not just the unknown / modded ones
  - Models now export `Blockbench` ready with correct pivots, boxes, and uvs.
  - models now export to `.minecraft/emf/export/`
- implemented `is_on_head`, `is_in_hand` & `is_in_item_frame`, which did nothing before
- fixed a `"newstate" is null` crash
- added the `Animation LOD distance` setting which allows you to set the distance at which EMF will start skipping animation frames to save performance
- added the `Retain LOD at low fps` setting which will proportionally reduce the impact of the above LOD setting while the game is running at below 60 fps, as skipped frames can become more noticeable at lower fps
- reworked the config screen to use sliders where appropriate
- added `wolf_armor` to the OptiFine name mappings
- invalid texture overrides will no longer cause the model to fail to load but instead log an error and use the vanilla texture
- EMF log messages are now prefixed with a shorter `[EMF]`
- temporarily disabled texture overrides with tridents as they are broken and difficult to troubleshoot
- added debug render option `Wireframe over texture` which renders both the wireframe and texture of a model together at the same time
- added debug render option `Wireframe flashing over texture` which renders both the wireframe and texture of a model together at the same time but the wireframe fades in and out
- emf now correctly reads all texture overrides with the various relative paths set by optifine e.g. `./`, `~/` etc
- added a config setting *(enabled by default)* that allows EMF to modify the `enhanced block entity (EBE)` mod's config to disable it for block entities that have custom EMF models loaded
- fixed texture overrides acting weird with feature renderers *(fixes Ewan's entity health bars cem models with villagers)*
- added `left_ear` & `right_ear` to `head_piglin.jem` which seems to be correct but missing in OptiFine's documentation :/
- reduced and reworded some of the loading log spam that would worry users *(for example Fresh Animations 1.9 now sends no log errors on load)*
- added EMF only rotational logic functions for radians and degrees `wrapdeg() wraprad() degdiff() raddiff()`
  - the `wrap` functions will wrap a rotation value down to it's smallest identical value, e.g. `wrapdeg(370)` will return `10` & `wrapdeg(350)` will return `-10`
  - the `diff` functions will return the resulting difference between two rotation values factoring in the rotation, e.g. `degdiff(10, 350)` will return `-20` & `degdiff(10, 370)` will return `0` 
- added very many EMF only interpolation animation functions
  - `catmullrom() quadbezier() cubicbezier() hermite() easeinoutexpo() easeinexpo() easeoutexpo() easeinoutcirc() easeincirc()  easeoutcirc()  easeinoutelastic()  easeinelastic()  easeoutelastic()  easeinoutback() easeinback()  easeoutback()  easeinoutbounce()  easeinbounce()  easeoutbounce()  easeinquad()  easeoutquad()  easeinoutquad()  easeincubic()  easeoutcubic()  easeinoutcubic()  easeinquart()  easeoutquart()  easeinoutquart()  easeinquint()  easeoutquint()  easeinoutquint()  easeinsine()  easeoutsine()  easeinoutsine()`
  - hermite & catmullrom have 5 args, the bezier's have 4, the rest have 3, and all are used in the same way as `lerp()` with the delta value being the first argument.
  - this is a good website to get an idea on what most of these look like https://easings.net/
- `e` variable added
- fixes for a certain mixin causing crashes
- EMF now supports modifying modded block entity models which use the vanilla block entity models separately from the vanilla ones
  - *(e.g. the `lootr` mod's chest now tries to read `modded/lootr/special_loot_chest.jem` instead of conflicting with `chest.jem`)*
  - enabling the "print unknown models" setting will print out these examples to the log
- completely rewrote the creation of animation Variables and Functions this should reduce memory usage and also allows other mods to add their own variables and functions
- reworded some of the translations

1.2.2

- added the following animation interpolation methods `easein`,`easeout`,`easeinout`,`cubiceasein`,`cubiceaseout`,`cubiceaseinout`. They function identically to `lerp` however offer different interpolation behaviours
- `shadow_size` in model.jem files should now apply
- more robust wolf_collar.jem implementation
- fixed a crash `layer is null`
- fixed `cannot inherit from final class` crash caused by completely unrelated forge mods missing their dependencies.
- added the `feet` part of the new bat model to the optifine name mappings (might revert if optifine doesn't do this too)
- fixed an issue preventing `mob2.jem` from loading correctly without a `mob.jem` (fixes reimagined's boats textures)
- custom animation variables should now be correctly addressable between different parts (OptiFine parity)
- allowed emf animations to declare the same variable multiple times to update it multiple times within the same frame (OptiFine parity)
- emf updated to use etf 5.2 

1.2.1

- now works with and requires ETF 5.1+ including the same stability fixes
- the `in()` animation method now allows 2 or more parameters, instead of the previous 3 or more *(OptiFine parity)*


1.2.0

- added an EMF only random property to make model variation easier in cases that do not typically need properties in ETF. 
e.g. you don't need a property to check a cat variant in ETF because you are already working with the black cat texture directly. 
the property name is `variant` or `variants`. 
The property allows regex, pattern, or a simple list of variant names. 
If the property starts with `"print:"` it will print the variant found for the entity to the game log, and use the rest of the property text as normal.
This property will work with any modded entity that implements the VariantHolder<T> class and will use the string representation of the type, e.g. "black" for a cat, "oak" for a boat.
This property also works uniquely with these block entities: 
signs (wood), bed (color), shulkerbox (color), Decorated pot (all 4 sherd face types).
For all other regular & block entities it returns the EntityType or BlockEntityType registry id. This allows for the 
separation of different entity types that might use the same model name, such as various modded entities do.

- added NeoForge 1.20.2 support, all prior versions will not have separate jars for forge and neoforge.
- fixed boolean inverted methods
- fixed most, if not, all model variation issues
- much better texture override code
- emissive textures in overrides now work correctly
- reworked for etf rewrite
- added model variation rate setting to emf settings
- fixed the config gui screen getting all black and not displaying the mobs
- added a debug printout setting which will print debug info to chat and the log when an entity is right clicked
- emf now accepts model variants without .properties files, you do not even need to declare a 'default' model,
you can just create villager2.jem and variant #1 will automatically be the vanilla model.
- fixed an issue with held entities such as players holding a chest, messing with further models to be rendered for that entity, such as elytras
- fixed several other minor issues
- fixed texture overrides affecting villager clothing layers also
- texture overrides now use the render layer factory of the underlying model *(fixes villager clothing layers and some others when the base model had textures overrides)*
- ensured texture overrides will not reapply again on models reused multiple times by entities *(e.g. villagers clothes reusing the base villager model)*
- fixed sodium 0.5.4 compat


1.1.0

- fixed `rule_index` variable using the wrong value
- fixed `random()` method breaking when used with no args
- fixed `Non [a-z0-9/._-] character in path` crash with modded entities having model id's that do not create valid Identifiers/ResourceLocations
- fixed wolf collar models only being separate from the base model if a wolf_collar.jem was present *(wolves share their base model with the collar renderer in vanilla)*
- fixed `!` boolean inverting not applying to `varb` variable booleans
- added `keyframe()` & `keyframeloop()` animation methods to simplify keyframe format animations, the format is 
`keyframe(k, a, b, c,...)` with `k` being the linear progress of the keyframes (typically a timer), and all further values being the individual keyframes value.
In practise, k=0 will output the value of keyframe `a`,
k=1 will output the value of keyframe `b`, and k=1.5 will give a linear output halfway between `b` & `c`. In effect `k` is the timer
playing through the keyframes, with each keyframe value at a whole number with `a` being 0. `keyframeloop()` will wrap around
from the final frame back to frame `a` and so on as `k` increases past the last frame. whereas `keyframe()` will only display the last frame for higher `k` values. 
`k` will be treated as a positive number even if negative.
- fixed `print()` & `printb()` methods only printing once if the x value had logically resolved to a constant
- now requires `ETF 4.6.1` or higher
- added `nan` variable that resolves to Float.NaN in runtime mostly for debugging purposes, it being used should make any math function/equation give its failure outcome.


0.2.13

- fixed `IndexOutOfBoundsException` & `already building` crashes related to texture overrides in sub-models
- added an option to select how to handle texture overrides on sub-model parts, whether to rely on EMF's rendering code, an Iris rendering quirk, or to disable them.*(this should almost always be left on default except when debugging texture overrides in sub-models)*


0.2.12

A bug fix update before I backport down to 1.19

- added a workaround to render the vanilla models parts when an entity dies for physics mod compatibility *(THIS IS FAR FROM PERFECT and really just a test)*
- prevented animation variables from invalidating expressions during division testing by defaulting to 0 during setup phase
- tweaked the config screens into categories and altered translations
- extended the option to the setting to print out all unknown model information, to also try and make an example .jem file for that entity model *(it is not a perfect match, they are meant as a starting point for you)*
- fixed the option to try and force EMF models back onto modified entities, triggering for entities without emf models
- fixed limb_speed idling at the minimum float value it should now idle at 0 instead
- improved texture overrides when used internally within models *(no longer relies on a broken behaviour of iris)* *(will have lighting glitches when used on mobs that have glowing eyes or ETF emissive textures until ETFs next update)**(isn't identical between 1.19 and 1.20)*


It's time, EMF has come a long way, in 9.5 months, and now seems to be very closely on parity with OptiFine CEM, seeing as it is not
actually OptiFine there will always be some minor issues or discrepancies, please continue to report these as you find them.
However on the whole EMF seems to be ready. 
Some things to note: EMF 1.0 is practically a different mod to the last beta 0.2.13 and the old private alpha, 
Almost the entire model creation code is different and almost all issues reported using old beta versions will be irrelevant going forwards. *(especially if it is about broken models)*
With that being said, many GitHub issues have been left mostly ignored by me if the issue is something that I expected would
be resolved passively during development, I'll be going through these in the coming months to weed out any now irrelevant issues.

0.2.11
Did someone say mod compatibility?
This update adds a few features regarding `EMF` and other mods the most notable of which being an initial trial of modded entity support.
The following rules for modded entities are subject to change at any time during the beta.
As a rule of thumb all modded entities jem files will need to be placed at `optifine/cem/modded/{NAMESPACE}/{MOBNAME}.jem`
To see if a modded entity can be altered by EMF enable the option to print modded entity model details to the game log, 
if it shows up in the log it will give you the .jem file path and all the root part names and their default values to work with.
If the mod creator for some reason gave the entity a different type name from its model name they will not currently animate, I have some plans for this eventually.

Secondly this change opens up the entirety of the minecraft entity model loading system to be customized via jem files safely without restriction,
for example I have yet to add any code specifically to model or animate signs but the modded printout option will reveal that 
`optifine/cem/sign/oak.jem` is a detected valid model file for signs now, this will be true for many unexpected vanilla models 
such as `elytra` and `block entities`. **HOWEVER** if they appear in this printout it means they are effectively "unknown" models,
they **DO NOT** yet use the OptiFine part names and are very unlikely to animate. 
The OptiFine part names are something I need to add manually because they differ from the internal code names.
And all of these modded or unknown models that are not the main model of a LivingEntity need to have animation support manually added in the code.

There is also a new option, enabled by default, to try and force the game to use the CEM models for vanilla entities even 
if another mod has tried to replace them, I'm not too sure how well this will work with all mods, but it seems to work nicely with the few I've tested with

- added modded entity support and, as a byproduct, "model only" support for every single entity model in the vanilla game
- added an option to print out part_name and default pivot/rotation/scale details for all modded or unknown entity models
- added an option to try and force `EMF` models to not be overridden by other mods *(probably won't work with all mods)*
- fixed several issues with variant models breaking *(random models feature)*
- fixed vanilla model rendering option not working on forge

0.2.10
- 1.20 update
- fixed an issues with the max() animation method
- added a temporary config option to experiment with how EMF handles resetting its data (intended to be used to troubleshoot with users when a known bug occurs that I haven't yet been able to replicate myself)


0.2.9
- emf/cem/ override directory added, .jem files here will be used before optifine/cem/ is checked
- optifine/cem/<entity_name>/... directory support added
- .jpm positioning vastly improved, now Fresh Animations official addon pack fully works, some others don't fully work
- now requires ETF 4.4.4 or newer
- enabled some block entities for testing (mob heads,shulker boxes, and single chests) they don't animate
- mapped some other optifine name differences to fix these models: llama spit, shulker bullets


0.2.8
- made ETF a required dependency *(Consider EMF now as an ETF addon, allowing you to still use other model mods without losing ETF)*
- EMF now has a basic config screen on both Fabric and Forge 
- added model texture override support
- re-added option to render the vanilla model underneath the custom one, or offset from it *(they will not animate and are meant to help pack makers position their models)*
- random model variations now update according to ETF's texture update rate setting
- fixed `frame_time` value it was about 2 times too large resulting in some animation transitions or counters playing faster
- added animation variables `day_time`, `day_count`, and tweaked `time`
- added `anger_time_start` and included tickDelta smoothing in `anger_time`
- fixed logical error in animation math expressions where "!(boolean)" was different to "!boolean"
- green render mode now pulses to allow accurate viewing of texture colours
- added an implementation of `move_forward` & `move_strafing` which are broken in OptiFine, in EMF these values relate 
how much the entities total movement is in a particular direction. Using a players [wasd] movement as reference a 
player holding `W` has a `move_forward` of 1, and a player holding `S` has a `move_forward` of -1, and a player 
holding `D` has a `move_strafing` of 1, and a player holding `A` has a `move_strafing` of -1, and a player holding `WD`
has a `move_forward` & `move_strafing` of roughly 0.7 in both . this value is independent of speed and is intended as a measurement 
of the directionality of an entities' movement.



0.2.7

- fixed all remaining base Fresh animations issues, **yes ***ALL*** of them**, *(the addons are still broken as are .jpms)*
- fixed several positional issues with models in general, almost every default generated model from blockbench matches exactly in game
- fixed flickering in pause menu for animations that factor in tick delta
- fixed texture size issues plaguing wardens and guardians in their blockbench default models
- added wither and ender dragon support, *(ender dragon doesn't support custom animations yet and when they do they will be limited)*

0.2.6

- fixed an issue with the head_yaw constraint fix in 0.2.5
- ensured floating point value consistency across all animation expression code
- fixed a IncompatibleClassChangeError crash with EMF when loading forge with unrelated mod dependency issues
- fixed a model creation crash
- improved non animated horse and ravager head model positioning, fixed horse saddle positioning, horse chests still broken
- added translations fr_fr, de_de, & el_gr


0.2.5

- fixed random models only changing the animations correctly not the models themselves
- fixed head_yaw not constraining to 360 degrees in some cases *(the cause of some weirdness in my animated player model)*
- added code to redirect modded entities for their own jem loading to be implemented in future *(this should fix crashes caused by modded mobs like "player_companions:pig" as emf used to think it was a regular pig )*
- fixed mod compatibility flags to prevent users loading EMF alongside OptiFabric or CEM as this is not supported





