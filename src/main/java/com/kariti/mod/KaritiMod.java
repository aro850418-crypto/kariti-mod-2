package com.kariti.mod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(KaritiMod.MOD_ID)
public class KaritiMod {

    public static final String MOD_ID = "kariti_mod";

    // Регистрация типа сущности Карити
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<KaritiEntity>> KARITI =
            ENTITY_TYPES.register("kariti", () ->
                    EntityType.Builder.<KaritiEntity>of(KaritiEntity::new, MobCategory.MISC)
                            .sized(1.4F, 2.7F)
                            .clientTrackingRange(10)
                            .build("kariti")
            );

    public KaritiMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // =====================================================================
    // СУЩНОСТЬ КАРИТИ
    // =====================================================================
    public static class KaritiEntity extends IronGolem {

        public KaritiEntity(EntityType<? extends IronGolem> type, Level level) {
            super(type, level);
            this.setMaxUpStep(1.0F);
        }

        @Override
        protected void registerGoals() {
            // Базовые цели поведения
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
            this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
            this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
            this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

            // Атакует любого моба с "verity" в названии типа
            this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                    this, Mob.class, true,
                    (entity) -> entity.getType().getDescriptionId().toLowerCase().contains("verity")
            ));
        }

        // Карити не берет урон от игрока — он на нашей стороне
        @Override
        public boolean canAttack(net.minecraft.world.entity.LivingEntity target) {
            if (target instanceof Player) return false;
            return super.canAttack(target);
        }
    }

    // =====================================================================
    // СОБЫТИЯ: спавн и диалог при входе игрока
    // =====================================================================
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onEntityJoin(EntityJoinLevelEvent event) {
            // Срабатывает, когда игрок заходит на сервер
            if (event.getEntity() instanceof Player player && !event.getLevel().isClientSide()) {
                Level level = event.getLevel();

                // Спавним Карити рядом с игроком
                KaritiEntity kariti = KARITI.get().create(level);
                if (kariti != null) {
                    kariti.moveTo(
                            player.getX() + 2,
                            player.getY(),
                            player.getZ() + 2,
                            player.getYRot(),
                            0
                    );
                    kariti.setPersistenceRequired(); // не деспавнится
                    level.addFreshEntity(kariti);
                }

                // Оранжевое предупреждение в чат
                player.sendSystemMessage(Component.literal(
                        "§6[Карити]:§f Привет! Не верь Верити — он опасен! Нам нужно избавиться от него вместе..."
                ));
            }
        }
    }
}
