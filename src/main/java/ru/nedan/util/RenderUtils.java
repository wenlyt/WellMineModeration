package ru.nedan.util;

import com.mojang.blaze3d.platform.GlStateManager;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@UtilityClass
public class RenderUtils implements Wrapper {

    public static void drawImage(MatrixStack matrixStack, Identifier res, int x, int y, int width, int height, int tWidth, int tHeight) {
        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(res);
        DrawableHelper.drawTexture(matrixStack, x, y, width, height, 0, 0, tWidth, tHeight, tWidth, tHeight);
        GlStateManager.popMatrix();
    }

}
