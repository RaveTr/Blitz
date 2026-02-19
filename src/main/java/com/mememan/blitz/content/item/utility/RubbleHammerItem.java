package com.mememan.blitz.content.item.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RubbleHammerItem extends DiggerItem {

    public RubbleHammerItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(attackDamageModifier, attackSpeedModifier, tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        double miningRadius = 1;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (double x = -miningRadius; x <= miningRadius; x++) {
            for (double y = -miningRadius; y <= miningRadius; y++) {
                for (double z = -miningRadius; z <= miningRadius; z++) {
                    mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);

                    if (canMineBlock(stack, level, state, mutablePos)) {
                        level.destroyBlock(mutablePos, false);
                    }
                }
            }
        }

        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return stack.getDamageValue() < stack.getMaxDamage() - 1 ? super.getDestroySpeed(stack, state) : -1.0F;
    }

    public boolean canMineBlock(ItemStack heldStack, Level curLevel, BlockState targetState, BlockPos targetPos) {
        return !curLevel.isClientSide()
                && !targetState.isAir()
                && targetState.getDestroySpeed(curLevel, targetPos) != 0.0F
                && curLevel.getBlockEntity(targetPos) == null
                && heldStack.getDamageValue() < heldStack.getMaxDamage() - 1;
    }
}
