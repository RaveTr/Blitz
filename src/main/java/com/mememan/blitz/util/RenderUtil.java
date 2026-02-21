package com.mememan.blitz.util;

import com.mememan.blitz.BlitzClient;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

/**
 * Client-only utility {@code class} containing helper methods for rendering miscellaneous level-based elements/effects
 * that don't require a separate instance-based renderer.
 *
 * @see BlitzClient
 */
public final class RenderUtil {

    private RenderUtil() {
        throw new IllegalAccessError("Attempted to construct instance of utility class! (RenderUtil)");
    }

    public static void renderBlockOutline() {

    }

    public static void renderBox(BufferBuilder buffer, PoseStack poseStack, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) { // pass pos as doubles here for convenience (lazy ahh:tm:)
        Matrix4f matrix = poseStack.last().pose();

        // Bottom face
        buffer.vertex(matrix, (float) minX, (float) minY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) minY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) minY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) minY, (float) maxZ).color(r, g, b, a).endVertex();

        // Top face
        buffer.vertex(matrix, (float) minX, (float) maxY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) maxY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) maxY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) maxY, (float) minZ).color(r, g, b, a).endVertex();

        // North face
        buffer.vertex(matrix, (float) minX, (float) minY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) maxY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) maxY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) minY, (float) minZ).color(r, g, b, a).endVertex();

        // South face
        buffer.vertex(matrix, (float) minX, (float) minY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) minY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) maxY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) maxY, (float) maxZ).color(r, g, b, a).endVertex();

        // West face
        buffer.vertex(matrix, (float) minX, (float) minY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) minY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) maxY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) minX, (float) maxY, (float) minZ).color(r, g, b, a).endVertex();

        // East face
        buffer.vertex(matrix, (float) maxX, (float) minY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) maxY, (float) minZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) maxY, (float) maxZ).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, (float) maxX, (float) minY, (float) maxZ).color(r, g, b, a).endVertex();
    }
}
