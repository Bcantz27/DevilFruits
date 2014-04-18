package com.piraterevenge.Fruit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.piraterevenge.DevilFruits;

public class FruitManager {
	
	private ConcurrentHashMap<String,Integer> fruits = new ConcurrentHashMap<String,Integer>(); // PlayerName, Fruit ID
	private static FruitManager instance;
	private long currentTime = 0;
	
	private ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap<String,Long>(); // PlayerName, Cooldown
	
	private List<String> waterWalkers = new ArrayList<String>();
	
	public FruitManager()
	{
		if(fruits.isEmpty())
		{
			try {
				loadFruits();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		instance = this;
	}
	
	public static FruitManager getInstance()
	{
		return instance;
	}
	
	public long getTime()
	{
		return currentTime;
	}
	
	public void setTime()
	{
		currentTime = System.currentTimeMillis();
	}
	
	public long getChangeInTime()
	{
		return (currentTime-System.currentTimeMillis());
	}
	
	public ConcurrentHashMap<String,Integer> getFruits()
	{
		return fruits;
	}
	
	public ConcurrentHashMap<String,Long> getCooldowns()
	{
		return cooldowns;
	}

	public long getPlayerCooldown(Player p)
	{
		if(cooldowns.get(p.getName()) == null)
			return 0;
		else
			return cooldowns.get(p.getName());
	}
	
	public void setPlayerCooldown(Player p)
	{
		cooldowns.put(p.getName(), (long)getFruitByPlayer(p).getCooldown());
	}
	
	public boolean checkIfPlayerIsDFUser(Player p)
	{
		boolean flag = false;
		
		if(fruits.containsKey(p.getName()))
		{
			flag = true;
		}
		
		return flag;
	}
	
	public void setPlayerCooldown(Player p, long cd)
	{
		cooldowns.put(p.getName(), cd);
	}
	
	public List<String> getWaterWalkers()
	{
		return waterWalkers;
	}
	
    public void saveWaterWalkers() throws IOException
    {
    	FileOutputStream fos = new FileOutputStream(DevilFruits.getInstance().getDataFolder() + "/ww.xml");
    	if(fos == null) return;
    	ObjectOutputStream oos = new ObjectOutputStream(fos);
    	if(oos == null) return;
    	oos.writeObject(waterWalkers);
    	oos.close();
    }
    
	@SuppressWarnings("unchecked")
	public void loadWaterWalkers() throws IOException, ClassNotFoundException
    {
    	FileInputStream fis = new FileInputStream(DevilFruits.getInstance().getDataFolder() + "/ww.xml");
    	if(fis == null) return;
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	if(ois == null) return;
    	waterWalkers = (List<String>) ois.readObject();
    	ois.close();
    }
	
    public void saveFruits() throws IOException
    {
    	FileOutputStream fos = new FileOutputStream(DevilFruits.getInstance().getDataFolder() + "/fruitdata.xml");
    	if(fos == null) return;
    	ObjectOutputStream oos = new ObjectOutputStream(fos);
    	if(oos == null) return;
    	oos.writeObject(fruits);
    	oos.close();
    }
    
	@SuppressWarnings("unchecked")
	public void loadFruits() throws IOException, ClassNotFoundException
    {
    	FileInputStream fis = new FileInputStream(DevilFruits.getInstance().getDataFolder() + "/fruitdata.xml");
    	if(fis == null) return;
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	if(ois == null) return;
    	fruits = (ConcurrentHashMap<String,Integer>) ois.readObject();
    	ois.close();
    }
	
	public void printPlayerInfo(Player sender, Player check)
	{
		if(check == null) return;
		Fruit f = getFruitByPlayer(check);
		if(f != null)
		{
			sender.sendMessage(ChatColor.GREEN + "" + check.getName() + " has eatin the " + f.getName());
		}
		else
		{
			sender.sendMessage(ChatColor.GREEN + "" + check.getName() + " has not eatin a devil fruit.");
		}
	}
	
	public void printAllPlayerInfo(Player sender)
	{
		Enumeration<String> fe = FruitManager.getInstance().getFruits().keys();
		int[] numOfFruits = new int[Fruits.values().length];
		Fruit f = null;
		String s = null;
		
		sender.sendMessage(ChatColor.GREEN + "Devil Fruits Tracker:");
		while(fe.hasMoreElements())
		{
			s = (String) fe.nextElement();
			
			f = getFruitByPlayerName(s);
			
			if(f == null) continue;
			
			numOfFruits[f.getId()] = numOfFruits[f.getId()] + 1;
		}
		
		if(numOfFruits == null) return;
		
		for(int i = 0; i < Fruits.values().length; i++)
		{
			sender.sendMessage(getFruitById(i).getName() + "" + ChatColor.WHITE + ": " + numOfFruits[i]);
		}
	}
	
	public Fruit getFruitById(int id)
	{
		return Fruits.values()[id].getFruit();
	}
	
	public Fruit getFruitByPlayer(Player p)
	{
		if(fruits.get(p.getName()) == null) return null;
		return getFruitById(fruits.get(p.getName()));
	}
	
	public Fruit getFruitByPlayerName(String name)
	{
		return getFruitById(fruits.get(name));
	}
	
	public Fruit getFruitByItem(ItemStack is)
	{
		if(!isItemAFruit(is)) return null;
		
		Fruit fruit = null;
		String type = is.getItemMeta().getLore().get(0);
		
		for(int i = 0; i < Fruits.values().length; i++)
		{
			if(type.contains(Fruits.values()[i].getFruit().getName()))
			{
				fruit = Fruits.values()[i].getFruit();
				break;
			}
		}
		return fruit;
	}
	
	public void removeDevilFruit(Player p)
	{
	    for (PotionEffect effect : p.getActivePotionEffects())		//Clear Effects
	        p.removePotionEffect(effect.getType());
		fruits.remove(p.getName());
	}
	
	public void printDevilFruits(Player p)
	{
		p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Devil Fruits");
		for(int i = 0; i < Fruits.values().length; i++)
		{
			p.sendMessage(ChatColor.GREEN + "" + i + ". " + Fruits.values()[i].getFruit().getName());
		}
	}
	
	public void checkPassiveEffects(Player p, Fruit f)
	{
		for (PotionEffect effect : f.getPassiveEffects())
		{
			if(!p.getActivePotionEffects().contains(effect))
			{
				p.addPotionEffect(effect);
			}
		}
	}
	
	public void applyPassiveFruitEffects(Player p, Fruit f)
	{
		List<PotionEffect> pE = f.getPassiveEffects();
		
	    for (PotionEffect effect : p.getActivePotionEffects())		//Clear Effects
	        p.removePotionEffect(effect.getType());
	    
		for(int i = 0; i < pE.size(); i++)
		{
			p.addPotionEffect(pE.get(i));
		}
	}
	
	public void playerEatFruit(final Player p, Fruit f)
	{
		if(!fruits.containsKey(p.getName()))
		{
			if(f.getId() == 12)
			{
				p.sendMessage(ChatColor.GREEN + "You have no devil fruit to remove.");
				return;
			}
			else if(f.getId() == 17)
			{
				PermissionsEx.getPermissionManager().getUser(p.getName()).addPermission("nocheatplus.checks.moving");
			}
			
			p.sendMessage(ChatColor.GREEN + "You have just eaten the " + f.getName() + ChatColor.WHITE + ".");
			fruits.put(p.getName(), f.getId());
			applyPassiveFruitEffects(p,f);
			
			DevilFruits.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "A " + f.getName() + "" + ChatColor.GOLD +" has just been eaten!");
			
			new BukkitRunnable() {
				@Override
				public void run() {
			    	p.setItemInHand(null);
			    	p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10*20, 2));
			    	p.updateInventory();
				}
			}.runTaskLater(DevilFruits.getInstance(), 1L);

		}
		else
		{
			if(f.getId() == 12)
			{
				removeDevilFruit(p);
				p.sendMessage(ChatColor.RED + "Your devil fruit has been removed.");
				new BukkitRunnable() {
					@Override
					public void run() {
				    	p.setItemInHand(null);
				    	p.updateInventory();
					}
				}.runTaskLater(DevilFruits.getInstance(), 1L);
			}
			else
			{
				p.sendMessage(ChatColor.RED + "You already ate a devil fruit.");
			}
		}
	}
	
	public boolean roll(int chance)
	{
		boolean flag = false;
		Random generator = new Random();
		int randomNumber = generator.nextInt(100);
		
		if(randomNumber <= chance)
		{
			flag = true;
		}
		
		return flag;
	}
	
	public boolean isItemAFruit(ItemStack is)
	{
		boolean flag = false;
		
		if(is != null)
		{
			if(is.getType().isEdible())
			{
				if(is.getItemMeta().getLore() != null)
				{
				    if(is.getItemMeta().getLore().get(0).contains("Fruit"));
				    {
				    	flag = true;
				    }
				}
			}
		}
		
		return flag;
	}
	
	public enum Fruits
	{
		Speed{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.AQUA + "Speed Fruit", 0,260);
				tempFruit.setDesc("You can dig faster and run faster.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 1));
				
				return tempFruit;
			}
		},
		Spring{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLUE + "Spring Fruit", 1,391);
				tempFruit.setDesc("You can jump higher and run faster.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.JUMP, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
				
				return tempFruit;
			}
		},
		Stone{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.GRAY + "Stone Fruit", 2,392);
				tempFruit.setDesc("You gain more HP and damage resistance but walk slower.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 9999999, 3));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 1));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 2));
				
				return tempFruit;
			}
		},
		Night{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Night Fruit", 3,375);
				tempFruit.setDesc("Ability to see at night and run faster at night.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 5));
				
				return tempFruit;
			}
		},
		Flame{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.RED + "Flame Fruit", 4,260,10);
				tempFruit.setDesc("Resistent to fire and 25% chance to set attackers on fire");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999999, 4));
				
				return tempFruit;
			}
		},
		Gomu{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "GomuGomu Fruit", 5,396);
				tempFruit.setDesc("Resistant to lightning, projectiles, and physical damage.");		//TODO
				tempFruit.setLore("Once belonged to the Pirate Age's second Pirate King.");
				
				return tempFruit;
			}
		},
		Mole{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.DARK_BLUE + "Mole Fruit", 6,367);
				tempFruit.setDesc("Ability to dig extremely fast and see in the dark.");
				tempFruit.setLore("King of the undergroud.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 4));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 3));
				
				return tempFruit;
			}
		},
		Poison{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.DARK_BLUE + "Poison Fruit", 7,375);
				tempFruit.setDesc("Poison players that attack you.");
				
				return tempFruit;
			}
		}
		,
		Heal{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.GREEN + "Heal Fruit", 8,360);
				tempFruit.setDesc("Regen health over time and have boosted health.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 9999999, 2));
				
				return tempFruit;
			}
		},
		Zombie{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.DARK_GRAY + "Zombie Fruit", 9,367);
				tempFruit.setDesc("Regen health over time and cause players you hit to become hungry.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 1));
				
				return tempFruit;
			}
		},
		Blind{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Blind Fruit", 10,349);
				tempFruit.setDesc("Cause players you hit to become blind.");
				
				return tempFruit;
			}
		},
		Vanish{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Vanish Fruit", 11,322);
				tempFruit.setDesc("Shift to become invisable for 10s.");
				
				return tempFruit;
			}
		},
		Angel{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.MAGIC + "Angel Fruit", 12,322);
				tempFruit.setDesc("Removes all devil fruit powers.");
				
				return tempFruit;
			}
		},
		Turtle{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.GREEN + "Turtle Fruit", 13,297);
				tempFruit.setDesc("Take reduced damage when sneaking but walk slower.");
				
				return tempFruit;
			}
		},
		Juggernaut{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.GRAY + "Juggernaut Fruit", 14,360);
				tempFruit.setDesc("Boosted health and reduced knockback.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 9999999, 2));
				
				return tempFruit;
			}
		},
		Madman{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.RED + "Madman Fruit", 15,367);
				tempFruit.setDesc("Players that attack you become confused and you gain a damage buff.");
				
				return tempFruit;
			}
		},
		Blink{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Blink Fruit", 16,396,30);
				tempFruit.setDesc("Shift-Click to teleport. 30s CD");
				
				return tempFruit;
			}
		},
		Spider{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Spider Fruit", 17,375);
				tempFruit.setDesc("Hold shift to climb up walls.");
				
				return tempFruit;
			}
		},
		Weakness{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.DARK_PURPLE + "Weakness Fruit", 18,375);
				tempFruit.setDesc("Attackers get weakened and you gain strength.");
				
				return tempFruit;
			}
		},
		Life{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Life Fruit", 19,360);
				tempFruit.setDesc("Heal on all melee attacks.");
				
				return tempFruit;
			}
		},
		WindSoul{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Wind Soul Fruit", 20,360);
				tempFruit.setDesc("Sprint faster and take less damage while sprinting.");
				
				return tempFruit;
			}
		},
		Obsidian{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLACK + "Obsidian Fruit", 21,297);
				tempFruit.setDesc("Immune to fire and projectile damage.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999999, 2));
				
				return tempFruit;
			}
		},
		Hero{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLUE + "Hero Fruit", 22,400);
				tempFruit.setDesc("Increased damage and speed.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 2));
				
				return tempFruit;
			}
		},
		Villain{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLACK + "Villain Fruit", 23,375);
				tempFruit.setDesc("Increased damage and fire resistance.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 2));
				
				return tempFruit;
			}
		},
		Steel{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.GRAY + "Steel Fruit", 24,320);
				tempFruit.setDesc("20% chance to block incoming attacks.");
				
				return tempFruit;
			}
		};

		public abstract Fruit getFruit();
	}
	
}
