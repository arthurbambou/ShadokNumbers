package fr.catcore.shadoknumbers.mixin.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import fr.catcore.shadoknumbers.ShadokNumbers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Mixin(value = ItemStack.class, priority = 9999)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean hasCustomName();

    @Shadow public abstract Text getName();

    @Shadow public abstract Rarity getRarity();

    @Shadow public abstract Item getItem();

    @Shadow protected abstract int getHideFlags();

    @Shadow
    protected static boolean isSectionHidden(int flags, ItemStack.TooltipSection tooltipSection) {
        return false;
    }

    @Shadow public abstract boolean hasTag();

    @Shadow
    public static void appendEnchantments(List<Text> tooltip, ListTag enchantments) {
    }

    @Shadow public abstract ListTag getEnchantments();

    @Shadow private CompoundTag tag;

    @Shadow @Final private static Style LORE_STYLE;

    @Shadow public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot);

    @Shadow
    protected static Collection<Text> parseBlockTag(String tag) {
        return null;
    }

    @Shadow public abstract boolean isDamaged();

    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract int getDamage();

    /**
     * @author CatCore
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        List<Text> list = Lists.newArrayList();
        MutableText mutableText = (new LiteralText("")).append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            mutableText.formatted(Formatting.ITALIC);
        }

        list.add(mutableText);
        if (!context.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add((new LiteralText("#" + FilledMapItem.getMapId((ItemStack) (Object)this))).formatted(Formatting.GRAY));
        }

        int i = this.getHideFlags();
        if (isSectionHidden(i, ItemStack.TooltipSection.ADDITIONAL)) {
            this.getItem().appendTooltip((ItemStack) (Object)this, player == null ? null : player.world, list, context);
        }

        int j;
        if (this.hasTag()) {
            if (isSectionHidden(i, ItemStack.TooltipSection.ENCHANTMENTS)) {
                appendEnchantments(list, this.getEnchantments());
            }

            if (this.tag.contains("display", 10)) {
                CompoundTag compoundTag = this.tag.getCompound("display");
                if (isSectionHidden(i, ItemStack.TooltipSection.DYE) && compoundTag.contains("color", 99)) {
                    if (context.isAdvanced()) {
                        list.add((new TranslatableText("item.color", new Object[]{String.format("#%06X", compoundTag.getInt("color"))})).formatted(Formatting.GRAY));
                    } else {
                        list.add((new TranslatableText("item.dyed")).formatted(Formatting.GRAY, Formatting.ITALIC));
                    }
                }

                if (compoundTag.getType("Lore") == 9) {
                    ListTag listTag = compoundTag.getList("Lore", 8);

                    for(j = 0; j < listTag.size(); ++j) {
                        String string = listTag.getString(j);

                        try {
                            MutableText mutableText2 = Text.Serializer.fromJson(string);
                            if (mutableText2 != null) {
                                list.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
                            }
                        } catch (JsonParseException var19) {
                            compoundTag.remove("Lore");
                        }
                    }
                }
            }
        }

        int l;
        if (isSectionHidden(i, ItemStack.TooltipSection.MODIFIERS)) {
            EquipmentSlot[] var20 = EquipmentSlot.values();
            l = var20.length;

            for(j = 0; j < l; ++j) {
                EquipmentSlot equipmentSlot = var20[j];
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
                if (!multimap.isEmpty()) {
                    list.add(LiteralText.EMPTY);
                    list.add((new TranslatableText("item.modifiers." + equipmentSlot.getName())).formatted(Formatting.GRAY));
                    Iterator var11 = multimap.entries().iterator();

                    while(var11.hasNext()) {
                        Map.Entry<EntityAttribute, EntityAttributeModifier> entry = (Map.Entry)var11.next();
                        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
                        double d = entityAttributeModifier.getValue();
                        boolean bl = false;
                        if (player != null) {
                            if (entityAttributeModifier.getId().equals(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"))) {
                                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                                d += (double) EnchantmentHelper.getAttackDamage((ItemStack) (Object)this, EntityGroup.DEFAULT);
                                bl = true;
                            } else if (entityAttributeModifier.getId().equals(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"))) {
                                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                                bl = true;
                            }
                        }

                        double g;
                        if (entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (((EntityAttribute)entry.getKey()).equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
                                g = d * 10.0D;
                            } else {
                                g = d;
                            }
                        } else {
                            g = d * 100.0D;
                        }

                        if (bl) {
                            list.add((new LiteralText(" ")).append(new TranslatableText("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), new Object[]{ShadokNumbers.parseNumber(Double.parseDouble(ItemStack.MODIFIER_FORMAT.format(g))), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())})).formatted(Formatting.DARK_GREEN));
                        } else if (d > 0.0D) {
                            list.add((new TranslatableText("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), new Object[]{ShadokNumbers.parseNumber(Double.parseDouble(ItemStack.MODIFIER_FORMAT.format(g))), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())})).formatted(Formatting.BLUE));
                        } else if (d < 0.0D) {
                            g *= -1.0D;
                            list.add((new TranslatableText("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), new Object[]{ShadokNumbers.parseNumber(Double.parseDouble(ItemStack.MODIFIER_FORMAT.format(g))), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())})).formatted(Formatting.RED));
                        }
                    }
                }
            }
        }

        if (this.hasTag()) {
            if (isSectionHidden(i, ItemStack.TooltipSection.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                list.add((new TranslatableText("item.unbreakable")).formatted(Formatting.BLUE));
            }

            ListTag listTag3;
            if (isSectionHidden(i, ItemStack.TooltipSection.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
                listTag3 = this.tag.getList("CanDestroy", 8);
                if (!listTag3.isEmpty()) {
                    list.add(LiteralText.EMPTY);
                    list.add((new TranslatableText("item.canBreak")).formatted(Formatting.GRAY));

                    for(l = 0; l < listTag3.size(); ++l) {
                        list.addAll(parseBlockTag(listTag3.getString(l)));
                    }
                }
            }

            if (isSectionHidden(i, ItemStack.TooltipSection.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
                listTag3 = this.tag.getList("CanPlaceOn", 8);
                if (!listTag3.isEmpty()) {
                    list.add(LiteralText.EMPTY);
                    list.add((new TranslatableText("item.canPlace")).formatted(Formatting.GRAY));

                    for(l = 0; l < listTag3.size(); ++l) {
                        list.addAll(parseBlockTag(listTag3.getString(l)));
                    }
                }
            }
        }

        if (context.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableText("item.durability", new Object[]{this.getMaxDamage() - this.getDamage(), this.getMaxDamage()}));
            }

            list.add((new LiteralText(Registry.ITEM.getId(this.getItem()).toString())).formatted(Formatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add((new TranslatableText("item.nbt_tags", new Object[]{this.tag.getKeys().size()})).formatted(Formatting.DARK_GRAY));
            }
        }

        return list;
    }
}
