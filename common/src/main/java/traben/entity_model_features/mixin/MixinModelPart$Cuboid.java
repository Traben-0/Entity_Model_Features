package traben.entity_model_features.mixin;

import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFCuboidDataSupplier;

import java.util.Set;

@Mixin(ModelPart.Cuboid.class)
public class MixinModelPart$Cuboid implements EMFCuboidDataSupplier {


    @Unique
    private int[] emf$textureUV = null;

    @Unique
    private float[] emf$sizeAdd = null;

    @Unique
    private int[] emf$textureXY = null;

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void emf$injectAnnouncer(final int u, final int v, final float x, final float y, final float z, final float sizeX, final float sizeY, final float sizeZ, final float extraX, final float extraY, final float extraZ, final boolean mirror, final float textureWidth, final float textureHeight, final Set<?> set, final CallbackInfo ci) {
        if (EMF.config().getConfig().modelExportMode.doesLog()) {
            emf$textureUV = new int[]{u, v};
            emf$sizeAdd = new float[]{extraX, extraY, extraZ};
            emf$textureXY = new int[]{(int) textureWidth, (int) textureHeight};
        }
    }

    @Override
    public int[] emf$getTextureUV() {
        return emf$textureUV;
    }

    @Override
    public int[] emf$getTextureXY() {
        return emf$textureXY;
    }

    @Override
    public float[] emf$getSizeAdd() {
        return emf$sizeAdd;
    }
}