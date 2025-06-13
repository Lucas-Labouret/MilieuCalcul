package local.test;

import local.computingMedia.cannings.Canning;
import local.computingMedia.cannings.VertexCanningCompleter;
import local.computingMedia.cannings.evaluation.MasksComputer;
import local.computingMedia.cannings.vertexCannings.RoundedCoordDichotomyVCanning;
import local.computingMedia.cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;
import local.savefileManagers.HardRectangleManager;
import local.savefileManagers.SavefileManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class Tester {
    private static final String OUTPUT_DIR = "src/main/java/local/test/testResults/";
    private static final String EXTENSION = ".csv";

    private static final HashMap<String, SavefileManager> savefileManagers = new HashMap<>() {{
        put("HardSquare", new HardRectangleManager());
        put("HardRectangle", new HardRectangleManager());
    }};

    private static final List<String> families = savefileManagers.keySet().stream().toList();

    private static final List<String> sizes = List.of(
        "36",
        "64",
        "121",
        "256",
        "529",
        "1024",
        "2025"
    );

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Tester.Tester <testMethod>");
            return;
        }
        String methodName = args[0];

        try {
            Method m = Tester.class.getDeclaredMethod(methodName, String.class);
            m.setAccessible(true);
            m.invoke(null, methodName);
        }
        catch (NoSuchMethodException e) {
            System.out.println("Method not found: " + methodName);
        }
        catch (Exception e) {
            System.out.println("Error invoking method:\n");
            e.printStackTrace();
        }
    }

    private static void makeCSV(String methodName, String[][] results) {
        StringBuilder sb = new StringBuilder();
        for (String[] line : results) {
            for (String cell : line) {
                sb.append(cell == null ? "" : cell).append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("\n");
        }

        String fileName = OUTPUT_DIR + methodName + EXTENSION;
        BufferedWriter writer;
        try { writer = new BufferedWriter(new FileWriter(fileName)); }
        catch (Exception e) {
            System.out.println("Error creating file: " + fileName);
            return;
        }
        try { writer.write(sb.toString()); }
        catch (Exception e) {
            System.out.println("Error writing to file: " + fileName);
            return;
        }
        try { writer.close(); }
        catch (Exception e) {
            System.out.println("Error closing file: " + fileName);
        }
    }

    private static void testRoundedCoordIncrementalVsDichotomy(String methodName) {
        System.out.println("Executing " + methodName);

        final int horizontalOffset = sizes.size() + 2;

        String[][] results = new String[9 * families.size() - 1][(sizes.size()+1)*2 + 1];

        for (int familyIndex = 0; familyIndex < families.size(); familyIndex++) {
            String family = families.get(familyIndex);
            int verticalOffset = familyIndex * 9;
            results[verticalOffset][0] = family;

            results[verticalOffset +1][0] = "Incremental"; results[verticalOffset +1][sizes.size() + 2] = "Dichotomy";
            results[verticalOffset +2][0] = results[verticalOffset +2][horizontalOffset] = "deltaY";
            results[verticalOffset +3][0] = results[verticalOffset +3][horizontalOffset] = "deltaX";
            results[verticalOffset +4][0] = results[verticalOffset +4][horizontalOffset] = "density";
            results[verticalOffset +5][0] = results[verticalOffset +5][horizontalOffset] = "Ve<->Ev";
            results[verticalOffset +6][0] = results[verticalOffset +6][horizontalOffset] = "Vf<->Fv";
            results[verticalOffset +7][0] = results[verticalOffset +7][horizontalOffset] = "Ef<->Fe";

            for (int sizeIndex = 0; sizeIndex < sizes.size(); sizeIndex++) {
                String size = sizes.get(sizeIndex);

                String mediumName = family+size;
                Medium medium;
                try { medium = savefileManagers.get(family).load(mediumName); }
                catch (Exception e) {
                    System.out.println("Failed to load medium: " + mediumName);
                    continue;
                }

                Canning incrementalCanning = new VertexCanningCompleter(new RoundedCoordIncrementalVCanning(medium));
                incrementalCanning.can();
                MasksComputer incrementalMasks = new MasksComputer(incrementalCanning);

                Canning dichotomyCanning = new VertexCanningCompleter(new RoundedCoordDichotomyVCanning(medium));
                dichotomyCanning.can();
                MasksComputer dichotomyMasks = new MasksComputer(dichotomyCanning);

                //size
                results[verticalOffset + 1][                   1 + sizeIndex] = size;
                results[verticalOffset + 1][horizontalOffset + 1 + sizeIndex] = size;

                //deltaY
                results[verticalOffset + 2][                   1 + sizeIndex] = String.valueOf(incrementalMasks.getDeltas()[0]);
                results[verticalOffset + 2][horizontalOffset + 1 + sizeIndex] = String.valueOf(dichotomyMasks.getDeltas()[0]);

                //deltaX
                results[verticalOffset + 3][                   1 + sizeIndex] = String.valueOf(incrementalMasks.getDeltas()[1]);
                results[verticalOffset + 3][horizontalOffset + 1 + sizeIndex] = String.valueOf(dichotomyMasks.getDeltas()[1]);

                //density
                results[verticalOffset + 4][                   1 + sizeIndex] = String.format("%.2f", incrementalCanning.getDensity());
                results[verticalOffset + 4][horizontalOffset + 1 + sizeIndex] = String.format("%.2f", dichotomyCanning.getDensity());

                //Ve<->Ev
                results[verticalOffset + 5][                   1 + sizeIndex] = String.format("%.2f", incrementalMasks.getAverageVeEv());
                results[verticalOffset + 5][horizontalOffset + 1 + sizeIndex] = String.format("%.2f", dichotomyMasks.getAverageVeEv());

                //Vf<->Fv
                results[verticalOffset + 6][                   1 + sizeIndex] = String.format("%.2f", incrementalMasks.getAverageVfFv());
                results[verticalOffset + 6][horizontalOffset + 1 + sizeIndex] = String.format("%.2f", dichotomyMasks.getAverageVfFv());

                //Ef<->Fe
                results[verticalOffset + 7][                   1 + sizeIndex] = String.format("%.2f", incrementalMasks.getAverageEfFe());
                results[verticalOffset + 7][horizontalOffset + 1 + sizeIndex] = String.format("%.2f", dichotomyMasks.getAverageEfFe());
            }
        }

        makeCSV(methodName, results);
    }

    private static void testAbnormalNeighborCounts(String methodName) {
        System.out.println("Executing " + methodName);

        for (String family : families) for (String size : sizes) {
            String mediumName = family + size;
            Medium medium;
            try { medium = savefileManagers.get(family).load(mediumName); }
            catch (Exception e) {
                System.out.println("Failed to load medium: " + mediumName);
                continue;
            }

            for (Vertex v : medium)
                if ((v.getNeighbors().size() <= 3 && !v.isBorder()) || (v.getNeighbors().size() >= 9))
                    System.out.println("Abnormal vertex found in " + mediumName + ": " + v.getNeighbors().size() + " neighbors.");
        }
    }

    private static void testCardinalIsotropism(String methodName) {
        System.out.println("Executing " + methodName);

        for (String family : families) for (String size : sizes) {
            String mediumName = family + size;
            Medium medium;
            try { medium = savefileManagers.get(family).load(mediumName); }
            catch (Exception e) {
                System.out.println("Failed to load medium: " + mediumName);
                continue;
            }

            int totalErrors = 0;
            for (Vertex v : medium) {
                int maxCount = 5;
                if (v.isBorder()) maxCount = 6;
                int northCount = 0, southCount = 0, eastCount = 0, westCount = 0;
                for (Vertex neighbor : v.getNeighbors()) {
                    if (neighbor.getY() < v.getY()) northCount++;
                    else if (neighbor.getY() > v.getY()) southCount++;
                    if (neighbor.getX() < v.getX()) westCount++;
                    else if (neighbor.getX() > v.getX()) eastCount++;
                }
                if (northCount >= maxCount || southCount >= maxCount) {
                    System.out.println("    Vertical isotropism violation in " + mediumName + " at vertex (" + v.getX() + ", " + v.getY() + "):");
                    totalErrors++;
                }
                if (eastCount >= maxCount || westCount >= maxCount) {
                    System.out.println("    Horizontal isotropism violation in " + mediumName + " at vertex (" + v.getX() + ", " + v.getY() + "):");
                    totalErrors++;
                }
            }
            System.out.println("Total cardinal isotropism violations in " + mediumName + ": " + totalErrors);
        }
    }

    public static void testEdgeIsotropism(String methodName) {
        System.out.println("Executing " + methodName);

        String[][] results = new String[sizes.size() + 1][families.size() + 1];

        for (int y = 0; y < sizes.size(); y++) {
            results[y + 1][0] = sizes.get(y);
        }

        for (int x = 0; x < families.size(); x++) {
            results[0][x + 1] = families.get(x);
        }

        for (int y = 0; y < sizes.size(); y++) for (int x = 0; x < families.size(); x++) {
            String mediumName = families.get(x) + sizes.get(y);
            Medium medium;
            try { medium = savefileManagers.get(families.get(x)).load(mediumName); }
            catch (Exception e) {
                System.out.println("Failed to load medium: " + mediumName);
                continue;
            }

            MediumStatisticTest test = new MediumStatisticTest(medium);
            boolean isotropic = test.testIsotropism(0.1);
            results[y + 1][x + 1] = isotropic ? "Isotropic" : "Anisotropic";
        }

        makeCSV(methodName, results);
    }
}