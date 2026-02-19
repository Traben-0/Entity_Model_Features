
- fix arm overrides pre 1.21.9
- fixed armor in 1.21.9+ causing a lot of lag with animated player models loaded
- reduced a portion of the lag introduced by the 1.21.9+ changes
- partially fixed compatibility with Essential Mod cosmetics and animated player models *(currently doesn't work when the root part animates, the fix has been supplied to the Essential team)*
- fixed `is_on_shoulder` in 1.21.9+
- fixed happy ghast harness models `body` not working correctly *(fresh animations also needs to update for this fix)*
- fixed horse armor names not matching OptiFine's
- fixed camel_husk_saddle model
- fixed tadpole models not matching OptiFine
- fixed armor_stand models headwear part visibility being reset in secondary models
- fixed bell model 'base' part not working correctly
- fixed raft models breaking after a resource reload
- fixed the animation LOD setting sometimes breaking for mobs with multiple models *(was quite noticeable on happy ghasts with harnesses)*
- fixed first person hand animations breaking in 1.21.9+


[3.0.11]
- updated cem model names and part names to match `OptiFine 1.21.11_HD_U_J9_pre2`
- added `parrot_shoulder` model
- added models for warm pigs and chickens which don't have unique models in vanilla
- added the model `dragon_fireball` with only a single part `fireball` (EMF ONLY)
- fixed trident models not variating properly on 1.21.9+

[3.0.10]
- fixed armor textures breaking on 3.0.9 in MC 1.21.9+
- fixed entities flickering in some GUIs in older versions, e.g. horses inventory when using Fresh Animations & some player GUIs

[3.0.9]
- neoforge 1.21.11
- fixed model exports incorrectly adding rotations, there is now an option to include rotations in exports if desired but they will not work in game
- fixed the setting to only modify the client players models not properly affecting other players armor models in 1.21.9+
- hid several more model and variable issues from the logs to reduce spam unless model debug logging is enabled
- added OptiFine part name mappings for the models: parched, zombie_horse_armor, camel_saddle, camel_husk, camel_husk_saddle, and the various warden layer models
- added support for PAL and bendable cuboids
- added new animation api method to register a function to control whether an entity should be forced into the vanilla model
- fixed item hand attachments breaking in th left hand when a right hand item was held in 1.21.2+

[3.0.8]
- updated to 1.21.11

[3.0.7]
- caught a NPE crash that has been intentionally left in an EmoteCraft mixin to an EMF method
- added `registerPauseCondition(Function<EMFEntity, Boolean> shouldPause)` to the EMFAnimationApi to better allow other mods to add their own pause conditions for entities rather than mixing in like EmoteCraft did
- fixed chest and some other entity models in the inventory not loading or rendering correctly in 1.21.4+
- fixed the `prevent first person hand animations` player setting not working correctly in 1.21.9+
- fixed 1.21.9+ entity held items to match pre 1.21.9 behaviour when the base model is animated, e.g. fox mouth items positioning
- fixed arm item attachments (position overrides) in 1.21.9+ and in general improved their behaviour for older versions
- fixed held tridents and shields breaking WaveyCapes mod compatibility in 1.20

[3.0.6]
- fixed exports being broken
- matches `random()`s behaviour exactly to optifine
- several fixes, mostly config gui, to EMF that were actually bugs in ETF so go update that too

[3.0.5]
- fixed armor models no longer copying the custom animations of the base entity model in 1.21.9+ (this will not work for armor models that have their own custom animations)
- print functions no longer continue ticking during game pause
- added a shorthand version of the `print()` & `printb()` functions that only accept 1 argument and print every frame with the id being the argument itself

[3.0.4]
- fixed textures not variating correctly on custom models in 1.21.9+ (broke many packs)
- fixed a neoforge black screen issue in 1.21.9+

[3.0.2]
- jpm files will now print loading exceptions to the log even without the logging option enabled
- fixed texture overrides in fallback models not always applying
- improved export files
- added happy Ghast part mappings to match OptiFines Ghast
- fixed trident OptiFine part mappings
- readded culling to texture overrides on arrows and some block entities

[3.0.1]
- fixed the temperate_cow/pig/chicken override models in 1.21.5+ not working correctly
- fixed optifine part name mappings breaking with some override / fallback models
- fixed properties reading incorrectly with some fallback and override models
- fixed the debug right click chat printout fallback models display
- bumped the api version

[3.0.0]
- fixed entities in gui in 1.21.6+
- fixed many crashes
- added optifine format part mappings for new models `sheep_wool_undercoat`, `sheep_baby_wool_undercoat`, `(all the horse types)_saddle`
- fixed the 1.21.2+ optifine chest models being split in 2 not working correctly with emf
- now using the EBE config api to correctly disable EBE blocks when an EMF model for it is loaded
- fixed some model creation log messages still showing regardless of the setting for it being enabled
- reduced the threshold for ignoring spam model creation from other mods *(that incorrectly use entity models)* from 500 attempts to 64
- fixed some models not variating correctly until a resource reload
- first release for 1.20 in a while
- large restructure of the source code
- fixed animation execution ordering when loading animations via jpm files
- fixed part name resolving issue (e.g. `mouth.ty` resolved to `classic_mouth.ty`)
- fixed `baby_wolf_collar.jem`
- removed the fallback behaviour of the new cow, chicken, and pig models from their `cold_` and `warm_` variants they will not fall back to the base model any more
- added `temperate_cow.jem` as an optional override to `cow.jem` on 1.21.5+ to allow pack makers an easier time managing the different versions models, also applies to pigs and chickens
- bones that use custom per-face UV's now properly ignore the `mirrorTexture` field like optifine


[2.4.4]
- added 1.21.6
- fixed relative paths for jpms
- fixed a 1.21.5+ crash with attachments in models
- fixed a crash where an entity was incorrectly assumed to be a Player
- added support for `wolf_baby_collar.jem`


[2.4.3] bug fix and update
- updated to 1.21.5
- added `warm_` and `cold_` model variants for the new pigs, chickens, and cows, they will attempt to fallback to the old model if the new one is not present
- `cold_cow.jem` also has `right_horn` and `left_horn` parts, all other model differences for the 3 mobs are done via cubes only, not new parts
- fixed `ResourceLocationException` log spam when attempting to find modded chest models
- removed all code for the partial physics mod compat option as official physics mod compatibility is coming soon to the physics mod :)
- added a fallback model list to the right click debug log options

