package traben.entity_model_features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.vanilla_model_children.*;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.vanilla_part_mapping.VanillaMappings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EMFData {

    private EMFConfig EMFConfigData;
    private static EMFData self = null;

    public EMFConfig getConfig(){
        if(EMFConfigData == null){
            loadConfig();
        }
        return EMFConfigData;
    }
    public  Int2BooleanOpenHashMap alreadyCalculatedForRenderer = new Int2BooleanOpenHashMap();
    public  Int2ObjectOpenHashMap<EMF_EntityModel<?>> JEMPATH_CustomModel = new Int2ObjectOpenHashMap<>();
    private EMFData(){
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

    // config code based on bedrockify & actually unbreaking fabric config code
    // https://github.com/juancarloscp52/BedrockIfy/blob/1.17.x/src/main/java/me/juancarloscp52/bedrockify/Bedrockify.java
    // https://github.com/wutdahack/ActuallyUnbreakingFabric/blob/1.18.1/src/main/java/wutdahack/actuallyunbreaking/ActuallyUnbreaking.java
    public void loadConfig() {
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
        if(EMFConfigData == null){
            EMFConfigData = new EMFConfig();
            EMF_saveConfig();
        }
    }

    public<T extends LivingEntity> EMF_EntityModel<T> createEMFModel(String modelJemName, int hashKeyTypicallyEntityType, EntityModel<T> vanillaModel){
        if(!JEMPATH_CustomModel.containsKey(hashKeyTypicallyEntityType)) {
            String modelID = "optifine/cem/" + modelJemName + ".jem";
            System.out.println("checking " + modelID);
            try {
                EMF_JemData jem = EMFUtils.EMF_readJemData(modelID);
                VanillaMappings.VanillaMapper vanillaPartSupplier = VanillaMappings.getVanillaModelPartsMapSupplier(hashKeyTypicallyEntityType, vanillaModel);
                //vanillaPartsByType.put(typeHash,vanillaPartList);
                EMF_EntityModel<T> model = new EMF_EntityModel<>(jem, modelID, vanillaPartSupplier, vanillaModel);
                JEMPATH_CustomModel.put(hashKeyTypicallyEntityType, (EMF_EntityModel<LivingEntity>) model);
                System.out.println("put emfpart in data =" + model.toString());

            } catch (Exception e) {
                EMFUtils.EMF_modMessage("failed for " + modelID + e, false);
                e.printStackTrace();
                JEMPATH_CustomModel.put(hashKeyTypicallyEntityType, null);
            }
        }
        return (EMF_EntityModel<T>) JEMPATH_CustomModel.get(hashKeyTypicallyEntityType);
    }

    public<T extends LivingEntity, M extends EntityModel<T>> M getCustomModelForRenderer(EMF_EntityModel<T> alreadyBuiltSubmodel,M vanillaModelForInstanceCheck){
        //figure out whether to send a vanilla child model or a direct EMF custom model
        if(vanillaModelForInstanceCheck instanceof CowEntityModel<?>){
            return (M) new EMFCustomCowModel<T>(alreadyBuiltSubmodel);
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



}
