package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getFrom().getY() >= event.getTo().getY()) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isOnGround()) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment != null && enchantment.canEnchantItem(item)) {
                enchantments.add(enchantment);
            }
        }
        if (enchantments.isEmpty()) {
            return;
        }

        Enchantment selected = enchantments.get(
                ThreadLocalRandom.current().nextInt(enchantments.size()));
        int level = meta.getEnchantLevel(selected);
        int newLevel = level > 0 ? level + 1 : 1;
        meta.addEnchant(selected, newLevel, true);
        item.setItemMeta(meta);

        String enchantName = selected.key().value().replace('_', ' ');
        player.sendMessage(Component.text("+1 " + enchantName + " ("
                + newLevel + ")", NamedTextColor.GREEN));
    }
}
