package chum.gl;

import java.util.ArrayList;


/**
  Mock OpenGL ES implementation used for testing.  All GL calls get logged in a list
  for checking afterward.
*/
abstract class MockGL {

    /** The list of commands executed */
    public ArrayList<String> commands = new ArrayList<String>();


    public void add(String cmd) {
        commands.add(cmd);
    }


    public int numCommands() {
        return commands.size();
    }


    public String get(int i) {
        return commands.get(i);
    }


    public void clear() {
        commands.clear();
    }


    /** Test whether the command list contains a specific
        set of strings.  They have to be in the specified order, but not
        necessarily consecutive */
    public boolean contains(String ... list) {
        int base = -1;
        for ( int l=0; l < list.length; ++l ) {
            boolean found = false;
            for( int c=base; c<commands.size(); ++c ) {
                if ( commands.get(c).equals(list[l]) ) {
                    found = true;
                    break;
                }
            }
            if ( !found ) return false;
        }
        return true;
    }


}