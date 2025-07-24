package test;

import cannings.Canning;
import cannings.VertexCanningCompleter;
import cannings.coords.sCoords.VertexCoord;
import cannings.evaluation.MasksComputer;
import cannings.vertexCannings.*;
import computingMedia.media.HardRectangleMedium;
import computingMedia.media.Medium;
import computingMedia.sLoci.Vertex;
import savefileManagers.HardRectangleManager;
import savefileManagers.SavefileManager;
import simulatedAnnealing.NearestNeighborHybridAnnealer;
import simulatedAnnealing.VertexCanningNearestNeighborAnnealer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Tester {
    private static final String OUTPUT_DIR = "src/main/java/test/testResults/";
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
        "2025",
        "4096"
    );

    private static final List<String> canningMethods = List.of(
        "ORCI",
        "ORCI_AGVC",
        "ORCI2D"
    );

    private static Medium createMedium(String family, String size) {
        return switch (family) {
            case "HardSquare" -> new HardRectangleMedium(
                    1.0,
                    (int)Math.sqrt(Double.parseDouble(size))+2,
                    Integer.parseInt(size)
            );
            case "HardRectangle" -> new HardRectangleMedium(
                    Math.sqrt(3),
                    (int)Math.sqrt(Double.parseDouble(size))+2,
                    Integer.parseInt(size)
            );
            default -> throw new IllegalArgumentException("Unknown family: " + family);
        };
    }

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
            e.printStackTrace();
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
                try { medium = savefileManagers.get(family).load(mediumName).getMedium(); }
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
            try { medium = savefileManagers.get(family).load(mediumName).getMedium(); }
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
            try { medium = savefileManagers.get(family).load(mediumName).getMedium(); }
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
            try { medium = savefileManagers.get(families.get(x)).load(mediumName).getMedium(); }
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

    public static void testRoundedCoordIncrementalCoefStability(String methodName) {
        System.out.println("Executing " + methodName);

        String[][] results = new String[families.size()*sizes.size() + 1][201];
        results[0][0] = "";

        for (double incr = 0.01; incr <= 2.0; incr += 0.01) {
            results[0][(int)(incr * 100)] = String.format("%.5f", incr);
        }

        for (int i = 0; i < families.size(); i++) for (int j = 0; j < sizes.size(); j++) {
            String family = families.get(i);
            String size = sizes.get(j);
            String mediumName = family + size;
            results[i * sizes.size() + j + 1][0] = mediumName;

            System.out.println("Testing " + mediumName);
            for (double incr = 0.01; incr <= 2.0; incr += 0.01) {
                Medium medium;
                try { medium = savefileManagers.get(family).load(mediumName).getMedium(); }
                catch (Exception e) {
                    System.out.println("Failed to load medium: " + mediumName);
                    continue;
                }

                RoundedCoordIncrementalVCanning vc = new RoundedCoordIncrementalVCanning(medium, incr);
                Canning canning = new VertexCanningCompleter(vc);
                canning.can();

                double density = vc.getDensity();
                results[i * sizes.size() + j + 1][(int)(incr * 100)] = String.format("%.5f", density);
            }
        }

        makeCSV(methodName, results);
    }

    public static void canningStats(String methodName) {
        System.out.println("Executing " + methodName);

        String[][] results = new String[sizes.size() * 10 + 1][11];

        results[0][0]  = "Name";
        results[0][1]  = "dY";
        results[0][2]  = "dX";
        results[0][3]  = "Masks Average";
        results[0][4]  = "Masks Max";
        results[0][5]  = "Lines";
        results[0][6]  = "Average Fragments/Line";
        results[0][7]  = "Max Fragments/Line";
        results[0][8]  = "Average Fragment Size";
        results[0][9]  = "Max Fragment Size";
        results[0][10] = "Density";

        for (int iSize = 0; iSize < sizes.size(); iSize++) for (int i = 0; i < 10; i++){
            String name = "HardSquare" + sizes.get(iSize) + "_AnnealedRCIFull_" + i;

            Canning canning;
            try { canning = savefileManagers.get("HardSquare").load(name); }
            catch (Exception e) {
                System.out.println("Failed to load : " + name);
                continue;
            }

            System.out.println("Studying " + name);

            int line = iSize * 10 + i + 1;

            results[line][0] = name;

            MasksComputer masksRCI = new MasksComputer(canning);
            int[] deltas = masksRCI.getDeltas();

            results[line][1] = String.valueOf(deltas[0]);
            results[line][2] = String.valueOf(deltas[1]);

            double averageMasks = (masksRCI.getAverageEfFe() +
                                   masksRCI.getAverageVfFv() +
                                   masksRCI.getAverageVeEv()   ) / 3.0;
            results[line][3] = String.format("%.2f", averageMasks);

            int maxMasks = Math.max(
                    Math.max(masksRCI.getMaxEfFe(), masksRCI.getMaxVfFv()),
                    masksRCI.getMaxVeEv()
            );
            results[line][4] = String.valueOf(maxMasks);

            results[line][5] = String.valueOf(canning.getHeight());

            HashMap<VertexCoord, HashSet<Vertex>> fragments = new HashMap<>();
            for (Vertex vertex : canning.getMedium()) {
                VertexCoord coord = canning.getVertexCanning().get(vertex);
                VertexCoord fragment = new VertexCoord(coord.Y(), coord.X()/(32 - 2*deltas[1]) + 1);
                fragments.computeIfAbsent(fragment, k -> new HashSet<>()).add(vertex);
            }

            int[] fragmentsPerLine = new int[canning.getHeight()];
            for (VertexCoord fragment : fragments.keySet()) {
                int lineIndex = fragment.Y();
                if(fragment.X() > fragmentsPerLine[lineIndex]) fragmentsPerLine[lineIndex] = fragment.X();
            }

            double averageFragmentsPerLine = 0;
            int maxFragmentsPerLine = 0;
            for (int k : fragmentsPerLine) {
                averageFragmentsPerLine += k;
                if (k > maxFragmentsPerLine) maxFragmentsPerLine = k;
            }
            averageFragmentsPerLine /= canning.getHeight();

            results[line][6] = String.format("%.2f", averageFragmentsPerLine);
            results[line][7] = String.valueOf(maxFragmentsPerLine);

            double averageFragmentSize = 0;
            int maxFragmentSize = 0;
            for (HashSet<Vertex> fragment : fragments.values()) {
                int size = fragment.size();
                averageFragmentSize += size;
                if (size > maxFragmentSize) maxFragmentSize = size;
            }
            averageFragmentSize /= fragments.size();

            results[line][8] = String.format("%.2f", averageFragmentSize);
            results[line][9] = String.valueOf(maxFragmentSize);

            results[line][10] = String.format("%.2f", canning.getDensity());
        }

        makeCSV(methodName + "2", results);
    }
}