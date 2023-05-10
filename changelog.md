
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





