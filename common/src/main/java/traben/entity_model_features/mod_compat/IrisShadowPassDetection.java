package traben.entity_model_features.mod_compat;

import net.irisshaders.iris.api.v0.IrisApi;
import traben.entity_texture_features.ETF;

import java.util.Objects;

public abstract class IrisShadowPassDetection {

    public abstract boolean inShadowPass();

    private static IrisShadowPassDetection instance;
    public static IrisShadowPassDetection getInstance() {
        if (instance == null) {
            try{
                instance = new IrisShadowPassDetectionImpl();
            } catch (Throwable e) {
                instance = new IrisShadowPassDetection() {
                    @Override
                    public boolean inShadowPass() {
                        return false;
                    }
                };
            }
        }
        return instance;
    }

    private static class IrisShadowPassDetectionImpl extends IrisShadowPassDetection {

        IrisShadowPassDetectionImpl(){
            if (!ETF.IRIS_DETECTED) throw new RuntimeException("Iris not detected, cannot use this class");
            Objects.requireNonNull(IrisApi.getInstance()) ;
        }
        @Override
        public boolean inShadowPass() {
            return IrisApi.getInstance().isRenderingShadowPass();
        }
    }
}
