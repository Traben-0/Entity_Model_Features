<div align="center">

<img src="https://raw.githubusercontent.com/Traben-0/Entity_Model_Features/master/fabric/src/main/resources/icon.png" alt="EMF icon" width=200>

# Entity Model Features
 
[![Modrinth downloads](https://img.shields.io/modrinth/dt/entity-model-features?color=00AF5C&label=downloads&style=round&logo=modrinth)](https://modrinth.com/mod/entity-model-features)
[![CurseForge downloads](https://cf.way2muchnoise.eu/full_844662_downloads.svg)](https://curseforge.com/minecraft/mc-mods/entity-model-features)

[![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)](https://modrinth.com/mods?e=client)
[![Discord](https://img.shields.io/discord/950942125225283634?color=blue&logo=discord&label=Discord)](https://discord.com/invite/rURmwrzUcz)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/traben)

Entity Model Features (EMF) is a Fabric, Quilt & Forge mod that adds support for OptiFine's Custom Entity Models (CEM).<br />It's designed for anyone who wants to use the CEM resource pack features but to use mods such as Sodium, Continuity or ETF.

<img src="https://cdn.modrinth.com/data/4I1XuqiY/images/49f5b98dfef5b073a3971750673d343e1f92efe5.png" alt="EMF in use" width=550>

</div>

## ⚠️ 🚧 Work in Progress!

EMF is in a beta stage, meaning some things will probably work, but is expected for many not to. Before reporting an issue, make sure to read [the roadmap](README.md#Roadmap) and check [the active issues](https://github.com/Traben-0/Entity_Model_Features/issues). If your issue is mentioned there, don't report it and wait for it to be implemented. If not, report it in the [Issues section](https://github.com/Traben-0/Entity_Model_Features/issues/new/choose).

## Recommended mods
Please also install the below mods if you can, the obvious ones such as Sodium are not mentioned.

It should also go without saying EMF is incompatible with dorianpb's CEM and OptiFabric.
#### highly recommended
- [Entity Texture Features (ETF)](https://modrinth.com/mod/entitytexturefeatures): 
  EMF uses several features from my other mod ETF so it is recommended *(and will one day soon be mandatory for some things I have planned)*

- [Entity Culling](https://modrinth.com/mod/entityculling): This mod does wonders for reducing entity rendering lag, doing even more than Sodium's included entity culling. I cannot recommend this enough.

#### Optional
- [YetAnotherConfigLib (YACL)](https://modrinth.com/mod/yacl): EMF fabric uses YACL to create a settings screen for the time being, eventually it'll copy the ETF config screens to display on forge and fabric

## Recommended mods
Please also install the below mods if you can, the obvious ones such as Sodium are not mentioned.

It should also go without saying EMF is incompatible with dorianpb's CEM and OptiFabric.
#### highly recommended
- [Entity Texture Features (ETF)](https://modrinth.com/mod/entitytexturefeatures): 
  EMF uses several features from my other mod ETF so it is recommended *(and will one day soon be mandatory for some things I have planned)*

- [Entity Culling](https://modrinth.com/mod/entityculling): This mod does wonders for reducing entity rendering lag, doing even more than Sodium's included entity culling. I cannot recommend this enough.

#### Optional
- [YetAnotherConfigLib (YACL)](https://modrinth.com/mod/yacl): EMF fabric uses YACL to create a settings screen for the time being, eventually it'll copy the ETF config screens to display on forge and fabric

## Mod requirements
Please also install the below mods if you can, the obvious ones such as Sodium are not mentioned.

It should also go without saying EMF is incompatible with dorianpb's CEM and OptiFabric.
#### Required
- [Entity Texture Features (ETF)](https://modrinth.com/mod/entitytexturefeatures): 
  EMF uses several features from my other mod ETF so it is recommended *(and will one day soon be mandatory for some things I have planned)*

#### Highly recommended
- [Entity Culling](https://modrinth.com/mod/entityculling): This mod does wonders for reducing entity rendering lag, doing even more than Sodium's included entity culling. I cannot recommend this enough.


## Fresh Animations?

<img align="right" width="450" src="https://cdn.modrinth.com/data/4I1XuqiY/images/2f32dbeadc25e46ef6c56f0e47e5eb5d305c8ea2.png">

Since EMF is in beta, not everything works. However, the brilliant [Fresh Animations](https://www.planetminecraft.com/texture-pack/fresh-animations-v1-0/) resource pack works out of the box, as it was the primary focus for this mod, no fork/pack editing required.<br />

The addon packs for FA 1.8 is still quite broken with EMF in its current state:<br />

Everything else in FA works!

## Roadmap

*(these are roughly in order but are all subject to change at any time during development)*

- [X] `.jem` loading 
- [X] Model animations
- [X] Random models
- [ ] Re-add model overrides as the beta currently injects into the vanilla models *(this will likely substantially fix many reported compatibility issues)*
- [X] Re-add texture overrides
- [ ] Support for block entity, minecart and boat models
- [ ] Support modded mob `.jem` models
- [ ] Add an optional EMF/CEM overriding directory for models to account for EMF differences
- [ ] Fix `attach = true` cases
- [ ] Position `.jpm`s correctly
- [ ] Fix remaining model issues
- [ ] Sprite support
- [ ] Full parity with OptiFine CEM
- [ ] Backport to still commonly used older MC versions e.g. 1.16


### EMF _exclusive_ features

- Custom armor models for bipeds 
`File names: "MOBNAME_inner_armor.jem" & "MOBNAME_outer_armor.jem"`
- Player skin CEM support including animations, including slim skin! 
[[Example player model pack without animations]](Vanilla_player_models_No_animations.zip)
`File names: "player.jem" & "player_slim.jem"`
- The `is_climbing` animation variable 😈

## FAQ

> **Q:** What's different between EMF and dorianpb's [CEM](https://modrinth.com/mod/cem)?

**A:** OptiFine CEM is a tricky and time-consuming thing to reproduce, with all of its weird quirks, both EMF and dorianpb's CEM can be entirely broken depending on the resource pack models loaded.

Keeping this in mind, EMF was built with a primary focus on Fresh Animations as the working goal. It's the reason why FA works better than most other resource packs in EMF, without need for a fork or an alternative resource pack. EMF also utilises ETF's random property reading to enable support for random entity models, mimicing the latest OptiFine behaviour.

For all other packs you might find one mod or the other works best for the time being. I **do intend** to close the gap between EMF and OptiFine but it has proven to be quite difficult (There is a reason these mods have taken a while to be functional on Fabric 💀)

(P.S: I also really wanted custom player model & animation support, which EMF adds :P)

> **Q:** Do all CEM resource packs work?

**A:** Not yet, but that's the plan.

> **Q:** Why is OptiFine CEM so hard to reproduce?

**A:** Customizing and changing entity models in Minecraft with a mod is actually quite easy to do when you know what to do (I pretty much had custom models loading within the first few hours of work on this mod). However, OptiFine does things quite differently, in a confusing way at times. So, reverse engineering is essential, and _quite_ hard. EMF attempts to add all CEM features while being compatible as much as possible.

> **Q:** Backports?

**A:** Once EMF reaches a stable state, backports will then be made regularly, to still popular Minecraft versions such as 1.16 & 1.18.

## License

EMF is licensed under the [GNU Lesser Public License](LICENSE), version 3.0
