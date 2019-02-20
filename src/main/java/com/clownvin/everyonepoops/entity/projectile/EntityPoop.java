package com.clownvin.everyonepoops.entity.projectile;

import com.clownvin.everyonepoops.config.PoopConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPoop extends EntityThrowable {

    public EntityPoop(World worldIn)
    {
        super(worldIn);
    }

    public EntityPoop(World worldIn, EntityLivingBase throwerIn)
    {
        super(worldIn, throwerIn);
    }

    public EntityPoop(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public static void registerFixesSnowball(DataFixer fixer)
    {
        EntityThrowable.registerFixesThrowable(fixer, "Poop");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
            for (int i = 0; i < 8; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, id);
            }
        }
    }

    protected void onImpact(RayTraceResult result)
    {
        if (result.entityHit == null)
            return;
        result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0f);
        if (this.world.isRemote || !PoopConfig.poopCausesNausea) {
            return;
        }
        this.world.setEntityState(this, (byte)3);
        this.setDead();
        if (!(result.entityHit instanceof EntityLivingBase) || !((EntityLivingBase) result.entityHit).canBeHitWithPotion())
            return;
        ((EntityLivingBase) result.entityHit).addPotionEffect(new PotionEffect(Potion.getPotionById(9), 300));
    }
}
