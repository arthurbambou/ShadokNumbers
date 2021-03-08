package fr.catcore.shadoknumbers.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fr.catcore.shadoknumbers.ShadokNumbers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(value = PotionUtil.class, priority = 99999)
public abstract class PotionUtilMixin {

    @Shadow
    public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
        return null;
    }

    @Shadow @Final private static MutableText field_25817;

    /**
     * @author CatCore
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public static void buildTooltip(ItemStack stack, List<Text> list, float f) {
        List<StatusEffectInstance> list2 = getPotionEffects(stack);
        List<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();
        Iterator var5;
        TranslatableText mutableText;
        StatusEffect statusEffect;
        if (list2.isEmpty()) {
            list.add(field_25817);
        } else {
            for(var5 = list2.iterator(); var5.hasNext(); list.add(mutableText.formatted(statusEffect.getType().getFormatting()))) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var5.next();
                mutableText = new TranslatableText(statusEffectInstance.getTranslationKey());
                statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    Iterator var10 = map.entrySet().iterator();

                    while(var10.hasNext()) {
                        Map.Entry<EntityAttribute, EntityAttributeModifier> entry = (Map.Entry)var10.next();
                        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list3.add(new Pair(entry.getKey(), entityAttributeModifier2));
                    }
                }

                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = new TranslatableText("potion.withAmplifier", new Object[]{mutableText, new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier())});
                }

                if (statusEffectInstance.getDuration() > 20) {
                    mutableText = new TranslatableText("potion.withDuration", new Object[]{mutableText, StatusEffectUtil.durationToString(statusEffectInstance, f)});
                }
            }
        }

        if (!list3.isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add((new TranslatableText("potion.whenDrank")).formatted(Formatting.DARK_PURPLE));
            var5 = list3.iterator();

            while(var5.hasNext()) {
                Pair<EntityAttribute, EntityAttributeModifier> pair = (Pair)var5.next();
                EntityAttributeModifier entityAttributeModifier3 = (EntityAttributeModifier)pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double g;
                if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    g = entityAttributeModifier3.getValue();
                } else {
                    g = entityAttributeModifier3.getValue() * 100.0D;
                }

                if (d > 0.0D) {
                    list.add((new TranslatableText("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), new Object[]{ShadokNumbers.parseNumber(Double.parseDouble(ItemStack.MODIFIER_FORMAT.format(g))), new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())})).formatted(Formatting.BLUE));
                } else if (d < 0.0D) {
                    g *= -1.0D;
                    list.add((new TranslatableText("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), new Object[]{ShadokNumbers.parseNumber(Double.parseDouble(ItemStack.MODIFIER_FORMAT.format(g))), new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())})).formatted(Formatting.RED));
                }
            }
        }

    }
}
