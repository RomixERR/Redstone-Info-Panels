package com.redstoneinfopanels;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(
                            RedstoneInfoPanels.MODID,
                            "main"
                    ),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );

    private static int packetId = 0;

    public static void register() {

        CHANNEL.registerMessage(
                packetId++,
                PanelSavePacket.class,
                PanelSavePacket::encode,
                PanelSavePacket::new,
                (packet, contextSupplier) -> {

                    var context = contextSupplier.get();

                    context.enqueueWork(() -> {

                        var player = context.getSender();

                        if (player == null) {
                            return;
                        }

                        if (player.level.getBlockEntity(packet.getPanelPos())
                                instanceof RedstoneInfoPanelBlockEntity panel) {
                            panel.setTitle(packet.getTitle());
                            for (int i = 0; i < 16; i++) {

                                panel.setEntryText(
                                        i,
                                        packet.getTexts()[i]
                                );

                                panel.setEntryEnabled(
                                        i,
                                        packet.getEnabled()[i]
                                );

                                panel.getEntry(i).setTextSize(
                                        packet.getTextSizes()[i]
                                );

                                panel.getEntry(i).setTextColor(
                                        packet.getTextColors()[i]
                                );

                                panel.getEntry(i).setFaceColor(
                                        packet.getFaceColors()[i]
                                );
                            }
                            panel.refreshSignal();
                            // Помечаем BlockEntity как изменённую
                            panel.setChanged();

                            // Сообщаем клиенту, что данные панели обновились
                            if (panel.getLevel() != null) {

                                panel.getLevel().sendBlockUpdated(
                                        panel.getBlockPos(),
                                        panel.getBlockState(),
                                        panel.getBlockState(),
                                        3
                                );
                            }
                        }
                    });

                    context.setPacketHandled(true);
                }
        );
    }
}