package traben.entity_model_features.mixin;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import net.minecraft.entity.mob.SpiderEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.models.EMF_CustomModel;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



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
            EMFData emfData = EMFData.getInstance();
            int typeHash = livingEntity.getType().hashCode();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(getTexture(livingEntity)));

            if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)){
                String entityTypeName =livingEntity.getType().getName().getString().toLowerCase().replace("\s","_");
                String modelID = "optifine/cem/"+entityTypeName+".jem";
                System.out.println("checking "+modelID);
                try {
                    EMF_JemData jem = EMFUtils.EMF_readJemData(modelID);
                    HashMap<String,ModelPart> vanillaPartList =getVanillaModelParts(getModel());
                    vanillaPartsByType.put(typeHash,vanillaPartList);
                    EMF_CustomModel<T> model = new EMF_CustomModel<>(jem,modelID,vanillaPartList);
                    emfData.JEMPATH_CustomModel.put(typeHash, (EMF_CustomModel<LivingEntity>) model);

                    //todo construct the animations processor
                    //todo render logic
                    //System.out.println("////////////////jemJsonObjects ");
                    //System.out.println(jem.toString());
                    //System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\jemJsonObjects ");

                }catch(Exception e){
                    EMFUtils.EMF_modMessage("failed for "+modelID+e,false);
                    e.printStackTrace();
                    emfData.JEMPATH_CustomModel.put(typeHash,null);
                }

                //temp while testing so only runs once
                //JEMPATH_CustomModel.put(entityTypeName,null);
            }
            if (emfData.JEMPATH_CustomModel.containsKey(typeHash)){
                if (emfData.JEMPATH_CustomModel.get(typeHash) != null){

                    //render model,
                    //System.out.println("rendering");
                    //JEMPATH_CustomModel.get(modelID).animate();


                    if (vanillaPartsByType.containsKey(typeHash)) {

                        HashMap<String,ModelPart> vanillaPartList =getVanillaModelParts(getModel());
                        EMF_CustomModel<LivingEntity> model =emfData.JEMPATH_CustomModel.get(typeHash);
                        //EMFAnimationProcessor.animateThisModel(vanillaParts,model,livingEntity);
                        model.setAnglesEMF(livingEntity, o, n, l, k, m,g,vanillaPartList);
                        model.render(vanillaPartsByType.get(typeHash),matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                    }
                }
            }
        }
    }

    private HashMap<String,ModelPart> getVanillaModelParts(M vanillaModel){
        HashMap<String,ModelPart> vanillaPartsList = new HashMap<>();

        if (vanillaModel instanceof SpiderEntityModel spider) {

            ModelPart root = spider.getPart();
            vanillaPartsList.put("head",root.getChild("head"));
            vanillaPartsList.put("neck",root.getChild("body0"));
            vanillaPartsList.put("body",root.getChild("body1"));
            vanillaPartsList.put("leg1",root.getChild("right_hind_leg"));
            vanillaPartsList.put("leg2",root.getChild("left_hind_leg"));
            vanillaPartsList.put("leg3",root.getChild("right_middle_hind_leg"));
            vanillaPartsList.put("leg4",root.getChild("left_middle_hind_leg"));
            vanillaPartsList.put("leg5",root.getChild("right_middle_front_leg"));
            vanillaPartsList.put("leg6",root.getChild("left_middle_front_leg"));
            vanillaPartsList.put("leg7",root.getChild("right_front_leg"));
            vanillaPartsList.put("leg8",root.getChild("left_front_leg"));


        }else if (vanillaModel instanceof IronGolemEntityModel iron) {

            ModelPart root = iron.getPart();
            vanillaPartsList.put("head",root.getChild("head"));
            vanillaPartsList.put("body",root.getChild("body"));
            vanillaPartsList.put("right_arm",root.getChild("right_arm"));
            vanillaPartsList.put("left_arm",root.getChild("left_arm"));
            vanillaPartsList.put("right_leg",root.getChild("right_leg"));
            vanillaPartsList.put("left_leg",root.getChild("left_leg"));


        }else if (vanillaModel instanceof QuadrupedEntityModel quadped) {
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((QuadrupedEntityModelAccessor) quadped).callGetHeadParts();
            for (ModelPart part : hed) {
                vanillaPartsList.put("head",part);
                break;
            }

            hed = ((QuadrupedEntityModelAccessor) quadped).callGetBodyParts();
            for (ModelPart part : hed) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("leg1",bodyParts.get(1));
            vanillaPartsList.put("leg2",bodyParts.get(2));
            vanillaPartsList.put("leg3",bodyParts.get(3));
            vanillaPartsList.put("leg4",bodyParts.get(4));


        }else if(vanillaModel instanceof BipedEntityModel biped){
            ArrayList<ModelPart> bodyParts = new ArrayList<>();
            Iterable<ModelPart> hed = ((BipedEntityModelAccessor) biped).callGetHeadParts();
            for (ModelPart part : hed) {
                vanillaPartsList.put("head",part);
                break;
            }
            //ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
            hed = ((BipedEntityModelAccessor) biped).callGetBodyParts();
            for (ModelPart part : hed) {
                bodyParts.add(part);
            }
            vanillaPartsList.put("body",bodyParts.get(0));
            vanillaPartsList.put("right_arm",bodyParts.get(1));
            vanillaPartsList.put("left_arm",bodyParts.get(2));
            vanillaPartsList.put("right_leg",bodyParts.get(3));
            vanillaPartsList.put("left_leg",bodyParts.get(4));
            vanillaPartsList.put("headwear",bodyParts.get(5));
        }else if(vanillaModel instanceof VillagerResemblingModel villager){
            // ArrayList<ModelPart> bodyParts = new ArrayList<>();
            ModelPart villagerRoot = villager.getPart();
            vanillaPartsList.put("head",villagerRoot.getChild("head"));
            vanillaPartsList.put("headwear",villagerRoot.getChild("head").getChild("hat"));
            vanillaPartsList.put("headwear2",villagerRoot.getChild("head").getChild("hat").getChild("hat_rim"));
            vanillaPartsList.put("body",villagerRoot.getChild("body"));
            vanillaPartsList.put("bodywear",villagerRoot.getChild("body").getChild("jacket"));
            vanillaPartsList.put("arms",villagerRoot.getChild("arms"));
            vanillaPartsList.put("left_leg",villagerRoot.getChild("left_leg"));
            vanillaPartsList.put("right_leg",villagerRoot.getChild("right_leg"));
            vanillaPartsList.put("nose",villagerRoot.getChild("head").getChild("nose"));
        }

        return vanillaPartsList;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void EMF_VanillaRenderPrevent(EntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float k, float l, float m, float n) {





           // if(vanillaModel == null) {
               // vanillaModel = instance;
                //vanillaParts.clear();
                //System.out.println("is quadped = "+(instance instanceof QuadrupedEntityModel));
               {

                }
                //System.out.println(vanillaParts);
           // }
//            for (ModelPart part: vanillaParts.values()) {
//                part.visible = false;
//            }
           // if (vanillaParts.get("head") != null ) vanillaParts.get("head").visible = true;
           // instance.render(matrixStack, vertexConsumer, i, j, k, l, m, n);


    }

  //  EntityModel<T> vanillaModel = null;
  Int2ObjectOpenHashMap<HashMap<String,ModelPart>> vanillaPartsByType = new Int2ObjectOpenHashMap<>();
}
