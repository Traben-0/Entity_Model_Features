package traben.entity_model_features.models.animation.math.variables;

import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public enum EMFModelOrRenderVariable {
    TX() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.x = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.x;
        }
    },
    TY() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.y = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.y;
        }
    },
    TZ() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.z = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.z;
        }
    },
    RX() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.xRot = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.xRot;
        }
    },
    RY() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.yRot = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.yRot;
        }
    },
    RZ() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.zRot = value;
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return modelPart.zRot;
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
            modelPart.visible = MathValue.toBoolean(value);
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return MathValue.fromBoolean(modelPart.visible);
        }

        @Override
        public boolean isBoolean() {
            return true;
        }
    },
    VISIBLE_BOXES() {
        @Override
        public void setValue(EMFModelPart modelPart, float value) {
            if (modelPart == null) return;
            modelPart.skipDraw = !MathValue.toBoolean(value);
        }

        @Override
        public float getValue(ModelPart modelPart) {
            if (modelPart == null) return 0;
            return MathValue.fromBoolean(!modelPart.skipDraw);
        }

        @Override
        public boolean isBoolean() {
            return true;
        }
    },
    RENDER_shadow_size() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setShadowSize(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.shadowSize() : 0;
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_SHADOW_OPACITY() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setShadowOpacity(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.shadowOpacity() : 0;
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_SHADOW_X() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setShadowX(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.shadowX() : 0;
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_SHADOW_Z() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setShadowZ(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.shadowZ() : 0;
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },

    RENDER_LEASH_X() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_x", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_x", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Y() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_y", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_y", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Z() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_z", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_z", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_LEASH_X_1() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_x_1", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_x_1", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Y_1() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_y_1", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_y_1", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Z_1() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_z_1", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_z_1", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_LEASH_X_2() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_x_2", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_x_2", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Y_2() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_y_2", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_y_2", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Z_2() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_z_2", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_z_2", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_LEASH_X_3() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_x_3", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_x_3", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Y_3() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_y_3", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_y_3", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Z_3() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_z_3", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_z_3", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_LEASH_X_4() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_x_4", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_x_4", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Y_4() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_y_4", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_y_4", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_LEASH_Z_4() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_offset_z_4", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_offset_z_4", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_HOLDER_LEASH_X() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_x", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_x", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Y() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_y", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_y", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Z() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_z", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_z", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_HOLDER_LEASH_X_1() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_x_1", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_x_1", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Y_1() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_y_1", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_y_1", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Z_1() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_z_1", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_z_1", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_HOLDER_LEASH_X_2() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_x_2", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_x_2", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Y_2() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_y_2", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_y_2", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Z_2() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_z_2", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_z_2", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_HOLDER_LEASH_X_3() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_x_3", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_x_3", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Y_3() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_y_3", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_y_3", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Z_3() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_z_3", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_z_3", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_HOLDER_LEASH_X_4() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_x_4", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_x_4", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Y_4() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_y_4", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_y_4", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },
    RENDER_HOLDER_LEASH_Z_4() {
        @Override public void setValue(EMFModelPart ignored, float value) {var state = EMFState.state();if (state != null) state.variableMap().put("render.leash_holder_offset_z_4", value);}
        @Override public float getValue(ModelPart ignored) {var state = EMFState.state();return state != null ? state.variableMap().getOrDefault("render.leash_holder_offset_z_4", 0f) : 0;}
        @Override public boolean isRenderVariable() {return true;}
    },

    RENDER_FIRE_X() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setFireX(value);
        }

        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.fireX() : 0;
        }

        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_FIRE_Y() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setFireY(value);
        }
        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.fireY() : 0;
        }
        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_FIRE_Z() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setFireZ(value);
        }
        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.fireZ() : 0;
        }
        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_FIRE_SCALE() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setFireScale(value);
        }
        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.fireScale() : 0;
        }
        @Override
        public boolean isRenderVariable() {
            return true;
        }
    },
    RENDER_FIRE_HEIGHT() {
        @Override
        public void setValue(EMFModelPart ignored, float value) {
            var state = EMFState.state();
            if (state != null) state.setFireHeight(value);
        }
        @Override
        public float getValue(ModelPart ignored) {
            var state = EMFState.state();
            return state != null ? state.fireHeight() : 0;
        }
        @Override
        public boolean isRenderVariable() {
            return true;
        }
    }
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
            case "render.leash_offset_x_1" -> RENDER_LEASH_X_1;
            case "render.leash_offset_y_1" -> RENDER_LEASH_Y_1;
            case "render.leash_offset_z_1" -> RENDER_LEASH_Z_1;
            case "render.leash_offset_x_2" -> RENDER_LEASH_X_2;
            case "render.leash_offset_y_2" -> RENDER_LEASH_Y_2;
            case "render.leash_offset_z_2" -> RENDER_LEASH_Z_2;
            case "render.leash_offset_x_3" -> RENDER_LEASH_X_3;
            case "render.leash_offset_y_3" -> RENDER_LEASH_Y_3;
            case "render.leash_offset_z_3" -> RENDER_LEASH_Z_3;
            case "render.leash_offset_x_4" -> RENDER_LEASH_X_4;
            case "render.leash_offset_y_4" -> RENDER_LEASH_Y_4;
            case "render.leash_offset_z_4" -> RENDER_LEASH_Z_4;


            case "render.leash_holder_offset_x" -> RENDER_HOLDER_LEASH_X;
            case "render.leash_holder_offset_y" -> RENDER_HOLDER_LEASH_Y;
            case "render.leash_holder_offset_z" -> RENDER_HOLDER_LEASH_Z;
            case "render.leash_holder_offset_x_1" -> RENDER_HOLDER_LEASH_X_1;
            case "render.leash_holder_offset_y_1" -> RENDER_HOLDER_LEASH_Y_1;
            case "render.leash_holder_offset_z_1" -> RENDER_HOLDER_LEASH_Z_1;
            case "render.leash_holder_offset_x_2" -> RENDER_HOLDER_LEASH_X_2;
            case "render.leash_holder_offset_y_2" -> RENDER_HOLDER_LEASH_Y_2;
            case "render.leash_holder_offset_z_2" -> RENDER_HOLDER_LEASH_Z_2;
            case "render.leash_holder_offset_x_3" -> RENDER_HOLDER_LEASH_X_3;
            case "render.leash_holder_offset_y_3" -> RENDER_HOLDER_LEASH_Y_3;
            case "render.leash_holder_offset_z_3" -> RENDER_HOLDER_LEASH_Z_3;
            case "render.leash_holder_offset_x_4" -> RENDER_HOLDER_LEASH_X_4;
            case "render.leash_holder_offset_y_4" -> RENDER_HOLDER_LEASH_Y_4;
            case "render.leash_holder_offset_z_4" -> RENDER_HOLDER_LEASH_Z_4;

            case "render.fire_x" -> RENDER_FIRE_X;
            case "render.fire_y" -> RENDER_FIRE_Y;
            case "render.fire_z" -> RENDER_FIRE_Z;
            case "render.fire_scale" -> RENDER_FIRE_SCALE;
            case "render.fire_height" -> RENDER_FIRE_HEIGHT;
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

    public boolean isBoolean() {
        return false;
    }

    public abstract float getValue(ModelPart modelPart);
    public float getValue() {
        return getValue(null);
    }


    public abstract void setValue(EMFModelPart modelPart, float value);

}
