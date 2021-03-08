package fr.catcore.shadoknumbers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.catcore.shadoknumbers.ShadokNumbers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemRenderer.class, priority = 99999)
public abstract class ItemRendererMixin {

    @Shadow public float zOffset;

    @Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    /**
     * @author CatCore
     */
    @Overwrite
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        if (!stack.isEmpty()) {
            MatrixStack matrixStack = new MatrixStack();
            if (stack.getCount() != 1 || countLabel != null) {
                String string = countLabel == null ? ShadokNumbers.parseNumber(stack.getCount()) : countLabel;
                matrixStack.translate(0.0D, 0.0D, (double)(this.zOffset + 200.0F));
                VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                renderer.draw((String)string, (float)(x + 19 - 2 - renderer.getWidth(string)), (float)(y + 6 + 3), 16777215, true, matrixStack.peek().getModel(), immediate, false, 0, 15728880);
                immediate.draw();
            }

            if (stack.isDamaged()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                float f = (float)stack.getDamage();
                float g = (float)stack.getMaxDamage();
                float h = Math.max(0.0F, (g - f) / g);
                int i = Math.round(13.0F - f * 13.0F / g);
                int j = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
            float k = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
            if (k > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tessellator tessellator2 = Tessellator.getInstance();
                BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
                this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - k)), 16, MathHelper.ceil(16.0F * k), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

        }
    }
}
