package com.mklprudence.tcbs.tcbscontroller.setup;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.block.TurtleBE;
import com.mklprudence.tcbs.tcbscontroller.block.TurtleBlock;
import com.mklprudence.tcbs.tcbscontroller.gui.ControllerItem;
import com.mklprudence.tcbs.tcbscontroller.gui.ControllerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TCBSController.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TCBSController.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TCBSController.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TCBSController.MODID);

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        MENUS.register(modEventBus);
    }

    public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();

    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BLOCK_PROPERTIES));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = fromBlock(EXAMPLE_BLOCK);

    public static final RegistryObject<TurtleBlock> TURTLE = BLOCKS.register("turtle", () -> new TurtleBlock());
    public static final RegistryObject<Item> TURTLE_ITEM = fromBlock(TURTLE);
    public static final RegistryObject<BlockEntityType<TurtleBE>> TURTLE_BE = BLOCK_ENTITIES.register("turtle", () -> BlockEntityType.Builder.of(TurtleBE::new, TURTLE.get()).build(null));

    public static final RegistryObject<Item> CONTROLLER_ITEM = ITEMS.register("controller", ControllerItem::new);

    public static final RegistryObject<MenuType<ControllerMenu>> CONTROLLER_MENU = MENUS.register("controller", () -> IForgeMenuType.create(((windowId, inv, data) -> new ControllerMenu(windowId, inv))));

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
