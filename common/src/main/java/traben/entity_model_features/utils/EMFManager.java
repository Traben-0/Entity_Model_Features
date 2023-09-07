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
import net.minecraft.entity.EntityType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.EMFModelPart;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.models.animation.EMFDefaultModelVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;


public class EMFManager {//singleton for data holding and resetting needs


//    private static final Object2ObjectOpenHashMap<String, String> map_MultiMobVariantMap = new Object2ObjectOpenHashMap<>() {{
////        put("cat_b", "cat_collar");
////        put("wither_skeleton_b", "wither_skeleton_inner_armor");
////        put("wither_skeleton_c", "wither_skeleton_outer_armor");
////        put("zombie_b", "zombie_inner_armor");
////        put("zombie_c", "zombie_outer_armor");
////        put("skeleton_b", "skeleton_inner_armor");
////        put("skeleton_c", "skeleton_outer_armor");
////        put("zombified_piglin_b", "zombified_piglin_inner_armor");
////        put("zombified_piglin_c", "zombified_piglin_outer_armor");
////        put("piglin_b", "piglin_inner_armor");
////        put("piglin_c", "piglin_outer_armor");
////        put("piglin_brute_b", "piglin_brute_inner_armor");
////        put("piglin_brute_c", "piglin_brute_outer_armor");
////        put("armor_stand_b", "armor_stand_inner_armor");
////        put("armor_stand_c", "armor_stand_outer_armor");
////        put("zombie_villager_b", "zombie_villager_inner_armor");
////        put("zombie_villager_c", "zombie_villager_outer_armor");
////        put("giant_b", "giant_inner_armor");
////        put("giant_c", "giant_outer_armor");
////        put("player_b", "player_inner_armor");
////        put("player_c", "player_outer_armor");
////        put("drowned_b", "drowned_inner_armor");
////        put("drowned_c", "drowned_outer_armor");
////        put("drowned_d", "drowned_outer");
////        put("stray_b", "stray_inner_armor");
////        put("stray_c", "stray_outer_armor");
////        put("stray_d", "stray_outer");
//        put("shulker_b", "shulker_box");//todo this entire map appears redundant now, follow up!
////        put("husk_b", "husk_inner_armor");
////        put("husk_c", "husk_outer_armor");
////        put("player_slim_b", "player_slim_inner_armor");
////        put("player_slim_c", "player_slim_outer_armor");
////        put("creeper_b", "creeper_charge");
////        put("pig_b", "pig_saddle");
////        put("strider_b", "strider_saddle");
////        put("sheep_b", "sheep_wool");
////        put("slime_b", "slime_outer");
////
////        put("parrot_b", "parrot");//todo shoulder parrots
////        put("parrot_c", "parrot");//todo
//
//    }};

    public static EMFModelPartRoot lastCreatedRootModelPart = null;
    public long entityRenderCount = 0;

    public final boolean IS_PHYSICS_MOD_INSTALLED;
    private static EMFManager self = null;
    private final Object2ObjectOpenHashMap<String, EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<>();

