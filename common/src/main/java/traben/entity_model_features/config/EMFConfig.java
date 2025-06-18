package traben.entity_model_features.config;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.model.geom.ModelLayers;
#if MC > MC_21
import net.minecraft.client.renderer.ShapeRenderer;
#else
import net.minecraft.client.renderer.LevelRenderer;
#endif




import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.utils.IEMFUnmodifiedLayerRootGetter;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreenList;
import traben.tconfig.gui.entries.*;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

@SuppressWarnings("CanBeFinal")
public class EMFConfig extends TConfig {

    public boolean logModelCreationData = false;
    public boolean debugOnRightClick = false;
    public RenderModeChoice renderModeChoice = RenderModeChoice.NORMAL;
    public VanillaModelRenderMode vanillaModelHologramRenderMode_2 = VanillaModelRenderMode.OFF;
    public ModelPrintMode modelExportMode = ModelPrintMode.NONE;
//    public PhysicsModCompatChoice attemptPhysicsModPatch_2 = PhysicsModCompatChoice.CUSTOM;
    public ETFConfig.UpdateFrequency modelUpdateFrequency = ETFConfig.UpdateFrequency.Average;
    public ETFConfig.String2EnumNullMap<RenderModeChoice> entityRenderModeOverrides = new ETFConfig.String2EnumNullMap<>();
//    public ETFConfig.String2EnumNullMap<PhysicsModCompatChoice> entityPhysicsModPatchOverrides = new ETFConfig.String2EnumNullMap<>();
    public ETFConfig.String2EnumNullMap<VanillaModelRenderMode> entityVanillaHologramOverrides = new ETFConfig.String2EnumNullMap<>();

    public RenderModeChoice getRenderModeFor(EMFEntity entity) {
        String typeString = getTypeString(entity);
        if (typeString == null) return renderModeChoice;
        return Objects.requireNonNullElseGet(entityRenderModeOverrides.getNullable(typeString), () -> renderModeChoice);
    }

//    public PhysicsModCompatChoice getPhysicsModModeFor(EMFEntity entity) {
//        String typeString = getTypeString(entity);
//        if (typeString == null) return attemptPhysicsModPatch_2;
//        return Objects.requireNonNullElseGet(entityPhysicsModPatchOverrides.getNullable(typeString), () -> attemptPhysicsModPatch_2);
//    }

    public VanillaModelRenderMode getVanillaHologramModeFor(EMFEntity entity) {
        String typeString = getTypeString(entity);
        if (typeString == null) return vanillaModelHologramRenderMode_2;
        return Objects.requireNonNullElseGet(entityVanillaHologramOverrides.getNullable(typeString), () -> vanillaModelHologramRenderMode_2);
    }

    private static @Nullable String getTypeString(final EMFEntity entity) {
        if (entity instanceof BlockEntity block) {
            return ETFApi.getBlockEntityTypeToTranslationKey(block.getType());
        } else if (entity instanceof Entity realBoy) {
            return realBoy.getType().getDescriptionId();
        } else {
            return null;
        }
    }

    public boolean isModelDisabled(String modelName) {
        return modelsNamesDisabled.contains(modelName);
    }

    public ObjectOpenHashSet<String> modelsNamesDisabled = new ObjectOpenHashSet<>();
    public boolean allowEBEModConfigModify = true;
    public int animationLODDistance = 20;
    public boolean retainDetailOnLowFps = true;
    public boolean retainDetailOnLargerMobs = true;
    public boolean animationFrameSkipDuringIrisShadowPass = true;
    public boolean preventFirstPersonHandAnimating = false;
    public boolean onlyClientPlayerModel = false;
    public boolean doubleChestAnimFix = true;
    @Deprecated(since = "2.4.1", forRemoval = true) public boolean enforceOptifineVariationRequiresDefaultModel = false;
    public boolean enforceOptifineVariationRequiresDefaultModel_v2 = false;
    @Deprecated(since = "2.4.1", forRemoval = true) public boolean resetPlayerModelEachRender = true;
    public boolean resetPlayerModelEachRender_v2 = true;
    public boolean onlyDebugRenderOnHover = false;
    public boolean enforceOptifineSubFoldersVariantOnly = true;
    public boolean enforceOptiFineAnimSyntaxLimits = true;
    public boolean allowOptifineFallbackProperties = true;
    #if MC < MC_21
    public boolean enforceOptiFineFloorUVs = true;
    #endif
    public boolean showReloadErrorToast = true;

