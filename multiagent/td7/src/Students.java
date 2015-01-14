/**
 * Created with IntelliJ IDEA.
 * User: Matthieu Hanne
 * Date: 06/05/2014
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */


import sim.engine.*;
public class Students extends SimState
{
    public Students(long seed)
    {
    super(seed);
    }
    public static void main(String[] args)
    {
    doLoop(Students.class, args);
    System.exit(0);
    }
}