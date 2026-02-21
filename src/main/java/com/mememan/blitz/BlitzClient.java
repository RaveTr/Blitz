package com.mememan.blitz;

import com.mememan.blitz.content.item.utility.OreSonarItem;
import com.mememan.blitz.core.client.vfx.level.HighlightBlockEffect;
import com.mememan.blitz.util.RenderUtil;
import com.mememan.nexus.event.result.EventResult;
import com.mememan.nexus.template.event.blueprint.common.TickEventBlueprint;
import com.mememan.nexus.template.event.def.common.TickEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class BlitzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        handleClientEvents();
    }

    private static void handleClientEvents() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((phase, listener) -> {
            
            return true;
        });

        WorldRenderEvents.LAST.register((context) -> {
            Object2ObjectLinkedOpenHashMap<BlockPos, HighlightBlockEffect> blockHighlightData = HighlightBlockEffect.getBlockHighlightData();

            if (blockHighlightData.isEmpty()) return;

            PoseStack curPoseStack = context.matrixStack();
            Camera curCamera = context.camera();

            curPoseStack.pushPose();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest(); // Need this cuz otherwise we can't see through opaque blocks (well, duh, how did I miss this bru)
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator globalTesselator = Tesselator.getInstance();
            BufferBuilder globalBufferBuilder = globalTesselator.getBuilder();

            globalBufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            blockHighlightData // Make sure to drop expired highlight effects instead of needlessly taxing performance trying to render invisible overlays
                    .entrySet()
                    .removeIf(curEntry -> curEntry.getValue().data().getDuration() <= 0);

            for (Map.Entry<BlockPos, HighlightBlockEffect> curEntry : blockHighlightData.entrySet()) { // Traditional for loops FTW
                BlockPos targetPos = curEntry.getKey();
                HighlightBlockEffect.HighlightBlockData curEffectData = curEntry.getValue().data();

                Vec3 cameraPos = curCamera.getPosition();

                double x = targetPos.getX() - cameraPos.x();
                double y = targetPos.getY() - cameraPos.y();
                double z = targetPos.getZ() - cameraPos.z();

                int color = curEffectData.getColor();
                double alpha = curEffectData.getAlpha() * curEffectData.getInitialAlphaMultiplier();

                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;

                float offset = 0.02F; // Render on top of blocks w/out z-fighting (or js straight-up not appearing at all)

                RenderUtil.renderBox(
                        globalBufferBuilder, curPoseStack,
                        x - offset, y - offset, z - offset,
                        x + 1 + offset, y + 1 + offset, z + 1 + offset,
                        r, g, b, (float) alpha
                );

                curEffectData.setDuration(Math.max(0.0D, curEffectData.getDuration() - Math.max(0.1D, curEffectData.getFadeOutRate())));
            }

            globalTesselator.end(); // Pops the buffer builder

            RenderSystem.depthMask(true); // Restore global render state
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();

            curPoseStack.popPose();
        });

        TickEventBlueprint.CLIENT_LEVEL_TICK.onEvent(event -> {
            if (event.getPhase() == TickEvent.Phase.END) {
                OreSonarItem.tickActiveScans();
            }

            return EventResult.success(event);
        });
    }
}