    private final Object2BooleanOpenHashMap<UUID> cache_UUIDDoUpdating = new Object2BooleanOpenHashMap<>() {{
        defaultReturnValue(true);
    }};
    private final Object2IntOpenHashMap<UUIDAndMobTypeKey> cache_UUIDAndTypeToCurrentVariantInt = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(1);
    }};



    public final boolean IS_IRIS_INSTALLED;
    private EMFManager() {
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

    public static ModelPart traverseRootForChildOrNull(ModelPart root, String nameOfModelToFind) {
        if (root.hasChild(nameOfModelToFind))
            return root.getChild(nameOfModelToFind);
        for (ModelPart part :
                ((ModelPartAccessor) root).getChildren().values()) {
            ModelPart found = traverseRootForChildOrNull(part, nameOfModelToFind);
            if (found != null)
                return found;
        }
        return null;
    }

//    public static String getTypeName(Entity entity) {
//        String forReturn = Registries.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:", "");
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
//            forReturn =  (fish.getVariant().getSize() == TropicalFishEntity.Size.LARGE ? "tropical_fish_b" : "tropical_fish_a");
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
        EMFJemData emfJemData = getJemDataWithDirectory("emf/cem/"+jemFileName,mobModelIDInfo);
        if (emfJemData != null) return emfJemData;
        emfJemData = getJemDataWithDirectory("emf/cem/"+mobModelIDInfo+"/"+jemFileName,mobModelIDInfo);
        if (emfJemData != null) return emfJemData;

        //try read optifine jems
        emfJemData = getJemDataWithDirectory("optifine/cem/"+jemFileName,mobModelIDInfo);
        if (emfJemData != null) return emfJemData;
        emfJemData = getJemDataWithDirectory("optifine/cem/"+mobModelIDInfo+"/"+jemFileName,mobModelIDInfo);
        return emfJemData;

    }

    public interface CemDirectoryApplier{
        String getThisDirectoryOfFilename(String fileName);

        static CemDirectoryApplier getEMF(){
            return (fileName)-> "emf/cem/"+fileName;
        }
        static CemDirectoryApplier getEMF_Mob(String mobname){
            return (fileName)-> "emf/cem/"+mobname+"/"+fileName;
        }
        static CemDirectoryApplier getCEM(){
            return (fileName)-> "optifine/cem/"+fileName;
        }
        static CemDirectoryApplier getCem_Mob(String mobName){
            return (fileName)-> "optifine/cem/"+mobName+"/"+fileName;
        }
    }



    @Nullable
    public static CemDirectoryApplier getResourceCemDirectoryApplierOrNull(String inCemPathResource, String rawMobName) {
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();
        //try emf folder
        if(resources.getResource(new Identifier("emf/cem/"+inCemPathResource)).isPresent())
            return CemDirectoryApplier.getEMF();
        if(resources.getResource(new Identifier("emf/cem/"+rawMobName+"/"+inCemPathResource)).isPresent())
            return CemDirectoryApplier.getEMF_Mob(rawMobName);
        if(resources.getResource(new Identifier("optifine/cem/"+inCemPathResource)).isPresent())
            return CemDirectoryApplier.getCEM();
        if(resources.getResource(new Identifier("optifine/cem/"+rawMobName+"/"+inCemPathResource)).isPresent())
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
                if (EMFConfig.getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMFModMessage(".jem read failed " + pathOfJem + " does not exist", false);
                return null;
            }
            if (EMFConfig.getConfig().printModelCreationInfoToLog)
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
            jem.sendFileName(pathOfJem,mobModelIDInfo);
            jem.prepare();
            if(mobModelIDInfo.areBothSame())
                EMFManager.getInstance().cache_JemDataByFileName.put(pathOfJem, jem);
            return jem;
            //}
        } catch (InvalidIdentifierException | FileNotFoundException e) {
            if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFUtils.EMFModMessage(".jem failed to load " + e, false);
        } catch (Exception e) {
            EMFUtils.EMFModMessage(".jem failed to load " + e, false);
            e.printStackTrace();
        }
        return null;
    }


    private final Object2IntOpenHashMap<String> COUNT_OF_MOB_NAME_ALREADY_SEEN = new Object2IntOpenHashMap<>();
    private boolean traderLlamaHappened = false;
    public ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root) {

        EMFManager.lastCreatedRootModelPart = null;

        boolean printing =  (EMFConfig.getConfig().printModelCreationInfoToLog);
//        if (layer == EntityModelLayers.SPIDER ||layer == EntityModelLayers.IRON_GOLEM ||layer == EntityModelLayers.ZOMBIE || layer == EntityModelLayers.COW || layer == EntityModelLayers.SHEEP || layer == EntityModelLayers.VILLAGER) {
//            System.out.println("ran zomb and sheep");
        OptifineMobNameForFileAndEMFMapId mobNameForFileAndMap = new OptifineMobNameForFileAndEMFMapId(layer.getId().getPath());


        if(!"main".equals(layer.getName())){
            mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName() + "_" + layer.getName());
        }

        //add simple modded check
        if(!"minecraft".equals(layer.getId().getNamespace())){
            mobNameForFileAndMap.setBoth("modded/"+layer.getId().getNamespace()+"/"+mobNameForFileAndMap);
        }else {
            //vanilla model
            if (mobNameForFileAndMap.getfileName().contains("pufferfish"))
                mobNameForFileAndMap.setBoth(mobNameForFileAndMap.getfileName().replace("pufferfish", "puffer_fish"));


            switch (mobNameForFileAndMap.getfileName()) {
                case "tropical_fish_large" -> mobNameForFileAndMap.setBoth( "tropical_fish_b");
                case "tropical_fish_small" -> mobNameForFileAndMap.setBoth( "tropical_fish_a");
                case "tropical_fish_large_pattern" -> mobNameForFileAndMap.setBoth( "tropical_fish_pattern_b");
                case "tropical_fish_small_pattern" -> mobNameForFileAndMap.setBoth( "tropical_fish_pattern_a");

                case "trader_llama" -> traderLlamaHappened = true;
                case "llama" -> traderLlamaHappened = false;
                case "llama_decor" -> mobNameForFileAndMap.setBoth( traderLlamaHappened ? "trader_llama_decor" : "llama_decor");
                case "ender_dragon" -> mobNameForFileAndMap.setBoth( "dragon");
                case "dragon_skull" -> mobNameForFileAndMap.setBoth( "head_dragon");
                case "player_head" -> mobNameForFileAndMap.setBoth( "head_player");
                case "skeleton_skull" -> mobNameForFileAndMap.setBoth( "head_skeleton");
                case "wither_skeleton_skull" -> mobNameForFileAndMap.setBoth( "head_wither_skeleton");
                case "zombie_head" -> mobNameForFileAndMap.setBoth( "head_zombie");
                case "creeper_head" -> mobNameForFileAndMap.setBoth( "head_creeper");
                case "piglin_head" -> mobNameForFileAndMap.setBoth( "head_piglin");
                case "creeper_armor" -> mobNameForFileAndMap.setBoth( "creeper_charge");
                case "sheep_fur" -> mobNameForFileAndMap.setBoth( "sheep_wool");
                case "bed_head" -> mobNameForFileAndMap.setBoth( "bed","bed_head");
                case "bed_foot" -> mobNameForFileAndMap.setBoth( "bed","bed_foot");
                case "conduit_cage" -> mobNameForFileAndMap.setBoth( "conduit","conduit_cage");
                case "conduit_eye" -> mobNameForFileAndMap.setBoth( "conduit","conduit_eye");
                case "conduit_shell" -> mobNameForFileAndMap.setBoth( "conduit","conduit_shell");
                case "conduit_wind" -> mobNameForFileAndMap.setBoth( "conduit","conduit_wind");
                case "decorated_pot_base" -> mobNameForFileAndMap.setBoth( "decorated_pot","decorated_pot_base");
                case "decorated_pot_sides" -> mobNameForFileAndMap.setBoth( "decorated_pot","decorated_pot_sides");
                //case "parrot" -> mobNameForFileAndMap = "parrot";//todo check on shoulder parrot models they can technically be different



                default -> {
                    String countedName;
                    if (COUNT_OF_MOB_NAME_ALREADY_SEEN.containsKey(mobNameForFileAndMap.getfileName())) {
                        int amount = COUNT_OF_MOB_NAME_ALREADY_SEEN.getInt(mobNameForFileAndMap.getfileName());
                        amount++;
                        COUNT_OF_MOB_NAME_ALREADY_SEEN.put(mobNameForFileAndMap.getfileName(), amount);
                        //System.out.println("higherCount: "+ mobNameForFileAndMap+amount);
                        //String modelVariantAlias = mobNameForFileAndMap + '_' + (amount > 0 && amount < 27 ? String.valueOf((char) (amount + 'a' - 1)) : amount);
                        countedName = mobNameForFileAndMap.getfileName()+'#'+amount;
                    } else {
                        EMFManager.getInstance().COUNT_OF_MOB_NAME_ALREADY_SEEN.put(mobNameForFileAndMap.getfileName(), 1);
                        countedName = mobNameForFileAndMap.getfileName();//+'#'+1;
                    }
                    switch (countedName) {
                        case "shulker#2" -> mobNameForFileAndMap.setBoth("shulker");
                        case "shulker" -> mobNameForFileAndMap.setBoth("shulker_box");
                        case "chest#3"-> mobNameForFileAndMap.setBoth("trapped_chest","chest");
                        case "double_chest_left#3"->mobNameForFileAndMap.setBoth("trapped_chest_large","double_chest_left");
                        case "double_chest_right#3"->mobNameForFileAndMap.setBoth("trapped_chest_large","double_chest_right");
                        case "chest#2"-> mobNameForFileAndMap.setBoth("chest","chest");
                        case "double_chest_left#2"-> mobNameForFileAndMap.setBoth("chest_large","double_chest_left");
                        case "double_chest_right#2"-> mobNameForFileAndMap.setBoth("chest_large","double_chest_right");
                        case "chest"-> mobNameForFileAndMap.setBoth("ender_chest","chest");
                        case "double_chest_left"->mobNameForFileAndMap.setBoth("ender_chest_large","double_chest_left");//???
                        case "double_chest_right"->mobNameForFileAndMap.setBoth("ender_chest_large","double_chest_right");//???
                        case "book" -> mobNameForFileAndMap.setBoth("enchanting_book","book");
                        case "book#2" -> mobNameForFileAndMap.setBoth("lectern_book","book");
                        default -> {
                            //do nothing currently
                        }

                    }
                    //System.out.println("DEBUG modelName result: "+countedName + " -> "+mobNameForFileAndMap);
                }
            }
        }
        if (printing) System.out.println(" > EMF try to find a model for: " + mobNameForFileAndMap);


        ///jem name is final and correct from here

        //if (EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap).isEmpty()) {
            //construct simple map for modded or unknown entities
        Map<String, String> optifinePartNameMap = EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap.getMapId(),root);
        //}


        if (printing) System.out.println(" >> EMF trying to find: optifine/cem/" + mobNameForFileAndMap + ".jem");
        String jemName = /*"optifine/cem/" +*/ mobNameForFileAndMap + ".jem";
        EMFJemData jemData = getJemData(jemName,mobNameForFileAndMap);
        CemDirectoryApplier variantDirectoryApplier = getResourceCemDirectoryApplierOrNull(mobNameForFileAndMap + ".properties",mobNameForFileAndMap.getfileName());// (MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("optifine/cem/" + mobNameForFileAndMap + ".properties")).isPresent());

        if(jemData!=null || variantDirectoryApplier != null){
            //we do indeed need custom models

            //specification for the optifine map
            // only used for tadpole head parts currently as optifine actually uses the root as the body
            Set<String> optifinePartNames = new HashSet<>();
            optifinePartNameMap.forEach((optifine,vanilla)->{
                if(!optifine.equals("EMPTY")){
                    optifinePartNames.add(vanilla);
                }
            });

            EMFModelPartRoot emfRoot = new EMFModelPartRoot(mobNameForFileAndMap, variantDirectoryApplier,root,optifinePartNames, new HashMap<>());
            if(jemData!= null) {
                emfRoot.addVariantOfJem(jemData, 1);
                emfRoot.setVariantStateTo(1);
                setupAnimationsFromJemToModel(jemData,emfRoot,1);
            }

            lastCreatedRootModelPart = emfRoot;
            return emfRoot;
        }



