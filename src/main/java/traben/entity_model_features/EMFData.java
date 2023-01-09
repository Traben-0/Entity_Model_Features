package traben.entity_model_features;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.LivingEntity;
import traben.entity_model_features.models.EMF_CustomModel;

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
}