[2.4.1]
- fixed 2 settings being reset incorrectly when settings are reset to default via the GUI

[2.4.0]
- added `helmet`, `chestplate`, `leggings`, and `boots` partial armor model support. e.g. `helmet.jem`, each piece can be varied separately e.g. `helmet.properties` (1.21.2+ ONLY!, for now)
- added fallback properties support to match OptiFine behaviour. e.g. `drowned_outer.jem` will vary with `drowned.properties` if `drowned_outer.properties` doesn't exist. (The latter is only possible via EMF as OptiFine will only use `drowned.properties`)
- reduced log spam for certain loading warnings
- prevented some unimportant exceptions from triggering the load error warning toast
- math functions that allow string inputs can now use `\` to escape the `,`, `(`, `)`, & `\` characters, such as when needing a `,` in one of the inputs for `nbt()`. e.g. `nbt(Test,text="this\, that")`
- fixed whitespace characters in functions that allow string inputs
- fixed all minecart variants only using the `minecart` model in 1.21.2+, they now correctly use it as a fallback only
- fixed boat and minecart variant animation parameters
- fixed layer models not rendering in 1.21.2+
- fixed an issue with chest models in 1.21.2+
- fixed some config translation logic
- fixed `move_forward` & `move_strafing` breaking for other entities in multiplayer

[2.3.1]
- updated Forge and NeoForge to 1.21.4
- added a toast message when the reload encountered EMF errors (can be disabled in config)
- added config options to re-log many exceptions encountered during EMF loading, even some not normally logged due to config settings
- fixed `spectral_arrow` model breaking in 1.21.3
- limited `time`, `frame_counter`, `id`, and a few other variables to 27720
- fixed single block chest models not loading properly in 1.21.2+

[2.3.0]
- the remaining 1.21.3 changes should be fairly "stable" now and are unlikely to have significant changes going forwards, likely only semantic ones as OptiFine develops
- improved how EMF exports default models, they will now look much prettier in blockbench due to various semantic only changes
- fixed baby and other model scaling issues in previous 1.21.3 builds, baby models with certain custom animations, using the old offsets, are still broken and need pack makers to update their packs
- updated model & part names, see the `emf > models > all models` settings in game for the most detailed and accurate list of all model & part names
  - dragon - "neck1 - neck5" and "tail1 - tail12", replacing "spine"
  - bogged - same as skeleton + "mushrooms" part
  - bogged_outer - same as stray_outer
  - armadillo - see in-game model settings screen for parts list
  - end_crystal - "inner_glass" and "outer_glass" replacing "glass"
  - arrow - "back, cross_1, cross_2"
  - spectral_arrow - "back, cross_1, cross_2"
  - bee_stinger - "cross_1, cross_2"
  - player_cape - "cape"
  - player_ears - "left_ear" and "right_ear"
  - player_slim - will now fallback to player.jem if not present
  - salmon_large - falls back to "salmon"
  - salmon_small - falls back to "salmon"
  - breeze_wind_charge - same as "wind_charge"
  - boat models now use the naming convention <wood_type>_<boat_type> e.g. "oak_boat" and "oak_chest_boat" and fallback to the old model names e.g. "boat" and "chest_boat"
  - raft and boat models no longer contain the "water_patch" part themselves, this is now a separate model
  - water_patch - "water_patch" also supports boat type variants e.g. "oak_water_patch"
  - minecraft models now all fallback to the default minecart model
  - chest_large - has been split into "chest_left" and "chest_right" models, same for trapped_chests. EMF will retain legacy support for the old model name for the time being
  - wall_sign - falls back to the "sign" model and doesn't have the "stick" part
  - sign, wall_sign, and hanging_sign - all support wood types in the model name e.g. "oak_sign" and "oak_wall_sign" that will fall back to the generic model
  - there may be several more minor changes, see the in-game `emf > models > all models` settings screen for the most accurate and complete list
- added legacy model fallbacks `book`, `evocation_illager`, `evocation_fangs`, and `vindication_illager`
- many other minor tweaks and fixes

[2.2.8]

[2.2.7]
- updated to 1.21.2+

1.21.2+ has a large amount of internal entity rendering changes many of these changes will affect models, see my discord for a write-up I have done on the changes to expect and their impacts
or this link 

https://github.com/Traben-0/Entity_Model_Features/blob/master/.github/1.21.2%2B%20%20changes.md


- removed the setting that would attempt to revert emf models changed by other mods, as it had a lot of overhead with new changes
- the `creaking_transient` entity variant will try and use `creaking_transient.jem` first and will use `creaking.jem` otherwise
- different boat types now use the format <wood_type>_<boat_type>.jem e.g. `oak_boat.jem`, `oak_chest_boat.jem`, and the rafts `bamboo_raft.jem`, `bamboo_chest_raft.jem`
this differs from EMF previously using their vanilla model id such as `boat/oak.jem`
- `baby_inner_armor.jem` and `baby_outer_armor.jem` have been added as fallbacks for the baby armor models
- `player_cape.jem` and `arrow.jem` have completely different implementations now, due to inclusion in vanilla rendering
- `wind_charge` part names updated to reflect changes in OptiFine
- the OptiFine limitation setting to always require a base model for variation has been set as disabled by default, as the limitation is inconsistent in OptiFine and has affected several packs working normally in OptiFine (the config name has changed so this will automatically update for you)

[2.2.6]
- fixed model textures prefixed with `./` & `~/` breaking from a recent change
- expanded the EMF API:
  - added `int getCurrentEMFVariantOfModel(EntityModel<?>)` returns the current variant of the model or -1 if the model is not a custom EMF model.
  - added `boolean isModelAnimatedByEMF(EntityModel<?>)` returns true if the model has custom EMF animations.
  - added `boolean isModelCustomizedByEMF(EntityModel<?>)` returns true if the model is a custom EMF model.
  - added `boolean isModelPartCustomToEMF(ModelPart)` returns true if this model part is an extraneous part added by EMF, and does not represent any actual normal vanilla parts.
  - added `boolean isModelPartAnimatedByEMF(ModelPart)` returns true if this model part itself is animated by EMF, e.g `"modePart.rx":"sin(age)"`.
- fixed some per-entity model override settings from not applying correctly in game, such as modifying render mode per entity type.
- moved the box face UV height/width are zero warnings to only appear when model creation logging is enabled, as it got far too spammy with the many packs that don't care about this.

[2.2.5]
- fixed a crash when arrows render stuck inside a player with a custom model that replaced all their vanilla parts
- fixed arrows stuck inside players not following with the custom animations of the custom player model
- EMF now removes the `floor UVs` OptiFine parity setting in 1.21 and newer versions, as OptiFine now matches EMF's default behaviour. 
- The warning for a cube having 0 UV width and height has been made more verbose, and will also be dependent on the `floor UVs` setting in earlier versions.

[2.2.4]
- fixed `rot_y` giving an incorrect value for specifically the client player model
- fixed the `part` keyword in animations not working correctly for OptiFine part names that differ from the vanilla ones
- fixed texture overrides breaking at the .jem level for most models, due to a broken redundancy optimization

[2.2.3]
- added an optifine cem syntax limitation parity setting
- added a parity setting to enforce custom UV value flooring in boxes like OptiFine does
- added support for referencing `part` directly in animations such as `this` already does
- added more detailed validation warnings to box uv co-ordinates as well as fleshing out validation error messages to inform users of the exact issue source
- added `frame_counter` variable from the latest OptiFine
- added `wolf_collar` to the model list
- made `frame_time` match OptiFine's pausing and tick freeze behaviour
- fixed modded block entity models incorrectly including the namespace in the model file name when it should not e.g. looking for `optifine/cem/mod:model.jem` instead of `mod:optifine/cem/model.jem`
- fixed models with variants not resetting their initial state correctly on first load until at least 1 variation occurs naturally


[2.2.2]
- now requires ETF 6.2.1 or newer
- fixed a model setup error that would break villager and warden, clothing and glowing textures with custom models that declared textures
- is_hover now respects the players current block interaction range for block entities
- id variable now matches OptiFine's value exactly
- reworked the player_cape.jem implementation to be more mod compatible, e.g. `wavey capes` mod can now correctly overwrite it & essentials works with it now
- swapped subfolder load priority for models. `optifine/cem/skeleton/skeleton.jem` now loads before `optifine/cem/skeleton.jem` matching optifine behaviour
- added an OptiFine compat setting to require submodel folders to have variants, e.g. `optifine/cem/skeleton/skeleton.jem` will not load without a `optifine/cem/skeleton/skeleton2.jem` or a valid .properties file.
- model exports no longer print rotation float values as doubles
- model exports now also log all box values for exported and non-exported parts
- fixed texture overrides not applying to `attach=true` or vanilla parts
- fixed animations breaking when the same part transform or variable is declared / updated more than once over all the animation entries
- added tickdelta to the death time variable *(fixes jittering in packs like skeleton death physics)*
- fixed not all instances of `this` being correctly resolved in animation math
- added spelling correction warnings for frequently misspelled variables like `is_aggressive`
- variables not being found now log as errors rather than warnings and will now correctly default to false for unknown booleans

[2.2]
- fixed forge 1.21 & 1.20.6 compat
- fixed issues when used with the Sodium 0.6 betas
- Added a setting to make EMF reset all the vanilla part transforms of player models well before every render.
  - Only applies if custom models are present for the player.
  - This is not the typical behaviour however with how many mods alter player animations
  - this setting vastly reduces the difficulty of player animation compatibility between mods and animation packs.
  - E.G. player emote mods.
  - This compatibility still requires work on the pack makers end.
- added the `nbt(key,test)` boolean animation function it works exactly like the nbt random property such that `models.1.SaddleItem=exits:false` will be `nbt(SaddleItem,exists:false)`
- added boolean animation variable `is_paused` true when the game is paused
- added boolean animation variable `is_hovered` true when the client is looking at the entity or block entity position
- added an option in model debug settings to only render models in debug mode when the mob is hovered over
- added a setting to control whether EMF requires a 'base' model for model variation like OptiFine *(e.g. `pig2.jem` requires a `pig.jem` to work)*. This setting is now enabled by default to promote backwards compatibility and prevent confusion.
- tweaked the `keyframe()` & `keyframeloop()` animation functions to use catmulrom spline interpolation for smooth transitions between keyframes, factoring in the previous and upcoming frames
- fixed villager clothing and profession textures failing to render when a texture override is set in the model
- fixed an issue with `chest_large.jem` due to how it has to be split into 2 half models in game, this removes the log spam about the missing left/right half and also somewhat fixes animations referring to the opposite side of the chest, you are still highly recommended to keep the left and right half animations separate. A config option has been enabled to toggle this fix in case of modded chest model conflicts.
- fixed `slime_outer` models removing translucency when using texture overrides
- fixed `is_swinging_right_arm` and `is_swinging_left_arm`
- added `is_using_item` true when any item is being used, usually you can detect which arm via `is_swinging_right_arm` and `is_swinging_left_arm` but this is not reliable for all item usages
- added `is_holding_item_right` and `is_holding_item_left` to detect if the entity is holding an item in the right or left hand, may not work for all entities that hold items, only tested with players
- added vex charging to `is_aggressive` variable and also made it potentially more consistent with modded mobs
- fixed variation for special secondary or backup models, like `outer_armor.jem`. or old directory modded models.
- fixed the print animation methods accidentally printing during pre-validation
- signs and boats now have optional override models, based on how vanilla separates every sign and boat variant into its own models, these are only identifiable by enabling model creation logging and seeing what they are called there, and if absent will fallback to the optifine defaults.
- improved the file name and location displays in the config list of all models
- added the emf 16x logo to the config gui to replace the full logo
- improvements to texture overrides, emf now strips redundant texture declarations to improve runtime efficiency *(e.g. ignores `pig.png` declared in `pig.jem` as its redundant)*
- fixed an issue with some entity animation values getting reset during entity feature layer rendering and not being restored correctly

[2.1.2]
- fixed the player shadows not animating in first person with iris with custom player models
- fixed a crash when holding tridents, chests, and other custom entity models with texture overrides.

[2.1.1]
- now requires ETF 6.1.3 or newer
- added new EMFAnimationAPI methods to allow other mods to:
  - pause/resume entire custom model animations for an entity
  - pause/resume individual custom model parts from animations for an entity
  - lock/unlock an entity to only use their vanilla models
  - utility methods to cast Entity and BlockEntity into the EMFEntity interface
  these will allow other mods to inhibit EMF animations and models for specific entities when required e.g. for emotes
- fixed EMF applying values to the last part with the given id as opposed to the first *(OptiFine parity)*
- fixed the `scale` model part default setting not correctly applying on mobs with new format animations like wardens & frogs
- fixed a crash when exporting models in 1.21
- fixed a `not building` crash in 1.21
- fixed the `root` part not appearing in model exports
- fixed a bug where item attachments could carry over to other mob renders
- fixed the ender dragon `spine` part not rendering when having a texture override
- the setting that allows EMF to disable EBE settings, when custom block entity models are loaded, now has a much more verbose and informative display in the EMF config warning screen
- fixed the modded model export log using the old `modded` directory format, the export log now also gives a full path for the jem starting from the assets folder
- model exports are now placed inside the `emf/export/assets/` folder in the `.minecraft` directory with a fully correct and namespaced path starting from the assets folder
- fixed the vanilla banner model waving adding on top of EMF animations due to vanilla banners being rendered in multiple stages
- changed `pi` from 3.1415926 to 3.1415927 to match vanilla pi usage and seems to match OptiFine despite the docs
- changed various math methods such as `sin() & cos()` to no longer use java.lang.Math and instead use Minecraft internal math class
- the above 2 changes fixed vindicators in fa 1.9.1
- added support for `inner_armor.jem` & `outer_armor.jem` as fallbacks if an entity specific armor model is not found, e.g. `zombie_inner_armor.jem` & `zombie_outer_armor.jem`
- fixed some missing mobs support for the `is_aggressive` variable
- changed the `height_above_ground` variable to detect blocks with standable top surfaces not just solid blocks
- fixed a bug causing some block entity model texture overrides to appear in first person view in their iris shader shadow pass position
- added an optimization option that will skip recalculating entity model animations during the iris shadow pass, on by default, and should always stay on, it is only an option in case of future breaking iris api changes or weird behaviour with iris ports. 
- fixed the trident model part declaring additional parts in the OptiFine mapping by accident, which could cause vanilla trident parts to re-appear in many custom trident models

2.0.2
- fixed the first person hand settings not getting reset correctly in 2.0.1
- added the `is_swinging_right_arm` & `is_swinging_left_arm` boolean variables, 
used to distinguish right and left arm swinging in biped models, if no arm is swinging both should be false, if `swing_progress > 0` then one of these should be true.
- added support for an optional `player_cape.jem` model file to be used for better animating the player cape
`player_cape.jem` only contains a `cloak` part, positioned exactly as it is in the regular `player.jem`,
  if this model is present it will render in a special way that will not apply the vanilla animations to the cape.
  Allowing much easier custom animations
  *(note this jem file can not be exported by the EMF config, but the `player.jem` exported model will work perfectly for it, as of v2.0.2)*

The only transforms EMF applies to `player_cape.jem` before rendering & custom animating is:
- rotating the cape 180 degrees around the `y` axis. (matching vanilla)
- translating the cape 2 pixels backwards. (matching vanilla)
- translating the cape a further 1 pixel backwards & upwards if the player has a chest-plate on. (matching vanilla)

No other rotations or translations are applied to the cape, allowing for fully custom animations.

The `cloak` part inside the regular `player.jem` will render if `player_cape.jem` is not present, but will not benefit from having its vanilla animation cancelled by EMF



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


1.0.2
- fixed a mistake in the block entity code causing unnecessary lag

1.0

It's time, EMF has come a long way, in 9.5 months, and now seems to be very closely on parity with OptiFine CEM, seeing as it is not
actually OptiFine there will always be some minor issues or discrepancies, please continue to report these as you find them.
However on the whole EMF seems to be ready. 
Some things to note: EMF 1.0 is practically a different mod to the last beta 0.2.13 and the old private alpha, 
Almost the entire model creation code is different and almost all issues reported using old beta versions will be irrelevant going forwards. *(especially if it is about broken models)*
With that being said, many GitHub issues have been left mostly ignored by me if the issue is something that I expected would
be resolved passively during development, I'll be going through these in the coming months to weed out any now irrelevant issues.

(changes from last beta)
- Model creation has been completely rewritten & OptiFine CEM appears to be at parity in EMF and seems to be fully working for almost every single model
- large rework, reimplementation, and internalisation of animation code and model variant checking.
  preventing all occurrences of animation de-sync and fixing animations for all modded/replaced entities/models and
  block entities.
- various stability fixes to model variants including texture support.
- block entities fully supported
- physics mod compat can now choose to try render the vanilla model parts or the custom EMF model parts
- should no longer cause `part not found` crashes or model failures
- reduced the log message length of model loading errors
- fixed emf/ & mobName/mobName directory issues
- animations now support model part name hierarchy as in OptiFine. i.e   `left_arm:hand:finger1` works to separate part `finger1` in the `left_arm` model group from the `right_arm` one
- warnings added to the "prevent model overrides" setting prompting users to disable setting if it causes crashes or to warn users of models being modified by other mods
- added OptiFine name format support for all previously missing entities including sniffer and camel and block entities
- added support for render variables in animations `e.g. render.shadow_size`
- expanded animation model variable support to block entities, seemingly matching OptiFines output for these.
- block entity animations support variables "var.???"
- added various render modes for custom parts, `NORMAL, GREEN FLASHING, WIREFRAME, OFF`, will not affect vanilla parts.
- so much more I've probably forgotten some.
- removed now unnecessary config settings
- leaving the config screen now only triggers a resource reload if a setting has been changed
- added a random entity display to the config screen
- various minor optimizations and code cleanups
- countless fixes caused by the previous beta not having OptiFine parity
