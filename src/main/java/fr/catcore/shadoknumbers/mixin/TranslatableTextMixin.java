package fr.catcore.shadoknumbers.mixin;

import fr.catcore.shadoknumbers.ShadokNumbers;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TranslatableText.class)
public class TranslatableTextMixin {

    @Mutable
    @Shadow @Final private Object[] args;

    @Inject(method = "<init>(Ljava/lang/String;[Ljava/lang/Object;)V", at = @At(value = "RETURN"))
    private void convertNumbers(String key, Object[] args, CallbackInfo ci) {
        this.args = ShadokNumbers.shadokify(args);
    }
}
