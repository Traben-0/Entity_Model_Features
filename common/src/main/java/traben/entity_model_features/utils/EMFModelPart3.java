package traben.entity_model_features.utils;


import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
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

            while(emfChildren.containsKey(sub.id)){
                sub.id = sub.id+"-";
            }
            emfChildren.put(sub.id, new EMFModelPart3(sub));
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






    @Environment(value = EnvType.CLIENT)
    public static class EMFCuboid extends Cuboid{
//        private final EMF_CustomModelPart.Quad[] sides;
//        public final float minX;
//        public final float minY;
//        public final float minZ;
//        public final float maxX;
//        public final float maxY;
//        public final float maxZ;

        //cuboid without custom UVs
        public EMFCuboid(EMFPartData selfModelData
                , float textureU, float textureV,
                         float cubeX, float cubeY, float cubeZ,
                         float sizeX, float sizeY, float sizeZ,
                         float extraX, float extraY, float extraZ,
                         float textureWidth, float textureHeight,
                         boolean mirrorU, boolean mirrorV) {

            super((int) textureU, (int) textureV,
             cubeX,  cubeY,  cubeZ,
             sizeX,  sizeY,  sizeZ,
             extraX,  extraY,  extraZ,false,
             textureWidth,  textureHeight);

            CuboidAccessor accessor = (CuboidAccessor) this;
            accessor.setMinX(cubeX);//this.minX = cubeX;
            accessor.setMinY(cubeY);//this.minY = cubeY;
            accessor.setMinZ(cubeZ);//this.minZ = cubeZ;
            accessor.setMaxX(cubeX + sizeX);//this.maxX = cubeX + sizeX;
            accessor.setMaxY(cubeY + sizeY);//this.maxY = cubeY + sizeY;
            accessor.setMaxZ(cubeZ + sizeZ);//this.maxZ = cubeZ + sizeZ;
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
            float l =  textureU + sizeZ + sizeX;
            float m =  textureU + sizeZ + sizeX + sizeX;
            float n =  textureU + sizeZ + sizeX + sizeZ;
            float o =  textureU + sizeZ + sizeX + sizeZ + sizeX;
            float p = textureV;
            float q =  textureV + sizeZ;
            float r =  textureV + sizeZ + sizeY;

            try {
               // sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight,false, Direction.DOWN);
                sides.add( new Quad(mirrorV ? new Vertex[]{vertex3, vertex4, vertex8, vertex7} : new Vertex[]{vertex6, vertex5, vertex, vertex2},
                        //k, p, l, q,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? l : k,
                        mirrorV ? q : p,
                        mirrorU ? k : l,
                        mirrorV ? p : q,
                        textureWidth, textureHeight,false, mirrorV ? Direction.UP : Direction.DOWN));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-dwn failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(mirrorV ? new Vertex[]{vertex6, vertex5, vertex, vertex2} : new Vertex[]{vertex3, vertex4, vertex8, vertex7},
                        //l, q, m, p,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? m : l,
                        mirrorV ? p : q,
                        mirrorU ? l : m,
                        mirrorV ? q : p,
                        textureWidth, textureHeight,false,mirrorV ? Direction.DOWN : Direction.UP));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-up failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                        //j, q, k, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? k : j,
                        mirrorV ? r : q,
                        mirrorU ? j : k,
                        mirrorV ? q : r,
                        textureWidth, textureHeight,false, mirrorU ? Direction.EAST : Direction.WEST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-west failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad( new Vertex[]{vertex2, vertex, vertex4, vertex3},
                       // k, q, l, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? l : k,
                        mirrorV ? r : q,
                        mirrorU ? k : l,
                        mirrorV ? q : r,
                        textureWidth, textureHeight,false, Direction.NORTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-nrth failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} :  new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                        //l, q, n, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? n : l,
                        mirrorV ? r : q,
                        mirrorU ? l : n,
                        mirrorV ? q : r,
                        textureWidth, textureHeight,false, mirrorU ? Direction.WEST : Direction.EAST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-east failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                       // n, q, o, r,
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? o : n,
                        mirrorV ? r : q,
                        mirrorU ? n : o,
                        mirrorV ? q : r,
                        textureWidth, textureHeight,false, Direction.SOUTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-sth failed for "+selfModelData.id);
            }


            ((CuboidAccessor) this).setSides(sides.toArray(new Quad[0]));
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

            super( 0,  0,
                    cubeX,  cubeY,  cubeZ,
                    sizeX,  sizeY,  sizeZ,
                    extraX,  extraY,  extraZ,false,
                    textureWidth,  textureHeight);

            CuboidAccessor accessor = (CuboidAccessor) this;
            accessor.setMinX(cubeX);//this.minX = cubeX;
            accessor.setMinY(cubeY);//this.minY = cubeY;
            accessor.setMinZ(cubeZ);//this.minZ = cubeZ;
            accessor.setMaxX(cubeX + sizeX);//this.maxX = cubeX + sizeX;
            accessor.setMaxY(cubeY + sizeY);//this.maxY = cubeY + sizeY;
            accessor.setMaxZ(cubeZ + sizeZ);//this.maxZ = cubeZ + sizeZ;
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
                sides.add( new Quad(mirrorV ? new Vertex[]{vertex8, vertex7, vertex3, vertex4} : new Vertex[]{vertex, vertex2, vertex6, vertex5},
                        mirrorU ? uvUp[2] : uvUp[0],
                        mirrorV ? uvUp[3] : uvUp[1],
                        mirrorU ? uvUp[0] : uvUp[2],
                        mirrorV ? uvUp[1] : uvUp[3],
                        textureWidth, textureHeight,false,mirrorV ? Direction.UP :  Direction.DOWN));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-up failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(mirrorV ? new Vertex[]{vertex, vertex2, vertex6, vertex5} : new Vertex[]{vertex8, vertex7, vertex3, vertex4},//actually down
                       // uvDown[0], uvDown[1], uvDown[2], uvDown[3],
                        mirrorU ? uvDown[2] : uvDown[0],
                        mirrorV ? uvDown[3] : uvDown[1],
                        mirrorU ? uvDown[0] : uvDown[2],
                        mirrorV ? uvDown[1] : uvDown[3],
                        textureWidth, textureHeight, false,mirrorV ? Direction.DOWN : Direction.UP));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-down failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(mirrorU? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                       // uvWest[0], uvWest[1], uvWest[2], uvWest[3],
                        mirrorU ? uvWest[2] : uvWest[0],
                        mirrorV ? uvWest[3] : uvWest[1],
                        mirrorU ? uvWest[0] : uvWest[2],
                        mirrorV ? uvWest[1] : uvWest[3],
                        textureWidth, textureHeight,false,mirrorU? Direction.WEST :  Direction.EAST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-west failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3},
                        //uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3],
                        mirrorU ? uvNorth[2] : uvNorth[0],
                        mirrorV ? uvNorth[3] : uvNorth[1],
                        mirrorU ? uvNorth[0] : uvNorth[2],
                        mirrorV ? uvNorth[1] : uvNorth[3],
                        textureWidth, textureHeight,false, Direction.NORTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-north failed for "+selfModelData.id);
                }
            try {
                sides.add( new Quad(mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                        //uvEast[0], uvEast[1], uvEast[2], uvEast[3],
                        mirrorU ? uvEast[2] : uvEast[0],
                        mirrorV ? uvEast[3] : uvEast[1],
                        mirrorU ? uvEast[0] : uvEast[2],
                        mirrorV ? uvEast[1] : uvEast[3],
                        textureWidth, textureHeight,false,mirrorU? Direction.EAST : Direction.WEST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-east failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                        //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                        mirrorU ? uvSouth[2] : uvSouth[0],
                        mirrorV ? uvSouth[3] : uvSouth[1],
                        mirrorU ? uvSouth[0] : uvSouth[2],
                        mirrorV ? uvSouth[1] : uvSouth[3],
                        textureWidth, textureHeight,false,Direction.SOUTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-south failed for "+selfModelData.id);
            }




            ((CuboidAccessor) this).setSides(sides.toArray(new Quad[0]));


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


//        public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
////            Matrix4f matrix4f = entry.getPositionMatrix();
////            Matrix3f matrix3f = entry.getNormalMatrix();
////            for (EMF_CustomModelPart.Quad quad : this.sides) {
////                Vector3f vec3f = quad.direction.copy();
////                vec3f.transform(matrix3f);
////                float f = vec3f.x();
////                float g = vec3f.y();
////                float h = vec3f.z();
////                for (EMF_CustomModelPart.Vertex vertex : quad.vertices) {
////                    float i = vertex.pos.x() / 16.0f;
////                    float j = vertex.pos.y() / 16.0f;
////                    float k = vertex.pos.z() / 16.0f;
////                    Vector4f vector4f = new Vector4f(i, j, k, 1.0f);
////                    vector4f.transform(matrix4f);
////                    vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
////                }
////            }
//
//
//            Matrix4f matrix4f = entry.getPositionMatrix();
//            Matrix3f matrix3f = entry.getNormalMatrix();
//            EMF_CustomModelPart.Quad[] var11 = ((CuboidAccessor)this).getSides();
//            int var12 = var11.length;
//
//            for(int ij = 0; ij < var12; ++ij) {
//                EMF_CustomModelPart.Quad quad = var11[ij];
//                if (quad != null) {
//                    Vector3f vector3f = matrix3f.transform(new Vector3f(quad.direction));
//                    float f = vector3f.x();
//                    float g = vector3f.y();
//                    float h = vector3f.z();
//                    EMF_CustomModelPart.Vertex[] var19 = quad.vertices;
//                    int var20 = var19.length;
//
//                    for (int ii = 0; ii < var20; ++ii) {
//                        EMF_CustomModelPart.Vertex vertex = var19[ii];
//                        float i = vertex.pos.x() / 16.0F;
//                        float j = vertex.pos.y() / 16.0F;
//                        float k = vertex.pos.z() / 16.0F;
//                        Vector4f vector4f = matrix4f.transform(new Vector4f(i, j, k, 1.0F));
//                        vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
//                    }
//                }
//            }
//        }
    }

//    @Environment(value = EnvType.CLIENT)
//    static class Vertex {
//        public final Vector3f pos;
//        public final float u;
//        public final float v;
//
//        public Vertex(float x, float y, float z, float u, float v) {
//            this(new Vector3f(x, y, z), u, v);
//        }
//
//        public EMF_CustomModelPart.Vertex remap(float u, float v) {
//            return new EMF_CustomModelPart.Vertex(this.pos, u, v);
//        }
//
//        public Vertex(Vector3f pos, float u, float v) {
//            this.pos = pos;
//            this.u = u;
//            this.v = v;
//        }
//    }

//    @Environment(value = EnvType.CLIENT)
//    static class Quad {
//        public final EMF_CustomModelPart.Vertex[] vertices;
//        public final Vector3f direction;
//
//        public Quad(EMF_CustomModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, Direction direction) {
//
//
//
//
//            this.vertices = vertices;
//            float f = 0.0f / squishU;
//            float g = 0.0f / squishV;
//            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
//            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
//            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
//            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
//
//
//            this.direction = direction.getUnitVector();
//            //todo check this
////            if (mirrorUV[0]) {
////                this.direction.mul(-1.0f, 1.0f, 1.0f);
////            }
////            if (mirrorUV[1]) {
////                this.direction.mul(1.0f, -1.0f, 1.0f);
////            }
//        }
//    }

    public Object2ReferenceOpenHashMap<String, EMFModelPart3> getAllParts(){
        Object2ReferenceOpenHashMap<String, EMFModelPart3> list = new Object2ReferenceOpenHashMap<>();
        for (EMFModelPart3 part :
                emfChildren.values()) {
            list.put(part.selfModelData.id,part);
            list.putAll(part.getAllParts());
        }
        return list;
    }

    @Override
    public String toString() {
        return "emfPart3{id="+selfModelData.id +", part="+ selfModelData.part+"}";
    }
}
