import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 06/05/2014
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class Insecte implements Steppable {
    public int x, y;
    public static int NBRE_POINTS=10;
    public static int CHARGE_MAX=7;

    public int distanceDeplacement=1;
    public int distancePerception=1;
    public int chargeMax=1;

    public Insecte(int a, int b) {
        int i=0;
        x=a;
        y=b;

        while(i<NBRE_POINTS){
            switch ((int)(Math.random()*3)) {
                case 0: distanceDeplacement++; ++i; break;
                case 1: distancePerception++; ++i; break;
                case 2:
                    if (chargeMax<CHARGE_MAX) {
                        chargeMax++;
                        ++i;
                    }
                    break;
            }
        }
    }

    @Override
    public void step(SimState state) {
        Beings beings = (Beings) state;
        if (friendsNum(beings)*3 < 8) {
            move(beings);
        }
    }
    protected int friendsNum(Beings beings) {
        return friendsNum(beings,x,y);
    }

    protected int friendsNum(Beings beings,int l,int c) {
        int nb = 0;
        for (int i = -1 ; i <= 1 ; i++) {
            for (int j = -1 ; j <= 1 ; j++) {
                if (i != 0 || j != 0) {
                    Int2D flocation = new Int2D(beings.yard.stx(l + i),
                            beings.yard.sty(c + j));
                    Object ag = beings.yard.get(flocation.x,flocation.y);
                    if (ag != null && ag.getClass() == this.getClass())
                        nb++;
                }
            }
        }
        return nb;
    }

    public void move(Beings beings) {
        int n = beings.random.nextInt(8);
        switch(n) {
            case 0:
                if (beings.free(x-1, y)) {
                    beings.yard.set(x, y, null);
                    beings.yard.set(beings.yard.stx(x-1), y, this);
                    x = beings.yard.stx(x-1);
                }
                break;
            }
    }
}
