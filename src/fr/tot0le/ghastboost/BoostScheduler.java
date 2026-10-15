package fr.tot0le.ghastboost;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class BoostScheduler {

    private final JavaPlugin plugin;
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    private static final long MILLIS_PER_TICK = 50L;

    public BoostScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleExpiration(UUID ghastId, long delayMillis, Runnable onExpire) {
        cancelTask(ghastId);

        long delayTicks = delayMillis / MILLIS_PER_TICK;
        BukkitTask scheduledTask = this.plugin.getServer().getScheduler().runTaskLater(
            this.plugin,
            () -> {
                this.activeTasks.remove(ghastId);
                onExpire.run();
                return;
            },
            delayTicks
        );

        this.activeTasks.put(ghastId, scheduledTask);
        return;
    }

    public void cancelTask(UUID ghastId) {
        if (this.activeTasks.containsKey(ghastId)) {
            BukkitTask task = this.activeTasks.remove(ghastId);
            task.cancel();
        }
        return;
    }
}
