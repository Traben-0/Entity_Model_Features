package traben.entity_model_features.models.animation;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.utils.EMFModelPart3;

public enum EMFDefaultModelVariable {
    tx(), ty(), tz(),
    rx(true), ry(true), rz(true),
    sx(), sy(), sz(),
    visible(),
    visible_boxes(),
    CUSTOM();

    //nessecary as default valueOf doesnt work correctly
    @Nullable
    public static EMFDefaultModelVariable get(String id){
        if(id == null) return null;
        return switch (id){
            case "tx" -> tx;
            case "ty" -> ty;
            case "tz" -> tz;
            case "rx" -> rx;
            case "ry" -> ry;
            case "rz" -> rz;
            case "sx" -> sx;
            case "sy" -> sy;
            case "sz" -> sz;
            case "visible" -> visible;
            case "visible_boxes" -> visible_boxes;
            default -> null;
        };
    }

    EMFDefaultModelVariable(boolean val) {
        isRotation = val;
    }

    EMFDefaultModelVariable() {
        isRotation = false;
    }

    public final boolean isRotation;



    public float getDefaultFromModel(ModelPart modelPart) {
        if (modelPart == null) {
            System.out.println("model part was null cannot get its default value");
            return 0;
        }
        ModelTransform transform = modelPart.getDefaultTransform();
        switch (this) {
            case tx -> {
                return transform.pivotX;
            }
            case ty -> {
                return transform.pivotY;
            }
            case tz -> {
                return transform.pivotZ;
            }
            case rx -> {
                return transform.pitch;
            }
            case ry -> {
                return transform.yaw;
            }
            case rz -> {
                return transform.roll;
            }
            case sx -> {
                return modelPart.xScale;
            }
            case sy -> {
                return modelPart.yScale;
            }
            case sz -> {
                return modelPart.zScale;
            }
            case visible -> {
                return modelPart.visible ? 1 : 0;
            }
            case visible_boxes -> {
                return modelPart.hidden ? 0 : 1;
            }
            default -> {
                System.out.println("model variable was defaulted cannot get its default value");
                return 0;
            }
        }
    }

    public float getFrom3Model(EMFModelPart3 modelPart, EMFModelPart3 sourceModel) {
        if (modelPart == null) {
            System.out.println("model part was null cannot get its value");
            return 0;
        }

        float[] parentModify;
            if( modelPart.selfModelData != null){
                parentModify = modelPart.selfModelData.parentModified;
            }else{
                parentModify = new float[]{0, 0, 0};
            }
       // ModelTransform defaults = modelPart.vanillaTransform == null? ModelTransform.NONE : modelPart.vanillaTransform;
        switch (this) {
            case tx -> {
                return modelPart.pivotX - parentModify[0];
            }
            case ty -> {
                return modelPart.pivotY - parentModify[1];
            }
            case tz -> {
                return modelPart.pivotZ - parentModify[2];
            }
            case rx -> {
                return modelPart.pitch;// -defaults.pitch;
            }
            case ry -> {
                return modelPart.yaw ;// - defaults.yaw;
            }
            case rz -> {
                return modelPart.roll ;// - defaults.roll;
            }
            case sx -> {
                return modelPart.xScale;
            }
            case sy -> {
                return modelPart.yScale;
            }
            case sz -> {
                return modelPart.zScale;
            }
            case visible
                    -> {//todo
                return modelPart.visible ? 1 : 0;
            }
            case visible_boxes -> {//todo
                return modelPart.hidden ? 0 : 1;
            }
            default -> {
                System.out.println("model variable was defaulted cannot get its value");
                return 0;
            }
        }
    }
        public void setValueIn3Model(EMFModelPart3 modelPart, float value) {
            if(modelPart == null){
                System.out.println("model part was null cannot set its value");
                return;
            }
            float[] parentModify;
            if( modelPart.selfModelData != null){
                parentModify = modelPart.selfModelData.parentModified;
            }else{
                parentModify = new float[]{0, 0, 0};
            }

            //ModelTransform defaults = modelPart.vanillaTransform == null? ModelTransform.NONE : modelPart.vanillaTransform;
            switch (this){
                case tx -> {
                     modelPart.pivotX = value + parentModify[0];
                }
                case ty -> {
                     modelPart.pivotY = value + parentModify[1];
                }
                case tz -> {
                     modelPart.pivotZ = value + parentModify[2];
                }
                case rx -> {
                     modelPart.pitch = value;// + defaults.pitch;
                }
                case ry -> {
                     modelPart.yaw = value ;// + defaults.yaw;
                }
                case rz -> {
                     modelPart.roll = value ;// + defaults.roll;
                }
                case sx -> {
                     modelPart.xScale = value;
                }
                case sy -> {
                     modelPart.yScale = value;
                }
                case sz -> {
                     modelPart.zScale = value;
                }
                case visible -> {
                    modelPart.visible = value == 1;
                    //System.out.println("1");
                }
                case visible_boxes -> {//todo check correct
                    modelPart.hidden = value != 1;
                    //System.out.println("2");
                }
                default -> {
                    System.out.println("model variable was defaulted cannot set its value");
                }
            }
        }
}
