package com.mememan.blitz.core.network.packets.s2c;

import com.mememan.blitz.core.client.vfx.level.HighlightBlockEffect;
import com.mememan.nexus.network.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record HighlightBlockPacket(BlockPos targetPos, int color, double fadeOut, double fadeOutRate, double initialAlphaMultiplier, double duration) {

    public static HighlightBlockPacket decode(FriendlyByteBuf buf) {
        return new HighlightBlockPacket(buf.readBlockPos(), buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(targetPos);
        buf.writeInt(color);
        buf.writeDouble(fadeOut);
        buf.writeDouble(fadeOutRate);
        buf.writeDouble(initialAlphaMultiplier);
        buf.writeDouble(duration);
    }

    public static PacketContext handle(HighlightBlockPacket decodedPacket) {
        return (packetHandler, curLevel, curConnection, curSide) -> new HighlightBlockEffect(decodedPacket.color, decodedPacket.fadeOut, decodedPacket.fadeOutRate, decodedPacket.initialAlphaMultiplier, decodedPacket.duration).highlightAt(decodedPacket.targetPos, curLevel, packetHandler);
    }
}
