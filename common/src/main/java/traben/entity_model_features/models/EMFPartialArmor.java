package traben.entity_model_features.models;
#if MC >= MC_21_2
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFUtils;
#endif

public class EMFPartialArmor {

    #if MC >= MC_21_2
    private static final ModelLayerLocation helmLoc = new ModelLayerLocation(EMFUtils.res("minecraft", "helmet"), "main");
    private static final ModelLayerLocation chestLoc = new ModelLayerLocation(EMFUtils.res("minecraft", "chestplate"), "main");
    private static final ModelLayerLocation legLoc = new ModelLayerLocation(EMFUtils.res("minecraft", "leggings"), "main");
    private static final ModelLayerLocation bootLoc = new ModelLayerLocation(EMFUtils.res("minecraft", "boots"), "main");
    
    private static final ModelLayerLocation helmLoc_baby = new ModelLayerLocation(EMFUtils.res("minecraft", "helmet"), "baby");
    private static final ModelLayerLocation chestLoc_baby = new ModelLayerLocation(EMFUtils.res("minecraft", "chestplate"), "baby");
    private static final ModelLayerLocation legLoc_baby = new ModelLayerLocation(EMFUtils.res("minecraft", "leggings"), "baby");
    private static final ModelLayerLocation bootLoc_baby = new ModelLayerLocation(EMFUtils.res("minecraft", "boots"), "baby");
    
    public ModelPart helmetModel = null;
    public ModelPart chestplateModel = null;
    public ModelPart leggingsModel = null;
    public ModelPart bootsModel = null;
    
    public ModelPart helmetModel_baby = null;
    public ModelPart chestplateModel_baby = null;
    public ModelPart leggingsModel_baby = null;
    public ModelPart bootsModel_baby = null;

    EMFPartialArmor() {
        try {
            var outer = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.0F)), 64, 32).bakeRoot();
            var inner = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(0.5F)), 64, 32).bakeRoot();

            var possibleHelm = EMFManager.getInstance().injectIntoModelRootGetter(helmLoc, outer);
            var possibleChest = EMFManager.getInstance().injectIntoModelRootGetter(chestLoc, outer);
            var possibleLeg = EMFManager.getInstance().injectIntoModelRootGetter(legLoc, inner);
            var possibleBoot = EMFManager.getInstance().injectIntoModelRootGetter(bootLoc, outer);

            var possibleHelm_baby = EMFManager.getInstance().injectIntoModelRootGetter(helmLoc_baby, outer);
            var possibleChest_baby = EMFManager.getInstance().injectIntoModelRootGetter(chestLoc_baby, outer);
            var possibleLeg_baby = EMFManager.getInstance().injectIntoModelRootGetter(legLoc_baby, inner);
            var possibleBoot_baby = EMFManager.getInstance().injectIntoModelRootGetter(bootLoc_baby, outer);

            if (possibleHelm instanceof EMFModelPartRoot) {
                helmetModel = possibleHelm;
                hasCustom = true;
            }
            if (possibleChest instanceof EMFModelPartRoot) {
                chestplateModel = possibleChest;
                hasCustom = true;
            }
            if (possibleLeg instanceof EMFModelPartRoot) {
                leggingsModel = possibleLeg;
                hasCustom = true;
            }
            if (possibleBoot instanceof EMFModelPartRoot) {
                bootsModel = possibleBoot;
                hasCustom = true;
            }
            
            if (possibleHelm_baby instanceof EMFModelPartRoot) {
                helmetModel_baby = possibleHelm_baby;
                hasCustom = true;
            }
            if (possibleChest_baby instanceof EMFModelPartRoot) {
                chestplateModel_baby = possibleChest_baby;
                hasCustom = true;
            }
            if (possibleLeg_baby instanceof EMFModelPartRoot) {
                leggingsModel_baby = possibleLeg_baby;
                hasCustom = true;
            }
            if (possibleBoot_baby instanceof EMFModelPartRoot) {
                bootsModel_baby = possibleBoot_baby;
                hasCustom = true;
            }
            
        }catch (Exception e) {
            EMFManager.getInstance().receiveException(e);
        }
    }
    #endif

    private boolean hasCustom = false;

    public boolean hasCustom(){
        return hasCustom;
    }
}
