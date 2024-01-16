package traben.entity_model_features.models.animation;

import net.minecraft.client.model.ModelPart;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.EMFModelPart;

public enum EMFModelOrRenderVariable {
    TX() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.pivotX = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.pivotX;
        }
    },
    TY() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.pivotY = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.pivotY;
        }
    },
    TZ() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.pivotZ = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.pivotZ;
        }
    },
    RX() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.pitch = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.pitch;
        }
    },
    RY() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.yaw = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.yaw;
        }
    },
    RZ() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.roll = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.roll;
        }
    },
    SX() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.xScale = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.xScale;
        }
    },
    SY() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.yScale = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.yScale;
        }
    },
    SZ() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.zScale = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.zScale;
        }
    },
    VISIBLE() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.visible = value == 1;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.visible ? 1 : 0;
        }
    },
    VISIBLE_BOXES() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.hidden = value == 1;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.hidden ? 1 : 0;
        }
    },
    RENDER_shadow_size() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setShadowSize(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getShadowSize();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_SHADOW_OPACITY() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setShadowOpacity(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getShadowOpacity();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_SHADOW_X() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setShadowX(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getShadowX();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_SHADOW_Z() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setShadowZ(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getShadowZ();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_LEASH_X() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setLeashX(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getLeashX();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_LEASH_Y() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setLeashY(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getLeashY();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_LEASH_Z() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            EMFAnimationHelper.setLeashZ(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            return EMFAnimationHelper.getLeashZ();
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    ;


    @Nullable
    public static EMFModelOrRenderVariable getRenderVariable(String id) {
        if (id == null) return null;
        return switch (id) {
            case "render.shadow_size" -> RENDER_shadow_size;
            case "render.shadow_opacity" -> RENDER_SHADOW_OPACITY;
            case "render.shadow_offset_x" -> RENDER_SHADOW_X;
            case "render.shadow_offset_z" -> RENDER_SHADOW_Z;
            case "render.leash_offset_x" -> RENDER_LEASH_X;
            case "render.leash_offset_y" -> RENDER_LEASH_Y;
            case "render.leash_offset_z" -> RENDER_LEASH_Z;
            default -> null;
        };
    }

    //nessecary as default valueOf doesnt work correctly
    @Nullable
    public static EMFModelOrRenderVariable get(String id) {
        if (id == null) return null;
        return switch (id) {
            case "tx" -> TX;
            case "ty" -> TY;
            case "tz" -> TZ;
            case "rx" -> RX;
            case "ry" -> RY;
            case "rz" -> RZ;
            case "sx" -> SX;
            case "sy" -> SY;
            case "sz" -> SZ;
            case "visible" -> VISIBLE;
            case "visible_boxes" -> VISIBLE_BOXES;
            default -> null;
        };
    }

    public boolean isRenderVariable() {
        return false;
    }

    public abstract float getValue(ModelPart modelPart);

    public float getValue() {
        return getValue(null);
    }


    abstract void setValue(EMFModelPart modelPart, float value);
}
