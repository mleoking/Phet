package edu.colorado.phet.eatingandexercise.model;

import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.control.Activity;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.FoodCalorieSet;

/**
 * Created by: Sam
 * Apr 3, 2008 at 1:05:20 PM
 */
public class Human {

    private String name;
    private ArrayList listeners = new ArrayList();

    private Gender gender = DEFAULT_VALUE.getGender();
    private DefaultTemporalVariable height = new DefaultTemporalVariable();//meters
    private DefaultTemporalVariable mass = new DefaultTemporalVariable();//kg
    private DefaultTemporalVariable age = new DefaultTemporalVariable();//sec
    private DefaultTemporalVariable fatMassFraction = new DefaultTemporalVariable();

    private DefaultTemporalVariable lipids = new DefaultTemporalVariable();
    private DefaultTemporalVariable carbs = new DefaultTemporalVariable();
    private DefaultTemporalVariable proteins = new DefaultTemporalVariable();

    private DefaultTemporalVariable activity = new DefaultTemporalVariable();//initialized to 0.5*BMR
    private DefaultTemporalVariable exercise = new DefaultTemporalVariable();//initialized to make sure weight is constant at startup
    private DefaultTemporalVariable bmr = new DefaultTemporalVariable();//dependent variable
    //    private Exercise exerciseObject = null;
    //New defaults: 5'8" 150 lbs 22 years
    private static final ReferenceHuman REFERENCE_MALE = new ReferenceHuman( true, 22, 5 + EatingAndExerciseUnits.inchesToFeet( 8 ), 8.5, EatingAndExerciseUnits.poundsToKg( 150 ), 86 );
    private static final ReferenceHuman REFERENCE_FEMALE = new ReferenceHuman( false, 22, 5 + EatingAndExerciseUnits.inchesToFeet( 8 ), 4.5, EatingAndExerciseUnits.poundsToKg( 150 ), 74 );
    public static final ReferenceHuman DEFAULT_VALUE = REFERENCE_FEMALE;

    private CalorieSet exerciseItems = new CalorieSet();
    private FoodCalorieSet foodItems = new FoodCalorieSet();
    private double activityLevel = Activity.DEFAULT_ACTIVITY_LEVEL.getValue();
    private ITemporalVariable caloricIntakeVariable = new DefaultTemporalVariable();
    private ITemporalVariable caloricBurnVariable = new DefaultTemporalVariable();
    private CaloricFoodItem defaultIntake;
    public static final String FOOD_PYRAMID = "food-pyramid.png";
    private boolean alive = true;
    private double starvingTime = 0;

