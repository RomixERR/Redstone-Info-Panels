package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WirelessRedstoneBlockEntity extends BlockEntity {

    // Координаты подключённого источника Redstone.
    // null = источник ещё не подключён.
    private BlockPos sourcePos = null;

    // Текущий передаваемый сигнал.
    private int signal = 0;

    // Служебный счётчик тиков.
    private int tickCounter = 0;

    public WirelessRedstoneBlockEntity(
            BlockPos pos,
            BlockState state) {

        super(
                RedstoneInfoPanels.WIRELESS_REDSTONE_BLOCK_ENTITY.get(),
                pos,
                state
        );
    }

    public BlockPos getSourcePos() {
        return sourcePos;
    }

    public void setSourcePos(BlockPos sourcePos) {

        this.sourcePos = sourcePos;

        setChanged();

        if (level != null && !level.isClientSide) {

            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public int getSignal() {
        return signal;
    }

    /**
     * Опрос подключённого источника Redstone.
     */
    public static void tick(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            BlockEntity blockEntity) {

        if (level.isClientSide) {
            return;
        }

        if (!(blockEntity instanceof WirelessRedstoneBlockEntity wireless)) {
            return;
        }

        wireless.tickCounter++;

        // Проверяем Redstone 4 раза в секунду.
        if (wireless.tickCounter < 5) {
            return;
        }

        wireless.tickCounter = 0;

        // Нет подключённого источника.
        if (wireless.sourcePos == null) {
            return;
        }

        // Источник ещё не загружен.
        if (!level.isLoaded(wireless.sourcePos)) {
            return;
        }

        int newSignal = 0;

        // Проверяем все 6 сторон источника.
        for (net.minecraft.core.Direction direction :
                net.minecraft.core.Direction.values()) {

            newSignal = Math.max(
                    newSignal,
                    level.getSignal(
                            wireless.sourcePos,
                            direction
                    )
            );
        }

        if (newSignal != wireless.signal) {

            wireless.signal = newSignal;

            wireless.setChanged();

            // Сообщаем соседним Redstone-блокам,
            // что сила сигнала изменилась.
            level.updateNeighborsAt(
                    pos,
                    state.getBlock()
            );

            // Обновляем BlockEntity на клиенте.
            level.sendBlockUpdated(
                    pos,
                    state,
                    state,
                    3
            );
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);

        tag.putInt("Signal", signal);

        if (sourcePos != null) {

            CompoundTag sourceTag = new CompoundTag();

            sourceTag.putInt("X", sourcePos.getX());
            sourceTag.putInt("Y", sourcePos.getY());
            sourceTag.putInt("Z", sourcePos.getZ());

            tag.put("SourcePos", sourceTag);
        }
    }

    @Override
    public void load(CompoundTag tag) {

        super.load(tag);

        signal = tag.getInt("Signal");

        if (tag.contains("SourcePos")) {

            CompoundTag sourceTag =
                    tag.getCompound("SourcePos");

            sourcePos = new BlockPos(
                    sourceTag.getInt("X"),
                    sourceTag.getInt("Y"),
                    sourceTag.getInt("Z")
            );

        } else {

            sourcePos = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {

        CompoundTag tag = new CompoundTag();

        saveAdditional(tag);

        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this);
    }
}