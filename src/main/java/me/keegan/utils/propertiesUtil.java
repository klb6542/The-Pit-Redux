package me.keegan.utils;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class propertiesUtil {
    // Changes the way items work by using namespaces or other methods

    public static final NamespacedKey notPlaceable = new NamespacedKey(
            ThePitRedux.getPlugin(),
            "not_placeable");

    public static void setProperty(NamespacedKey key, ItemMeta itemMeta) {
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "");
    }

    public static void removeProperty(NamespacedKey key, ItemMeta itemMeta) {
        itemMeta.getPersistentDataContainer().remove(key);
    }

    public static boolean hasProperty(NamespacedKey key, ItemMeta itemMeta) {
        return itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
}
