package com.mklprudence.tcbs.tcbscontroller.datagen;

import com.mklprudence.tcbs.tcbscontroller.TCBSController;
import com.mklprudence.tcbs.tcbscontroller.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class TCBSLanguageProvider extends LanguageProvider {
    public TCBSLanguageProvider(PackOutput output, String locale) {
        super(output, TCBSController.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.tcbscontroller", "TCBS Controller");
        add(Registration.EXAMPLE_BLOCK.get(), "Example block");
        add(Registration.TURTLE.get(), "Turtle");
        add(Registration.CONTROLLER_ITEM.get(), "Controller");

        add("screen.controller", "Controller");

        add("screen.controller.button.Reload", "\u27F3");

        add("screen.controller.button.Forward", "\u23FA");
        add("screen.controller.button.Backward", "\u2A00");
        add("screen.controller.button.Up", "\u2191");
        add("screen.controller.button.Down", "\u2193");
        add("screen.controller.button.TurnLeft", "\u2190");
        add("screen.controller.button.TurnRight", "\u2192");

        add("screen.controller.button.Dig", "\u26CF");
        add("screen.controller.button.DigUp", "\u2191");
        add("screen.controller.button.DigDown", "\u2193");

        add("screen.controller.button.Place", "\u2592");
        add("screen.controller.button.PlaceUp", "\u2191");
        add("screen.controller.button.PlaceDown", "\u2193");

        add("screen.controller.button.Attack", "\uD83D\uDDE1");
        add("screen.controller.button.AttackUp", "\u2191");
        add("screen.controller.button.AttackDown", "\u2193");

        add("screen.controller.button.Drop", "\u2604");
        add("screen.controller.button.DropUp", "\u2191");
        add("screen.controller.button.DropDown", "\u2193");

        add("screen.controller.button.Suck", "\u25BD");
        add("screen.controller.button.SuckUp", "\u2191");
        add("screen.controller.button.SuckDown", "\u2193");

        add("screen.controller.button.Inspect", "?");
        add("screen.controller.button.InspectUp", "\u2191");
        add("screen.controller.button.InspectDown", "\u2193");

        add("screen.controller.button.Refuel", "\u271A");
        add("screen.controller.button.Terminate", "\u2716");

        add("screen.controller.button.Lua", "\u25B6");

        add("screen.controller.cycler", "TURTLE");

        add("screen.controller.luabox", "Lua Command");
    }
}
