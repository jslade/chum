package chum.engine;

import junit.framework.TestCase;


/**
 */
public class GameNodeTests extends TestCase {

    protected void setUp() {

    }


    public void test_findNode() {

        GameNode n1 = new GameNode().setName("n1");
        GameNode n2 = new GameNode().setName("n2");
        GameNode n3 = new GameNode().setName("n3");
        GameNode n4 = new GameNode().setName("n4");

        n1.addNode(n2);
        n1.addNode(n3.addNode(n4));

        assertEquals("N1 should have 2 children",2,n1.num_children);
        assertEquals("N2 should have 0 children",0,n2.num_children);

        assertEquals("N2 is a child of N1",n1,n2.parent);
        assertEquals("N3 is a child of N1",n1,n3.parent);
        assertEquals("N4 is a child of N3",n3,n4.parent);

        assertEquals(n1,n1.findNode("n1"));
        assertEquals(n1,n4.findNode("n1"));
        assertEquals(n4,n1.findNode("n4"));
        assertEquals(n3,n3.findNode("n3"));
        assertEquals(n2,n4.findNode("n2"));
        assertEquals(n4,n2.findNode("n4"));
    }


    public void test_findNodeHierarchical() {
        GameNode root = new GameNode().setName("root");
        GameNode a = new GameNode().setName("a");
        GameNode b = new GameNode().setName("b");
        GameNode c1 = new GameNode().setName("c");
        GameNode c2 = new GameNode().setName("c");
        GameNode d = new GameNode().setName("d");

        root.addNode(a);
        root.addNode(b);
        a.addNode(c1);
        b.addNode(c2);
        c1.addNode(d);

        assertEquals(c1,root.findNode("c"));
        assertEquals(c1,a.findNode("c"));
        assertEquals(c2,b.findNode("c"));

        assertEquals(c1,root.findNode("a.c"));
        assertEquals(c2,root.findNode("b.c"));

        assertEquals(c1,c2.findNode("a.c"));
        assertEquals(c2,c1.findNode("b.c"));

        assertEquals(d,c2.findNode("c.d"));
    }

}
