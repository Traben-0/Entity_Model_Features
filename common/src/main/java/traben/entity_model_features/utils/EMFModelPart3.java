package traben.entity_model_features.utils;


import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
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
    //public final Map<String, EMFModelPart3> cannonicalChildren = new HashMap<>();

    public final EMFPartData selfModelData;

    public int currentModelVariantState = 0;

    public boolean isValidToRenderInThisState = true;

    private static final Cuboid EMPTY_CUBOID = new Cuboid(0,0,0,0,0,0,0,0,0,0,0,false,0,0);

    public Int2ObjectArrayMap<EMFModelState> allKnownStateVariants = new Int2ObjectArrayMap<>();


    //public static final EMFModelPart3 BLANK_MODEL_PART = new EMFModelPart3(EMFPartData.BLANK_PART_DATA);


//    @Override
//    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
//        render(matrices,vertices,light,overlay, 1,1,1,1);
//    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //assertChildrenAndCuboids();
        //if(new Random().nextInt(100)==1) System.out.println("rendered");
        if(isValidToRenderInThisState)
            super.render(matrices, vertices, light, overlay, red, 0.0f, blue, alpha);

    }
//     final Identifier customTexture;
//    public final ModelPart vanillaPart;


    public EMFModelPart3(List<Cuboid> cuboids, Map<String, ModelPart> children,int variantNumber){
        //create empty root model object
        //noinspection ConstantValue
        super(/*cuboids.isEmpty() && EMFVersionDifferenceManager.isThisModLoaded("physicsmod")? List.of(EMPTY_CUBOID) :*/ cuboids, children);
        selfModelData = null;

        if(variantNumber== 0)
            allKnownStateVariants.put(variantNumber,getCurrentState());
    }

    private static List<Cuboid> getCuboidsFromData(EMFPartData emfPartData){

        //if(cuboids.isEmpty() && EMFVersionDifferenceManager.isThisModLoaded("physicsmod"))
        //    cuboids.add(EMPTY_CUBOID);
        return createCuboidsFromBoxDataV3(emfPartData);//false remove pivot value

    }
    private static Map<String, ModelPart> getChildrenFromData(EMFPartData emfPartData, int variantNumber){
        Map<String, ModelPart> emfChildren = new HashMap<>();
        for (EMFPartData sub : emfPartData.submodels) {

            //prefer part name for vanilla model structure mirroring
            String idForMap = sub.part == null? sub.id : sub.part;
            while(emfChildren.containsKey(idForMap)){
                idForMap = idForMap+"-";
            }
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                System.out.println(" > > > > EMF sub part made: "+sub.toString(false));
            emfChildren.put(idForMap, new EMFModelPart3(sub,variantNumber));
        }
        return emfChildren;
    }

    public EMFModelPart3(EMFPartData emfPartData, int variantNumber){//,//float[] parentalTransforms) {

        super(getCuboidsFromData(emfPartData), getChildrenFromData(emfPartData,variantNumber));

        selfModelData = emfPartData;


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


//        for (Map.Entry<String,ModelPart> part:
//        ((ModelPartAccessor)this).getChildren().entrySet()) {
//            if(part.getValue() instanceof EMFModelPart3 m3 && m3.selfModelData.part!= null){
//                cannonicalChildren.put(part.getKey(),m3);
//            }
//        }

        //assertChildrenAndCuboids();
        if(variantNumber== 0)
            allKnownStateVariants.put(variantNumber,getCurrentState());

    }



    public void assertChildrenAndCuboids() {
        ((ModelPartAccessor)this).setChildren(new HashMap<String, ModelPart>(emfChildren));
        ((ModelPartAccessor)this).setCuboids(new ArrayList<Cuboid>(emfCuboids));
    }

    private static List<Cuboid> createCuboidsFromBoxDataV3(EMFPartData emfPartData) {
        List<Cuboid> emfCuboids = new LinkedList<>();
        if (emfPartData.boxes.length > 0) {
            try {
                for (EMFBoxData box :
                        emfPartData.boxes) {
                    EMFCuboid cube;

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

   // public ModelTransform vanillaTransform = null;

//    public void applyDefaultModelRotatesToChildren(ModelTransform defaults){
////        ModelTransform thisDefaults = getDefaultTransform();
////        ModelTransform newDefaults = ModelTransform.of(
////                defaults.pivotX- thisDefaults.pivotX,
////                defaults.pivotY- thisDefaults.pivotY,
////                defaults.pivotZ- thisDefaults.pivotZ,
////                defaults.pitch- thisDefaults.pitch,
////                defaults.yaw- thisDefaults.yaw,
////                defaults.roll- thisDefaults.roll
////        );
//
//        for (ModelPart part:
//        ((ModelPartAccessor)this).getChildren().values()) {
//            if(part instanceof EMFModelPart3 p3) p3.applyDefaultModelRotates(defaults);
//        }
//    }
    public void applyDefaultModelRotates(ModelTransform defaults){
        //todo its possible here lies the actual cause of all the parent 1 stuff if i factor in transforms here
        //highly possible
        //todo seriously look into the above

        if(defaults!= null) {
            this.setTransform(defaults);
        }

    }


    public Object2ReferenceOpenHashMap<String, EMFModelPart3> getAllChildPartsAsMap(){
        Object2ReferenceOpenHashMap<String, EMFModelPart3> list = new Object2ReferenceOpenHashMap<>();
        for (ModelPart part :
                ((ModelPartAccessor)this).getChildren().values()) {
            if(part instanceof EMFModelPart3 part3) {

                list.put(part3.selfModelData.part == null? part3.selfModelData.id : part3.selfModelData.part, part3);
                list.putAll(part3.getAllChildPartsAsMap());
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "emfPart3{id="+selfModelData.id +", part="+ selfModelData.part+"}";
    }

    public Map<String, ModelPart> getChildrenEMF(){
        return ((ModelPartAccessor)this).getChildren();
    }
    public void setChildrenEMF(Map<String, ModelPart> children){
        ((ModelPartAccessor)this).setChildren(children);
    }

    public void mergePartVariant(int variantNumber,EMFModelPart3 partToMergeIntoThisAsVariant){
        EMFModelState incomingPartState = partToMergeIntoThisAsVariant.getCurrentState();
        allKnownStateVariants.put(variantNumber,incomingPartState);
        for (Map.Entry<String,ModelPart> childEntry:
             partToMergeIntoThisAsVariant.getChildrenEMF().entrySet()) {
            if(childEntry.getValue() instanceof EMFModelPart3 p2 && getChildrenEMF().get(childEntry.getKey()) instanceof EMFModelPart3 p3){
                p3.mergePartVariant(variantNumber,p2);
            }else{
                Map<String, ModelPart> children = getChildrenEMF();
                children.put(childEntry.getKey(),childEntry.getValue());
                //setChildren(children);//todo might be redundant idk how accessors get the value
            }
        }
    }

    public void setVariantStateTo(int newVariantState){
        if(currentModelVariantState != newVariantState) {
            if (allKnownStateVariants.containsKey(newVariantState)) {
                currentModelVariantState = newVariantState;
                setFromState(allKnownStateVariants.get(newVariantState));
                isValidToRenderInThisState = true;
            } else if (selfModelData != null && selfModelData.part == null) {
                currentModelVariantState = newVariantState;
                isValidToRenderInThisState = false;
            } else {
                EMFUtils.EMF_modWarn("no state for top level part????");
                currentModelVariantState = newVariantState;
                isValidToRenderInThisState = false;
            }
            for (ModelPart part:
                 getChildrenEMF().values()) {
                if(part instanceof EMFModelPart3 p3)
                    p3.setVariantStateTo(newVariantState);
            }
        }//else{
            //System.out.println("same state emf part");
        //}

    }

    private EMFModelState getCurrentState(){
        return new EMFModelState(
                getDefaultTransform(),
                ((ModelPartAccessor)this).getCuboids(),
                //((ModelPartAccessor)this).getChildren(),
                xScale,yScale,zScale,
                visible, hidden

        );
    }
    private void setFromState(EMFModelState newState){
        setDefaultTransform(newState.defaultTransform());
        setTransform(getDefaultTransform());
        ((ModelPartAccessor)this).setCuboids(newState.cuboids());
        xScale = newState.xScale();
        yScale = newState.yScale();
        zScale = newState.zScale();
        visible = newState.visible();
        hidden = newState.hidden();
    }

    private record EMFModelState(
            ModelTransform defaultTransform,
           // ModelTransform currentTransform,
            List<Cuboid> cuboids,
           // Map<String, ModelPart> children,
            float xScale,
            float yScale,
            float zScale,
            boolean visible,
            boolean hidden
    ){

    }


}
