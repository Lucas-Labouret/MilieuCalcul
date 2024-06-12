// package local.Ui.View;

// import javafx.scene.Group;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.paint.Color;
// import javafx.scene.paint.PhongMaterial;
// import javafx.scene.shape.Box;
// import javafx.scene.shape.Line;
// import javafx.scene.shape.Sphere;
// import javafx.scene.transform.Rotate;

// public class ContentPane extends BorderPane {

//     public ContentPane() {
//         super();
//         ToolBarComponent tc = new ToolBarComponent();
//         setTop(tc);

//         Box cube = new Box(100, 100, 100);
//         cube.setMaterial(new PhongMaterial(Color.RED));
//         Sphere sphere = new Sphere(50);
//         sphere.setMaterial(new PhongMaterial(Color.BLUE));
//         Line xAxis = new Line(-2000, 0, 2000, 0);
//         xAxis.setStroke(Color.RED);
//         Line yAxis = new Line(0, -2000, 0, 2000);
//         yAxis.setStroke(Color.GREEN);
//         Line zAxis = new Line(-2000, 0, 2000, 0);
//         zAxis.getTransforms().add(new Rotate(90, 0, 0, 0, Rotate.Y_AXIS));
//         zAxis.setStroke(Color.BLUE);

//         setCenter(new Group(cube, sphere, xAxis, yAxis, zAxis));
//     }
// }