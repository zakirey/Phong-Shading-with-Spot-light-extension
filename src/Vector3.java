public class Vector3 implements Cloneable{
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public Vector3 scale(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 subtract(Vector3 vector) {
        return new Vector3(this.x - vector.x, this.y - vector.y, this.z - vector.z);
    }

    public double dot(Vector3 vector) {
        return (vector.x * this.x) + (vector.y * this.y) + (vector.z * this.z);
    }

    public double angle(Vector3 vector){
        return Math.acos(this.dot(vector)/(this.getMagnitude()*vector.getMagnitude()));
    }

    public double getMagnitude(){
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
    }

    public Vector3 unitVector(Light light) {
        double x = light.X - this.x;
        double y = light.Y - this.y;
        double z = light.Z - this.z;
        double m = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return new Vector3(x/m, y/m, z/m);
    }

    public double distanceFromLight(Light light) {
        return Math.sqrt(Math.pow(light.X - this.x, 2) +
                Math.pow(light.Y - this.y, 2) +
                Math.pow(light.Z - this.z, 2));
    }

    public Vector3 vectorObserver() {
        Vector3 observer = new Vector3(0,0,-1);
        double scalar = dot(observer);
        Vector3 observerP = scale(scalar);
        Vector3 observerT = observer.subtract(observerP);
        return observerP.subtract(observerT);
    }

    public Vector3 normalize(double radius) {
        double x = this.x / radius;
        double y = this.y / radius;
        double z = this.z / radius;
        return new Vector3(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}