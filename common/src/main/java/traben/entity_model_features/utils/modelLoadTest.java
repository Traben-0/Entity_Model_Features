package traben.entity_model_features.utils;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class modelLoadTest {


    public static ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root){

        if(layer == EntityModelLayers.ZOMBIE || layer == EntityModelLayers.SHEEP){
            System.out.println("ran zomb and sheep");
            String jemName = "optifine/cem/"+ layer.getId().getPath()+".jem";
            EMFJemData jemData = EMFUtils.EMF_readJemData(jemName);
            if(jemData != null){
                Map<String,ModelPart> rootChildren = new HashMap<>();

                System.out.println(layer.getId() +" exists = " + optifineMapsMap.containsKey(layer.getId()));


                Map<String,String> optifineMap = optifineMapsMap.get(layer.getId());
                if(optifineMap != null){
                    for (EMFPartData partData:
                         jemData.models) {
                        if(partData != null && partData.part != null){

                            String optifinePartName = optifineMap.getOrDefault(partData.part,partData.part);

                            EMFModelPart3 newPart = new EMFModelPart3(partData);
                            System.out.println("part made = "+ partData.id +" - "+ partData.part);
                            //todo need to incorporate parenting that matches vanilla part layout
                            //address it up here to both attach modes target the same point
                            if (!partData.attach) {

                                rootChildren.put(optifinePartName,newPart);
                            } else {
                                //if attach
                                if( rootChildren.containsKey(optifinePartName)){
                                    //todo attach
                                    // rootChildren.get(optifinePartName).emfChildren.putAll(newPart.emfChildren);
                                    // rootChildren.get(optifinePartName).emfCuboids.addAll(newPart.emfCuboids);
                                }else{
                                    // no part to attach to :/ ....
                                    System.out.println("no attach target: " + optifinePartName);
                                }
                            }

                        }else{
                            //part is not mapped to a vanilla part
                            System.out.println("no part definition");
                        }
                    }
                    //have iterated over all parts in jem and made them


                    EMFModelPart3 emfRootModelPart = new EMFModelPart3( new ArrayList<ModelPart.Cuboid>(), rootChildren);
                    //try
                    emfRootModelPart.pivotY = 24;
                    //todo check all were mapped correctly before return
                    System.out.println("emf returned");

                    //emfRootModelPart.assertChildrenAndCuboids();



                    return emfRootModelPart;
                }else{
                    //modded mob?
                    System.out.println("modded?");
                }
            }else{
                //not a cem mob
                System.out.println("nocem");
            }
            System.out.println("root returned");
        }

        return root;
    }


    private static final Map<Identifier,Map<String,String>> optifineMapsMap = new Reference2ObjectArrayMap<>(){{
        put(EntityModelLayers.ZOMBIE.getId(),
                Map.of("headwear","hat"));
        put(EntityModelLayers.SHEEP.getId(),
                Map.of("leg1","right_hind_leg",
                        "leg2", "left_hind_leg",
                        "leg3", "right_front_leg",
                        "leg4", "left_front_leg"));
    }};


    private static final HashMap<String,String> optifineMapZombie = new HashMap<>(){{
        put("headwear","hat");
    }};
    private static final HashMap<String,String> optifineMapSheep = new HashMap<>(){{
        put("leg1","right_hind_leg");
        put("leg2", "left_hind_leg");
        put("leg3", "right_front_leg");
        put("leg4", "left_front_leg");
    }};
}
