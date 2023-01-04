package traben.entity_model_features.models;


import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import org.joml.*;
import traben.entity_model_features.utils.AnimationValues;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.models.jemJsonObjects.EMF_BoxData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;

import java.lang.Math;
import java.util.*;

@Environment(value = EnvType.CLIENT)
public class EMF_CustomModelPart<T extends Entity> extends ModelPart  {

    //todo probably needs parent offset but could be calculated before render




    private final List<EMF_CustomModelPart.Cuboid> cuboids = new ArrayList<>();
    private final Map<String, EMF_CustomModelPart<T>> children = new HashMap<>();


    public final EMF_ModelData selfModelData;
    public final ArrayList<EMF_ModelData> parentModelData;

    public Double rx, ry, rz;
    public Double tx, ty, tz;
    public Double sx, sy, sz;
    public boolean visible_boxes = true;
    public boolean visible = true;



    public void render( int parentCount,HashMap<String, ModelPart> vanillaParts, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        //matrices.scale(5,2,5);

        //boolean skip = false;
        //System.out.println("parent check for base model" + selfModelData.baseId + vanillaParts.containsKey(selfModelData.baseId));
        //if(vanillaParts.containsKey(selfModelData.baseId)) {
        //       copyTransform(vanillaParts.get(selfModelData.baseId));
        //}

        float translateX;
        float translateY;
        float translateZ;
        if (tx != null && ty != null && tz != null) {// && !cuboids.isEmpty()) {
            //System.out.println("was translated");
            translateZ = ( (tz.floatValue() ) / 16.0f);
            translateX = ( (tx.floatValue() ) / 16.0f);
            translateY = ( (ty.floatValue() ) / 16.0f);
        }else{
            translateZ = ( ( selfModelData.translate[2]) / 16.0f);
            translateX = ( ( selfModelData.translate[0]) / 16.0f);
            translateY = ( ( selfModelData.translate[1]) / 16.0f);
        }

        float rotateX;
        float rotateY;
        float rotateZ;

        if (rx != null && ry != null && rz != null) {// && !cuboids.isEmpty()) {

           // System.out.println("was rotated");
            rotateZ = rz.floatValue() +((float) Math.toRadians( selfModelData.rotate[2]));
            rotateX = -rx.floatValue() +((float) Math.toRadians( selfModelData.rotate[0]));
            rotateY = ry.floatValue() +((float) Math.toRadians( selfModelData.rotate[1]));


        }else if (vanillaParts.containsKey(selfModelData.part)) {// && !cuboids.isEmpty()) {
            ModelPart vanilla = vanillaParts.get(selfModelData.part);
            //System.out.println("head is null? "+ vanilla == null);
            //copyTransform(vanilla);
            rotateZ = vanilla.roll +((float) Math.toRadians( selfModelData.rotate[2]));
            rotateX = -vanilla.pitch+((float) Math.toRadians( selfModelData.rotate[0]));
            rotateY = vanilla.yaw+((float) Math.toRadians( selfModelData.rotate[1]));
            //System.out.println("rotate="+vanilla +", "+ vanilla.roll+", "+vanilla.pitch+", "+vanilla.yaw);

        }else{
           // System.out.println("head fail");
            rotateZ = (float) Math.toRadians( selfModelData.rotate[2]);
            rotateX = (float) Math.toRadians( selfModelData.rotate[0]);
            rotateY = (float) Math.toRadians( selfModelData.rotate[1]);
        }
        //rotateZ = 2;
        //todo remove after animation support
        if (selfModelData.id.equals("baby_head")) {
            visible = false;
        }
//  todo testing head rotation
//        if (selfModelData.id.equals("head2") && vanillaParts.containsKey("leg1")) {
//            ModelPart vanilla = vanillaParts.get("leg1");
//            //copyTransform(vanilla);
//            rotateZ = vanilla.roll +(selfModelData.rotate[2]*0.01745329251f);
//            rotateX = -vanilla.pitch+(selfModelData.rotate[0]*0.01745329251f);
//            rotateY = vanilla.yaw+(selfModelData.rotate[1]*0.01745329251f);
//        }

        matrices.push();
        if (visible) {

            //translate affects children

            //TODO RETURN TO
            // matrices.scale(selfModelData.scale, selfModelData.scale, selfModelData.scale);
            matrices.push();
            //if(parentCount == 0)
            if(selfModelData.part != null){
                matrices.translate(translateX , translateY , -translateZ );
            }else{
                matrices.translate(-translateX , -translateY , translateZ );
            }
//            if(selfModelData.id.equals("mirrored"))
//                matrices.translate(0, 8 / 16.0f, 0);
            //rotate only for this
           // if(!cuboids.isEmpty())

            rotate(matrices,rotateX,rotateY,rotateZ);


            //matrices.scale(-1,-1,-1);
            for (Cuboid cube :
                    cuboids) {
                //hide leg 1 for testing
                //if(!"leg1".equals(this.selfModelData.part))
                    cube.renderCuboid(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);

            }
            matrices.pop();

           // if(parentModelData.size() < 1){
                //remove first models translate
                //matrices.translate(-(selfModelData.translate[0] / 16.0f), -(selfModelData.translate[1] / 16.0f), -(selfModelData.translate[2] / 16.0f));
           // }else{
           // somethign with these or cuboid creation affetcing pivots...
            //if(!cuboids.isEmpty())
            //if(parentCount == 0)
            if(parentCount != 0 ){//&& !selfModelData.id.equals("arms")) {
                if (selfModelData.part != null) {
                    matrices.translate(translateX , translateY , -translateZ );
                } else {
                    matrices.translate(-translateX , -translateY , translateZ );
                }
            }

           // matrices.translate(selfModelData.translate[0] / 16.0f, selfModelData.translate[1] / 16.0f, selfModelData.translate[2] / -16.0f);
           // }
            //if(selfModelData.id.equals("arms"))
            if(parentCount == 0 && selfModelData.part != null)
                matrices.translate(translateX , translateY , -translateZ );
            if(!cuboids.isEmpty())// && !selfModelData.id.equals("arms"))
            //if(parentCount != 0 || selfModelData.part == null)
            //if(selfModelData.part != null)
                rotate(matrices,rotateX,rotateY,rotateZ);
           // if(selfModelData.id.equals("arms"))
            if(parentCount == 0 && selfModelData.part != null)
                matrices.translate(-translateX , -translateY , translateZ );
//            if(selfModelData.id.equals("arms"))
//                matrices.translate(selfModelData.translate[0] / 16.0f, selfModelData.translate[1] / -16.0f, selfModelData.translate[2] / 16.0f);

            for (String key :
                    children.keySet()) {
                children.get(key).render(parentCount + 1, vanillaParts, matrices, vertices, light, overlay, red, green, blue, alpha);
            }

        }
        matrices.pop();
    }


//    private double constrainrotationTo180(double given) {
//        double adjust = 1;// 1.57079632679f
//        if (given >= 360*adjust) {
//            given = given % (360*adjust);
//            if (given >= 180*adjust) {
//                given -= 360*adjust;
//            }
//        }
//        if (given < 0) {
//            given = given % (-360*adjust);
//            if (given <= -180*adjust) {
//                given += 360*adjust;
//            }
//        }
//        return given;
//    }


