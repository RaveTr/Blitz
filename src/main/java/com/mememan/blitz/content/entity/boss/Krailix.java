package com.mememan.blitz.content.entity.boss;

import com.mememan.blitz.content.entity.base.AnimatableBoss;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class Krailix extends AnimatableBoss {
    private static final EntityDataAccessor<Integer> STAMINA = SynchedEntityData.defineId(Krailix.class, EntityDataSerializers.INT);

    protected Krailix(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }
}
