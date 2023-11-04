package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.models.animation.EMFModelOrRenderVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;


public class EMFManager {//singleton for data holding and resetting needs

    public static WolfEntityModel<WolfEntity> wolfCollarModel = null;
    public static EMFModelPartRoot lastCreatedRootModelPart = null;
    private static EMFManager self = null;
    public final boolean IS_PHYSICS_MOD_INSTALLED;
    public final boolean IS_IRIS_INSTALLED;
    private final Object2ObjectOpenHashMap<String, EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<>();

    private final Object2BooleanOpenHashMap<UUID> cache_UUIDDoUpdating = new Object2BooleanOpenHashMap<>() {{
        defaultReturnValue(true);
    }};
    private final Object2IntOpenHashMap<UUIDAndMobTypeKey> cache_UUIDAndTypeToCurrentVariantInt = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(1);
    }};
    //private final Object2IntOpenHashMap<String> COUNT_OF_MOB_NAME_ALREADY_SEEN = new Object2IntOpenHashMap<>();
    public long entityRenderCount = 0;
    public boolean isAnimationValidationPhase = false;
    public String currentSpecifiedModelLoading = "";
    private boolean traderLlamaHappened = false;


    private EMFManager() {
        EMFAnimationHelper.resetForNewEntity();
        IS_PHYSICS_MOD_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("physicsmod");
        IS_IRIS_INSTALLED = EMFVersionDifferenceManager.isThisModLoaded("iris") || EMFVersionDifferenceManager.isThisModLoaded("oculus");
    }

    public static EMFManager getInstance() {
        if (self == null) self = new EMFManager();
        return self;
    }

    public static void resetInstance() {
        EMFUtils.EMFModMessage("Clearing EMF data.");
        EMFOptiFinePartNameMappings.UNKNOWN_MODEL_MAP_CACHE.clear();
        self = new EMFManager();
    }


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
        if (resources.getResource(new Identifier("emf/cem/" + inCemPathResource)).isPresent())
            return CemDirectoryApplier.getEMF();
        if (resources.getResource(new Identifier("emf/cem/" + rawMobName + "/" + inCemPathResource)).isPresent())
            return CemDirectoryApplier.getEMF_Mob(rawMobName);
        if (resources.getResource(new Identifier("optifine/cem/" + inCemPathResource)).isPresent())
            return CemDirectoryApplier.getCEM();
        if (resources.getResource(new Identifier("optifine/cem/" + rawMobName + "/" + inCemPathResource)).isPresent())
            return CemDirectoryApplier.getCem_Mob(rawMobName);
        return null;
    }

    @Nullable
    private static EMFJemData getJemDataWithDirectory(String pathOfJem, OptifineMobNameForFileAndEMFMapId mobModelIDInfo) {
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        if (EMFManager.getInstance().cache_JemDataByFileName.containsKey(pathOfJem)) {
            return EMFManager.getInstance().cache_JemDataByFileName.get(pathOfJem);
        }
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem));
            if (res.isEmpty()) {
                if (EMFConfig.getConfig().logModelCreationData)
                    EMFUtils.EMFModMessage(".jem read failed " + pathOfJem + " does not exist", false);
                return null;
            }
            if (EMFConfig.getConfig().logModelCreationData)
                EMFUtils.EMFModMessage(".jem read success " + pathOfJem + " exists", false);
            Resource jemResource = res.get();
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
        } catch (InvalidIdentifierException | FileNotFoundException e) {
            if (EMFConfig.getConfig().logModelCreationData)
                EMFUtils.EMFModMessage(".jem failed to load " + e, false);
        } catch (Exception e) {
            EMFUtils.EMFModMessage(".jem failed to load " + e, false);
            e.printStackTrace();
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
        System.out.println("NULL animation hierachy id result of: " + hierarchId + "\n in " + map);
        return null;
    }

    public ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root) {
        try {
            EMFManager.lastCreatedRootModelPart = null;

            boolean printing = (EMFConfig.getConfig().logModelCreationData);

            OptifineMobNameForFileAndEMFMapId mobNameForFileAndMap = new OptifineMobNameForFileAndEMFMapId(layer.getId().getPath());


            if (!"main".equals(layer.getName())) {
                mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName() + "_" + layer.getName());
            }
            //add simple modded check
            if (!"minecraft".equals(layer.getId().getNamespace())) {
                mobNameForFileAndMap.setBoth(("modded/" + layer.getId().getNamespace() + "/" + mobNameForFileAndMap).toLowerCase().replaceAll("[^a-z0-9/._-]", "_"));
            } else {
                //vanilla model
                if (mobNameForFileAndMap.getfileName().contains("pufferfish"))
                    mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName().replace("pufferfish", "puffer_fish"));


                switch (mobNameForFileAndMap.getfileName()) {
                    case "tropical_fish_large" -> mobNameForFileAndMap.setBoth("tropical_fish_b");
                    case "tropical_fish_small" -> mobNameForFileAndMap.setBoth("tropical_fish_a");
                    case "tropical_fish_large_pattern" -> mobNameForFileAndMap.setBoth("tropical_fish_pattern_b");
                    case "tropical_fish_small_pattern" -> mobNameForFileAndMap.setBoth("tropical_fish_pattern_a");

                    case "leash_knot" -> mobNameForFileAndMap.setBoth("lead_knot");

                    case "trader_llama" -> traderLlamaHappened = true;
                    case "llama" -> traderLlamaHappened = false;
                    case "llama_decor" -> mobNameForFileAndMap.setBoth(traderLlamaHappened ? "trader_llama_decor" : "llama_decor");
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
                    //case "parrot" -> mobNameForFileAndMap = "parrot";//todo check on shoulder parrot models they can technically be different

                    case "book" -> {
                        if (currentSpecifiedModelLoading.equals("enchanting_book")) {
                            mobNameForFileAndMap.setBoth("enchanting_book", "book");
                        } else/* if(currentSpecifiedModelLoading.equals("lectern_book"))*/ {
                            mobNameForFileAndMap.setBoth("lectern_book", "book");
                        }
                    }

                    case "chest" -> {
                        if (currentSpecifiedModelLoading.equals("ender_chest")) {
                            mobNameForFileAndMap.setBoth("ender_chest", "chest");
                        } else if (currentSpecifiedModelLoading.equals("trapped_chest")) {
                            mobNameForFileAndMap.setBoth("trapped_chest", "chest");
                        } else {
                            mobNameForFileAndMap.setBoth("chest", "chest");
                        }
                    }
                    case "double_chest_left" -> {
                        if (currentSpecifiedModelLoading.equals("trapped_chest")) {
                            mobNameForFileAndMap.setBoth("trapped_chest_large", "double_chest_left");
                        } else {
                            mobNameForFileAndMap.setBoth("chest_large", "double_chest_left");
                        }
                    }

                    case "double_chest_right" -> {
                        if (currentSpecifiedModelLoading.equals("trapped_chest")) {
                            mobNameForFileAndMap.setBoth("trapped_chest_large", "double_chest_right");
                        } else {
                            mobNameForFileAndMap.setBoth("chest_large", "double_chest_right");
                        }
                    }
                    case "shulker" -> {
                        if (currentSpecifiedModelLoading.equals("shulker_box")) {
                            mobNameForFileAndMap.setBoth("shulker_box");
                            currentSpecifiedModelLoading = "";
                        } else {
                            mobNameForFileAndMap.setBoth("shulker");
                        }

                    }

                    default -> {
                        //todo this if statement mess can likely be looked at for possible expanded features as each model can be different
                        if (mobNameForFileAndMap.getfileName().contains("/") && layer.getName().equals("main")) {
                            if (mobNameForFileAndMap.getfileName().startsWith("hanging_sign/")) {
                                mobNameForFileAndMap.setBoth("hanging_sign");
                            } else if (mobNameForFileAndMap.getfileName().startsWith("sign/")) {
                                mobNameForFileAndMap.setBoth("sign");
                            } else if (mobNameForFileAndMap.getfileName().startsWith("chest_boat/")) {
                                if (mobNameForFileAndMap.getfileName().startsWith("chest_boat/bamboo")) {
                                    mobNameForFileAndMap.setBoth("chest_raft");
                                } else {
                                    mobNameForFileAndMap.setBoth("chest_boat");
                                }
                            } else if (mobNameForFileAndMap.getfileName().startsWith("boat/")) {
                                if (mobNameForFileAndMap.getfileName().startsWith("boat/bamboo")) {
                                    mobNameForFileAndMap.setBoth("raft");
                                } else {
                                    mobNameForFileAndMap.setBoth("boat");
                                }
                            }
                        } //else {
//                        String countedName;
//                        if (COUNT_OF_MOB_NAME_ALREADY_SEEN.containsKey(mobNameForFileAndMap.getfileName())) {
//                            int amount = COUNT_OF_MOB_NAME_ALREADY_SEEN.getInt(mobNameForFileAndMap.getfileName());
//                            amount++;
//                            COUNT_OF_MOB_NAME_ALREADY_SEEN.put(mobNameForFileAndMap.getfileName(), amount);
//                            //System.out.println("higherCount: "+ mobNameForFileAndMap+amount);
//                            //String modelVariantAlias = mobNameForFileAndMap + '_' + (amount > 0 && amount < 27 ? String.valueOf((char) (amount + 'a' - 1)) : amount);
//                            countedName = mobNameForFileAndMap.getfileName() + '#' + amount;
//                        } else {
//                            EMFManager.getInstance().COUNT_OF_MOB_NAME_ALREADY_SEEN.put(mobNameForFileAndMap.getfileName(), 1);
//                            countedName = mobNameForFileAndMap.getfileName();//+'#'+1;
//                        }
//
//                        switch (countedName) {
//                            //todo ordering doesnt seem right
//                            case "shulker#2" -> mobNameForFileAndMap.setBoth("shulker");
//                            case "shulker" -> mobNameForFileAndMap.setBoth("shulker_box");
//                            default ->{}
//
//                        }
                        //}
                        //System.out.println("DEBUG modelName result: "+countedName + " -> "+mobNameForFileAndMap);
                    }
                }
            }
            //if file name isn't valid for identifiers
            //uses the static Identifier method to be influenced by other mods affecting resource name validity
            if (!isIdentifierPathValid(mobNameForFileAndMap.getfileName() + ".jem")) {
                String newValidPath = mobNameForFileAndMap.getfileName().replaceAll("[^a-z0-9/_.-]", "_");
                mobNameForFileAndMap.setBoth(newValidPath, mobNameForFileAndMap.getMapId());
            }

            if (printing) System.out.println(" > EMF try to find a model for: " + mobNameForFileAndMap);


            ///jem name is final and correct from here

            //if (EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap).isEmpty()) {
            //construct simple map for modded or unknown entities
            Map<String, String> optifinePartNameMap = EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap.getMapId(), root);
            //}


            if (printing) System.out.println(" >> EMF trying to find: optifine/cem/" + mobNameForFileAndMap + ".jem");
            String jemName = /*"optifine/cem/" +*/ mobNameForFileAndMap + ".jem";
            CemDirectoryApplier variantDirectoryApplier = getResourceCemDirectoryApplierOrNull(mobNameForFileAndMap + ".properties", mobNameForFileAndMap.getfileName());// (MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("optifine/cem/" + mobNameForFileAndMap + ".properties")).isPresent());

            EMFJemData jemData = getJemData(jemName, mobNameForFileAndMap);
            if (jemData != null || variantDirectoryApplier != null) {
                //we do indeed need custom models

                //specification for the optifine map
                // only used for tadpole head parts currently as optifine actually uses the root as the body
                Set<String> optifinePartNames = new HashSet<>();
                optifinePartNameMap.forEach((optifine, vanilla) -> {
                    if (!optifine.equals("EMPTY")) {
                        optifinePartNames.add(vanilla);
                    }
                });

                EMFModelPartRoot emfRoot = new EMFModelPartRoot(mobNameForFileAndMap, variantDirectoryApplier, root, optifinePartNames, new HashMap<>());
                if (jemData != null) {
                    emfRoot.addVariantOfJem(jemData, 1);
                    emfRoot.setVariantStateTo(1);
                    setupAnimationsFromJemToModel(jemData, emfRoot, 1);
                }

                lastCreatedRootModelPart = emfRoot;
                return emfRoot;
            }


            if (printing) System.out.println(" > Vanilla model used for: " + mobNameForFileAndMap);
            return root;
        }catch (Exception e) {
            EMFUtils.EMFModWarn("default model returned for "+ layer.toString() + " due to exception: " + e.getMessage());
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

    private void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPartRoot emfRootPart, int variantNum) {
        ///////SETUP ANIMATION EXECUTABLES////////////////

        boolean printing = EMFConfig.getConfig().logModelCreationData;

        Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = new Object2ObjectOpenHashMap<>();
        allPartsBySingleAndFullHeirachicalId.put("EMF_root", emfRootPart);
        allPartsBySingleAndFullHeirachicalId.putAll(emfRootPart.getAllChildPartsAsAnimationMap("", variantNum, EMFOptiFinePartNameMappings.getMapOf(emfRootPart.modelName.getMapId(), null)));

        Object2ObjectLinkedOpenHashMap<String, Object2ObjectLinkedOpenHashMap<String, EMFAnimation>> emfAnimationsByPartName = new Object2ObjectLinkedOpenHashMap<>();


        if (printing) {
            System.out.println(" > finalAnimationsForModel =");
            jemData.allTopLevelAnimationsByVanillaPartName.forEach((part, anims) -> anims.forEach((key, expression) -> System.out.println(" >> " + key + " = " + expression)));
        }
        jemData.allTopLevelAnimationsByVanillaPartName.forEach((part, anims) -> {
            Object2ObjectLinkedOpenHashMap<String, EMFAnimation> thisPartAnims = new Object2ObjectLinkedOpenHashMap<>();
            anims.forEach((animKey, animationExpression) -> {
                if (EMFConfig.getConfig().logModelCreationData)
                    EMFUtils.EMFModMessage("parsing animation value: [" + animKey + "]");
                String modelId = animKey.split("\\.")[0];
                String modelVariable = animKey.split("\\.")[1];

                EMFModelOrRenderVariable thisVariable = EMFModelOrRenderVariable.get(modelVariable);
                if (thisVariable == null) thisVariable = EMFModelOrRenderVariable.getRenderVariable(animKey);

                EMFModelPart thisPart = "render".equals(modelId) ? null : getModelFromHierarchichalId(modelId, allPartsBySingleAndFullHeirachicalId);
                thisPartAnims.put(animKey,
                        new EMFAnimation(
                                thisPart,
                                thisVariable,
                                animKey,
                                animationExpression,
                                jemData.fileName//, variableSuppliers
                        )
                );
            });
            emfAnimationsByPartName.put(part, thisPartAnims);
        });
        //LinkedList<EMFAnimation> orderedAnimations = new LinkedList<>();
        //System.out.println("> anims: " + emfAnimationsByPartName);
        isAnimationValidationPhase = true;
        emfAnimationsByPartName.forEach((part, animMap) -> {

            Iterator<Map.Entry<String, EMFAnimation>> animMapIterate = animMap.entrySet().iterator();
            while (animMapIterate.hasNext()) {
                Map.Entry<String, EMFAnimation> anim = animMapIterate.next();
                if (anim.getValue() != null) {
                    anim.getValue().initExpression(animMap, allPartsBySingleAndFullHeirachicalId);
                    if (!anim.getValue().isValid()) {
                        EMFUtils.EMFModWarn("animations was invalid: " + anim.getValue().animKey + " = " + anim.getValue().expressionString);
                        animMapIterate.remove();
                    }
                } else {
                    animMapIterate.remove();
                }
            }

//            animMap.forEach((key, anim) -> {
//                //System.out.println(">> anim key: " + key);
//                if (anim != null) {
//                    //System.out.println(">> anim: " + anim.expressionString);
//                    anim.initExpression(animMap, allPartsBySingleAndFullHeirachicalId);
//                    //System.out.println(">>> valid: " + anim.isValid());
//                    if (anim.isValid())
//                        orderedAnimations.add(anim);
//                    else
//                        EMFUtils.EMFModWarn("animations was invalid: " + anim.animKey + " = " + anim.expressionString);
//                }
//            });
        });
        isAnimationValidationPhase = false;

        //EMFAnimationExecutor executor = new EMFAnimationExecutor(variableSuppliers, orderedAnimations);

//        if(emfRootPart.modelName.getMapId().contains("axolotl")) emfAnimationsByPartName.forEach((k,va)-> va.forEach((k2,v)-> System.out.println("axolotl>>> "+v.animKey+"="+v.expressionString+" ### "+(v.partToApplyTo == null ? "null" :v.partToApplyTo.toString()))));
        emfRootPart.receiveAnimations(variantNum, emfAnimationsByPartName);

        //cache_EntityNameToAnimationExecutable.put(jemData.mobName, executor);
        ///////////////////////////
    }

    public void doVariantCheckFor(EMFModelPartRoot cannonRoot) {
        EMFEntity entity = EMFAnimationHelper.getEMFEntity();
        //String mobName = getTypeName(entity);
        //cache_JemNameDoesHaveVariants.getBoolean(mobName)
        if (entity != null
                && cannonRoot.variantDirectoryApplier != null
                && cache_UUIDDoUpdating.getBoolean(entity.getUuid())
                && ETFApi.getETFConfigObject().textureUpdateFrequency_V2 != ETFConfig.UpdateFrequency.Never
        ) {
            String mobName = cannonRoot.modelName.getfileName();
            UUIDAndMobTypeKey key = new UUIDAndMobTypeKey(entity.getUuid(), entity.getTypeString());

            long randomizer = ETFApi.getETFConfigObject().textureUpdateFrequency_V2.getDelay() * 20L;
            if (System.currentTimeMillis() % randomizer == Math.abs(entity.getUuid().hashCode()) % randomizer) {
                //if (cache_UUIDAndTypeToLastVariantCheckTime.getLong(key) + 1500 < System.currentTimeMillis()) {

                if (cannonRoot.variantTester == null) {
                    Identifier propertyID = new Identifier(cannonRoot.variantDirectoryApplier.getThisDirectoryOfFilename(mobName + ".properties"));
                    if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
                        cannonRoot.variantTester = ETFApi.readRandomPropertiesFileAndReturnTestingObject3(propertyID, "models");
                    } else {
                        EMFUtils.EMFModWarn("no property" + propertyID);
                        //cache_JemNameDoesHaveVariants.put(mobName, false);
                        cannonRoot.variantDirectoryApplier = null;
                        return;
                    }
                }
                ETFApi.ETFRandomTexturePropertyInstance emfProperty = cannonRoot.variantTester;
                if (emfProperty != null) {
                    int suffix;
                    if (entity.entity() == null) {
                        suffix = emfProperty.getSuffixForBlockEntity(entity.getBlockEntity(), entity.getUuid(), cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
                    } else {
                        suffix = emfProperty.getSuffixForEntity(entity.entity(), cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
                    }

                    //EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                    if (suffix > 1) { // ignore 0 & 1
                        //System.out.println(" > apply model variant: "+suffix +", to "+mobName);
                        if (!cannonRoot.allKnownStateVariants.containsKey(suffix)) {
                            String jemName = cannonRoot.variantDirectoryApplier.getThisDirectoryOfFilename(mobName + suffix + ".jem");
                            if (EMFConfig.getConfig().logModelCreationData)
                                System.out.println(" >> first time load of : " + jemName);
                            EMFJemData jemData = getJemDataWithDirectory(jemName, cannonRoot.modelName);
                            if (jemData != null) {
                                cannonRoot.addVariantOfJem(jemData, suffix);
                                cannonRoot.setVariantStateTo(suffix);
                                setupAnimationsFromJemToModel(jemData, cannonRoot, suffix);
//                                ModelPart vanillaRoot = cannonRoot.vanillaRoot; //cache_JemNameToVanillaModelRoot.get(mobName);
//                                if (vanillaRoot != null) {
//                                    EMFModelPartMutable variantRoot = getEMFRootModelFromJem(jemData, vanillaRoot, suffix);
//                                    cannonRoot.mergePartVariant(suffix, variantRoot);
//                                    setupAnimationsFromJemToModel(jemData, cannonRoot,suffix);
//                                }
                            } else {
                                System.out.println("invalid jem: " + jemName);
                            }
                        }
                        cannonRoot.setVariantStateTo(suffix);
                        cache_UUIDAndTypeToCurrentVariantInt.put(key, suffix);
                    } else {
                        cannonRoot.setVariantStateTo(1);
                        cache_UUIDAndTypeToCurrentVariantInt.put(key, 1);
                    }
                } else {
                    cannonRoot.variantDirectoryApplier = null;
                    cannonRoot.setVariantStateTo(1);
                }
                // cache_UUIDAndTypeToLastVariantCheckTime.put(key, System.currentTimeMillis());
            } else {
                //EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                cannonRoot.setVariantStateTo(cache_UUIDAndTypeToCurrentVariantInt.getInt(key));
            }
        } else {
            cannonRoot.setVariantStateTo(1);
        }
    }


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

    private record UUIDAndMobTypeKey(UUID uuid, String entityType) {
    }


}
