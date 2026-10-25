package org.mcenvision.drunir;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.*;
import org.mcenvision.drunir.worldgen.IslandFeatures;
import org.mcenvision.drunir.boulder.BoulderPieceBlock;
import org.mcenvision.drunir.boulder.BoulderPieceItem;
import org.mcenvision.drunir.boulder.CollectionBoulders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

@Mod(DrunirCollection.ID)
public final class DrunirCollection {
    public static final String ID = "landofdrunir";
    public static final Catalog CONTENT = readCatalog();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final DeferredRegister<ArmorMaterial> ARMOR = DeferredRegister.create(Registries.ARMOR_MATERIAL, ID);
    public static final Map<String, DeferredBlock<Block>> BLOCK_ENTRIES = new LinkedHashMap<>();
    public static final Map<String, DeferredItem<Item>> ITEM_ENTRIES = new LinkedHashMap<>();
    public static final List<DeferredItem<Item>> GALLERY = new ArrayList<>();
    private static final Map<String, DeferredHolder<ArmorMaterial, ArmorMaterial>> MATERIALS = new LinkedHashMap<>();

    public DrunirCollection(IEventBus bus) {
        registerArmor();
        registerBlocks();
        registerItems();
        CollectionBoulders.registerItems();
        registerGallery();
        CollectionCreatures.register(bus);
        IslandFeatures.register(bus);
        org.mcenvision.drunir.worldgen.IslandParticles.register(bus);
        registerTabs();
        ARMOR.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
    }

