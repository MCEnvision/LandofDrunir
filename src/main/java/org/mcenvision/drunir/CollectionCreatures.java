package org.mcenvision.drunir;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

import java.util.*;

public final class CollectionCreatures {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, DrunirCollection.ID);
    public static final Map<String, DeferredHolder<EntityType<?>, EntityType<Creature>>> TYPES = new LinkedHashMap<>();
    public static final List<DeferredItem<DeferredSpawnEggItem>> CREATIVE_EGGS = new ArrayList<>();
    public static final DeferredHolder<EntityType<?>, EntityType<SmallFireball>> FIREBALL = ENTITIES.register("magmafiend_fireball",
            () -> EntityType.Builder.<SmallFireball>of(SmallFireball::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(10)
                    .build(DrunirCollection.ID + ":magmafiend_fireball"));

    private CollectionCreatures() {}

    public static void register(IEventBus bus) {
        for (String name : DrunirCollection.CONTENT.creatures()) {
            float width = name.equals("magmafiend") || name.equals("portal_guardian") ? 1.4F : 0.8F;
            float height = name.equals("magmafiend") || name.equals("portal_guardian") ? 2.8F : 1.5F;
            var type = ENTITIES.register(name, () -> EntityType.Builder.of(Creature::new, MobCategory.CREATURE)
                    .sized(width, height).clientTrackingRange(8).build(DrunirCollection.ID + ":" + name));
            TYPES.put(name, type);
            var egg = DrunirCollection.ITEMS.register(name + "_spawn_egg",
                    () -> new DeferredSpawnEggItem(type, 0x655342, 0xBCB17C, new Item.Properties()));
            if (name.equals("deer")) CREATIVE_EGGS.add(egg);
        }
        ENTITIES.register(bus);
        bus.addListener(CollectionCreatures::attributes);
    }

    private static void attributes(EntityAttributeCreationEvent event) {
        TYPES.values().forEach(type -> event.put(type.get(), Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.2).build()));
    }

    public static final class Creature extends PathfinderMob {
        public Creature(EntityType<? extends Creature> type, Level level) { super(type, level); }

        @Override
        protected void registerGoals() {
            goalSelector.addGoal(0, new FloatGoal(this));
            goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
            goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6));
            goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        }
    }
}
