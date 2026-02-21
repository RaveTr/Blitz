package com.mememan.blitz.core.client.vfx.screen;

import com.mememan.blitz.core.config.JsonConfig;
import com.mememan.blitz.core.network.packets.s2c.ScreenShakePacket;
import com.mememan.blitz.mixins.client.GameRendererMixin;
import com.mememan.nexus.platform.NexusServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Data-holding {@code record} responsible for handling screen shake effects on the current client asynchronously (not
 * literally multithreading, but rather, just FIFO'ing whatever instances get enqueued for processing on the client).
 *
 * @param data The data object storing all relevant information for the screen shake effect.
 *
 * @see GameRendererMixin
 */
public record ScreenShakeEffect(ShakeData data) {
    private static final ConcurrentLinkedQueue<ScreenShakeEffect> SHAKES = new ConcurrentLinkedQueue<>(); // Just convenient for internal method use; this'll only ever be mutated on the client anyway

    public ScreenShakeEffect(BlockPos originPos, double range, float magnitude, float duration, float fadeOut) {
        this(new ShakeData(originPos, range, magnitude, duration, fadeOut));
    }

    public void enqueue(Level curLevel) {
        if (JsonConfig.ENABLE_SCREEN_SHAKE.get().getAsBoolean()) {
            if (curLevel == null || curLevel.isClientSide) SHAKES.add(this);
            if (curLevel != null && !curLevel.isClientSide) NexusServices.NETWORK_MANAGER.sendToAllClients(new ScreenShakePacket(data.getOriginPos(), data.getRange(), data.getMagnitude(), data.getDuration(), data.getFadeOut()));
        }
    }

    public static ConcurrentLinkedQueue<ScreenShakeEffect> getEnqueuedShakes() {
        return SHAKES;
    }

    public static class ShakeData {
        private final BlockPos originPos;
        private final double range;
        private final float magnitude;
        private float duration;
        private final float fadeOut;

        public ShakeData(BlockPos originPos, double range, float magnitude, float duration, float fadeOut) {
            this.originPos = originPos;
            this.range = range;
            this.magnitude = magnitude;
            this.duration = duration;
            this.fadeOut = fadeOut;
        }

        public BlockPos getOriginPos() {
            return originPos;
        }

        public double getRange() {
            return range;
        }

        public float getMagnitude() {
            return magnitude;
        }

        public float getDuration() {
            return duration;
        }

        public float getFadeOut() {
            return fadeOut;
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }
    }
}