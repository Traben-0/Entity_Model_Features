package traben.entity_model_features.models.animation.state;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static traben.entity_model_features.models.animation.math.EMFMath.WRAP_CONST;

public abstract class EMFState {

    public static EMFEntityRenderState state() {
        return (EMFEntityRenderState) ETFState.state();
    }

    @Deprecated // Don't rely on this its just a stop gap to simplify state refactor
    public static EMFEntity emfEntity() {
        var state = state();
        return state != null ? state.emfEntity() : null;
    }

    public static final List<EMFAnimationApi.EMFAnimationHook> animationHooks = new ArrayList<>();

    public static void clear() {
        frameCounter = 0;
        ETFState.clear();
        isLayerPhase = false;
        isMainPhase = false;
        isInShoulderMethod = false;
        isInGui = false;
        modelVariationIgnoresVisibility = false;
        isInGroundOverride = false;
        isOnHead = false;
        isInHand = false;
        isInLeftHand = null;
        isInItemFrame = false;
        isInHandItemLayerTransform = false;
        hasDoneArmOverride = null;
    }

    public static float frameCounter = 0;
    public static boolean isLayerPhase = false;
    public static boolean isMainPhase = false;
    public static boolean isInShoulderMethod = false;
    public static boolean isInGui = false;
    public static boolean isInGroundOverride = false;
    public static boolean isOnHead = false;
    public static boolean isInHand = false;
    public static @Nullable Boolean isInLeftHand = null;
    public static boolean isInHandItemLayerTransform = false;
    public static @Nullable Boolean hasDoneArmOverride = null;
    public static boolean isInItemFrame = false;
    public static boolean modelVariationIgnoresVisibility = false;


    public static void incFrameCount(){
        //not 100% certain if the shadow pass passes through this method, I highly doubt it but just in case
        if(IrisShadowPassDetection.getInstance().inShadowPass()) return;

        float inc = frameCounter + 1;
        //reset counter after exceeding floating point precision cutoff
        frameCounter = inc >= WRAP_CONST ? 0 : inc;
    }

    public static float getFrameCounter(){
        return frameCounter;
    }


    //region debug chat anouncement

    public static boolean announceModels = false;

    public static void anounceModels(EMFEntityRenderState assertEntity) {
        String type = assertEntity.typeString();
        Set<EMFModelPartRoot> debugRoots = EMFManager.getInstance().rootPartsPerEntityTypeForDebug.get(type);
        EMFUtils.chat("§e-----------EMF Debug Printout-------------§r");
        if (debugRoots == null) {
            EMFUtils.chat(
                    "\n§c§oThe EMF debug printout did not find any custom models registered to the following entity:\n §3§l§u" + type
            );
        } else {
            String message = "\n§2§oThe EMF debug printout found the following custom models for the entity:\n §3§l§u" +
                    type +
                    "§r\n§2§oThis first model is usually the primary model for the entity.";

            EMFUtils.chat(message);

            int count = 1;
            for (EMFModelPartRoot debugRoot :
                    debugRoots) {
                StringBuilder model = new StringBuilder();
                model.append("§eModel #").append(count).append("§r")
                        .append(entryAndValue("name", debugRoot.modelName.getfileName() + ".jem"));
                if (debugRoot.modelName.hasFallbackModels()){
                    model.append("\n§eFallback Models:§r");
                    debugRoot.modelName.forEachFallback((modelId) ->
                            model.append("\n§6 - §r").append(modelId.getfileName()));
                }
                if (debugRoot.directoryContext != null) {
                    model.append(entryAndValue("directory",
                            debugRoot.directoryContext
                                    .getRelativeDirectoryLocationNoValidation(debugRoot.modelName.getfileName())));
                }

                if (debugRoot.textureOverride != null) {
                    model.append(entryAndValue("texture_override", debugRoot.textureOverride.toString()));
                }
                if (debugRoot.variantTester != null) {
                    Set<Integer> set = new HashSet<>(debugRoot.allKnownStateVariants.keySet());
                    set.remove(0);
                    model.append(entryAndValue("model_variants", set.toString()))
                            .append(entryAndValue("current_variant", String.valueOf(debugRoot.currentModelVariant)));
                }
                EMFUtils.chat(model + "\n§6 - parts:§r printed in game log only.");

                EMFUtils.log("\n - parts: " + debugRoot.simplePrintChildren(0));

                count++;
            }
        }

        EMFUtils.chat("\n§e----------------------------------------§r");
        if (!EMFManager.getInstance().modelsAnnounced.isEmpty()) {
            String vanillaMessage = "\n§2§oThe EMF debug printout found the following non-custom models for the entity:\n §3§l§u" +
                    type +
                    "§r\n§2§oThis first model is usually the primary model for the entity.";

            EMFUtils.chat(vanillaMessage);
            int count = 1;
            for (EMFModel_ID data : EMFManager.getInstance().modelsAnnounced) {
                StringBuilder model = new StringBuilder();
                model.append("\n§eNon-Custom Model #").append(count).append("§r")
                        .append(entryAndValue("possible .jem name", data.getDisplayFileName()));
                if (data.hasFallbackModels()){
                    model.append("§eFallback Models:§r");
                    data.forEachFallback((modelId) ->
                            model.append("\n§6 - §r").append(modelId.getfileName()));
                }
                Map<String, String> map = EMFModelMappings.getMapOf(data, null);
                if (!map.isEmpty()) {
                    EMFUtils.chat(model + "\n§6 - part names:§r printed in game log only.");
                    StringBuilder parts = new StringBuilder();
                    parts.append("\n - part names: ");
                    map.forEach((k, v) -> parts.append("\n   | - [").append(k).append(']'));

                    EMFUtils.log(parts.toString());
                } else {
                    EMFUtils.chat(model.toString());
                    EMFUtils.log(" - part names: could not be found. use the 'printout unknown models' setting instead.");
                }
            }
            EMFUtils.chat("\n§e----------------------------------------§r");
            EMFManager.getInstance().modelsAnnounced.clear();
        }

        announceModels = false;
    }

