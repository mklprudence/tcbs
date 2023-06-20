package com.mklprudence.tcbs.tcbscontroller.datagen;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TCBSBlockStates extends BlockStateProvider {


    public TCBSBlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TCBSController.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(Registration.EXAMPLE_BLOCK.get(), cubeAll(Registration.EXAMPLE_BLOCK.get()));
    }
}
