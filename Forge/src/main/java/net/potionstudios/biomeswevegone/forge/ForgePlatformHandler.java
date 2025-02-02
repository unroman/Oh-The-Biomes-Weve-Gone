package net.potionstudios.biomeswevegone.forge;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import net.potionstudios.biomeswevegone.PlatformHandler;
import net.potionstudios.biomeswevegone.world.level.block.BWGBlocks;
import net.potionstudios.biomeswevegone.world.level.block.custom.BWGFarmLandBlock;
import net.potionstudios.biomeswevegone.world.level.block.wood.BWGWood;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@AutoService(PlatformHandler.class)
public class ForgePlatformHandler implements PlatformHandler {
	@Override
	public String getPlatformName() {
		return "Forge";
	}

	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BiomesWeveGone.MOD_ID);

	@Override
	public <E extends Entity> Supplier<EntityType<E>> registerEntity(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height) {
		return ENTITY_TYPES.register(id, () -> EntityType.Builder.of(factory, category).sized(width, height).build(BiomesWeveGone.id(id).toString()));
	}

	@Override
	public <E extends Entity> Supplier<EntityType<E>> registerEntity(String id, EntityType.EntityFactory<E> factory, MobCategory category, float width, float height, int trackingRange) {
		return ENTITY_TYPES.register(id, () -> EntityType.Builder.of(factory, category).sized(width, height).clientTrackingRange(trackingRange).build(BiomesWeveGone.id(id).toString()));
	}

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BiomesWeveGone.MOD_ID);

	@Override
	public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String key, Supplier<BlockEntityType.Builder<T>> builder) {
		return BLOCK_ENTITIES.register(key, () -> builder.get().build(Util.fetchChoiceType(References.BLOCK_ENTITY, key)));
	}

	@Override
	public Supplier<SpawnEggItem> createSpawnEgg(Supplier<EntityType<? extends Mob>> entity, int backgroundColor, int highlightColor) {
		return () -> new ForgeSpawnEggItem(entity, backgroundColor, highlightColor, new Item.Properties());
	}

	@Override
	public Supplier<FlowerPotBlock> createPottedBlock(Supplier<? extends Block> block) {
		return () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, block, BlockBehaviour.Properties.copy(Blocks.FLOWER_POT));
	}

	@Override
	public Supplier<MobBucketItem> createMobBucket(Supplier<EntityType<? extends Mob>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> sound) {
		return () -> new MobBucketItem(entity, fluid, sound, new Item.Properties().stacksTo(1));
	}

	@Override
	public Supplier<RecordItem> createRecordItem(int comparatorValue, Supplier<SoundEvent> sound, int lengthInSeconds) {
		return () -> new RecordItem(comparatorValue, sound, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), lengthInSeconds * 20);
	}

	@Override
	public Supplier<BWGFarmLandBlock> bwgFarmLandBlock(Supplier<Block> dirt) {
		return () -> new net.potionstudios.biomeswevegone.forge.world.level.block.BWGFarmLandBlock(dirt);
	}

	@Override
	public WoodType createWoodType(String id, @NotNull BlockSetType setType) {
		return WoodType.register(new WoodType(BiomesWeveGone.MOD_ID + ":" + id, setType));
	}

	private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BiomesWeveGone.MOD_ID);

	@Override
	public Supplier<SimpleParticleType> registerCreateParticle(String name) {
		return PARTICLES.register(name, () -> new SimpleParticleType(false));
	}

	@SafeVarargs
	@Override
	public final Supplier<CreativeModeTab> createCreativeTab(String name, Supplier<ItemStack> icon, ArrayList<Supplier<? extends Item>>... items) {
		return register(BuiltInRegistries.CREATIVE_MODE_TAB, name, () -> CreativeModeTab.builder()
				.title(Component.translatable("itemGroup." + BiomesWeveGone.MOD_ID + "." + name))
				.icon(icon)
				.displayItems((context, entries) -> {
					for (ArrayList<Supplier<? extends Item>> item : items)
						item.forEach((item1) -> entries.accept(item1.get()));
				})
				.withSearchBar()
				.build());
	}

	private static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = DeferredRegister.create(Registries.BLOCK_PREDICATE_TYPE, BiomesWeveGone.MOD_ID);

	@Override
	public <P extends BlockPredicate> Supplier<BlockPredicateType<P>> registerBlockPredicate(String id, Supplier<Codec<P>> codec) {
		return BLOCK_PREDICATE_TYPE.register(id, () -> codec::get);
	}

	public static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

	@Override
	public <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value) {
		return CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), BiomesWeveGone.MOD_ID)).register(name, value);
	}

	@Override
	public <T> Supplier<Holder.Reference<T>> registerForHolder(Registry<T> registry, String name, Supplier<T> value) {
		RegistryObject<T> registryObject = CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), BiomesWeveGone.MOD_ID)).register(name, value);
		return () -> (Holder.Reference<T>) registryObject.getHolder().get();
	}

	public static void registerPottedPlants() {
		BWGBlocks.BLOCKS.forEach(entry -> {
			if (entry.get() instanceof FlowerPotBlock)
				((FlowerPotBlock) Blocks.FLOWER_POT)
						.addPlant(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(((FlowerPotBlock) entry.get()).getContent())), entry);
		});
		BWGWood.WOOD.forEach(entry -> {
			if (entry.get() instanceof FlowerPotBlock)
				((FlowerPotBlock) Blocks.FLOWER_POT)
						.addPlant(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(((FlowerPotBlock) entry.get()).getContent())), entry);
		});
	}

	public static void register(IEventBus bus) {
		ENTITY_TYPES.register(bus);
		PARTICLES.register(bus);
		CACHED.values().forEach(deferredRegister -> deferredRegister.register(bus));
		BLOCK_ENTITIES.register(bus);
		BLOCK_PREDICATE_TYPE.register(bus);
	}
}
