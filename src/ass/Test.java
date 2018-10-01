package ass;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        // GameContext gc = new GameContext();
        // gc.setCurrentUser("TPA");
        // gc.setGameBoard(new String[20][20]);
        // Random r = new Random();
        // for(int i = 0; i< 20; i++){
        // for(int j = 0; j<20;j++){
        // gc.getGameBoard()[i][j] =new Integer(r.nextInt((90 - 65) + 1) + 65).toString();
        // }
        // }
        // String jsonStr = JsonUtility.toJson(gc);
        // System.out.println(jsonStr);
        //
        // gc = JsonUtility.fromJson(jsonStr, GameContext.class);
        // System.out.println("Back to object, current user = " + gc.getCurrentUser());
        //
        // ServerMessage sm = new ServerMessage();
        // sm.setGameContext(gc);
        // sm.setType(ServerMessage.Type.REQUEST);
        // sm.setMessage("How are You?");
        // sm.setTime(new Date().getTime());
        //
        // System.out.println(JsonUtility.toJson(sm));
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");
        users.add("user4");
        System.out.println(users);
        System.out.println(getNextUser(users, "user1"));
        System.out.println(getNextUser(users, "user2"));
        System.out.println(getNextUser(users, "user3"));
        System.out.println(getNextUser(users, "user4"));
    }

    private static String getNextUser(List<String> gamingUsers, String currentUser) {
        List<String> userList = gamingUsers;
        String uid = "";
        if (userList.contains(currentUser)) {
            int i = userList.indexOf(currentUser);
            if (i < userList.size() - 1) {
                uid = userList.get(i + 1);
            } else {
                uid = userList.get(0);
            }
        }
        return uid;
    }

}
