package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class LinkerItem extends Item {

    private BlockPos sourcePos = null;

    public LinkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        if (context.getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();

        // Клик по панели
        if (context.getLevel().getBlockEntity(clickedPos)
                instanceof RedstoneInfoPanelBlockEntity panel) {

            if (context.getPlayer() != null
                    && context.getPlayer().isShiftKeyDown()) {

                BlockPos savedSource = panel.getSourcePos();

                if (savedSource == null) {

                    context.getPlayer().sendSystemMessage(
                            Component.literal(
                                    "Linker: у этой панели источник не привязан."
                            )
                    );

                } else {

                    context.getPlayer().sendSystemMessage(
                            Component.literal(
                                    "Linker: источник панели — "
                                            + savedSource.getX() + ", "
                                            + savedSource.getY() + ", "
                                            + savedSource.getZ()
                            )
                    );
                }

                return InteractionResult.SUCCESS;
            }

            if (sourcePos == null) {

                if (context.getPlayer() != null) {

                    context.getPlayer().sendSystemMessage(
                            Component.literal(
                                    "Linker: сначала кликни по блоку-источнику Redstone."
                            )
                    );
                }

                return InteractionResult.SUCCESS;
            }

            panel.setSourcePos(sourcePos);

            if (context.getPlayer() != null) {

                context.getPlayer().sendSystemMessage(
                        Component.literal(
                                "Linker: панель привязана к источнику "
                                        + sourcePos.getX() + ", "
                                        + sourcePos.getY() + ", "
                                        + sourcePos.getZ()
                        )
                );
            }

            sourcePos = null;

            return InteractionResult.SUCCESS;
        }

        // Клик по беспроводному блоку
        if (context.getLevel().getBlockEntity(clickedPos)
                instanceof WirelessRedstoneBlockEntity wireless) {

            if (context.getPlayer() != null
                    && context.getPlayer().isShiftKeyDown()) {

                BlockPos savedSource = wireless.getSourcePos();

                if (savedSource == null) {

                    context.getPlayer().sendSystemMessage(
                            Component.literal(
                                    "Linker: у этого блока источник не привязан."
                            )
                    );

                } else {

                    context.getPlayer().sendSystemMessage(
                            Component.literal(
                                    "Linker: источник блока — "
                                            + savedSource.getX() + ", "
                                            + savedSource.getY() + ", "
                                            + savedSource.getZ()
                            )
                    );
                }

                return InteractionResult.SUCCESS;
            }

            if (sourcePos == null) {

                if (context.getPlayer() != null) {

                    context.getPlayer().sendSystemMessage(
                            Component.literal(
                                    "Linker: сначала кликни по блоку-источнику Redstone."
                            )
                    );
                }

                return InteractionResult.SUCCESS;
            }

            wireless.setSourcePos(sourcePos);

            if (context.getPlayer() != null) {

                context.getPlayer().sendSystemMessage(
                        Component.literal(
                                "Linker: беспроводной блок привязан к источнику "
                                        + sourcePos.getX() + ", "
                                        + sourcePos.getY() + ", "
                                        + sourcePos.getZ()
                        )
                );
            }

            sourcePos = null;

            return InteractionResult.SUCCESS;
        }

        // Первый клик — выбираем источник Redstone.
        sourcePos = clickedPos;

        if (context.getPlayer() != null) {

            context.getPlayer().sendSystemMessage(
                    Component.literal(
                            "Linker: источник выбран — "
                                    + sourcePos.getX() + ", "
                                    + sourcePos.getY() + ", "
                                    + sourcePos.getZ()
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}