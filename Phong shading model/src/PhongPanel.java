import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PhongPanel extends JPanel {

    MaterialParameters materialAmbientColor;
    MaterialParameters materialDiffuseColor;
    MaterialParameters materialSpecularColor;
    MaterialParameters selfLuminance;
    double materialShininess;
    double ambientIntensity;

    MaterialParameters attenuation;
    ArrayList<Light> lights;

    String imageFileName;

    Sphere sphere;

    BufferedImage img;

    JFrame frame;
    boolean coneLightOn;
    PhongPanel(String fileName, JFrame frame, boolean coneLightOn){
        super();
        this.frame = frame;
        lights = new ArrayList<>();
        this.coneLightOn = coneLightOn;
        this.load(fileName);
        this.render();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if(img != null) {
            g2d.drawImage(img, 0,0, this);
        }
    }

    public void load (String FileName) {
        BufferedReader br;
        try{
            String line;
            br = new BufferedReader(new FileReader(FileName));
            if((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                frame.setPreferredSize(new Dimension(width+10, height+10));
                setPreferredSize(new Dimension(width, height));
                while((line = br.readLine()) != null) {
                    switch (line) {
                        case "LightParameters":
                            parts = br.readLine().split(",");
                            double x = Double.parseDouble(parts[0]);
                            double y = Double.parseDouble(parts[1]);
                            double z = Double.parseDouble(parts[2]);
                            double intensityR = Double.parseDouble(parts[3]);
                            double intensityG = Double.parseDouble(parts[4]);
                            double intensityB = Double.parseDouble(parts[5]);
                            if(parts.length <= 6 ) {
                                lights.add(new Light(x, y, z, intensityR, intensityG, intensityB));
                            } else {
                                double vectorX = Double.parseDouble(parts[6]);
                                double vectorY = Double.parseDouble(parts[7]);
                                double vectorZ = Double.parseDouble(parts[8]);
                                double innerConeAngle = Double.parseDouble(parts[9]);
                                double outerConeAngle = Double.parseDouble(parts[10]);
                                Vector3 vector3 = new Vector3(vectorX, vectorY, vectorZ);
                                lights.add(new Light(x, y, z, intensityR, intensityG, intensityB, vector3,
                                        innerConeAngle, outerConeAngle));
                            }
                            break;
                        case "DiffuseParameters": {
                            parts = br.readLine().split(",");
                            double paramR = Double.parseDouble(parts[0]);
                            double paramG = Double.parseDouble(parts[1]);
                            double paramB = Double.parseDouble(parts[2]);
                            materialDiffuseColor = new MaterialParameters(paramR, paramG, paramB);
                            break;
                        }
                        case "SelfLuminanceParameters": {
                            parts = br.readLine().split(",");
                            double paramR = Double.parseDouble(parts[0]);
                            double paramG = Double.parseDouble(parts[1]);
                            double paramB = Double.parseDouble(parts[2]);
                            selfLuminance = new MaterialParameters(paramR, paramG, paramB);
                            break;
                        }
                        case "SpecularParameters": {
                            parts = br.readLine().split(",");
                            double paramR = Double.parseDouble(parts[0]);
                            double paramG = Double.parseDouble(parts[1]);
                            double paramB = Double.parseDouble(parts[2]);
                            materialSpecularColor = new MaterialParameters(paramR, paramG, paramB);
                            break;
                        }
                        case "AmbientParameters": {
                            parts = br.readLine().split(",");
                            double paramR = Double.parseDouble(parts[0]);
                            double paramG = Double.parseDouble(parts[1]);
                            double paramB = Double.parseDouble(parts[2]);
                            materialAmbientColor = new MaterialParameters(paramR, paramG, paramB);
                            break;
                        }
                        case "AmbientIntensityParameter":
                            parts = br.readLine().split(" ");
                            ambientIntensity = Double.parseDouble(parts[0]);
                            break;
                        case "ShininessParameter":
                            parts = br.readLine().split(" ");
                            materialShininess = Double.parseDouble(parts[0]);
                            break;
                        case "AttenuationParameters": {
                            parts = br.readLine().split(",");
                            double paramR = Double.parseDouble(parts[0]);
                            double paramG = Double.parseDouble(parts[1]);
                            double paramB = Double.parseDouble(parts[2]);
                            attenuation = new MaterialParameters(paramR, paramG, paramB);
                            break;
                        }
                        case "SphereRadius": {
                            parts = br.readLine().split(" ");
                            sphere = new Sphere(Integer.parseInt(parts[0]));
                            break;
                        }
                        case "OutputFileName":
                            parts = br.readLine().split(" ");
                            imageFileName = parts[0];
                            break;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private void render(){
        double width = getPreferredSize().width;
        double height = getPreferredSize().height;
        BufferedImage outputImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                double x = (sphere.radius * (( (2.0 * j) / (width - 1) ) - 1));
                double y = (sphere.radius * (1 - ( (2.0 * i) / (height - 1) )));


                if((Math.pow(x, 2) + Math.pow(y, 2)) < Math.pow(sphere.radius, 2)) {
                    double z =  Math.sqrt(Math.pow(sphere.radius, 2) - Math.pow(x, 2) - Math.pow(y, 2));
                    Vector3 intersectionPoint = new Vector3(x, y, z);
                    Vector3 normalizedVector = intersectionPoint.normalize(sphere.radius);
                    Vector3 observerVector = normalizedVector.vectorObserver();


                    int luminanceR = (int) (selfLuminance.R + (materialDiffuseColor.R * FirstSum(intersectionPoint, normalizedVector, "R")) +
                            (materialSpecularColor.R * SecondSum(intersectionPoint, observerVector, "R")) +
                            (materialAmbientColor.R * ambientIntensity));
                    int luminanceG = (int) (selfLuminance.G + (materialDiffuseColor.G * FirstSum(intersectionPoint, normalizedVector, "G")) +
                            (materialSpecularColor.G * SecondSum(intersectionPoint, observerVector, "G")) +
                            (materialAmbientColor.G * ambientIntensity));
                    int luminanceB = (int) (selfLuminance.B + (materialDiffuseColor.B * FirstSum(intersectionPoint, normalizedVector, "B")) +
                            (materialSpecularColor.B * SecondSum(intersectionPoint, observerVector, "B")) +
                            (materialAmbientColor.B * ambientIntensity));
                    Color rgb = new Color(Math.min(luminanceR, 255), Math.min(luminanceG, 255), Math.min(luminanceB, 255));

                    outputImage.setRGB(j, i, rgb.getRGB());
                }
//                else {
//                    Color rgb = new Color(0, 0, 160);
//
//                    outputImage.setRGB(j, i, rgb.getRGB());
//                }
            }
        }
        img = outputImage;
        try {
            ImageIO.write(outputImage, "jpg", new File(imageFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        repaint();
    }

    private double coneLight(double intensity, Vector3 unitVectorToLight, Light light) {
        double coneLightAngle = Math.toDegrees(light.coneAxis.angle(unitVectorToLight));
        if(light.innerConeAngle < coneLightAngle && coneLightAngle < light.outerConeAngle) {
            double att = 1.0 - ((coneLightAngle - light.innerConeAngle)/(light.outerConeAngle-light.innerConeAngle));
            return intensity * att;
        } else if(coneLightAngle < light.innerConeAngle) {
            return intensity;
        } else {
            return 0;
        }
    }

    private double SecondSum(Vector3 intersectionPoint, Vector3 observerVector, String i) {
        double sum = 0.0;
        for (Light light : lights) {
            double distanceToLight = intersectionPoint.distanceFromLight(light);
            Vector3 unitVectorToLight = intersectionPoint.unitVector(light);
            double dot = Math.pow(unitVectorToLight.dot(observerVector), materialShininess);
            double attenuationFactor = attenuationFactor(distanceToLight);
            Vector3 unitLight = new Vector3(intersectionPoint.x - light.X/distanceToLight,
                    intersectionPoint.y - light.Y/distanceToLight,
                    intersectionPoint.z - light.Z/distanceToLight);
            switch (i) {
                case "R": {
                    if(coneLightOn) {
                        sum += attenuationFactor * coneLight(light.intensityR, unitLight, light) * dot;
                    } else {
                        sum += attenuationFactor * light.intensityR * dot;
                    }
                    break;
                }
                case "G": {
                    if(coneLightOn) {
                        sum += attenuationFactor * coneLight(light.intensityG, unitLight, light) * dot;
                    } else {
                        sum += attenuationFactor * light.intensityG * dot;
                    }
                    break;
                }
                case "B": {
                    if(coneLightOn) {
                        sum += attenuationFactor * coneLight(light.intensityB, unitLight, light) * dot;
                    } else {
                        sum += attenuationFactor * light.intensityB * dot;
                    }
                    break;
                }
            }
        }
        return sum;
    }

    private double FirstSum(Vector3 intersectionPoint, Vector3 normalizedVector, String i) {
        double sum = 0.0;
        for (Light light : lights) {
            double distanceToLight = intersectionPoint.distanceFromLight(light);
            Vector3 unitVectorToLight = intersectionPoint.unitVector(light);
            double dot = Math.max(unitVectorToLight.dot(normalizedVector), 0);
            double attenuationFactor = attenuationFactor(distanceToLight);
            Vector3 unitLight = new Vector3(intersectionPoint.x - light.X/distanceToLight,
                    intersectionPoint.y - light.Y/distanceToLight,
                    intersectionPoint.z - light.Z/distanceToLight);
            switch (i) {
                case "R": {
                    if(coneLightOn) {
                        sum += attenuationFactor * coneLight(light.intensityR, unitLight, light) * dot;
                    } else {
                        sum += attenuationFactor * light.intensityR * dot;
                    }
                    break;
                }
                case "G": {
                    if(coneLightOn) {
                        sum += attenuationFactor * coneLight(light.intensityG, unitLight, light) * dot;
                    } else {
                        sum += attenuationFactor * light.intensityG * dot;
                    }
                    break;
                }
                case "B": {
                    if(coneLightOn) {
                        sum += attenuationFactor * coneLight(light.intensityB, unitLight, light) * dot;
                    } else {
                        sum += attenuationFactor * light.intensityB * dot;
                    }
                    break;
                }
            }
        }
        return sum;
    }

    private double attenuationFactor(double distanceToLight) {
        return Math.min((1.0 / (attenuation.B * (Math.pow(distanceToLight, 2)) + (attenuation.G * distanceToLight) + attenuation.R)), 1);
    }

}