    public EMF_CustomModelPart(int parentNumber,
                               EMF_ModelData EMFmodelData,
                               ArrayList<EMF_ModelData> parentEMFmodelData){//,//float[] parentalTransforms) {

        super(new ArrayList<>(), new HashMap<>());
        selfModelData = EMFmodelData;
        parentModelData = parentEMFmodelData;

        pivotX = 0;// selfModelData.translate[0];
        pivotY = 0;// selfModelData.translate[1];
        pivotZ = 0;// selfModelData.translate[2];

        boolean[] invertFirst = new boolean[]{true,true,false};
//        if(parentModelData.size() == 0){
//            //invert y & z for some reason for first model
//            selfModelData.translate[0] =0;// -selfModelData.translate[0];
//            selfModelData.translate[1] =0;// -selfModelData.translate[1];
//            selfModelData.translate[2] =0;// -selfModelData.translate[2]; // forward
//
//        }
//        if(selfModelData.boxes.length > 0)
//            parentalTransforms = new float[]{
//                parentalTransforms[0] + selfModelData.translate[0],
//                parentalTransforms[1] + selfModelData.translate[1],
//                parentalTransforms[2] + selfModelData.translate[2]};

        //might be either any model with a vanilla part or any top layer model
        boolean removePivotValue =  parentNumber == 0;

        //if (selfModelData.id.equals("mirrored")){ removePivotValue = false; invertFirst =new boolean[]{false,false,false};}
        createCuboidsFromBoxData(invertFirst,removePivotValue);
        System.out.println("data = " + selfModelData.toString(false));
        for (EMF_ModelData sub : selfModelData.submodels) {
            ArrayList<EMF_ModelData> hold = new ArrayList<>(parentEMFmodelData);
            hold.add(selfModelData);
            children.put(sub.id, new EMF_CustomModelPart<T>(parentNumber + 1, sub, hold));
        }
    }


