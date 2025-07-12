package ru.nedan.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(Text.class)
public interface TextMixin {

    @Shadow String asString();

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    default <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, this.asString());
    }

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    default <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.asString());
    }
}