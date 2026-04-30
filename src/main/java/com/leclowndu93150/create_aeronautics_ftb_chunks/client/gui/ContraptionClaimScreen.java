package com.leclowndu93150.create_aeronautics_ftb_chunks.client.gui;

import com.leclowndu93150.create_aeronautics_ftb_chunks.network.ContraptionAllyPacket;
import com.leclowndu93150.create_aeronautics_ftb_chunks.network.ContraptionClaimActionPacket;
import com.leclowndu93150.create_aeronautics_ftb_chunks.network.ContraptionClaimActionPacket.Action;
import com.leclowndu93150.create_aeronautics_ftb_chunks.network.OpenContraptionScreenPacket;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.VerticalSpaceWidget;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.AbstractThreePanelScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContraptionClaimScreen extends AbstractThreePanelScreen<ContraptionClaimScreen.ContentPanel> {

    private static OpenContraptionScreenPacket pendingData;

    private OpenContraptionScreenPacket data;

    private ContraptionClaimScreen(OpenContraptionScreenPacket data) {
        this.data = data;
        showCloseButton(true);
        showScrollBar(false);
        showBottomPanel(false);
    }

    public static void open(OpenContraptionScreenPacket data) {
        pendingData = data;
        new ContraptionClaimScreen(data).openGui();
    }

    @Override
    public boolean onInit() {
        setWidth(Math.min(250, getWindow().getGuiScaledWidth() - 20));
        setHeight(Math.min(260, getWindow().getGuiScaledHeight() - 20));
        return true;
    }

    @Override
    public Component getTitle() {
        String name = data.shipName().isEmpty() ? "Contraption" : data.shipName();
        return Component.literal(name).withStyle(ChatFormatting.BOLD);
    }

    @Override
    protected void doCancel() { closeGui(); }

    @Override
    protected void doAccept() { closeGui(); }

    @Override
    protected int getTopPanelHeight() { return 20; }

    @Override
    protected ContentPanel createMainPanel() {
        return new ContentPanel(this, pendingData);
    }

    private void sendAction(Action action) {
        playClickSound();
        PacketDistributor.sendToServer(new ContraptionClaimActionPacket(action));

        // Helper to rebuild data with most fields unchanged
        record D(UUID sl, String sn, int cc, boolean cl, boolean fl, boolean pfl, boolean phfl,
                 boolean cpfl, boolean cphfl, int uc, int mc, int ufl, int mfl, boolean ipt,
                 String am, UUID ou, String on2, List<String> mn, List<String> an, List<Long> chl) {
            OpenContraptionScreenPacket pack() {
                return new OpenContraptionScreenPacket(sl, sn, cc, cl, fl, pfl, phfl, cpfl, cphfl, uc, mc, ufl, mfl, ipt, am, ou, on2, mn, an, chl);
            }
        }
        var d = new D(data.subLevelUUID(), data.shipName(), data.chunkCount(), data.claimed(), data.forceLoaded(),
                data.plotForceLoaded(), data.physicsForceLoaded(), data.canPlotForceLoad(), data.canPhysicsForceLoad(),
                data.usedClaims(), data.maxClaims(), data.usedForceLoads(), data.maxForceLoads(), data.isPartyTeam(),
                data.accessMode(), data.ownerUUID(), data.ownerName(), data.memberNames(), data.allyNames(), data.chunkLongs());

        data = switch (action) {
            case CLAIM              -> new D(d.sl(), d.sn(), d.cc(), true,  d.fl(), d.pfl(), d.phfl(), d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), d.am(), d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case UNCLAIM            -> new D(d.sl(), d.sn(), d.cc(), false, false, false, false, false, false,   d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), d.am(), d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case FORCE_LOAD         -> new D(d.sl(), d.sn(), d.cc(), d.cl(), true,  d.pfl(), d.phfl(), d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl() + d.cc(), d.mfl(), d.ipt(), d.am(), d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case UNFORCE_LOAD       -> new D(d.sl(), d.sn(), d.cc(), d.cl(), false, false,   false,   d.cpfl(), d.cphfl(), d.uc(), d.mc(), Math.max(0, d.ufl() - d.cc()), d.mfl(), d.ipt(), d.am(), d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case PHYSICS_FORCE_LOAD   -> new D(d.sl(), d.sn(), d.cc(), d.cl(), d.fl(), d.pfl(), true,  d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), d.am(), d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case PHYSICS_UNFORCE_LOAD -> new D(d.sl(), d.sn(), d.cc(), d.cl(), d.fl(), d.pfl(), false, d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), d.am(), d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case ACCESS_PRIVATE     -> new D(d.sl(), d.sn(), d.cc(), d.cl(), d.fl(), d.pfl(), d.phfl(), d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), "private", d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case ACCESS_ALLIES      -> new D(d.sl(), d.sn(), d.cc(), d.cl(), d.fl(), d.pfl(), d.phfl(), d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), "allies",  d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
            case ACCESS_PUBLIC      -> new D(d.sl(), d.sn(), d.cc(), d.cl(), d.fl(), d.pfl(), d.phfl(), d.cpfl(), d.cphfl(), d.uc(), d.mc(), d.ufl(), d.mfl(), d.ipt(), "public",  d.ou(), d.on2(), d.mn(), d.an(), d.chl()).pack();
        };

        pendingData = data;
        refreshWidgets();
    }

    class ContentPanel extends Panel {

        private OpenContraptionScreenPacket data;

        ContentPanel(ContraptionClaimScreen screen, OpenContraptionScreenPacket data) {
            super(screen);
            this.data = data;
        }

        @Override
        public void addWidgets() {
            this.data = ContraptionClaimScreen.this.data;

            add(new VerticalSpaceWidget(this, 2));

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.status"),
                    data.claimed()
                            ? Component.translatable("create_aeronautics_ftb_chunks.screen.claimed").withStyle(ChatFormatting.GREEN)
                            : Component.translatable("create_aeronautics_ftb_chunks.screen.unclaimed").withStyle(ChatFormatting.RED)));

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.force_loaded"),
                    data.forceLoaded()
                            ? Component.literal("Yes").withStyle(ChatFormatting.GREEN)
                            : Component.literal("No").withStyle(ChatFormatting.GRAY)));

            if (data.canPlotForceLoad()) {
                add(infoRow(
                        Component.translatable("create_aeronautics_ftb_chunks.screen.plot_force_load"),
                        data.plotForceLoaded()
                                ? Component.literal("Active").withStyle(ChatFormatting.GREEN)
                                : Component.literal("Off").withStyle(ChatFormatting.GRAY)));
            }

            if (data.canPhysicsForceLoad()) {
                add(infoRow(
                        Component.translatable("create_aeronautics_ftb_chunks.screen.physics_force_load"),
                        data.physicsForceLoaded()
                                ? Component.literal("Active").withStyle(ChatFormatting.GREEN)
                                : Component.literal("Off").withStyle(ChatFormatting.GRAY)));
            }

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.chunks"),
                    Component.literal(String.valueOf(data.chunkCount())).withStyle(ChatFormatting.AQUA)));

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.claims_used"),
                    usageComponent(data.usedClaims(), data.maxClaims())));

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.force_used"),
                    usageComponent(data.usedForceLoads(), data.maxForceLoads())));

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.access"),
                    accessModeComponent(data.accessMode())));

            add(infoRow(
                    Component.translatable("create_aeronautics_ftb_chunks.screen.owner"),
                    Component.literal(data.ownerName()).withStyle(ChatFormatting.YELLOW)));

            if (!data.memberNames().isEmpty()) {
                add(infoRow(
                        Component.translatable("create_aeronautics_ftb_chunks.screen.members"),
                        Component.literal(String.join(", ", data.memberNames())).withStyle(ChatFormatting.WHITE)));
            }

            if (!data.allyNames().isEmpty()) {
                add(infoRow(
                        Component.translatable("create_aeronautics_ftb_chunks.screen.allies"),
                        Component.literal(String.join(", ", data.allyNames())).withStyle(ChatFormatting.AQUA)));
            }

            add(new VerticalSpaceWidget(this, 4));

            add(SimpleTextButton.create(this,
                    data.claimed()
                            ? Component.translatable("create_aeronautics_ftb_chunks.screen.unclaim")
                            : Component.translatable("create_aeronautics_ftb_chunks.screen.claim"),
                    data.claimed() ? Icons.REMOVE : Icons.ADD,
                    mb -> sendAction(data.claimed() ? Action.UNCLAIM : Action.CLAIM)));

            add(new VerticalSpaceWidget(this, 2));

            if (data.claimed()) {
                add(SimpleTextButton.create(this,
                        data.forceLoaded()
                                ? Component.translatable("create_aeronautics_ftb_chunks.screen.unforce")
                                : Component.translatable("create_aeronautics_ftb_chunks.screen.force"),
                        Icons.REFRESH,
                        mb -> sendAction(data.forceLoaded() ? Action.UNFORCE_LOAD : Action.FORCE_LOAD)));

                add(new VerticalSpaceWidget(this, 2));

                if (data.canPlotForceLoad()) {
                    add(SimpleTextButton.create(this,
                            data.plotForceLoaded()
                                    ? Component.translatable("create_aeronautics_ftb_chunks.screen.plot_unforce")
                                    : Component.translatable("create_aeronautics_ftb_chunks.screen.plot_force"),
                            Icons.LOCK,
                            mb -> sendAction(data.plotForceLoaded() ? Action.UNFORCE_LOAD : Action.FORCE_LOAD)));
                    add(new VerticalSpaceWidget(this, 2));
                }

                if (data.canPhysicsForceLoad()) {
                    add(SimpleTextButton.create(this,
                            data.physicsForceLoaded()
                                    ? Component.translatable("create_aeronautics_ftb_chunks.screen.physics_unforce")
                                    : Component.translatable("create_aeronautics_ftb_chunks.screen.physics_force"),
                            Icons.ACCEPT,
                            mb -> sendAction(data.physicsForceLoaded() ? Action.PHYSICS_UNFORCE_LOAD : Action.PHYSICS_FORCE_LOAD)));
                    add(new VerticalSpaceWidget(this, 2));
                }
            }

            Action nextAccess = nextAccessAction(data.accessMode());
            add(SimpleTextButton.create(this,
                    accessCycleLabel(data.accessMode()),
                    Icons.SETTINGS,
                    mb -> sendAction(nextAccess)));

            add(new VerticalSpaceWidget(this, 2));

            if (data.isPartyTeam()) {
                add(SimpleTextButton.create(this,
                        Component.translatable("create_aeronautics_ftb_chunks.screen.manage_allies"),
                        Icons.ADD,
                        mb -> { playClickSound(); openAllyModal(); }));
            }
        }

        private void openAllyModal() {
            AllyModal modal = new AllyModal(this);
            getGui().pushModalPanel(modal);
        }

        @Override
        public void alignWidgets() {
            for (Widget w : widgets) {
                w.setX(4);
                w.setWidth(width - 8);
            }
            WidgetLayout.VERTICAL.align(this);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            theme.drawPanelBackground(graphics, x, y, w, h);
        }

        private Widget infoRow(Component label, Component value) {
            return new Widget(this) {
                { setHeight(13); }

                @Override
                public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
                    theme.drawString(graphics, label.copy().withStyle(ChatFormatting.GRAY), x + 2, y + 2);
                    String val = value.getString();
                    theme.drawString(graphics, value, x + w - theme.getStringWidth(val) - 2, y + 2);
                    Color4I.rgba(0x55, 0x55, 0x77, 0x60).draw(graphics, x, y + h - 1, w, 1);
                }
            };
        }
    }

    private Component usageComponent(int used, int max) {
        ChatFormatting color = used >= max ? ChatFormatting.RED : used >= max * 0.9 ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
        return Component.literal(used + "/" + max).withStyle(color);
    }

    private Component accessModeComponent(String mode) {
        return switch (mode) {
            case "public"  -> Component.literal("Public").withStyle(ChatFormatting.GREEN);
            case "allies"  -> Component.literal("Allies").withStyle(ChatFormatting.AQUA);
            default        -> Component.literal("Private").withStyle(ChatFormatting.RED);
        };
    }

    private Component accessCycleLabel(String mode) {
        String next = switch (mode) {
            case "private" -> "Allies";
            case "allies"  -> "Public";
            default        -> "Private";
        };
        return Component.literal("Access: ").withStyle(ChatFormatting.GRAY)
                .append(accessModeComponent(mode))
                .append(Component.literal(" → " + next).withStyle(ChatFormatting.DARK_GRAY));
    }

    private Action nextAccessAction(String mode) {
        return switch (mode) {
            case "private" -> Action.ACCESS_ALLIES;
            case "allies"  -> Action.ACCESS_PUBLIC;
            default        -> Action.ACCESS_PRIVATE;
        };
    }

    private class AllyModal extends ModalPanel {

        private TextBox textBox;
        private final List<String> localAllies;

        AllyModal(Panel parent) {
            super(parent);
            this.localAllies = new ArrayList<>(data.allyNames());
            setSize(200, 120);
        }

        @Override
        public void addWidgets() {
            textBox = new TextBox(this) {
                { setSize(width - 8, 14); ghostText = "Player name..."; }
            };
            add(textBox);

            add(SimpleTextButton.create(this,
                    Component.translatable("create_aeronautics_ftb_chunks.screen.add_ally"),
                    Icons.ADD,
                    mb -> {
                        String name = textBox.getText().trim();
                        if (!name.isEmpty()) {
                            playClickSound();
                            PacketDistributor.sendToServer(new ContraptionAllyPacket(name, false));
                            if (!localAllies.contains(name)) localAllies.add(name);
                            data = new OpenContraptionScreenPacket(data.subLevelUUID(), data.shipName(), data.chunkCount(), data.claimed(), data.forceLoaded(), data.plotForceLoaded(), data.physicsForceLoaded(), data.canPlotForceLoad(), data.canPhysicsForceLoad(), data.usedClaims(), data.maxClaims(), data.usedForceLoads(), data.maxForceLoads(), data.isPartyTeam(), data.accessMode(), data.ownerUUID(), data.ownerName(), data.memberNames(), localAllies, data.chunkLongs());
                            pendingData = data;
                            textBox.setText("");
                            getGui().refreshWidgets();
                        }
                    }));

            for (String ally : localAllies) {
                add(SimpleTextButton.create(this,
                        Component.literal("✗ ").withStyle(ChatFormatting.RED)
                                .append(Component.literal(ally).withStyle(ChatFormatting.WHITE)),
                        Icons.REMOVE,
                        mb -> {
                            playClickSound();
                            PacketDistributor.sendToServer(new ContraptionAllyPacket(ally, true));
                            localAllies.remove(ally);
                            data = new OpenContraptionScreenPacket(data.subLevelUUID(), data.shipName(), data.chunkCount(), data.claimed(), data.forceLoaded(), data.plotForceLoaded(), data.physicsForceLoaded(), data.canPlotForceLoad(), data.canPhysicsForceLoad(), data.usedClaims(), data.maxClaims(), data.usedForceLoads(), data.maxForceLoads(), data.isPartyTeam(), data.accessMode(), data.ownerUUID(), data.ownerName(), data.memberNames(), localAllies, data.chunkLongs());
                            pendingData = data;
                            getGui().refreshWidgets();
                        }));
            }

            add(SimpleTextButton.create(this,
                    Component.translatable("gui.close"),
                    Icons.CANCEL,
                    mb -> { playClickSound(); getGui().closeModalPanel(this); }));
        }

        @Override
        public void alignWidgets() {
            int x = 4, y = 6;
            for (Widget w : widgets) {
                w.setPos(x, y);
                w.setWidth(width - 8);
                y += w.getHeight() + 2;
            }
            setHeight(y + 4);
            setPos((getGui().width - width) / 2, (getGui().height - height) / 2);
        }

        @Override
        public boolean keyPressed(Key key) {
            if (key.is(GLFW.GLFW_KEY_ESCAPE)) {
                getGui().closeModalPanel(this);
                return true;
            }
            return super.keyPressed(key);
        }

        @Override
        public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            theme.drawWidget(graphics, x, y, w, h, WidgetType.NORMAL);
            theme.drawString(graphics,
                    Component.translatable("create_aeronautics_ftb_chunks.screen.manage_allies").withStyle(ChatFormatting.BOLD),
                    x + 4, y - 10);
        }
    }
}
