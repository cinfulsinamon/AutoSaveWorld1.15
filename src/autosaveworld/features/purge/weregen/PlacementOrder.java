package autosaveworld.features.purge.weregen;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

public class PlacementOrder {
    private static final Set<Material> shouldPlaceLast = new HashSet<Material>();
    static {
        shouldPlaceLast.add(Material.SAPLING);
        shouldPlaceLast.add(Material.BED);
        shouldPlaceLast.add(Material.POWERED_RAIL);
        shouldPlaceLast.add(Material.DETECTOR_RAIL);
        shouldPlaceLast.add(Material.LONG_GRASS);
        shouldPlaceLast.add(Material.DEAD_BUSH);
        shouldPlaceLast.add(Material.PISTON_EXTENSION);
        shouldPlaceLast.add(Material.YELLOW_FLOWER);
        //shouldPlaceLast.add(Material.RED_FLOWER);
        shouldPlaceLast.add(Material.BROWN_MUSHROOM);
        shouldPlaceLast.add(Material.RED_MUSHROOM);
        shouldPlaceLast.add(Material.TORCH);
        shouldPlaceLast.add(Material.FIRE);
        shouldPlaceLast.add(Material.REDSTONE_WIRE);
        shouldPlaceLast.add(Material.CROPS);
        shouldPlaceLast.add(Material.LADDER);
        shouldPlaceLast.add(Material.RAILS);
        shouldPlaceLast.add(Material.ACTIVATOR_RAIL);
        shouldPlaceLast.add(Material.DETECTOR_RAIL);
        shouldPlaceLast.add(Material.POWERED_RAIL);
        shouldPlaceLast.add(Material.LEVER);
        shouldPlaceLast.add(Material.STONE_PLATE);
        shouldPlaceLast.add(Material.WOOD_PLATE);
        shouldPlaceLast.add(Material.REDSTONE_TORCH_OFF);
        shouldPlaceLast.add(Material.REDSTONE_TORCH_ON);
        shouldPlaceLast.add(Material.STONE_BUTTON);
        shouldPlaceLast.add(Material.SNOW);
        shouldPlaceLast.add(Material.PORTAL);
        //shouldPlaceLast.add(Material.REDSTONE_REPEATER_OFF);
        //shouldPlaceLast.add(Material.REDSTONE_REPEATER_ON);
        shouldPlaceLast.add(Material.TRAP_DOOR);
        shouldPlaceLast.add(Material.VINE);
        shouldPlaceLast.add(Material.WATER_LILY);
        shouldPlaceLast.add(Material.NETHER_WARTS);
        shouldPlaceLast.add(Material.PISTON_BASE);
        shouldPlaceLast.add(Material.PISTON_STICKY_BASE);
        shouldPlaceLast.add(Material.PISTON_EXTENSION);
        shouldPlaceLast.add(Material.PISTON_MOVING_PIECE);
        shouldPlaceLast.add(Material.COCOA);
        shouldPlaceLast.add(Material.TRIPWIRE_HOOK);
        shouldPlaceLast.add(Material.TRIPWIRE);
        shouldPlaceLast.add(Material.FLOWER_POT);
        shouldPlaceLast.add(Material.CARROT);
        shouldPlaceLast.add(Material.POTATO);
        shouldPlaceLast.add(Material.WOOD_BUTTON);
        shouldPlaceLast.add(Material.STONE_BUTTON);
        shouldPlaceLast.add(Material.ANVIL); // becomes relevant with asynchronous placement
        //shouldPlaceLast.add(Material.PRESSURE_PLATE_LIGHT);
        //shouldPlaceLast.add(Material.PRESSURE_PLATE_HEAVY);
        shouldPlaceLast.add(Material.REDSTONE_COMPARATOR_OFF);
        shouldPlaceLast.add(Material.REDSTONE_COMPARATOR_ON);
        shouldPlaceLast.add(Material.IRON_TRAPDOOR);
        shouldPlaceLast.add(Material.CARPET);
        shouldPlaceLast.add(Material.DOUBLE_PLANT);
        shouldPlaceLast.add(Material.DAYLIGHT_DETECTOR_INVERTED);
    }
    
    public static boolean shouldPlaceLast(Material block) {
        return shouldPlaceLast.contains(block);
    }

    private static final Set<Material> shouldPlaceFinal = new HashSet<Material>();
    static {
        shouldPlaceFinal.add(Material.SIGN_POST);
        shouldPlaceFinal.add(Material.WOODEN_DOOR);
        shouldPlaceFinal.add(Material.ACACIA_DOOR);
        shouldPlaceFinal.add(Material.BIRCH_DOOR);
        shouldPlaceFinal.add(Material.JUNGLE_DOOR);
        shouldPlaceFinal.add(Material.DARK_OAK_DOOR);
        shouldPlaceFinal.add(Material.SPRUCE_DOOR);
        shouldPlaceFinal.add(Material.WALL_SIGN);
        shouldPlaceFinal.add(Material.IRON_DOOR);
        shouldPlaceFinal.add(Material.CACTUS);
        shouldPlaceFinal.add(Material.SUGAR_CANE);
        shouldPlaceFinal.add(Material.CAKE_BLOCK);
        shouldPlaceFinal.add(Material.PISTON_EXTENSION);
        shouldPlaceFinal.add(Material.PISTON_MOVING_PIECE);
        shouldPlaceFinal.add(Material.STANDING_BANNER);
        shouldPlaceFinal.add(Material.WALL_BANNER);
    }

    public static boolean shouldPlaceFinal(Material block) {
        return shouldPlaceFinal.contains(block);
    }
}