
- fixed a crash "layer is null"

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