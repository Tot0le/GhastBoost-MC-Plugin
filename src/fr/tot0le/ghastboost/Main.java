package fr.tot0le.ghastboost;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	private final NamespacedKey boostKey = new NamespacedKey(this, "energizing_powder");
	@Override
	public void onEnable() {
	    this.registerSugarRecipe();
	    this.getServer().getPluginManager().registerEvents(this, this);
	    
	    this.getLogger().info("The plugin GhastBoost start successfully !");
	}
  
   	public void registerSugarRecipe() {
	    ItemStack energizingPowder = new ItemStack(Material.SUGAR);
	    ItemMeta powderMeta = energizingPowder.getItemMeta();
	    
	    if (powderMeta != null) {
	    	powderMeta.getPersistentDataContainer().set(this.boostKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
	    	
	    	net.kyori.adventure.text.Component powderName = net.kyori.adventure.text.Component.text("Energizing powder");
	        powderMeta.displayName(powderName);
	        
	        energizingPowder.setItemMeta(powderMeta);
	    }
	    
	    
	    NamespacedKey recipeKey = new NamespacedKey(this, "smelt_special_sugar");
	    FurnaceRecipe specialRecipe = new FurnaceRecipe(recipeKey, energizingPowder, Material.SUGAR, 2.0f, 200);
	    
	    Bukkit.addRecipe(specialRecipe);
	}

   	@EventHandler
   	public void onGhastFeed(PlayerInteractEntityEvent interactEvent) {
   	    Player actionPlayer = interactEvent.getPlayer();
   	    Entity clickedEntity = interactEvent.getRightClicked();
   	    ItemStack handItem = actionPlayer.getInventory().getItemInMainHand();
   	    boolean isPlayerSneaking = actionPlayer.isSneaking();
   	    org.bukkit.inventory.EquipmentSlot handSlot = interactEvent.getHand();

   	    if (handSlot == org.bukkit.inventory.EquipmentSlot.HAND) {
   	        if (clickedEntity.getType().name().equals("HAPPY_GHAST") && clickedEntity instanceof org.bukkit.entity.LivingEntity) {
   	            org.bukkit.entity.LivingEntity targetHappyGhast = (org.bukkit.entity.LivingEntity) clickedEntity;
   	            
   	            if (isPlayerSneaking) {
   	                if (handItem.getType() == Material.SUGAR && handItem.hasItemMeta()) {
   	                    ItemMeta itemMeta = handItem.getItemMeta();
   	                    boolean isLegitPowder = itemMeta.getPersistentDataContainer().has(
   	                        this.boostKey, 
   	                        org.bukkit.persistence.PersistentDataType.BYTE
   	                    );
   	                    
   	                    if (isLegitPowder) {
   	                        org.bukkit.attribute.AttributeInstance speedAttribute = targetHappyGhast.getAttribute(org.bukkit.attribute.Attribute.FLYING_SPEED);
   	                        
   	                        if (speedAttribute != null) {
   	                            double normalSpeed = speedAttribute.getBaseValue();
   	                            double boostedSpeed = normalSpeed * 2.0;
   	                            long taskDelayTicks = 400L;
   	                            
   	                            interactEvent.setCancelled(true);
   	                            
   	                            speedAttribute.setBaseValue(boostedSpeed);
   	                            handItem.setAmount(handItem.getAmount() - 1);
   	                            
   	                            actionPlayer.sendMessage("§a[GhastBoost] 2x speed boost activated for your Happy Ghast!");
   	                            
   	                            Runnable resetTask = () -> {
   	                                speedAttribute.setBaseValue(normalSpeed);
   	                                actionPlayer.sendMessage("§e[GhastBoost] Speed boost has expired.");
   	                                return;
   	                            };
   	                            
   	                            this.getServer().getScheduler().runTaskLater(this, resetTask, taskDelayTicks);
   	                        }
   	                    }
   	                }
   	            }
   	        }
   	    }
   	}
}

