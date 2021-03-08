package fr.catcore.shadoknumbers.mixin.client;

import fr.catcore.shadoknumbers.ShadokNumbers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ChatUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChatUtil.class, priority = 99999)
public class ChatUtilMixin {

    /**
     * @author CatCore
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public static String ticksToString(int ticks) {
        int i = ticks / 20;
        int j = i / 60;
        i %= 60;
        return ShadokNumbers.parseNumber(j) + ":" + ShadokNumbers.parseNumber(i);
    }
}
