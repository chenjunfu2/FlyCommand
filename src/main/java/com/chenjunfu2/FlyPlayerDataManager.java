package com.chenjunfu2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.UUID;

import static com.chenjunfu2.Flycommand.server;

public class FlyPlayerDataManager
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static Path dataFile;
	private static HashSet<UUID> flyPlayerList = new HashSet<>();
	private static boolean dirty = false;
	
	public static void initialize(MinecraftServer server)
	{
		dataFile = server.getSavePath(WorldSavePath.ROOT).resolve("fly_players.json");
		loadFlyPlayers();
	}
	
	public static void saveData()
	{
		if (dirty)
		{
			saveFlyPlayers();
			dirty = false;
		}
	}
	
	public static boolean addPlayer(UUID playerId)
	{
		dirty = true;
		return flyPlayerList.add(playerId);
	}
	
	public static boolean removePlayer(UUID playerId)
	{
		dirty = true;
		return flyPlayerList.remove(playerId);
	}
	
	public static boolean containsPlayer(UUID playerId)
	{
		return flyPlayerList.contains(playerId);
	}
	
	private static void loadFlyPlayers()
	{
		if (Files.notExists(dataFile))
		{
			return;
		}
		
		try (Reader reader = Files.newBufferedReader(dataFile))
		{
			flyPlayerList = GSON.fromJson(reader, new TypeToken<HashSet<UUID>>(){}.getType());
			server.sendMessage(Text.literal("[FlyCommand] Loaded " + flyPlayerList.size() + " fly players from file"));
		}
		catch (IOException e)
		{
			server.sendMessage(Text.literal("[FlyCommand] Failed to load fly players data: " + e.getMessage()));
		}
	}
	
	private static void saveFlyPlayers() {
		if (dataFile == null) return;
		
		try {
			Files.createDirectories(dataFile.getParent());
			
			try (Writer writer = Files.newBufferedWriter(dataFile))
			{
				GSON.toJson(flyPlayerList, writer);
				server.sendMessage(Text.literal("[FlyCommand] Saved " + flyPlayerList.size() + " fly players to file"));
			}
		}
		catch (IOException e)
		{
			server.sendMessage(Text.literal("[FlyCommand] Failed to save fly players data: " + e.getMessage()));
		}
	}
}