package ass;

import ass.communication.GameContext;
import ass.communication.JsonUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Random;

public class Test {
    public static void main(String[] args){

        GameContext gc = new GameContext();
        gc.setCurrentUser("TPA");
        gc.setGameBoard(new Character[20][20]);
        Random r = new Random();
        for(int i = 0; i< 20; i++){
            for(int j = 0; j<20;j++){
                gc.getGameBoard()[i][j] = (char)(r.nextInt((90 - 65) + 1) + 65);
            }
        }
        String jsonStr = JsonUtility.toJson(gc);
        System.out.println(jsonStr);

        gc = JsonUtility.fromJson(jsonStr, GameContext.class);
        System.out.println("Back to object, current user = " + gc.getCurrentUser());
    }
}