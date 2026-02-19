package com.mememan.blitz.content.entity.base;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class AnimatableBoss extends Monster {
    private static final EntityDataAccessor<Byte> ATTACK_ID = SynchedEntityData.defineId(AnimatableBoss.class, EntityDataSerializers.BYTE);


    protected AnimatableBoss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }
}
