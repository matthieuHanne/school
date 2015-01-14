/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 06/05/2014
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */

import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.ObjectGrid2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.util.Int2D;

public class Beings extends SimState {
    public static int GRID_SIZE = 50;
    public static int NUM_INSECT = 10;
    //public static int NUM_FOOD_CELL = 500;
    public SparseGridPortrayal2D yard =
            new SparseGridPortrayal2D(

            );

    public Beings(long seed) {
        super(seed);
    }
    public void start() {
        super.start();
        yard.clear();
        addAgentsInsecte();
    }

    private void addAgentsInsecte() {
        for(int i = 0; i < NUM_INSECT; i++) {
            Int2D location = new Int2D(random.nextInt(yard.getWidth()),
                    random.nextInt(yard.getHeight()) );
            Object ag = null;
            while ((ag = yard.get(location.x,location.y)) != null) {
                location = new Int2D(random.nextInt(yard.getWidth()),
                        random.nextInt(yard.getHeight()));
            }
            addAgentInsecte(location.x, location.y);
        }

    }

    public void addAgentInsecte(int x, int y) {
        Int2D location = new Int2D(x,y);
        Insecte a = new Insecte(location.x, location.y);
        yard.set(location.x,location.y,a);
        a.x = location.x;
        a.y = location.y;

        schedule.scheduleRepeating(a);
    }

    public boolean free(int x, int y) {
        if(x<0 || x>GRID_SIZE || y<0 || y>GRID_SIZE) return false;
        return yard.get(x, y)==null;
    }
}






























   /*
    public void start() {
        super.start();
        yard.clear();
        addAgentsInsecte();
        addAgentsNourriture();
    }

    private void addAgentsNourriture() {
        for(int i = 0; i < NUM_FOOD_CELL; i++) {
            addAgentNourriture();
        }

//addAgentNourriture(1, 10);
//addAgentNourriture(GRID_SIZE-2, 4);
//addAgentNourriture(4, 1);
//addAgentNourriture(10, GRID_SIZE-2);
    }

    public void addAgentNourriture() {
        Int2D location = new Int2D(random.nextInt(yard.getWidth()),
                random.nextInt(yard.getHeight()) );
        Object ag = null;
        while ((ag = yard.get(location.x,location.y)) != null) {
            location = new Int2D(random.nextInt(yard.getWidth()),
                    random.nextInt(yard.getHeight()) );
        }
        addAgentNourriture(location.x, location.y);
    }

    public void addAgentNourriture(int x, int y) {
        Int2D location = new Int2D(x,y);
        Nourriture a = new Nourriture(location.x,location.y);
        yard.set(location.x,location.y,a);
        a.x = location.x;
        a.y = location.y;

        Stoppable stoppable=schedule.scheduleRepeating(a);
        a.stoppable=stoppable;
    }

    private void addAgentsInsecte() {
        for(int i = 0; i < NUM_INSECT; i++) {
            Int2D location = new Int2D(random.nextInt(yard.getWidth()),
                    random.nextInt(yard.getHeight()) );
            Object ag = null;
            while ((ag = yard.get(location.x,location.y)) != null) {
                location = new Int2D(random.nextInt(yard.getWidth()),
                        random.nextInt(yard.getHeight()) );
            }
            addAgentInsecte(location.x, location.y);
        }
//addAgentInsecte(GRID_SIZE-1, 10);
//addAgentInsecte(0, 4);
//addAgentInsecte(4, GRID_SIZE-1);
//addAgentInsecte(10, 0);
    }

    public void addAgentInsecte(int x, int y) {
        Int2D location = new Int2D(x,y);
        Insecte a = new Insecte(location.x, location.y);
        yard.set(location.x,location.y,a);
        a.x = location.x;
        a.y = location.y;

        Stoppable stoppable=schedule.scheduleRepeating(a);
        a.stoppable=stoppable;
    }

    public boolean free(int x, int y) {
        if(x<0 || x>GRID_SIZE || y<0 || y>GRID_SIZE) return false;
        return yard.get(x, y)==null;
    }
}*/