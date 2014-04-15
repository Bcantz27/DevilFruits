package com.piraterevenge.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.piraterevenge.DevilFruits;
import com.piraterevenge.Fruit.Fruit;
import com.piraterevenge.Fruit.FruitManager;

public class PlayerListener implements Listener
{
	private HashMap<Player, List<Block>> blocks = new HashMap<Player, List<Block>>();
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerUse(PlayerInteractEvent event){
	    Player p = event.getPlayer();
	    Fruit f = null;
	    Fireball fireball = null;
	    
	    if(FruitManager.getInstance().checkIfPlayerIsDFUser(p))
		{
	    	f = FruitManager.getInstance().getFruitByPlayer(p);
	    	
			switch(f.getId())
			{
				case 4:		//Flame Fruit
					if(event.getItem() == null)
					{
						if(FruitManager.getInstance().getCooldowns().get(p.getName()) != null)
						{
							if(p.isSneaking())
							{
								if(FruitManager.getInstance().getCooldowns().get(p.getName()) == 0L)
								{
									FruitManager.getInstance().setPlayerCooldown(p);
									Location loc = p.getEyeLocation().toVector().add(p.getLocation().getDirection().multiply(2)).toLocation(p.getWorld(), p.getLocation().getYaw(), p.getLocation().getPitch());
									fireball = p.getWorld().spawn(loc, Fireball.class);
									fireball.setVelocity(p.getLocation().getDirection().multiply(3));
								}
								else
								{
									p.sendMessage(ChatColor.RED + "Ability on cooldown for " + FruitManager.getInstance().getCooldowns().get(p.getName()) + " seconds.");
								}
							}
						}
						else
						{
							FruitManager.getInstance().setPlayerCooldown(p,0);
						}
					}
					break;
				case 16:		//Blink Fruit
					if(event.getItem() == null)
					{
						if(FruitManager.getInstance().getCooldowns().get(p.getName()) != null)
						{
							if(p.isSneaking())
							{
								if(FruitManager.getInstance().getCooldowns().get(p.getName()) == 0L)
								{
									FruitManager.getInstance().setPlayerCooldown(p);
									Block b = p.getTargetBlock(null, 40);
									p.teleport(b.getLocation().add(0, 1, 0));
								}
								else
								{
									p.sendMessage(ChatColor.RED + "Ability on cooldown for " + FruitManager.getInstance().getCooldowns().get(p.getName()) + " seconds.");
								}
							}
						}
						else
						{
							FruitManager.getInstance().setPlayerCooldown(p,0);
						}
					}
					break;
				default:
			}
		}
	    
	    if(FruitManager.getInstance().isItemAFruit(p.getItemInHand()) && event.getAction() == Action.RIGHT_CLICK_AIR)	//Eat Fruit
	    {
	    	f = FruitManager.getInstance().getFruitByItem(p.getItemInHand());
	    	FruitManager.getInstance().playerEatFruit(p,f);
	    }
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent e)
	{
		final Entity ent = e.getEntity();
		Entity damager = e.getDamager();
		Fruit f = null;
		if(ent instanceof Player && damager instanceof Player)
		{
			if(FruitManager.getInstance().checkIfPlayerIsDFUser((Player)ent))
			{
		    	f = FruitManager.getInstance().getFruitByPlayer((Player)ent);
		    	
				switch(f.getId())
				{
					case 5:		//GomuGomuNo Fruit
						if(((Player) damager).getItemInHand() == null)
						{
							e.setCancelled(true);
						}
						break;
					case 7:		//Poison Fruit
						if(!((Player)damager).hasPotionEffect(PotionEffectType.POISON))
						{
							((Player)damager).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10*20, 2));
						}
						break;
					case 10:	//Blind Fruit
						if(!((Player)damager).hasPotionEffect(PotionEffectType.BLINDNESS))
						{
							((Player)damager).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 3));
						}
						break;
					case 14:	//Jugg Fruit
						new BukkitRunnable() {
							@Override
							public void run() {
			                    Vector knockback = ((Player)ent).getVelocity().multiply(0.5f);
			                    ((Player)ent).setVelocity(knockback);
							}
						}.runTaskLater(DevilFruits.getInstance(), 1L);
						break;
					case 15:	//Madman Fruit
						if(!((Player)damager).hasPotionEffect(PotionEffectType.CONFUSION))
						{
							((Player)damager).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10*20, 3));
						}
						if(!((Player)ent).hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
						{
							((Player)ent).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5*20, 2));
						}
						break;
					default: 
				}
			}
			
			if(FruitManager.getInstance().checkIfPlayerIsDFUser((Player)damager))
			{
		    	f = FruitManager.getInstance().getFruitByPlayer((Player)damager);
		    	
				switch(f.getId())
				{
					case 9:		//Zombie Fruit
						if(!((Player)ent).hasPotionEffect(PotionEffectType.HUNGER))
						{
							((Player)ent).addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10*20, 2));
						}
						break;
					case 18:	//Weakness Fruit
						if(!((Player)damager).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
						{
							((Player)damager).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 2));
						}
						if(!((Player)ent).hasPotionEffect(PotionEffectType.WEAKNESS))
						{
							((Player)ent).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, 2));
						}
						break;
					case 19:	//Life Fruit
						if(!((Player)damager).hasPotionEffect(PotionEffectType.REGENERATION))
						{
							((Player)damager).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 2));
						}
						break;
					default:
				}
			}
		}
		else if(ent instanceof Player)
		{
			if(FruitManager.getInstance().checkIfPlayerIsDFUser((Player)ent))
			{
		    	f = FruitManager.getInstance().getFruitByPlayer((Player)ent);
		    	
				switch(f.getId())
				{
					case 5:		//GomuGomuNo Fruit
						if(damager instanceof Snowball || damager instanceof LightningStrike || damager instanceof Projectile)
						{
							e.setCancelled(true);
						}
						break;
					case 14:	//Jugg Fruit
						new BukkitRunnable() {
							@Override
							public void run() {
			                    Vector knockback = ((Player)ent).getVelocity().multiply(0.5f);
			                    ((Player)ent).setVelocity(knockback);
							}
						}.runTaskLater(DevilFruits.getInstance(), 1L);
						break;
					case 21:	//Obsidian Fruit
						if(damager instanceof Projectile || damager instanceof Fireball)
						{
							e.setCancelled(true);
						}
						new BukkitRunnable() {
							@Override
							public void run() {
			                    Vector knockback = ((Player)ent).getVelocity().multiply(0.8f);
			                    ((Player)ent).setVelocity(knockback);
							}
						}.runTaskLater(DevilFruits.getInstance(), 1L);
						break;
					default: 
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Fruit f = null;
		
		if(FruitManager.getInstance().getWaterWalkers().contains(p.getName()))
		{
			Location loc = e.getPlayer().getLocation().add(0, -1, 0);
			Block block;
			List<Block> blist = new ArrayList<Block>();
			blist.add(loc.getBlock());
			
			block = e.getPlayer().getLocation().add(0, -1, 1).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(0, -1, -1).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(1, -1, 0).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(-1, -1, 0).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(1, -1, 1).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(-1, -1, 1).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(1, -1, -1).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);
			
			block = e.getPlayer().getLocation().add(-1, -1, -1).getBlock();
			if(block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER) || block.getType().equals(Material.GLASS))
				blist.add(block);

			if(blocks.containsKey(e.getPlayer())){
				List<Block> blist2 = blocks.get(e.getPlayer());
				
				for(Block b: blist2)
				{
					b.setType(Material.STATIONARY_WATER); // (or water source?)
				}
			}
			if(loc.getBlock().getType().equals(Material.WATER) || loc.getBlock().getType().equals(Material.STATIONARY_WATER)){ // (or water source?)
					for(Block b: blist)
					{
						b.setType(Material.GLASS); //Sets the Block to glass
					}
			        blocks.put(e.getPlayer(), blist);
			}
		}

		if(FruitManager.getInstance().checkIfPlayerIsDFUser(p))			//Drown Devil Fruit users
		{
	    	f = FruitManager.getInstance().getFruitByPlayer(p);
	    	
			switch(f.getId())
			{
				case 17:		//Spider Fruit
					climbWall(p);
					break;
				default: 
			}
			
		    Material m = p.getLocation().add(0, 1, 0).getBlock().getType();
		    if (m == Material.STATIONARY_WATER || m == Material.WATER) {
		    	if(!p.hasPotionEffect(PotionEffectType.BLINDNESS))
		    		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5*20, 3));
		    	
		    	if(!p.hasPotionEffect(PotionEffectType.WEAKNESS))
		    		p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, 2));
		    	
		    	if(p.getVelocity().getX() != 0 || p.getVelocity().getZ() != 0 || p.getVelocity().getY() > 0)
		    	{
		    		e.setCancelled(true);
		    	}
		    	p.setVelocity(new Vector(0,-0.6f,0));
		    }
		}
	}
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent e)
	{
		Player p = e.getPlayer();
		Fruit f = null;

		if(FruitManager.getInstance().checkIfPlayerIsDFUser(p))
		{
	    	f = FruitManager.getInstance().getFruitByPlayer(p);
	    	
			switch(f.getId())
			{
			case 11:	//Vanish Fruit
				if(p.isSneaking())
				{
			    	if(!p.hasPotionEffect(PotionEffectType.INVISIBILITY))
			    		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10*20, 2));
				}
				break;
			case 13:	//Turtle Fruit
				if(!p.isSneaking())
				{
			    	if(!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
			    		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10*20, 5));
				}
				break;
			}
		}
	}
	
	private void climbWall(Player p)
	{
		Block b1 = p.getLocation().getBlock();
		
		if (b1.getType() != Material.AIR)
			return;
		
		Block b2 = b1.getRelative(BlockFace.UP);
		Location l = p.getLocation();
		if (p.isSneaking() && (b1.getRelative(BlockFace.NORTH).getType() != Material.AIR
		|| b1.getRelative(BlockFace.EAST).getType() != Material.AIR
		|| b1.getRelative(BlockFace.SOUTH).getType() != Material.AIR
		|| b1.getRelative(BlockFace.WEST).getType() != Material.AIR)) {
		double y = l.getY();
		p.sendBlockChange(b1.getLocation(), Material.VINE, (byte) 0);
		if (y % 1 > .40 && b2.getType() == Material.AIR) {
		p.sendBlockChange(b2.getLocation(), Material.VINE, (byte) 0);
		}
		}
	}
}
