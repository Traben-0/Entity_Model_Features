package traben.entity_model_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFCustomModel;
import traben.entity_model_features.models.anim.AnimationGetters;
import traben.entity_model_features.models.vanilla_model_children.*;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.etfPropertyReader;
import traben.entity_model_features.vanilla_part_mapping.VanillaMappings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class EMFData {

    private static Boolean isValidETF = null;
    private EMFConfig EMFConfigData;
    private static EMFData self = null;

    public static boolean isValidETF(){
        //System.out.println("ran");
        if(isValidETF == null) {
            isValidETF = false;
            for (ModContainer mod:
            FabricLoader.getInstance().getAllMods()){
                if ("entity_texture_features".equals(mod.getMetadata().getId())){
                    isValidETF = "4.3.2.dev.1".equals(mod.getMetadata().getVersion().getFriendlyString());//todo proper api update
                   // EMFUtils.EMF_modMessage("ETF valid = "+isValid);
                }
            }

        }
        return isValidETF;
    }

    public EMFConfig getConfig(){
        if(EMFConfigData == null){
            loadConfig();
        }
        return EMFConfigData;
    }
    public  Int2BooleanOpenHashMap alreadyCalculatedForRenderer = new Int2BooleanOpenHashMap();
    public  Int2ObjectOpenHashMap<EMF_EntityModel<?>> JEMPATH_CustomModel = new Int2ObjectOpenHashMap<>();


    private EMFData(){
        isETFPresent = FabricLoader.getInstance().isModLoaded("entity_texture_features");
        alreadyCalculatedForRenderer.defaultReturnValue(false);
        getConfig();
    }

    public static EMFData getInstance(){
        if(self == null){
           self = new EMFData();
        }
        return self;
    }

    public static void reset(){
        self = new EMFData();
    }

    public void EMF_saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_model_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(EMFConfigData));
            fileWriter.close();
        } catch (IOException e) {
            EMFUtils.EMF_modMessage("Config could not be saved", false);
        }
    }

    public void loadConfig() {
        try {
            File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_model_features.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (config.exists()) {
                try {
                    FileReader fileReader = new FileReader(config);
                    EMFConfigData = gson.fromJson(fileReader, EMFConfig.class);
                    fileReader.close();
                    EMF_saveConfig();
                } catch (IOException e) {
                    EMFUtils.EMF_modMessage("Config could not be loaded, using defaults", false);
                }
            } else {
                EMFConfigData = new EMFConfig();
                EMF_saveConfig();
            }
            if (EMFConfigData == null) {
                EMFConfigData = new EMFConfig();
                EMF_saveConfig();
            }
        }catch (Exception e){
            EMFConfigData = new EMFConfig();
        }
    }

    public  <T extends LivingEntity> EMF_EntityModel<T> createEMFModelOnly(String modelJemName, EntityModel<T> vanillaModel){
        int hashKeyTypicallyEntityType = modelJemName.hashCode();
        if(!JEMPATH_CustomModel.containsKey(hashKeyTypicallyEntityType)) {
            String modelID = "optifine/cem/" + modelJemName + ".jem";
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("checking " + modelID);
            try {
                EMF_JemData jem = EMFUtils.EMF_readJemData(modelID);
                if(jem!=null) {
                    VanillaMappings.VanillaMapper vanillaPartSupplier = VanillaMappings.getVanillaModelPartsMapSupplier(hashKeyTypicallyEntityType, vanillaModel);
                    //vanillaPartsByType.put(typeHash,vanillaPartList);
                    EMF_EntityModel<T> model = new EMF_EntityModel<>(jem, modelID, vanillaPartSupplier, vanillaModel);
                    JEMPATH_CustomModel.put(hashKeyTypicallyEntityType, (EMF_EntityModel<LivingEntity>) model);
                    if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                        EMFUtils.EMF_modMessage("put emfpart in data =" + model.toString());
                }else{
                    if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("no jem found for " + modelID);
                    return null;
                }
            } catch (Exception e) {
                EMFUtils.EMF_modMessage("failed for " + modelID + e, false);
                e.printStackTrace();
                JEMPATH_CustomModel.put(hashKeyTypicallyEntityType, null);
                return null;
            }
        }
        return (EMF_EntityModel<T>) JEMPATH_CustomModel.get(hashKeyTypicallyEntityType);
    }

    private <T extends LivingEntity, M extends EntityModel<T>> EntityModel<?> getCustomModelForRendererGeneric(EMF_EntityModel<?> alreadyBuiltSubmodel, EntityModel<?> vanillaModelForInstanceCheck){
        return getCustomModelForRenderer((EMF_EntityModel<T>)alreadyBuiltSubmodel,(M)vanillaModelForInstanceCheck);
    }

    private <T extends LivingEntity, M extends EntityModel<T>> M getCustomModelForRenderer(EMF_EntityModel<T> alreadyBuiltSubmodel,M vanillaModelForInstanceCheck){
        //figure out whether to send a vanilla child model or a direct EMF custom model
        if(vanillaModelForInstanceCheck instanceof CowEntityModel<?>){
            return (M) new EMFCustomCowModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof SlimeEntityModel<?>){
            return (M) new EMFCustomSlimeModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof LlamaEntityModel){
            return (M) new EMFCustomLlamaModel<T, LlamaEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof FoxEntityModel){
            return (M) new EMFCustomFoxModel<T, FoxEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof EndermanEntityModel){
            return (M) new EMFCustomEndermanModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof HorseEntityModel){
            return (M) new EMFCustomHorseModel<T, AbstractHorseEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof PlayerEntityModel){
            return (M) new EMFCustomPlayerModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof WitchEntityModel){
            return (M) new EMFCustomWitchModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof IllagerEntityModel){
            return (M) new EMFCustomIllagerModel<T, IllagerEntity>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof VillagerResemblingModel){
            return (M) new EMFCustomVillagerModel<T>(alreadyBuiltSubmodel);
        }
        //this for instance allows vanilla features like non custom armour and hand held items to work for bipeds
        if(vanillaModelForInstanceCheck instanceof BipedEntityModel){
            return (M) new EMFCustomBipedModel<T>(alreadyBuiltSubmodel);
        }
        if(vanillaModelForInstanceCheck instanceof QuadrupedEntityModel){
            return (M) new EMFCustomQuadrapedModel<T>(alreadyBuiltSubmodel);
        }
        //this for instance allows vanilla features like flower holding to work
        if(vanillaModelForInstanceCheck instanceof IronGolemEntityModel){
            return (M) new EMFCustomIronGolemModel<T,IronGolemEntity>(alreadyBuiltSubmodel);
        }

        return (M) alreadyBuiltSubmodel;

    }
    public<T extends LivingEntity, M extends EntityModel<T>> M getModelVariantGeneric(Entity entity, String entityTypeName, EntityModel<?> vanillaModel){
        return getModelVariant(entity,entityTypeName,(EntityModel<T>)vanillaModel);
    }

    Object2LongOpenHashMap<UUID> UUID_LAST_UPDATE_TIME = new Object2LongOpenHashMap<>(){{defaultReturnValue(0);}};

    Object2ObjectOpenHashMap<UUID,EMFCustomModel<?>> UUID_TO_MODEL = new Object2ObjectOpenHashMap<>();
    public<T extends LivingEntity, M extends EntityModel<T>> M getModelVariant(Entity entity, String entityTypeName, EntityModel<T> vanillaModel) {


        if(entity == null){
            EMF_EntityModel<T> emfModel = createEMFModelOnly(entityTypeName,vanillaModel);
            return (M) getFinalEMFModel(entityTypeName,emfModel, vanillaModel);
        }

        EMFCustomModel<?> knownModel = UUID_TO_MODEL.get(entity.getUuid());
        if (knownModel != null) {
            if (UUID_MOB_MODEL_UPDATES.getBoolean(entity.getUuid())) {
                long time = System.currentTimeMillis();
                if (time > 1000 + UUID_LAST_UPDATE_TIME.getLong(entity.getUuid())) {
                    UUID_LAST_UPDATE_TIME.put(entity.getUuid(), time);
                    EMFCustomModel<?> newModel = getModelVariantPossibleNew(entity, entityTypeName, vanillaModel);
                    if (newModel != null)
                        UUID_TO_MODEL.put(entity.getUuid(), newModel);
                    return (M) newModel;
                }
            }
            return (M) knownModel;
        }
        return getModelVariantPossibleNew(entity, entityTypeName, vanillaModel);
    }

    private boolean isETFPresent;
    private<T extends LivingEntity, M extends EntityModel<T>> M getModelVariantPossibleNew(Entity entity, String entityTypeName, EntityModel<T> vanillaModel){
       // System.out.println("ran");
        EMF_EntityModel<T> emfModel = createEMFModelOnly(entityTypeName,vanillaModel);
        //System.out.println("rans="+isETFPresent+etfPropertyReader.isValidETF());
        if(emfModel != null) {
            // jem exists so decide if variation occurs
            //System.out.println("rans="+isETFPresent+etfPropertyReader.isValidETF());
            if (isETFPresent && isValidETF()) {

                if(!MODEL_CASES.containsKey(entityTypeName)) {
                    Identifier propertyID = new Identifier("optifine/cem/" + entityTypeName + ".properties");
                    if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
                        List<etfPropertyReader.EMFPropertyCase> emfCases = etfPropertyReader.getAllValidPropertyObjects(propertyID, "models", entityTypeName);
                        MODEL_CASES.put(entityTypeName, emfCases);
                    }
                }
                List<etfPropertyReader.EMFPropertyCase> emfCases = MODEL_CASES.get(entityTypeName);
                if (emfCases != null && !emfCases.isEmpty()){
                    for (etfPropertyReader.EMFPropertyCase emfCase:
                            emfCases) {
                        if (emfCase.testCase(entity,false,UUID_MOB_MODEL_UPDATES)){
                          //  System.out.println("was true");
                            int suffix = emfCase.getSuffix(entity.getUuid());
                            String variantName = entityTypeName+suffix;
                            EMF_EntityModel<T> emfModelVariant = createEMFModelOnly(variantName,vanillaModel);
                            if(emfModelVariant != null){
                                EMFCustomModel<T> mod = (EMFCustomModel<T>) getFinalEMFModel(variantName,emfModelVariant, vanillaModel);
                                UUID_TO_MODEL.put(entity.getUuid(), mod);
                                return (M) mod;
                            }
                        }
                    }
                }
            }
            EMFCustomModel<T> mod = (EMFCustomModel<T>) getFinalEMFModel(entityTypeName,emfModel, vanillaModel);
            UUID_TO_MODEL.put(entity.getUuid(), mod);
            return (M) mod;
        }
        return null;
    }

    private <T extends LivingEntity, M extends EntityModel<T>> M  getFinalEMFModel(String jemName,EMF_EntityModel<T> alreadyBuiltSubmodel,M vanillaModelForInstanceCheck){
        if(!COMPLETE_MODELS_FOR_RETURN.containsKey(jemName)){
            M finalModel = getCustomModelForRenderer(alreadyBuiltSubmodel, vanillaModelForInstanceCheck);
            COMPLETE_MODELS_FOR_RETURN.put(jemName, (EMFCustomModel<?>) finalModel);
            return finalModel;
        }
        return (M) COMPLETE_MODELS_FOR_RETURN.get(jemName);
    }

    public Object2ObjectOpenHashMap<String, EMFCustomModel<?>> COMPLETE_MODELS_FOR_RETURN = new Object2ObjectOpenHashMap<>();
    public Object2ObjectOpenHashMap<String,List<etfPropertyReader.EMFPropertyCase>> MODEL_CASES = new Object2ObjectOpenHashMap<>();
    public Object2BooleanOpenHashMap<UUID> UUID_MOB_MODEL_UPDATES = new Object2BooleanOpenHashMap<>();

    public EMFCustomPlayerModel<?> clientPlayerModel = null;
    public EntityModel<PlayerEntity> clientPlayerVanillaModel = null;
    public boolean checkedHand = false;
    public AnimationGetters clientGetter = null;

}
