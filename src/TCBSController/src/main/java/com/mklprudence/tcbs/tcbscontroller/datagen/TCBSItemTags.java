package com.mklprudence.tcbs.tcbscontroller.datagen;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TCBSItemTags extends ItemTagsProvider {
    public TCBSItemTags(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, TCBSController.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        tag(Tags.Items.ORES)
                .add(Registration.EXAMPLE_BLOCK_ITEM.get());
    }

    @Override
    public String getName() {
        return "TCBS Item Tags";
    }
}
