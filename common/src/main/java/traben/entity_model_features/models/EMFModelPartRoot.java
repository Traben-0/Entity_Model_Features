package traben.entity_model_features.models;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;
import traben.entity_texture_features.ETFApi;

import java.util.*;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartRoot extends EMFModelPartWithState {


    //construct vanilla root
    public EMFModelPartRoot(OptifineMobNameForFileAndEMFMapId mobNameForFileAndMap,
                            EMFManager.CemDirectoryApplier variantDirectoryApplier,
                            ModelPart vanillaRoot,
                            Map<String, String> optifinePartNameMap) {
        //create vanilla root model object
        super(new ArrayList<>(),new HashMap<>());
        this.modelName = mobNameForFileAndMap;
        this.variantDirectoryApplier = variantDirectoryApplier;
//        EMFModelState state = getStateOf(vanillaRoot);
//        setFromState(state);
//        allKnownStateVariants.put(0, state);


        Map<String,ModelPart> children = getChildrenEMF();
        for (Map.Entry<String,ModelPart> child:
             ((ModelPartAccessor) vanillaRoot).getChildren().entrySet()) {
            EMFModelPartVanilla vanilla =new EMFModelPartVanilla(child.getKey(),child.getValue(),optifinePartNameMap.values(), allVanillaParts);
            children.put(child.getKey(), vanilla);
            allVanillaParts.put(child.getKey(), vanilla);
        }

        receiveRootVariationRunnable(()->{
            if(this.lastFrameVariatedOn != EMFManager.getInstance().entityRenderCount){
                this.lastFrameVariatedOn = EMFManager.getInstance().entityRenderCount;
                EMFManager.getInstance().doVariantCheckFor(this);
            }
        });
        this.vanillaRoot = vanillaRoot;

        vanillaChildren = getChildrenEMF();
        allKnownStateVariants.put(0,getCurrentState());
    }

    Map<String,EMFModelPartVanilla> allVanillaParts = new HashMap<>();

    //root only
    public void addVariantOfJem(EMFJemData jemData, int variant){
        Map<String, EMFModelPartCustom> newEmfParts = new HashMap<>();
        for (EMFPartData part:
             jemData.models) {
            if(part.part != null)
                newEmfParts.put(getIdUnique(newEmfParts.keySet(),part.id),new EMFModelPartCustom(part,variant,part.part));
        }

        for (Map.Entry<String, EMFModelPartVanilla> vanillaEntry:
             allVanillaParts.entrySet()) {
            String thisPartName = vanillaEntry.getKey();
            EMFModelPartVanilla thisPart = vanillaEntry.getValue();
            EMFModelState vanillaState =EMFModelState.copy(thisPart.allKnownStateVariants.get(0));
            thisPart.setFromState(vanillaState);
            Map<String,ModelPart> children = new HashMap<>();
            for (Map.Entry<String, EMFModelPartCustom> newPartEntry:
                 newEmfParts.entrySet()) {
                EMFModelPartCustom newPart = newPartEntry.getValue();
                if(thisPartName.equals(newPart.partToBeAttached)){
                    if(!newPart.attach){
                        ((ModelPartAccessor)thisPart).setCuboids(List.of());
                    }
                    children.put(getIdUnique(thisPart.getChildrenEMF().keySet(),newPartEntry.getKey()),newPart);
                }
                children.putAll(thisPart.vanillaChildren);
                ((ModelPartAccessor)thisPart).setChildren(children);
            }
            thisPart.allKnownStateVariants.put(variant,thisPart.getCurrentState());
        }
        allKnownStateVariants.put(variant,EMFModelState.copy(allKnownStateVariants.get(0)));
        //done?
    }

    @Override
    public void setVariantStateTo(int newVariantState) {
        System.out.println("set= "+newVariantState);
        if (currentModelVariantState != newVariantState) {
            if (newVariantState == 1 && !allKnownStateVariants.containsKey(1)) {
                super.setVariantStateTo(0);
            }else{
                super.setVariantStateTo(newVariantState);
            }
        }
    }

    private static String getIdUnique(Set<String> known, String desired){
        if(desired.isBlank())desired="-";
        while(known.contains(desired)){
            desired = desired +"-";
        }
        return desired;
    }




    public OptifineMobNameForFileAndEMFMapId modelName;
    public EMFJemData jemData = null;
    public EMFManager.CemDirectoryApplier variantDirectoryApplier;
    public ETFApi.ETFRandomTexturePropertyInstance variantTester = null;

    public ModelPart vanillaRoot = null;

    private final Int2ObjectOpenHashMap<ModelPart> vanillaFormatModelPartOfEachState = new Int2ObjectOpenHashMap<>();

    public void tryRenderVanillaRootNormally(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay){
        if(vanillaRoot != null) {
            matrixStack.push();
            if (EMFConfig.getConfig().vanillaModelRenderMode == EMFConfig.VanillaModelRenderMode.Positon_offset) {
                matrixStack.translate(1, 0, 0);
            }
            vanillaRoot.render(matrixStack,vertexConsumer,light,overlay,1,0.5f,0.5f,0.5f);
            matrixStack.pop();
        }
    }

    ModelPart getVanillaModelPartsOfCurrentState(){
        Map<String, ModelPart> children = new HashMap<>();
        for (Map.Entry<String,ModelPart> child:
             getChildrenEMF().entrySet()) {
            if(child.getValue() instanceof EMFModelPartRoot emf){
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




    public void tryRenderVanillaFormatRoot(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay){
        if(EMFConfig.getConfig().attemptPhysicsModPatch_2 == EMFConfig.PhysicsModCompatChoice.VANILLA){
            if (vanillaRoot != null) {
                vanillaRoot.render(matrixStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
            }
        }else{
            if (!vanillaFormatModelPartOfEachState.containsKey(currentModelVariantState)) {
                vanillaFormatModelPartOfEachState.put(currentModelVariantState, getVanillaModelPartsOfCurrentState());
            }
            ModelPart vanillaFormat = vanillaFormatModelPartOfEachState.get(currentModelVariantState);
            if (vanillaFormat != null) {
                vanillaFormat.render(matrixStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
            }
        }
    }


    private long lastFrameVariatedOn = -1;



    public void receiveAnimations(int variant,LinkedList<EMFAnimation> orderedAnimations){
        if(orderedAnimations.size()>0) {
            //this.orderedAnimations = orderedAnimations;

            Runnable run = ()->{
                if(this.lastFrameAnimatedOn != EMFManager.getInstance().entityRenderCount){
                    this.lastFrameAnimatedOn = EMFManager.getInstance().entityRenderCount;
                    System.out.println("anim= "+variant);
                    orderedAnimations.forEach((EMFAnimation::calculateAndSet));
                }
            };
//            if(variant == currentModelVariantState){
//                this.tryAnimate.setAnimation(run);
//            }
            allKnownStateVariants.get(variant).animation().setAnimation(run);
            getChildrenEMF().values().forEach((child) -> {
                if (child instanceof EMFModelPartVanilla emf) {
                    emf.receiveRootAnimationRunnable(variant,run);
                }
            });

        }
    }




    private long lastFrameAnimatedOn = -1;













}
