DeathTpPlusRenewed
===========

DeathTpPlusRenewed announces a death with a random message based on the death cause and allows you to teleport to the point of death.
It can also create a TombStone at the place were you died to let you save your inventory (aka DeadMansChest).
You can also create a tomb sign which can be used as respawn point and as a place of information of how often you died
and what your last death cause was.

More information can be found here: http://dev.bukkit.org/server-mods/deathtpplus/


java.lang.Error: Unresolved compilation problems: 
	WALL_SIGN cannot be resolved or is not a field
	The method setData(byte) is undefined for the type Block
	The method setData(byte) is undefined for the type Block
	The method setData(byte) is undefined for the type Block
	The method setData(byte) is undefined for the type Block

	at org.spigotmc.DeathTpPlusRenewed.tomb.TombStoneHelper.protectWithLockette(TombStoneHelper.java:102) ~[?:?]
	at org.spigotmc.DeathTpPlusRenewed.death.events.handlers.EntityDeathHandler.CreateTombStone(EntityDeathHandler.java:450) ~[?:?]
	at org.spigotmc.DeathTpPlusRenewed.death.events.handlers.EntityDeathHandler.oEDeaGeneralDeath(EntityDeathHandler.java:201) ~[?:?]
	at org.spigotmc.DeathTpPlusRenewed.death.events.listeners.EntityListener.onEntityDeath(EntityListener.java:59) ~[?:?]
	at com.destroystokyo.paper.event.executor.asm.generated.GeneratedEventExecutor1741.execute(Unknown Source) ~[?:?]
	at org.bukkit.plugin.EventExecutor.lambda$create$1(EventExecutor.java:69) ~[patched_1.14.4.jar:git-Paper-194]
	at co.aikar.timings.TimedEventExecutor.execute(TimedEventExecutor.java:80) ~[patched_1.14.4.jar:git-Paper-194]
	at org.bukkit.plugin.RegisteredListener.callEvent(RegisteredListener.java:70) ~[patched_1.14.4.jar:git-Paper-194]
	at org.bukkit.plugin.SimplePluginManager.callEvent(SimplePluginManager.java:545) ~[patched_1.14.4.jar:git-Paper-194]
	at org.bukkit.craftbukkit.v1_14_R1.event.CraftEventFactory.callPlayerDeathEvent(CraftEventFactory.java:775) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityPlayer.die(EntityPlayer.java:586) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityLiving.damageEntity(EntityLiving.java:1198) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityHuman.damageEntity(EntityHuman.java:786) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityPlayer.damageEntity(EntityPlayer.java:757) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityInsentient.C(EntityInsentient.java:1303) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityZombie.C(EntityZombie.java:325) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack.a(SourceFile:139) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack.e(SourceFile:131) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.PathfinderGoalZombieAttack.e(SourceFile:28) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.PathfinderGoalWrapped.e(SourceFile:55) ~[patched_1.14.4.jar:git-Paper-194]
	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184) ~[?:1.8.0_212]
	at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175) ~[?:1.8.0_212]
	at java.util.Iterator.forEachRemaining(Iterator.java:116) ~[?:1.8.0_212]
	at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801) ~[?:1.8.0_212]
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482) ~[?:1.8.0_212]
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472) ~[?:1.8.0_212]
	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151) ~[?:1.8.0_212]
	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174) ~[?:1.8.0_212]
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[?:1.8.0_212]
	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418) ~[?:1.8.0_212]
	at net.minecraft.server.v1_14_R1.PathfinderGoalSelector.doTick(SourceFile:80) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityInsentient.doTick(EntityInsentient.java:662) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityLiving.movementTick(EntityLiving.java:2532) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityInsentient.movementTick(EntityInsentient.java:504) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityMonster.movementTick(EntityMonster.java:23) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityZombie.movementTick(EntityZombie.java:209) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityLiving.tick(EntityLiving.java:2321) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityInsentient.tick(EntityInsentient.java:275) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityMonster.tick(EntityMonster.java:37) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.EntityZombie.tick(EntityZombie.java:180) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.WorldServer.entityJoinedWorld(WorldServer.java:665) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.World.a(World.java:936) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.WorldServer.doTick(WorldServer.java:437) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.MinecraftServer.b(MinecraftServer.java:1208) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.DedicatedServer.b(DedicatedServer.java:417) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.MinecraftServer.a(MinecraftServer.java:1075) ~[patched_1.14.4.jar:git-Paper-194]
	at net.minecraft.server.v1_14_R1.MinecraftServer.run(MinecraftServer.java:919) ~[patched_1.14.4.jar:git-Paper-194]
	at java.lang.Thread.run(Thread.java:748) [?:1.8.0_212]