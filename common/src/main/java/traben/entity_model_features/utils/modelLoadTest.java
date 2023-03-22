package traben.entity_model_features.utils;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class modelLoadTest {


    public static ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root) {

        if (layer == EntityModelLayers.ZOMBIE || layer == EntityModelLayers.SHEEP || layer == EntityModelLayers.VILLAGER) {
            System.out.println("ran zomb and sheep");
            String jemName = "optifine/cem/" + layer.getId().getPath() + ".jem";
            EMFJemData jemData = EMFUtils.EMF_readJemData(jemName);
            if (jemData != null) {
                Map<String, ModelPart> rootChildren = new HashMap<>();

                for (EMFPartData partData :
                        jemData.models) {
                    if (partData != null && partData.part != null) {

                        EMFModelPart3 newPart = new EMFModelPart3(partData);
                        System.out.println("part made = "+ partData.id +" - "+ partData.part);

                        rootChildren.put(partData.part,newPart);

                    } else {
                        //part is not mapped to a vanilla part
                        System.out.println("no part definition");
                    }
                }
                //have iterated over all parts in jem and made them


                EMFModelPart3 emfRootModelPart = new EMFModelPart3(new ArrayList<ModelPart.Cuboid>(), rootChildren);
                //try
                //todo pretty sure we must match root transforms because of fucking frogs, maybe?
                //emfRootModelPart.pivotY = 24;
                //todo check all were mapped correctly before return
                System.out.println("emf returned");

                //emfRootModelPart.assertChildrenAndCuboids();


                return emfRootModelPart;

            } else {
                //not a cem mob
                System.out.println("nocem");
            }
            System.out.println("root returned");
        }

        return root;
    }



}
