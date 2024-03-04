package traben.entity_model_features.models;

import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollHook;
import net.diebuddies.physics.ragdoll.RagdollMapper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

import java.util.List;

public class EMFCustomRagDollHookTest implements RagdollHook {

    @Override
    public void map(Ragdoll ragdoll, Entity entity, EntityModel entityModel) {

        if (((IEMFModel) entityModel).emf$isEMFModel()) {
            //this is the custom class model root
            EMFModelPartRoot root = ((IEMFModel) entityModel).emf$getEMFRootModel();

            //this root is the raw original vanilla model root
//            ModelPart rawVanillaRoot = root.vanillaRoot;

            //this root is made of regular ModelPart classes and none have empty cuboids
            ModelPart currentStateAsVanillaModelParts = root.getVanillaFormatRoot();

//            System.out.println("EMF");
//            RagdollMapper.printModelPart(root,0,false);
//            System.out.println("RAW VANILLA");
//            RagdollMapper.printModelPart(rawVanillaRoot,0,false);
//            System.out.println("///////MODIFIED EMF////////");
//            RagdollMapper.printModelPart(currentStateAsVanillaModelParts,0,false);

            //none of them work here
            RagdollMapper.Counter counter = new RagdollMapper.Counter();
            RagdollMapper.getCuboids(ragdoll, currentStateAsVanillaModelParts, counter, true);

            //not sure what I'm doing here
            ragdoll.addConnection(1, 0, true);

        }
    }


    @Override
    public void filterCuboidsFromEntities(List<PhysicsEntity> list, Entity entity, EntityModel entityModel) {

    }
}
