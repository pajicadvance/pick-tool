package me.pajic.picktool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

//? fabric
import net.fabricmc.loader.api.FabricLoader;
//? neoforge
//import net.neoforged.fml.loading.FMLPaths;

public class PickToolConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE_PATH = getConfigFilePath();
	private static final Logger LOGGER = LoggerFactory.getLogger(PickToolConfig.class);
	public static Config CONFIG;

	public static void loadConfig() {
		readConfig();
		saveConfig();
	}

	private static void readConfig() {
		try (FileReader reader = new FileReader(FILE_PATH.toFile())) {
			CONFIG = GSON.fromJson(reader, Config.class);
		} catch (FileNotFoundException | JsonSyntaxException e) {
			initializeConfig();
		} catch (IOException e) {
			LOGGER.error("Failed to read mod config", e);
		}
	}

	private static void saveConfig() {
		try (FileWriter writer = new FileWriter(FILE_PATH.toFile())) {
			GSON.toJson(CONFIG, writer);
		} catch (IOException e) {
			LOGGER.error("Failed to save mod config", e);
		}
	}

	private static void initializeConfig() {
		try (FileWriter writer = new FileWriter(FILE_PATH.toFile())) {
			CONFIG = new Config();
			GSON.toJson(CONFIG, writer);
		} catch (IOException e) {
			LOGGER.error("Failed to initialize mod config", e);
		}
	}

	public static class Config {

		List<String> blacklist;

		public Config() {
			blacklist = List.of();
		}

		public List<String> blacklist() {
			return blacklist;
		}
	}

	private static Path getConfigFilePath() {
		//? fabric
		return FabricLoader.getInstance().getConfigDir().resolve("picktool.json");
		//? neoforge
		//return FMLPaths.CONFIGDIR.get().resolve("picktool.json");
	}
}
