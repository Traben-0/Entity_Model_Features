package traben.entity_model_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.packs.PackResources;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mod_compat.EBEConfigModifier;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModelNameContainer;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.utils.EMFDirectoryHandler;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.utils.EntityIntLRU;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.entity.BlockEntityType;


public class EMFManager {//singleton for data holding and resetting needs


    private static final Map<BlockEntityType<?>, String> EBETypes = Map.of(
            BlockEntityType.BED, "bed",
            BlockEntityType.CHEST, "chest",
            BlockEntityType.TRAPPED_CHEST, "chest",
            BlockEntityType.ENDER_CHEST, "chest",
            BlockEntityType.SHULKER_BOX, "shulker_box",
            BlockEntityType.BELL, "bell",
            BlockEntityType.SIGN, "sign",
            BlockEntityType.DECORATED_POT, "decorated_pot");

    public static EMFModelPartRoot lastCreatedRootModelPart = null;
    private static EMFManager self = null;
    public final boolean IS_PHYSICS_MOD_INSTALLED;
    public final boolean IS_EBE_INSTALLED;

    public final EntityIntLRU lastModelRuleOfEntity;
    public final EntityIntLRU lastModelSuffixOfEntity;
    public final Object2ObjectLinkedOpenHashMap<String, Set<EMFModelPartRoot>> rootPartsPerEntityTypeForDebug = new Object2ObjectLinkedOpenHashMap<>() {{
        defaultReturnValue(null);
    }};
    public final ObjectSet<EMFModel_ID> modelsAnnounced = new ObjectOpenHashSet<>();