    private void createCuboidsFromBoxData(boolean[] invertAxis, boolean removePivotValue) {
        if (selfModelData.boxes.length > 0) {
            try {
                for (EMF_BoxData box :
                        selfModelData.boxes) {
                    Cuboid cube;
                    if (box.textureOffset.length == 2) {

                        //create super easy texture offset box
                        //todo ensure matches below
//                        cube = new Cuboid(selfModelData,
//                                box.textureOffset[0], box.textureOffset[1],
//                                box.coordinates[0], box.coordinates[1], box.coordinates[2],
//                                box.coordinates[3], box.coordinates[4], box.coordinates[5],
//                                box.sizeAdd, box.sizeAdd, box.sizeAdd,
//                                new boolean[]{selfModelData.mirrorTexture.contains("u"), selfModelData.mirrorTexture.contains("v")},
//                                selfModelData.textureSize[0], selfModelData.textureSize[1], "");//selfModelData.invertAxis);
                        cube=null;
                    } else {
                        //create annoying custom uv box
                        cube = new Cuboid(selfModelData,
                                box.uvDown, box.uvUp, box.uvNorth,
                                box.uvSouth, box.uvWest, box.uvEast,
                                -box.coordinates[0]-box.coordinates[3], -box.coordinates[1]-box.coordinates[4], box.coordinates[2],
                                box.coordinates[3], box.coordinates[4], box.coordinates[5],
                                box.sizeAdd, box.sizeAdd, box.sizeAdd,
                                new boolean[]{selfModelData.mirrorTexture.contains("u"), selfModelData.mirrorTexture.contains("v")},
                                selfModelData.textureSize[0], selfModelData.textureSize[1], invertAxis, removePivotValue);//selfModelData.invertAxis);
                    }
                    cuboids.add(cube);
                }

            } catch (Exception e) {
                EMFUtils.EMF_modMessage("cuboid construction broke: " + e, false);

            }
        }

    }

    public void rotate(MatrixStack matrices,float rotateX,float rotateY,float rotateZ) {
        //matrices.translate((this.pivotX+adjustPivotX) / 16.0f, (this.pivotY+adjustPivotY) / 16.0f, (this.pivotZ+adjustPivotZ) / 16.0f);
        //matrices.translate(selfModelData.[0] / -16.0f, selfModelData.translate[1] / -16.0f, selfModelData.translate[2] / 16.0f);

        ///////////////matrices.translate((double)(this.pivotX / 16.0F), (double)(this.pivotY / 16.0F), (double)(this.pivotZ / 16.0F));

//        System.out.println("id="+selfModelData.id);
//        System.out.println("roll="+this.roll+"-"+selfModelData.rotate[2]);
//        System.out.println("yaw="+this.yaw+"-"+selfModelData.rotate[1]);
//        System.out.println("pitch="+this.pitch+"-"+selfModelData.rotate[0]);


//                [STDOUT]: id=right_ear
//                [STDOUT]: roll=-94.24778--47.12389
//                [STDOUT]: yaw=-47.12389-0.0
//                [STDOUT]: pitch=0.0--94.24778

//                [STDOUT]: id=right_ear
//                [STDOUT]: roll=-94.24778--47.12389
//                [STDOUT]: yaw=-47.12389-0.0
//                [STDOUT]: pitch=0.0--94.24778

        //this number below is so important, it will take the rotations in degrees and turn it into what the multiply method wants
//// number for degrees to rotation for quart     0.01745329251
        //matrices.translate(selfModelData.translate[0] / 16.0f, selfModelData.translate[1] / 16.0f, selfModelData.translate[2] / -16.0f);
//        if (/*constrainrotationTo180(this.roll+selfModelData.rotate[2]*/ rotateZ != 0.0f) {
//            matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(/*this.roll+*/rotateZ));
//        }
//        if (/*constrainrotationTo180(this.yaw+selfModelData.rotate[1]*/ rotateY != 0.0f) {
//            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(/*this.yaw+*/rotateY));
//        }
//
//        //this is inverted for some reason
//        if (/*constrainrotationTo180(this.pitch+selfModelData.rotate[0]*/ rotateX != 0.0f) {
//            matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(/*this.pitch+*/-rotateX));
//        }

        if (rotateZ != 0.0F || rotateY != 0.0F || rotateX != 0.0F) {
            matrices.multiply((new Quaternionf()).rotationZYX(rotateZ, rotateY, -rotateX));
        }

    }


