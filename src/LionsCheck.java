import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
*
* LionsCheck
* Copyright (C) 2011 tjnome
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* 
*  @author tjnome
*/

public class LionsCheck extends JavaPlugin {
	public PluginManager pm;

	public void onEnable() {
		registerEvents();
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	public void onDisable() {
		System.out.println("Lionscheck disable");
	}

	public void registerEvents() {
		this.pm = getServer().getPluginManager();
	}
	
	public boolean sendRequestUUID(UUID uuid) {
		String output = "";

		try {
			String urlParameters = "UUID=" + URLEncoder.encode(uuid.toString(), "UTF-8");
			URL url = new URL("www.lions.no/auth");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			System.out.println(urlParameters);

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			output = in.readLine();

			in.close();
			connection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.println(output);
		
		if (output.equalsIgnoreCase("ok")) {
			return false;
		} else {
			return true;
		}
	}
	
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		
		// Check for uuid
		if (sendRequestUUID(event.getUniqueId())) {
			event.disallow(Result.KICK_OTHER, "Din Minecraft brukerer er ikke regristert oss Lions.");
		}
		// Else, all good!
	}
	
	public static boolean isPlayer(final CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		} else {
			return false;
		}
	}
}
