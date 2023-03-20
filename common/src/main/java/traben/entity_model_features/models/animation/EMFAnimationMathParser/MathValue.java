package traben.entity_model_features.models.animation.EMFAnimationMathParser;

import net.minecraft.entity.Entity;
import traben.entity_model_features.models.animation.EMFAnimation;

public abstract class MathValue implements  MathComponent{


    MathValue(boolean isNegative, EMFAnimation calculationInstance){
        this.isNegative = isNegative;
        this.calculationInstance =  calculationInstance;
    }
    MathValue(boolean isNegative){
        this.isNegative = isNegative;
        this.calculationInstance =  null;
    }
    MathValue(){
        this.isNegative = false;
        this.calculationInstance =  null;
    }

    final EMFAnimation calculationInstance;
    public boolean isNegative;

    abstract public ValueSupplier getSupplier();

    public void print(String str){
        if(calculationInstance != null)
            calculationInstance.animPrint(str);
    }

    @Override
    public double get() {
        if(calculationInstance != null)
            calculationInstance.indentCount++;
        double ret = isNegative ? -getSupplier().get() : getSupplier().get();
        if(calculationInstance != null)
            calculationInstance.indentCount--;
        return ret;
    }

    public void makeNegative(boolean become){
        if(become) isNegative = !isNegative;
    }

    public interface ValueSupplier{
        double get();
    }

    public interface AnimationValueSupplier{

//        default float get(){
//            return get(null);
//        }
        float get(Entity entity);

    }

}