    private static String entryAndValue(String entry, String value) {
        return "\n§6 - " + entry + ":§r " + value;
    }
    //endregion

    //region model variation disabling
    public static Set<UUID> entitiesToForceVanillaModel = new HashSet<>();

    public static List<Function<EMFEntity, Boolean>> forceVanillaModelListeners = new ArrayList<>();

    public static boolean isEntityForcedToVanillaModel(@NotNull EMFEntityRenderState state) {
        if (entitiesToForceVanillaModel.contains(state.uuid())) return true;

        try {

            EntityType<?> type = state.entityType();
            if (type != null && type.toString().contains("customnpc")) {
                // CustomNPC has been ported to 1.20.1 by another dev that ask for having access to the source code
                // CustomNPC nor UnofficialCustomNPC are not open source (so no source code :x)
                // Downloads on https://www.curseforge.com/minecraft/mc-mods/customnpcs-unofficial/files/all?page=1&pageSize=20&version=1.20.1&gameVersionTypeId=1&showAlphaFiles=hide
                CompoundTag nbtTags = state.nbt();

                // If the customNPC is in puppet role
                // Or if we want the customNPC to not use EMF animations (like with Fresh Animation RP) we can set his maxhp to 777 to have a full control (usefull for the siting pose for example)
                if ((((LivingEntity) state.entity()).getMaxHealth() == 777f) ||
                        nbtTags.contains("PuppetStanding") ||
                        nbtTags.contains("PuppetMoving") ||
                        nbtTags.contains("PuppetAttacking") ||
                        nbtTags.contains("PuppetAnimate")) {
                    return true;
                }
            }

            for (var check : forceVanillaModelListeners) {
                if (check.apply(state.emfEntity())) return true;
            }
        } catch (Exception ignored) {}

        return EMF.config().getConfig().onlyClientPlayerModel
                && state.emfEntity() instanceof Player player && !player.isLocalPlayer();
    }
    //endregion

    public static RenderType getLayerFromRecentFactoryOrETFOverrideOrTranslucent(ResourceLocation identifier) {
        var state = state();
        if (state == null || state.layerFactory() == null) {
            var layer = ETF.config().getConfig().getRenderLayerOverride();
            if (layer == null) {
                return
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                            .entityTranslucent(identifier);
            } else {
                return switch (layer) {
                    case TRANSLUCENT ->
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                            .entityTranslucent(identifier);
                    case TRANSLUCENT_CULL ->
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(identifier);
                        //#elseif MC >=12102
                        RenderType.entityTranslucent(identifier);
                        //#else
                        //$$ RenderType.entityTranslucentCull(identifier);
                        //#endif
                    case END ->
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                            .endGateway();
                    case OUTLINE ->
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                            .outline(identifier);
                };
            }
        }
        return state.layerFactory().apply(identifier);
    }

}
