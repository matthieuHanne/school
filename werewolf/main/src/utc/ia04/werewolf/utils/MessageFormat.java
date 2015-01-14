package utc.ia04.werewolf.utils;


import jade.core.AID;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageFormat {
	private String action;
	private HashMap<String, Object> args;

	public MessageFormat() {
		args = new HashMap<String, Object>();
	}

	public MessageFormat(String action, HashMap<String, Object> args) {
		super();
		this.action = action;
		this.args = args;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public HashMap<String, Object> getArgs() {
		return args;
	}
	
	public Object getArg(String key){
		if(args.containsKey(key))
			return args.get(key);
		else
			return null;
	}
	
	public AID getAIDArg(String key){
		return HashMapToAID((LinkedHashMap<String, Object>)args.get(key));
	}
	
	private AID HashMapToAID(LinkedHashMap<String, Object> data){
		AID player = new AID();
		player.setName((String)data.get("name"));
		player.setLocalName((String)data.get("localName"));
		for(String address : (ArrayList<String>)data.get("addressesArray")){
			player.addAddresses(address);
		}
		return player;
	}
	
	public HashMap<String, AID> getPlayers(String key){
		HashMap<String, AID> players = new HashMap<String, AID>();
		HashMap<String, LinkedHashMap<String, Object>> data = (HashMap<String, LinkedHashMap<String, Object>>)this.getArg(key);
		for(String player : data.keySet()){
			players.put(player, HashMapToAID(data.get(player)));
		}
		return players;
	}
	
	public ArrayList<AID> getAIDArgList(String key){
		ArrayList<AID> players = new ArrayList<AID>();
		ArrayList<LinkedHashMap<String, Object>> data = (ArrayList<LinkedHashMap<String, Object>>)args.get(key);
		
		for(LinkedHashMap<String, Object> aid : data){
			players.add(HashMapToAID(aid));
		}
		return players;
	}

	public void setArgs(HashMap<String, Object> args) {
		this.args = args;
	}

	public void addArg(String id, Object arg) {
		args.put(id, arg);
	}

	public String toJSON() {
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(sw, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String s = sw.toString();
		return s;
	}

	public static MessageFormat read(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		MessageFormat p = null;
		try {
			p = mapper.readValue(jsonString, MessageFormat.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
}
