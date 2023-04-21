
0.2.6

- fixed an issue with the head_yaw constraint fix in 0.2.5
- ensured floating point value consistency across all animation expression code
- fixed a IncompatibleClassChangeError crash with EMF when loading forge with unrelated mod dependency issues
- fixed a model creation crash
- improved non animated horse and ravager head model positioning, fixed horse saddle positioning, horse chests still broken



0.2.5

- fixed random models only changing the animations correctly not the models themselves
- fixed head_yaw not constraining to 360 degrees in some cases *(the cause of some weirdness in my animated player model)*
- added code to redirect modded entities for their own jem loading to be implemented in future *(this should fix crashes caused by modded mobs like "player_companions:pig" as emf used to think it was a regular pig )*
- fixed mod compatibility flags to prevent users loading EMF alongside OptiFabric or CEM as this is not supported





