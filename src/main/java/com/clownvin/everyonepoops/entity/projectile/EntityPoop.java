package com.clownvin.everyonepoops.entity.projectile;

import com.clownvin.everyonepoops.EveryonePoops;
import com.clownvin.everyonepoops.config.PoopConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityPoop extends EntityThrowable {
    public static EntityType<EntityPoop> entityPoopEntityType;

    public static void init() {
        entityPoopEntityType = /*EntityType.register("poop", */EntityType.Builder.create(EntityPoop.class, EntityPoop::new).tracker(100, 1, true).build("poop");//);
        entityPoopEntityType.setRegistryName(EveryonePoops.MODID, "poop");
        //System.out.println("Ini: "+entityPoopEntityType.getRegistryName());
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event) {
        //System.out.println("Reg: "+entityPoopEntityType.getRegistryName());
        event.getRegistry().register(entityPoopEntityType);
    }

    public EntityPoop(World p_i1773_1_) {
        super(entityPoopEntityType, p_i1773_1_);
    }

    public EntityPoop(World p_i1774_1_, EntityLivingBase p_i1774_2_) {
        super(entityPoopEntityType, p_i1774_2_, p_i1774_1_);
    }

    public EntityPoop(World p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_) {
        super(entityPoopEntityType, p_i1775_2_, p_i1775_4_, p_i1775_6_, p_i1775_1_);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 3) {
            for(int lvt_2_1_ = 0; lvt_2_1_ < 8; ++lvt_2_1_) {
                this.world.spawnParticle((IParticleData) Particles.FALLING_DUST, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    protected void onImpact(RayTraceResult result)
    {
        if (result.entity == null)
            return;
        result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0f);
        if (this.world.isRemote || !PoopConfig.poopCausesNausea.get()) {
            return;
        }
        this.world.setEntityState(this, (byte)3);
        this.remove();
        if (!(result.entity instanceof EntityLivingBase) || !((EntityLivingBase) result.entity).canBeHitWithPotion())
            return;
        ((EntityLivingBase) result.entity).addPotionEffect(new PotionEffect(Potion.getPotionById(9), PoopConfig.nauseaDuration.get()));
    }
}