    @FunctionalInterface
    @Environment(value = EnvType.CLIENT)
    public static interface CuboidConsumer {
        public void accept(MatrixStack.Entry var1, String var2, int var3, EMF_CustomModelPart.Cuboid var4);
    }

    @Environment(value = EnvType.CLIENT)
    public static class Cuboid {
        private final EMF_CustomModelPart.Quad[] sides;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        //cuboid without custom UVs
        public Cuboid(EMF_ModelData selfModelData
                , int textureU, int textureV,
                      float cubeX, float cubeY, float cubeZ,
                      float sizeX, float sizeY, float sizeZ,
                      float extraX, float extraY, float extraZ,
                      boolean[] mirrorUV,
                      float textureWidth, float textureHeight,
                      String axisToInvertXYZ) {
            if (axisToInvertXYZ.toLowerCase().contains("x")) {
                cubeX = -cubeX;
                sizeX = -sizeX;
                extraX = -extraX;
            }
            if (axisToInvertXYZ.toLowerCase().contains("y")) {
                cubeY = -cubeY;
                sizeY = -sizeY;
                extraY = -extraY;
            }
            if (axisToInvertXYZ.toLowerCase().contains("z")) {
                cubeZ = -cubeZ;
                sizeZ = -sizeZ;
                extraZ = -extraZ;
            }
            //cubeX += selfModelData.translate[0];
            //cubeY += selfModelData.translate[1];
            //cubeZ += selfModelData.translate[2];

            this.minX = cubeX;
            this.minY = cubeY;
            this.minZ = cubeZ;
            this.maxX = cubeX + sizeX;
            this.maxY = cubeY + sizeY;
            this.maxZ = cubeZ + sizeZ;
            this.sides = new EMF_CustomModelPart.Quad[6];
            float cubeX2 = cubeX + sizeX;
            float cubeY2 = cubeY + sizeY;
            float cubeZ2 = cubeZ + sizeZ;
            cubeX -= extraX;
            cubeY -= extraY;
            cubeZ -= extraZ;
            cubeX2 += extraX;
            cubeY2 += extraY;
            cubeZ2 += extraZ;
            if (mirrorUV[0]) {
                float i = cubeX2;
                cubeX2 = cubeX;
                cubeX = i;
            }
            if (mirrorUV[1]) {
                float i = cubeY2;
                cubeY2 = cubeY;
                cubeY = i;
            }
            EMF_CustomModelPart.Vertex vertex = new EMF_CustomModelPart.Vertex(cubeX, cubeY, cubeZ, 0.0f, 0.0f);
            EMF_CustomModelPart.Vertex vertex2 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY, cubeZ, 0.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex3 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY2, cubeZ, 8.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex4 = new EMF_CustomModelPart.Vertex(cubeX, cubeY2, cubeZ, 8.0f, 0.0f);
            EMF_CustomModelPart.Vertex vertex5 = new EMF_CustomModelPart.Vertex(cubeX, cubeY, cubeZ2, 0.0f, 0.0f);
            EMF_CustomModelPart.Vertex vertex6 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY, cubeZ2, 0.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex7 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY2, cubeZ2, 8.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex8 = new EMF_CustomModelPart.Vertex(cubeX, cubeY2, cubeZ2, 8.0f, 0.0f);
            float j = textureU;
            float k = (float) textureU + sizeZ;
            float l = (float) textureU + sizeZ + sizeX;
            float m = (float) textureU + sizeZ + sizeX + sizeX;
            float n = (float) textureU + sizeZ + sizeX + sizeZ;
            float o = (float) textureU + sizeZ + sizeX + sizeZ + sizeX;
            float p = textureV;
            float q = (float) textureV + sizeZ;
            float r = (float) textureV + sizeZ + sizeY;
            this.sides[2] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirrorUV, Direction.DOWN);
            this.sides[3] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirrorUV, Direction.UP);
            this.sides[1] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirrorUV, Direction.WEST);
            this.sides[4] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirrorUV, Direction.NORTH);
            this.sides[0] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirrorUV, Direction.EAST);
            this.sides[5] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirrorUV, Direction.SOUTH);
        }

        //Cuboid with custom UVs
        public Cuboid(EMF_ModelData selfModelData,
                      int[] uvDown, int[] uvUp, int[] uvNorth, int[] uvSouth, int[] uvWest, int[] uvEast,
                      float cubeX, float cubeY, float cubeZ,
                      float sizeX, float sizeY, float sizeZ,
                      float extraX, float extraY, float extraZ,
                      boolean[] mirrorUV,
                      float textureWidth, float textureHeight,
                      boolean[] invertAxis,
                      boolean removePivotValue) {
           // this should not be seperate
             //       maybe more inversion needs to happen as translates of ++- work better
            if(removePivotValue) {
                if (invertAxis[0]) {
//                cubeX = -cubeX;
//                sizeX = -sizeX;
//                extraX = -extraX;
                    cubeX -= selfModelData.translate[0];
                } else {
                    cubeX += selfModelData.translate[0];
                }
                if (invertAxis[1]) {
//                cubeY = -cubeY;
//                sizeY = -sizeY;
//                extraY = -extraY;
                    cubeY -= selfModelData.translate[1];
                } else {
                    cubeY += selfModelData.translate[1];
                }
                if (invertAxis[2]) {
//                cubeZ = -cubeZ;
//                sizeZ = -sizeZ;
//                extraZ = -extraZ;
                    cubeZ -= selfModelData.translate[2];
                } else {
                    cubeZ += selfModelData.translate[2];
                }
            }




            this.minX = cubeX;
            this.minY = cubeY;
            this.minZ = cubeZ;
            this.maxX = cubeX + sizeX;
            this.maxY = cubeY + sizeY;
            this.maxZ = cubeZ + sizeZ;
            this.sides = new EMF_CustomModelPart.Quad[6];
            float cubeX2 = cubeX + sizeX;
            float cubeY2 = cubeY + sizeY;
            float cubeZ2 = cubeZ + sizeZ;
            cubeX -= extraX;
            cubeY -= extraY;
            cubeZ -= extraZ;
            cubeX2 += extraX;
            cubeY2 += extraY;
            cubeZ2 += extraZ;
//todo might be right but must change vertex facing
//            if (mirrorUV[0]) {
//                float i = cubeX2;
//                cubeX2 = cubeX;
//                cubeX = i;
//            }
//            if (mirrorUV[1]) {
//                float i = cubeY2;
//                cubeY2 = cubeY;
//                cubeY = i;
//            }
            EMF_CustomModelPart.Vertex vertex = new EMF_CustomModelPart.Vertex(cubeX, cubeY, cubeZ, 0.0f, 0.0f);
            EMF_CustomModelPart.Vertex vertex2 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY, cubeZ, 0.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex3 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY2, cubeZ, 8.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex4 = new EMF_CustomModelPart.Vertex(cubeX, cubeY2, cubeZ, 8.0f, 0.0f);
            EMF_CustomModelPart.Vertex vertex5 = new EMF_CustomModelPart.Vertex(cubeX, cubeY, cubeZ2, 0.0f, 0.0f);
            EMF_CustomModelPart.Vertex vertex6 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY, cubeZ2, 0.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex7 = new EMF_CustomModelPart.Vertex(cubeX2, cubeY2, cubeZ2, 8.0f, 8.0f);
            EMF_CustomModelPart.Vertex vertex8 = new EMF_CustomModelPart.Vertex(cubeX, cubeY2, cubeZ2, 8.0f, 0.0f);

            //altered custom uv quads see working out below
            //probably needs to be adjusted but thats later me problem



            //vertexes ordering format
            // 1 2
            // 4 3
            try {
                this.sides[2] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex, vertex2, vertex6, vertex5},//actually up
                        uvUp[0], uvUp[1], uvUp[2], uvUp[3], textureWidth, textureHeight, mirrorUV, Direction.DOWN);
            }catch (Exception e){
                System.out.println("uv-up failed for "+selfModelData.id);
            }
            try {
                this.sides[3] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex8, vertex7, vertex3, vertex4},//actually down
                        uvDown[0], uvDown[1], uvDown[2], uvDown[3], textureWidth, textureHeight, mirrorUV, Direction.UP);
            }catch (Exception e){
                System.out.println("uv-down failed for "+selfModelData.id);
            }
            try {
                this.sides[1] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7},
                        uvWest[0], uvWest[1], uvWest[2], uvWest[3], textureWidth, textureHeight, mirrorUV, Direction.WEST);
            }catch (Exception e){
                System.out.println("uv-west failed for "+selfModelData.id);
            }
            try {
                this.sides[4] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3},
                        uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3], textureWidth, textureHeight, mirrorUV, Direction.NORTH);
            }catch (Exception e){
                    System.out.println("uv-north failed for "+selfModelData.id);
                }
            try {
                this.sides[0] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4},
                        uvEast[0], uvEast[1], uvEast[2], uvEast[3], textureWidth, textureHeight, mirrorUV, Direction.EAST);
            }catch (Exception e){
                System.out.println("uv-east failed for "+selfModelData.id);
            }
            try {
                this.sides[5] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8},
                        uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3], textureWidth, textureHeight, mirrorUV, Direction.SOUTH);
            }catch (Exception e){
                System.out.println("uv-south failed for "+selfModelData.id);
            }


            //looks like this next block calculates model box uvs
            //needs to be replaced with custom uvs
            //using player face skin texture locations as variable names for personal visualization if I ever need to return to this
