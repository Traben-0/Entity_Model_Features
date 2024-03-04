package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mod_compat.EBEConfigModifier;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.IEMFModelNameContainer;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class EMFManager {//singleton for data holding and resetting needs


    private static final Map<BlockEntityType<?>, String> EBETypes = Map.of(
            BlockEntityType.BED, "bed",
            BlockEntityType.CHEST, "chest",
            BlockEntityType.TRAPPED_CHEST, "chest",
            BlockEntityType.ENDER_CHEST, "chest",
            BlockEntityType.SHULKER_BOX, "shulker_box",
            BlockEntityType.BELL, "bell",
            BlockEntityType.SIGN, "sign")
//            ,
//            BlockEntityType.DECORATED_POT, "decorated_pot")
            ;
    public static EMFModelPartRoot lastCreatedRootModelPart = null;
    private static EMFManager self = null;
    public final boolean IS_PHYSICS_MOD_INSTALLED;
    public final boolean IS_EBE_INSTALLED;
    public final Object2ObjectLinkedOpenHashMap<String, Set<EMFModelPartRoot>> rootPartsPerEntityTypeForDebug = new Object2ObjectLinkedOpenHashMap<>() {{
        defaultReturnValue(null);
    }};
    public final ObjectSet<OptifineMobNameForFileAndEMFMapId> modelsAnnounced = new ObjectOpenHashSet<>();
    private final Object2ObjectOpenHashMap<String, EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<EntityModelLayer> amountOfLayerAttempts = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(0);
    }};
    private final Set<String> EBE_JEMS_FOUND = new HashSet<>();
    public UUID entityForDebugPrint = null;
    public long entityRenderCount = 0;
    public boolean isAnimationValidationPhase = false;
    public String currentSpecifiedModelLoading = "";
    public BlockEntityType<?> currentBlockEntityTypeLoading = null;
    public final Object2ObjectLinkedOpenHashMap<String, Set<Runnable>> rootPartsPerEntityTypeForVariation = new Object2ObjectLinkedOpenHashMap<>() {{
        defaultReturnValue(null);
    }};
    private boolean traderLlamaHappened = false;

    private EMFManager() {
        EMFAnimationEntityContext.reset();
        IS_PHYSICS_MOD_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("physicsmod");
//        IS_IRIS_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("iris") || EMFVersionDifferenceManager.isThisModLoaded("oculus");
        IS_EBE_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("enhancedblockentities");
    }

    public static EMFManager getInstance() {
        if (self == null) self = new EMFManager();
        return self;
    }

    public static void resetInstance() {
        EMFUtils.log("[EMF (Entity Model Features)]: Clearing data for reload.", false, true);
        EMFOptiFinePartNameMappings.UNKNOWN_MODEL_MAP_CACHE.clear();
        self = new EMFManager();
    }

//    public static ModelPart traverseRootForChildOrNull(ModelPart root, String nameOfModelToFind) {
//        if (modelPartHasChild(root,nameOfModelToFind))
//            return root.getChild(nameOfModelToFind);
//        for (ModelPart part :
//                ((ModelPartAccessor) root).getChildren().values()) {
//            ModelPart found = traverseRootForChildOrNull(part, nameOfModelToFind);
//            if (found != null)
//                return found;
//        }
//        return null;
//    }

//    public static String getTypeName(Entity entity) {
//        String forReturn = Registry.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:", "");
////        if (entity instanceof PlayerEntity plyr && plyr.thin ((PlayerEntityModelAccessor) plyr).isThinArms()) {
////            forReturn = entityTypeBaseName + "_slim";
////        } else
//
//
//        if(forReturn.contains(":")){
//            forReturn = "modded/"+forReturn.replaceFirst(":","/");
////            String[] split = forReturn.split(":");
////            if(split.length == 2 && !split[0].isBlank() && !split[1].isBlank())
////                forReturn = "modded/"+split[0]+"/"+split[1];
//        }


