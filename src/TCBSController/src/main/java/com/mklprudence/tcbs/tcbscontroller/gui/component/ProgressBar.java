package com.mklprudence.tcbs.tcbscontroller.gui.component;

import com.mklprudence.tcbs.tcbscontroller.gui.ControllerScreen;
import com.mklprudence.tcbs.tcbscontroller.data.Data;
import com.mklprudence.tcbs.tcbscontroller.data.Turtle;
import com.mklprudence.tcbs.tcbscontroller.network.ActionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Modified from net.minecraft.client.gui.components.BossHealthOverlay
 */
@OnlyIn(Dist.CLIENT)
public class ProgressBar extends GuiComponent {
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int OVERLAY_OFFSET = 80;
    private static final boolean RENDER_LABEL = true;
    private ControllerScreen parent;
    private final Minecraft minecraft;
    //final Map<UUID, LerpingBossEvent> events = Maps.newLinkedHashMap();
    private final LerpingBossEvent event;
    private final Button refuelButton;
    private final int element_Y;


    public ProgressBar(int y, ControllerScreen parent) {
        this.element_Y = y;
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.event = new LerpingBossEvent(
                UUID.randomUUID(),
                Component.literal("Progress"),
                0.0f,
                BossEvent.BossBarColor.YELLOW,
                BossEvent.BossBarOverlay.PROGRESS,
                false,
                false,
                false
        );
        Data.addListener(Data.Side.CLIENT, this::setProgress);
        setProgress();

        refuelButton = new ActionButton(ActionType.Refuel, parent).build();
        refuelButton.visitWidgets(parent::addRenderableWidget);
    }

    public void setProgress() {
        setProgress(Data.CLIENT.getTurtleFromClientID(parent.selectedTurtle).getFuelPercentage());
    }

    public void setProgress(float progress) {
        event.setProgress(progress);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Turtle selectedTurtle = Data.CLIENT.getTurtleFromClientID(parent.selectedTurtle);

        int i = this.minecraft.getWindow().getGuiScaledWidth();
        int barX = i / 2 - (BAR_WIDTH + 30) / 2;
        int barY = RENDER_LABEL ? element_Y + 12 : element_Y + 10 - BAR_HEIGHT / 2;
        RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);
        this.drawBar(pPoseStack, barX, barY, event);

        if (RENDER_LABEL) {
            Component component = Component.literal("Infinite");
            if (selectedTurtle.isConsumeFuel())
                component = Component.literal(String.format("%d / %d", selectedTurtle.getFuelLevel(), selectedTurtle.getFuelLimit()));
            int labelWidth = this.minecraft.font.width(component);
            int labelX = i / 2 - (labelWidth + 30) / 2;
            int labelY = barY - 9;
            this.minecraft.font.drawShadow(pPoseStack, component, (float) labelX, (float) labelY, 16777215);
        }

        refuelButton.setPosition(i / 2 + (BAR_WIDTH + 30) / 2 - 20, element_Y);
        refuelButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void drawBar(PoseStack pPoseStack, int pX, int pY, BossEvent pBossEvent) {
        this.drawBar(pPoseStack, pX, pY, pBossEvent, BAR_WIDTH, 0);
        int i = (int)(pBossEvent.getProgress() * ((float) BAR_WIDTH + 1));
        if (i > 0) {
            this.drawBar(pPoseStack, pX, pY, pBossEvent, i, 5);
        }

    }

    private void drawBar(PoseStack p_232470_, int p_232471_, int p_232472_, BossEvent p_232473_, int p_232474_, int p_232475_) {
        blit(p_232470_, p_232471_, p_232472_, 0, p_232473_.getColor().ordinal() * 5 * 2 + p_232475_, p_232474_, 5);
        if (p_232473_.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            RenderSystem.enableBlend();
            blit(p_232470_, p_232471_, p_232472_, 0, 80 + (p_232473_.getOverlay().ordinal() - 1) * 5 * 2 + p_232475_, p_232474_, 5);
            RenderSystem.disableBlend();
        }

    }

    /*
    public void update(ClientboundBossEventPacket pPacket) {
        pPacket.dispatch(new ClientboundBossEventPacket.Handler() {
            public void add(UUID p_168824_, Component p_168825_, float p_168826_, BossEvent.BossBarColor p_168827_, BossEvent.BossBarOverlay p_168828_, boolean p_168829_, boolean p_168830_, boolean p_168831_) {
                BossHealthOverlay.this.event.put(p_168824_, new LerpingBossEvent(p_168824_, p_168825_, p_168826_, p_168827_, p_168828_, p_168829_, p_168830_, p_168831_));
            }

            public void remove(UUID p_168812_) {
                BossHealthOverlay.this.events.remove(p_168812_);
            }

            public void updateProgress(UUID p_168814_, float p_168815_) {
                BossHealthOverlay.this.events.get(p_168814_).setProgress(p_168815_);
            }

            public void updateName(UUID p_168821_, Component p_168822_) {
                BossHealthOverlay.this.events.get(p_168821_).setName(p_168822_);
            }

            public void updateStyle(UUID p_168817_, BossEvent.BossBarColor p_168818_, BossEvent.BossBarOverlay p_168819_) {
                LerpingBossEvent lerpingbossevent = BossHealthOverlay.this.events.get(p_168817_);
                lerpingbossevent.setColor(p_168818_);
                lerpingbossevent.setOverlay(p_168819_);
            }

            public void updateProperties(UUID p_168833_, boolean p_168834_, boolean p_168835_, boolean p_168836_) {
                LerpingBossEvent lerpingbossevent = BossHealthOverlay.this.events.get(p_168833_);
                lerpingbossevent.setDarkenScreen(p_168834_);
                lerpingbossevent.setPlayBossMusic(p_168835_);
                lerpingbossevent.setCreateWorldFog(p_168836_);
            }
        });
    }

    public void reset() {
        this.events.clear();
    }
     */

    public boolean shouldPlayMusic() {
        return event.shouldPlayBossMusic();
    }

    public boolean shouldDarkenScreen() {
        return event.shouldDarkenScreen();
    }

    public boolean shouldCreateWorldFog() {
        return event.shouldCreateWorldFog();
    }
}
