package traben.entity_model_features.models.animation;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.EMFModelPart;

public enum EMFModelOrRenderVariable {
    tx(), ty(), tz(),
    rx(), ry(), rz(),
    sx(), sy(), sz(),
    visible(),
    visible_boxes(),
    RENDER_shadow_size(),
    RENDER_shadow_opacity(),
    RENDER_shadow_x(),
    RENDER_shadow_z(),
    //    RENDER_fire_x(),
//    RENDER_fire_y(),
//    RENDER_fire_z(),
    RENDER_leash_x(),
    RENDER_leash_y(),
    RENDER_leash_z(),


    ;



    @Nullable
    public static EMFModelOrRenderVariable getRenderVariable(String id) {
        if (id == null) return null;
        return switch (id) {
            case "render.shadow_size" -> RENDER_shadow_size;
            case "render.shadow_opacity" -> RENDER_shadow_opacity;
            case "render.shadow_offset_x" -> RENDER_shadow_x;
            case "render.shadow_offset_z" -> RENDER_shadow_z;
            case "render.leash_offset_x" -> RENDER_leash_x;
            case "render.leash_offset_y" -> RENDER_leash_y;
            case "render.leash_offset_z" -> RENDER_leash_z;
            default -> null;
        };
    }

    //nessecary as default valueOf doesnt work correctly
    @Nullable
    public static EMFModelOrRenderVariable get(String id) {
        if (id == null) return null;
        return switch (id) {
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

    public boolean isRenderVariable() {
        return switch (this) {
            case RENDER_leash_x, RENDER_leash_y, RENDER_leash_z, RENDER_shadow_opacity, RENDER_shadow_size, RENDER_shadow_x, RENDER_shadow_z ->
                    true;
            default -> false;
        };
    }

    public float getDefaultFromRenderVariable() {
        return switch (this) {
            //case RENDER_leash_x, RENDER_leash_z, RENDER_leash_y, RENDER_shadow_x, RENDER_shadow_z -> 0;
            case RENDER_shadow_size, RENDER_shadow_opacity -> 1;
            default -> 0f;
        };
    }

    public void trySetValue(EMFModelPart modelPart, float value) {
        if (modelPart != null) {
            setValueInMutableModel(modelPart, value);
        } else if (isRenderVariable()) {
            setRenderVariable(value);
        }

    }

    public float getRenderVariable() {
        return switch (this) {
            case RENDER_leash_x -> EMFAnimationHelper.getLeashX();
            case RENDER_shadow_z -> EMFAnimationHelper.getShadowZ();
            case RENDER_shadow_x -> EMFAnimationHelper.getShadowX();
            case RENDER_shadow_size -> EMFAnimationHelper.getShadowSize();
            case RENDER_shadow_opacity -> EMFAnimationHelper.getShadowOpacity();
            case RENDER_leash_z -> EMFAnimationHelper.getLeashZ();
            case RENDER_leash_y -> EMFAnimationHelper.getLeashY();
            default -> 0;
        };
    }

    public void setRenderVariable(float value) {
        switch (this) {
            case RENDER_leash_x -> EMFAnimationHelper.setLeashX(value);
            case RENDER_shadow_z -> EMFAnimationHelper.setShadowZ(value);
            case RENDER_shadow_x -> EMFAnimationHelper.setShadowX(value);
            case RENDER_shadow_size -> EMFAnimationHelper.setShadowSize(value);
            case RENDER_shadow_opacity -> EMFAnimationHelper.setShadowOpacity(value);
            case RENDER_leash_z -> EMFAnimationHelper.setLeashZ(value);
            case RENDER_leash_y -> EMFAnimationHelper.setLeashY(value);
            default -> {
            }
        }
    }

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

    public float getFromMutableModel(EMFModelPart modelPart//,
                                     //EMFModelPartMutable sourceModel
    ) {
        if (modelPart == null) {
            System.out.println("model part was null cannot get its value");
            return 0;
        }

        float[] parentModify;
//        if (modelPart.selfModelData != null) {
//            parentModify = modelPart.selfModelData.parentModified;
//        } else {
        parentModify = new float[]{0, 0, 0};
//        }
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
                return modelPart.yaw;// - defaults.yaw;
            }
            case rz -> {
                return modelPart.roll;// - defaults.roll;
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
            case visible -> {//todo
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

    public void setValueInMutableModel(EMFModelPart modelPart, float value) {
        if (modelPart == null) {
            System.out.println("model part was null cannot set its value");
            return;
        }
        float[] parentModify;
//        if (modelPart.selfModelData != null) {
//            parentModify = modelPart.selfModelData.parentModified;
//        } else {
        parentModify = new float[]{0, 0, 0};
//        }

        //ModelTransform defaults = modelPart.vanillaTransform == null? ModelTransform.NONE : modelPart.vanillaTransform;
        switch (this) {
            case tx -> modelPart.pivotX = value + parentModify[0];
            case ty -> modelPart.pivotY = value + parentModify[1];
            case tz -> modelPart.pivotZ = value + parentModify[2];
            case rx -> modelPart.pitch = value;// + defaults.pitch;
            case ry -> modelPart.yaw = value;// + defaults.yaw;
            case rz -> modelPart.roll = value;// + defaults.roll;
            case sx -> modelPart.xScale = value;
            case sy -> modelPart.yScale = value;
            case sz -> modelPart.zScale = value;
            case visible -> //System.out.println("1");
                    modelPart.visible = value == 1;
            case visible_boxes -> //todo check correct
                //System.out.println("2");
                    modelPart.hidden = value != 1;
            default -> System.out.println("model variable was defaulted cannot set its value");
        }
    }
}
