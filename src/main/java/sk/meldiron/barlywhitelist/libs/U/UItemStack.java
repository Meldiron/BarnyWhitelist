package sk.meldiron.barlywhitelist.libs.U;

import sk.meldiron.barlywhitelist.libs.Utils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class UItemStack {
    public static ItemStack createItem(String material, String title, List<String> lore) {
        return createItem(UMaterial.valueOf(material), title, lore);
    }

    public static ItemStack createItem(UMaterial material, String title, List<String> lore) {
        ItemStack item = material.getItemStack();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.color(title));
        itemMeta.setLore(lore.stream().map(n -> Utils.color(n)).collect(Collectors.toList()));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        return item;
    }
}