    private static void registerArmor() {
        for (String material : List.of("drunite", "galeon", "plasmite", "extra")) {
            boolean accessory = material.equals("extra");
            MATERIALS.put(material, ARMOR.register(material, () -> new ArmorMaterial(
                    accessory ? Map.of(ArmorItem.Type.HELMET, 0, ArmorItem.Type.CHESTPLATE, 0,
                            ArmorItem.Type.LEGGINGS, 0, ArmorItem.Type.BOOTS, 0, ArmorItem.Type.BODY, 0)
                            : Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
                            ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3, ArmorItem.Type.BODY, 11),
                    accessory ? 0 : 10, accessory ? SoundEvents.ARMOR_EQUIP_LEATHER : SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> accessory ? Ingredient.EMPTY : Ingredient.of(ITEM_ENTRIES.get(material).get()),
                    List.of(new ArmorMaterial.Layer(location(material))), accessory ? 0 : 2.0F, 0.0F)));
        }
    }

    private static void registerBlocks() {
        for (BlockEntry entry : CONTENT.blocks()) {
            DeferredBlock<Block> block = BLOCKS.register(entry.name, () -> createBlock(entry));
            BLOCK_ENTRIES.put(entry.name, block);
            if (entry.kind.equals("boulder_piece")) {
                ITEMS.register(entry.name, () -> new BoulderPieceItem(block.get(), 4, 0, true, new Item.Properties()));
            } else {
                ITEMS.registerSimpleBlockItem(block);
            }
        }
    }

    private static void registerItems() {
        for (ItemEntry entry : CONTENT.items()) {
            ITEM_ENTRIES.put(entry.name, ITEMS.register(entry.name, () -> createItem(entry)));
        }
    }

    private static void registerGallery() {
        for (String texture : CONTENT.textures()) {
            GALLERY.add(ITEMS.register("texture/" + texture, () -> new Item(new Item.Properties())));
        }
    }

    private static void registerTabs() {
        tab("blocks", () -> BLOCK_ENTRIES.get("blueschist").get().asItem(),
                () -> BLOCK_ENTRIES.values().stream().map(Supplier::get).map(Block::asItem).toList());
        tab("boulders", () -> CollectionBoulders.PIECES.get(16).get(),
                () -> CollectionBoulders.PIECES.stream().map(Supplier::get).map(item -> (Item) item).toList());
        tab("items", () -> ITEM_ENTRIES.get("drunite").get(),
                () -> ITEM_ENTRIES.values().stream().map(Supplier::get).toList());
        tab("creatures", () -> CollectionCreatures.CREATIVE_EGGS.getFirst().get(),
                () -> CollectionCreatures.CREATIVE_EGGS.stream().map(Supplier::get).map(item -> (Item) item).toList());
        tab("textures", () -> GALLERY.getFirst().get(), () -> GALLERY.stream().map(Supplier::get).toList());
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    private static Block createBlock(BlockEntry entry) {
        BlockBehaviour.Properties stone = BlockBehaviour.Properties.ofFullCopy(Blocks.STONE);
        return switch (entry.kind) {
            case "plant" -> new DecorativePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).noCollission().noOcclusion()
                    .lightLevel(state -> entry.name.equals("sun_holly") ? 4 : 0));
            case "slab" -> new SlabBlock(stone);
            case "stairs" -> new StairBlock(Blocks.STONE.defaultBlockState(), stone);
            case "boulder_piece" -> new BoulderPieceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));
            case "boulder" -> new BoulderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion());
            case "shore" -> new ShoreSandBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND));
            case "leaves" -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));
            case "log" -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
            case "hollow" -> new HollowLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).noOcclusion());
            case "door" -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR));
            case "trapdoor" -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR));
            default -> new Block(entry.name.contains("planks")
                    ? BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    : entry.name.equals("lightling_block") ? stone.lightLevel(state -> 12) : stone);
        };
    }

    private static Item createItem(ItemEntry entry) {
        Item.Properties properties = new Item.Properties();
        if (entry.name.equals("goblin_drip")) {
            return new ArmorItem(MATERIALS.get("extra"), ArmorItem.Type.CHESTPLATE, properties);
        }
        Tier tier = toolTier(entry.name);
        return switch (entry.kind) {
            case "sword", "lance" -> new SwordItem(tier, properties.attributes(SwordItem.createAttributes(tier, 3, -2.4F)));
            case "pickaxe" -> new PickaxeItem(tier, properties.attributes(PickaxeItem.createAttributes(tier, 1, -2.8F)));
            case "axe" -> new AxeItem(tier, properties.attributes(AxeItem.createAttributes(tier, 6, -3.1F)));
            case "shovel" -> new ShovelItem(tier, properties.attributes(ShovelItem.createAttributes(tier, 1.5F, -3.0F)));
            case "hoe" -> new HoeItem(tier, properties.attributes(HoeItem.createAttributes(tier, -2, -1.0F)));
            case "armor" -> {
                String material = entry.name.substring(0, entry.name.lastIndexOf('_'));
                ArmorItem.Type type = ArmorItem.Type.valueOf(entry.name.substring(entry.name.lastIndexOf('_') + 1).toUpperCase(Locale.ROOT));
                yield new ArmorItem(MATERIALS.get(material), type, properties.durability(type.getDurability(33)));
            }
            case "food" -> new Item(properties.food(new FoodProperties.Builder()
                    .nutrition(entry.name.startsWith("cooked") ? 8 : 3)
                    .saturationModifier(entry.name.startsWith("cooked") ? 0.8F : 0.3F).build()));
            default -> new Item(properties);
        };
    }

    private static Tier toolTier(String name) {
        String material = name.substring(0, name.indexOf('_') < 0 ? name.length() : name.indexOf('_'));
        return switch (material) {
            case "wooden" -> Tiers.WOOD;
            case "stone" -> Tiers.STONE;
            case "gold", "golden" -> Tiers.GOLD;
            case "netherite" -> Tiers.NETHERITE;
            case "diamond", "drunite", "plasmite" -> Tiers.DIAMOND;
            default -> Tiers.IRON;
        };
    }

    private static void tab(String name, Supplier<Item> icon, Supplier<List<Item>> contents) {
        TABS.register(name, () -> CreativeModeTab.builder()
                .title(Component.translatable("tab.landofdrunir." + name))
                .icon(() -> icon.get().getDefaultInstance())
                .displayItems((parameters, output) -> contents.get().forEach(output::accept)).build());
    }

    private static Catalog readCatalog() {
        try (var stream = DrunirCollection.class.getResourceAsStream("/assets/landofdrunir/content.json")) {
            if (stream == null) throw new IllegalStateException("Drunir content catalog is missing.");
            return new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Catalog.class);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read the Drunir content catalog.", exception);
        }
    }

    public record Catalog(List<BlockEntry> blocks, List<ItemEntry> items, List<String> creatures,
                          List<String> textures,
                          @SerializedName("boulder_shape") List<List<Double>> boulderShape) {}
    public record BlockEntry(String name, String kind) {}
    public record ItemEntry(String name, String kind) {}
}
