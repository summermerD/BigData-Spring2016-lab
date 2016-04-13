import jAudioFeatureExtractor.AudioFeatures.*;
import jAudioFeatureExtractor.jAudioTools.AudioSamples;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.util.Random;


/**
 * Created by Mayanka on 25-Oct-15.
 */
public class FeatureExtraction2 {
    public enum AudioFeature {
        Spectral_Centroid(1),
        Spectral_Rolloff_Point(2),
        Spectral_Flux(3),
        Compactness(4),
        Spectral_Variability(5),
        Root_Mean_Square(6),
        Fration_of_Low_Energy_Windows(7),
        Zero_Crossings(8),
        Strongest_Beat(9),
        Beat_Sum(10),
        MFCC(11),
        ConstantQ(12),
        LPC(13),
        Method_of_Moments(14),
        Peak_Detection(15),
        Area_Method_of_MFCCs(16);

        private final int value;

        AudioFeature(int value) {

            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    static FilteredClassifier classifier;
    public static void main(String args[]) {

        try {

            FastVector atts = new FastVector();
            atts.addElement(new Attribute("Zero_Crossings"));
            atts.addElement(new Attribute("LPC"));

            //   atts.addElement(new Attribute("Class",(FastVector)null));
            //atts.addElement(new Attribute("Class"));
            String[] classes = {"cry", "laugh"};

            double[] val;
            FastVector attValsRel = new FastVector();
            for (int i = 0; i < classes.length; i++)
                attValsRel.addElement(classes[i]);
            atts.addElement(new Attribute("class", attValsRel));
            Instances instances = new Instances("AudioSamples", atts, 0);
            for (int i = 0; i < classes.length; i++) {
                File folder = new File("datatesting/training/" + classes[i]);
                if (folder.isDirectory()) {
                    File[] files = folder.listFiles();
                    for (int j = 0; j < files.length; j++) {
                        val = makeData(files[j], classes[i], attValsRel, instances.numAttributes());
                        instances.add(new Instance(1.0, val));
                    }
                }
            }

            System.out.println(instances);
            wekaAlgorithms(instances);
            classify(instances,new File("datatesting/testing/laugh/laugh 2.wav"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double[] makeData(File file, String str, FastVector attValsRel, int noOfAttributes) throws Exception {
        AudioSamples samples = new AudioSamples(file, file.getPath(), false);
        if(str==null)
        {
            double[] val = new double[noOfAttributes-1];
            double[] f = feature(samples, AudioFeature.Zero_Crossings);
            val[0] = f[0];
            double[] f1 = feature(samples, AudioFeature.LPC);
            val[1] = f1[0];
            return val;
        }
        else {
            double[] val = new double[noOfAttributes];
            double[] f = feature(samples, AudioFeature.Zero_Crossings);
            val[0] = f[0];
            double[] f1 = feature(samples, AudioFeature.LPC);
            val[1] = f1[0];
            if (str != null)
                val[2] = attValsRel.indexOf(str);
            return val;
        }

    }

    public static double[] feature(AudioSamples audio, AudioFeature i) throws Exception {
        /**
         * 1. Spectral Centroid
         * 2. Spectral Rolloff Point
         * 3. Spectral Flux
         * 4. Compactness
         * 5. Spectral Variability
         * 6. Root Mean Square
         * 7. Fration of Low Energy Windows
         * 8. Zero Crossings
         * 9. Strongest Beat
         * 10. Beat Sum
         * 11. MFCC
         * 12. ConstantQ
         * 13. LPC
         * 14. Method of Moments
         * 15. Peak Detection
         * 16. Area Method of MFCCs
         *
         */
        FeatureExtractor featureExt;
        double[] samples = audio.getSamplesMixedDown();
        double sampleRate = audio.getSamplingRateAsDouble();
        double[][] otherFeatures = null;
        double[][] windowSample;
        switch (i.getValue()) {
            case 1:
                featureExt = new SpectralCentroid();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);

            case 2:
                featureExt = new PowerSpectrum();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new SpectralRolloffPoint();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);

            case 3:
                windowSample = audio.getSampleWindowsMixedDown(2);
                featureExt = new MagnitudeSpectrum();
                otherFeatures[0] = featureExt.extractFeature(windowSample[0], sampleRate, otherFeatures);
                otherFeatures[1] = featureExt.extractFeature(windowSample[1], sampleRate, otherFeatures);
                featureExt = new SpectralFlux();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);

            case 4:
                featureExt = new MagnitudeSpectrum();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new Compactness();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);

            case 5:
                featureExt = new MagnitudeSpectrum();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new SpectralVariability();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);

            case 6:
                featureExt = new RMS();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 7:
                featureExt = new RMS();
                windowSample = audio.getSampleWindowsMixedDown(100);
                for (int j = 0; j < 100; j++) {
                    otherFeatures[j] = featureExt.extractFeature(windowSample[j], sampleRate, null);
                }
                featureExt = new FractionOfLowEnergyWindows();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 8:
                featureExt = new ZeroCrossings();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 9:
                featureExt = new BeatHistogram();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new BeatHistogramLabels();
                otherFeatures[1] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new StrongestBeat();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 10:
                featureExt = new BeatHistogram();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new BeatSum();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 11:
                featureExt = new MagnitudeSpectrum();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new MFCC();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 12:
                featureExt = new ConstantQ();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 13:
                featureExt = new LPC();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 14:
                featureExt = new MagnitudeSpectrum();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new Moments();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 15:
                featureExt = new MagnitudeSpectrum();
                otherFeatures[0] = featureExt.extractFeature(samples, sampleRate, otherFeatures);
                featureExt = new PeakFinder();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);
            case 16:
                featureExt = new MagnitudeSpectrum();
                windowSample = audio.getSampleWindowsMixedDown(100);
                for (int j = 0; j < 100; j++) {
                    otherFeatures[j] = featureExt.extractFeature(windowSample[j], sampleRate, null);
                }
                featureExt = new AreaMoments();
                return featureExt.extractFeature(samples, sampleRate, otherFeatures);

            default:
                return null;

        }
    }

