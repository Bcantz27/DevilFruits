package com.piraterevenge.Fruit;

import java.util.Enumeration;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.piraterevenge.DevilFruits;

public class FruitTask implements Runnable {

	@Override
	public void run() {
		Enumeration<String> fe = FruitManager.getInstance().getFruits().keys();
		Enumeration<String> cd = FruitManager.getInstance().getCooldowns().keys();
		Enumeration<String> dis = FruitManager.getInstance().getDisabled().keys();
		if(fe == null) return;
		if(cd == null) return;
		
		String s = null;
		Player p = null;
		Fruit f = null;
		long cooldown = 0;
		long time = 0;
		
		FruitManager.getInstance().setTime();
		
		while(dis.hasMoreElements())
		{
			s = (String) cd.nextElement();
			p = DevilFruits.getInstance().getServer().getPlayer(s);
			
			if(p == null) continue;
			
			if(FruitManager.getInstance().getDisabled().get(s) != null)
			{
				cooldown = FruitManager.getInstance().getDisabled().get(s);
			}
			else
			{
				FruitManager.getInstance().getDisabled().remove(p.getName());
				continue;
			}
			
			if(cooldown <= 0)
			{
				FruitManager.getInstance().getDisabled().remove(p.getName());
			}
			else if(cooldown > 0)
			{
				if(cooldown > 3600)
				{
					cooldown = 3600;
				}
				FruitManager.getInstance().getDisabled().remove(p.getName());
				FruitManager.getInstance().getDisabled().put(p.getName(), (cooldown-DevilFruits.TASK_PERIOD));
			}
		}
	    
		while(cd.hasMoreElements())
		{
			s = (String) cd.nextElement();
			p = DevilFruits.getInstance().getServer().getPlayer(s);
			
			if(p == null) continue;
			if(!FruitManager.getInstance().checkIfPlayerIsDFUser(p)) continue;
			
			if(FruitManager.getInstance().getCooldowns().get(s) != null)
				cooldown = FruitManager.getInstance().getCooldowns().get(s);
			else
				cooldown = 0;
			
			if(cooldown < 0)
			{
				FruitManager.getInstance().getCooldowns().remove(p.getName());
				FruitManager.getInstance().getCooldowns().put(p.getName(), (long) 0);
			}
			else if(cooldown > 0)
			{
				FruitManager.getInstance().getCooldowns().remove(p.getName());
				FruitManager.getInstance().getCooldowns().put(p.getName(), (cooldown-DevilFruits.TASK_PERIOD));
			}
		}
		
		while(fe.hasMoreElements())
		{
			s = (String) fe.nextElement();
			p = DevilFruits.getInstance().getServer().getPlayer(s);
			
			if(p == null) continue;
			if(!FruitManager.getInstance().checkIfPlayerIsDFUser(p)) continue;
			
			f = FruitManager.getInstance().getFruitByPlayer(p);
			time = p.getWorld().getTime();
			
			//DevilFruits.getInstance().getLogger().log(Level.INFO, "Player: " + p.getName() + " Fruit: " + f.getName());
			
			FruitManager.getInstance().checkPassiveEffects(p, FruitManager.getInstance().getFruitByPlayer(p));
			
			switch(f.getId())
			{
				case 3:		//Night Fruit
				    if(time > 0 && time < 12300) {
			    		if(!p.hasPotionEffect(PotionEffectType.SPEED))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 0));
			    		if(!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15*20, 0));
				    } else {
			    		if(!p.hasPotionEffect(PotionEffectType.SPEED))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 1));
			    		if(!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15*20, 1));
				    }
					break;
				case 8:		//Heal Fruit
			    	if(p.getHealth() < 9)
			    		if(!p.hasPotionEffect(PotionEffectType.REGENERATION))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 2));
					break;
				case 13:	//Turtle Fruit
					if(p.isSneaking())
					{
				    	if(!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 3));
					}
					break;
				case 25:		//Day Fruit
				    if(time > 0 && time > 12300) {
			    		if(!p.hasPotionEffect(PotionEffectType.SPEED))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 0));
			    		if(!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15*20, 0));
				    } else {
			    		if(!p.hasPotionEffect(PotionEffectType.SPEED))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 1));
			    		if(!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15*20, 1));
				    }
					break;
				case 26:		//Ingnorance Fruit
				    if(p.getHealth() < 10)
				    	if(!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10*20, 1));
					break;
				case 17:		//Goliath Fruit
				    if(p.getHealth() > 8)
				    	if(!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7*20, 1));
				    if(p.getHealth() > 8)
				    	if(!p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7*20, 1));
					break;
				default: 
					break;
			}
		}
	}

}
