package traben.entity_model_features.models.animation;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import traben.entity_model_features.models.EMFModelPart;

public enum EMFDefaultModelVariable {
    tx(), ty(), tz(),
    rx(true), ry(true), rz(true),
    sx(), sy(), sz(),
    visible(),
    visible_boxes(),
    CUSTOM();

    EMFDefaultModelVariable(boolean val) {
        isRotation = val;
    }

    EMFDefaultModelVariable() {
        isRotation = false;
    }

    public final boolean isRotation;

    public void set(EMFModelPart part, Float value) {
        if (value == null) {
            System.out.println("this model couldn't be set as the calculation returned null: " + part.selfModelData.id + "." + this);
            return;
        }
        switch (this) {
            case tx -> {
                //part.tx = value;
                part.setAnimPivotX(value);
            }
            case ty -> {
                //part.ty = value;
                part.setAnimPivotY(value);
            }
            case tz -> {
                //part.tz = value;
                part.setAnimPivotZ(value);
            }
            case rx -> {
                //part.rx = value;
                //part.pitch = value;
                part.setAnimPitch(value);
            }
            case ry -> {
                //part.ry = value;
                //part.yaw = value;
                part.setAnimYaw(value);
            }
            case rz -> {
                //part.rz = value;
                // part.roll = value;
                part.setAnimRoll(value);
            }
            case sx -> {
                //part.sx = value;
                part.xScale = value;
            }
            case sy -> {
                //part.sy = value;
                part.yScale = value;
            }
            case sz -> {
                //part.sz = value;
                part.zScale = value;
            }
            case CUSTOM -> {//todo visibles
                //todo pain.jpeg
            }
        }
    }

    public void setValueAsAnimated(EMFModelPart part) {
        if (part == null) {
            System.out.println("this model couldn't be anim set as the method sent null part to " + this);
            return;
        }
        switch (this) {
            case tx -> {
                part.doesAnimtx = true;
            }
            case ty -> {
                part.doesAnimty = true;
            }
            case tz -> {
                part.doesAnimtz = true;
            }
            case rx -> {

                part.doesAnimrx = true;
            }
            case ry -> {
                part.doesAnimry = true;
            }
            case rz -> {
                part.doesAnimrz = true;
            }
            case sx -> {
                part.doesAnimsx = true;
            }
            case sy -> {
                part.doesAnimsy = true;
            }
            case sz -> {
                part.doesAnimsz = true;
            }
            default -> {//todo visibles
                //hmmm
            }
        }
    }

    public float getFromEMFModel(EMFModelPart modelPart) {
        return getFromEMFModel(modelPart, false);
    }

    public float getFromEMFModel(EMFModelPart modelPart, boolean isSibling) {
        if (modelPart == null) {
            System.out.println("EMF model part was null cannot get its value");
            return 0;
        }
        switch (this) {
            case tx -> {
                //return modelPart.tx.floatValue();
                //sibling check is required to remove parent offsets if they are from the same parent
                //todo this might actually be required on every single get call to a parent num == 1 part, i have only seen the issue on parts matching parents, check this
                return isSibling ? modelPart.getAnimPivotXSibling() : modelPart.getAnimPivotX();
            }
            case ty -> {
                //return modelPart.ty.floatValue();
                return isSibling ? modelPart.getAnimPivotYSibling() : modelPart.getAnimPivotY();
            }
            case tz -> {
                //return modelPart.tz.floatValue();
                return isSibling ? modelPart.getAnimPivotZSibling() : modelPart.getAnimPivotZ();
            }
            case rx -> {
                //return modelPart.rx.floatValue();
                return modelPart.pitch;
            }
            case ry -> {
                //return modelPart.ry.floatValue();
                return modelPart.yaw;
            }
            case rz -> {
                //return modelPart.rz.floatValue();
                return modelPart.roll;
            }
            case sx -> {
                //return modelPart.sx.floatValue();
                return modelPart.xScale;
            }
            case sy -> {
                //return modelPart.sy.floatValue();
                return modelPart.yScale;
            }
            case sz -> {
                //return modelPart.sz.floatValue();
                return modelPart.zScale;
            }
            case visible, visible_boxes -> {//todo
                //return modelPart.sz.floatValue();
                return modelPart.visible ? 1 : 0;
            }
            default -> {
                System.out.println("model variable was defaulted cannot get its value");
                return 0;
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
            case sx, sz, sy -> {
                if (modelPart instanceof EMFModelPart emf)
                    return emf.selfModelData.scale;
                else
                    return 1;
            }
            case visible, visible_boxes -> {
                return 1;//todo
            }
            default -> {
                System.out.println("model variable was defaulted cannot get its default value");
                return 0;
            }
        }
    }

    public float getFromVanillaModel(ModelPart modelPart) {
        if (modelPart == null) {
            System.out.println("model part was null cannot get its value");
            return 0;
        }
        switch (this) {
            case tx -> {
                return modelPart.pivotX;
            }
            case ty -> {
                return modelPart.pivotY;
            }
            case tz -> {
                return modelPart.pivotZ;
            }
            case rx -> {
                return modelPart.pitch;
            }
            case ry -> {
                return modelPart.yaw;
            }
            case rz -> {
                return modelPart.roll;
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
            case visible, visible_boxes -> {//todo
                return modelPart.visible ? 1 : 0;
            }
            default -> {
                System.out.println("model variable was defaulted cannot get its value");
                return 0;
            }
        }
    }
//        public void setValueInVanillaModel(ModelPart modelPart, Double value) {
//            if(modelPart == null){
//                System.out.println("model part was null cannot set its value");
//                return;
//            }
//            switch (this){
//                case tx -> {
//                     modelPart.pivotX = value.floatValue();
//                }
//                case ty -> {
//                     modelPart.pivotY = value.floatValue();
//                }
//                case tz -> {
//                     modelPart.pivotZ = value.floatValue();
//                }
//                case rx -> {
//                     modelPart.pitch = value.floatValue();
//                }
//                case ry -> {
//                     modelPart.yaw = value.floatValue();
//                }
//                case rz -> {
//                     modelPart.roll = value.floatValue();
//                }
//                case sx -> {
//                     modelPart.xScale = value.floatValue();
//                }
//                case sy -> {
//                     modelPart.yScale = value.floatValue();
//                }
//                case sz -> {
//                     modelPart.zScale = value.floatValue();
//                }
//                default -> {
//                    System.out.println("model variable was defaulted cannot set its value");
//                }
//            }
//        }
}
