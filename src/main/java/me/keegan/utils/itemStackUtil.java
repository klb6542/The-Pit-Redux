package me.keegan.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class itemStackUtil {
    public static boolean isSimilar(ItemStack itemStack, ItemStack itemStack2) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemMeta itemMeta2 = itemStack2.getItemMeta();

        return itemMeta != null && itemMeta2 != null
                && itemMeta.getLore() != null
                && itemMeta2.getLore() != null
                && itemMeta.getLore().equals(itemMeta2.getLore())
                && itemMeta.getDisplayName().equals(itemMeta2.getDisplayName())
                && itemStack.getType() == itemStack2.getType();
    }

    public static boolean isLivingEntityHoldingItemStack(LivingEntity livingEntity, ItemStack itemStack) {
        return livingEntity.getEquipment() != null && (isSimilar(livingEntity.getEquipment().getItemInMainHand(), itemStack)
                || isSimilar(livingEntity.getEquipment().getItemInOffHand(), itemStack));
    }
}
