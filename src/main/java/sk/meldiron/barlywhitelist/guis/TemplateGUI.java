package sk.meldiron.barlywhitelist.guis;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TemplateGUI implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(1,1, ClickableItem.empty(new ItemStack(Material.BOOK, 2)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}