    @Override
    public TConfigEntryCategory getGUIOptions() {
        return new TConfigEntryCategory.Empty().add(
                new TConfigEntryCategory("config.entity_features.models_main").add(
                        new TConfigEntryCategory("entity_model_features.config.options", "entity_model_features.config.options.tooltip").add(
//                                new TConfigEntryBoolean("entity_model_features.config.force_models", "entity_model_features.config.force_models.tooltip",
//                                        () -> attemptRevertingEntityModelsAlteredByAnotherMod, value -> attemptRevertingEntityModelsAlteredByAnotherMod = value, true),
//                                new TConfigEntryEnumButton<>("entity_model_features.config.physics", "entity_model_features.config.physics.tooltip",
//                                        () -> attemptPhysicsModPatch_2, value -> attemptPhysicsModPatch_2 = value, PhysicsModCompatChoice.CUSTOM),
                                new TConfigEntryBoolean("entity_model_features.config.ebe_config_modify", "entity_model_features.config.ebe_config_modify.tooltip",
                                        () -> allowEBEModConfigModify, value -> allowEBEModConfigModify = value, true),
                                new TConfigEntryBoolean("entity_model_features.config.double_chest_fix", "entity_model_features.config.double_chest_fix.tooltip",
                                        () -> doubleChestAnimFix, value -> doubleChestAnimFix = value, true)
                        ),
                        new TConfigEntryCategory("entity_model_features.config.player_settings").add(
                                new TConfigEntryBoolean("entity_model_features.config.prevent_hand", "entity_model_features.config.prevent_hand.tooltip",
                                        () -> preventFirstPersonHandAnimating, value -> preventFirstPersonHandAnimating = value, false),
                                new TConfigEntryBoolean("entity_model_features.config.only_client", "entity_model_features.config.only_client.tooltip",
                                        () -> onlyClientPlayerModel, value -> onlyClientPlayerModel = value, false),
                                new TConfigEntryBoolean("entity_model_features.config.reset_player", "entity_model_features.config.reset_player.tooltip",
                                        () -> resetPlayerModelEachRender_v2, value -> resetPlayerModelEachRender_v2 = value, true)
                        ),
                        new TConfigEntryCategory("entity_model_features.config.performance").add(
                                new TConfigEntryEnumSlider<>("entity_model_features.config.update", "entity_model_features.config.update.tooltip",
                                        () -> modelUpdateFrequency, value -> modelUpdateFrequency = value, ETFConfig.UpdateFrequency.Average),
                                new TConfigEntryInt("entity_model_features.config.lod", "entity_model_features.config.lod.tooltip",
                                        () -> animationLODDistance, value -> animationLODDistance = value, 20, 0, 65, true, true),
                                new TConfigEntryBoolean("entity_model_features.config.low_fps_lod", "entity_model_features.config.low_fps_lod.tooltip",
                                        () -> retainDetailOnLowFps, value -> retainDetailOnLowFps = value, true),
                                new TConfigEntryBoolean("entity_model_features.config.large_mob_lod", "entity_model_features.config.large_mob_lod.tooltip",
                                        () -> retainDetailOnLargerMobs, value -> retainDetailOnLargerMobs = value, true),
                                new TConfigEntryBoolean("entity_model_features.config.iris_shadow_skip", "entity_model_features.config.iris_shadow_skip.tooltip",
                                        () -> animationFrameSkipDuringIrisShadowPass, value -> animationFrameSkipDuringIrisShadowPass = value, true)
                        ),
                        new TConfigEntryCategory("entity_model_features.config.tools", "entity_model_features.config.tools.tooltip").add(
                                new TConfigEntryEnumSlider<>("entity_model_features.config.vanilla_render", "entity_model_features.config.vanilla_render.tooltip",
                                        () -> vanillaModelHologramRenderMode_2, value -> vanillaModelHologramRenderMode_2 = value, VanillaModelRenderMode.OFF),
                                new TConfigEntryEnumSlider<>("entity_model_features.config.print_mode", "entity_model_features.config.print_mode.tooltip",
                                        () -> modelExportMode, value -> modelExportMode = value, ModelPrintMode.NONE)
                        ),
                        new TConfigEntryCategory("entity_model_features.config.debug", "entity_model_features.config.debug.tooltip").add(
                                new TConfigEntryEnumSlider<>("entity_model_features.config.render", "entity_model_features.config.render.tooltip",
                                        () -> renderModeChoice, value -> renderModeChoice = value, RenderModeChoice.NORMAL),
                                new TConfigEntryBoolean("entity_model_features.config.debug_hover", "entity_model_features.config.debug_hover.tooltip",
                                        () -> onlyDebugRenderOnHover, value -> onlyDebugRenderOnHover = value, false),
                                new TConfigEntryBoolean("entity_model_features.config.log_models", "entity_model_features.config.log_models.tooltip",
                                        () -> logModelCreationData, value -> logModelCreationData = value, false),
                                new TConfigEntryBoolean("entity_model_features.config.debug_right_click", "entity_model_features.config.debug_right_click.tooltip",
                                        () -> debugOnRightClick, value -> debugOnRightClick = value, false),
                                new TConfigEntryCustomButton(
                                        "entity_model_features.config.load_warn.title",
                                        "entity_model_features.config.load_warn.tooltip",
                                        (button) -> reprintLoadingErrors(false)),
                                new TConfigEntryCustomButton(
                                        "entity_model_features.config.load_warn.title2",
                                        "entity_model_features.config.load_warn.tooltip",
                                        (button) -> reprintLoadingErrors(true)
                                        ),
                                new TConfigEntryBoolean("entity_model_features.config.show_reload_error_toast", "entity_model_features.config.show_reload_error_toast.tooltip",
                                        () -> showReloadErrorToast, value -> showReloadErrorToast = value, true)
                        ), getModelSettings()
                        , getMathInfo()
                )//, new TConfigEntryCategory("config.entity_features.general_settings.title")
                , getEntitySettings(),
                new TConfigEntryCategory("config.entity_features.optifine_settings","config.entity_texture_features.optifine.desc").add(
                        new TConfigEntryBoolean("entity_model_features.config.variation_base", "entity_model_features.config.variation_base.tooltip",
                                () -> enforceOptifineVariationRequiresDefaultModel_v2, value -> enforceOptifineVariationRequiresDefaultModel_v2 = value, false),
                        new TConfigEntryBoolean("entity_model_features.config.optifine_subfolders", "entity_model_features.config.optifine_subfolders.tooltip",
                                () -> enforceOptifineSubFoldersVariantOnly, value -> enforceOptifineSubFoldersVariantOnly = value, true),
                        new TConfigEntryBoolean("entity_model_features.config.optifine_syntax", "entity_model_features.config.optifine_syntax.tooltip",
                                () -> enforceOptiFineAnimSyntaxLimits, value -> enforceOptiFineAnimSyntaxLimits = value, true),
                        new TConfigEntryBoolean("entity_model_features.config.optifine_fallback_properties", "entity_model_features.config.optifine_fallback_properties.tooltip",
                                () -> allowOptifineFallbackProperties, value -> allowOptifineFallbackProperties = value, true)

                        #if MC < MC_21 , new TConfigEntryBoolean("entity_model_features.config.optifine_floor", "entity_model_features.config.optifine_floor.tooltip",
                                () -> enforceOptiFineFloorUVs, value -> enforceOptiFineFloorUVs = value, true)#endif

                )
        );
    }

    private static void reprintLoadingErrors(boolean stacktrace) {
        EMFUtils.logError("~~//BEGIN: EMF MASS PRINT OF LOADING ERRORS\\\\~~\nNot all errors here will be helpful as they may be missing surrounding log context.\nThis is best used to know what you might need to be searching for in your log\nor to give the dev debugging info.");
        int i = 1;
        for (Exception loadingException : EMFManager.getInstance().loadingExceptions) {
            System.out.println("----------//Exception #" + i + "\\\\---------- \n" + loadingException.getMessage());
            if (stacktrace) //noinspection CallToPrintStackTrace
                loadingException.printStackTrace();
            i++;
        }
        EMFUtils.logError("~~\\\\END: EMF MASS PRINT OF LOADING ERRORS//~~");
    }

    private TConfigEntryCategory getMathInfo() {
        TConfigEntryCategory category = new TConfigEntryCategory("entity_model_features.config.math");
        category.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.math.explain", 200, TConfigEntryText.TextAlignment.LEFT));

        TConfigEntryCategory variables = new TConfigEntryCategory("entity_model_features.config.variables");
        category.add(variables);
        variables.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.variables.explain", 200, TConfigEntryText.TextAlignment.LEFT));
        for (UniqueVariableFactory uniqueVariableFactory : VariableRegistry.getInstance().getUniqueVariableFactories()) {
            TConfigEntryCategory unique = new TConfigEntryCategory(uniqueVariableFactory.getTitleTranslationKey())
                    .addAll(TConfigEntryText.fromLongOrMultilineTranslation(uniqueVariableFactory.getExplanationTranslationKey(), 200, TConfigEntryText.TextAlignment.LEFT));
            variables.add(unique);
        }
        VariableRegistry.getInstance().getSingletonVariableExplanationTranslationKeys().keySet().stream().sorted().forEach(key -> {
            var value = VariableRegistry.getInstance().getSingletonVariableExplanationTranslationKeys().get(key);
            TConfigEntryCategory unique = new TConfigEntryCategory(key)
                    .addAll(TConfigEntryText.fromLongOrMultilineTranslation(value, 200, TConfigEntryText.TextAlignment.LEFT));
            variables.add(unique);
        });
        TConfigEntryCategory methods = new TConfigEntryCategory("entity_model_features.config.functions");
        category.add(methods);
        methods.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.functions.explain", 200, TConfigEntryText.TextAlignment.LEFT));
        MethodRegistry.getInstance().getMethodExplanationTranslationKeys().keySet().stream().sorted().forEach(key -> {
            var value = MethodRegistry.getInstance().getMethodExplanationTranslationKeys().get(key);
            TConfigEntryCategory method = new TConfigEntryCategory(key + "()")
                    .addAll(TConfigEntryText.fromLongOrMultilineTranslation(value, 200, TConfigEntryText.TextAlignment.LEFT));
            methods.add(method);
        });


        return category;
    }

    private TConfigEntryCategory getModelSettings() {
        TConfigEntryCategory category = new TConfigEntryCategory("entity_model_features.config.models");
        category.addAll(TConfigEntryText.fromLongOrMultilineTranslation("entity_model_features.config.models_text", 200, TConfigEntryText.TextAlignment.LEFT));

        EMFManager.getInstance().cache_LayersByModelName.put(new EMFModel_ID("wolf_collar"), ModelLayers.WOLF);

        EMFManager.getInstance().cache_LayersByModelName.keySet().stream().sorted().forEach(mapData -> {
            var layer = EMFManager.getInstance().cache_LayersByModelName.get(mapData);
            if (layer != null) {
                var vanilla = Minecraft.getInstance().getEntityModels().roots.get(layer);
                if (vanilla != null) {
                    String namespace = "minecraft".equals(mapData.getNamespace()) ? "" : mapData.getNamespace() + ':';
                    var fileName = namespace + mapData.getfileName();
                    TConfigEntryCategory model = new TConfigEntryCategory(fileName + ".jem");
                    model.setAlign(TConfigScreenList.Align.RIGHT);
                    model.setWidgetBackgroundToFullWidth();
                    model.setRenderFeature(new ModelRootRenderer(layer));
                    category.add(model);

                    StringBuilder fallbacks = new StringBuilder();
                    mapData.forEachFallback((fallBackData)-> fallbacks.append(fallBackData.getfileName()).append(".jem\n"));

                    model.add(new TConfigEntryBoolean("entity_model_features.config.models.enabled", "entity_model_features.config.models.enabled.tooltip",
                                    () -> !modelsNamesDisabled.contains(fileName),
                                    value -> {
                                        if (value) {
                                            modelsNamesDisabled.remove(fileName);
                                        } else {
                                            modelsNamesDisabled.add(fileName);
                                        }

                                    },
                                    true),
                            new TConfigEntryCategory("entity_model_features.config.models.part_names").addAll(
                                    getmappings(mapData.getMapId())
                            ),
                            getExport(mapData, layer),
                            new TConfigEntryCategory("entity_model_features.config.models.file_names").addAll(
                                    TConfigEntryText.fromLongOrMultilineTranslation(
                                            "<Folders>\nassets/" + mapData.getNamespace() + "/emf/cem/\n" +
                                                    "assets/" + mapData.getNamespace() + "/optifine/cem/\n\n" +
                                                    "<possible model names>\n<checked from top down>\n" +
                                                    mapData.getfileName() + ".jem\n" +  fallbacks
                                            ,
                                            600, TConfigEntryText.TextAlignment.CENTER)
                            )
                    ).addAll(TConfigEntryText.fromLongOrMultilineTranslation(
                                "entity_model_features.config.models.explain", 100, TConfigEntryText.TextAlignment.LEFT)
                    );

                }
            }
        });
//        category.addAll(TConfigEntryText.fromLongOrMultilineTranslation(
//                "entity_model_features.config.models.arrows", 200, TConfigEntryText.TextAlignment.LEFT));
//        category.addAll(TConfigEntryText.fromLongOrMultilineTranslation(
//                "entity_model_features.config.models.cape", 200, TConfigEntryText.TextAlignment.LEFT));
        return category;
    }

    @NotNull
    private TConfigEntry getExport(final EMFModel_ID key, ModelLayerLocation layer) {
        TConfigEntry export;
        try {
            Objects.requireNonNull(key.getMapId());
//            Objects.requireNonNull(((IEMFUnmodifiedLayerRootGetter)Minecraft.getInstance().getEntityModels())
//                    .emf$getUnmodifiedRoots().get(layer));
            export = new TConfigEntryCustomButton("entity_model_features.config.models.export", "entity_model_features.config.models.export.tooltip", (button) -> {
                var old = modelExportMode;
                modelExportMode = ModelPrintMode.ALL_LOG_AND_JEM;
                try {
                    EMFModelMappings.getMapOf(key,
                            Objects.requireNonNullElseGet(
                                    ((IEMFUnmodifiedLayerRootGetter)Minecraft.getInstance().getEntityModels())
                                            .emf$getUnmodifiedRoots().get(layer),
                                    () -> Minecraft.getInstance().getEntityModels().roots.get(layer)
                            ).bakeRoot(),
                            false);
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
                modelExportMode = old;
                button.active = false;
                //dont use etf translation class it crashes for some unknown reason
                button.setMessage(Component.translatable("entity_model_features.config.models.export.success"));
            });
        } catch (Exception e) {
            export = new TConfigEntryText.TwoLines("entity_model_features.config.models.export.fail", e.getMessage());
        }
        return export;
    }


    private Collection<TConfigEntry> getmappings(String mapKey) {
        var list = new ArrayList<TConfigEntry>();
        Map<String, String> map;
        if (EMFModelMappings.OPTIFINE_MODEL_MAP_CACHE.containsKey(mapKey)) {
            list.add(new TConfigEntryText("entity_model_features.config.variable_explanation.optifine_parts"));
            //noinspection NoTranslation
            list.add(new TConfigEntryText("\\/"));
            map = EMFModelMappings.OPTIFINE_MODEL_MAP_CACHE.get(mapKey);
        } else {
            list.add(new TConfigEntryText("entity_model_features.config.variable_explanation.unknown_parts"));
            //noinspection NoTranslation
            list.add(new TConfigEntryText("\\/"));
            map = EMFModelMappings.UNKNOWN_MODEL_MAP_CACHE.get(mapKey);
        }
        if (map == null) {
            return List.of();
        }

        for (String entry : map.keySet()) {
            list.add(new TConfigEntryText(entry));
        }
        return list;
    }


    private TConfigEntryCategory getEntitySettings() {
        TConfigEntryCategory category = new TConfigEntryCategory("config.entity_features.per_entity_settings");

        try {
            BuiltInRegistries.ENTITY_TYPE.forEach((entityType) -> {
                //if (entityType != EntityType.PLAYER) {
                String translationKey = entityType.getDescriptionId();
                TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey);
                this.addEntityConfigs(entityCategory, translationKey);
                category.add(entityCategory);
                //}
            });
            BlockEntityRenderers.PROVIDERS.keySet().forEach((entityType) -> {
                String translationKey = ETFApi.getBlockEntityTypeToTranslationKey(entityType);
                TConfigEntryCategory entityCategory = (new TConfigEntryCategory(translationKey));
                this.addEntityConfigs(entityCategory, translationKey);
                category.add(entityCategory);
            });
        } catch (Exception var4) {
            //noinspection CallToPrintStackTrace
            var4.printStackTrace();
        }

        return category;
    }

    private void addEntityConfigs(TConfigEntryCategory entityCategory, String translationKey) {
        TConfigEntryCategory category = new TConfigEntryCategory("config.entity_features.models_main");
        entityCategory.add(category);
        category.add(
                new TConfigEntryEnumSlider<>("entity_model_features.config.render", "entity_model_features.config.render.tooltip",
                        () -> this.entityRenderModeOverrides.getNullable(translationKey),
                        (layer) -> this.entityRenderModeOverrides.putNullable(translationKey, layer),
                        null, RenderModeChoice.class),
                new TConfigEntryEnumButton<>("entity_model_features.config.vanilla_render", "entity_model_features.config.vanilla_render.tooltip",
                        () -> this.entityVanillaHologramOverrides.getNullable(translationKey),
                        (layer) -> this.entityVanillaHologramOverrides.putNullable(translationKey, layer),
                        null, VanillaModelRenderMode.class)//,
//                new TConfigEntryEnumButton<>("entity_model_features.config.physics", "entity_model_features.config.physics.tooltip",
//                        () -> this.entityPhysicsModPatchOverrides.getNullable(translationKey),
//                        (layer) -> this.entityPhysicsModPatchOverrides.putNullable(translationKey, layer),
//                        null, PhysicsModCompatChoice.class)
        );
    }

    @Override
    public ResourceLocation getModIcon() {
        return EMFUtils.res("entity_model_features", "textures/gui/icon.png");
    }


    public enum ModelPrintMode {
        NONE("options.off"),
        @SuppressWarnings("unused")
        LOG_ONLY("entity_model_features.config.print_mode.log"),
        LOG_AND_JEM("entity_model_features.config.print_mode.log_jem"),
        @SuppressWarnings("unused")
        ALL_LOG_ONLY("entity_model_features.config.print_mode.all_log"),
        ALL_LOG_AND_JEM("entity_model_features.config.print_mode.all_log_jem");

        private final String text;

        ModelPrintMode(String text) {
            this.text = text;
        }

        public boolean doesJems() {
            return this == LOG_AND_JEM || this == ALL_LOG_AND_JEM;
        }

        public boolean doesAll() {
            return this == ALL_LOG_ONLY || this == ALL_LOG_AND_JEM;
        }

        public boolean doesLog() {
            return this != NONE;
        }

        @Override
        public String toString() {
            return Component.translatable(text).getString();
        }
    }

    public enum VanillaModelRenderMode {
        OFF("options.off"),
        @SuppressWarnings("unused")
        NORMAL("entity_model_features.config.vanilla_render.normal"),
        OFFSET("entity_model_features.config.vanilla_render.offset");

        private final String text;

        VanillaModelRenderMode(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return Component.translatable(text).getString();
        }
    }
