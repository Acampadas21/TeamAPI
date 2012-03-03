package net.acampadas21.teamapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.acampadas21.teamapi.groups.DBTeam;
import net.acampadas21.teamapi.groups.Team;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamManager {

	// NOTEPAD
	
	// In the "leader" row 1 means that the player is the leader. Any other value means the opposite.
	
	private FileConfiguration config = null;
	private File configF = null;
	private TeamAPI plugin;
	
	private HashMap<String, Team> teams;

	public TeamManager(TeamAPI instance) {
		plugin = instance;
		teams = new HashMap<String, Team>();
	}
	
	private void buildhm(){
		
	}

	private void reloadConfig() {
	    if (configF == null) {
	    configF = new File(plugin.getDataFolder(), "teams.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(configF);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("teams_default.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}

	private FileConfiguration getCustomConfig() {
	    if (config == null) {
	        reloadConfig();
	    }
	    return config;
	}
	
	private void save() {
	    if (config == null || configF == null) {
	    return;
	    }
	    try {
	        config.save(configF);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	
	/**
	 * Checks if exists any team with that name. 
	 * @param name The name of the team that is going to be checked.
	 * @return True if exists a team with that name
	 */
	public boolean isTeam(String name) {
		return config.contains("teams." + name);
	}

	/**
	 * Creates a new team. 
	 * @param name The name of the team that is going to be checked.
	 * @return True if exists a team with that name
	 */
	public boolean newTeam(String name) {
		if (!isTeam(name)) {
			db.refreshConnection();
			db.standardQuery("CREATE TABLE " + name + "(player VARCHAR(20), leader TINYINT);");
			db.closeConnection();
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a player to a certain team
	 * @param p The player we are adding
	 * @param t The team where the player is going to be added.
	 */
	public void joinTeam(Player p, Team t){
		db.refreshConnection();
			db.standardQuery("INSERT INTO " + t.getName() + " VALUES("+ t.getName() +", 0);");
			db.closeConnection();
	}
	
	/**
	 * Removes a team
	 * @param t The team that is going to be removed.
	 */
	public void deleteTeam(Team t){
		if(isTeam(t.getName())){
			db.refreshConnection();
			db.standardQuery("DROP TABLE " + t.getName() + ";");
			db.closeConnection();
		}
	
	}
	
	/**
	 * Gets a team from its name
	 * @param name The name of the team
	 * @return Team with that name. Returns null if there isn't a team with that name.
	 */
	public Team getTeamByName(String name){
		if(isTeam(name)){
			ArrayList<Player> p = new ArrayList<Player>();
			Player leader = null;
				try {
					db.refreshConnection();
				    ResultSet rs = db.sqlQuery("SELECT * FROM " + name);
				    while (rs.next()) {
				        p.add(Bukkit.getServer().getPlayer(rs.getString(1)));
				        if(rs.getInt(2) != 0) leader = Bukkit.getServer().getPlayer(rs.getString(1));
				    }
				    rs.close();
				    db.closeConnection();
				    
				} catch (SQLException e) {
					TeamAPI.logger.log(Level.SEVERE, "Can't connect to database");
				}
				return new DBTeam(name, p, leader, this);
				
		}
		return null;
	}
	
	/**
	 * Gets the team where a player belongs.
	 * @param p The player we are checking.
	 * @return The team where the player belongs. Returns null if it doesn't belong to any team.
	 */
	public Team getTeamByPlayer(Player p){
		Team[] teams = listTeams();
		if(teams == null) return null;
		for(Team t : teams){
			if(t.isInTeam(p)) return t;
		}
		return null;
	}


	/**
	 * Gets all the teams stored in the database.
	 * @return Array that contains all the teams.
	 */
    public Team[] listTeams() {
		ArrayList<Team> t = new ArrayList<Team>();
		try {
			db.refreshConnection();
	    	ResultSet rs = db.sqlQuery("SELECT name FROM sqlite_master WHERE type='table'");
			while(rs.next()){
				t.add(this.getTeamByName(rs.getString(1)));
			}
			rs.close();
			db.closeConnection();
			return (Team[]) (t.toArray());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    
    public void clearTeams(){
    		f.delete();
    		dbInit();
    	
    }
	
	
}
