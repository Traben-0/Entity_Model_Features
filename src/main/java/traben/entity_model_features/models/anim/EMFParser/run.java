package traben.entity_model_features.models.anim.EMFParser;

import net.minecraft.entity.LivingEntity;
import org.mariuszgromada.math.mxparser.Expression;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.anim.AnimationCalculation;
import traben.entity_model_features.models.anim.AnimationCalculationEMFParser;

public class run {
    public static void main(String[] args) {
        EMF_EntityModel<LivingEntity> bob = new EMF_EntityModel<>("modelPath");



        String ex = "-2+ -4 * pi /if(4 == 0, 8,2) - 2-2";//"((0.8 + 0.6 / (18+4)-8 * - 4.9) % 3) + limb_swing+ -if(3 < 1,4,8)";


        Expression mxp = new Expression(ex);
        mxp.checkSyntax();

        AnimationCalculationEMFParser calc = new AnimationCalculationEMFParser(bob,null, AnimationCalculation.AnimVar.rx,"test",ex);
        //calc.setVerbose(true);
        System.out.println("expression: "+ex);

        System.out.println( "valid ="+calc.isValid());
        //MathExpression exp = new MathExpression(ex,false, null);
        time();
        double result = calc.calculatorRun();
        System.out.println("EMF result = "+result);
        time();
        System.out.println("other parser");
        time();
        double result2 = mxp.calculate();
        System.out.println("MXP result = "+result2);
        time();
        System.out.println("difference = "+ Math.abs(result-result2));
    }

    private static void time(){
        System.out.println("time = "+System.currentTimeMillis());
    }
}
