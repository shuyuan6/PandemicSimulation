import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;
import java.util.concurrent.*;


 public class PandemicSimulation extends Application {
    int simulationTimeStep = 10;
    int numPersons = 100;
    int fieldXLimit = 700;
    int fieldYLimit = 600;
    int chartXLimit = 700;
    int chartYLimit = 600;
    static int circleRadius = 5;
    int speedVariance = 20; // pixels per second
    int speedBase = 100; // pixels per second
    static Random random = new Random();
    List<Person> persons = new ArrayList<>(numPersons);
    int safeDistance = 1;

    static double sickSegment = 0.5;
    static double immuneSegment = 0.4;
    // the rest are healthy

    int sickTotal = 0;

    Map<Integer, Integer> time2NumSick = new HashMap<>();

    boolean sickNumUpdated = false;
    LineChart<Number, Number>  chart = getChart();

    enum Type {
        HEALTHY,
        INCUBATING,
        SICK,
        RECOVERED,
        IMMUNE
    }

    static class Person {
        public Circle circle;
        public Type type;
        public double xSpeed;
        public double ySpeed;
        public double x;
        public double y;

        public Person(double x, double y, double xSpeed, double ySpeed) {
            double typeDice = random.nextDouble();
            if (typeDice < sickSegment) {
                if (x < 100) {
                    this.type = Type.SICK;
                } else {
                    this.type = Type.HEALTHY;
                }
            } else if (typeDice < immuneSegment) {
                this.type = Type.IMMUNE;
            } else {
                this.type = Type.HEALTHY;
            }

            this.circle = new Circle(x, y, circleRadius);
            this.x = x;
            this.y = y;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            System.out.printf("Speed %f, %f\n", xSpeed, ySpeed);
        }

        public void move(int timeIntervalInMillis) {
            x += xSpeed * timeIntervalInMillis / 1000;
            y += ySpeed * timeIntervalInMillis / 1000;
        }

        public void syncState() {
            circle.setCenterX(x);
            circle.setCenterY(y);
            switch (type) {
                case SICK:
                    circle.setFill(Color.RED);
                    break;
                case HEALTHY:
                    circle.setFill(Color.GREEN);
                    break;
                case IMMUNE:
                    circle.setFill(Color.BLUE);
                    break;
                case INCUBATING:
                    circle.setFill(Color.ORANGE);
                    break;
                case RECOVERED:
                    circle.setFill(Color.GRAY);
                    break;
                default:
                    circle.setFill(Color.BLACK);
            }
        }

    }

    boolean hitVerticalLine(Person p, Line line) {
        if (Math.abs(line.getStartX() - line.getEndX()) > circleRadius) {
            throw new RuntimeException("Not a vertical line: " + line.toString());
        }
        return Math.abs(p.x - line.getStartX()) <= circleRadius
                && p.y >line.getStartY()
                && p.y < line.getEndY();
    }

    boolean hitHorizontalLine(Person p, Line line) {
        if (Math.abs(line.getStartY() - line.getEndY()) > circleRadius) {
            throw new RuntimeException("Not a horizontal line: " + line.toString());
        }
        return Math.abs(p.y - line.getStartY()) <= circleRadius
                && p.x >line.getStartX()
                && p.x < line.getEndX();
    }

    LineChart<Number, Number> getChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        NumberAxis yAxis = new NumberAxis();

        yAxis.setLabel("Number of sick people");

        XYChart.Series<Number, Number> dataSeries1 = new XYChart.Series<>();
        dataSeries1.setName("Number of sick people");


        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.getData().add(dataSeries1);

        return chart;
    }

    @Override
    public void start(Stage stage) {

        GridPane root = new GridPane();
        root.getColumnConstraints().add(new ColumnConstraints(fieldXLimit));
        root.getColumnConstraints().add(new ColumnConstraints(fieldXLimit));
        root.getRowConstraints().add(new RowConstraints(fieldYLimit));
        root.getRowConstraints().add(new RowConstraints(fieldYLimit));

        Pane group = new Pane();



        root.add(group, 0, 0);
        root.add(chart, 1, 0);

        Text text = new Text();
        text.setX(300);
        text.setY(10);
        group.getChildren().add(text);

        long startTimeInSec = (long) (System.currentTimeMillis());
        System.out.println("Start time: " + startTimeInSec);
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                for (Person p : persons) {
                    p.syncState();
                }
                text.setText("Sick: " + sickTotal);
                if (sickNumUpdated) {
                    sickNumUpdated = false;
                    long currTime = (long)(System.currentTimeMillis());
                    long elapsedTime = currTime - startTimeInSec;
                    //System.out.println("Elapsed time: " + currTime);
                    chart.getData().get(0).getData().add(
                            new XYChart.Data<>(elapsedTime, sickTotal)
                    );
                }
            }
        }.start();

        Line verticalLine = new Line(100, 5, 100, 700);
        Line horizontalLine = new Line(100, 300, 520, 300);
        group.getChildren().add(verticalLine);
        group.getChildren().add(horizontalLine);

        for (int i = 0; i< numPersons; i++) {
            Person person = new Person(
                    random.nextInt(fieldXLimit),
                    random.nextInt(fieldYLimit),
                    speedBase + random.nextInt(speedVariance),
                    speedBase + random.nextInt(speedVariance));
            group.getChildren().add(person.circle);
            persons.add(person);
        }

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
                4);

        for (int i = 0; i < numPersons; i++) {
            Integer personId = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //System.out.println("Working on person " + personId);
                    Person p = persons.get(personId);
                    p.move(simulationTimeStep);
                    if (p.x <= 0 || p.x >= fieldXLimit) {
                        p.xSpeed = -p.xSpeed;
                    }

                    if (p.y <= 0 || p.y >= fieldYLimit) {
                        p.ySpeed = -p.ySpeed;
                    }

                    if (hitVerticalLine(p, verticalLine)) {
                        p.xSpeed = -p.xSpeed;
                    }
                    if (hitHorizontalLine(p, horizontalLine)) {
                        p.ySpeed = -p.ySpeed;
                    }
                }
            };
            executorService.scheduleAtFixedRate(
                    runnable,
                    simulationTimeStep,
                    simulationTimeStep,
                    TimeUnit.MILLISECONDS );
        }

        Runnable contagiousCheck = () -> {
            for (Person p1 : persons) {
                if (p1.type == Type.SICK) {
                    for (Person p2 : persons) {
                        if (Math.abs(p2.x - p1.x) < safeDistance && Math.abs(p2.y - p1.y) < safeDistance && p2.type == Type.HEALTHY ) {
                            p2.type = Type.SICK;
                            sickTotal++;
                            sickNumUpdated = true;
                        }
                    }
                }
            }
        };

        executorService.scheduleAtFixedRate(
                contagiousCheck,
                simulationTimeStep,
                simulationTimeStep,
                TimeUnit.MILLISECONDS);

        Scene scene = new Scene(root, fieldXLimit + chartXLimit, fieldYLimit);
        stage.setTitle("Pandemic simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




