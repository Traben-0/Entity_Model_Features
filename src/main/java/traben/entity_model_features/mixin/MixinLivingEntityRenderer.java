package traben.entity_model_features.mixin;


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFAnimationProcessor;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.models.EMF_CustomModel;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static traben.entity_model_features.Entity_model_featuresClient.JEMPATH_CustomModel;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }
    @Shadow
    public abstract M getModel();
    @Shadow
    protected M model;

    @Shadow @Final protected List<FeatureRenderer<T, M>> features;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",shift = At.Shift.AFTER)
            , locals = LocalCapture.CAPTURE_FAILSOFT)
    private void injected(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, float h, float j, float k, float m, float l, float n, float o) {
        //here can redirect model rendering
        if (true/*livingEntity instanceof SheepEntity || livingEntity instanceof VillagerEntity*/){

            int typeHash = livingEntity.getType().hashCode();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(livingEntity)));

            if (!JEMPATH_CustomModel.containsKey(typeHash)){
                String entityTypeName =livingEntity.getType().getName().getString().toLowerCase().replace("\s","_");
                String modelID = "optifine/cem/"+entityTypeName+".jem";
                System.out.println("checking "+modelID);
                try {
                    EMF_JemData jem = EMFUtils.EMF_readJemData(modelID);
                    EMF_CustomModel<T> model = new EMF_CustomModel<>(jem);
                    JEMPATH_CustomModel.put(typeHash, (EMF_CustomModel<LivingEntity>) model);

                    //todo construct the animations processor
                    //todo render logic
                    //System.out.println("////////////////jemJsonObjects ");
                    //System.out.println(jem.toString());
                    //System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\jemJsonObjects ");

                }catch(Exception e){
                    EMFUtils.EMF_modMessage("failed for "+modelID+e,false);
                    e.printStackTrace();
                    JEMPATH_CustomModel.put(typeHash,null);
                }

                //temp while testing so only runs once
                //JEMPATH_CustomModel.put(entityTypeName,null);
            }
            if (JEMPATH_CustomModel.containsKey(typeHash)){
                if (JEMPATH_CustomModel.get(typeHash) != null){

                    //render model,
                    //System.out.println("rendering");
                    //JEMPATH_CustomModel.get(modelID).animate();
                    if (vanillaParts != null) {
                        EMF_CustomModel<LivingEntity> model =JEMPATH_CustomModel.get(typeHash);
                        //EMFAnimationProcessor.animateThisModel(vanillaParts,model,livingEntity);
                        model.animateModel(livingEntity, o, n, g);
                        model.setAngles(livingEntity, o, n, l, k, m);
                        model.render(vanillaParts,matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                    }
                }
            }
        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void EMF_VanillaRenderPrevent(EntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float k, float l, float m, float n) {





           // if(vanillaModel == null) {
               // vanillaModel = instance;
                vanillaParts.clear();
                //System.out.println("is quadped = "+(instance instanceof QuadrupedEntityModel));
                if (instance instanceof QuadrupedEntityModel quadped) {
                    ArrayList<ModelPart> bodyParts = new ArrayList<>();
                    Iterable<ModelPart> hed = ((QuadrupedEntityModelAccessor) quadped).callGetHeadParts();
                    for (ModelPart part : hed) {
                        vanillaParts.put("head",part);
                        break;
                    }

                    hed = ((QuadrupedEntityModelAccessor) quadped).callGetBodyParts();
                    for (ModelPart part : hed) {
                        bodyParts.add(part);
                    }
                    vanillaParts.put("body",bodyParts.get(0));
                    vanillaParts.put("leg1",bodyParts.get(1));
                    vanillaParts.put("leg2",bodyParts.get(2));
                    vanillaParts.put("leg3",bodyParts.get(3));
                    vanillaParts.put("leg4",bodyParts.get(4));


                }else if(instance instanceof BipedEntityModel<T> biped){
                    ArrayList<ModelPart> bodyParts = new ArrayList<>();
                    Iterable<ModelPart> hed = ((BipedEntityModelAccessor) biped).callGetHeadParts();
                    for (ModelPart part : hed) {
                        vanillaParts.put("head",part);
                        break;
                    }
                    //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
                    hed = ((BipedEntityModelAccessor) biped).callGetBodyParts();
                    for (ModelPart part : hed) {
                        bodyParts.add(part);
                    }
                    vanillaParts.put("body",bodyParts.get(0));
                    vanillaParts.put("right_arm",bodyParts.get(1));
                    vanillaParts.put("left_arm",bodyParts.get(2));
                    vanillaParts.put("right_leg",bodyParts.get(3));
                    vanillaParts.put("left_leg",bodyParts.get(4));
                    vanillaParts.put("headwear",bodyParts.get(5));
                }else if(instance instanceof VillagerResemblingModel<T> villager){
                   // ArrayList<ModelPart> bodyParts = new ArrayList<>();
                    ModelPart villagerRoot = villager.getPart();
                    vanillaParts.put("head",villagerRoot.getChild("head"));
                    vanillaParts.put("headwear",villagerRoot.getChild("head").getChild("hat"));
                    vanillaParts.put("headwear2",villagerRoot.getChild("head").getChild("hat").getChild("hat_rim"));
                    vanillaParts.put("body",villagerRoot.getChild("body"));
                    vanillaParts.put("bodywear",villagerRoot.getChild("body").getChild("jacket"));
                    vanillaParts.put("arms",villagerRoot.getChild("arms"));
                    vanillaParts.put("left_leg",villagerRoot.getChild("left_leg"));
                    vanillaParts.put("right_leg",villagerRoot.getChild("right_leg"));
                    vanillaParts.put("nose",villagerRoot.getChild("head").getChild("nose"));
                }{

                }
                //System.out.println(vanillaParts);
           // }
            for (ModelPart part: vanillaParts.values()) {
                part.visible = false;
            }
            if (vanillaParts.get("head") != null ) vanillaParts.get("head").visible = true;
            instance.render(matrixStack, vertexConsumer, i, j, k, l, m, n);


    }

  //  EntityModel<T> vanillaModel = null;
    HashMap<String,ModelPart> vanillaParts = new HashMap<>();
}
