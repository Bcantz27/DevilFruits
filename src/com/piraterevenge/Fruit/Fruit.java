package com.piraterevenge.Fruit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class Fruit {
	private String name;
	private String desc;
	private String lore;
	private int id;
	private int itemId;
	private int cooldown;		//in seconds
	
	private List<PotionEffect> passiveEffects = new ArrayList<PotionEffect>();
	
	public Fruit()
	{
		this.name = "No Name";
		this.id = -1;
		this.itemId = 260; // Apple
		this.cooldown = 0;
	}
	
	public Fruit(String name, int id)
	{
		this.name = name;
		this.id = id;
		this.itemId = 260; // Apple
		this.cooldown = 0;
	}
	
	public Fruit(String name, int id, int itemId)
	{
		this.name = name;
		this.id = id;
		this.itemId = itemId;
		this.cooldown = 0;
	}
	
	public Fruit(String name, int id, int itemId, int cooldown)
	{
		this.name = name;
		this.id = id;
		this.itemId = itemId;
		this.cooldown = cooldown;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
	public int getCooldown()
	{
		return cooldown;
	}
	
	public String getLore()
	{
		return lore;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	
	public void setLore(String lore)
	{
		this.lore = lore;
	}
	
	public void addPassiveEffects(List<PotionEffect> passiveEffects)
	{
		this.passiveEffects = passiveEffects;
	}
	
	public void addPassiveEffect(PotionEffect passiveEffect)
	{
		this.passiveEffects.add(passiveEffect);
	}
	
	public List<PotionEffect> getPassiveEffects()
	{
		return passiveEffects;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(itemId,1);
		ItemMeta im = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("This is the " + name +".");
		lore.add(desc);
		lore.add(this.lore);
		im.setDisplayName(name);
		im.setLore(lore);
		item.setItemMeta(im);
		
		return item;
	}
}
