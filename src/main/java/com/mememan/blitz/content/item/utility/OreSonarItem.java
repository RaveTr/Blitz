package com.mememan.blitz.content.item.utility;

import com.mememan.blitz.core.client.vfx.screen.ScreenShakeEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class OreSonarItem extends Item {

    public OreSonarItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack heldStack = player.getMainHandItem();

        if (!heldStack.isEmpty() && heldStack.getItem() instanceof OreSonarItem heldOreSonar && !level.isClientSide()) {
            BlockPos curPos = player.blockPosition();
            ScreenShakeEffect shakeEffect = new ScreenShakeEffect(curPos, 2.5D, 0.01F, 245.5F, 11.2F);

            shakeEffect.enqueue(level);



            player.getCooldowns().addCooldown(heldOreSonar, 20);

            return InteractionResultHolder.success(heldStack);
        }

        return InteractionResultHolder.pass(heldStack);
    }
}
