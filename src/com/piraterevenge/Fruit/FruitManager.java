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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.piraterevenge.DevilFruits;

public class FruitManager {
	
	private ConcurrentHashMap<String,Integer> fruits = new ConcurrentHashMap<String,Integer>(); // PlayerName, Fruit ID
	private static FruitManager instance;
	private long currentTime = 0;
	
	private ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap<String,Long>(); // PlayerName, Cooldown
	private ConcurrentHashMap<String, Long> disabled = new ConcurrentHashMap<String,Long>(); // PlayerName, Cooldown
	
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
	
	public ConcurrentHashMap<String,Long> getDisabled()
	{
		return disabled;
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
		
		if(fruits.containsKey(p.getName()) && !disabled.containsKey(p.getName()))
		{
			flag = true;
		}
		
		return flag;
	}
	
	public boolean checkIfPlayerIsDFUser(String name)
	{
		boolean flag = false;
		
		if(fruits.containsKey(name) && !disabled.containsKey(name))
		{
			flag = true;
		}
		
		return flag;
	}
	
	public void setPlayerCooldown(Player p, long cd)
	{
		cooldowns.put(p.getName(), cd);
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
		Object[][] fruitList = new Object[Fruits.values().length][10];
		Fruit f = null;
		String playerName = null;
		String dfStringList = null;
		int[] listLength = new int[Fruits.values().length];
		
		sender.sendMessage(ChatColor.GREEN + "Devil Fruits Tracker:");
		while(fe.hasMoreElements())
		{
			playerName = (String) fe.nextElement();
			
			if(playerName == null) continue;
			if(!FruitManager.getInstance().checkIfPlayerIsDFUser(playerName)) continue;
			
			f = FruitManager.getInstance().getFruitByPlayer(playerName);
			
			if(f == null) continue;
			
			if(fruitList[f.getId()] == null){
				System.out.println("Empty");
				fruitList[f.getId()][0] = playerName;
				System.out.println("Added");
			}else{
				System.out.println("Not Empty");
				fruitList[f.getId()][listLength[f.getId()]] = playerName;
				System.out.println("Added");
			}
			listLength[f.getId()]++;
		}
		
		if(fruitList == null) return;
		
		for(int i = 0; i < Fruits.values().length; i++)
		{
			dfStringList = getFruitById(i).getName() + "" + ChatColor.WHITE + ": ";
			if( fruitList[i] != null){
				for(int j = 0; j < listLength[i]; j++){
					dfStringList = dfStringList + fruitList[i][j] + " ";
				}
			}
			sender.sendMessage(dfStringList);
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
	
	public Fruit getFruitByPlayer(String  name)
	{
		if(fruits.get(name) == null) return null;
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
	
	public Fruit getRandomFruit(){
		Random rand = new Random();
		int number = rand.nextInt((Fruits.values().length - 0) + 1) + 0;
		System.out.println(number);
		return Fruits.values()[number].getFruit();
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
	
	public void setPlayerFruit(Player p, int fruitId){
		Fruit f = getFruitById(fruitId);
		if(!fruits.containsKey(p.getName()))
		{
			fruits.put(p.getName(), fruitId);
			applyPassiveFruitEffects(p,f);
		}
		else{
			fruits.remove(p.getName());
			fruits.put(p.getName(), fruitId);
			applyPassiveFruitEffects(p,f);
		}
	}
	
	public void playerEatFruit(final Player p, Fruit f, PlayerInteractEvent event)
	{
		if(!fruits.containsKey(p.getName()))
		{
			if(f.getId() == 12)
			{
				p.sendMessage(ChatColor.GREEN + "You have no devil fruit to remove.");
				return;
			}
			
			Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " has just eaten the " + f.getName() + ".");
			p.sendMessage(ChatColor.GREEN + "You have just eaten the " + f.getName() + ".");
			fruits.put(p.getName(), f.getId());
			applyPassiveFruitEffects(p,f);
			
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
				event.setCancelled(true);
			}
		}
	}
	
	public void playerEatFruit(final Player p, Fruit f, PlayerItemConsumeEvent event)
	{
		if(!fruits.containsKey(p.getName()))
		{
			if(f.getId() == 12)
			{
				p.sendMessage(ChatColor.GREEN + "You have no devil fruit to remove.");
				return;
			}
			
			Bukkit.broadcastMessage(ChatColor.GREEN + p.getName() + " has just eaten the " + f.getName() + ".");
			p.sendMessage(ChatColor.GREEN + "You have just eaten the " + f.getName() + ".");
			fruits.put(p.getName(), f.getId());
			applyPassiveFruitEffects(p,f);
			
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
				event.setCancelled(true);
			}
		}
	}
	
	public void printFruitData(Player p)
	{
		String[][] fruitsPlayers = new String[Fruits.values().length][10];
	
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
				tempFruit.setDesc("You can dig faster and run faster and you attackers are slowed.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 2));
				
				return tempFruit;
			}
		},
		Spring{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLUE + "Spring Fruit", 1,391);
				tempFruit.setDesc("Immune to Fall Damage and sneak for a super jump.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 0));
				
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
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
				
				return tempFruit;
			}
		},
		Night{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Night Fruit", 3,375);
				tempFruit.setDesc("Ability to see in the dark and makes you faster and stronger at night.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 3));
				
				return tempFruit;
			}
		},
		Flame{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.RED + "Flame Fruit", 4,260,5);
				tempFruit.setDesc("Immune to fire, everything you hit lights on fire, and projectiles burn away.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999999, 3));
				
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
				tempFruit.setDesc("Ability to dig extremely fast, see in the dark, and take less damage.");
				tempFruit.setLore("King of the undergroud.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 2));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
				
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
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 0));
				
