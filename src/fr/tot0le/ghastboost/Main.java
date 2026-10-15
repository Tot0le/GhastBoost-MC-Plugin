package fr.tot0le.ghastboost;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	private PowderItem powderItem;
	
	@Override
	public void onEnable() {
		this.powderItem = new PowderItem(this);
	    this.registerSugarRecipe();
	    this.getServer().getPluginManager().registerEvents(this, this);
	    
	    this.getLogger().info("The plugin GhastBoost start successfully !");
	}
  
	public void registerSugarRecipe() {
	    NamespacedKey recipeKey = new NamespacedKey(this, "smelt_special_sugar");
	    ItemStack energizingPowder = powderItem.createPowder();
	    FurnaceRecipe specialRecipe = new FurnaceRecipe(recipeKey, energizingPowder, Material.SUGAR, 2.0f, 200);
	    
	    Bukkit.addRecipe(specialRecipe);
	}

   	@EventHandler
   	public void onGhastFeed(PlayerInteractEntityEvent interactEvent) {
   	    Player actionPlayer = interactEvent.getPlayer();
   	    Entity clickedEntity = interactEvent.getRightClicked();
   	    ItemStack handItem = actionPlayer.getInventory().getItemInMainHand();
   	    boolean isPlayerSneaking = actionPlayer.isSneaking();
   	    EquipmentSlot handSlot = interactEvent.getHand();

   	    if (handSlot == EquipmentSlot.HAND) {
   	        if (clickedEntity.getType().name().equals("HAPPY_GHAST") && clickedEntity instanceof LivingEntity) {
   	            LivingEntity targetHappyGhast = (LivingEntity) clickedEntity;
   	            
   	            if (isPlayerSneaking) {
   	               
                    if (powderItem.isEnergizingPowder(handItem)) {
                        AttributeInstance speedAttribute = targetHappyGhast.getAttribute(Attribute.FLYING_SPEED);
                        
                        if (speedAttribute != null) {
                            double normalSpeed = speedAttribute.getBaseValue();
                            double boostedSpeed = normalSpeed * 2.0;
                            long taskDelayTicks = 400L; // 20 ticks = 1 secondes, the L is for "converting" the number to long
                            
                            interactEvent.setCancelled(true);
                            
                            speedAttribute.setBaseValue(boostedSpeed);
                            // TODO maybe add if creative don't remove the item
                            handItem.setAmount(handItem.getAmount() - 1);
                            
                            actionPlayer.sendMessage("§a[GhastBoost] 2x speed boost activated for your Happy Ghast!");
                            
                            // TODO: stack boost duration/effects on multiple feeds
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

