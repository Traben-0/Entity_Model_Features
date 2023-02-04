package traben.entity_model_features.models;


import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.mixin.accessor.CuboidAccessor;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.jemJsonObjects.EMF_BoxData;
import traben.entity_model_features.models.jemJsonObjects.EMF_ModelData;
import traben.entity_model_features.utils.EMFUtils;

import java.util.*;

@Environment(value = EnvType.CLIENT)
public class EMF_ModelPart extends ModelPart  {
    private final List<EMFCuboid> cuboids = new ArrayList<>();
    private final Map<String, EMF_ModelPart> children = new HashMap<>();


    public final EMF_ModelData selfModelData;
    public final ArrayList<EMF_ModelData> parentModelData;
    public final EMF_ModelPart parent;

    public boolean visible_boxes = true;//todo
    public boolean visible = true;

    public boolean visibilityIsOveridden = false;



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        render(matrices,vertices,light,overlay, 1,1,1,1);
    }



    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if(vanillaPart != null && !visibilityIsOveridden){
            this.visible = vanillaPart.visible;
            this.hidden = vanillaPart.hidden;
        }
        if (this.visible) {
            if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
                matrices.push();
                this.rotate(matrices);
                // override texture if needed
                // this is not recommended for ETF support but is very useful for .jpms
                if(this.customTexture != null && this.thisModel.currentVertexProvider != null && this.thisModel.vanillaModel != null){
                    vertices = this.thisModel.currentVertexProvider.getBuffer(this.thisModel.vanillaModel.getLayer(this.customTexture));
                }

                if (!this.hidden) {
                    for (EMFCuboid cube :
                            cuboids) {
                        if(cube != null) {
                            cube.renderCuboid(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
                        }
                    }
                }
                Iterator<EMF_ModelPart> var9 = this.children.values().iterator();

                while(var9.hasNext()) {
                    EMF_ModelPart modelPart = var9.next();
                    modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }

                matrices.pop();
            }
        }
    }

    public float getVanillaPivotXWithPossibleVanillaParentTransforms(){
        if (vanillaParentPart != null){
            return vanillaPart.pivotX + vanillaParentPart.pivotX;
        }
        return vanillaPart.pivotX;
    }
    public float getVanillaPivotYWithPossibleVanillaParentTransforms(){
        if (vanillaParentPart != null){
            return vanillaPart.pivotY + vanillaParentPart.pivotY;
        }
        return vanillaPart.pivotY;
    }
    public float getVanillaPivotZWithPossibleVanillaParentTransforms(){
        if (vanillaParentPart != null){
            return vanillaPart.pivotZ + vanillaParentPart.pivotZ;
        }
        return vanillaPart.pivotZ;
    }
    public float getPivotXWithPossibleVanillaParentTransforms(){
        if(vanillaParentPart == null){
            return this.pivotX;
        } else if (vanillaParentPart instanceof EMF_ModelPart emf){
            return this.pivotX + emf.getPivotXWithPossibleVanillaParentTransforms();
        } else{
            return this.pivotX + vanillaParentPart.pivotX;
        }
    }
    public float getPivotYWithPossibleVanillaParentTransforms(){
        if(vanillaParentPart == null){
            return this.pivotY;
        } else if (vanillaParentPart instanceof EMF_ModelPart emf){
            return this.pivotY + emf.getPivotYWithPossibleVanillaParentTransforms();
        } else{
            return this.pivotY + vanillaParentPart.pivotY;
        }
    }
    public float getPivotZWithPossibleVanillaParentTransforms(){
        if(vanillaParentPart == null){
            return this.pivotZ;
        } else if (vanillaParentPart instanceof EMF_ModelPart emf){
            return this.pivotZ + emf.getPivotZWithPossibleVanillaParentTransforms();
        } else{
            return this.pivotZ + vanillaParentPart.pivotZ;
        }
    }
    public float getRollWithPossibleVanillaParentTransforms(){
        if(vanillaParentPart == null){
            return this.roll;
        } else if (vanillaParentPart instanceof EMF_ModelPart emf){
            return this.roll + emf.getRollWithPossibleVanillaParentTransforms();
        } else{
            return this.roll + vanillaParentPart.roll;
        }
    }
    public float getYawWithPossibleVanillaParentTransforms(){
        if(vanillaParentPart == null){
            return this.yaw;
        } else if (vanillaParentPart instanceof EMF_ModelPart emf){
            return this.yaw + emf.getYawWithPossibleVanillaParentTransforms();
        } else{
            return this.yaw + vanillaParentPart.yaw;
        }
    }
    public float getPitchWithPossibleVanillaParentTransforms(){
        if(vanillaParentPart == null){
            return this.pitch;
        } else if (vanillaParentPart instanceof EMF_ModelPart emf){
            return this.pitch + emf.getPitchWithPossibleVanillaParentTransforms();
        } else{
            return this.pitch + vanillaParentPart.pitch;
        }
    }


    //todo investigate need for a rotate(vec3D) override
    @Override
    public void rotate(MatrixStack matrices) {

        float pivotX = (doesAnimtx || vanillaPart == null ? getPivotXWithPossibleVanillaParentTransforms() : /*getDefaultTransform().pivotX +*/ getVanillaPivotXWithPossibleVanillaParentTransforms() );
        float pivotY = (doesAnimty || vanillaPart == null ? getPivotYWithPossibleVanillaParentTransforms() : /*getDefaultTransform().pivotY +*/ getVanillaPivotYWithPossibleVanillaParentTransforms() );
        float pivotZ = (doesAnimtz || vanillaPart == null ? getPivotZWithPossibleVanillaParentTransforms() : /*getDefaultTransform().pivotZ +*/ getVanillaPivotZWithPossibleVanillaParentTransforms() );

        float roll  = (doesAnimrz || vanillaPart == null ? getRollWithPossibleVanillaParentTransforms()  : getDefaultTransform().roll  + vanillaPart.roll );
        float yaw   = (doesAnimry || vanillaPart == null ? getYawWithPossibleVanillaParentTransforms()   : getDefaultTransform().yaw   + vanillaPart.yaw  );
        float pitch = (doesAnimrx || vanillaPart == null ? getPitchWithPossibleVanillaParentTransforms() : getDefaultTransform().pitch + vanillaPart.pitch);

       // float xScale = (doesAnimsx || vanillaPart == null ? this.xScale : vanillaPart.xScale );
       // float yScale = (doesAnimsy || vanillaPart == null ? this.yScale : vanillaPart.yScale );
       // float zScale = (doesAnimsz || vanillaPart == null ? this.zScale : vanillaPart.zScale );

        matrices.translate(
                (pivotX) / 16.0F,
                (pivotY) / 16.0F,
                (pivotZ) / 16.0F);

        matrices.multiply((new Quaternionf()).rotationZYX(roll, yaw, pitch));

        if (xScale != 1.0F || yScale != 1.0F || zScale != 1.0F) {
            matrices.scale(xScale, yScale, zScale);
        }
    }

    public void setAnimPitch(float newPitch){
        this.pitch = newPitch;// + getDefaultTransform().pitch;
    }
    public void setAnimYaw(float newYaw){
        this.yaw = newYaw;// + getDefaultTransform().yaw;
    }
    public void setAnimRoll(float newRoll){
        this.roll = newRoll;// + getDefaultTransform().roll;
    }
    public void setAnimPivotX(float val){
            this.pivotX = val  + parentOnePivotXOverride;
    }
    public void setAnimPivotY(float val){
            this.pivotY = val  + parentOnePivotYOverride;
    }
    public void setAnimPivotZ(float val){
        this.pivotZ = val + parentOnePivotZOverride;
    }
    public float getAnimPivotX(){
        return pivotX ;
    }
    public float getAnimPivotY(){
        return pivotY ;
    }
    public float getAnimPivotZ(){
        return pivotZ ;
    }
    public float getAnimPivotXSibling(){
        return pivotX - parentOnePivotXOverride;
    }
    public float getAnimPivotYSibling(){
        return pivotY - parentOnePivotYOverride;
    }
    public float getAnimPivotZSibling(){
        return pivotZ - parentOnePivotZOverride;
    }

    public boolean doesAnimtx = false;
    public boolean doesAnimty = false;
    public boolean doesAnimtz = false;
    public boolean doesAnimrx = false;
    public boolean doesAnimry = false;
    public boolean doesAnimrz = false;
    public boolean doesAnimsx = false;
    public boolean doesAnimsy = false;
    public boolean doesAnimsz = false;


    private float parentOnePivotXOverride = 0;
    private float parentOnePivotYOverride = 0;
    private float parentOnePivotZOverride = 0;



    private final EMF_EntityModel<?> thisModel;
     final Identifier customTexture;
    public final ModelPart vanillaPart;
    public ModelPart vanillaParentPart = null;



    public final String vanillaParentPartName;
    public EMF_ModelPart(EMF_ModelPart parent, int parentNumber,
                         EMF_ModelData EMFmodelData,
                         ArrayList<EMF_ModelData> parentEMFmodelData,
                         float[] pivotModifyForParNum1Only,
                         ModelPart vanillaPartOfThis,
                         String vanillaParentPartName,
                         EMF_EntityModel<?> thisModel){//,//float[] parentalTransforms) {

        super(new ArrayList<>(), new HashMap<>());

        this.vanillaParentPartName = vanillaParentPartName;
        this.thisModel = thisModel;
        this.parent = parent;
        selfModelData = EMFmodelData;
        parentModelData = parentEMFmodelData;
        vanillaPart = vanillaPartOfThis;

        //check if texture ovvveride needs to happen
        // i am keeping it an identifier as opposed to storing a renderlayer to allow future etf api support
        if (!selfModelData.texture.isEmpty()){
            Identifier texture =new Identifier( selfModelData.texture);
            if(MinecraftClient.getInstance().getResourceManager().getResource(texture).isPresent()){
                customTexture = texture;
            }else{
                customTexture = null;
            }
        }else{
            customTexture = null;
        }

        //grab booleans to avoid further contains checks
        boolean invX = selfModelData.invertAxis.contains("x");
        boolean invY = selfModelData.invertAxis.contains("y");
        boolean invZ = selfModelData.invertAxis.contains("z");

       // this.invX = invX;
        //this.invY = invY;
        //this.invZ = invZ;
        //selfModelData.

        //these ones need to change due to some unknown bullshit
        float translateX= selfModelData.translate[0];
        float translateY= selfModelData.translate[1];
        float translateZ= selfModelData.translate[2];

        double rotateX= Math.toRadians( selfModelData.rotate[0]);
        double rotateY= Math.toRadians(selfModelData.rotate[1]);
        double rotateZ= Math.toRadians(selfModelData.rotate[2]);


//        if (vanillaPartOfThis != null && selfModelData.attach) {
//            System.out.println("ran");
//            ModelTransform def = vanillaPartOfThis.getTransform();
//            translateX= def.pivotX*2;
//            translateY= def.pivotY*2;
//            translateZ= def.pivotZ*2;
//
//            rotateX= def.pitch;
//            rotateY= def.yaw;
//            rotateZ= def.roll;
//
//        }

        //figure out the bullshit
        if( invX){
            rotateX = -rotateX;
            translateX = -translateX;
        }else{
            //nothing? just an invert?
        }
        if( invY){
            rotateY = -rotateY;
            translateY = -translateY;
        }
        if( invZ){
            rotateZ = -rotateZ;
            translateZ = -translateZ;
        }


        float[] sendToFirstChild = {0,0,0};
        // this if statement aged me by like 5 years to brute force figure out
        // the logic of this is utterly essential to correct model positioning of jems
        // and isn't #$@%!@$# documented anywhere that I found
        // I cannot even articulate how many variations of this I had to try
        if(parentNumber == 0){// && selfModelData.boxes.length == 0){
            sendToFirstChild = new float[]{translateX, translateY, translateZ};
            pivotX = translateX;//0;
            pivotY = 24 - translateY ;//24;//0; 24 makes it look nice normally but animations need to include it separately
            pivotZ = translateZ;//0;
        }else if(parentNumber == 1 ){
            float parent0sTX = pivotModifyForParNum1Only[0];
            float parent0sTY = pivotModifyForParNum1Only[1];
            float parent0sTZ = pivotModifyForParNum1Only[2];
            parentOnePivotXOverride =  parent0sTX;
            parentOnePivotYOverride = parent0sTY;
            parentOnePivotZOverride =  parent0sTZ;
            pivotX = parent0sTX + translateX;
            pivotY = parent0sTY + translateY;// pivotModifyForParNum1Only[1];
            pivotZ = parent0sTZ + translateZ;
        }else{// of course it just suddenly acts normal after the first 2 :L
            pivotX = translateX;
            pivotY = translateY;
            pivotZ = translateZ;
        }

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

        pitch = (float) rotateX;
        yaw = (float) rotateY;
        roll = (float) rotateZ;

        //seems to be just straight into model no bullshit?
        //todo check up on scale?
        xScale = selfModelData.scale;
        yScale = selfModelData.scale;
        zScale = selfModelData.scale;

        this.setDefaultTransform(this.getTransform());

        boolean removePivotValue = parentNumber == 0;// && selfModelData.boxes.length!=0;//   parentNumber == 0;//something about zombie headwear doesn't need this'
        createCuboidsFromBoxDataV2(invX, invY, invZ,removePivotValue);
        if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("data = " + selfModelData.toString(false));
        for (EMF_ModelData sub : selfModelData.submodels) {
            //seems like i need to alter parentcount 1's pivots
            ArrayList<EMF_ModelData> hold = new ArrayList<>(parentEMFmodelData);
            hold.add(selfModelData);
            while(children.containsKey(sub.id)){
                sub.id = sub.id+"-";
            }
            children.put(sub.id, new EMF_ModelPart(this,parentNumber + 1, sub, hold,sendToFirstChild,null,null,this.thisModel));
        }
        boolean emptyChildren = true;
        for (EMF_ModelPart child:
             children.values()) {
            if(!child.isEmptyPart){
                emptyChildren = false;
                break;
            }
        }
        isEmptyPart = cuboids.isEmpty() && emptyChildren;

//        if("right_arm".equals(selfModelData.part)){
//            System.out.println("empty="+isEmptyPart);
//        }
        //give the super our children and cuboids incase they are needed by some other mod or process
        //using my own maps as the default ones are private final etc
        ((ModelPartAccessor)this).setChildren(new HashMap<String, ModelPart>(children));
        ((ModelPartAccessor)this).setCuboids(new ArrayList<Cuboid>(cuboids));
    }

    public boolean isEmptyPart = false;
    private void createCuboidsFromBoxDataV2(boolean invX, boolean invY, boolean invZ, boolean parentZero) {
        if (selfModelData.boxes.length > 0) {
            try {
                for (EMF_BoxData box :
                        selfModelData.boxes) {
                    EMFCuboid cube;

                    //already figures this out in v1
                    //figures it would match to the invert values fml...

                    //seems it needs to include the full box value aswell
                    //moved all coord processing to here
                    float[] coOrds = box.coordinates;

                    //this should be added
                    if(parentZero) {
                        coOrds[0] += selfModelData.translate[0];
                        coOrds[1] += selfModelData.translate[1];
                        coOrds[2] += selfModelData.translate[2];
                    }
                    //then invert?
                    if(invX){
                        coOrds[0] = -coOrds[0] - coOrds[3];
                    }
                    if(invY){
                        coOrds[1] = -coOrds[1]- coOrds[4];
                    }
                    if(invZ){//todo check this as not used in fresh animations
                        coOrds[2] = -coOrds[2]- coOrds[5];
                    }





                    if (box.textureOffset.length == 2) {
                        //System.out.println("non custom uv box ignoring for now");
                        cube = new EMFCuboid(selfModelData,
                                box.textureOffset[0],box.textureOffset[1],
                                coOrds[0],coOrds[1],coOrds[2],
                                coOrds[3], coOrds[4], coOrds[5],
                                box.sizeAdd, box.sizeAdd, box.sizeAdd,
                                selfModelData.textureSize[0], selfModelData.textureSize[1]);//selfModelData.invertAxis);
                    } else {
                        //create a custom uv cuboid
                        cube = new EMFCuboid(selfModelData,
                                box.uvDown, box.uvUp, box.uvNorth,
                                box.uvSouth, box.uvWest, box.uvEast,
                                coOrds[0],coOrds[1],coOrds[2],
                                coOrds[3], coOrds[4], coOrds[5],
                                box.sizeAdd, box.sizeAdd, box.sizeAdd,
                                selfModelData.textureSize[0], selfModelData.textureSize[1]);//selfModelData.invertAxis);
                    }
                    cuboids.add(cube);
                }

            } catch (Exception e) {
                EMFUtils.EMF_modMessage("cuboid construction broke: " + e, false);

            }
        }

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
        public EMFCuboid(EMF_ModelData selfModelData
                , float textureU, float textureV,
                         float cubeX, float cubeY, float cubeZ,
                         float sizeX, float sizeY, float sizeZ,
                         float extraX, float extraY, float extraZ,
                         float textureWidth, float textureHeight) {

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
            float k = (float) textureU + sizeZ;
            float l = (float) textureU + sizeZ + sizeX;
            float m = (float) textureU + sizeZ + sizeX + sizeX;
            float n = (float) textureU + sizeZ + sizeX + sizeZ;
            float o = (float) textureU + sizeZ + sizeX + sizeZ + sizeX;
            float p = textureV;
            float q = (float) textureV + sizeZ;
            float r = (float) textureV + sizeZ + sizeY;

            try {
               // sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight,false, Direction.DOWN);
                sides.add( new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight,false, Direction.DOWN));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-dwn failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight,false, Direction.UP));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-up failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight,false, Direction.WEST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-west failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight,false, Direction.NORTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-nrth failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight,false, Direction.EAST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-east failed for "+selfModelData.id);
            }
            try {
                sides.add(  new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight,false, Direction.SOUTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-sth failed for "+selfModelData.id);
            }
            ((CuboidAccessor) this).setSides(sides.toArray(new Quad[0]));
        }

       // private static final Quad blankQuad = new Quad(new Vertex[]{0, 0, 0, 0}, 0, 0, 0, 0, 0, 0,false, Direction.NORTH);

        //Cuboid with custom UVs
        public EMFCuboid(EMF_ModelData selfModelData,
                         float[] uvDown, float[] uvUp, float[] uvNorth, float[] uvSouth, float[] uvWest, float[] uvEast,
                         float cubeX, float cubeY, float cubeZ,
                         float sizeX, float sizeY, float sizeZ,
                         float extraX, float extraY, float extraZ,
                         float textureWidth, float textureHeight) {

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
                sides.add( new Quad(new Vertex[]{vertex, vertex2, vertex6, vertex5},uvUp[0], uvUp[1], uvUp[2], uvUp[3], textureWidth, textureHeight,false,  Direction.DOWN));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-up failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(new Vertex[]{vertex8, vertex7, vertex3, vertex4},//actually down
                        uvDown[0], uvDown[1], uvDown[2], uvDown[3], textureWidth, textureHeight, false, Direction.UP));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-down failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                        uvWest[0], uvWest[1], uvWest[2], uvWest[3], textureWidth, textureHeight,false,  Direction.EAST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-west failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3},
                        uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3], textureWidth, textureHeight,false, Direction.NORTH));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-north failed for "+selfModelData.id);
                }
            try {
                sides.add( new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4},
                        uvEast[0], uvEast[1], uvEast[2], uvEast[3], textureWidth, textureHeight,false, Direction.WEST));
            }catch (Exception e){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("uv-east failed for "+selfModelData.id);
            }
            try {
                sides.add( new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                        uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3], textureWidth, textureHeight,false,Direction.SOUTH));
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

    public Object2ReferenceOpenHashMap<String, EMF_ModelPart> getAllParts(){
        Object2ReferenceOpenHashMap<String, EMF_ModelPart> list = new Object2ReferenceOpenHashMap<>();
        for (EMF_ModelPart part :
                children.values()) {
            list.put(part.selfModelData.id,part);
            list.putAll(part.getAllParts());
        }
        return list;
    }


}
