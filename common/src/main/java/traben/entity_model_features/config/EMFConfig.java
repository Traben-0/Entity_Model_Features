package traben.entity_model_features.config;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.UniqueVariableFactory;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFOptiFinePartNameMappings;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.tconfig.TConfig;
import traben.tconfig.gui.TConfigScreenList;
import traben.tconfig.gui.entries.*;

import java.util.*;

@SuppressWarnings("CanBeFinal")
public class EMFConfig extends TConfig {

    public boolean logModelCreationData = false;
    public boolean debugOnRightClick = false;
    public RenderModeChoice renderModeChoice = RenderModeChoice.NORMAL;
    public VanillaModelRenderMode vanillaModelHologramRenderMode_2 = VanillaModelRenderMode.OFF;
    public boolean attemptRevertingEntityModelsAlteredByAnotherMod = true;
    public ModelPrintMode modelExportMode = ModelPrintMode.NONE;
    public PhysicsModCompatChoice attemptPhysicsModPatch_2 = PhysicsModCompatChoice.CUSTOM;
    public ETFConfig.UpdateFrequency modelUpdateFrequency = ETFConfig.UpdateFrequency.Average;

    public ETFConfig.String2EnumNullMap<RenderModeChoice> entityRenderModeOverrides = new ETFConfig.String2EnumNullMap<>();
    public ETFConfig.String2EnumNullMap<PhysicsModCompatChoice> entityPhysicsModPatchOverrides = new ETFConfig.String2EnumNullMap<>();
    public ETFConfig.String2EnumNullMap<VanillaModelRenderMode> entityVanillaHologramOverrides = new ETFConfig.String2EnumNullMap<>();
    public ObjectOpenHashSet<String> modelsNamesDisabled = new ObjectOpenHashSet<>();

    public boolean allowEBEModConfigModify = true;

    public int animationLODDistance = 20;

    public boolean retainDetailOnLowFps = true;

    public boolean retainDetailOnLargerMobs = true;

