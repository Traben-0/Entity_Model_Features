package traben.entity_model_features.models.vanilla_model_compat;

import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.AngledModelEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped.*;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.other.*;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped.*;
import traben.entity_model_features.utils.EMFUtils;


/*
 * this class provides static methods to acquire 'vanilla model wrappers' (EMFCustomEntityModel<?>)
 * for the Generic emf custom entity model (EMFGenericEntityEntityModel<?>)
 * a "vanilla model wrappers" refers to an object inheriting from a vanilla model and containing/wrapping a generic emf entity model
 * in this way the EMF model can remain as generic as possible and the wrapper allows it to smoothly replace the vanilla
 * model without any form of class cast exception from vanilla or mods (usually).
 *
 * This class should only ever be called once per entity type, per model variant, per resource reload
 * which is good as instanceof checks cannot be optimized past a massive if-else stack like I'm the Yandere dev or something
 * additional calls may arise from models altered within feature renderers
 */
public class VanillaModelWrapperHandler {

    public static <T extends LivingEntity, M extends EntityModel<T>> M getCustomModelForRenderer(EMFGenericCustomEntityModel<T> alreadyBuiltSubmodel, M vanillaModelForInstanceCheck) {
        //figure out whether to send a vanilla child model or a direct EMF custom model
        return (M) getEMFCustomModelForRenderer(alreadyBuiltSubmodel,vanillaModelForInstanceCheck);
    }

