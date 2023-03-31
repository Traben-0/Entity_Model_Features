# Entity Model Features

### "PUBLIC BETA - WORK IN PROGRESS"
#### "DOES NOT CURRENTLY FULLY SUPPORT ALL CEM PACKS, YOUR MILEAGE MAY VARY"


Entity Model Features (EMF) is a new Fabric & Forge mod
that is an Optifine Custom Entity Models (CEM) compatible alternative for Fabric,
and those who no longer use OptiFine due to alternatives such as Sodium, Iris, and ETF.

## Fresh Animations?

As this is a Beta not everything works, however the brilliant Fresh Animations resourcepack almost entirely works out of the box,
as it was the primary focus for this mod, no fork/ pack editing required.
there are 4 things broken in Fresh Animations 1.8 with EMF in its current state:
1. horse saddles are offset incorrectly
2. frog legs are offset incorrectly
3. dolphin fins are rotated incorrectly
4. The addon pack doesn't fully work

Everything else seems to work 100% with FA

# Features & Differences from OptiFine

#Not yet added

1. no sprite support yet
2. .jpms will load but don't tend to position correctly
3. attach = true; behaviour hasn't been finalised but will work to an extent
4. texture overrides being set in the model worked in the Alpha but haven't been readded to the Beta yet
5. Wither, EnderDragon, block entity, minecart and boat models are not supported yet as they havent been a priority but are planned;
6. modded mob .jem support has not been added yet but there are plans
7. an optional emf/cem overriding directory for models to account for emf differences

##Added

1. OptiFine CEM .jem loading 
2. OptiFine CEM model animations
3. OptiFine CEM random models feature
4. OptiFine CEM .JPM loading (partially)
5. EMF adds custom armor models for bipeds
these are formatted such that "MOBNAME_inner_armor.jem" & "MOBNAME_outer_armor.jem" are the model file names, and should work for all the bipeds that wear armor
6. EMF adds player skin CEM support including animations, there are two file names "player.jem" & "player_slim.jem"
7. EMF adds the "is_climbing" animation variable, just cause it can






