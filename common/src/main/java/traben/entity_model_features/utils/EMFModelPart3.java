package traben.entity_model_features.utils;


import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.mixin.accessor.CuboidAccessor;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.jem_objects.EMFBoxData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.util.*;

@Environment(value = EnvType.CLIENT)
public class EMFModelPart3 extends ModelPart  {
    public final List<EMFCuboid> emfCuboids = new ArrayList<>();
    public final Map<String, EMFModelPart3> emfChildren = new HashMap<>();


    public final EMFPartData selfModelData;


    private boolean invX = false;
    private boolean invY = false;
    private boolean invZ = false;



//    @Override
//    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
//        render(matrices,vertices,light,overlay, 1,1,1,1);
//    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //assertChildrenAndCuboids();
        //if(new Random().nextInt(100)==1) System.out.println("rendered");

        super.render(matrices, vertices, light, overlay, red, 0.0f, blue, alpha);

    }
//     final Identifier customTexture;
//    public final ModelPart vanillaPart;


    public EMFModelPart3(List<Cuboid> cuboids, Map<String, ModelPart> children){
        //create empty root model object
        super(cuboids, children);
        selfModelData = null;

    }

    private static List<Cuboid> getCuboidsFromData(EMFPartData emfPartData){
        return createCuboidsFromBoxDataV3(emfPartData);//false remove pivot value

    }
    private static Map<String, ModelPart> getChildrenFromData(EMFPartData emfPartData){
        Map<String, ModelPart> emfChildren = new HashMap<>();
        for (EMFPartData sub : emfPartData.submodels) {

            //prefer part name for vanilla model structure mirroring
            String idForMap = sub.part == null? sub.id : sub.part;
            while(emfChildren.containsKey(idForMap)){
                idForMap = idForMap+"-";
            }
            emfChildren.put(idForMap, new EMFModelPart3(sub));
        }
        return emfChildren;
    }

    public EMFModelPart3(EMFPartData emfPartData){//,//float[] parentalTransforms) {

        super(getCuboidsFromData(emfPartData), getChildrenFromData(emfPartData));

        selfModelData = emfPartData;
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("data = " + selfModelData.toString(false));


        //check if texture ovvveride needs to happen
        // i am keeping it an identifier as opposed to storing a renderlayer to allow future etf api support
//        if (!selfModelData.texture.isEmpty()){
//            Identifier texture =new Identifier( selfModelData.texture);
//            if(MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent()){
//                customTexture = texture;
//            }else{
//                customTexture = null;
//            }
//        }else{
//            customTexture = null;
//        }
//
//        //grab booleans to avoid further contains checks
//        boolean invX = selfModelData.invertAxis.contains("x");
//        boolean invY = selfModelData.invertAxis.contains("y");
//        boolean invZ = selfModelData.invertAxis.contains("z");
//
//        this.invX = invX;
//        this.invY = invY;
//        this.invZ = invZ;
//        //selfModelData.
//
//        //these ones need to change due to some unknown bullshit
//        float translateX= selfModelData.translate[0];
//        float translateY= selfModelData.translate[1];
//        float translateZ= selfModelData.translate[2];
//
//        double rotateX= Math.toRadians( selfModelData.rotate[0]);
//        double rotateY= Math.toRadians(selfModelData.rotate[1]);
//        double rotateZ= Math.toRadians(selfModelData.rotate[2]);
//
//
////        if (vanillaPartOfThis != null && selfModelData.attach) {
////            System.out.println("ran");
////            ModelTransform def = vanillaPartOfThis.getTransform();
////            translateX= def.pivotX*2;
////            translateY= def.pivotY*2;
////            translateZ= def.pivotZ*2;
////
////            rotateX= def.pitch;
////            rotateY= def.yaw;
////            rotateZ= def.roll;
////
////        }
//
//        //figure out the bullshit
//        if( invX){
//            rotateX = -rotateX;
//            translateX = -translateX;
//        }else{
//            //nothing? just an invert?
//        }
//        if( invY){
//            rotateY = -rotateY;
//            translateY = -translateY;
//        }
//        if( invZ){
//            rotateZ = -rotateZ;
//            translateZ = -translateZ;
//        }
//
//
//
////        // this if statement aged me by like 5 years to brute force figure out
////        // the logic of this is utterly essential to correct model positioning of jems
////        // and isn't #$@%!@$# documented anywhere that I found
////        // I cannot even articulate how many variations of this I had to try
//        if(parentNumber == 0){// && selfModelData.boxes.length == 0){
//            //sendToFirstChild = new float[]{translateX, translateY, translateZ};
//            pivotX = translateX;//0;
//            pivotY = 24 - translateY ;//24;//0; 24 makes it look nice normally but animations need to include it separately
//            pivotZ = translateZ;//0;
//        }else if(parentNumber == 1 ){
//            float parent0sTX = fromFirstChild[0];
//            float parent0sTY = fromFirstChild[1];
//            float parent0sTZ = fromFirstChild[2];
//            pivotX = parent0sTX + translateX;
//            pivotY = parent0sTY + translateY;// pivotModifyForParNum1Only[1];
//            pivotZ = parent0sTZ + translateZ;
//        }else{// of course it just suddenly acts normal after the first 2 :L
//            pivotX = translateX;
//            pivotY = translateY;
//            pivotZ = translateZ;
//        }

        //this seems to fix the issue with sheep cows pigs etc where the body emf part isn't aligned right when not animated
        // this attempts to copy over model default transforms from vanilla parts
//        if (vanillaPartOfThis != null ){
//
//            ModelTransform defaults = vanillaPartOfThis.getDefaultTransform();
//            if(defaults.pitch != 0 || defaults.yaw != 0 || defaults.roll != 0) {
//                rotateX += defaults.pitch;
//                rotateY += defaults.yaw;
//                rotateZ += defaults.roll;
//
//                // seems this is a factor as it has proved functional for pigs sheep and cows despite their varied offsets
////                float stanceWidthMaybe = -defaults.pivotY + 15;
////                //sheep 10   pig 4
////
////                pivotX = defaults.pivotX;
////                pivotY = defaults.pivotY + (stanceWidthMaybe / 4);//+2;
////                pivotZ = (float) (defaults.pivotZ + (stanceWidthMaybe * 1.8));//+20;
//
//                //nvm lol had something else disabled while testing
//                pivotX = defaults.pivotX;
//                pivotY = defaults.pivotY;
//                pivotZ = defaults.pivotZ;
//            }
//        }

        //try the vanilla model values
//
//        pitch = (float) rotateX;
//        yaw = (float) rotateY;
//        roll = (float) rotateZ;

        //seems to be just straight into model no bullshit?
        //todo check up on scale?
        xScale = selfModelData.scale;
        yScale = selfModelData.scale;
        zScale = selfModelData.scale;


        pivotX = selfModelData.translate[0];
        pivotY = selfModelData.translate[1];
        pivotZ = selfModelData.translate[2];

        pitch = selfModelData.rotate[0];
        yaw = selfModelData.rotate[1];
        roll = selfModelData.rotate[2];

        this.setDefaultTransform(this.getTransform());



        //assertChildrenAndCuboids();
    }

    public void assertChildrenAndCuboids() {
        ((ModelPartAccessor)this).setChildren(new HashMap<String, ModelPart>(emfChildren));
        ((ModelPartAccessor)this).setCuboids(new ArrayList<Cuboid>(emfCuboids));
    }

    private static List<Cuboid> createCuboidsFromBoxDataV3(EMFPartData emfPartData) {
        List<Cuboid> emfCuboids = new ArrayList<>();
        if (emfPartData.boxes.length > 0) {
            try {
                for (EMFBoxData box :
                        emfPartData.boxes) {
                    EMFCuboid cube;

                    //already figures this out in v1
                    //figures it would match to the invert values fml...

                    //seems it needs to include the full box value aswell
                    //moved all coord processing to here






                    if (box.textureOffset.length == 2) {
                        //System.out.println("non custom uv box ignoring for now");
                        cube = new EMFCuboid(emfPartData,
                                box.textureOffset[0],box.textureOffset[1],
                                box.coordinates[0],box.coordinates[1],box.coordinates[2],
                                box.coordinates[3], box.coordinates[4], box.coordinates[5],
                                box.sizeAdd, box.sizeAdd, box.sizeAdd,
                                emfPartData.textureSize[0], emfPartData.textureSize[1],
                                emfPartData.mirrorTexture.contains("u"),emfPartData.mirrorTexture.contains("v"));//selfModelData.invertAxis);
                    } else {
                        //create a custom uv cuboid
                        cube = new EMFCuboid(emfPartData,
                                box.uvDown, box.uvUp, box.uvNorth,
                                box.uvSouth, box.uvWest, box.uvEast,
                                box.coordinates[0],box.coordinates[1],box.coordinates[2],
                                box.coordinates[3], box.coordinates[4], box.coordinates[5],
                                box.sizeAdd, box.sizeAdd, box.sizeAdd,
                                emfPartData.textureSize[0], emfPartData.textureSize[1],
                                emfPartData.mirrorTexture.contains("u"),emfPartData.mirrorTexture.contains("v"));//selfModelData.invertAxis);
                    }
                    emfCuboids.add(cube);
                }

            } catch (Exception e) {
                EMFUtils.EMF_modMessage("cuboid construction broke: " + e, false);

            }
        }

        return emfCuboids;
    }

    //stop trying to optimize my code so it doesn't work sodium :P
    @Override // overrides to circumvent sodium optimizations that mess with custom uv quad creation
    protected void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        //this is a copy of the vanilla renderCuboids() method
        for (Cuboid cuboid : ((ModelPartAccessor) this).getCuboids()) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    @Environment(value = EnvType.CLIENT)
    public static class EMFCuboid extends Cuboid {
        private final Quad[] sidesEMF;
        public final float minXEMF;
        public final float minYEMF;
        public final float minZEMF;
        public final float maxXEMF;
        public final float maxYEMF;
        public final float maxZEMF;

        //cuboid without custom UVs
        public EMFCuboid(EMFPartData selfModelData
                , float textureU, float textureV,
                         float cubeX, float cubeY, float cubeZ,
                         float sizeX, float sizeY, float sizeZ,
                         float extraX, float extraY, float extraZ,
                         float textureWidth, float textureHeight,
                         boolean mirrorU, boolean mirrorV) {

            super((int) textureU, (int) textureV,
                    cubeX, cubeY, cubeZ,
                    sizeX, sizeY, sizeZ,
                    extraX, extraY, extraZ, false,
                    textureWidth, textureHeight);

            CuboidAccessor accessor = (CuboidAccessor) this;
            accessor.setMinX(cubeX);
            this.minXEMF = cubeX;
            accessor.setMinY(cubeY);
            this.minYEMF = cubeY;
            accessor.setMinZ(cubeZ);
            this.minZEMF = cubeZ;
            accessor.setMaxX(cubeX + sizeX);
            this.maxXEMF = cubeX + sizeX;
            accessor.setMaxY(cubeY + sizeY);
            this.maxYEMF = cubeY + sizeY;
            accessor.setMaxZ(cubeZ + sizeZ);
            this.maxZEMF = cubeZ + sizeZ;
            //Quad[] sides = new Quad[6];
            ArrayList<Quad> sides = new ArrayList<>();
            float cubeX2 = cubeX + sizeX;
            float cubeY2 = cubeY + sizeY;
            float cubeZ2 = cubeZ + sizeZ;
            cubeX -= extraX;
            cubeY -= extraY;
            cubeZ -= extraZ;
            cubeX2 += extraX;
            cubeY2 += extraY;
            cubeZ2 += extraZ;

            Vertex vertex = new Vertex(cubeX, cubeY, cubeZ, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(cubeX2, cubeY, cubeZ, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(cubeX2, cubeY2, cubeZ, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(cubeX, cubeY2, cubeZ, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(cubeX, cubeY, cubeZ2, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(cubeX2, cubeY, cubeZ2, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(cubeX2, cubeY2, cubeZ2, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(cubeX, cubeY2, cubeZ2, 8.0f, 0.0f);
            float j = textureU;
            float k = textureU + sizeZ;
            float l = textureU + sizeZ + sizeX;
            float m = textureU + sizeZ + sizeX + sizeX;
            float n = textureU + sizeZ + sizeX + sizeZ;
            float o = textureU + sizeZ + sizeX + sizeZ + sizeX;
            float p = textureV;
            float q = textureV + sizeZ;
            float r = textureV + sizeZ + sizeY;

            try {
                // sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight,false, Direction.DOWN);
                sides.add(new Quad(mirrorV ? new Vertex[]{vertex3, vertex4, vertex8, vertex7} : new Vertex[]{vertex6, vertex5, vertex, vertex2},
                        //k, p, l, q,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? l : k,
                        mirrorV ? q : p,
                        mirrorU ? k : l,
                        mirrorV ? p : q,
                        textureWidth, textureHeight, false, mirrorV ? Direction.UP : Direction.DOWN));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-dwn failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(mirrorV ? new Vertex[]{vertex6, vertex5, vertex, vertex2} : new Vertex[]{vertex3, vertex4, vertex8, vertex7},
                        //l, q, m, p,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? m : l,
                        mirrorV ? p : q,
                        mirrorU ? l : m,
                        mirrorV ? q : p,
                        textureWidth, textureHeight, false, mirrorV ? Direction.DOWN : Direction.UP));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-up failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                        //j, q, k, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? k : j,
                        mirrorV ? r : q,
                        mirrorU ? j : k,
                        mirrorV ? q : r,
                        textureWidth, textureHeight, false, mirrorU ? Direction.EAST : Direction.WEST));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-west failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3},
                        // k, q, l, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? l : k,
                        mirrorV ? r : q,
                        mirrorU ? k : l,
                        mirrorV ? q : r,
                        textureWidth, textureHeight, false, Direction.NORTH));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-nrth failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                        //l, q, n, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? n : l,
                        mirrorV ? r : q,
                        mirrorU ? l : n,
                        mirrorV ? q : r,
                        textureWidth, textureHeight, false, mirrorU ? Direction.WEST : Direction.EAST));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-east failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                        // n, q, o, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? o : n,
                        mirrorV ? r : q,
                        mirrorU ? n : o,
                        mirrorV ? q : r,
                        textureWidth, textureHeight, false, Direction.SOUTH));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-sth failed for " + selfModelData.id);
            }


            this.sidesEMF = sides.toArray(new Quad[0]);
            ((CuboidAccessor) this).setSides(sidesEMF);
        }

        // private static final Quad blankQuad = new Quad(new Vertex[]{0, 0, 0, 0}, 0, 0, 0, 0, 0, 0,false, Direction.NORTH);

        //Cuboid with custom UVs
        public EMFCuboid(EMFPartData selfModelData,
                         float[] uvDown, float[] uvUp, float[] uvNorth, float[] uvSouth, float[] uvWest, float[] uvEast,
                         float cubeX, float cubeY, float cubeZ,
                         float sizeX, float sizeY, float sizeZ,
                         float extraX, float extraY, float extraZ,
                         float textureWidth, float textureHeight,
                         boolean mirrorU, boolean mirrorV) {

            super(0, 0,
                    cubeX, cubeY, cubeZ,
                    sizeX, sizeY, sizeZ,
                    extraX, extraY, extraZ, false,
                    textureWidth, textureHeight);

            CuboidAccessor accessor = (CuboidAccessor) this;
            accessor.setMinX(cubeX);
            this.minXEMF = cubeX;
            accessor.setMinY(cubeY);
            this.minYEMF = cubeY;
            accessor.setMinZ(cubeZ);
            this.minZEMF = cubeZ;
            accessor.setMaxX(cubeX + sizeX);
            this.maxXEMF = cubeX + sizeX;
            accessor.setMaxY(cubeY + sizeY);
            this.maxYEMF = cubeY + sizeY;
            accessor.setMaxZ(cubeZ + sizeZ);
            this.maxZEMF = cubeZ + sizeZ;
            //Quad[] sides = new Quad[6];
            ArrayList<Quad> sides = new ArrayList<>();

            float cubeX2 = cubeX + sizeX;
            float cubeY2 = cubeY + sizeY;
            float cubeZ2 = cubeZ + sizeZ;

            //todo check this is right
            cubeX -= extraX;
            cubeY -= extraY;
            cubeZ -= extraZ;
            cubeX2 += extraX;
            cubeY2 += extraY;
            cubeZ2 += extraZ;


            Vertex vertex = new Vertex(cubeX, cubeY, cubeZ, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(cubeX2, cubeY, cubeZ, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(cubeX2, cubeY2, cubeZ, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(cubeX, cubeY2, cubeZ, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(cubeX, cubeY, cubeZ2, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(cubeX2, cubeY, cubeZ2, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(cubeX2, cubeY2, cubeZ2, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(cubeX, cubeY2, cubeZ2, 8.0f, 0.0f);

            //altered custom uv quads see working out below
            //probably needs to be adjusted but thats later me problem


            //vertexes ordering format
            // 1 2
            // 4 3


            try {
                sides.add(new Quad(mirrorV ? new Vertex[]{vertex8, vertex7, vertex3, vertex4} : new Vertex[]{vertex, vertex2, vertex6, vertex5},
                        mirrorU ? uvUp[2] : uvUp[0],
                        mirrorV ? uvUp[3] : uvUp[1],
                        mirrorU ? uvUp[0] : uvUp[2],
                        mirrorV ? uvUp[1] : uvUp[3],
                        textureWidth, textureHeight, false, mirrorV ? Direction.UP : Direction.DOWN));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-up failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(mirrorV ? new Vertex[]{vertex, vertex2, vertex6, vertex5} : new Vertex[]{vertex8, vertex7, vertex3, vertex4},//actually down
                        // uvDown[0], uvDown[1], uvDown[2], uvDown[3],
                        mirrorU ? uvDown[2] : uvDown[0],
                        mirrorV ? uvDown[3] : uvDown[1],
                        mirrorU ? uvDown[0] : uvDown[2],
                        mirrorV ? uvDown[1] : uvDown[3],
                        textureWidth, textureHeight, false, mirrorV ? Direction.DOWN : Direction.UP));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-down failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                        // uvWest[0], uvWest[1], uvWest[2], uvWest[3],
                        mirrorU ? uvWest[2] : uvWest[0],
                        mirrorV ? uvWest[3] : uvWest[1],
                        mirrorU ? uvWest[0] : uvWest[2],
                        mirrorV ? uvWest[1] : uvWest[3],
                        textureWidth, textureHeight, false, mirrorU ? Direction.WEST : Direction.EAST));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-west failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3},
                        //uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3],
                        mirrorU ? uvNorth[2] : uvNorth[0],
                        mirrorV ? uvNorth[3] : uvNorth[1],
                        mirrorU ? uvNorth[0] : uvNorth[2],
                        mirrorV ? uvNorth[1] : uvNorth[3],
                        textureWidth, textureHeight, false, Direction.NORTH));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-north failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                        //uvEast[0], uvEast[1], uvEast[2], uvEast[3],
                        mirrorU ? uvEast[2] : uvEast[0],
                        mirrorV ? uvEast[3] : uvEast[1],
                        mirrorU ? uvEast[0] : uvEast[2],
                        mirrorV ? uvEast[1] : uvEast[3],
                        textureWidth, textureHeight, false, mirrorU ? Direction.EAST : Direction.WEST));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-east failed for " + selfModelData.id);
            }
            try {
                sides.add(new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? uvSouth[2] : uvSouth[0],
                        mirrorV ? uvSouth[3] : uvSouth[1],
                        mirrorU ? uvSouth[0] : uvSouth[2],
                        mirrorV ? uvSouth[1] : uvSouth[3],
                        textureWidth, textureHeight, false, Direction.SOUTH));
            } catch (Exception e) {
                if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMF_modMessage("uv-south failed for " + selfModelData.id);
            }


            this.sidesEMF = sides.toArray(new Quad[0]);
            ((CuboidAccessor) this).setSides(sidesEMF);

        }


    }

    public ModelTransform vanillaTransform = null;

    public void applyDefaultModelRotatesToChildren(ModelTransform defaults){
        for (ModelPart part:
        ((ModelPartAccessor)this).getChildren().values()) {
            if(part instanceof EMFModelPart3 p3) p3.applyDefaultModelRotates(defaults);
        }
    }
    public void applyDefaultModelRotates(ModelTransform defaults){
        //todo its possible here lies the actual cause of all the parent 1 stuff if i factor in transforms here
        //highly possible
        //todo seriously look into the above
        vanillaTransform = defaults;
//        ModelTransform defaultOfThis = getDefaultTransform();
//        float newPitch = defaultOfThis.pitch - defaults.pitch;
//        float newYaw = defaultOfThis.yaw - defaults.yaw;
//        float newRoll = defaultOfThis.roll + defaults.roll;
//
//        setDefaultTransform(ModelTransform.of(defaultOfThis.pivotX,defaultOfThis.pivotY,defaultOfThis.pivotZ,newPitch,newYaw,newRoll));

    }


    public Object2ReferenceOpenHashMap<String, EMFModelPart3> getAllChildPartsAsMap(){
        Object2ReferenceOpenHashMap<String, EMFModelPart3> list = new Object2ReferenceOpenHashMap<>();
        for (ModelPart part :
                ((ModelPartAccessor)this).getChildren().values()) {
            if(part instanceof EMFModelPart3 part3) {

                list.put(part3.selfModelData.part == null? part3.selfModelData.id :part3.selfModelData.part, part3);
                list.putAll(part3.getAllChildPartsAsMap());
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "emfPart3{id="+selfModelData.id +", part="+ selfModelData.part+"}";
    }
}