    public Human() {
        lipids.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyDietChanged();
            }
        } );
        carbs.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyDietChanged();
            }
        } );
        proteins.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyDietChanged();
            }
        } );
        exercise.addListener( new IVariable.Listener() {
            public void valueChanged() {
                notifyExerciseChanged();
            }
        } );
        exerciseItems.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                updateExercise();
            }

            public void itemRemoved( CaloricItem item ) {
                updateExercise();
            }

            public void itemChanged( CaloricItem item ) {
                updateExercise();
            }
        } );
        foodItems.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                updateIntake();
            }

            public void itemRemoved( CaloricItem item ) {
                updateIntake();
            }

            public void itemChanged( CaloricItem item ) {
                updateIntake();
            }

        } );
        resetAll();
    }

    public void resetAll() {
        name = "Larry";

        clearTemporalVariables();

        setGender( DEFAULT_VALUE.getGender() );
        setHeight( DEFAULT_VALUE.getHeightMeters() );
        setMass( DEFAULT_VALUE.getMassKG() );
        setAge( DEFAULT_VALUE.getAgeSeconds() );
        setFatMassPercent( ( 100 - DEFAULT_VALUE.getFatFreeMassPercent() ) );

        updateBMR();
        setActivityLevel( Activity.DEFAULT_ACTIVITY_LEVELS[2].getValue() );
        Diet initialDiet = EatingAndExerciseModel.BALANCED_DIET;
        foodItems.clear();
        exerciseItems.clear();
        if ( defaultIntake == null ) {//todo: change to single instance so that view/controller can observe it
//            defaultIntake = new CaloricFoodItem( EatingAndExerciseResources.getString( "diet.healthy" ), FOOD_PYRAMID, 0.3 * 9 * 2000 / 5.5 / 9, 0.4 * 4 * 2000 / 5.5 / 4, 0.3 * 4 * 2000 / 5.5 / 4, false );
            defaultIntake = new CaloricFoodItem( EatingAndExerciseResources.getString( "diet.healthy" ), FOOD_PYRAMID, initialDiet.getFat() / 9, initialDiet.getCarb() / 4, initialDiet.getProtein() / 4, false );
        }
//        foodItems.addItem( defaultIntake );//todo: standardize constructor units
        updateIntake();

        //todo remove the need for this workaround
        simulationTimeChanged( 0.0 );
        foodItems.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                notifyDietChanged();
            }

            public void itemRemoved( CaloricItem item ) {
                notifyDietChanged();
            }

            public void itemChanged( CaloricItem item ) {
                notifyDietChanged();
            }
        } );
        exerciseItems.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                exercise.setValue( exerciseItems.getTotalCalories() );
                notifyExerciseChanged();
            }

            public void itemRemoved( CaloricItem item ) {
                exercise.setValue( exerciseItems.getTotalCalories() );
                notifyExerciseChanged();
            }

            public void itemChanged( CaloricItem item ) {
                exercise.setValue( exerciseItems.getTotalCalories() );
                notifyExerciseChanged();
            }
        } );

    }

    public double getActivityLevel() {
        return activityLevel;
    }

    public ITemporalVariable getMassVariable() {
        return mass;
    }

    public ITemporalVariable getCaloricIntakeVariable() {
        return caloricIntakeVariable;
    }

    public ITemporalVariable getCaloricBurnVariable() {
        return caloricBurnVariable;
    }

    public CaloricFoodItem getDefaultIntake() {
        return defaultIntake;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive( boolean alive ) {
        if ( alive != this.alive ) {
            this.alive = alive;
            notifyAliveChanged();
        }
    }

    private void notifyAliveChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).aliveChanged();
        }
    }

    private void clearTemporalVariables() {
        height.clear();
        mass.clear();
        age.clear();
        fatMassFraction.clear();
        lipids.clear();
        carbs.clear();
        proteins.clear();
        activity.clear();
        exercise.clear();
        bmr.clear();
        caloricIntakeVariable.clear();
        caloricBurnVariable.clear();
    }

    private void updateIntake() {
        lipids.setValue( foodItems.getTotalLipidCalories() );
        carbs.setValue( foodItems.getTotalCarbCalories() );
        proteins.setValue( foodItems.getTotalProteinCalories() );
    }

    private void updateExercise() {
        exercise.setValue( exerciseItems.getTotalCalories() );
    }

    public CalorieSet getSelectedFoods() {
        return foodItems;
    }

    public CalorieSet getSelectedExercise() {
        return exerciseItems;
    }

    private void notifyExerciseChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).exerciseChanged();
        }
        notifyCaloricBurnChanged();
    }

    public Diet getDiet() {
        return EatingAndExerciseModel.getDiet( lipids.getValue(), carbs.getValue(), proteins.getValue() );
    }

    private void notifyDietChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).dietChanged();
            ( (Listener) listeners.get( i ) ).caloricIntakeChanged();
        }
    }

    /*
     * SR � According to Exercise Physiology 4th Ed. By McArdle, Katch & Katch, 1996, p154, the Resting Daily Energy Expenditure is:
     RDEE (kcal) =370+21.6 * FFM (kg)
     This equation is said to apply to males and females over a wide range of body weights, and is the equation recommended to us by the CU Integrated Physiologists
     */
    private void updateBMR() {
//        bmr.setValue( BasalMetabolicRate.getBasalMetabolicRateHarrisBenedict( getMass(), getHeight(), getAge(), gender ) );
//        System.out.println( "value = " + value +", FFMP="+getFatFreeMassPercent());
        //
        bmr.setValue( 392 + 21.8 * getFatFreeMassKG() );
        updateActivity();
    }

    private double getFatFreeMassKG() {
        return getFatFreeMassPercent() / 100.0 * getMass();
    }

    /**
     * http://usmilitary.about.com/od/airforcejoin/a/afmaxweight.htm
     * The formula to compute BMI is
     * weight (in pounds) divided by the square of height (in inches),
     * multiplied by 704.5
     * <p/>
     * (Don't worry about that though, the below chart shows the maximum and minimum weights using the formula).
     *
     * @return
     */
    public double getBMIOrig() {
        return getWeightPounds() / Math.pow( getHeightInches(), 2 ) * 704.5;
    }

    public double getBMI() {
        return getMass() / Math.pow( getHeight(), 2 );
    }

    private double getHeightInches() {
        return getHeight() / 0.0254;
    }

    private double getWeightPounds() {
        return getMass() * 2.20462262;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

//    public double getLeanMuscleMass() {
//        return leanMuscleMass.getValue();
//    }

//    public double getFatPercent() {
//        return 100 - leanMuscleMass.;
//    }

//    public void setLeanMuscleMass( double value ) {
//        this.leanMuscleMass.setValue( value );
//        notifyMusclePercentChanged();
//        notifyFatPercentChanged();
//    }

    private void notifyFatPercentChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.fatPercentChanged();
        }
    }

    private void notifyMusclePercentChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.musclePercentChanged();
        }
    }

