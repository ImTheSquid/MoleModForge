package dev.jackhogan.molemodforge;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MoleModForge.MODID)
public class MoleModForge {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "molemodforge";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "MoleModForge" namespace
    // public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "MoleModForge" namespace
    // public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "MoleModForge:example_block", combining the namespace and path
    // public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // Creates a new BlockItem with the id "MoleModForge:example_block", combining the namespace and path
    // public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public MoleModForge() {
        // IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        // modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        // BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        // ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

//    private void commonSetup(final FMLCommonSetupEvent event) {
//        // Some common setup code
//        LOGGER.info("HELLO FROM COMMON SETUP");
//        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//    }

    private static final int SAFE_TICKS = 600;

    private final HashMap<UUID, Integer> safeRegistry = new HashMap<>();
    private final HashMap<UUID, OffenseTracker> offenses = new HashMap<>();

    // You can use SubscribeEvent and let the Event Bus discover methods to call
//    @SubscribeEvent
//    public void onServerStarting(ServerStartingEvent event) {
//        // Do something when the server starts
//        LOGGER.info("HELLO from server starting");
//    }

//    @SubscribeEvent
//    public void onLevelTick(TickEvent.LevelTickEvent event) {
//        // Tick players
//        safeRegistry.replaceAll((uuid, integer) -> integer + 1);
//        safeRegistry.entrySet().removeIf(ticks -> ticks.getValue() > SAFE_TICKS);
//
//        for (Player player: event.level.players()) {
//            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
//            if (maxHealth != null) {
//                maxHealth.setBaseValue(16);
//            }
//
//            Integer count = safeRegistry.get(player.getUUID());
//            if (count != null && count == 2) {
//                player.displayClientMessage(Component.literal(ChatFormatting.RED + "WARNING! You are in the burning light of the sun! You have 15 seconds to return to the safety of the dark."), true);
//                // player.sendSystemMessage();
//            }
//
//            if (player.isDeadOrDying()) {
//                // Reset offenses if the player dies
//                if (offenses.containsKey(player.getUUID())) {
//                    offenses.put(player.getUUID(), new OffenseTracker());
//                }
//                safeRegistry.put(player.getUUID(), 0);
//                continue;
//            }
//
//            if (event.level.canSeeSky(player.blockPosition()) && event.level.isDay() && player.getLevel().dimension() == Level.OVERWORLD && !safeRegistry.containsKey(player.getUUID()) && !(player.isCreative() || player.isSpectator())) {
//                if (!offenses.containsKey(player.getUUID())) {
//                    offenses.put(player.getUUID(), new OffenseTracker());
//                }
//
//                OffenseTracker tracker = offenses.get(player.getUUID());
//                tracker.startOffense(player);
//
//                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, tracker.getOffenses()));
//                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 2 * tracker.getOffenses()));
//                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 3 * tracker.getOffenses()));
//            } else if (offenses.containsKey(player.getUUID())) {
//                offenses.get(player.getUUID()).endOffense();
//            }
//        }
//    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        // Tick players
        safeRegistry.replaceAll((uuid, integer) -> integer + 1);
        safeRegistry.entrySet().removeIf(ticks -> ticks.getValue() > SAFE_TICKS);

        for (Player player: event.getServer().getPlayerList().getPlayers()) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(16);
            }

            Integer count = safeRegistry.get(player.getUUID());
            if (count != null && count == 2) {
                player.displayClientMessage(Component.literal(ChatFormatting.RED + "WARNING! You are in the burning light of the sun! You have 15 seconds to return to the safety of the dark."), true);
                // player.sendSystemMessage();
            }

            if (player.isDeadOrDying()) {
                // Reset offenses if the player dies
                if (offenses.containsKey(player.getUUID())) {
                    offenses.put(player.getUUID(), new OffenseTracker());
                }
                safeRegistry.put(player.getUUID(), 0);
                continue;
            }

            if (player.getLevel().canSeeSkyFromBelowWater(player.blockPosition()) && player.getLevel().isDay() && player.getLevel().dimension() == Level.OVERWORLD && !safeRegistry.containsKey(player.getUUID()) && !(player.isCreative() || player.isSpectator())) {
                if (!offenses.containsKey(player.getUUID())) {
                    offenses.put(player.getUUID(), new OffenseTracker());
                }

                OffenseTracker tracker = offenses.get(player.getUUID());
                tracker.startOffense(player);

                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, tracker.getOffenses()));
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 2 * tracker.getOffenses()));
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 3 * tracker.getOffenses()));
            } else if (offenses.containsKey(player.getUUID())) {
                offenses.get(player.getUUID()).endOffense();
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
//    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//    public static class ClientModEvents {
//
//        @SubscribeEvent
//        public static void onClientSetup(FMLClientSetupEvent event)
//        {
//            // Some client setup code
//            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
//        }
//    }
}
