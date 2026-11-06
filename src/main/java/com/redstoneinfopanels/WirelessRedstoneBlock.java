package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WirelessRedstoneBlock extends Block implements EntityBlock {

    public WirelessRedstoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state) {

        return new WirelessRedstoneBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {

        return type == RedstoneInfoPanels.WIRELESS_REDSTONE_BLOCK_ENTITY.get()
                ? WirelessRedstoneBlockEntity::tick
                : null;
    }

    /**
     * Этот блок является источником Redstone.
     */
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    /**
     * Выдаём наружу текущую силу сигнала.
     */
    @Override
    public int getSignal(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof WirelessRedstoneBlockEntity wireless) {
            return wireless.getSignal();
        }

        return 0;
    }

    /**
     * Также выдаём полный сигнал для обычной проверки соседями.
     */
    @Override
    public int getDirectSignal(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof WirelessRedstoneBlockEntity wireless) {
            return wireless.getSignal();
        }

        return 0;
    }
}
