import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.ArrayList;
import java.util.Random;
import static java.util.Collections.sort;

// Each MyPolygon has a color and a Polygon object
class MyPolygon {

    Polygon polygon;
    Color color;

    public MyPolygon(Polygon _p, Color _c) {
        polygon = _p;
        color = _c;
    }

    public Color getColor() {
        return color;
    }

    public Polygon getPolygon() {
        return polygon;
    }
}

// Each GASolution has a list of MyPolygon objects -> CHROMOSOME CLASS
class GASolution {

    ArrayList<MyPolygon> shapes;

    // width and height are for the full resulting image
    int width, height;
    double fitness;

    public GASolution(int _width, int _height) {
        shapes = new ArrayList<MyPolygon>();
        width = _width;
        height = _height;
    }
    public void addPolygon(MyPolygon p) {shapes.add(p);}
    public ArrayList<MyPolygon> getShapes() {
        return shapes;
    }
    public int size() {
        return shapes.size();
    }
    public void setShapes(ArrayList<MyPolygon> sh)
    {
        this.shapes = sh;
    }
    // Create a BufferedImage of this solution
    // Use this to compare an evolved solution with
    // a BufferedImage of the target image
    //
    // This is almost surely NOT the fastest way to do this...
    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (MyPolygon p : shapes) {
            Graphics g2 = image.getGraphics();
            g2.setColor(p.getColor());
            Polygon poly = p.getPolygon();
            if (poly.npoints > 0) {
                g2.fillPolygon(poly);
            }
        }
        return image;
    }
    public String toString() {
        return "" + shapes;
    }
}


// A Canvas to draw the highest ranked solution each epoch
class GACanvas extends JComponent{
    int width, height;
    GASolution solution;
    public GACanvas(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
        width = WINDOW_WIDTH;
        height = WINDOW_HEIGHT;
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setImage(GASolution sol) {
        solution = sol;
    }
    public void paintComponent(Graphics g) {
        BufferedImage image = solution.getImage(); //RETURNS buffered image
        g.drawImage(image, 0, 0, null);
    }
}

public class GA extends JComponent
{
    GACanvas canvas;
    int width, height;
    BufferedImage realPicture;
    ArrayList<GASolution> population;
    ArrayList<Double> popFitness;

    // Adjust these parameters as necessary for your simulation
    double MUTATION_RATE = 0.06;
    double CROSSOVER_RATE = 0.6;
    int MAX_POLYGON_POINTS = 5;
    int MAX_POLYGONS = 10;
    int POP_SIZE = 100;
    int FIT_TEST = 200;
    int GENERATIONS = 10000;
    int WEIGHT_DIV = 10;
    
    public GA(GACanvas _canvas, BufferedImage _realPicture) 
    {
        canvas = _canvas;
        realPicture = _realPicture;
        width = realPicture.getWidth();
        height = realPicture.getHeight();
        population = new ArrayList<GASolution>();
        popFitness = new ArrayList<Double>();
        generateInitPopulation(POP_SIZE, MAX_POLYGONS, MAX_POLYGON_POINTS);
    }

    public void generateInitPopulation(int init_num, int max_poly, int max_p_p)
    {
        for(int i = 0; i < init_num; i++)
        {
            GASolution ga = new GASolution(width, height);
            for(int j = 0; j < max_poly; j++)
            {
                ga.addPolygon(generateRandomChromosome(max_p_p));
            }
            population.add(ga);
        }
        generatePopulationFitness();
    }
    
    public MyPolygon generateRandomChromosome(int max_p_p)
    {
        Random r = new Random();
        int[] x = new int[max_p_p];
        int[] y = new int[max_p_p];
        for(int k = 0; k < x.length; k++)
        {
            x[k] = r.nextInt(width);
            y[k] = r.nextInt(height);

        }
        Polygon poly = new Polygon(x.clone(),y.clone(), max_p_p);
        Color colour = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        return new MyPolygon(poly, colour);
    }

    public void generatePopulationFitness()
    {
        ArrayList<Double> new_fit = new ArrayList<>();
        for (GASolution gs : population)
        {
            double fit = doFitness(gs);
            new_fit.add(fit);
            gs.fitness = fit;
        }
        popFitness = new_fit;
    }

    public double doFitness(GASolution gs) //assess fitness of particular solution
    {
        double fitness = 0;
        BufferedImage newImg = gs.getImage();
        for(int i = 0; i < FIT_TEST; i++)
        {
            fitness += getColorDiff(newImg);
        }
        fitness = 1/(fitness/FIT_TEST);
        return  fitness;
    }

    public double getColorDiff(BufferedImage img) //compares real img to test img, gets distance between r g b
    {
        Random r = new Random();
        int g = r.nextInt(width);
        int h = r.nextInt(height);
        Color test = new Color(img.getRGB(g, h));
        Color real = new Color(realPicture.getRGB(g, h));
        double red = test.getRed() - real.getRed();
        double green = test.getGreen() - real.getGreen();
        double blue = test.getBlue() - real.getBlue();
        red =  Math.pow(red,2);
        green =  Math.pow(green,2);
        blue = Math.pow(blue,2);
        return Math.sqrt(red + green + blue);
    }