//    public static String getTypeName(Entity entity) {
//        String forReturn = Registry.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:", "");
////        if (entity instanceof PlayerEntity plyr && plyr.thin ((PlayerEntityModelAccessor) plyr).isThinArms()) {
////            forReturn = entityTypeBaseName + "_slim";
////        } else
//
//
//        if(forReturn.contains(":")){
//            forReturn = "modded/"+forReturn.replaceFirst(":","/");
////            String[] split = forReturn.split(":");
////            if(split.length == 2 && !split[0].isBlank() && !split[1].isBlank())
////                forReturn = "modded/"+split[0]+"/"+split[1];
//        }
//
//
//        if (entity instanceof PufferfishEntity puffer) {
//            forReturn = "puffer_fish_" + switch (puffer.getPuffState()) {
//                case 0 -> "small";
//                case 1 -> "medium";
//                default -> "big";
//            };
//        } else if (entity instanceof TropicalFishEntity fish) {
//            forReturn =  (fish.getShape() == 0 ? "tropical_fish_a" : "tropical_fish_b");
////        } else if (entity instanceof LlamaEntity llama) {
////            forReturn = llama.isTrader() ? "trader_llama" : "llama";
//        } else if (entity instanceof EnderDragonEntity) {
//            forReturn = "dragon";
//        }
//
//
//        return forReturn;
//    }

    @Nullable
    public static EMFJemData getJemData(String jemFileName, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {

        //try emf folder
        EMFJemData emfJemData = getJemDataWithDirectory("emf/cem/" + jemFileName, mobModelIDInfo);
        if (emfJemData != null) return emfJemData;
        emfJemData = getJemDataWithDirectory("emf/cem/" + mobModelIDInfo + "/" + jemFileName, mobModelIDInfo);
        if (emfJemData != null) return emfJemData;

        //try read optifine jems
        emfJemData = getJemDataWithDirectory("optifine/cem/" + jemFileName, mobModelIDInfo);
        if (emfJemData != null) return emfJemData;
        emfJemData = getJemDataWithDirectory("optifine/cem/" + mobModelIDInfo + "/" + jemFileName, mobModelIDInfo);
        return emfJemData;

    }

    @Nullable
    public static CemDirectoryApplier getResourceCemDirectoryApplierOrNull(String inCemPathResource, String rawMobName) {
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();
        //try emf folder
        if (isExistingFile(new Identifier("emf/cem/" + inCemPathResource)))
            return CemDirectoryApplier.getEMF();
        if (isExistingFile(new Identifier("emf/cem/" + rawMobName + "/" + inCemPathResource)))
            return CemDirectoryApplier.getEMF_Mob(rawMobName);
        if (isExistingFile(new Identifier("optifine/cem/" + inCemPathResource)))
            return CemDirectoryApplier.getCEM();
        if (isExistingFile(new Identifier("optifine/cem/" + rawMobName + "/" + inCemPathResource)))
            return CemDirectoryApplier.getCem_Mob(rawMobName);
        return null;
    }

    public static boolean isExistingFile(Identifier path){
        try{
            Resource res = MinecraftClient.getInstance().getResourceManager().getResource(path);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Nullable
    public static EMFJemData getJemDataWithDirectory(String pathOfJem, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        if (EMFManager.getInstance().cache_JemDataByFileName.containsKey(pathOfJem)) {
            return EMFManager.getInstance().cache_JemDataByFileName.get(pathOfJem);
        }
        try {
            Resource jemResource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem));

            if (EMFConfig.getConfig().logModelCreationData)
                EMFUtils.log(".jem read success " + pathOfJem + " exists", false);
//            Resource jemResource = res.get();
            //File jemFile = new File(pathOfJem);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println("jem exists "+ jemFile.exists());
            //if (jemFile.exists()) {
            //FileReader fileReader = new FileReader(jemFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(jemResource.getInputStream()));

            EMFJemData jem = gson.fromJson(reader, EMFJemData.class);
            reader.close();
            jem.prepare(pathOfJem, mobModelIDInfo);
            if (mobModelIDInfo.areBothSame())
                EMFManager.getInstance().cache_JemDataByFileName.put(pathOfJem, jem);
            return jem;
            //}
        } catch (IOException e) {
            if (EMFConfig.getConfig().logModelCreationData)
                EMFUtils.log(".jem read failed " + pathOfJem + " does not exist", false);
        } catch (InvalidIdentifierException e) {
            if (EMFConfig.getConfig().logModelCreationData)
                EMFUtils.log(".jem failed to load " + e, false);
        } catch (Exception e) {
            EMFUtils.log(".jem failed to load " + e, false);
        }
        return null;
    }

    public static EMFModelPart getModelFromHierarchichalId(String hierarchId, Map<String, EMFModelPart> map) {
        if (hierarchId == null || hierarchId.isBlank()) return null;
        if (!hierarchId.contains(":")) {
            EMFModelPart part = map.get(hierarchId);
            if (part == null)
                return map.get("EMF_" + hierarchId);
            return part;
        }
        for (Map.Entry<String, EMFModelPart> entry :
                map.entrySet()) {
            if (entry.getKey().equals(hierarchId) || entry.getKey().equals("EMF_" + hierarchId)
                    || (entry.getKey().endsWith(":" + hierarchId)) || (entry.getKey().endsWith(":EMF_" + hierarchId)))
                return entry.getValue();
            boolean anyMissing = false;
            String last = "";
            for (String str :
                    hierarchId.split(":")) {
                last = str;
                if (!(entry.getKey().contains(str) || entry.getKey().contains("EMF_" + str))) {
                    anyMissing = true;
                    break;
                }
            }
            if (!anyMissing && entry.getKey().endsWith(last)) return entry.getValue();
        }
        //all possible occurances should be accounted for above must be null
        EMFUtils.logWarn("NULL animation hierachy id result of: " + hierarchId + "\n in " + map);
        return null;
    }

    public void modifyEBEIfRequired() {
        if (IS_EBE_INSTALLED && !EBE_JEMS_FOUND.isEmpty() && EMFConfig.getConfig().allowEBEModConfigModify) {
            try {
                EBEConfigModifier.modifyEBEConfig(EBE_JEMS_FOUND);
            } catch (Exception | Error e) {
                EMFUtils.logWarn("EBE config modification issue: " + e);
            }
        }
        EBE_JEMS_FOUND.clear();
    }

    public ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root) {
        int creationsOfLayer = amountOfLayerAttempts.put(layer, amountOfLayerAttempts.getInt(layer) + 1);
        if (creationsOfLayer > 500) {
            if (creationsOfLayer == 501) {
                EMFUtils.logWarn("model attempted creation more than 500 times {" + layer.toString() + "]. EMF is now ignoring this model.");
            }
            return root;
        }

        String originalLayerName = layer.getId().getPath();
        OptifineMobNameForFileAndEMFMapId mobNameForFileAndMap = new OptifineMobNameForFileAndEMFMapId(
                currentSpecifiedModelLoading.isBlank() ? originalLayerName : currentSpecifiedModelLoading);


        try {


            EMFManager.lastCreatedRootModelPart = null;

            boolean printing = (EMFConfig.getConfig().logModelCreationData);


            if (!"main".equals(layer.getName())) {
                mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName() + "_" + layer.getName());
                originalLayerName = originalLayerName + "_" + layer.getName();
            }

            //add simple modded layer checks
            if (!"minecraft".equals(layer.getId().getNamespace())) {
                mobNameForFileAndMap.setBoth(("modded/" + layer.getId().getNamespace() + "/" + originalLayerName).toLowerCase().replaceAll("[^a-z0-9/._-]", "_"));
            } else {
                //vanilla model
                if (mobNameForFileAndMap.getfileName().contains("pufferfish"))
                    mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName().replace("pufferfish", "puffer_fish"));


                switch (originalLayerName) {
                    case "tropical_fish_large" -> mobNameForFileAndMap.setBoth("tropical_fish_b");
                    case "tropical_fish_small" -> mobNameForFileAndMap.setBoth("tropical_fish_a");
                    case "tropical_fish_large_pattern" -> mobNameForFileAndMap.setBoth("tropical_fish_pattern_b");
                    case "tropical_fish_small_pattern" -> mobNameForFileAndMap.setBoth("tropical_fish_pattern_a");
                    case "leash_knot" -> mobNameForFileAndMap.setBoth("lead_knot");
                    case "trader_llama" -> traderLlamaHappened = true;
                    case "llama" -> traderLlamaHappened = false;
                    case "llama_decor" ->
                            mobNameForFileAndMap.setBoth(traderLlamaHappened ? "trader_llama_decor" : "llama_decor");
                    case "ender_dragon" -> mobNameForFileAndMap.setBoth("dragon");
                    case "dragon_skull" -> mobNameForFileAndMap.setBoth("head_dragon");
                    case "player_head" -> mobNameForFileAndMap.setBoth("head_player");
                    case "skeleton_skull" -> mobNameForFileAndMap.setBoth("head_skeleton");
                    case "wither_skeleton_skull" -> mobNameForFileAndMap.setBoth("head_wither_skeleton");
                    case "zombie_head" -> mobNameForFileAndMap.setBoth("head_zombie");
                    case "creeper_head" -> mobNameForFileAndMap.setBoth("head_creeper");
                    case "piglin_head" -> mobNameForFileAndMap.setBoth("head_piglin");
                    case "creeper_armor" -> mobNameForFileAndMap.setBoth("creeper_charge");
                    case "sheep_fur" -> mobNameForFileAndMap.setBoth("sheep_wool");
                    case "bed_head" -> mobNameForFileAndMap.setBoth("bed", "bed_head");
                    case "bed_foot" -> mobNameForFileAndMap.setBoth("bed", "bed_foot");
                    case "conduit_cage" -> mobNameForFileAndMap.setBoth("conduit", "conduit_cage");
                    case "conduit_eye" -> mobNameForFileAndMap.setBoth("conduit", "conduit_eye");
                    case "conduit_shell" -> mobNameForFileAndMap.setBoth("conduit", "conduit_shell");
                    case "conduit_wind" -> mobNameForFileAndMap.setBoth("conduit", "conduit_wind");
                    case "decorated_pot_base" -> mobNameForFileAndMap.setBoth("decorated_pot", "decorated_pot_base");
                    case "decorated_pot_sides" -> mobNameForFileAndMap.setBoth("decorated_pot", "decorated_pot_sides");
                    case "book" -> {
                        if (currentSpecifiedModelLoading.equals("enchanting_book")) {
                            mobNameForFileAndMap.setBoth("enchanting_book", "book");
                        } else/* if(currentSpecifiedModelLoading.equals("lectern_book"))*/ {
                            mobNameForFileAndMap.setBoth("lectern_book", "book");
                        }
                    }
                    case "chest" -> mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading, "chest");
                    case "double_chest_left" ->
                            mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading + "_large", "double_chest_left");
                    case "double_chest_right" ->
                            mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading + "_large", "double_chest_right");
                    case "shulker" -> {
                        if (currentSpecifiedModelLoading.equals("shulker_box")) {
                            mobNameForFileAndMap.setBoth("shulker_box");
                        } else {
                            mobNameForFileAndMap.setBoth("shulker");
                        }
                    }

                    default -> {


                        if (!currentSpecifiedModelLoading.isBlank()) {
                            switch (currentSpecifiedModelLoading) {
                                case "sign", "hanging_sign" ->
                                        mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading);
                                default -> {
                                    if (EMFConfig.getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE)
                                        EMFUtils.log("EMF unknown modifiable block entity model identified during loading: " + currentSpecifiedModelLoading + ".jem");
                                    mobNameForFileAndMap.setBoth(currentSpecifiedModelLoading);
                                }
                            }
                        } else if (originalLayerName.contains("/") && layer.getName().equals("main")) {
//                            if (switchKey.startsWith("hanging_sign/")) {
//                                mobNameForFileAndMap.setBoth("hanging_sign");
//                            } else if (switchKey.startsWith("sign/")) {
//                                mobNameForFileAndMap.setBoth("sign");
//                            } else
                            if (originalLayerName.startsWith("chest_boat/")) {
                                if (originalLayerName.startsWith("chest_boat/bamboo")) {
                                    mobNameForFileAndMap.setBoth("chest_raft");
                                } else {
                                    mobNameForFileAndMap.setBoth("chest_boat");
                                }
                            } else if (originalLayerName.startsWith("boat/")) {
                                if (originalLayerName.startsWith("boat/bamboo")) {
                                    mobNameForFileAndMap.setBoth("raft");
                                } else {
                                    mobNameForFileAndMap.setBoth("boat");
                                }
                            }
                        }
                    }
                }
            }

            if (EMFConfig.getConfig().modelExportMode != EMFConfig.ModelPrintMode.NONE
                    && !currentSpecifiedModelLoading.isBlank() && currentSpecifiedModelLoading.startsWith("modded/")) {
                EMFUtils.log("EMF modifiable modded block entity model identified during loading: " + mobNameForFileAndMap.getfileName() + ".jem");
            }

            //if file name isn't valid for identifiers
            //uses the static Identifier method to be influenced by other mods affecting resource name validity
            if (!isIdentifierPathValid(mobNameForFileAndMap.getfileName() + ".jem")) {
                String newValidPath = mobNameForFileAndMap.getfileName().replaceAll("[^a-z0-9/_.-]", "_");
                mobNameForFileAndMap.setBoth(newValidPath, mobNameForFileAndMap.getMapId());
            }

            if (printing) EMFUtils.log(" > EMF try to find a model for: " + mobNameForFileAndMap);


            ///jem name is final and correct from here

            //if (EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap).isEmpty()) {
            //construct simple map for modded or unknown entities
            Map<String, String> optifinePartNameMap = EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap.getMapId(), root);
            //}


            if (printing) EMFUtils.log(" >> EMF trying to find: optifine/cem/" + mobNameForFileAndMap + ".jem");
            String jemName = /*"optifine/cem/" +*/ mobNameForFileAndMap + ".jem";
            CemDirectoryApplier hasVariantsAndCanApplyThisDirectory = getResourceCemDirectoryApplierOrNull(mobNameForFileAndMap + ".properties", mobNameForFileAndMap.getfileName());// (MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("optifine/cem/" + mobNameForFileAndMap + ".properties")).isPresent());
            if (hasVariantsAndCanApplyThisDirectory == null) {
                hasVariantsAndCanApplyThisDirectory = getResourceCemDirectoryApplierOrNull(mobNameForFileAndMap + "2.jem", mobNameForFileAndMap.getfileName());
            }
            EMFJemData jemData = getJemData(jemName, mobNameForFileAndMap);
            if (jemData != null || hasVariantsAndCanApplyThisDirectory != null) {
                //we do indeed need custom models

                //specification for the optifine map
                // only used for tadpole head parts currently as optifine actually uses the root as the body
                Set<String> optifinePartNames = new HashSet<>();
                optifinePartNameMap.forEach((optifine, vanilla) -> {
                    if (!optifine.equals("EMPTY")) {
                        optifinePartNames.add(vanilla);
                    }
                });

                EMFModelPartRoot emfRoot = new EMFModelPartRoot(mobNameForFileAndMap, hasVariantsAndCanApplyThisDirectory, root, optifinePartNames, new HashMap<>());
                if (jemData != null) {
                    emfRoot.addVariantOfJem(jemData, 1);
                    emfRoot.setVariantStateTo(1);
                    setupAnimationsFromJemToModel(jemData, emfRoot, 1);
                    emfRoot.containsCustomModel = true;
                    if (hasVariantsAndCanApplyThisDirectory != null) {
                        emfRoot.discoverAndInitVariants();
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

                    return emfRoot;
                }
            }

            if (printing) EMFUtils.logWarn(" > Vanilla model used for: " + mobNameForFileAndMap);
            ((IEMFModelNameContainer) root).emf$insertKnownMappings(mobNameForFileAndMap);
            return root;
        } catch (Exception e) {
            EMFUtils.logWarn("default model returned for " + layer + " due to exception: " + e);
            ((IEMFModelNameContainer) root).emf$insertKnownMappings(mobNameForFileAndMap);
            return root;
        }
    }

    private static boolean isIdentifierPathValid(String path) {
        for(int i = 0; i < path.length(); ++i) {
            if (!Identifier.isPathCharacterValid(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPartRoot emfRootPart, int variantNum) {

        boolean printing = EMFConfig.getConfig().logModelCreationData;

        Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = new Object2ObjectOpenHashMap<>();
        allPartsBySingleAndFullHeirachicalId.put("EMF_root", emfRootPart);
        allPartsBySingleAndFullHeirachicalId.putAll(emfRootPart.getAllChildPartsAsAnimationMap("", variantNum, EMFOptiFinePartNameMappings.getMapOf(emfRootPart.modelName.getMapId(), null)));

        //Object2ObjectLinkedOpenHashMap<String, Object2ObjectLinkedOpenHashMap<String, EMFAnimation>> emfAnimationsByPartName = new Object2ObjectLinkedOpenHashMap<>();
        Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimations = new Object2ObjectLinkedOpenHashMap<>();


        if (printing) {
            EMFUtils.log(" > finalAnimationsForModel =");
            jemData.getAllTopLevelAnimationsByVanillaPartName().forEach((part, anims) -> anims.forEach((key, expression) -> EMFUtils.log(" >> " + key + " = " + expression)));
        }
        jemData.getAllTopLevelAnimationsByVanillaPartName().forEach((part, anims) -> {
            anims.forEach((animKey, animationExpression) -> {
                if (EMFConfig.getConfig().logModelCreationData)
                    EMFUtils.log("parsing animation value: [" + animKey + "]");
                String modelId = animKey.split("\\.")[0];
                String modelVariable = animKey.split("\\.")[1];

                EMFModelOrRenderVariable thisVariable = EMFModelOrRenderVariable.get(modelVariable);
                if (thisVariable == null) thisVariable = EMFModelOrRenderVariable.getRenderVariable(animKey);

                EMFModelPart thisPart = "render".equals(modelId) ? null : getModelFromHierarchichalId(modelId, allPartsBySingleAndFullHeirachicalId);

                EMFAnimation newAnimation = new EMFAnimation(
                        thisPart,
                        thisVariable,
                        animKey,
                        animationExpression,
                        jemData.getFileName()//, variableSuppliers
                );

                if (emfAnimations.containsKey(animKey) && emfAnimations.get(animKey).isVariable) {
                    //this is a secondary variable modification
                    // add it in the animation list but hash out the key name
                    emfAnimations.put(animKey + '#' + System.currentTimeMillis(), newAnimation);
                    //set this variable to instead set the value of the true variable source
                    newAnimation.setTrueVariableSource(emfAnimations.get(animKey));
                } else {
                    emfAnimations.put(animKey, newAnimation);
                }


            });

            //emfAnimationsByPartName.put(part, thisPartAnims);
        });
        isAnimationValidationPhase = true;
//        emfAnimationsByPartName.forEach((part, animMap) -> {


        Iterator<EMFAnimation> animMapIterate = emfAnimations.values().iterator();
        while (animMapIterate.hasNext()) {
            EMFAnimation anim = animMapIterate.next();
            if (anim != null) {
                anim.initExpression(emfAnimations, allPartsBySingleAndFullHeirachicalId);
                if (!anim.isValid()) {
                    EMFUtils.logWarn("animation was invalid: " + anim.animKey + " = " + anim.expressionString);
                    animMapIterate.remove();
                }
            } else {
                animMapIterate.remove();
            }
        }

        //});
        isAnimationValidationPhase = false;

        //EMFAnimationExecutor executor = new EMFAnimationExecutor(variableSuppliers, orderedAnimations);

//        if(emfRootPart.modelName.getMapId().contains("axolotl")) emfAnimationsByPartName.forEach((k,va)-> va.forEach((k2,v)-> System.out.println("axolotl>>> "+v.animKey+"="+v.expressionString+" ### "+(v.partToApplyTo == null ? "null" :v.partToApplyTo.toString()))));
        emfRootPart.receiveAnimations(variantNum, emfAnimations.values());

        //cache_EntityNameToAnimationExecutable.put(jemData.mobName, executor);
        ///////////////////////////
    }

//    public void doVariantCheckFor(EMFModelPartRoot cannonRoot) {
//        EMFEntity entity = EMFAnimationHelper.getEMFEntity();
//        //String mobName = getTypeName(entity);
//        //cache_JemNameDoesHaveVariants.getBoolean(mobName)
//        if (entity != null
//                && cannonRoot.variantDirectoryApplier != null
//                && cache_UUIDDoUpdating.getBoolean(entity.getUuid())
//                && ETFApi.getETFConfigObject().textureUpdateFrequency_V2 != ETFConfig.UpdateFrequency.Never
//        ) {
//            String mobName = cannonRoot.modelName.getfileName();
//            UUIDAndMobTypeKey key = new UUIDAndMobTypeKey(entity.getUuid(), entity.getTypeString());
//
//            long randomizer = ETFApi.getETFConfigObject().textureUpdateFrequency_V2.getDelay() * 20L;
//            if (System.currentTimeMillis() % randomizer == Math.abs(entity.getUuid().hashCode()) % randomizer) {
//                //if (cache_UUIDAndTypeToLastVariantCheckTime.getLong(key) + 1500 < System.currentTimeMillis()) {
//
//                if (cannonRoot.variantTester == null) {
//                    Identifier propertyID = new Identifier(cannonRoot.variantDirectoryApplier.getThisDirectoryOfFilename(mobName + ".properties"));
//                    if (isExistingFile(propertyID)) {
//                        cannonRoot.variantTester = ETFApi.readRandomPropertiesFileAndReturnTestingObject3(propertyID, "models");
//                    } else {
//                        EMFUtils.EMFModWarn("no property" + propertyID);
//                        //cache_JemNameDoesHaveVariants.put(mobName, false);
//                        cannonRoot.variantDirectoryApplier = null;
//                        return;
//                    }
//                }
//                ETFApi.ETFRandomTexturePropertyInstance emfProperty = cannonRoot.variantTester;
//                if (emfProperty != null) {
//                    int suffix;
//                    if (entity.entity() == null) {
//                        suffix = emfProperty.getSuffixForBlockEntity(entity.getBlockEntity(), entity.getUuid(), cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
//                    } else {
//                        suffix = emfProperty.getSuffixForEntity(entity.entity(), cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
//                    }
//
//                    //EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
//                    if (suffix > 1) { // ignore 0 & 1
//                        //System.out.println(" > apply model variant: "+suffix +", to "+mobName);
//                        if (!cannonRoot.allKnownStateVariants.containsKey(suffix)) {
//                            String jemName = cannonRoot.variantDirectoryApplier.getThisDirectoryOfFilename(mobName + suffix + ".jem");
//                            if (EMFConfig.getConfig().logModelCreationData)
//                                System.out.println(" >> first time load of : " + jemName);
//                            EMFJemData jemData = getJemDataWithDirectory(jemName, cannonRoot.modelName);
//                            if (jemData != null) {
//                                cannonRoot.addVariantOfJem(jemData, suffix);
//                                cannonRoot.setVariantStateTo(suffix);
//                                setupAnimationsFromJemToModel(jemData, cannonRoot, suffix);
////                                ModelPart vanillaRoot = cannonRoot.vanillaRoot; //cache_JemNameToVanillaModelRoot.get(mobName);
////                                if (vanillaRoot != null) {
////                                    EMFModelPartMutable variantRoot = getEMFRootModelFromJem(jemData, vanillaRoot, suffix);
////                                    cannonRoot.mergePartVariant(suffix, variantRoot);
////                                    setupAnimationsFromJemToModel(jemData, cannonRoot,suffix);
////                                }
//                            } else {
//                                System.out.println("invalid jem: " + jemName);
//                            }
//                        }
//                        cannonRoot.setVariantStateTo(suffix);
//                        cache_UUIDAndTypeToCurrentVariantInt.put(key, suffix);
//                    } else {
//                        cannonRoot.setVariantStateTo(1);
//                        cache_UUIDAndTypeToCurrentVariantInt.put(key, 1);
//                    }
//                } else {
//                    cannonRoot.variantDirectoryApplier = null;
//                    cannonRoot.setVariantStateTo(1);
//                }
//                // cache_UUIDAndTypeToLastVariantCheckTime.put(key, System.currentTimeMillis());
//            } else {
//                //EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
//                cannonRoot.setVariantStateTo(cache_UUIDAndTypeToCurrentVariantInt.getInt(key));
//            }
//        } else {
//            cannonRoot.setVariantStateTo(1);
//        }
//    }


    public interface CemDirectoryApplier {
        static CemDirectoryApplier getEMF() {
            return (fileName) -> "emf/cem/" + fileName;
        }

        static CemDirectoryApplier getEMF_Mob(String mobname) {
            return (fileName) -> "emf/cem/" + mobname + "/" + fileName;
        }

        static CemDirectoryApplier getCEM() {
            return (fileName) -> "optifine/cem/" + fileName;
        }

        static CemDirectoryApplier getCem_Mob(String mobName) {
            return (fileName) -> "optifine/cem/" + mobName + "/" + fileName;
        }

        String getThisDirectoryOfFilename(String fileName);
    }


}
