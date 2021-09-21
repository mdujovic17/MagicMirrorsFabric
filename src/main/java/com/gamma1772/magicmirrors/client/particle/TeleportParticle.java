package com.gamma1772.magicmirrors.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class TeleportParticle extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;

    protected TeleportParticle(ClientWorld clientWorld, double posX, double posY, double posZ, double speedX, double speedY, double speedZ) {
        super(clientWorld, posX, posY, posZ, speedX, speedY, speedZ);
        this.velocityX = speedX;
        this.velocityY = speedY;
        this.velocityZ = speedZ;
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;
        this.scale = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
        //float j = this.random.nextFloat() * 0.6F + 0.4F;
        this.colorRed = 0;
        this.colorGreen = 222 / 255F;
        this.colorBlue = 255 / 255F;
        this.maxAge = (int)(Math.random() * 10.0D) + 40;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    public float getSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.maxAge;
        f = 1.0F - f;
        f *= f;
        f = 1.0F - f;
        return this.scale * f;
    }

    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        float f = (float)this.age / (float)this.maxAge;
        f *= f;
        f *= f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += (int)(f * 15.0F * 16.0F);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            float f = (float)this.age / (float)this.maxAge;
            this.velocityY += 0.04F;

            if (this.y == this.prevPosY) {
                this.velocityX *= 0.5F;
                this.velocityZ *= 0.5F;
            }

            float g = f;
            f = -f + f * f * 2.0F;
            f = 1.0F - f;

            this.velocityX *= 0.5D * f;
            this.velocityY *= 0.5D * f;
            this.velocityZ *= 0.5D * f;

            this.x = this.startX + this.velocityX * (double)f;
            this.y = this.startY + this.velocityY * (double)f + (double)(1.0F - g);
            this.z = this.startZ + this.velocityZ * (double)f;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            TeleportParticle particle = new TeleportParticle(clientWorld, d, e, f, g, h, i);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
    /*private static final Random RAND = new Random();
    protected final SpriteProvider provider;

    private TeleportParticle(SpriteProvider provider, ClientWorld clientWorld, double x, double y, double z, double speedX, double speedY, double speedZ) {
        super(clientWorld, x, y, z, 0.5D - RAND.nextDouble(), speedY, 0.5D - RAND.nextDouble());
        this.provider = provider;
        this.velocityY *= 0.15D;
        if(speedX == 0.0D && speedZ == 0.0D) {
            this.velocityX *= 0.5D;
            this.velocityZ *= 0.5D;
        }
        this.scale *= 1.3F;
        this.maxAge = (int)(7.5D / (Math.random() * 0.2D + 0.1D)); //was 7.5 0.4 and 0.2
        this.collidesWithWorld = false;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if(this.age++ >= this.maxAge){
            this.markDead();
        }else{
            this.setSpriteForAge(this.provider);
            this.velocityY += 0.04D;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            if(this.y == this.prevPosY){
                this.velocityX *= 0.5D;
                this.velocityZ *= 0.5D;
            }
            this.velocityX *= 0.5D;
            this.velocityY *= 0.5D;
            this.velocityZ *= 0.5D;
            if (this.onGround) {
                this.velocityX *= 0.5D;
                this.velocityZ *= 0.5D;
            }
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

//    @Override
//    protected int getBrightness(float tint) {
//        float f = ((float) this.age + tint) / (float) this.maxAge;
//        f = MathHelper.clamp(f, 0f, 1f);
//        int i = super.getBrightness(tint);
//        int j = i & 255;
//        int k = i >> 16 & 255;
//        j = j + (int) (f * 15f * 16f);
//        if (j > 240) {
//            j = 240;
//        }
//        return j | k << 16;    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double z, double y, double speedX, double speedY, double speedZ) {
            TeleportParticle particle = new TeleportParticle(this.spriteProvider ,clientWorld, x, y, z, speedX, speedY, speedZ);
            particle.setSprite(spriteProvider);
            return particle;
        }
    }*/
}