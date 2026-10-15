package fr.tot0le.ghastboost;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BoostService {

    private final NamespacedKey boostSpeedKey;
    private final BoostScheduler scheduler;
    private final Map<UUID, BoostSession> activeSessions = new HashMap<>();

    private static final double TARGET_SPEED_MULTIPLIER = 2.0;

    public BoostService(JavaPlugin plugin, BoostScheduler scheduler) {
        this.boostSpeedKey = new NamespacedKey(plugin, "boost_speed");
        this.scheduler = scheduler;
    }

    public boolean isAtMaximum(UUID ghastId, long currentTime) {
        boolean atMaximum = false;
        BoostSession session = this.activeSessions.get(ghastId);

        if (session != null && session.isAtMaximum(currentTime)) {
            atMaximum = true;
        }

        return atMaximum;
    }

    public long getRemainingSeconds(UUID ghastId, long currentTime) {
        long remaining = 0L;
        BoostSession session = this.activeSessions.get(ghastId);

        if (session != null) {
            remaining = session.getRemainingSeconds(currentTime);
        }

        return remaining;
    }

    public void applyBoost(LivingEntity ghast, Player player) {
        AttributeInstance speedAttribute = ghast.getAttribute(Attribute.FLYING_SPEED);

        if (speedAttribute != null) {
            UUID ghastId = ghast.getUniqueId();
            long currentTime = System.currentTimeMillis();
            BoostSession session = this.activeSessions.get(ghastId);
            boolean alreadyBoosted = false;

            if (session != null && !session.isExpired(currentTime)) {
                alreadyBoosted = true;
                session.extend(currentTime);
            } else {
                session = new BoostSession(currentTime);
                this.activeSessions.put(ghastId, session);
            }

            applySpeedModifier(speedAttribute);

            long delayMillis = session.getRemainingMillis(currentTime);
            this.scheduler.scheduleExpiration(ghastId, delayMillis, () -> {
                removeSpeedModifier(speedAttribute);
                this.activeSessions.remove(ghastId);
                player.sendMessage("§e[GhastBoost] Speed boost has expired.");
                return;
            });

            if (alreadyBoosted) {
                long remainingSeconds = session.getRemainingSeconds(currentTime);
                player.sendMessage("§a[GhastBoost] Speed boost extended! (" + remainingSeconds + "s remaining)");
            } else {
                player.sendMessage("§a[GhastBoost] 2x speed boost activated for your Happy Ghast!");
            }
        }
        return;
    }

    private void applySpeedModifier(AttributeInstance speedAttribute) {
        speedAttribute.removeModifier(this.boostSpeedKey);

        double modifierAmount = TARGET_SPEED_MULTIPLIER - 1.0;
        AttributeModifier speedModifier = new AttributeModifier(
            this.boostSpeedKey,
            modifierAmount,
            AttributeModifier.Operation.ADD_SCALAR
        );

        speedAttribute.addModifier(speedModifier);
        return;
    }

    private void removeSpeedModifier(AttributeInstance speedAttribute) {
        speedAttribute.removeModifier(this.boostSpeedKey);
        return;
    }
}