//    public void setFatPercent( double value ) {
//        this.leanMuscleMass = 100 - value;
//        notifyFatPercentChanged();
//        notifyMusclePercentChanged();

    //    }


    public void setActivityLevel( double val ) {
        activityLevel = val;
        updateActivity();
//        this.activity.setValue( val );
    }

    private void updateActivity() {
        this.activity.setValue( activityLevel * bmr.getValue() );
        notifyActivityChanged();
    }

    private void notifyActivityChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).activityChanged();
        }
        notifyCaloricBurnChanged();
    }

    private void notifyCaloricBurnChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).caloricBurnChanged();
        }
    }

    public DefaultTemporalVariable getLipids() {
        return lipids;
    }

    public DefaultTemporalVariable getCarbs() {
        return carbs;
    }

    public DefaultTemporalVariable getProteins() {
        return proteins;
    }

    public DefaultTemporalVariable getBmr() {
        return bmr;
    }

    public DefaultTemporalVariable getActivity() {
        return activity;
    }

    public DefaultTemporalVariable getExercise() {
        return exercise;
    }

    public void simulationTimeChanged( double simulationTimeChange ) {
        setAge( getAge() + simulationTimeChange );
        double caloriesGainedPerDay = getDeltaCaloriesGained();
        double kgGainedPerDay = EatingAndExerciseUnits.caloriesToKG( caloriesGainedPerDay );

        setMass( getMass() + kgGainedPerDay * EatingAndExerciseUnits.secondsToDays( simulationTimeChange ) );
        mass.addValue( getMass(), getAge() );

        caloricIntakeVariable.setValue( getDailyCaloricIntake() );
        caloricIntakeVariable.addValue( getDailyCaloricIntake(), getAge() );

        caloricBurnVariable.setValue( getDailyCaloricBurn() );
        caloricBurnVariable.addValue( getDailyCaloricBurn(), getAge() );

        if ( isStarving() ) {
            starvingTime += simulationTimeChange;
        }
        else {
            starvingTime = 0;
        }
        if ( getStarvingTimeDays() > 30 * 2 && isAlive() ) {
            setAlive( false );
        }

//        System.out.println( "getDailyCaloricIntake() = " + getDailyCaloricIntake() );
    }

    public double getStarvingTimeDays() {
        return EatingAndExerciseUnits.secondsToDays( starvingTime );
    }

    private boolean isStarving() {
        return getFatMassPercent() < 2.0;
    }

    private double getDeltaCaloriesGained() {
        return getDailyCaloricIntake() - getDailyCaloricBurn();
    }

    public double getDailyCaloricBurn() {
        return bmr.getValue() + activity.getValue() + exercise.getValue();
    }

    public double getDailyCaloricIntake() {
        return lipids.getValue() + proteins.getValue() + carbs.getValue();
    }

    public double getHeartHealth() {
        return 0.5;
    }

    public double getFatMassPercent() {
        return fatMassFraction.getValue() * 100;
    }

    public double getFatFreeMassPercent() {
        return 100 - getFatMassPercent();
    }

    public void setFatMassPercent( double value ) {
        fatMassFraction.setValue( gender.clampFatMassPercent( value ) / 100.0 );
//        fatMassFraction.setValue( value / 100.0 );
        updateBMR();
        notifyFatPercentChanged();
    }

    public void clearMassData() {
        mass.clear();
    }

    public String getCauseOfDeath() {
        if ( getStarvingTimeDays() >= 60 ) {
            return "starvation";
        }
        else {
            return null;
        }
    }

    public static class Gender {
        public static Gender MALE = new Gender( EatingAndExerciseResources.getString( "gender.male" ).toLowerCase(), 0, 100 );
        public static Gender FEMALE = new Gender( EatingAndExerciseResources.getString( "gender.female" ).toLowerCase(), 0, 100 );
        private String name;
        private double minFatMassPercent;
        private double maxFatMassPercent;

        private Gender( String name, double minFatMassPercent, double maxFatMassPercent ) {
            this.name = name;
            this.minFatMassPercent = minFatMassPercent;
            this.maxFatMassPercent = maxFatMassPercent;
        }

        public String toString() {
            return name;
        }

        public double getMinFatMassPercent() {
            return minFatMassPercent;
        }

        public double getMaxFatMassPercent() {
            return maxFatMassPercent;
        }

        public double clampFatMassPercent( double value ) {
            return MathUtil.clamp( minFatMassPercent, value, maxFatMassPercent );
        }
    }

    public double getAge() {
        return age.getValue();
    }

    public void setAge( double age ) {
        if ( getAge() != age ) {
            this.age.setValue( age );
            notifyAgeChanged();
        }
    }

    private void notifyAgeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.ageChanged();
        }
    }

    public double getHeight() {
        return height.getValue();
    }

    public void setHeight( double height ) {
        if ( this.height.getValue() != height ) {
            this.height.setValue( height );
            notifyHeightChanged();
            notifyBMIChanged();
        }
    }

    private void notifyBMIChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.bmiChanged();
        }
    }

    private void notifyHeightChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.heightChanged();
        }
    }

    public double getMass() {
        return mass.getValue();
    }

    public void setMass( double weight ) {
        double originalBmr = getBmr().getValue();
        this.mass.setValue( Math.max( weight, 0 ) );
        setFatMassPercent( 100 - ( ( originalBmr - 392 ) / 21.8 ) * ( 100 / weight ) );
        notifyFatPercentChanged();
        notifyWeightChanged();
        notifyBMIChanged();
    }

    private void notifyWeightChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.weightChanged();
        }
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender( Gender gender ) {
        if ( this.gender != gender ) {
            this.gender = gender;
            setFatMassPercent( gender.getMinFatMassPercent() );
            notifyGenderChanged();
        }
    }

    private void notifyGenderChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).genderChanged();
        }
    }

    public static interface Listener {
        void bmiChanged();

        void heightChanged();

        void weightChanged();

        void genderChanged();

        void musclePercentChanged();

        void fatPercentChanged();

        void ageChanged();

        void dietChanged();

        void exerciseChanged();

        void activityChanged();

        void caloricIntakeChanged();

        void caloricBurnChanged();

        void aliveChanged();
    }

    public static class Adapter implements Listener {

        public void bmiChanged() {
        }

        public void heightChanged() {
        }

        public void weightChanged() {
        }

        public void genderChanged() {
        }

        public void musclePercentChanged() {
        }

        public void fatPercentChanged() {
        }

        public void ageChanged() {
        }

        public void dietChanged() {
        }

        public void exerciseChanged() {
        }

        public void activityChanged() {
        }

        public void caloricIntakeChanged() {
        }

        public void caloricBurnChanged() {
        }

        public void aliveChanged() {
        }
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

//    public void addFoodItem( FoodItem foodItem ) {
//
//        HashSet orig = new HashSet( foods );
//        foods.add( foodItem );
//        if ( !orig.equals( foods ) ) {
//            System.out.println( "added foodItem = " + foodItem );
//            notifyFoodItemsChanged();
//        }
//    }
//
//    private void notifyFoodItemsChanged() {
//        for ( int i = 0; i < listeners.size(); i++ ) {
//            Listener listener = (Listener) listeners.get( i );
//            listener.foodItemsChanged();
//        }
//    }
//
//    public void removeFoodItem( FoodItem foodItem ) {
//
//        HashSet orig = new HashSet( foods );
//        foods.remove( foodItem );
//        if ( !orig.equals( foods ) ) {
//            System.out.println( "removed foodItem = " + foodItem );
//            notifyFoodItemsChanged();
//        }

    //    }

    public static class ReferenceHuman {
        boolean male;
        double ageYears;
        double heightFT;
        double massKG;
        double fatFreeMassPercent;

        public ReferenceHuman( boolean male, double ageYears, double heightFT, double heightIN, double massKG, double fatFreeMassPercent ) {
            this.male = male;
            this.ageYears = ageYears;
            this.heightFT = heightFT + heightIN / 12;
            this.massKG = massKG;
            this.fatFreeMassPercent = fatFreeMassPercent;
        }

        public double getHeightMeters() {
            return EatingAndExerciseUnits.feetToMeters( heightFT );
        }

        public double getAgeSeconds() {
            return EatingAndExerciseUnits.yearsToSeconds( ageYears );
        }

        public double getMassKG() {
            return massKG;
        }

        public double getFatFreeMassPercent() {
            return fatFreeMassPercent;
        }

        public Gender getGender() {
            return male ? Gender.MALE : Gender.FEMALE;
        }
    }
}
