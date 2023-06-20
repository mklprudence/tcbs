package com.mklprudence.tcbs.tcbscontroller.datagen;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TCBSItemModels extends ItemModelProvider {

    public TCBSItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TCBSController.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(Registration.EXAMPLE_BLOCK_ITEM.getId().getPath(), modLoc("block/example_block"));
        withExistingParent(Registration.TURTLE_ITEM.getId().getPath(), modLoc("block/turtle"));
        basicItem(Registration.CONTROLLER_ITEM.getId());
    }
}
