package traben.entity_model_features;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.models.EMF_CustomModel;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.vanilla_part_mapping.VanillaMappings;

public class EMFData {

    private static EMFData self = null;

    public  Int2ObjectOpenHashMap<EMF_CustomModel<LivingEntity>> JEMPATH_CustomModel = new Int2ObjectOpenHashMap<>();
    private EMFData(){

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

    public void createEMFModel(String modelJemName, int hashKeyTypicallyEntityType, EntityModel<?> vanillaModel){

        String modelID = "optifine/cem/"+modelJemName+".jem";
        System.out.println("checking "+modelID);
        try {
            EMF_JemData jem = EMFUtils.EMF_readJemData(modelID);
            VanillaMappings.VanillaMapper vanillaPartSupplier = VanillaMappings.getVanillaModelPartsMapSupplier(hashKeyTypicallyEntityType,vanillaModel);
            //vanillaPartsByType.put(typeHash,vanillaPartList);
            EMF_CustomModel<?> model = new EMF_CustomModel<>(jem,modelID,vanillaPartSupplier,vanillaModel);
            JEMPATH_CustomModel.put(hashKeyTypicallyEntityType, (EMF_CustomModel<LivingEntity>) model);
            System.out.println("put emfpart in data ="+ model.toString());

        }catch(Exception e){
            EMFUtils.EMF_modMessage("failed for "+modelID+e,false);
            e.printStackTrace();
            JEMPATH_CustomModel.put(hashKeyTypicallyEntityType,null);
        }

    }



    private final int allayHash =EntityType.ALLAY.hashCode();


}
