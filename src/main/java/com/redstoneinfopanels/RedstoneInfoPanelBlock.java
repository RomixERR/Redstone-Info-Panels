package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;


public class RedstoneInfoPanelBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING =
            BlockStateProperties.FACING;

    // 0 = белая
    // 1 = зелёная
    // 2 = жёлтая
    // 3 = красная
    public static final IntegerProperty FACE_COLOR =
            IntegerProperty.create("face_color", 0, 3);

    // Панель толщиной 2/16 блока
    private static final VoxelShape NORTH_SHAPE = Block.box(
            0, 0, 0,
            16, 16, 2
    );

    public RedstoneInfoPanelBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(FACE_COLOR, 0)
        );
    }

    @Override
    public BlockState getStateForPlacement(
            net.minecraft.world.item.context.BlockPlaceContext context) {

        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {

        Direction direction = state.getValue(FACING);

        return switch (direction) {
            case NORTH -> Block.box(
                    0, 0, 14,
                    16, 16, 16
            );

            case SOUTH -> Block.box(
                    0, 0, 0,
                    16, 16, 2
            );

            case WEST -> Block.box(
                    14, 0, 0,
                    16, 16, 16
            );

            case EAST -> Block.box(
                    0, 0, 0,
                    2, 16, 16
            );

            case DOWN -> Block.box(
                    0, 14, 0,
                    16, 16, 16
            );

            case UP -> Block.box(
                    0, 0, 0,
                    16, 2, 16
            );
        };
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(FACING, FACE_COLOR);
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state) {

        return new RedstoneInfoPanelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {

        return type == RedstoneInfoPanels.REDSTONE_INFO_PANEL_BLOCK_ENTITY.get()
                ? RedstoneInfoPanelBlockEntity::tick
                : null;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {

        if (player.getItemInHand(hand).getItem() == RedstoneInfoPanels.LINKER.get()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {

            NetworkHooks.openScreen(
                    serverPlayer,
                    new net.minecraft.world.SimpleMenuProvider(
                            (containerId, playerInventory, serverPlayer1) ->
                                    new RedstoneInfoPanelMenu(
                                            containerId,
                                            playerInventory,
                                            pos
                                    ),
                            Component.literal("Redstone Info Panel")
                    ),
                    buffer -> buffer.writeBlockPos(pos)
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}