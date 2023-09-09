<div align="center">

<img src="icon.png" alt="EMF icon" width=300>

# Entity Model Features
 
[![Modrinth downloads](https://img.shields.io/modrinth/dt/entity-model-features?color=00AF5C&label=downloads&style=round&logo=modrinth)](https://modrinth.com/mod/entity-model-features)
[![CurseForge downloads](https://cf.way2muchnoise.eu/full_844662_downloads.svg)](https://curseforge.com/minecraft/mc-mods/entity-model-features)

[![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)](https://modrinth.com/mods?e=client)
[![Discord](https://img.shields.io/discord/950942125225283634?color=blue&logo=discord&label=Discord)](https://discord.com/invite/rURmwrzUcz)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/traben)

Entity Model Features (EMF) is a Fabric, Quilt & Forge mod that adds support for OptiFine's Custom Entity Models (CEM).<br />It's designed for anyone who wants to use the CEM resource pack features but also use mods such as Sodium, Continuity or ETF.

<img src="https://cdn.modrinth.com/data/4I1XuqiY/images/49f5b98dfef5b073a3971750673d343e1f92efe5.png" alt="EMF in use" width=550>

</div>


## Required Mods
Please also install the below mods if you can, the obvious ones such as Sodium are not mentioned.

It should also go without saying EMF is NOT compatible with dorianpb's CEM and OptiFabric.
#### Required
- [Entity Texture Features (ETF)](https://modrinth.com/mod/entitytexturefeatures): 
  EMF uses several features from my other mod ETF, so it is recommended *(and will one day soon be mandatory for some things I have planned)*

#### Highly recommended
- [Entity Culling](https://modrinth.com/mod/entityculling): This mod does wonders for reducing entity rendering lag, doing even more than Sodium's included entity culling.


## Fresh Animations?
Yes.

it works :)

<img align="right" width="450" src="https://cdn.modrinth.com/data/4I1XuqiY/images/2f32dbeadc25e46ef6c56f0e47e5eb5d305c8ea2.png">

[Download Fresh Animations](https://www.planetminecraft.com/texture-pack/fresh-animations-v1-0/)


## Roadmap

*(these are roughly in order but are all subject to change at any time during development)*

- [X] `.jem` loading 
- [X] Model animations
- [X] Random models
- [X] Re-add texture overrides
- [X] Prep for block entity support
- [X] Animations and Random models working in all circumstances
- [X] Support for block entity, minecart and boat models
- [X] Support modded mob `.jem` models
- [X] Add an optional EMF/CEM overriding directory for models to account for EMF differences
- [X] Fix `attach = true` cases
- [X] Position `.jpm`s correctly
- [X] Fix remaining model issues
- [ ] Sprite support
- [X] Full parity with OptiFine CEM
- [ ] Backport to still commonly used older MC versions e.g. 1.18


### EMF _exclusive_ features

- Custom armor models for bipeds 
`File names: "MOBNAME_inner_armor.jem" & "MOBNAME_outer_armor.jem"`
- Player skin CEM support including animations, including slim skin! 
[[Example player model pack without animations]](Vanilla_player_models_No_animations.zip)
`File names: "player.jem" & "player_slim.jem"`
- The `is_climbing` animation variable 😈

## FAQ

> **Q:** Do all OptiFine CEM resource packs work?

**A:** Most packs work fine, but I'm sure there are some exceptions, report any found issues [here](https://github.com/Traben-0/Entity_Model_Features/issues), or on my [discord](https://discord.com/invite/rURmwrzUcz).

> **Q:** What's different between EMF and dorianpb's [CEM](https://modrinth.com/mod/cem)?

**A:** EMF is complete, released and boasts a practically 1 to 1 parity with OptiFine CEM, EMF also does a few extra things like player animations and armor model support, with more ideas on the way :)

> **Q:** Backports?

**A:** Backports to 1.18 & 1.19 are planned.
Backports to version 1.17 and below are not planned at this stage.

## License

EMF is licensed under the [GNU Lesser Public License](LICENSE), version 3.0
