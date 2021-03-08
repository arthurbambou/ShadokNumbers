package fr.catcore.shadoknumbers.mixin.client;

import fr.catcore.shadoknumbers.ShadokNumbers;
import net.minecraft.client.options.Option;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Option.class, priority = 99999)
public abstract class OptionMixin {

    @Shadow protected abstract Text getGenericLabel(Text value);

    /**
     * @author CatCore
     */
    @Overwrite
    public Text getGenericLabel(int value) {
        return this.getGenericLabel(new LiteralText(ShadokNumbers.parseNumber(value)));
    }
}
