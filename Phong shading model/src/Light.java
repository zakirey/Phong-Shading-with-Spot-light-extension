public class Light {
    double X, Y, Z;
    double intensityR, intensityG, intensityB;
    Vector3 coneAxis;
    double innerConeAngle;
    double outerConeAngle;
    
    Light(double x, double y, double z, double intensityR, double intensityB, double intensityG, Vector3 coneAxis,
          double innerConeAngle, double outerConeAngle) {
        this.X = x; this.Y = y; this.Z = z;
        this.intensityR = intensityR; this.intensityG = intensityG; this.intensityB = intensityB;
        this.coneAxis = new Vector3(coneAxis);
        this.innerConeAngle = innerConeAngle;
        this.outerConeAngle = outerConeAngle;
    }

    Light(double x, double y, double z, double intensityR, double intensityB, double intensityG) {
        this.X = x; this.Y = y; this.Z = z;
        this.intensityR = intensityR; this.intensityG = intensityG; this.intensityB = intensityB;
    }
}
