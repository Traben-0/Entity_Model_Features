package traben.entity_model_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.EMFCustomEntityModel;
import traben.entity_model_features.models.EMFGenericCustomEntityModel;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.vanilla_model_compat.VanillaModelPartOptiFineMappings;
import traben.entity_model_features.models.vanilla_model_compat.VanillaModelWrapperHandler;
import traben.entity_model_features.models.vanilla_model_compat.model_wrappers.biped.EMFCustomPlayerEntityModel;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.etfPropertyReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class EMFData {

    private static Boolean isValidETF = null;
    private EMFConfig EMFConfigData;
    private static EMFData self = null;



    public EMFConfig getConfig(){
        if(EMFConfigData == null){
            loadConfig();
        }
        return EMFConfigData;
    }
    public  Int2BooleanOpenHashMap alreadyCalculatedForRenderer = new Int2BooleanOpenHashMap();
    public  Int2ObjectOpenHashMap<EMFGenericCustomEntityModel<?>> JEMPATH_CustomModel = new Int2ObjectOpenHashMap<>();


    private EMFData(){
        isETFPresent = FabricLoader.getInstance().isModLoaded("entity_texture_features");
        alreadyCalculatedForRenderer.defaultReturnValue(false);

        // must be called at least once to reset config
        //getConfig();

        //todo future math optimization options will need this removed
        getConfig().mathFunctionChoice = EMFConfig.MathFunctionChoice.JavaMath;

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

    public  <T extends LivingEntity> EMFGenericCustomEntityModel<T> createEMFModelOnly(String modelJemName, EntityModel<T> vanillaModel){
        int hashKeyTypicallyEntityType = modelJemName.hashCode();
        if(!JEMPATH_CustomModel.containsKey(hashKeyTypicallyEntityType)) {
            String modelID = "optifine/cem/" + modelJemName + ".jem";
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("checking " + modelID);
            try {
                EMFJemData jem = EMFUtils.EMF_readJemData(modelID);
                if(jem!=null) {
                    VanillaModelPartOptiFineMappings.VanillaMapper vanillaPartSupplier = VanillaModelPartOptiFineMappings.getVanillaModelPartsMapSupplier(hashKeyTypicallyEntityType, vanillaModel);
                    //vanillaPartsByType.put(typeHash,vanillaPartList);
                    EMFGenericCustomEntityModel<T> model = new EMFGenericCustomEntityModel<>(jem, modelID, vanillaPartSupplier, vanillaModel);
                    JEMPATH_CustomModel.put(hashKeyTypicallyEntityType, (EMFGenericCustomEntityModel<LivingEntity>) model);
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
        return (EMFGenericCustomEntityModel<T>) JEMPATH_CustomModel.get(hashKeyTypicallyEntityType);
    }

    public<T extends LivingEntity, M extends EntityModel<T>> M getModelVariantGeneric(Entity entity, String entityTypeName, EntityModel<?> vanillaModel){
        return getModelVariant(entity,entityTypeName,(EntityModel<T>)vanillaModel);
    }

    Object2LongOpenHashMap<UUID> UUID_LAST_UPDATE_TIME = new Object2LongOpenHashMap<>(){{defaultReturnValue(0);}};

    Object2ObjectOpenHashMap<UUID, EMFCustomEntityModel<?>> UUID_TO_MODEL = new Object2ObjectOpenHashMap<>();
    public<T extends LivingEntity, M extends EntityModel<T>> M getModelVariant(Entity entity, String entityTypeName, EntityModel<T> vanillaModel) {


        if(entity == null){
            EMFGenericCustomEntityModel<T> emfModel = createEMFModelOnly(entityTypeName,vanillaModel);
            return (M) getFinalEMFModel(entityTypeName,emfModel, vanillaModel);
        }
        UUID id;
        if(entity instanceof PufferfishEntity){
            String newU = entity.getUuid().toString() + entityTypeName.hashCode();
            id = UUID.nameUUIDFromBytes(newU.getBytes());
        }else {
            id = entity.getUuid();
        }

        EMFCustomEntityModel<?> knownModel = UUID_TO_MODEL.get(id);
        if (knownModel != null) {
            if (UUID_MOB_MODEL_UPDATES.getBoolean(id)) {
                long time = System.currentTimeMillis();
                if (time > 1000 + UUID_LAST_UPDATE_TIME.getLong(id)) {
                    UUID_LAST_UPDATE_TIME.put(id, time);
                    EMFCustomEntityModel<?> newModel = getModelVariantPossibleNew(entity, entityTypeName, vanillaModel);
                    if (newModel != null)
                        UUID_TO_MODEL.put(id, newModel);
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
        EMFGenericCustomEntityModel<T> emfModel = createEMFModelOnly(entityTypeName,vanillaModel);
        //System.out.println("rans="+isETFPresent+etfPropertyReader.isValidETF());
        if(emfModel != null) {
            // jem exists so decide if variation occurs
            //System.out.println("rans="+isETFPresent+etfPropertyReader.isValidETF());
            if (isETFPresent) {

                if(!MODEL_CASES.containsKey(entityTypeName)) {
                    Identifier propertyID = new Identifier("optifine/cem/" + entityTypeName + ".properties");
                    if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
                        EMFPropertyTester emfTester = etfPropertyReader.getAllValidPropertyObjects(propertyID);
                        MODEL_CASES.put(entityTypeName, emfTester);
                    }
                }
                EMFPropertyTester emfProperty = MODEL_CASES.get(entityTypeName);
                if (emfProperty != null){
//                    for (EMFPropertyTester emfCase:
//                            emfCases) {
                        //if (emfCase.testCase(entity,false,UUID_MOB_MODEL_UPDATES)){
                          //  System.out.println("was true");
                    int suffix = emfProperty.getSuffixOfEntity(entity,UUID_MOB_MODEL_UPDATES.containsKey(entity.getUuid()),UUID_MOB_MODEL_UPDATES);
                    if(suffix > 1) { // ignore 0 & 1
                        String variantName = entityTypeName + suffix;
                        EMFGenericCustomEntityModel<T> emfModelVariant = createEMFModelOnly(variantName, vanillaModel);
                        if (emfModelVariant != null) {
                            EMFCustomEntityModel<T> mod = (EMFCustomEntityModel<T>) getFinalEMFModel(variantName, emfModelVariant, vanillaModel);
                            UUID_TO_MODEL.put(entity.getUuid(), mod);
                            return (M) mod;
                        }else{
                            EMFUtils.EMF_modWarn(" Model variant didn't exist: looked for ["+variantName+"], found nothing. using default model...");
                        }
                    }
//                    }
                }
            }
            EMFCustomEntityModel<T> mod = (EMFCustomEntityModel<T>) getFinalEMFModel(entityTypeName,emfModel, vanillaModel);
            UUID_TO_MODEL.put(entity.getUuid(), mod);
            return (M) mod;
        }
        return null;
    }

    private <T extends LivingEntity, M extends EntityModel<T>> M  getFinalEMFModel(String jemName, EMFGenericCustomEntityModel<T> alreadyBuiltSubmodel, M vanillaModelForInstanceCheck){
        if(!COMPLETE_MODELS_FOR_RETURN.containsKey(jemName)){
            M finalModel = VanillaModelWrapperHandler.getCustomModelForRenderer(alreadyBuiltSubmodel, vanillaModelForInstanceCheck);
            COMPLETE_MODELS_FOR_RETURN.put(jemName, (EMFCustomEntityModel<?>) finalModel);
            return finalModel;
        }
        return (M) COMPLETE_MODELS_FOR_RETURN.get(jemName);
    }

    public Object2ObjectOpenHashMap<String, EMFCustomEntityModel<?>> COMPLETE_MODELS_FOR_RETURN = new Object2ObjectOpenHashMap<>();
    public Object2ObjectOpenHashMap<String,EMFPropertyTester> MODEL_CASES = new Object2ObjectOpenHashMap<>();
    public Object2BooleanOpenHashMap<UUID> UUID_MOB_MODEL_UPDATES = new Object2BooleanOpenHashMap<>();

    public EMFCustomPlayerEntityModel<?> clientPlayerModel = null;
    public EntityModel<PlayerEntity> clientPlayerVanillaModel = null;
    public boolean checkedHand = false;
    public final HashMap<Integer, VanillaModelPartOptiFineMappings.VanillaMapper> vanilla_mappings_map = new HashMap<>();

    public interface EMFPropertyTester {
        int getSuffixOfEntity(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom);
    }
}
