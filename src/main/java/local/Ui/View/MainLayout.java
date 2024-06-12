// package local.Ui.View;

// import javafx.scene.Group;
// import javafx.scene.Node;
// import javafx.scene.PerspectiveCamera;
// import javafx.scene.Scene;
// import javafx.scene.SceneAntialiasing;
// import javafx.scene.SubScene;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.paint.Color;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.Box;
// import javafx.scene.shape.Line;
// import javafx.scene.shape.Sphere;
// import javafx.scene.transform.Rotate;
// import javafx.scene.transform.Translate;
// import local.Ui.Main;
// import local.Ui.camera.SimpleFPSCamera;
// import local.furthestpointoptimization.model.Vertex;
// import local.furthestpointoptimization.model.VertexSet;

// public class MainLayout extends BorderPane {

//     SidePanel sidepannel;

//     SubScene subScene3D;
//     SubScene subSceneFortune;

//     Group g;

//     public MainLayout() {
//         super();

//         this.sidepannel = new SidePanel();

//         ToolBarComponent tc = new ToolBarComponent();
//         tc.back.setOnAction(event -> {
//             if (this.getLeft() == null) {
//                 setLeft(this.sidepannel);
//             } else setLeft(null);
//         });
        
//         setTop(tc);


//         g = new Group();

//         VertexSet vs = new VertexSet(150);
//         vs.triangulate();

//         Box cube = new Box(100, 100, 100);
//         cube.setMaterial(new PhongMaterial(Color.RED));
//         g.getChildren().addAll(cube);

//         showVertexSet(vs);

//         addAxis(g);


//         subScene3D = new SubScene(g, Main.WIDTH, Main.HEIGHT, true, SceneAntialiasing.BALANCED);
        
//         SimpleFPSCamera fpscam = new SimpleFPSCamera();
//         subScene3D.setCamera(fpscam.getCamera());
//         fpscam.loadControlsForSubScene(subScene3D);
//         subScene3D.setCamera(fpscam.getCamera());
//         subScene3D.setPickOnBounds(true);

//         Group gf = new Group();
//         Sphere sphere = new Sphere(50);
//         sphere.setMaterial(new PhongMaterial(Color.BLUE));
//         gf.getChildren().add(sphere);
//         subSceneFortune = new SubScene(gf, Main.WIDTH, Main.HEIGHT, true, SceneAntialiasing.BALANCED);
        
//         tc.fortune.setOnAction(event -> {
//             setCenter(subSceneFortune);
//         });

//         tc.thirdDimension.setOnAction(event -> {
//             setCenter(subScene3D);
//         });

//         setCenter(subScene3D);
//     }

//     void addAxis(Group g) {
//         Line xAxis = new Line(-2000, 0, 2000, 0);
//         xAxis.setStroke(Color.RED);
//         Line yAxis = new Line(0, -2000, 0, 2000);
//         yAxis.setStroke(Color.GREEN);
//         Line zAxis = new Line(-2000, 0, 2000, 0);
//         zAxis.getTransforms().add(new Rotate(90, 0, 0, 0, Rotate.Y_AXIS));
//         zAxis.setStroke(Color.BLUE);
//         g.getChildren().addAll(xAxis, yAxis, zAxis);
//     }

//     public void showVertexSet(VertexSet vs) {
//         for (Vertex v : vs) {
//             double x = v.getX();
//             double y = v.getY();
            
//             // Créer la sphère et définir sa couleur en noir
//             Sphere s = new Sphere(10);
//             PhongMaterial blackMaterial = new PhongMaterial();
//             blackMaterial.setDiffuseColor(Color.BLACK);
//             s.setMaterial(blackMaterial);
//             s.getTransforms().add(new Translate(x * 3000, y * 3000));
//             g.getChildren().add(s);
            
//             for (Vertex nei : v.getNeighbors()) {
//                 // Créer la ligne et définir sa couleur en noir
//                 Line l = new Line(x * 3000, y * 3000, nei.getX() * 3000, nei.getY() * 3000);
//                 l.setStroke(Color.BLACK);
//                 g.getChildren().add(l);
//             }
//         }
//     }
// }

