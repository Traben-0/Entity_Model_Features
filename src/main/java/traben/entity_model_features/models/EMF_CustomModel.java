package traben.entity_model_features.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import traben.entity_model_features.client.EMFUtils;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Environment(value= EnvType.CLIENT)
public class EMF_CustomModel<T extends Entity> extends EntityModel<T>  {

    private final EMF_JemData jemData;
    private final Map<String, EMF_CustomModelPart<T>> children = new HashMap<>();


    public EMF_CustomModel(EMF_JemData jem){
        jemData = jem;
        for (EMF_ModelData sub:
                jemData.models) {
            children.put(sub.id,new EMF_CustomModelPart<T>(0,sub,new ArrayList<EMF_ModelData>()));
        }

    }



    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

    }

    public void render(HashMap<String,ModelPart> vanillaParts,MatrixStack herematrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        for (String key:
                children.keySet()) {
            herematrices.push();
            //herematrices.translate(0,16,0);
            children.get(key).render(0,vanillaParts,herematrices,vertices,light,overlay,red,green,blue,alpha);
            herematrices.pop();
        }
    }

}
