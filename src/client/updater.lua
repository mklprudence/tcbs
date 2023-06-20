shell.run("rm", "client.lua")
shell.run("pastebin", "get", "BtXMs5wK", "client.lua")
print("[UPDATER] updated client.lua")

shell.run("rm", "json.lua")
shell.run("pastebin", "get", "qgMQerUR", "json.lua")
print("[UPDATER] updated json.lua")

shell.run("rm", "data.lua")
shell.run("pastebin", "get", "VbKD1ucs", "data.lua")
print("[UPDATER] updated data.lua")

print("Starting client")
shell.run("client")