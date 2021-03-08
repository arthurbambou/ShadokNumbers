package fr.catcore.shadoknumbers.mixin.client;

import fr.catcore.shadoknumbers.ShadokNumbers;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = SoundSliderWidget.class, priority = 99999)
public abstract class SoundSliderWidgetMixin extends OptionSliderWidget {

    @Shadow @Final private SoundCategory category;

    protected SoundSliderWidgetMixin(GameOptions options, int x, int y, int width, int height, double value) {
        super(options, x, y, width, height, value);
    }

    /**
     * @author CatCore
     */
    @Overwrite
    protected void updateMessage() {
        Text text = (float)this.value == (float)this.getYImage(false) ? ScreenTexts.OFF :
                new LiteralText(ShadokNumbers.parseNumber((int)(this.value * 100.0D)) + "%");
        this.setMessage((new TranslatableText("soundCategory." + this.category.getName())).append(": ").append((Text)text));
    }

}