        private static <T extends LivingEntity,
                M extends EntityModel<T>,
                Skelly extends MobEntity & RangedAttackMob,
                Hog extends MobEntity & Hoglin,
                Axo extends AxolotlEntity & AngledModelEntity>
        EMFCustomEntityModel<?> getEMFCustomModelForRenderer(EMFGenericCustomEntityModel<T> alreadyBuiltSubmodel, M vanillaModelForInstanceCheck){
        //figure out whether to send a vanilla child model or a direct EMF custom model
        try {
            //todo extend to all entity models
            if (vanillaModelForInstanceCheck instanceof DrownedEntityModel) {//before zombie
                return new EMFCustomDrownedEntityModel<>((EMFGenericCustomEntityModel<DrownedEntity>) alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof GiantEntityModel) {//before zombie
                return new EMFCustomGiantEntityModel<>((EMFGenericCustomEntityModel<GiantEntity>) alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof ZombieVillagerEntityModel) {//before zombie
                return new EMFCustomZombieVillagerEntityModel<>((EMFGenericCustomEntityModel<ZombieEntity>) alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof ZombieEntityModel) {
                return new EMFCustomZombieEntityModel<>((EMFGenericCustomEntityModel<ZombieEntity>) alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SkeletonEntityModel) {
                return new EMFCustomSkeletonEntityModel<>((EMFGenericCustomEntityModel<Skelly>) alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof PandaEntityModel) {
                return new EMFCustomPandaEntityModel<>((EMFGenericCustomEntityModel<PandaEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof PolarBearEntityModel) {
                return new EMFCustomPolarBearEntityModel<>((EMFGenericCustomEntityModel<PolarBearEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof TurtleEntityModel) {
                return new EMFCustomTurtleEntityModel<>((EMFGenericCustomEntityModel<TurtleEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof WolfEntityModel) {
                return new EMFCustomWolfEntityModel<>((EMFGenericCustomEntityModel<WolfEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof TadpoleEntityModel) {
                return new EMFCustomTadpoleEntityModel<>((EMFGenericCustomEntityModel<TadpoleEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof GoatEntityModel) {
                return new EMFCustomGoatEntityModel<>((EMFGenericCustomEntityModel<GoatEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof HoglinEntityModel) {
                return new EMFCustomHoglinEntityModel<>((EMFGenericCustomEntityModel<Hog>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof ShulkerEntityModel) {
                return new EMFCustomShulkerEntityModel<>((EMFGenericCustomEntityModel<ShulkerEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof CatEntityModel) {
                return new EMFCustomCatEntityModel<>((EMFGenericCustomEntityModel<CatEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof StriderEntityModel) {
                return new EMFCustomStriderEntityModel<>((EMFGenericCustomEntityModel<StriderEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof MagmaCubeEntityModel) {
                return new EMFCustomMagmaCubeEntityModel<>((EMFGenericCustomEntityModel<SlimeEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof ArmorStandEntityModel) {
                return new EMFCustomArmorStandEntityModel<>((EMFGenericCustomEntityModel<ArmorStandEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof AxolotlEntityModel) {
                return new EMFCustomAxolotlEntityModel<>((EMFGenericCustomEntityModel<Axo>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof FrogEntityModel) {
                return new EMFCustomFrogEntityModel<>((EMFGenericCustomEntityModel<FrogEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof PhantomEntityModel) {
                return new EMFCustomPhantomEntityModel<>((EMFGenericCustomEntityModel<PhantomEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof BeeEntityModel<?>) {
                return new EMFCustomBeeEntityModel<>((EMFGenericCustomEntityModel<BeeEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof RabbitEntityModel) {
                return new EMFCustomRabbitEntityModel<>((EMFGenericCustomEntityModel<RabbitEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof WardenEntityModel) {
                return new EMFCustomWardenEntityModel<>((EMFGenericCustomEntityModel<WardenEntity>)alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof DolphinEntityModel) {
                return new EMFCustomDolphinEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof ParrotEntityModel) {
                return new EMFCustomParrotEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SnowGolemEntityModel) {
                return new EMFCustomSnowGolemEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof BatEntityModel) {
                return new EMFCustomBatEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof AllayEntityModel) {
                return new EMFCustomAllayEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof VexEntityModel) {
                return new EMFCustomVexEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof EndermiteEntityModel) {
                return new EMFCustomEndermiteEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SilverfishEntityModel) {
                return new EMFCustomSilverfishEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof GhastEntityModel) {
                return new EMFCustomGhastEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof GuardianEntityModel) {
                return new EMFCustomGuardianEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof OcelotEntityModel) {
                return new EMFCustomOcelotEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof BlazeEntityModel) {
                return new EMFCustomBlazeEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SpiderEntityModel) {
                return new EMFCustomSpiderEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof RavagerEntityModel) {
                return new EMFCustomRavagerEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof LargePufferfishEntityModel) {
                return new EMFCustomPufferLargeEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof MediumPufferfishEntityModel) {
                return new EMFCustomPufferMediumEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SmallPufferfishEntityModel) {
                return new EMFCustomPufferSmallEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SmallTropicalFishEntityModel) {
                return new EMFCustomTropicalFishAEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof LargeTropicalFishEntityModel) {
                return new EMFCustomTropicalFishBEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof ChickenEntityModel) {
                return new EMFCustomChickenEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SquidEntityModel) {
                return new EMFCustomSquidEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SalmonEntityModel) {
                return new EMFCustomSalmonEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof CodEntityModel) {
                return new EMFCustomCodEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof CreeperEntityModel) {
                return new EMFCustomCreeperEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof DonkeyEntityModel) {
                return new EMFCustomDonkeyEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SheepWoolEntityModel) {
                return new EMFCustomSheepWoolEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SheepEntityModel) {
                return new EMFCustomSheepEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof PigEntityModel) {
                return new EMFCustomPigEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof CowEntityModel) {
                return new EMFCustomCowEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof SlimeEntityModel<?>) {
                return new EMFCustomSlimeEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof LlamaEntityModel) {
                return new EMFCustomLlamaEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof FoxEntityModel) {
                return new EMFCustomFoxEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof EndermanEntityModel) {
                return new EMFCustomEndermanEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof HorseEntityModel) {
                return new EMFCustomHorseEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof WitchEntityModel) {
                return new EMFCustomWitchEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof IronGolemEntityModel) {
                return new EMFCustomIronGolemEntityModel<>(alreadyBuiltSubmodel);
            }

            if (vanillaModelForInstanceCheck instanceof PiglinEntityModel) {
                //must be before  player model
                return new EMFCustomPiglinEntityModel<>((EMFGenericCustomEntityModel<MobEntity>)alreadyBuiltSubmodel);
            }
            //player
            if (vanillaModelForInstanceCheck instanceof PlayerEntityModel) {
                return new EMFCustomPlayerEntityModel<>(alreadyBuiltSubmodel);
            }

            //attempt some generic checks
            if (vanillaModelForInstanceCheck instanceof IllagerEntityModel) {
                return new EMFCustomIllagerEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof VillagerResemblingModel) {
                return new EMFCustomVillagerEntityModel<>(alreadyBuiltSubmodel);
            }
            //this for instance allows vanilla features like non custom armour and hand held items to work for bipeds
            if (vanillaModelForInstanceCheck instanceof BipedEntityModel) {
                return new EMFCustomBipedEntityModel<>(alreadyBuiltSubmodel);
            }
            if (vanillaModelForInstanceCheck instanceof QuadrupedEntityModel) {
                return new EMFCustomQuadrapedEntityModel<>(alreadyBuiltSubmodel);
            }

        }catch (ClassCastException e){
            EMFUtils.EMF_modError("Could not build EMF vanilla model wrapper for: "+ alreadyBuiltSubmodel.modelPathIdentifier);
            EMFUtils.EMF_modError(e.getMessage());
        }

        // failed all instance checks.
        // the mob must either be modded, or not integrated yet into EMF
        //
        // it does have a valid .jem so try and return just a generic entity model,
        // it also implements EMFCustomEntityModel just for this case, "wrapping itself" so to speak
        return alreadyBuiltSubmodel;

    }

}
