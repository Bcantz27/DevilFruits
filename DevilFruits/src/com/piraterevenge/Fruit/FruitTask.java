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
		
		if(fe == null) return;
		if(cd == null) return;
		
		String s = null;
		Player p = null;
		Fruit f = null;
		long cooldown = 0;
		long time = 0;
		
		FruitManager.getInstance().setTime();
	    
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
				    } else {
			    		if(!p.hasPotionEffect(PotionEffectType.SPEED))
			    			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 3));
				    }
					break;
				case 8:		//Heal Fruit
				    if(time > 0 && time < 12300) {
				    } else {
				    	if(p.getHealth() < 5)
				    		if(!p.hasPotionEffect(PotionEffectType.REGENERATION))
				    			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 2));
				    }
					break;
				case 13:	//Turtle Fruit
					if(p.isSneaking())
					{
				    	if(!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 5));
					}
					break;
				case 20:	//WindSoul Fruit
					if(p.isSprinting())
					{
				    	if(!p.hasPotionEffect(PotionEffectType.SPEED))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5*20, 2));
				    	if(!p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 2));
					}
					break;
				default: 
					break;
			}
		}
	}

}
