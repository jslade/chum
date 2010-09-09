package chum.engine.common;

import chum.f.Vec3;


public interface Rotatable {
    public void setAngle(float angle);
    public void setAxis(Vec3 axis);
    public float getAngle();
    public Vec3 getAxis();
}

