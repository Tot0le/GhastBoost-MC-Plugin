package fr.tot0le.ghastboost;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private PowderItem powderItem;
    private BoostScheduler boostScheduler;
    private BoostService boostService;

    @Override
    public void onEnable() {
        this.powderItem = new PowderItem(this);
        this.boostScheduler = new BoostScheduler(this);
        this.boostService = new BoostService(this, this.boostScheduler);

        registerFurnaceRecipe();

        getServer().getPluginManager().registerEvents(
            new GhastInteractListener(this.powderItem, this.boostService),
            this
        );
        return;
    }

    private void registerFurnaceRecipe() {
        ItemStack energizingPowder = this.powderItem.createPowder();
        NamespacedKey recipeKey = new NamespacedKey(this, "smelt_special_sugar");
        FurnaceRecipe specialRecipe = new FurnaceRecipe(recipeKey, energizingPowder, Material.SUGAR, 2.0f, 200);

        Bukkit.addRecipe(specialRecipe);
        return;
    }
}