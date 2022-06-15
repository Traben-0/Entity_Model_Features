package traben.entity_model_features.models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import traben.entity_model_features.client.EMFUtils;
import traben.entity_model_features.models.jemJsonObjects.EMF_BoxData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static traben.entity_model_features.client.Entity_model_featuresClient.EMFConfigData;

@Environment(value = EnvType.CLIENT)
public class EMF_CustomModelPart<T extends Entity> extends ModelPart  {

    //todo probably needs parent offset but could be calculated before render


    public boolean visible = true;

    private final List<EMF_CustomModelPart.Cuboid> cuboids = new ArrayList<>();
    private final Map<String, EMF_CustomModelPart<T>> children = new HashMap<>();


    public final EMF_ModelData selfModelData;
    public final ArrayList<EMF_ModelData> parentModelData;

    public void render(int parentCount, HashMap<String, ModelPart> vanillaParts, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        //matrices.scale(5,2,5);

        //boolean skip = false;
        //System.out.println("parent check for base model" + selfModelData.baseId + vanillaParts.containsKey(selfModelData.baseId));
        //if(vanillaParts.containsKey(selfModelData.baseId)) {
        //       copyTransform(vanillaParts.get(selfModelData.baseId));
        //}

        float rotateX;
        float rotateY;
        float rotateZ;

        if (vanillaParts.containsKey(selfModelData.part)) {
            ModelPart vanilla = vanillaParts.get(selfModelData.part);
            //copyTransform(vanilla);
            rotateZ = vanilla.roll +(selfModelData.rotate[2]*0.01745329251f);
            rotateX = vanilla.pitch+(selfModelData.rotate[0]*0.01745329251f);
            rotateY = vanilla.yaw+(selfModelData.rotate[1]*0.01745329251f);


        }else{
            rotateZ = selfModelData.rotate[2]*0.01745329251f;
            rotateX = selfModelData.rotate[0]*0.01745329251f;
            rotateY = selfModelData.rotate[1]*0.01745329251f;
        }
        //todo remove after animation support
        if (selfModelData.id.equals("baby_head")) {
            visible = false;
        }

        matrices.push();
        if (visible) {

            //translate affects children

            //TODO RETURN TO
            // matrices.scale(selfModelData.scale, selfModelData.scale, selfModelData.scale);
            matrices.push();

            matrices.translate(selfModelData.translate[0] / -16.0f, selfModelData.translate[1] / -16.0f, selfModelData.translate[2] / 16.0f);
            //rotate only for this
            rotate(matrices,rotateX,rotateY,rotateZ);
            //matrices.scale(-1,-1,-1);
            for (Cuboid cube :
                    cuboids) {

                cube.renderCuboid(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);

            }
            matrices.pop();

            //if(parentModelData.size() < 1){
                //remove first models translate
                //matrices.translate(-(selfModelData.translate[0] / 16.0f), -(selfModelData.translate[1] / 16.0f), -(selfModelData.translate[2] / 16.0f));
            //}else{
                matrices.translate(selfModelData.translate[0] / -16.0f, selfModelData.translate[1] / -16.0f, selfModelData.translate[2] / 16.0f);
            //}
            rotate(matrices,rotateX,rotateY,rotateZ);
            for (String key :
                    children.keySet()) {
                children.get(key).render(parentCount + 1, vanillaParts, matrices, vertices, light, overlay, red, green, blue, alpha);
            }

        }
        matrices.pop();
    }


    private double constrainrotationTo180(double given) {
        double adjust = 1;// 1.57079632679f
        if (given >= 360*adjust) {
            given = given % (360*adjust);
            if (given >= 180*adjust) {
                given -= 360*adjust;
            }
        }
        if (given < 0) {
            given = given % (-360*adjust);
            if (given <= -180*adjust) {
                given += 360*adjust;
            }
        }
        return given;
    }


    public EMF_CustomModelPart(int parentNumber,
                               EMF_ModelData EMFmodelData,
                               ArrayList<EMF_ModelData> parentEMFmodelData) {

        super(new ArrayList<>(), new HashMap<>());
        selfModelData = EMFmodelData;
        parentModelData = parentEMFmodelData;

        pivotX = 0;// selfModelData.translate[0];
        pivotY = 0;// selfModelData.translate[1];
        pivotZ = 0;// selfModelData.translate[2];

        boolean[] invertFirst = new boolean[]{false,false,false};
        if(parentModelData.size() == 0){
            //invert y & z for some reason for first model
            selfModelData.translate[0] =0;// -selfModelData.translate[0];
            selfModelData.translate[1] =0;// -selfModelData.translate[1];
            selfModelData.translate[2] =0;// -selfModelData.translate[2]; // forward

        }


        createCuboidsFromBoxData(invertFirst);
        System.out.println("data = " + selfModelData.toString(false));
        for (EMF_ModelData sub : selfModelData.submodels) {
            ArrayList<EMF_ModelData> hold = new ArrayList<>(parentEMFmodelData);
            hold.add(selfModelData);
            children.put(sub.id, new EMF_CustomModelPart<T>(parentNumber + 1, sub, hold));
        }
    }