//        if (jemData != null) {
////            if (!EMFOptiFinePartNameMappings.getMapOf(mobNameForFileAndMap).isEmpty()) {
//                EMFModelPartMutable part = getEMFRootModelFromJem(jemData, root);
//
//                //cache_JemNameToCannonModelRoot.put(mobNameForFileAndMap, part);
//
//
//                //cache_JemNameToVanillaModelRoot.put(mobNameForFileAndMap, root);
//            //todo alternate folders
//            //cache_JemNameDoesHaveVariants.put(mobNameForFileAndMap, hasVariants);
//
//
//                part.setPartAsTopLevelRoot(mobNameForFileAndMap,jemData,variantDirectoryApplier,root);
//                //tie into emf model discovery
//
//                return part;
////            } else {
////                //not a cem mob
////                if (printing) System.out.println(" >> no EMF mapping found");//todo modded mob handling
////                EMFUtils.EMFModWarn("EMF Beta does not have the code to read unknown model [" + jemName+ "] yet, soon though." );
////            }
//        } else {
//            //no mob .jem
//
//            if (printing) System.out.println(" >> EMF mob does not have a .jem file");
//        }

        if (printing) System.out.println(" > Vanilla model used for: " + mobNameForFileAndMap);
        return root;
    }

//    private EMFModelPartMutable getEMFRootModelFromJem(EMFJemData jemData, ModelPart vanillaRoot) {
//        EMFModelPartMutable part = getEMFRootModelFromJem(jemData, vanillaRoot, 1);
//        setupAnimationsFromJemToModel(jemData, part, 1);
//        return part;
//    }

