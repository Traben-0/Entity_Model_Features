

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