    private void createCuboidsFromBoxData(boolean[] invertAxis) {
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
                                selfModelData.textureSize[0], selfModelData.textureSize[1], invertAxis);//selfModelData.invertAxis);
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

        if (constrainrotationTo180(/*this.roll+*/selfModelData.rotate[2] ) != 0.0f) {
            matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(/*this.roll+*/rotateZ));
        }
        if (constrainrotationTo180(/*this.yaw+*/selfModelData.rotate[1] ) != 0.0f) {
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(/*this.yaw+*/rotateY));
        }
        if (constrainrotationTo180(/*this.pitch+*/selfModelData.rotate[0] ) != 0.0f) {
            matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(/*this.pitch+*/-rotateX));
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
                      boolean[] invertAxis) {
            if (invertAxis[0]) {
                cubeX = -cubeX;
                sizeX = -sizeX;
                extraX = -extraX;
            }
            if (invertAxis[1]) {
                cubeY = -cubeY;
                sizeY = -sizeY;
                extraY = -extraY;
            }
            if (invertAxis[2]) {
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

            //altered custom uv quads see working out below
            //probably needs to be adjusted but thats later me problem
            this.sides[2] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2},
                    uvUp[0], uvUp[1], uvUp[2], uvUp[3], textureWidth, textureHeight, mirrorUV, Direction.DOWN);
            this.sides[3] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7},
                    uvDown[0], uvDown[3], uvDown[2], uvDown[1], textureWidth, textureHeight, mirrorUV, Direction.UP);
            this.sides[1] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4},
                    uvWest[0], uvWest[1], uvWest[2], uvWest[3], textureWidth, textureHeight, mirrorUV, Direction.WEST);
            this.sides[4] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3},
                    uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3], textureWidth, textureHeight, mirrorUV, Direction.NORTH);
            this.sides[0] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7},
                    uvEast[0], uvEast[1], uvEast[2], uvEast[3], textureWidth, textureHeight, mirrorUV, Direction.EAST);
            this.sides[5] = new EMF_CustomModelPart.Quad(new EMF_CustomModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8},
                    uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3], textureWidth, textureHeight, mirrorUV, Direction.SOUTH);


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
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            for (EMF_CustomModelPart.Quad quad : this.sides) {
                Vec3f vec3f = quad.direction.copy();
                vec3f.transform(matrix3f);
                float f = vec3f.getX();
                float g = vec3f.getY();
                float h = vec3f.getZ();
                for (EMF_CustomModelPart.Vertex vertex : quad.vertices) {
                    float i = vertex.pos.getX() / 16.0f;
                    float j = vertex.pos.getY() / 16.0f;
                    float k = vertex.pos.getZ() / 16.0f;
                    Vector4f vector4f = new Vector4f(i, j, k, 1.0f);
                    vector4f.transform(matrix4f);
                    vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
                }
            }
        }
    }

    @Environment(value = EnvType.CLIENT)
    static class Vertex {
        public final Vec3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vec3f(x, y, z), u, v);
        }

        public EMF_CustomModelPart.Vertex remap(float u, float v) {
            return new EMF_CustomModelPart.Vertex(this.pos, u, v);
        }

        public Vertex(Vec3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    @Environment(value = EnvType.CLIENT)
    static class Quad {
        public final EMF_CustomModelPart.Vertex[] vertices;
        public final Vec3f direction;

        public Quad(EMF_CustomModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean[] mirrorUV, Direction direction) {
            this.vertices = vertices;
            float f = 0.0f / squishU;
            float g = 0.0f / squishV;
            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
            if (mirrorUV[0]) {
                //left right invert
                //12  >  21
                //03  >  30
                int i = vertices.length;
                for (int j = 0; j < i / 2; ++j) {
                    EMF_CustomModelPart.Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }
            if (mirrorUV[1]) {
                //manually inverting vertical
                //12  >  03
                //03  >  12
                EMF_CustomModelPart.Vertex vertex = vertices[0];
                vertices[0] = vertices[1];
                vertices[1] = vertex;
                vertex = vertices[3];
                vertices[3] = vertices[2];
                vertices[2] = vertex;
            }
            this.direction = direction.getUnitVector();
            if (mirrorUV[0]) {
                this.direction.multiplyComponentwise(-1.0f, 1.0f, 1.0f);
            }
            if (mirrorUV[1]) {
                this.direction.multiplyComponentwise(1.0f, -1.0f, 1.0f);
            }
        }
    }
}
