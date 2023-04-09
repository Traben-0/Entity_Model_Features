<div align="center">

<img src="https://raw.githubusercontent.com/Traben-0/Entity_Model_Features/master/fabric/src/main/resources/icon.png" alt="EMF icon" width=200>

# Entity Model Features
 
[![Modrinth downloads](https://img.shields.io/modrinth/dt/entity-model-features?color=00AF5C&label=downloads&style=round&logo=modrinth)](https://modrinth.com/mod/entity-model-features)
[![CurseForge downloads](https://cf.way2muchnoise.eu/entity-model-features.svg)](https://curseforge.com/minecraft/mc-mods/entity-model-features)

[![Enviroment](https://img.shields.io/badge/Enviroment-Client-purple)](https://modrinth.com/mods?e=client)
[![Discord](https://img.shields.io/discord/950942125225283634?color=blue&logo=discord&label=Discord)](https://discord.com/invite/rURmwrzUcz)

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/traben)

Entity Model Features (EMF) is a Fabric, Quilt & Forge mod that adds support for OptiFine's Custom Entity Models (CEM). It's designed for anyone who wants to use the CEM resource pack features but to use mods such as Sodium, Continuity or ETF.

</div>

## ⚠️ 🚧 Work in Progress!

EMF is in a beta stage, meaning some things will probably work, but is expected for many not to. Before reporting an issue, make sure to read [the roadmap](README.md#Roadmap) and check [the active issues](https://github.com/Traben-0/Entity_Model_Features/issues). If your issue is mentioned there, don't report it and wait for it to be implemented. If not, report it in the [Issues section](https://github.com/Traben-0/Entity_Model_Features/issues/new/choose).

## Fresh Animations?

<div align="center"><img src="https://cdn.modrinth.com/data/4I1XuqiY/images/2f32dbeadc25e46ef6c56f0e47e5eb5d305c8ea2.png" alt="Fresh Animations in use" width=650></div>

Since EMF is in beta, not everything works. However, the brilliant [Fresh Animations](https://www.planetminecraft.com/texture-pack/fresh-animations-v1-0/) resource pack **almost entirely** works out of the box, as it was the primary focus for this mod, no fork/pack editing required.<br />
There are 4 things broken in Fresh Animations 1.8 with EMF in its current state:<br />

1. Horse saddles are offset incorrectly
2. Frog legs are offset incorrectly
3. Dolphin fins are rotated incorrectly
4. The FA add-on pack doesn't fully work

Everything else seems to work 100%!

## Roadmap

- [X] `.jem` loading 
- [X] Model animations
- [X] Random models
- [ ] Sprite support
- [ ] Position `.jpm`s correctly
- [ ] Fully implement `attach = true`
- [ ] Re-add texture overrides
- [ ] Support for Wither, Ender Dragon, block entity, minecart and boat models
- [ ] Support modded mob `.jem`s
- [ ] Add an optional EMF/CEM overriding directory for models to account for EMF differences

### EMF _exclusive_ features

- Custom armor models for bipeds
- Player skin CEM support including animations, including slim skin!
- The `is_climbing` animation variable 😈

## FAQ

 > **Q:** What's different between EMF and dorianpb's [CEM](https://modrinth.com/mod/cem)?

**A:** OptiFine CEM is a tricky and time consuming thing to reproduce, with all of its weird quirks, both EMF and dorianpb's CEM can be entirely broken depending on the resource pack models loaded.

Keeping this in mind, EMF was built with a primary focus on Fresh Animations as the working goal. It's the reason why FA works better than most other resource packs in EMF, without need for a fork or an alternative resource pack. EMF also utilises ETF's random property reading to enable support for random entity models, mimicing the latest OptiFine behaviour.

For all other packs you might find one mod or the other works best for the time being. I **do intend** to close the gap between EMF and OptiFine but it has proven to be quite difficult (There is a reason these mods have taken a while to be functional on Fabric 💀)

(P.S: I also really wanted custom player model & animation support, which EMF adds :P)

## License

EMF is licensed under the [GNU Lesser Public License](LICENSE), version 3.0
