package com.mklprudence.tcbs.tcbscontroller.datagen;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TCBSBlockTags extends BlockTagsProvider {
    public TCBSBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TCBSController.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(Registration.EXAMPLE_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.EXAMPLE_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(Registration.TURTLE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.TURTLE.get());
    }

    @Override
    public String getName() {
        return "TCBS Block Tags";
    }
}