				return tempFruit;
			}
		},
		Blind{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Blind Fruit", 10,349);
				tempFruit.setDesc("Cause players you hit to become blind.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 3));
				
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
				tempFruit.setDesc("A Little extra Health and Take reduced damage when sneaking.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 9999999, 0));
				
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
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Blink Fruit", 16,396,8);
				tempFruit.setDesc("Shift-Click to teleport. 8s CD");
				
				return tempFruit;
			}
		},
		Goliath{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.GOLD + "Goliath Fruit", 17,357);
				tempFruit.setDesc("Gives Stregth and takes less damage while at high health.");
			
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
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Life Fruit", 19,360,10);
				tempFruit.setDesc("Attacking gives a healing boost every 10 seconds.");
				
				return tempFruit;
			}
		},
		WindSoul{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Wind Soul Fruit", 20,360);
				tempFruit.setDesc("Faster and Take less damage.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
				
				return tempFruit;
			}
		},
		Obsidian{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLACK + "Obsidian Fruit", 21,297);
				tempFruit.setDesc("Immune to fire and projectile damage.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999999, 1));
				
				return tempFruit;
			}
		},
		Plenty{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Plenty Fruit", 22,391,10);
				tempFruit.setDesc("Never hungry and gains strength while attacking.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 1));
				
				return tempFruit;
			}
		},
		Knowledge{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.RED + "Knowledge Fruit", 23,297,60*20);
				tempFruit.setDesc("Gains Strength and Speed when you get exp.");
				
				return tempFruit;
			}
		},
		Death{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.BLACK + "Death Fruit", 24,394,120);
				tempFruit.setDesc("Causes Attackers to Wither.");
					
				return tempFruit;
			}
		},
		Day{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.WHITE + "Day Fruit", 25,375);
				tempFruit.setDesc("Fire Proof and makes you faster and stronger during the day.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999999, 3));
					
				return tempFruit;
			}
		},
		Ignorance{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Ignorance Fruit", 26,319);
				tempFruit.setDesc("Makes you strong, fast, and gives resistance at low health.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 1));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 0));
						
				return tempFruit;
			}
		},
		Glutton{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.DARK_GREEN + "Glutton Fruit", 27,400);
				tempFruit.setDesc("Gives Extra Health and stated hunger.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 9999999, 4));
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 1));
							
				return tempFruit;
			}
		},
		Chicken{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.DARK_GRAY + "Chicken Fruit", 28,366);
				tempFruit.setDesc("Immune to Fall Damage and Gains Speed When Hit.");
			
				return tempFruit;
			}
		      
		},
		Spider{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.YELLOW + "Spider Fruit", 29,375);
				tempFruit.setDesc("Poison bite and Hold shift to climb up walls.");
				
				return tempFruit;
			}
		},
		Ice{
			@Override
			public Fruit getFruit()
			{
				Fruit tempFruit = new Fruit(ChatColor.AQUA + "Ice Fruit", 30,350);
				tempFruit.setDesc("Gains some speed and attackers get chilled.");
				tempFruit.addPassiveEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
			
				return tempFruit;
			}
		      
		};
	      
		public abstract Fruit getFruit();
		
	}
	
	public static boolean percentChance(double percent){
	    if(percent > 100 || percent < 0){
	        throw new IllegalArgumentException("Percentage cannot be greater than 100 or less than 0!");
	    }
	    double result = new Random().nextDouble() * 100;
	    return result <= percent;
	}
}  