    public static void wekaAlgorithms(Instances data) throws Exception {
         classifier = new FilteredClassifier();         // new instance of tree
        classifier.setClassifier(new NaiveBayes());
        data.setClassIndex(data.numAttributes() - 1);
        Evaluation eval = new Evaluation(data);

        int folds = 4;
        eval.crossValidateModel(classifier, data, folds, new Random(1));

        System.out.println("===== Evaluating on filtered (training) dataset =====");
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        double[][] mat = eval.confusionMatrix();
        System.out.println("========= Confusion Matrix =========");
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {

                System.out.print(mat[i][j] + "  ");
            }
            System.out.println(" ");
        }
    }

    public static void classify(Instances train,File file) throws Exception {
        FastVector atts = new FastVector();
        String[] classes = {"cry", "laugh"};
        double[] val;
        FastVector attValsRel = new FastVector();

        //Setting attributes for the test data
            Attribute attributeZero=new Attribute("Zero_Crossings");
            atts.addElement(attributeZero);
            Attribute attributeLPC=new Attribute("LPC");
            atts.addElement(attributeLPC);
            for (int i = 0; i < classes.length; i++)
                attValsRel.addElement(classes[i]);
            atts.addElement(new Attribute("class", attValsRel));
        // Adding instances to the test dataset

            Instances test = new Instances("AudioSamples", atts, 0);
             val = makeData(file, null, attValsRel, test.numAttributes());
            Instance instance = new Instance(3);
            instance.setValue(attributeZero, val[0]);
            instance.setValue(attributeLPC,val[1]);
            test.add(instance);
        //Setting the class attribute
            test.setClassIndex(test.numAttributes() - 1);
            System.out.println(test);
        //Trainging the classifier with train dataset
            classifier=new FilteredClassifier();
            classifier.buildClassifier(train);

        //Classifying the test data with the train data
            for (int i = 0; i < test.numInstances(); i++) {
                double pred = classifier.classifyInstance(test.instance(i));
                System.out.println("===== Classified instance =====" );
                System.out.println("Class predicted: " + test.classAttribute().value((int) pred));
                socket.sendCommandToRobot("===== Classified instance =====" + '\n');
                socket.sendCommandToRobot("Class predicted: " + test.classAttribute().value((int) pred) + '\n');
            }

    }

}


