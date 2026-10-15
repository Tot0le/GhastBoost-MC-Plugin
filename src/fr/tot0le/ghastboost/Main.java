package fr.tot0le.ghastboost;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin implements Listener {
	private PowderItem powderItem;
	private final NamespacedKey boostSpeedKey = new NamespacedKey(this, "boost_speed");
	private final Map<UUID, Long> boostEndTimes = new HashMap<>();
	private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

	private static final long BOOST_DURATION_MILLIS = 20_000L;
	private static final long MILLIS_PER_TICK = 50L;
	
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
	    EquipmentSlot handSlot = interactEvent.getHand();
	    Player actionPlayer = interactEvent.getPlayer();
	    Entity clickedEntity = interactEvent.getRightClicked();
	    ItemStack handItem = actionPlayer.getInventory().getItemInMainHand();
	    boolean isSneaking = actionPlayer.isSneaking();

	    if (handSlot == EquipmentSlot.HAND && isSneaking) {
	        if (isHappyGhast(clickedEntity) && this.powderItem.isEnergizingPowder(handItem)) {
	            interactEvent.setCancelled(true); //canceled other actions (prevent doing multiple things at once)
	            if (actionPlayer.getGameMode() != GameMode.CREATIVE) {
	            	handItem.setAmount(handItem.getAmount() - 1);
	            }
	            
	            LivingEntity targetHappyGhast = (LivingEntity) clickedEntity;
	            boostGhast(targetHappyGhast, actionPlayer);
	        }
	    }
	    return;
	}

	private boolean isHappyGhast(Entity entity) {
	    boolean isMatch = false;

	    if (entity instanceof LivingEntity && entity.getType().name().equals("HAPPY_GHAST")) {
	        isMatch = true;
	    }

	    return isMatch;
	}

	private void boostGhast(LivingEntity ghast, Player player) {
	    AttributeInstance speedAttribute = ghast.getAttribute(Attribute.FLYING_SPEED);

	    if (speedAttribute != null) {
	        UUID ghastId = ghast.getUniqueId();
	        long currentTime = System.currentTimeMillis();
	        boolean alreadyBoosted = isBoostActive(ghastId, currentTime);

	        applySpeedModifier(speedAttribute);
	        long remainingSeconds = scheduleBoostExpiration(ghastId, speedAttribute, player, currentTime);

	        String message = "§a[GhastBoost] 2x speed boost activated for your Happy Ghast!";
	        if (alreadyBoosted) {
	            message = "§a[GhastBoost] Speed boost extended! (" + remainingSeconds + "s remaining)";
	        }

	        player.sendMessage(message);
	    }
	    return;
	}

	private void applySpeedModifier(AttributeInstance speedAttribute) {
	    speedAttribute.removeModifier(this.boostSpeedKey);
	    double targetMultiplier = 2.0; // x2
	    double modifierAmount = targetMultiplier - 1.0;  // speed = base_speed + (1*n)
		AttributeModifier speedModifier = new AttributeModifier(
	        this.boostSpeedKey,
	        modifierAmount,
	        AttributeModifier.Operation.ADD_SCALAR
	    );
	    speedAttribute.addModifier(speedModifier);
	    return;
	}


	private long scheduleBoostExpiration(UUID ghastId, AttributeInstance speedAttribute, Player player, long currentTime) {
	    long newEndTime = calculateNewEndTime(ghastId, currentTime);

	    this.boostEndTimes.put(ghastId, newEndTime);
	    cancelExistingTask(ghastId);

	    long remainingMillis = newEndTime - currentTime;
	    long delayTicks = remainingMillis / MILLIS_PER_TICK;
	    long remainingSeconds = remainingMillis / 1000L;

	    scheduleResetTask(ghastId, speedAttribute, player, delayTicks);
	    return remainingSeconds;
	}

	private long calculateNewEndTime(UUID ghastId, long currentTime) {
	    long targetEndTime = currentTime + BOOST_DURATION_MILLIS;

	    if (isBoostActive(ghastId, currentTime)) {
	        long currentEndTime = this.boostEndTimes.get(ghastId);
	        targetEndTime = currentEndTime + BOOST_DURATION_MILLIS;
	    }

	    return targetEndTime;
	}

	private boolean isBoostActive(UUID ghastId, long currentTime) {
	    boolean active = false;

	    if (this.boostEndTimes.containsKey(ghastId)) {
	        long endTime = this.boostEndTimes.get(ghastId);
	        if (endTime > currentTime) {
	            active = true;
	        }
	    }

	    return active;
	}

	private void cancelExistingTask(UUID ghastId) {
	    if (this.activeTasks.containsKey(ghastId)) {
	        BukkitTask existingTask = this.activeTasks.get(ghastId);
	        existingTask.cancel();
	    }
	    return;
	}

	private void scheduleResetTask(UUID ghastId, AttributeInstance speedAttribute, Player player, long delayTicks) {
	    Runnable resetTask = () -> {
	        speedAttribute.removeModifier(this.boostSpeedKey);
	        this.boostEndTimes.remove(ghastId);
	        this.activeTasks.remove(ghastId);
	        player.sendMessage("§e[GhastBoost] Speed boost has expired.");
	        return;
	    };

	    BukkitTask newTask = this.getServer().getScheduler().runTaskLater(this, resetTask, delayTicks);
	    this.activeTasks.put(ghastId, newTask);
	    return;
	}

	
}

