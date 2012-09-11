/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

/**
 *
 * @author Tyrone
 */
public class BigTriangle {
    SmallTriangle[] subTriangles = new SmallTriangle[3];
    int[] upTriangle = {4,2,6};
    int[] downTriangle = {5,7,3};
    
    public BigTriangle(int level, int triangleNumber, Boolean[][] relayTable) {
        // Check if level is even or odd
        if(level % 2 == 0) { // even level
            // Check if triangleNumber is even or odd
            if(triangleNumber % 2 == 0) { //even
                // 4, 2, 6
                setTriangles(upTriangle, level, triangleNumber, relayTable);
            }else{ // odd
                // 5, 7,3
                setTriangles(downTriangle, level, triangleNumber, relayTable);
            }
        }else{ // odd level
            // Check if triangleNumber is even or odd
            if(triangleNumber % 2 == 0) { //even
                // 5, 7,3
                setTriangles(downTriangle, level, triangleNumber, relayTable);
            }else{ // odd
                // 4, 2, 6
                setTriangles(upTriangle, level, triangleNumber, relayTable);
            }
        }
        
    }
    
    public void setTriangles(int[] triangleType, int level, int triangleNumber, Boolean[][] relayTable){
        for(int i=0; i<3; i++) {
            Boolean tempBool = new Boolean(false);
            relayTable[triangleType[i]+(level)*6-1][(triangleNumber/2)+1] = tempBool;
            subTriangles[i] = new SmallTriangle(triangleType[i],tempBool);
        }
    }
    
    
    
}
