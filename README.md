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

## Mod requirements
Please also install the below mods if you can, the obvious ones such as Sodium are not mentioned.

It should also go without saying EMF is NOT compatible with dorianpb's CEM and OptiFabric.
#### Required
- [Entity Texture Features (ETF)](https://modrinth.com/mod/entitytexturefeatures): 
  EMF uses several features from my other mod ETF, so it is required. (primarily used to support the random model feature, the config screen, and allow textures set in models to vary like in OptiFine)

#### Highly recommended
- [Entity Culling](https://modrinth.com/mod/entityculling): This mod does wonders for reducing entity rendering lag, doing even more than Sodium's included entity culling. This is very beneficial when using animation heavy packs like Fresh Animations.


## Fresh Animations?
Yes.

it works :)

<img width="450" src="https://cdn.modrinth.com/data/4I1XuqiY/images/2f32dbeadc25e46ef6c56f0e47e5eb5d305c8ea2.png">

The addon packs for FA 1.8 is still quite broken with EMF in its current state:<br />

Everything else in FA works!

<br>

[Download Fresh Animations](https://www.planetminecraft.com/texture-pack/fresh-animations-v1-0/)

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

## Features & OptiFine differences

Up-to-date feature details page : [Features & Optifine differences](FEATURES.md)


## FAQ

> **Q:** Do all OptiFine CEM resource packs work?

**A:** Most packs work fine, but I'm sure there are some exceptions, report any found issues [here](https://github.com/Traben-0/Entity_Model_Features/issues), or on my [discord](https://discord.com/invite/rURmwrzUcz).

> **Q:** What's different between EMF and dorianpb's [CEM](https://modrinth.com/mod/cem)?

**A:** EMF is in a complete state and has a nearly 1 to 1 parity with OptiFine CEM, EMF also does a few extra things like player animations and armor model support, with more ideas on the way :)

EMF also does things differently under the hood with little to no hardcoding of support for vanilla models. Allowing full CEM support for various modded entities.

> **Q:** Backports?

**A:** Backports to 1.18 & 1.19 are planned.
Backports to version 1.17 and below are not planned at this time.

## License

EMF is licensed under the [GNU Lesser Public License](LICENSE), version 3.0
