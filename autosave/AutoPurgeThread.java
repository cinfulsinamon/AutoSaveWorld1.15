package autosave;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;

import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class AutoPurgeThread extends Thread {
	
	protected final Logger log = Logger.getLogger("Minecraft");
	private AutoSave plugin = null;
	private AutoSaveConfig config;
	private AutoSaveConfigMSG configmsg;
	private boolean run = true;
	private boolean command = false;
	AutoPurgeThread(AutoSave plugin, AutoSaveConfig config, AutoSaveConfigMSG configmsg) {
		this.plugin = plugin;
		this.config = config;
		this.configmsg = configmsg;
	}

	// Allows for the thread to naturally exit if value is false
	public void setRun(boolean run) {
		this.run = run;
	}
	private int runnow;
	public void startpurge()
	{
	command = true;
	runnow = config.purgeInterval;
	}
	
	
	// The code to run...weee
	public void run() {
		if (config == null) {
			return;
		}

		log.info(String
				.format("[%s] AutoSaveThread Started: Interval is %d seconds, Warn Times are %s",
						plugin.getDescription().getName(), config.purgeInterval,
						Generic.join(",", config.varWarnTimes)));
		while (run) {
			// Prevent AutoPurge from never sleeping
			// If interval is 0, sleep for 5 seconds and skip saving
			if(config.varInterval == 0) {
				try {
					Thread.sleep(5000);
				} catch(InterruptedException e) {
					// care
				}
				continue;
			}
			
			
			// Do our Sleep stuff!
			for (runnow = 0; runnow < config.backupInterval; runnow++) {
				try {
					if (!run) {
						if (config.varDebug) {
							log.info(String.format("[%s] Graceful quit of AutoPurgeThread", plugin.getDescription().getName()));
						}
						return;
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.info("Could not sleep!");
				}
			}
			
			 if (config.purgeEnabled||command) performPurge();


		}
	}
	
	public void performPurge() {
		plugin.debug("Purge started");
		if ((plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null)){
		plugin.debug("WE found, purging");
		WGpurge();}
		if ((plugin.getServer().getPluginManager().getPlugin("LWC") != null)){
		plugin.debug("LWC found purging");
			LWCpurge();}
		command = false;
		plugin.debug("Purge finished");
	}
	
	public void WGpurge() {
		long awaytime = config.purgeAwayTime;
		WorldGuardPlugin wg = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		for(World w : Bukkit.getWorlds()) {
		plugin.debug("Checking WG protections in world "+w.toString());
		RegionManager m = wg.getRegionManager(w);
		for(ProtectedRegion rg : m.getRegions().values()) {
			plugin.debug("Checking region "+rg.getId());
			DefaultDomain dd = rg.getOwners();
			ArrayList<String> pltodelete = new ArrayList<String>();
			for (String checkPlayer : dd.getPlayers()) {
				plugin.debug("Checking player "+checkPlayer);
				if (Bukkit.getOfflinePlayer(checkPlayer).hasPlayedBefore()) {
				long timelp = Bukkit.getOfflinePlayer(checkPlayer).getLastPlayed();
				if (System.currentTimeMillis() - timelp >= awaytime)
				{
					pltodelete.add(checkPlayer);
					plugin.debug(checkPlayer+" is inactive");
				}
				}
			}
			if (dd.getPlayers().size() <= pltodelete.size()) {
				m.removeRegion(rg.getId());
				plugin.debug("No active owners for region " +rg.getId() + " Removing region");
			} else {
				if (pltodelete.size() != 0) {
					for (String plrem : pltodelete) {
					dd.removePlayer(plrem);
					plugin.debug("There is still some active owners in region " +rg.getId() + " Removing inactive owners");
					}
					rg.setOwners(dd);
				}
				
			}
			try {m.save();
			plugin.debug("Saving WG database");
			} catch (ProtectionDatabaseException e) {e.printStackTrace();}
			
			}
		}
	}
	
	public void LWCpurge() {
		long awaytime = config.purgeAwayTime;
		LWCPlugin lwc = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		OfflinePlayer[] checkPlayers = Bukkit.getServer().getOfflinePlayers();
		for (OfflinePlayer pl : checkPlayers)
		{
			if (System.currentTimeMillis() - pl.getLastPlayed() >= awaytime) {
				plugin.debug(pl.toString()+" is inactive Removing all LWC protections");
				lwc.getLWC().fastRemoveProtectionsByPlayer(sender, pl.toString(), true);
			}
		}
		
	}
	
	
	
	
	
	
}
