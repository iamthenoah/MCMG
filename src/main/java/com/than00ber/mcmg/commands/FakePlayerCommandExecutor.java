package com.than00ber.mcmg.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FakePlayerCommandExecutor extends PluginCommandExecutor {

    public FakePlayerCommandExecutor(Main instance, World world) {
        super("fakeplayer", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (sender instanceof Player player) {
            String name = args.length > 0 ? args[0] : "UNKNOWN";
            CraftPlayer craftPlayer = (CraftPlayer) player;

            //NMS representation of the MC server
            MinecraftServer server = craftPlayer.getHandle().getServer();
            //NMS representation of the MC world
            ServerLevel level = craftPlayer.getHandle().getLevel();

            GameProfile profile = new GameProfile(UUID.randomUUID(), name);
            ServerPlayer npc = new ServerPlayer(server, level, profile);
            npc.setPos(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

            //set the npc skin
            /* Retrieving skins:
             *  - First thing to understand is that skins have to be "signed" by Mojang. Each skin has two parts: texture and signature
             *  - You can retrieve this data from:
             *    https://sessionserver.mojang.com/session/minecraft/profile/UUID?unsigned=false
             *  - UUID = the uuid of a player without the dashes
             *  - UUID data can only be retrieved every 60 seconds (if same uuid)
             */
            String signature = "ArwoD4sGhthC32Qaq1oSwNOWPciJN54mLj+Tq0tZBUMCaw7Gnpj6W9HJhLrax6gVs8X3O5cWUrgLbAIF8uelb5jLdUpm9ZFsAFUo/MtE3oqCXBjoXw8+Wn8y8WR1UAXwv0ts+C6OSyOfLGk0tR7Jmkac6G7bUKYOAMFtCGcppdmoxvhALHPkcsPmdlE8SsHhOVDBp+SE9SBA0V5Z2YDTua34bLdCh4jHibb9x6D8yLxos5ksqcUzsLW9HZ6gqt29GqRD3+M2q1VyXyOjQCR1MD/5A0WfFAFBtExWPRn4V8Fl8a6+814a84H6apaoIN0e6rZHC9ArLEbfSStS54YbjFZ5jfUHx4jkyg0n16B14Z7KLVRmWJjUPtICWaW7zlOOzzq+ZkV1fckVmXEA0Ri349DnWMSGU44nkgPsjD5PL9PLdDqhWqXQGL9f3C+XmUC+5WWdE1cA2W+ZrTN0mZajlkmcwYL0priAZZfzubhVV6PqWAaM9phgaoK7s5oQc6ruaXObauGZvxZ2p+LDx8A+AKnpxSPvjE+fVoOZUAvzVIhwXkFo8Y7+lJi29GjNS8f+fZctPivnABnK2oHXVapvdWlOfpTg/Y8cgc+GHhsvY82f9p7tyFAjV59Ps2G3TDjNbxm7iRaNs4MBUf2e8+mQFt/MbbblCfDBMUOprV0vjks=";
            String texture = "ewogICJ0aW1lc3RhbXAiIDogMTYzMzI2Mzg5NjIyNSwKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ==";
            npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));

            //Send the packets to artificially spawn this entity, only the client we are sending the packet to will know of it's existence
            ServerGamePacketListenerImpl ps = craftPlayer.getHandle().connection;
            //Player Info Packet
            //Sent by the server to update the user list (<tab> in the client).
            ps.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            //Spawn Player packet
            ps.send(new ClientboundAddPlayerPacket(npc));

            //Give the player items and armor
//            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
//            ps.send(new ClientboundSetEquipmentPacket(npc.getBukkitEntity().getEntityId(), List.of(Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(item)))));
//            ps.send(new ClientboundSetEquipmentPacket(npc.getBukkitEntity().getEntityId(), List.of(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.GOLDEN_HELMET, 1))))));

            return ActionResult.success("Spawning " + name);
        }
        return PluginCommandExecutor.NOT_A_PLAYER;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return List.of();
    }

//    public void createNPC(Player player, String npcName) {
//        Location location = player.getLocation();
//        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
//        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
//        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "§a§l" + npcName);
//
//        EntityPlayer npc = new EntityPlayer(nmsServer, serverPlayer.getServer(), gameProfile);
//        Player npcPlayer = npc.getBukkitEntity().getPlayer();
//        npcPlayer.setPlayerListName(npcName);
//
//        npc.setPos(location.getX(), location.getY(), location.getZ());
//
//        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
//        connection.send(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
//        connection.send(new PacketPlayOutNamedEntitySpawn(npc));
//        connection.send(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc));
//    }
}