//            float XfarLeftEdge = textureU;
//            float XleftOfFaceFront = (float)textureU + sizeZ;
//            float XrightOfFaceFront = (float)textureU + sizeZ + sizeX;
//            float XrightOFUnderHeadTexture = (float)textureU + sizeZ + sizeX + sizeX;
//            float XleftOfBackHead = (float)textureU + sizeZ + sizeX + sizeZ;
//            float XFarRightEdge = (float)textureU + sizeZ + sizeX + sizeZ + sizeX;
//            float Ytopedge = textureV;
//            float YmiddleLine = (float)textureV + sizeZ;
//            float Ybottomedge = (float)textureV + sizeZ + sizeY;

            // uvUp0123
            //       uzdown0321

//            this.sides[2] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2},
//                    XleftOfFaceFront, Ytopedge, XrightOfFaceFront, YmiddleLine, textureWidth, textureHeight, mirrorUV, Direction.DOWN);
//            this.sides[3] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7},
//                    XrightOfFaceFront, YmiddleLine, XrightOFUnderHeadTexture, Ytopedge, textureWidth, textureHeight, mirrorUV, Direction.UP);
//            this.sides[1] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4},
//                    XfarLeftEdge, YmiddleLine, XleftOfFaceFront, Ybottomedge, textureWidth, textureHeight, mirrorUV, Direction.WEST);
//            this.sides[4] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3},
//                    XleftOfFaceFront, YmiddleLine, XrightOfFaceFront, Ybottomedge, textureWidth, textureHeight, mirrorUV, Direction.NORTH);
//            this.sides[0] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7},
//                    XrightOfFaceFront, YmiddleLine, XleftOfBackHead, Ybottomedge, textureWidth, textureHeight, mirrorUV, Direction.EAST);
//            this.sides[5] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8},
//                    XleftOfBackHead, YmiddleLine, XFarRightEdge, Ybottomedge, textureWidth, textureHeight, mirrorUV, Direction.SOUTH);
        }


        public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