//todo remove fully after physics mod implementation
//    public enum PhysicsModCompatChoice {
//        OFF("options.off"),
//        VANILLA("entity_model_features.config.physics.1"),
//        CUSTOM("entity_model_features.config.physics.2");
//
//        private final String text;
//
//        PhysicsModCompatChoice(String text) {
//            this.text = text;
//        }
//
//        @Override
//        public String toString() {
//            return Component.translatable(text).getString();
//        }
//    }

    public enum RenderModeChoice {
        NORMAL("entity_model_features.config.render.normal"),
        GREEN("entity_model_features.config.render.green"),
        LINES_AND_TEXTURE("entity_model_features.config.render.lines_texture"),
        LINES_AND_TEXTURE_FLASH("entity_model_features.config.render.lines_texture_flash"),
        LINES("entity_model_features.config.render.lines"),
        NONE("entity_model_features.config.render.none");

        private final String text;


        RenderModeChoice(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return Component.translatable(text).getString();
        }
    }


    private static class ModelRootRenderer implements TConfigScreenList.Renderable {

        private final ModelLayerLocation layer;
        private ModelPart root = null;
        private boolean asserted = false;

        ModelRootRenderer(ModelLayerLocation layer) {
            this.layer = layer;
        }

        private boolean canRender() {
            if (!asserted && root == null) {
                asserted = true;
                try {
                    root = Minecraft.getInstance().getEntityModels().roots.get(layer).bakeRoot();
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
            return root != null;
        }

        @Override
        public void render(final GuiGraphics context, final int mouseX, final int mouseY) {
            if (canRender()) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen == null) return;


                int y = (int) ((double) screen.height * 0.75);
                int x = (int) ((double) screen.width * 0.33);
                float g = (float) (-Math.atan((((float) (-mouseY) + (float) screen.height / 2.0F) / 40.0F)));
                float g2 = (float) (-Math.atan((((float) (-mouseX) + (float) screen.width / 3.0F) / 400.0F)));
                Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F).rotateY(g2 * 8);
                Quaternionf quaternionf2 = (new Quaternionf()).rotateX(-(g * 20.0F * 0.017453292F) * 2);
                quaternionf.mul(quaternionf2);

                #if MC>=MC_21_6
                PoseStack matrixStack = new PoseStack();
                #else
                PoseStack matrixStack = context.pose();
                Lighting.setupForEntityInInventory();
                #endif

                matrixStack.pushPose();
                matrixStack.translate(x, y, 150.0);
                float scaling = (float) ((double) screen.height * 0.3);
                matrixStack. #if MC >= MC_20_6 mulPose #else mulPoseMatrix #endif ((new Matrix4f()).scaling(scaling, scaling, -scaling));
                matrixStack.mulPose(quaternionf);

                matrixStack.pushPose();
                matrixStack.scale(-1.0F, -1.0F, 1.0F);
                matrixStack.translate(0.0F, -1.501F, 0.0F);
                var buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
                //who knows what mods might do smdh
                //noinspection ConstantValue
                if (buffer != null) {
                    renderBoxes(matrixStack, buffer, root);
                }
                matrixStack.popPose();
            }
        }

        private void renderBoxes(PoseStack matrices, VertexConsumer vertices, ModelPart modelPart) {
            if (modelPart.visible) {
                if (!modelPart.cubes.isEmpty() || !modelPart.children.isEmpty()) {
                    matrices.pushPose();
                    modelPart.translateAndRotate(matrices);
                    if (!modelPart.skipDraw) {
                        for (ModelPart.Cube cuboid : modelPart.cubes) {
                            AABB box = new AABB(cuboid.minX / 16, cuboid.minY / 16, cuboid.minZ / 16, cuboid.maxX / 16, cuboid.maxY / 16, cuboid.maxZ / 16);
                            #if MC > MC_21
                            ShapeRenderer.renderLineBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
                            #else
                            LevelRenderer.renderLineBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
                            #endif
                        }
                    }
                    for (ModelPart modelPartChildren : modelPart.children.values()) {
                        renderBoxes(matrices, vertices, modelPartChildren);
                    }
                    matrices.popPose();
                }
            }
        }
    }

}
