package traben.entity_model_features.models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import traben.entity_model_features.EMF;
import traben.entity_model_features.mixin.accessor.CuboidAccessor;
import traben.entity_model_features.models.jem_objects.EMFBoxData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.*;
import java.util.function.Consumer;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartCustom extends EMFModelPart {


    public final String partToBeAttached;
    public final String id;
    public final boolean attach;

    private final float defaultScale;

    private final @Nullable List<Consumer<PoseStack>> attachments;

    public EMFModelPartCustom(EMFPartData emfPartData, int variant, @Nullable String part, String id) {//,//float[] parentalTransforms) {

        super(getCuboidsFromData(emfPartData), getChildrenFromData(emfPartData, variant));
        this.attach = emfPartData.attach;
        this.partToBeAttached = part;
        this.id = id;
        //selfModelData = emfPartData;
        textureOverride = emfPartData.getCustomTexture();

        var attachments = emfPartData.getAttachments();
        this.attachments = attachments.isEmpty() ? null : attachments;


        //seems to be just straight into model no bullshit?
        defaultScale = emfPartData.scale;
        xScale = defaultScale;
        yScale = defaultScale;
        zScale = defaultScale;


        x = emfPartData.translate[0];
        y = emfPartData.translate[1];
        z = emfPartData.translate[2];

        xRot = emfPartData.rotate[0];
        yRot = emfPartData.rotate[1];
        zRot = emfPartData.rotate[2];

        this.setInitialPose(this.storePose());

        if (EMF.config().getConfig().logModelCreationData)
            EMFUtils.log(" > > EMF custom part made: " + emfPartData.id);
        //if (variantNumber == 0)


    }

    @Override
    public void resetPose() {
        super.resetPose();
        xScale = defaultScale;
        yScale = defaultScale;
        zScale = defaultScale;
    }

    private static List<Cube> getCuboidsFromData(EMFPartData emfPartData) {
        return createCuboidsFromBoxDataV3(emfPartData);

    }

    private static Map<String, ModelPart> getChildrenFromData(EMFPartData emfPartData, int variant) {
        Map<String, ModelPart> emfChildren = new HashMap<>();
        for (EMFPartData sub : emfPartData.submodels) {
            String idUnique = EMFUtils.getIdUnique(emfChildren.keySet(), sub.id);
            emfChildren.put(idUnique, new EMFModelPartCustom(sub, variant, null, idUnique));
        }
        return emfChildren;
    }

    private static List<Cube> createCuboidsFromBoxDataV3(EMFPartData emfPartData) {
        List<Cube> emfCuboids = new LinkedList<>();
        if (emfPartData.boxes.length > 0) {
            try {
                for (EMFBoxData box :
                        emfPartData.boxes) {
                    Cube cube;

                    if (box.textureOffset.length == 2) {
                        //System.out.println("non custom uv box ignoring for now");
                        cube = emfCuboidOf(emfPartData,
                                box.textureOffset[0], box.textureOffset[1],
                                box.coordinates[0], box.coordinates[1], box.coordinates[2],
                                box.coordinates[3], box.coordinates[4], box.coordinates[5],
                                box.sizeAddX, box.sizeAddY, box.sizeAddZ,
                                emfPartData.textureSize[0], emfPartData.textureSize[1],
                                emfPartData.mirrorTexture.contains("u"), emfPartData.mirrorTexture.contains("v"));//selfModelData.invertAxis);
                    } else {
                        //create a custom uv cuboid
                        cube = emfCuboidOf(emfPartData,
                                box.uvDown, box.uvUp, box.uvNorth,
                                box.uvSouth, box.uvWest, box.uvEast,
                                box.coordinates[0], box.coordinates[1], box.coordinates[2],
                                box.coordinates[3], box.coordinates[4], box.coordinates[5],
                                box.sizeAddX, box.sizeAddY, box.sizeAddZ,
                                emfPartData.textureSize[0], emfPartData.textureSize[1],
                                emfPartData.mirrorTexture.contains("u"), emfPartData.mirrorTexture.contains("v"));//selfModelData.invertAxis);
                    }
                    emfCuboids.add(cube);
                }

            } catch (Exception e) {
                EMFUtils.log("cuboid construction broke: " + e, false);

            }
        }

        return emfCuboids;
    }

    //cuboid without custom UVs
    public static Cube emfCuboidOf(EMFPartData selfModelData
            , float textureU, float textureV,
                                     float cubeX, float cubeY, float cubeZ,
                                     float sizeX, float sizeY, float sizeZ,
                                     float extraX, float extraY, float extraZ,
                                     float textureWidth, float textureHeight,
                                     boolean mirrorU, boolean mirrorV) {

        Cube cube = new Cube((int) textureU, (int) textureV,
                cubeX, cubeY, cubeZ,
                sizeX, sizeY, sizeZ,
                extraX, extraY, extraZ, false,
                textureWidth, textureHeight, new HashSet<>() {{
            addAll(List.of(Direction.values()));
        }});

        CuboidAccessor accessor = (CuboidAccessor) cube;
        accessor.setMinX(cubeX);
        accessor.setMinY(cubeY);
        accessor.setMinZ(cubeZ);
        accessor.setMaxX(cubeX + sizeX);
        accessor.setMaxY(cubeY + sizeY);
        accessor.setMaxZ(cubeZ + sizeZ);
        //Quad[] sides = new Quad[6];
        ArrayList<Polygon> sides = new ArrayList<>();
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
        @SuppressWarnings("UnnecessaryLocalVariable")
        float j = textureU;
        float k = textureU + sizeZ;
        float l = textureU + sizeZ + sizeX;
        float m = textureU + sizeZ + sizeX + sizeX;
        float n = textureU + sizeZ + sizeX + sizeZ;
        float o = textureU + sizeZ + sizeX + sizeZ + sizeX;
        @SuppressWarnings("UnnecessaryLocalVariable")
        float p = textureV;
        float q = textureV + sizeZ;
        float r = textureV + sizeZ + sizeY;

        try {
            // sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight,false, Direction.DOWN);
            sides.add(new Polygon(mirrorV ? new Vertex[]{vertex3, vertex4, vertex8, vertex7} : new Vertex[]{vertex6, vertex5, vertex, vertex2},
                    //k, p, l, q,
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? l : k,
                    mirrorV ? q : p,
                    mirrorU ? k : l,
                    mirrorV ? p : q,
                    textureWidth, textureHeight, false, mirrorV ? Direction.UP : Direction.DOWN));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-dwn failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(mirrorV ? new Vertex[]{vertex6, vertex5, vertex, vertex2} : new Vertex[]{vertex3, vertex4, vertex8, vertex7},
                    //l, q, m, p,
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? m : l,
                    mirrorV ? p : q,
                    mirrorU ? l : m,
                    mirrorV ? q : p,
                    textureWidth, textureHeight, false, mirrorV ? Direction.DOWN : Direction.UP));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-up failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                    //j, q, k, r,
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? k : j,
                    mirrorV ? r : q,
                    mirrorU ? j : k,
                    mirrorV ? q : r,
                    textureWidth, textureHeight, false, mirrorU ? Direction.EAST : Direction.WEST));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-west failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3},
                    // k, q, l, r,
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? l : k,
                    mirrorV ? r : q,
                    mirrorU ? k : l,
                    mirrorV ? q : r,
                    textureWidth, textureHeight, false, Direction.NORTH));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-nrth failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                    //l, q, n, r,
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? n : l,
                    mirrorV ? r : q,
                    mirrorU ? l : n,
                    mirrorV ? q : r,
                    textureWidth, textureHeight, false, mirrorU ? Direction.WEST : Direction.EAST));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-east failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                    // n, q, o, r,
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? o : n,
                    mirrorV ? r : q,
                    mirrorU ? n : o,
                    mirrorV ? q : r,
                    textureWidth, textureHeight, false, Direction.SOUTH));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-sth failed for " + selfModelData.id);
        }


        ((CuboidAccessor) cube).setPolygons(sides.toArray(new Polygon[0]));
        return cube;
    }

    //Cuboid with custom UVs
    public static Cube emfCuboidOf(EMFPartData selfModelData,
                                     float[] uvDown, float[] uvUp, float[] uvNorth, float[] uvSouth, float[] uvWest, float[] uvEast,
                                     float cubeX, float cubeY, float cubeZ,
                                     float sizeX, float sizeY, float sizeZ,
                                     float extraX, float extraY, float extraZ,
                                     float textureWidth, float textureHeight,
                                     boolean mirrorU, boolean mirrorV) {

        Cube cube = new Cube(0, 0,
                cubeX, cubeY, cubeZ,
                sizeX, sizeY, sizeZ,
                extraX, extraY, extraZ, false,
                textureWidth, textureHeight, new HashSet<>() {{
            addAll(List.of(Direction.values()));
        }});

        CuboidAccessor accessor = (CuboidAccessor) cube;
        accessor.setMinX(cubeX);
        accessor.setMinY(cubeY);
        accessor.setMinZ(cubeZ);
        accessor.setMaxX(cubeX + sizeX);
        accessor.setMaxY(cubeY + sizeY);
        accessor.setMaxZ(cubeZ + sizeZ);
        //Quad[] sides = new Quad[6];
        ArrayList<Polygon> sides = new ArrayList<>();

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
            sides.add(new Polygon(mirrorV ? new Vertex[]{vertex8, vertex7, vertex3, vertex4} : new Vertex[]{vertex, vertex2, vertex6, vertex5},
                    mirrorU ? uvUp[2] : uvUp[0],
                    mirrorV ? uvUp[3] : uvUp[1],
                    mirrorU ? uvUp[0] : uvUp[2],
                    mirrorV ? uvUp[1] : uvUp[3],
                    textureWidth, textureHeight, false, mirrorV ? Direction.UP : Direction.DOWN));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-up failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(mirrorV ? new Vertex[]{vertex, vertex2, vertex6, vertex5} : new Vertex[]{vertex8, vertex7, vertex3, vertex4},//actually down
                    // uvDown[0], uvDown[1], uvDown[2], uvDown[3],
                    mirrorU ? uvDown[2] : uvDown[0],
                    mirrorV ? uvDown[3] : uvDown[1],
                    mirrorU ? uvDown[0] : uvDown[2],
                    mirrorV ? uvDown[1] : uvDown[3],
                    textureWidth, textureHeight, false, mirrorV ? Direction.DOWN : Direction.UP));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-down failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(mirrorU ? new Vertex[]{vertex, vertex5, vertex8, vertex4} : new Vertex[]{vertex6, vertex2, vertex3, vertex7},
                    // uvWest[0], uvWest[1], uvWest[2], uvWest[3],
                    mirrorU ? uvWest[2] : uvWest[0],
                    mirrorV ? uvWest[3] : uvWest[1],
                    mirrorU ? uvWest[0] : uvWest[2],
                    mirrorV ? uvWest[1] : uvWest[3],
                    textureWidth, textureHeight, false, mirrorU ? Direction.WEST : Direction.EAST));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-west failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3},
                    //uvNorth[0], uvNorth[1], uvNorth[2], uvNorth[3],
                    mirrorU ? uvNorth[2] : uvNorth[0],
                    mirrorV ? uvNorth[3] : uvNorth[1],
                    mirrorU ? uvNorth[0] : uvNorth[2],
                    mirrorV ? uvNorth[1] : uvNorth[3],
                    textureWidth, textureHeight, false, Direction.NORTH));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-north failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(mirrorU ? new Vertex[]{vertex6, vertex2, vertex3, vertex7} : new Vertex[]{vertex, vertex5, vertex8, vertex4},
                    //uvEast[0], uvEast[1], uvEast[2], uvEast[3],
                    mirrorU ? uvEast[2] : uvEast[0],
                    mirrorV ? uvEast[3] : uvEast[1],
                    mirrorU ? uvEast[0] : uvEast[2],
                    mirrorV ? uvEast[1] : uvEast[3],
                    textureWidth, textureHeight, false, mirrorU ? Direction.EAST : Direction.WEST));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-east failed for " + selfModelData.id);
        }
        try {
            sides.add(new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8},
                    //uvSouth[0], uvSouth[1], uvSouth[2], uvSouth[3],
                    mirrorU ? uvSouth[2] : uvSouth[0],
                    mirrorV ? uvSouth[3] : uvSouth[1],
                    mirrorU ? uvSouth[0] : uvSouth[2],
                    mirrorV ? uvSouth[1] : uvSouth[3],
                    textureWidth, textureHeight, false, Direction.SOUTH));
        } catch (Exception e) {
            if (EMF.config().getConfig().logModelCreationData)
                EMFUtils.log("uv-south failed for " + selfModelData.id);
        }


        ((CuboidAccessor) cube).setPolygons(sides.toArray(new Polygon[0]));
        return cube;
    }

    @Override
    public String toString() {
        return "[custom part " + id + "], cubes =" + cubes.size() + ", children = " + children.size();
    }

    @Override
    public String toStringShort() {
        return "[custom part " + id.replaceFirst("EMF_", "") + "]";
    }

    // private static final Quad blankQuad = new Quad(new Vertex[]{0, 0, 0, 0}, 0, 0, 0, 0, 0, 0,false, Direction.NORTH);

    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, #if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {
        if (attachments != null) {
            for (Consumer<PoseStack> attachment : attachments) {
                matrices.pushPose();
                this.translateAndRotate(matrices);
                attachment.accept(matrices);
                matrices.popPose();
            }
        }

        switch (EMF.config().getConfig().renderModeChoice) {
            case NORMAL -> renderWithTextureOverride(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
            case GREEN -> {
                float flash = (float) Math.abs(Math.sin(System.currentTimeMillis() / 1000d));
                #if MC >= MC_21
                    var col = FastColor.ARGB32.color(
                            (int) (255 * flash),
                            FastColor.ARGB32.green(k),
                            (int) (255 * flash),
                            FastColor.ARGB32.alpha(k)
                            );
                    renderWithTextureOverride(matrices, vertices, light, overlay, col);
                #else
                    renderWithTextureOverride(matrices, vertices, light, overlay, flash, green, flash, alpha);
                #endif
            }
            case LINES ->
                    renderBoxes(matrices, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines()));
            case LINES_AND_TEXTURE -> {
                renderWithTextureOverride(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                renderBoxesNoChildren(matrices, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines()), 1f);
            }
            case LINES_AND_TEXTURE_FLASH -> {
                renderWithTextureOverride(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                float flash = (float) (Math.sin(System.currentTimeMillis() / 1000d) + 1) / 2f;
                renderBoxesNoChildren(matrices, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines()), flash);
            }
            case NONE -> {
            }
        }
    }

    @Override
    void renderWithTextureOverride(final PoseStack matrices, final VertexConsumer vertices, final int light, final int overlay,#if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {
        //do not render if this is a custom part and are rendering a feature overlay
        if (textureOverride != null && lastTextureOverride == EMFManager.getInstance().entityRenderCount) return;

        //otherwise render as normal
        super.renderWithTextureOverride(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
    }
}
