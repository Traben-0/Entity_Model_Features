package traben.entity_model_features.models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartVanilla extends EMFModelPartWithState {


    final String name;
    //construct single vanilla
    final boolean isOptiFinePartSpecified;
    final boolean rootType;
    public EMFModelPartVanilla(String name,
                               ModelPart vanillaPart,
                               Collection<String> optifinePartNames,
                               Map<String,EMFModelPartVanilla> allVanillaParts) {
        //create vanilla root model object
        super(new ArrayList<>(),new HashMap<>());
        this.name = name;

        if (EMFConfig.getConfig().printModelCreationInfoToLog)
            System.out.println(" > EMF vanilla part made: " + name);

        isOptiFinePartSpecified = optifinePartNames.contains(name);
        rootType = name.equals("root") || name.equals("EMF_root");

        EMFModelState state = getStateOf(vanillaPart);
        setFromState(state);
        Map<String,ModelPart> children = getChildrenEMF();
        for (Map.Entry<String,ModelPart> child:
                ((ModelPartAccessor) vanillaPart).getChildren().entrySet()) {
            EMFModelPartVanilla vanilla =new EMFModelPartVanilla(child.getKey(), child.getValue(),optifinePartNames, allVanillaParts);
            children.put(child.getKey(),vanilla);
            allVanillaParts.put(child.getKey(), vanilla);
        }
        vanillaChildren = getChildrenEMF();
        allKnownStateVariants.put(0,getCurrentState());

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //ignore non optifine specified parts when not vanilla variant
        if(rootType || currentModelVariantState == 0 || isOptiFinePartSpecified)
            super.render(matrices,vertices,light,overlay,red,green,blue,alpha);
    }

    ModelPart getVanillaModelPartsOfCurrentState(){
        Map<String, ModelPart> children = new HashMap<>();
        for (Map.Entry<String,ModelPart> child:
             getChildrenEMF().entrySet()) {
            if(child.getValue() instanceof EMFModelPart emf){
                children.put(child.getKey(),emf.getVanillaModelPartsOfCurrentState());
            }
        }

        ModelPart part = new ModelPart(((ModelPartAccessor)this).getCuboids(),children);
        part.setDefaultTransform(getDefaultTransform());
        part.pitch = pitch;
        part.roll = roll;
        part.yaw = yaw;
        part.pivotZ = pivotZ;
        part.pivotY = pivotY;
        part.pivotX = pivotX;
        part.xScale = xScale;
        part.yScale = yScale;
        part.zScale = zScale;

        return part;
    }


    //public static VertexConsumerProvider currentlyHeldProvider = null;
    void receiveRootVariationRunnable(Runnable run){
        variantCheck = run;
        getChildrenEMF().values().forEach((child)->{
            if(child instanceof EMFModelPartVanilla emf){
                emf.receiveRootVariationRunnable(run);
            }
        });
    }



    public void receiveRootAnimationRunnable(int variant, Runnable run){
        //tryAnimate = run;
        allKnownStateVariants.get(variant).animation().setAnimation(run);
//        if(variant == currentModelVariantState){
//            this.tryAnimate.setAnimation(run);
//        }
        getChildrenEMF().values().forEach((child)->{
            if(child instanceof EMFModelPartVanilla emf){
                emf.receiveRootAnimationRunnable(variant,run);
            }
        });
    }
}
