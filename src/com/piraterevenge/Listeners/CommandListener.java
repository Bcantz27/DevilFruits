package com.piraterevenge.Listeners;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.piraterevenge.DevilFruits;
import com.piraterevenge.Fruit.Fruit;
import com.piraterevenge.Fruit.FruitManager;

public class CommandListener implements CommandExecutor{

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args)
	{
		boolean invalid = false;
		Player p = null;
		
		if(sender instanceof Player)
		{
			p = (Player) sender;
			if(!p.hasPermission("devilfruits.admin"))
				return false;
		}
		if(args.length == 0){
			sender.sendMessage(ChatColor.GREEN+"/df reload");
			sender.sendMessage(ChatColor.GREEN+"/df list");
			sender.sendMessage(ChatColor.GREEN+"/df track");
			sender.sendMessage(ChatColor.GREEN+"/df remove <player>");
			sender.sendMessage(ChatColor.GREEN+"/df check <player>");
			sender.sendMessage(ChatColor.GREEN+"/df get <FruitId>");
			sender.sendMessage(ChatColor.GREEN+"/df giverandom <player>");
			sender.sendMessage(ChatColor.GREEN+"/df give <player> <FruitId>");
			return false;
		}
		
		if(args[0].equalsIgnoreCase("reload"))
		{
		    DevilFruits.getInstance().reloadConfig();
			try {
				FruitManager.getInstance().saveFruits();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				FruitManager.getInstance().loadFruits();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.sendMessage(ChatColor.GREEN + "Configuration Reloaded!");
		}
		else if(args[0].equalsIgnoreCase("list"))
		{
			FruitManager.getInstance().printDevilFruits(p);
		}
		else if(args[0].equalsIgnoreCase("track"))
		{
			FruitManager.getInstance().printAllPlayerInfo(p);
		}
		else if(args[0].equalsIgnoreCase("remove"))
		{
			if(DevilFruits.getInstance().getServer().getPlayer(args[1]) != null)
			{
				FruitManager.getInstance().removeDevilFruit(DevilFruits.getInstance().getServer().getPlayer(args[1]));
				p.sendMessage(ChatColor.GREEN + "Devil Fruit removed!");
			}
			else
			{
				invalid = true;
			}
		}
		else if(args[0].equalsIgnoreCase("check"))
		{
			if(DevilFruits.getInstance().getServer().getPlayer(args[1]) != null)
			{
				FruitManager.getInstance().printPlayerInfo(p, DevilFruits.getInstance().getServer().getPlayer(args[1]));
			}
			else
			{
				invalid = true;
			}
		}
		else if(args[0].equalsIgnoreCase("get"))
		{
			if(args[1] != null)
			{
				if(FruitManager.getInstance().getFruitById(Integer.parseInt(args[1])) != null)
				{
					p.getInventory().addItem(FruitManager.getInstance().getFruitById(Integer.parseInt(args[1])).getItemStack());
				}
				else
				{
					invalid = true;
				}
			}
			else
			{
				invalid = true;
			}
		}
		else if(args[0].equalsIgnoreCase("giverandom"))
		{
			if(args[1] != null)
			{
				Fruit randomFruit = FruitManager.getInstance().getRandomFruit();
				Bukkit.getPlayer(args[1]).getInventory().addItem(randomFruit.getItemStack());
				Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "You have received a " + randomFruit.getName());
			}
			else
			{
				invalid = true;
			}
		}
		else if(args[0].equalsIgnoreCase("give"))
		{
			if(args[1] != null)
			{
				if(FruitManager.getInstance().getFruitById(Integer.parseInt(args[2])) != null)
				{
					Bukkit.getPlayer(args[1]).getInventory().addItem(FruitManager.getInstance().getFruitById(Integer.parseInt(args[2])).getItemStack());
					Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "You have received a " + FruitManager.getInstance().getFruitById(Integer.parseInt(args[2])).getName());
				}
				else
				{
					invalid = true;
				}
			}
			else
			{
				invalid = true;
			}
		}
		else
		{
			invalid = true;
		}
		
		if(invalid)
		{
			sender.sendMessage(ChatColor.RED+"Invalid arguments.");
		}
		return false;
	}
}
