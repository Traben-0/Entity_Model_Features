package traben.entity_model_features.mod_compat;

import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;

import java.util.Objects;

public abstract class IrisShadowPassDetection {

    public abstract boolean inShadowPass();

    private static IrisShadowPassDetection instance;
    public static IrisShadowPassDetection getInstance() {
        //#if IRIS
        if (instance == null) {
            try{
                instance = new IrisShadowPassDetectionImpl();
            } catch (Throwable e) {
                EMFUtils.log("EMF did not find the Iris API, disabling shadow pass detection");
        //#endif
                instance = new IrisShadowPassDetection() {
                    @Override
                    public boolean inShadowPass() {
                        return false;
                    }
                };
        //#if IRIS
            }
        }
        //#endif
        return instance;
    }
    //#if IRIS
    private static class IrisShadowPassDetectionImpl extends IrisShadowPassDetection {

        IrisShadowPassDetectionImpl(){
            if (!ETF.IRIS_DETECTED) throw new RuntimeException("Iris not detected, cannot use this class");
            Objects.requireNonNull(net.irisshaders.iris.api.v0.IrisApi.getInstance()) ;
        }
        @Override
        public boolean inShadowPass() {
            return net.irisshaders.iris.api.v0.IrisApi.getInstance().isRenderingShadowPass();
        }
    }
    //#endif
}