//    private EMFModelPartMutable getEMFRootModelFromJem(EMFJemData jemData, ModelPart vanillaRoot, int variantNumber) {
//        Map<String, ModelPart> rootChildren = new HashMap<>();
//
//        boolean printing = EMFConfig.getConfig().printModelCreationInfoToLog;
//
//        for (EMFPartData partData :
//                jemData.models) {
//            if (partData != null && partData.part != null) {
//
//                ModelPart oldPart = traverseRootForChildOrNull(vanillaRoot, partData.part);
////                ModelPart oldPart = vanillaRoot.hasChild("root") ?
////                        ((ModelPartAccessor) vanillaRoot.getChild("root")).getChildren().getOrDefault(partData.part, null)
////                        :
////                        ((ModelPartAccessor) vanillaRoot).getChildren().getOrDefault(partData.part, null);
//
//                EMFModelPartMutable newPart = new EMFModelPartMutable(partData, variantNumber);
//                if (oldPart != null){
//                    newPart.applyDefaultModelRotates(oldPart.getDefaultTransform());
//                    iterateChildTransformCopy(newPart,oldPart);
//                }
//                if (printing) System.out.println(" >>> EMF part made: " + partData.toString(false));
////                if ("piglin".equals(jemData.mobName)) {
////                    System.out.println(" >>>>>>>>> piglin part made: " + partData.toString(false));
////                }
//                rootChildren.put(partData.part, newPart);
//
//            } else {
//                //part is not mapped to a vanilla part
//                System.out.println("no part definition");
//            }
//        }
//        //have iterated over all parts in jem and made them
//
//
//        EMFModelPartMutable emfRootModelPart = new EMFModelPartMutable( rootChildren, variantNumber, jemData);
//        //try
//        //todo pretty sure we must match root transforms because of fucking frogs, maybe?
//        //emfRootModelPart.pivotY = 24;
//        //todo check all were mapped correctly before return
//        if (printing) System.out.println(" > EMF model returned");
//
//        //emfRootModelPart.assertChildrenAndCuboids();
//
//
//
//        // check for if root is expected below the top level modelpart
//        // as in some single part entity models
//        if (vanillaRoot.hasChild("root")){
//            if(!emfRootModelPart.hasChild("root")) {
//                ModelPart subRoot = vanillaRoot.getChild("root");
//                if (subRoot.pivotX != 0 ||
//                        subRoot.pivotY != 0 ||
//                        subRoot.pivotZ != 0 ||
//                        subRoot.pitch != 0 ||
//                        subRoot.yaw != 0 ||
//                        subRoot.roll != 0 ||
//                        subRoot.xScale != 0 ||
//                        subRoot.yScale != 0 ||
//                        subRoot.zScale != 0
//
//                ) {
//                    //this covers things like frogs who pivot their root for some reason
//                    emfRootModelPart.setTransform(subRoot.getTransform());
//                    emfRootModelPart.setDefaultTransform(subRoot.getDefaultTransform());
//                }
//
//                emfRootModelPart = new EMFModelPartMutable( Map.of("root", emfRootModelPart), variantNumber, jemData);
//            }
//        }else if (emfRootModelPart.hasChild("root")){
//            //should only be tadpoles
//            emfRootModelPart = (EMFModelPartMutable) emfRootModelPart.getChild("root");
//        }
//
//
//
//        if(EMFConfig.getConfig().attemptToCopyVanillaModelIntoMissingModelPart)
//            emfRootModelPart.mergeInVanillaWhereRequired(vanillaRoot);
//        return emfRootModelPart;
//    }

