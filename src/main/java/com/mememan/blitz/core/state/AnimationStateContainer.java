package com.mememan.blitz.core.state;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Stateful data-container {@code class} that groups server-side actions with Vanilla
 * {@linkplain AnimationState AnimationStates}, allowing for near-tick-accurate sync between actions performed on the
 * server and animations played/stopped/etc. on the client.
 * <br></br>
 * For any instance of this {@code class} that's stored and ticked properly, the following guarantees are made:
 * <ul>
 *     <li>The wrapped {@link AnimationState} instance is updated on both sides, with the server being the authoritative
 *     source of truth.</li>
 *     <li>The server stores progress separately from the client, with updates being accurate to the nearest whole tick.</li>
 *     <li>If calls to {@link #start()} are made from the client, the rest of the state machine will not be updated
 *     and the animation will only play on the client.</li>
 * </ul>
 *
 * @param <E> Any {@link Entity} type.
 *
 * @apiNote We're going with this approach since it's not practical to implement the whole server-authoritative + bone-accurate
 * OBB + FABRIC/A2B IK model for a small one-shot content mod (I'm reserving these systems for large-scale mods or APIs
 * they're worth adding into). Still nice to have some level of API abstraction, though.
 */
public class AnimationStateContainer<E extends Entity> {
    @NotNull
    protected final E owner;
    protected final AnimationState heldState;
    protected final EntityDataAccessor<Byte> ownerStateData;
    protected double tickProgress = 0.0D;
    protected double tickSpeed = 1.0D;
    protected double currentActionLength = 0.0D;

    public AnimationStateContainer(@NotNull E owner, AnimationState heldState, EntityDataAccessor<Byte> ownerStateData) {
        this.owner = owner;
        this.heldState = heldState;
        this.ownerStateData = ownerStateData;
    }

    public void start(byte actionId) {
        boolean onClient = owner.level().isClientSide;

        // TODO
        if (onClient) {

        } else {
            owner.getEntityData().set(ownerStateData, actionId);
        }
    }

    public void stop() {

    }

    public void tickAuthoritative() {

    }

    public void pause() {

    }

    public void resume() {

    }
}