    public final Object2ObjectLinkedOpenHashMap<String, Set<EMFModelPartRoot>> rootPartsPerEntityTypeForVariation = new Object2ObjectLinkedOpenHashMap<>() {{
        defaultReturnValue(null);
    }};
    public final Object2ObjectOpenHashMap<String, EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectOpenHashMap<EMFModel_ID, ModelLayerLocation> cache_LayersByModelName = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<ModelLayerLocation> amountOfLayerAttempts = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(0);
    }};
    private final Set<String> EBE_JEMS_FOUND = new HashSet<>();
    public final Set<String> EBE_JEMS_FOUND_LAST = new HashSet<>();
    public UUID entityForDebugPrint = null;
    public long entityRenderCount = 0;
    public boolean isAnimationValidationPhase = false;
    public String currentSpecifiedModelLoading = "";
    public BlockEntityType<?> currentBlockEntityTypeLoading = null;
    private boolean traderLlamaHappened = false;

    public boolean wasEBEModified() {
        return !EBE_JEMS_FOUND_LAST.isEmpty();
    }



    private EMFManager() {
        EMFAnimationEntityContext.reset();
        IS_PHYSICS_MOD_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("physicsmod");
//        IS_IRIS_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("iris") || EMFVersionDifferenceManager.isThisModLoaded("oculus");
        IS_EBE_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("enhancedblockentities");
        lastModelRuleOfEntity = new EntityIntLRU();
        lastModelRuleOfEntity.defaultReturnValue(0);
        lastModelSuffixOfEntity = new EntityIntLRU();
        lastModelSuffixOfEntity.defaultReturnValue(0);

        KNOWN_RESOURCEPACK_ORDER = new ArrayList<>();
    }

    public static EMFManager getInstance() {
        if (self == null) self = new EMFManager();
        return self;
    }

    public ArrayList<String> getResourcePackList() {
        if (KNOWN_RESOURCEPACK_ORDER.isEmpty()) {
            var list = Minecraft.getInstance().getResourceManager().listPacks().toList();
            for (final PackResources pack : list) {
                this.KNOWN_RESOURCEPACK_ORDER.add(pack.packId());
            }
        }
        return KNOWN_RESOURCEPACK_ORDER;
    }

    private final ArrayList<String> KNOWN_RESOURCEPACK_ORDER;


    public static void resetInstance() {
        EMFUtils.log("[EMF (Entity Model Features)]: Clearing data for reload.", false, true);
        EMFModelMappings.UNKNOWN_MODEL_MAP_CACHE.clear();
        self = new EMFManager();
    }

    @Nullable
    public static EMFJemData getJemDataWithDirectory(EMFDirectoryHandler jemDirectory, EMFModel_ID mobModelIDInfo) {
        String pathOfJem = jemDirectory.getFinalFileLocation();
        if (EMFManager.getInstance().cache_JemDataByFileName.containsKey(pathOfJem)) {
            return EMFManager.getInstance().cache_JemDataByFileName.get(pathOfJem);
        }
        final boolean print = EMF.config().getConfig().logModelCreationData;
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(EMFUtils.res(pathOfJem));
            if (res.isEmpty()) {
                if (print) EMFUtils.log(pathOfJem + ", .jem read failed " + pathOfJem + " does not exist", false);
                return null;
            }
            if (print) EMFUtils.log(pathOfJem + ", .jem read success " + pathOfJem + " exists", false);
            Resource jemResource = res.get();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jemResource.open()));

            EMFJemData jem = gson.fromJson(reader, EMFJemData.class);
            reader.close();
            jem.prepare(jemDirectory, mobModelIDInfo);
            if (mobModelIDInfo.areBothSame())
                EMFManager.getInstance().cache_JemDataByFileName.put(pathOfJem, jem);
            return jem;
        } catch (ResourceLocationException | FileNotFoundException e) {
            if (print) EMFUtils.log(pathOfJem + ", .jem failed to load: " + e, false);
        } catch (Exception e) {
            EMFUtils.log(pathOfJem + ", .jem failed to load: " + e, false);
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return null;
    }

    public static EMFModelPart getModelFromHierarchichalId(String hierarchId, Map<String, EMFModelPart> map) {
        if (hierarchId == null || hierarchId.isBlank()) return null;

        EMFModelPart part = map.get(hierarchId);
        if (part == null) part = map.get("EMF_" + hierarchId);
        if (part != null) return part;

        //must search for heirarchical declaration
        for (Map.Entry<String, EMFModelPart> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(":" + hierarchId) || key.endsWith(":EMF_" + hierarchId)) {
                return entry.getValue();
            }
            boolean allPartsMatch = true;
            String[] parts = hierarchId.split(":");
            for (String partStr : parts) {
                if (!key.contains(partStr) && !key.contains("EMF_" + partStr)) {
                    allPartsMatch = false;
                    break;
                }
            }
            if (allPartsMatch && key.endsWith(parts[parts.length - 1])) {
                return entry.getValue();
            }
        }
        //all possible occurances should be accounted for above must be null
        //EMFUtils.logWarn("NULL animation hierachy id result of: " + hierarchId + "\n in " + map);
        return null;
    }


    public void modifyEBEIfRequired() {
        if (IS_EBE_INSTALLED && !EBE_JEMS_FOUND.isEmpty() && EMF.config().getConfig().allowEBEModConfigModify) {
            try {
                EBEConfigModifier.modifyEBEConfig(EBE_JEMS_FOUND);
            } catch (Exception | Error e) {
                EMFUtils.logWarn("EBE config modification issue: " + e);
            }
        }
        EBE_JEMS_FOUND_LAST.clear();
        EBE_JEMS_FOUND_LAST.addAll(EBE_JEMS_FOUND);
        EBE_JEMS_FOUND.clear();
    }

    public ModelPart injectIntoModelRootGetter(ModelLayerLocation layer, ModelPart root) {
        int creationsOfLayer = amountOfLayerAttempts.put(layer, amountOfLayerAttempts.getInt(layer) + 1);
        if (creationsOfLayer > 500) {
            if (creationsOfLayer == 501) {
                EMFUtils.logWarn("model attempted creation more than 500 times {" + layer.toString() + "]. EMF is now ignoring this model.");
            }
            return root;
        }

        String originalLayerName = layer.getModel().getPath();
        EMFModel_ID mobNameForFileAndMap = new EMFModel_ID(
                currentSpecifiedModelLoading.isBlank() ? originalLayerName : currentSpecifiedModelLoading);

        try {
            EMFManager.lastCreatedRootModelPart = null;
            boolean printing = (EMF.config().getConfig().logModelCreationData);

            if (!"main".equals(layer.getLayer())) {
                mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName() + "_" + layer.getLayer());
                originalLayerName = originalLayerName + "_" + layer.getLayer();
            }

            boolean modded;

            //add simple modded layer checks
            if (!"minecraft".equals(layer.getModel().getNamespace())) {
                modded = true;
                //mobNameForFileAndMap.setBoth(("modded/" + layer.getId().getNamespace() + "/" + originalLayerName).toLowerCase().replaceAll("[^a-z0-9/._-]", "_"));
                mobNameForFileAndMap.setBoth(originalLayerName.toLowerCase().replaceAll("[^a-z0-9/._-]", "_"));
                mobNameForFileAndMap.namespace = layer.getModel().getNamespace();
            } else {
                modded = false;
                //vanilla model
                switch (originalLayerName) {
                    case "bed_foot" -> mobNameForFileAndMap.setBoth("bed", "bed_foot");
                    case "bed_head" -> mobNameForFileAndMap.setBoth("bed", "bed_head");
                    case "book" -> {
                        if (currentSpecifiedModelLoading.equals("enchanting_book")) {
                            mobNameForFileAndMap.setBoth("enchanting_book", "book");
                        } else {/* if(currentSpecifiedModelLoading.equals("lectern_book"))*/
                            mobNameForFileAndMap.setBoth("lectern_book", "book");
                        }
                    }
                    case "chest" -> mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading, "chest");
                    case "conduit_cage" -> mobNameForFileAndMap.setBoth("conduit", "conduit_cage");
                    case "conduit_eye" -> mobNameForFileAndMap.setBoth("conduit", "conduit_eye");
                    case "conduit_shell" -> mobNameForFileAndMap.setBoth("conduit", "conduit_shell");
                    case "conduit_wind" -> mobNameForFileAndMap.setBoth("conduit", "conduit_wind");
                    case "creeper_armor" -> mobNameForFileAndMap.setBoth("creeper_charge");
                    case "creeper_head" -> mobNameForFileAndMap.setBoth("head_creeper");
                    case "decorated_pot_base" -> mobNameForFileAndMap.setBoth("decorated_pot", "decorated_pot_base");
                    case "decorated_pot_sides" -> mobNameForFileAndMap.setBoth("decorated_pot", "decorated_pot_sides");
                    case "double_chest_left" -> getDoubleChest(root, mobNameForFileAndMap, false, printing);
                    case "double_chest_right" -> getDoubleChest(root, mobNameForFileAndMap, true, printing);
                    case "dragon_skull" -> mobNameForFileAndMap.setBoth("head_dragon");
                    case "ender_dragon" -> mobNameForFileAndMap.setBoth("dragon");
                    case "leash_knot" -> mobNameForFileAndMap.setBoth("lead_knot");
                    case "llama" -> traderLlamaHappened = false;
                    case "llama_decor" -> mobNameForFileAndMap.setBoth(traderLlamaHappened ? "trader_llama_decor" : "llama_decor");
                    case "piglin_head" -> mobNameForFileAndMap.setBoth("head_piglin");
                    case "player_head" -> mobNameForFileAndMap.setBoth("head_player");
                    case "pufferfish_big" -> mobNameForFileAndMap.setBoth("puffer_fish_big");
                    case "pufferfish_medium" -> mobNameForFileAndMap.setBoth("puffer_fish_medium");
                    case "pufferfish_small" -> mobNameForFileAndMap.setBoth("puffer_fish_small");
                    case "shulker" -> {
                        if (currentSpecifiedModelLoading.equals("shulker_box")) {
                            mobNameForFileAndMap.setBoth("shulker_box");
                        }
                    }
                    case "skeleton_skull" -> mobNameForFileAndMap.setBoth("head_skeleton");
                    case "sheep_fur" -> mobNameForFileAndMap.setBoth("sheep_wool");
                    case "trader_llama" -> traderLlamaHappened = true;
                    case "tropical_fish_large" -> mobNameForFileAndMap.setBoth("tropical_fish_b");
                    case "tropical_fish_large_pattern" -> mobNameForFileAndMap.setBoth("tropical_fish_pattern_b");
                    case "tropical_fish_small" -> mobNameForFileAndMap.setBoth("tropical_fish_a");
                    case "tropical_fish_small_pattern" -> mobNameForFileAndMap.setBoth("tropical_fish_pattern_a");
                    case "wither_skeleton_skull" -> mobNameForFileAndMap.setBoth("head_wither_skeleton");
                    case "zombie_head" -> mobNameForFileAndMap.setBoth("head_zombie");
                    default -> {
                        if (!currentSpecifiedModelLoading.isBlank()) {
                            switch (currentSpecifiedModelLoading) {
                                case "sign", "hanging_sign" -> {
                                    mobNameForFileAndMap.setFileName(originalLayerName);
                                    mobNameForFileAndMap.setMapIdAndSecondaryFileName(currentSpecifiedModelLoading);
                                }
                                default -> {
                                    if (EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE)
                                        EMFUtils.log("EMF unknown modifiable block entity model identified during loading: " + currentSpecifiedModelLoading + ".jem");
                                    mobNameForFileAndMap.setFileName(currentSpecifiedModelLoading);
                                    mobNameForFileAndMap.setMapIdAndSecondaryFileName(currentSpecifiedModelLoading,originalLayerName);
                                }
                            }
                        } else if (originalLayerName.contains("/") && layer.getLayer().equals("main")) {
                            if (originalLayerName.startsWith("chest_boat/")) {
                                mobNameForFileAndMap.setMapIdAndSecondaryFileName(originalLayerName.startsWith("chest_boat/bamboo") ? "chest_raft" : "chest_boat");
                            } else if (originalLayerName.startsWith("boat/")) {
                                mobNameForFileAndMap.setMapIdAndSecondaryFileName(originalLayerName.startsWith("boat/bamboo") ? "raft" : "boat");
                            }
                        }
                    }
                }
            }

            if (EMF.config().getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE
                    && !currentSpecifiedModelLoading.isBlank() && currentSpecifiedModelLoading.contains(":")) {
                EMFUtils.log("EMF modifiable modded block entity model identified during loading: " + mobNameForFileAndMap.getfileName() + ".jem");
            }

            //if file name isn't valid for identifiers
            //uses the static Identifier method to be influenced by other mods affecting resource name validity
            if (!ResourceLocation.isValidPath(mobNameForFileAndMap.getfileName() + ".jem")) {
                String newValidPath = mobNameForFileAndMap.getfileName().replaceAll("[^a-z0-9/_.-]", "_");
                mobNameForFileAndMap.setBoth(newValidPath, mobNameForFileAndMap.getMapId());
            }

            ///jem name is final and correct from here
            mobNameForFileAndMap.finishAndPrepSecondaries();

            cache_LayersByModelName.put(mobNameForFileAndMap, layer);
            if (mobNameForFileAndMap.getSecondaryModel() != null){
                cache_LayersByModelName.put(mobNameForFileAndMap.getSecondaryModel(), layer);
            }

            //construct simple map for modded or unknown entities
            Map<String, String> optifinePartNameMap = EMFModelMappings.getMapOf(mobNameForFileAndMap.getMapId(), root);

            if (printing) EMFUtils.log(" >> EMF trying to find model: " + mobNameForFileAndMap.getNamespace() + ":optifine/cem/" + mobNameForFileAndMap + ".jem");

            var modelDataAndContext = getJemAndContext(printing, mobNameForFileAndMap);

            //try with secondary model
            var secondaryFileMap = mobNameForFileAndMap.getSecondaryModel();
            if (secondaryFileMap != null) {
                if (printing) EMFUtils.log(" >> EMF trying to find secondary model: " + secondaryFileMap.getNamespace() + ":optifine/cem/" + secondaryFileMap + ".jem");

                var secondaryModelDataAndContext = getJemAndContext(printing, secondaryFileMap);

                var jemDataFirst = modelDataAndContext.getLeft();
                var jemData2 = secondaryModelDataAndContext.getLeft();

                boolean isFirstModelInValid = jemDataFirst == null && modelDataAndContext.getMiddle().getRight() == null;
                if (isFirstModelInValid ||
                        (jemDataFirst != null && jemData2 != null && jemData2.directoryContext.packIndex() > jemDataFirst.directoryContext.packIndex())) {
                    //just use second if first is invalid or if the secondary model is in a higher resource pack
                    modelDataAndContext.setLeft(jemData2);
                    modelDataAndContext.setMiddle(secondaryModelDataAndContext.getMiddle());
                    modelDataAndContext.setRight(secondaryFileMap);
                }
            }

            EMFJemData jemData = modelDataAndContext.getLeft();
            var directoryContextBaseAndVariant = modelDataAndContext.getMiddle();
            EMFModel_ID finalMapData = modelDataAndContext.getRight();

            boolean hasVariants = directoryContextBaseAndVariant.getRight() != null;

            if (jemData != null || hasVariants) {
                //we do have custom models

                //abort with message if we have variant models and no base model and the setting to require this like OptiFine is set
                if(jemData == null && EMF.config().getConfig().variationRequiresDefaultModel){
                    EMFUtils.logWarn("The model [" + finalMapData.getfileName() +"] has variation but does not have a default 'base' model, this is not allowed in the OptiFine format.\nYou may disable this requirement in EMF in the 'model > options' settings. Though it is usually best to preserve OptiFine compatibility.\nYou can get a default model by exporting it in the EMF settings via 'models > allmodels > *model* > export'");
                }else {
                    //specification for the optifine map
                    // only used for tadpole head parts currently as optifine actually uses the root as the body
                    Set<String> optifinePartNames = new HashSet<>();
                    optifinePartNameMap.forEach((optifine, vanilla) -> {
                        if (!optifine.equals("EMPTY")) {
                            optifinePartNames.add(vanilla);
                        }
                    });

                    EMFModelPartRoot emfRoot = new EMFModelPartRoot(finalMapData, Objects.requireNonNullElseGet(directoryContextBaseAndVariant.getLeft(),directoryContextBaseAndVariant::getRight), root, optifinePartNames, new HashMap<>());
                    if (jemData != null) {
                        emfRoot.addVariantOfJem(jemData, 1);
                        emfRoot.setVariantStateTo(1);
                        setupAnimationsFromJemToModel(jemData, emfRoot, 1);
                        emfRoot.containsCustomModel = true;
                        if (hasVariants) {
                            emfRoot.discoverAndInitVariants();
                        }else if (!modded && jemData.directoryContext.isSubFolder && EMF.config().getConfig().enforceOptifineSubFoldersVariantOnly){
                            EMFUtils.logError("Error loading ["+jemData.directoryContext.getFinalFileLocation()+"] as it is in a subfolder but does not have any variants. This is not allowed in the OptiFine format. You may disable this requirement in EMF's settings at 'model > OptiFine settings'. Though it is usually best to preserve OptiFine compatibility.");
                            throw new Exception("Subfolder without variants, OptiFine compat enabled");
                        }
                    } else {
                        emfRoot.setVariant1ToVanilla0();
                        emfRoot.discoverAndInitVariants();
                    }

                    if (emfRoot.containsCustomModel) {
                        lastCreatedRootModelPart = emfRoot;

                        // set EBE config if required
                        if (IS_EBE_INSTALLED) {
                            if (currentBlockEntityTypeLoading != null && EBETypes.containsKey(currentBlockEntityTypeLoading)) {
                                EBE_JEMS_FOUND.add(EBETypes.get(currentBlockEntityTypeLoading));
                            }
                        }
                        if (printing) EMFUtils.logWarn(" > EMF model used for: " + mobNameForFileAndMap);
                        return emfRoot;//tada
                    }
                }
            }

            if (printing) EMFUtils.logWarn(" > Vanilla model used for: " + mobNameForFileAndMap);//original name
            ((IEMFModelNameContainer) root).emf$insertKnownMappings(mobNameForFileAndMap);
            return root;
        } catch (Exception e) {
            EMFUtils.logWarn("default model returned for " + layer + " due to exception: " + e);
            ((IEMFModelNameContainer) root).emf$insertKnownMappings(mobNameForFileAndMap);
            return root;
        }
    }

    private MutableTriple<EMFJemData, ImmutablePair<EMFDirectoryHandler, EMFDirectoryHandler>, EMFModel_ID> getJemAndContext(boolean printing, EMFModel_ID mobNameForFileAndMap){
        EMFDirectoryHandler baseModelDir = EMFDirectoryHandler.getDirectoryManagerOrNull(printing, mobNameForFileAndMap.getNamespace(), mobNameForFileAndMap.getfileName(), ".jem");
        EMFDirectoryHandler propertiesOrSecondDir = EMFDirectoryHandler.getDirectoryManagerOrNull(printing, mobNameForFileAndMap.getNamespace(), mobNameForFileAndMap.getfileName(), ".properties");
        if (propertiesOrSecondDir == null)
            propertiesOrSecondDir = EMFDirectoryHandler.getDirectoryManagerOrNull(printing, mobNameForFileAndMap.getNamespace(), mobNameForFileAndMap.getfileName(), "2.jem");

        //discard the variation context if they are in a lower pack to the base model and not in matching sub folder locations
        if(baseModelDir != null && !baseModelDir.validForThisBase(propertiesOrSecondDir)) propertiesOrSecondDir = null;

        EMFJemData jemDataFirst = baseModelDir == null ? null : getJemDataWithDirectory(baseModelDir, mobNameForFileAndMap);

        //pack result
        return MutableTriple.of(jemDataFirst, ImmutablePair.of(baseModelDir,propertiesOrSecondDir),mobNameForFileAndMap);
    }

    private void getDoubleChest(ModelPart root, EMFModel_ID mobNameForFileAndMap, boolean isRight, boolean printing) {
        String thisSide = isRight ? "right" : "left";
        String otherSide = isRight ? "left" : "right";

        mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading + "_large", "double_chest_" + thisSide);
        if (EMF.config().getConfig().doubleChestAnimFix) {
            if (printing) EMFUtils.log("injecting empty " + otherSide + " side parts into 'double chest' for animation purposes");
            Map<String, ModelPart> newChildren = new HashMap<>(root.children);
            newChildren.putIfAbsent("lid_" + otherSide, new ModelPart(List.of(), Map.of()));
            newChildren.putIfAbsent("base_" + otherSide, new ModelPart(List.of(), Map.of()));
            newChildren.putIfAbsent("knob_" + otherSide, new ModelPart(List.of(), Map.of()));
            root.children = newChildren; // mutable
        }
    }

    public void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPartRoot emfRootPart, int variantNum) {

        boolean printing = EMF.config().getConfig().logModelCreationData;

        Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = new Object2ObjectOpenHashMap<>();
        allPartsBySingleAndFullHeirachicalId.put("root", emfRootPart);
        allPartsBySingleAndFullHeirachicalId.putAll(emfRootPart.getAllChildPartsAsAnimationMap("", variantNum, EMFModelMappings.getMapOf(emfRootPart.modelName.getMapId(), null)));

        Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimations = new Object2ObjectLinkedOpenHashMap<>();


        if (printing) {
            EMFUtils.log(" > finalAnimationsForModel =");
            jemData.getAllTopLevelAnimationsByVanillaPartName().forEach((part, anims) -> anims.forEach((key, expression) -> EMFUtils.log(" >> " + key + " = " + expression)));
        }
        jemData.getAllTopLevelAnimationsByVanillaPartName().forEach((part, anims) -> {
            anims.forEach((animKey, animationExpression) -> {
                if (EMF.config().getConfig().logModelCreationData)
                    EMFUtils.log("parsing animation value: [" + animKey + "]");

                String[] animKeyParts = animKey.split("\\.");
                String modelId = animKeyParts[0];
                String modelVariable = animKeyParts[1];

                EMFModelOrRenderVariable thisVariable = EMFModelOrRenderVariable.get(modelVariable);
                if (thisVariable == null) thisVariable = EMFModelOrRenderVariable.getRenderVariable(animKey);

                EMFModelPart thisPart = "render".equals(modelId) ? null : getModelFromHierarchichalId(modelId, allPartsBySingleAndFullHeirachicalId);

                EMFAnimation newAnimation = new EMFAnimation(
                        thisPart,
                        thisVariable,
                        animKey,
                        animationExpression,
                        jemData.directoryContext.getFileNameWithType()
                );

                if (emfAnimations.containsKey(animKey) && emfAnimations.get(animKey).isVar()) {
                    //this is a secondary variable modification
                    String key = animKey + '#';
                    while (emfAnimations.containsKey(key)) {
                        key += '#';
                    }
                    // add it in the animation list but alter the key name
                    emfAnimations.put(key, newAnimation);
                    //set this variable to instead set the value of the true variable source
                    //newAnimation.setTrueVariableSource(emfAnimations.get(animKey));
                } else {
                    emfAnimations.put(animKey, newAnimation);
                }
            });
            //emfAnimationsByPartName.put(part, thisPartAnims);
        });

        isAnimationValidationPhase = true;
        Iterator<EMFAnimation> animMapIterate = emfAnimations.values().iterator();
        while (animMapIterate.hasNext()) {
            EMFAnimation anim = animMapIterate.next();
            if (anim != null) {
                anim.initExpression(emfAnimations, allPartsBySingleAndFullHeirachicalId);
                if (!anim.isValid()) {
                    EMFUtils.logError("animation was invalid: [" + anim.animKey + "] = [" + anim.expressionString + "] in model [" + emfRootPart.modelName + "]");
                    //animMapIterate.remove();
                    isAnimationValidationPhase = false;
                    return;
                }
            } else {
                animMapIterate.remove();
            }
        }
        isAnimationValidationPhase = false;

        emfRootPart.receiveAnimations(variantNum, emfAnimations.values()); //emfAnimationsByPartName);
    }
}
