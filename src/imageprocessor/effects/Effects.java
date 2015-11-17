package imageprocessor.effects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.effect.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by kgurgul on 2015-11-17.
 */
public class Effects {

    private ArrayList<Effect> effects;

    public Effects() {
        initEffects();
    }

    private void initEffects() {
        effects = new ArrayList<>();
        effects.add(null);
        effects.add(getBloomEffect());
        effects.add(getBoxBlurEffect());
        effects.add(getColorAdjustEffect());
        effects.add(getGaussianBlurEffect());
        effects.add(getGlowEffect());
        effects.add(getLightingEffect());
        effects.add(getMotionBlurEffect());
        effects.add(getSephiaToneEffect());
    }

    private Effect getBloomEffect() {
        return new Bloom(0.1);
    }

    private Effect getBoxBlurEffect() {
        BoxBlur boxBlur = new BoxBlur();
        boxBlur.setWidth(3);
        boxBlur.setHeight(3);
        boxBlur.setIterations(3);

        return boxBlur;
    }

    private Effect getColorAdjustEffect() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0.1);
        colorAdjust.setHue(-0.05);
        colorAdjust.setBrightness(0.1);
        colorAdjust.setSaturation(0.2);

        return colorAdjust;
    }

    private Effect getGaussianBlurEffect() {
        return new GaussianBlur();
    }

    private Effect getGlowEffect() {
        return new Glow(1.0);
    }

    private Effect getLightingEffect() {
        Light.Distant light = new Light.Distant();
        light.setAzimuth(50.0);
        light.setElevation(30.0);
        light.setColor(Color.YELLOW);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(50.0);

        return lighting;
    }

    private Effect getMotionBlurEffect() {
        MotionBlur motionBlur = new MotionBlur();
        motionBlur.setRadius(30);
        motionBlur.setAngle(-15.0);

        return motionBlur;
    }

    private Effect getSephiaToneEffect() {
        return new SepiaTone();
    }

    public ObservableList getEffectsList() {
        return FXCollections.observableArrayList(
                "Brak", "Bloom", "BoxBlur", "ColorAdjust", "GaussianBlur",
                "Glow", "Lighting", "MotionBlur", "SepiaTone"
        );
    }

    public Effect getEffect(int position) {
        return effects.get(position);
    }
}