    @Override
    public TConfigEntryCategory getGUIOptions() {
        return new TConfigEntryCategory.Empty().add(
                new TConfigEntryCategory("config.entity_features.models_main").add(
                        new TConfigEntryCategory("entity_model_features.config.options", "entity_model_features.config.options.tooltip").add(
                                new TConfigEntryBoolean("entity_model_features.config.force_models", "entity_model_features.config.force_models.tooltip",
                                        () -> attemptRevertingEntityModelsAlteredByAnotherMod, value -> attemptRevertingEntityModelsAlteredByAnotherMod = value, true),
                                new TConfigEntryEnumButton<>("entity_model_features.config.physics", "entity_model_features.config.physics.tooltip",
                                        () -> attemptPhysicsModPatch_2, value -> attemptPhysicsModPatch_2 = value, PhysicsModCompatChoice.CUSTOM),
                                new TConfigEntryBoolean("entity_model_features.config.ebe_config_modify", "entity_model_features.config.ebe_config_modify.tooltip",
                                        () -> allowEBEModConfigModify, value -> allowEBEModConfigModify = value, true)
                        ),
                        new TConfigEntryCategory("entity_model_features.config.performance").add(
                                new TConfigEntryEnumSlider<>("entity_model_features.config.update", "entity_model_features.config.update.tooltip",
                                        () -> modelUpdateFrequency, value -> modelUpdateFrequency = value, ETFConfig.UpdateFrequency.Average),
                                new TConfigEntryInt("entity_model_features.config.lod", "entity_model_features.config.lod.tooltip",
                                        () -> animationLODDistance, value -> animationLODDistance = value, 20, 0, 65, true, true),
                                new TConfigEntryBoolean("entity_model_features.config.low_fps_lod", "entity_model_features.config.low_fps_lod.tooltip",
                                        () -> retainDetailOnLowFps, value -> retainDetailOnLowFps = value, true),
                                new TConfigEntryBoolean("entity_model_features.config.large_mob_lod", "entity_model_features.config.large_mob_lod.tooltip",
                                        () -> retainDetailOnLargerMobs, value -> retainDetailOnLargerMobs = value, true)
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
                                new TConfigEntryBoolean("entity_model_features.config.log_models", "entity_model_features.config.log_models.tooltip",
                                        () -> logModelCreationData, value -> logModelCreationData = value, false),
                                new TConfigEntryBoolean("entity_model_features.config.debug_right_click", "entity_model_features.config.debug_right_click.tooltip",
                                        () -> debugOnRightClick, value -> debugOnRightClick = value, false)
                        ), getModelSettings()
                        , getMathInfo()
                )//, new TConfigEntryCategory("config.entity_features.general_settings.title")
                , getEntitySettings()
        );
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
        EMFManager.getInstance().cache_LayersByModelName.keySet().stream().sorted().forEach(mapData -> {
            var layer = EMFManager.getInstance().cache_LayersByModelName.get(mapData);
            if (layer != null) {
                var vanilla = MinecraftClient.getInstance().getEntityModelLoader().modelParts.get(layer);
                if (vanilla != null) {
                    String namespace = "minecraft".equals(mapData.getNamespace()) ? "" : mapData.getNamespace() + ':';
                    var fileName = namespace + mapData.getfileName();
                    TConfigEntryCategory model = new TConfigEntryCategory(fileName + ".jem");
                    model.setAlign(TConfigScreenList.Align.RIGHT);
                    model.setRenderFeature(new ModelRootRenderer(layer));
                    category.add(model);

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
                            getExport(mapData, layer)
                    );
                    model.addAll(TConfigEntryText.fromLongOrMultilineTranslation(
                            "assets/" + mapData.getNamespace() + "/optifine/cem/" + mapData.getfileName() + ".jem\n" +
                                    //"assets/"+mapData.getNamespace()+"/optifine/cem/"+  mapData.getfileName()+"/"+mapData.getfileName()+".jem\n" +
                                    "assets/" + mapData.getNamespace() + "/emf/cem/" + mapData.getfileName() + ".jem\n" //+
                            //"assets/"+mapData.getNamespace()+"/emf/cem/"+       mapData.getfileName()+"/"+mapData.getfileName()+".jem\n"
                            ,
                            100, TConfigEntryText.TextAlignment.LEFT));
                }
            }
        });
        category.addAll(TConfigEntryText.fromLongOrMultilineTranslation(
                "entity_model_features.config.models.arrows", 200, TConfigEntryText.TextAlignment.LEFT));
        return category;
    }

    @NotNull
    private TConfigEntry getExport(final OptifineMobNameForFileAndEMFMapId key, EntityModelLayer layer) {
        TConfigEntry export;
        try {
            Objects.requireNonNull(key.getMapId());
            Objects.requireNonNull(MinecraftClient.getInstance().getEntityModelLoader().modelParts.get(layer));
            export = new TConfigEntryCustomButton("entity_model_features.config.models.export", "entity_model_features.config.models.export.tooltip", (button) -> {
                var old = modelExportMode;
                modelExportMode = ModelPrintMode.ALL_LOG_AND_JEM;
                try {
                    EMFOptiFinePartNameMappings.getMapOf(key.getMapId(),
                            MinecraftClient.getInstance().getEntityModelLoader().modelParts.get(layer).createModel(),
                            false);
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
                modelExportMode = old;
                button.active = false;
                button.setMessage(ETFVersionDifferenceHandler.getTextFromTranslation("entity_model_features.config.models.export.success"));
            });
        } catch (Exception e) {
            export = new TConfigEntryText.TwoLines("entity_model_features.config.models.export.fail", e.getMessage());
        }
        return export;
    }


    private Collection<TConfigEntry> getmappings(String mapKey) {
        var list = new ArrayList<TConfigEntry>();
        Map<String, String> map;
        if (EMFOptiFinePartNameMappings.OPTIFINE_MODEL_MAP_CACHE.containsKey(mapKey)) {
            list.add(new TConfigEntryText("entity_model_features.config.variable_explanation.optifine_parts"));
            //noinspection NoTranslation
            list.add(new TConfigEntryText("\\/"));
            map = EMFOptiFinePartNameMappings.OPTIFINE_MODEL_MAP_CACHE.get(mapKey);
        } else {
            list.add(new TConfigEntryText("entity_model_features.config.variable_explanation.unknown_parts"));
            //noinspection NoTranslation
            list.add(new TConfigEntryText("\\/"));
            map = EMFOptiFinePartNameMappings.UNKNOWN_MODEL_MAP_CACHE.get(mapKey);
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
            Registries.ENTITY_TYPE.forEach((entityType) -> {
                //if (entityType != EntityType.PLAYER) {
                String translationKey = entityType.getTranslationKey();
                TConfigEntryCategory entityCategory = new TConfigEntryCategory(translationKey);
                this.addEntityConfigs(entityCategory, translationKey);
                category.add(entityCategory);
                //}
            });
            BlockEntityRendererFactories.FACTORIES.keySet().forEach((entityType) -> {
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
                        null, VanillaModelRenderMode.class),
                new TConfigEntryEnumButton<>("entity_model_features.config.physics", "entity_model_features.config.physics.tooltip",
                        () -> this.entityPhysicsModPatchOverrides.getNullable(translationKey),
                        (layer) -> this.entityPhysicsModPatchOverrides.putNullable(translationKey, layer),
                        null, PhysicsModCompatChoice.class)
        );
    }

    @Override
    public Identifier getModIcon() {
        return new Identifier("entity_model_features", "textures/gui/icon.png");
    }


    public enum ModelPrintMode {
        NONE(ScreenTexts.OFF),
        @SuppressWarnings("unused")
        LOG_ONLY(Text.translatable("entity_model_features.config.print_mode.log")),
        LOG_AND_JEM(Text.translatable("entity_model_features.config.print_mode.log_jem")),
        @SuppressWarnings("unused")
        ALL_LOG_ONLY(Text.translatable("entity_model_features.config.print_mode.all_log")),
        ALL_LOG_AND_JEM(Text.translatable("entity_model_features.config.print_mode.all_log_jem"));

        private final Text text;

        ModelPrintMode(Text text) {
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
            return text.getString();
        }
    }

    public enum VanillaModelRenderMode {
        OFF(ScreenTexts.OFF),
        @SuppressWarnings("unused")
        NORMAL(Text.translatable("entity_model_features.config.vanilla_render.normal")),
        OFFSET(Text.translatable("entity_model_features.config.vanilla_render.offset"));

        private final Text text;

        VanillaModelRenderMode(Text text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum PhysicsModCompatChoice {
        OFF(ScreenTexts.OFF),
        VANILLA(Text.translatable("entity_model_features.config.physics.1")),
        CUSTOM(Text.translatable("entity_model_features.config.physics.2"));

        private final Text text;

        PhysicsModCompatChoice(Text text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum RenderModeChoice {
        NORMAL(Text.translatable("entity_model_features.config.render.normal")),
        GREEN(Text.translatable("entity_model_features.config.render.green")),
        LINES_AND_TEXTURE(Text.translatable("entity_model_features.config.render.lines_texture")),
        LINES_AND_TEXTURE_FLASH(Text.translatable("entity_model_features.config.render.lines_texture_flash")),
        LINES(Text.translatable("entity_model_features.config.render.lines")),
        NONE(Text.translatable("entity_model_features.config.render.none"));

        private final String text;


        RenderModeChoice(Text text) {
            this.text = text.getString();
        }

        @Override
        public String toString() {
            return text;
        }
    }


    private static class ModelRootRenderer implements TConfigScreenList.Renderable {

        private final EntityModelLayer layer;
        private ModelPart root = null;
        private boolean asserted = false;

        ModelRootRenderer(EntityModelLayer layer) {
            this.layer = layer;
        }

        private boolean canRender() {
            if (!asserted && root == null) {
                asserted = true;
                try {
                    root = MinecraftClient.getInstance().getEntityModelLoader().modelParts.get(layer).createModel();
                } catch (Exception e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
            return root != null;
        }

        @Override
        public void render(final DrawContext context, final int mouseX, final int mouseY) {
            if (canRender()) {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                if (screen == null) return;


                int y = (int) ((double) screen.height * 0.75);
                int x = (int) ((double) screen.width * 0.33);
                float g = (float) (-Math.atan((((float) (-mouseY) + (float) screen.height / 2.0F) / 40.0F)));
                float g2 = (float) (-Math.atan((((float) (-mouseX) + (float) screen.width / 3.0F) / 400.0F)));
                Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F).rotateY(g2 * 8);
                Quaternionf quaternionf2 = (new Quaternionf()).rotateX(-(g * 20.0F * 0.017453292F) * 2);
                quaternionf.mul(quaternionf2);
                context.getMatrices().push();
                context.getMatrices().translate(x, y, 150.0);
                float scaling = (float) ((double) screen.height * 0.3);
                context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling(scaling, scaling, -scaling));
                context.getMatrices().multiply(quaternionf);
                DiffuseLighting.method_34742();
                MatrixStack matrixStack = context.getMatrices();

                matrixStack.push();
                matrixStack.scale(-1.0F, -1.0F, 1.0F);
                matrixStack.translate(0.0F, -1.501F, 0.0F);
                var buffer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
                if (buffer != null) {
                    renderBoxes(matrixStack, buffer, root);
                }
                matrixStack.pop();
            }
        }

        private void renderBoxes(MatrixStack matrices, VertexConsumer vertices, ModelPart modelPart) {
            if (modelPart.visible) {
                if (!modelPart.cuboids.isEmpty() || !modelPart.children.isEmpty()) {
                    matrices.push();
                    modelPart.rotate(matrices);
                    if (!modelPart.hidden) {
                        for (ModelPart.Cuboid cuboid : modelPart.cuboids) {
                            Box box = new Box(cuboid.minX / 16, cuboid.minY / 16, cuboid.minZ / 16, cuboid.maxX / 16, cuboid.maxY / 16, cuboid.maxZ / 16);
                            WorldRenderer.drawBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
                        }
                    }
                    for (ModelPart modelPartChildren : modelPart.children.values()) {
                        renderBoxes(matrices, vertices, modelPartChildren);
                    }
                    matrices.pop();
                }
            }
        }
    }

}
