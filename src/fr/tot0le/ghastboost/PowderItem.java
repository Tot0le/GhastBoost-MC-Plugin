package fr.tot0le.ghastboost;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;

public class PowderItem {
	private static final String POWDER_IN_GAME_NAME = "Energizing powder";
	private static final String POWDER_KEY_NAME = "energizing_powder";
	private final NamespacedKey key;
	
	public PowderItem(Plugin plugin) {
		key = new NamespacedKey(plugin, POWDER_KEY_NAME);
	}
	
	public ItemStack createPowder() {
	    ItemStack powder = new ItemStack(Material.SUGAR);
	    ItemMeta powderMeta = powder.getItemMeta();
	    
	    if (powderMeta != null) {
	    	powderMeta.getPersistentDataContainer().set(this.key, PersistentDataType.BYTE, (byte) 1);
	    	
	    	Component powderName = Component.text(POWDER_IN_GAME_NAME);
	        powderMeta.displayName(powderName);
	        
	        powder.setItemMeta(powderMeta);
	    }
	    
	    return powder;
	}
	
	public boolean isEnergizingPowder(ItemStack item) {
		boolean isValid = false;
		if (item != null && item.getType() == Material.SUGAR && item.hasItemMeta()) {
	        ItemMeta itemMeta = item.getItemMeta();
			isValid = itemMeta.getPersistentDataContainer().has(
			    this.key, 
			    org.bukkit.persistence.PersistentDataType.BYTE
			);
		}
		return isValid;
	}
	
	public NamespacedKey getKey() {
		return key;
	}

}
