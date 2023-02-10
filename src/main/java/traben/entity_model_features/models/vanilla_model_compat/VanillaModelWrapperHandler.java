package traben.entity_model_features.models.vanilla_model_compat;

import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import traben.entity_model_features.models.EMFGenericEntityEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped.*;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.other.EMFCustomSlimeEntityModel;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.quadraped.*;


/*
 * this class provides static methods to acquire 'vanilla model wrappers' (EMFCustomEntityModel<?>)
 * for the Generic emf custom entity model (EMFGenericEntityEntityModel<?>)
 * a "vanilla model wrappers" refers to an object inheriting from a vanilla model and containing/wrapping a generic emf entity model
 * in this way the EMF model can remain as generic as possible and the wrapper allows it to smoothly replace the vanilla
 * model without any form of class cast exception from vanilla or mods (usually).
 *
 * This class should only every be called once per entity type, per model variant, per resource reload
 * additional calls may arise from models altered within feature renderers
 */
public class VanillaModelWrapperHandler {

    public static <T extends LivingEntity, M extends EntityModel<T>> M getCustomModelForRenderer(EMFGenericEntityEntityModel<T> alreadyBuiltSubmodel, M vanillaModelForInstanceCheck){
        //figure out whether to send a vanilla child model or a direct EMF custom model

        //todo extend to all entity models

        if(vanillaModelForInstanceCheck instanceof CowEntityModel<?>){
            return (M) new EMFCustomCowEntityModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof SlimeEntityModel<?>){
            return (M) new EMFCustomSlimeEntityModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof LlamaEntityModel){
            return (M) new EMFCustomLlamaEntityModel<T, LlamaEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof FoxEntityModel){
            return (M) new EMFCustomFoxEntityModel<T, FoxEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof EndermanEntityModel){
            return (M) new EMFCustomEndermanEntityModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof HorseEntityModel){
            return (M) new EMFCustomHorseEntityModel<T, AbstractHorseEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof PlayerEntityModel){
            return (M) new EMFCustomPlayerEntityModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof WitchEntityModel){
            return (M) new EMFCustomWitchEntityModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof IllagerEntityModel){
            return (M) new EMFCustomIllagerEntityModel<T, IllagerEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof VillagerResemblingModel){
            return (M) new EMFCustomVillagerEntityModel<T>(alreadyBuiltSubmodel);
        }
        //this for instance allows vanilla features like non custom armour and hand held items to work for bipeds
        if(vanillaModelForInstanceCheck instanceof BipedEntityModel){
            return (M) new EMFCustomBipedEntityModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof QuadrupedEntityModel){
            return (M) new EMFCustomQuadrapedEntityModel<T>(alreadyBuiltSubmodel);
        }
        //this for instance allows vanilla features like flower holding to work
        if(vanillaModelForInstanceCheck instanceof IronGolemEntityModel){
            return (M) new EMFCustomIronGolemEntityModel<T, IronGolemEntity>(alreadyBuiltSubmodel);
        }

        // failed all instance checks.
        // the mob must either be modded, or not integrated yet into EMF
        //
        // it does have a valid .jem so try and return just a generic entity model,
        // it also implements EMFCustomEntityModel just for this case, "wrapping itself" so to speak
        return (M) alreadyBuiltSubmodel;

    }

    public static <T extends LivingEntity, M extends EntityModel<T>> EntityModel<?> getCustomModelForRendererGeneric(EMFGenericEntityEntityModel<?> alreadyBuiltSubmodel, EntityModel<?> vanillaModelForInstanceCheck){
        return getCustomModelForRenderer((EMFGenericEntityEntityModel<T>)alreadyBuiltSubmodel,(M)vanillaModelForInstanceCheck);
    }
}
