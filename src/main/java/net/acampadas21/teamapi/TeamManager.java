package net.acampadas21.teamapi;

import java.util.HashMap;

import net.acampadas21.teamapi.groups.Team;

import org.bukkit.entity.Player;

public class TeamManager {


	private TeamAPI plugin; 
	private HashMap<String, Team> teams;

	public TeamManager(TeamAPI instance) {
		plugin = instance;
		teams = new HashMap<String, Team>();
	}
	
	/**
	 * Checks if exists any team with that name. 
	 * @param name The name of the team that is going to be checked.
	 * @return True if exists a team with that name
	 */
	public boolean isTeam(String name) {
		return teams.containsKey(name);
	}

	/**
	 * Creates a new team. 
	 * @param name The name of the team that is going to be checked.
	 * @return True if exists a team with that name
	 */
	public boolean newTeam(String name) {
		if (!isTeam(name)) {
			teams.put(name, new Team(name));
			return true;
		}
		return false;
	}
	

	/**
	 * Removes a team
	 * @param t The team that is going to be removed.
	 */
	public void deleteTeam(Team t){
		if(isTeam(t.getName())){
			teams.remove(t.getName());
		}
	
	}
	
	/**
	 * Gets a team from its name
	 * @param name The name of the team
	 * @return Team with that name. Returns null if there isn't a team with that name.
	 */
	public Team getTeamByName(String name){
		return teams.get(name);
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
		return (Team[]) teams.values().toArray(new Team[teams.size()]);
    }
    
    
    public void clearTeams(){
    		teams.clear();
    	
    }
	
	
}