//    private void iterateChildTransformCopy(EMFModelPartMutable newPart, ModelPart oldPart){
//        for (String emfChildId:
//                newPart.getChildrenEMF().keySet()) {
//            if(oldPart.hasChild(emfChildId)){
//                EMFModelPartMutable newNewPart = ((EMFModelPartMutable)newPart.getChildrenEMF().get(emfChildId));
//                ModelPart oldOldPart =oldPart.getChild(emfChildId);
//                newNewPart.applyDefaultModelRotates(oldOldPart.getDefaultTransform());
//                iterateChildTransformCopy(newNewPart,oldOldPart);
//            }
//        }
//    }


    private void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPartRoot emfRootPart,int variantNum) {
        ///////SETUP ANIMATION EXECUTABLES////////////////

        boolean printing =   EMFConfig.getConfig().printModelCreationInfoToLog;

        Object2ObjectOpenHashMap<String, EMFModelPart> allPartsBySingleAndFullHeirachicalId = new Object2ObjectOpenHashMap<>();
        allPartsBySingleAndFullHeirachicalId.put("root", emfRootPart);
        allPartsBySingleAndFullHeirachicalId.putAll(emfRootPart.getAllChildPartsAsAnimationMap("",variantNum));

        Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimations = new Object2ObjectLinkedOpenHashMap<>();


        if (printing) {
            System.out.println(" > finalAnimationsForModel =");
            jemData.finalAnimationsForModel.forEach((key, expression) -> System.out.println(" >> " + key + " = " + expression));
        }
        jemData.finalAnimationsForModel.forEach((animKey, animationExpression) -> {

            if (EMFConfig.getConfig().printModelCreationInfoToLog)
                EMFUtils.EMFModMessage("parsing animation value: [" + animKey + "]");
            String modelId = animKey.split("\\.")[0];
            String modelVariable = animKey.split("\\.")[1];

            EMFDefaultModelVariable thisVariable = EMFDefaultModelVariable.get(modelVariable);

            EMFModelPart thisPart = getModelFromHierarchichalId(modelId,allPartsBySingleAndFullHeirachicalId);
            EMFAnimation thisCalculator;

            if (thisPart != null) {
                thisCalculator =
                        new EMFAnimation(
                                thisPart,
                                thisVariable,
                                animKey,
                                animationExpression,
                                jemData.fileName//, variableSuppliers
                        );
            } else {
                //not a custom model or vanilla must be a custom variable
                thisCalculator = new EMFAnimation(
                        null,
                        null,
                        animKey,
                        animationExpression,
                        jemData.fileName//, variableSuppliers
                );
            }
            emfAnimations.put(animKey, thisCalculator);
        });
        LinkedList<EMFAnimation> orderedAnimations = new LinkedList<>();
        //System.out.println("> anims: " + emfAnimations);
        isAnimationValidationPhase = true;
        emfAnimations.forEach((key, anim) -> {
            //System.out.println(">> anim key: " + key);
            if (anim != null) {
                //System.out.println(">> anim: " + anim.expressionString);
                anim.initExpression(emfAnimations, allPartsBySingleAndFullHeirachicalId);
                //System.out.println(">>> valid: " + anim.isValid());
                if (anim.isValid())
                    orderedAnimations.add(anim);
                else
                    EMFUtils.EMFModWarn("animations was invalid: " + anim.animKey + " = " + anim.expressionString);
            }
        });
        isAnimationValidationPhase = false;

        //EMFAnimationExecutor executor = new EMFAnimationExecutor(variableSuppliers, orderedAnimations);

        emfRootPart.receiveAnimations(variantNum,orderedAnimations);

        //cache_EntityNameToAnimationExecutable.put(jemData.mobName, executor);
        ///////////////////////////
    }

    public boolean isAnimationValidationPhase = false;


    public static EMFModelPart getModelFromHierarchichalId(String hierarchId, Map<String, EMFModelPart> map){
        if(hierarchId == null || hierarchId.isBlank()) return null;
        if(!hierarchId.contains(":")) {
            EMFModelPart part = map.get(hierarchId);
            if(part == null)
                return map.get("EMF_"+hierarchId);
            return part;
        }
        for (Map.Entry<String,EMFModelPart> entry:
             map.entrySet()) {
            if(entry.getKey().equals(hierarchId) || entry.getKey().equals("EMF_"+hierarchId)
                    || (entry.getKey().endsWith(":"+hierarchId))  || (entry.getKey().endsWith(":EMF_"+hierarchId)))
                return entry.getValue();
            boolean anyMissing = false;
            String last = "";
            for (String str:
                 hierarchId.split(":")) {
                last = str;
                if(!(entry.getKey().contains(str) || entry.getKey().contains("EMF_"+str)) ){
                    anyMissing = true;
                    break;
                }
            }
            if(!anyMissing && entry.getKey().endsWith(last)) return entry.getValue();
        }
        //all possible occurances should be accounted for above must be null
        System.out.println("NULL animation hierachy id result of: "+hierarchId +"\n in "+map);
        return null;
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
            UUIDAndMobTypeKey key = new UUIDAndMobTypeKey(entity.getUuid(), entity.getType());

            long randomizer = ETFApi.getETFConfigObject().textureUpdateFrequency_V2.getDelay() * 20L;
            if (System.currentTimeMillis() % randomizer == Math.abs(entity.getUuid().hashCode()) % randomizer){
            //if (cache_UUIDAndTypeToLastVariantCheckTime.getLong(key) + 1500 < System.currentTimeMillis()) {

                if (cannonRoot.variantTester==null) {
                    Identifier propertyID = new Identifier(cannonRoot.variantDirectoryApplier.getThisDirectoryOfFilename(mobName + ".properties"));
                    if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
                        cannonRoot.variantTester= ETFApi.readRandomPropertiesFileAndReturnTestingObject2(propertyID, "models");
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
                    if(entity.entity() == null){
                       suffix = emfProperty.getSuffixForBlockEntity(entity.getBlockEntity(), entity.getUuid(), cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
                    }else{
                        suffix = emfProperty.getSuffixForEntity(entity.entity(), cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
                    }

                    //EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                    if (suffix > 1) { // ignore 0 & 1
                        //System.out.println(" > apply model variant: "+suffix +", to "+mobName);
                        if (!cannonRoot.allKnownStateVariants.containsKey(suffix)) {
                            String jemName =cannonRoot.variantDirectoryApplier.getThisDirectoryOfFilename(mobName + suffix + ".jem");
                            System.out.println(" >> first time load of : " + jemName);
                            EMFJemData jemData = getJemDataWithDirectory(jemName,cannonRoot.modelName);
                            if (jemData != null) {
                                cannonRoot.addVariantOfJem(jemData, suffix);
                                cannonRoot.setVariantStateTo(suffix);
                                setupAnimationsFromJemToModel(jemData,cannonRoot,suffix);
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
                }else{
                    cannonRoot.variantDirectoryApplier = null;
                    cannonRoot.setVariantStateTo(1);
                }
               // cache_UUIDAndTypeToLastVariantCheckTime.put(key, System.currentTimeMillis());
            }else{
                //EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                cannonRoot.setVariantStateTo(cache_UUIDAndTypeToCurrentVariantInt.getInt(key));
            }
        }else{
            cannonRoot.setVariantStateTo(1);
        }
    }





    private record UUIDAndMobTypeKey(UUID uuid, EntityType<?> entityType) {
    }





}
