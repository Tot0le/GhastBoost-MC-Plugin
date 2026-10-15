package fr.tot0le.ghastboost;

import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class GhastInteractListener implements Listener {

    private final PowderItem powderItem;
    private final BoostService boostService;

    public GhastInteractListener(PowderItem powderItem, BoostService boostService) {
        this.powderItem = powderItem;
        this.boostService = boostService;
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
                interactEvent.setCancelled(true);

                LivingEntity targetHappyGhast = (LivingEntity) clickedEntity;
                UUID ghastId = targetHappyGhast.getUniqueId();
                long currentTime = System.currentTimeMillis();

                if (this.boostService.isAtMaximum(ghastId, currentTime)) {
                    long remainingSeconds = this.boostService.getRemainingSeconds(ghastId, currentTime);
                    actionPlayer.sendMessage("§c[GhastBoost] Your Happy Ghast is already at maximum boost duration (3m)! (" + remainingSeconds + "s remaining)");
                } else {
                    if (actionPlayer.getGameMode() != GameMode.CREATIVE) {
                        handItem.setAmount(handItem.getAmount() - 1);
                    }
                    this.boostService.applyBoost(targetHappyGhast, actionPlayer);
                }
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
}