//            Matrix4f matrix4f = entry.getPositionMatrix();
//            Matrix3f matrix3f = entry.getNormalMatrix();
//            for (EMF_CustomModelPart.Quad quad : this.sides) {
//                Vector3f vec3f = quad.direction.copy();
//                vec3f.transform(matrix3f);
//                float f = vec3f.x();
//                float g = vec3f.y();
//                float h = vec3f.z();
//                for (EMF_CustomModelPart.Vertex vertex : quad.vertices) {
//                    float i = vertex.pos.x() / 16.0f;
//                    float j = vertex.pos.y() / 16.0f;
//                    float k = vertex.pos.z() / 16.0f;
//                    Vector4f vector4f = new Vector4f(i, j, k, 1.0f);
//                    vector4f.transform(matrix4f);
//                    vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
//                }
//            }
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            EMF_CustomModelPart.Quad[] var11 = this.sides;
            int var12 = var11.length;

            for(int ij = 0; ij < var12; ++ij) {
                EMF_CustomModelPart.Quad quad = var11[ij];
                if (quad != null) {
                    Vector3f vector3f = matrix3f.transform(new Vector3f(quad.direction));
                    float f = vector3f.x();
                    float g = vector3f.y();
                    float h = vector3f.z();
                    EMF_CustomModelPart.Vertex[] var19 = quad.vertices;
                    int var20 = var19.length;

                    for (int ii = 0; ii < var20; ++ii) {
                        EMF_CustomModelPart.Vertex vertex = var19[ii];
                        float i = vertex.pos.x() / 16.0F;
                        float j = vertex.pos.y() / 16.0F;
                        float k = vertex.pos.z() / 16.0F;
                        Vector4f vector4f = matrix4f.transform(new Vector4f(i, j, k, 1.0F));
                        vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
                    }
                }
            }
        }
    }

    @Environment(value = EnvType.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public EMF_CustomModelPart.Vertex remap(float u, float v) {
            return new EMF_CustomModelPart.Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    @Environment(value = EnvType.CLIENT)
    static class Quad {
        public final EMF_CustomModelPart.Vertex[] vertices;
        public final Vector3f direction;

        public Quad(EMF_CustomModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean[] mirrorUV, Direction direction) {

            //64 x 32

//            if(squishU/2 != squishV){
//                if(squishV == squishU){
//                    squishV = squishV / 2;
//                }
//            }
           // squishV = squishV * 2;
            //System.out.println("v"+squishV);



            this.vertices = vertices;
            float f = 0.0f / squishU;
            float g = 0.0f / squishV;
            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);

//            if (true) {
//                int i = vertices.length;
//
//                for(int j = 0; j < i / 2; ++j) {
//                    EMF_CustomModelPart.Vertex vertex = vertices[j];
//                    vertices[j] = vertices[i - 1 - j];
//                    vertices[i - 1 - j] = vertex;
//                }
//            }

//            if (mirrorUV[0]) {
//                //left right invert
//                //12  >  21
//                //03  >  30
//                int i = vertices.length;
//                for (int j = 0; j < i / 2; ++j) {
//                    EMF_CustomModelPart.Vertex vertex = vertices[j];
//                    vertices[j] = vertices[i - 1 - j];
//                    vertices[i - 1 - j] = vertex;
//                }
//            }
//            if (mirrorUV[1]) {
//                //manually inverting vertical
//                //12  >  03
//                //03  >  12
//                EMF_CustomModelPart.Vertex vertex = vertices[0];
//                vertices[0] = vertices[1];
//                vertices[1] = vertex;
//                vertex = vertices[3];
//                vertices[3] = vertices[2];
//                vertices[2] = vertex;
//            }
            this.direction = direction.getUnitVector();
            if (mirrorUV[0]) {
                this.direction.mul(-1.0f, 1.0f, 1.0f);
            }
            if (mirrorUV[1]) {
                this.direction.mul(1.0f, -1.0f, 1.0f);
            }
        }
    }

    public Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<T>> getAllParts(){
        Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<T>> list = new Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<T>>();
        for (EMF_CustomModelPart<T> part :
                children.values()) {
            list.put(part.selfModelData.id,part);
            list.putAll(part.getAllParts());
        }
        return list;
    }
}
