package com.piraterevenge;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.piraterevenge.Fruit.FruitManager;
import com.piraterevenge.Fruit.FruitTask;
import com.piraterevenge.Listeners.CommandListener;
import com.piraterevenge.Listeners.EntityListener;
import com.piraterevenge.Listeners.PlayerListener;
 
public final class DevilFruits extends JavaPlugin 
{ 
	public static DevilFruits instance;
	public static Logger logger = Logger.getLogger("Minecraft");
	public static final long TASK_PERIOD = 5L;

    @Override
    public void onEnable(){
    	getDataFolder().mkdirs();
    	instance = this;
    	FruitManager fm = new FruitManager();
    	
    	try {
			FruitManager.getInstance().loadFruits();
			FruitManager.getInstance().loadWaterWalkers();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	this.getCommand("devilfruits").setExecutor(new CommandListener());
    	this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    	this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
    	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new FruitTask(), 0L, 20L*TASK_PERIOD);
    }
 
    @Override
    public void onDisable() {
    	try {
			FruitManager.getInstance().saveFruits();
			FruitManager.getInstance().saveWaterWalkers();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static DevilFruits getInstance()
    {
    	return instance;
    }  
}