    public GASolution pickFitParent()
    {
        Random rand = new Random();
        double totalFitness = 0;
        for(Double i : popFitness)
        {
            totalFitness += i;
        }
        double r = rand.nextDouble();
        r *=totalFitness;
        int index = -1;
        while(r > 0)
        {
            index += 1;
            r -= popFitness.get(index);
        }
        return population.get(index);
    }

    public GASolution getBestChromosome()
    {
        int max_index = popFitness.indexOf(getMaxFitness());
        return population.get(max_index);
    }

    public double getMaxFitness()
    {
        ArrayList<Double> clone = (ArrayList<Double>) popFitness.clone();
        sort(clone);
        return (clone.get(clone.size() -1));
    }

    public GASolution crossOver(GASolution parent1, GASolution parent2)
    {
        ArrayList<MyPolygon> parent1DNA = parent1.getShapes();
        ArrayList<MyPolygon> parent2DNA = parent2.getShapes();
        ArrayList<MyPolygon> childDNA = new ArrayList<>();
        GASolution child = new GASolution(width, height);
        Random rand = new Random();
        for(int i = 0; i < parent1DNA.size(); i++)
        {
            Polygon parentPolygon = rand.nextInt() > 0.5 ? parent1DNA.get(i).getPolygon() : parent2DNA.get(i).getPolygon();
            Color parentColor = rand.nextInt() < 0.5 ? parent1DNA.get(i).getColor() : parent2DNA.get(i).getColor();
            int[] child_x = parentPolygon.xpoints.clone();
            int[] child_y = parentPolygon.ypoints.clone();
            Polygon childPolygon = new Polygon(child_x, child_y, MAX_POLYGON_POINTS);
            Color childPheno = new Color(parentColor.getRGB());
            childDNA.add(new MyPolygon(childPolygon, childPheno));
        }
        child.setShapes(childDNA);
        return child;
    }

    public GASolution mutate(GASolution child)
    {
        ArrayList<MyPolygon> mutantMP = new ArrayList<>();
        Polygon mutantPolygon;
        Color mutantColor;
        Random rand = new Random();
        for (MyPolygon mp : child.getShapes())
        {
            if(Math.random() < MUTATION_RATE)
            {
                int[] x = mp.getPolygon().xpoints.clone();
                int[] y = mp.getPolygon().ypoints.clone();
                for(int i = 0; i < MAX_POLYGON_POINTS; i++)
                {
                    x[i] = (int)(x[i]+rand.nextGaussian() * width/WEIGHT_DIV);
                    y[i] = (int)(y[i]+rand.nextGaussian() * height/WEIGHT_DIV);
                }
                mutantPolygon = new Polygon(x,y,MAX_POLYGON_POINTS);
                mutantColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                mutantMP.add(new MyPolygon(mutantPolygon, mutantColor));
            }else
            {
                mutantMP.add(mp);
            }
        }
        child.setShapes(mutantMP);
        return child;
    }
    
    public void evolve()
    {
        for(int i = 0; i < GENERATIONS; i++) {
            generateNewPopulation();
            generatePopulationFitness();
            GASolution fittest = getBestChromosome();
            canvas.setImage(fittest);
            canvas.repaint();
            System.out.println("[gen: " + i + "] [max fitness: " + fittest.fitness + "]");
        }
    }

    public void generateNewPopulation()
    {
        ArrayList<GASolution> newPop = new ArrayList<>();
        double cross_prob = Math.random();
        double mut_rate = Math.random();
        double parent_carry = Math.random();
        GASolution parent1, parent2;
        for(int i = 0; i < POP_SIZE; i++)
        {
            parent1 = pickFitParent();
            parent2 = pickFitParent();
            if(cross_prob < CROSSOVER_RATE)
            {
                GASolution child = crossOver(parent1, parent2);
                child = mutate(child);
                newPop.add(child);
            }
            else
            {
                GASolution carryOn = parent_carry > 0.5 ? parent1 : parent2;
                newPop.add(carryOn);
            }
        }
        population = newPop;
    }
    
    public static void main(String[] args) throws IOException 
    {
        String realPictureFilename = "test.jpg";
        BufferedImage realPicture = ImageIO.read(new File(realPictureFilename));
        JFrame frame = new JFrame();
        frame.setSize(realPicture.getWidth(), realPicture.getHeight());
        frame.setTitle("GA Simulation of Art");
        GACanvas theCanvas = new GACanvas(realPicture.getWidth(), realPicture.getHeight());
        frame.add(theCanvas);
        frame.setVisible(true);
        GA pt = new GA(theCanvas, realPicture);
        pt.evolve();
    }
}