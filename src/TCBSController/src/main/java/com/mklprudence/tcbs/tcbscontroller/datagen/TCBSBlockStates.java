package com.mklprudence.tcbs.tcbscontroller.datagen;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class TCBSBlockStates extends BlockStateProvider {


    public TCBSBlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TCBSController.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(Registration.EXAMPLE_BLOCK.get(), cubeAll(Registration.EXAMPLE_BLOCK.get()));
        registerTurtle();
    }

    private void registerTurtle() {
        BlockModelBuilder frame = models().getBuilder("block/turtle");
        frame.parent(models().getExistingFile(mcLoc("block/block")));

        frame.element()
                .from(2f, 2f, 2f)
                .to(14f, 14f, 13f)
                .allFaces((direction, faceBuilder) -> {
                    switch (direction) {
                        case DOWN -> {faceBuilder.texture("#texture").uvs(5.75f, 2.75f, 2.75f, 0f);}
                        case UP -> {faceBuilder.texture("#texture").uvs(8.75f, 0f, 5.75f, 2.75f);}
                        case NORTH -> {faceBuilder.texture("#texture").uvs(11.5f, 5.75f, 8.5f, 2.75f);}
                        case SOUTH -> {faceBuilder.texture("#texture").uvs(5.75f, 5.75f, 2.75f, 2.75f);}
                        case WEST -> {faceBuilder.texture("#texture").uvs(8.5f, 5.75f, 5.75f, 2.75f);}
                        case EAST -> {faceBuilder.texture("#texture").uvs(2.75f, 5.75f, 0f, 2.75f);}
                    }
                })
                .end();

        ModelBuilder.ElementBuilder backCube = frame.element()
                .from(3f, 6f, 13f)
                .to(13f, 13f, 15f);
        backCube.face(Direction.DOWN).texture("#texture").uvs(11.75f, 0.5f, 9.25f, 0f);
        backCube.face(Direction.UP).texture("#texture").uvs(14.25f, 0f, 11.75f, 0.5f);
        backCube.face(Direction.SOUTH).texture("#texture").uvs(11.75f, 2.25f, 9.25f, 0.5f);
        backCube.face(Direction.WEST).texture("#texture").uvs(12.25f, 2.25f, 11.75f, 0.5f);
        backCube.face(Direction.EAST).texture("#texture").uvs(9.25f, 2.25f, 8.75f, 0.5f);
        backCube.end();

        frame.texture("texture", modLoc("block/turtle"));
        frame.renderType("translucent");

        horizontalBlock(Registration.TURTLE.get(), frame);
    }

    private void floatingCube(BlockModelBuilder builder, float fx, float fy, float fz, float tx, float ty, float tz) {
        builder.element()
                .from(fx, fy, fz)
                .to(tx, ty, tz)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#window"))
                .end();
    }
}
