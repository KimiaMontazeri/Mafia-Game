package mafia.model;

import mafia.model.element.Player;
import mafia.model.gamelogic.GameManager;

import static mafia.model.element.Role.GODFATHER;
import static mafia.model.element.Role.LECTOR;

public class GodMessages
{
    public static GameData gameData = GameData.getInstance();

    public static String welcomeAll()
    {
        return null;
    }

    public static String welcomeMafias()
    {
        StringBuilder mafiaNames = new StringBuilder("----------------------");

        for (Player p : gameData.getMafias())
        {
            if (p.getRole() == GODFATHER)
                mafiaNames.append(p.getUsername()).append(" (GODFATHER)\n");
            else if (p.getRole() == LECTOR)
                mafiaNames.append(p.getUsername()).append(" (DOCTOR LECTOR)\n");
            else
                mafiaNames.append(p.getUsername()).append("\n");
        }

        mafiaNames.append("----------------------");

        return "HELLO MAFIAS!\n " +
                "Try to kill the citizens as much as you can!\n" +
                "This is the list of all the mafia team:\n" +
                mafiaNames +
                "All the mafias (except the godfather) be cautious! " +
                "Or the detective wil find out about your role!!)\n";
    }

    public static String welcomeGodfather()
    {
        return "YOU ARE THE GODFATHER OF THE MAFIA'S TEAM!!! " +
                "(THE FINAL DECISION MAKER OF YOUR TEAM)\n" +
                "Don't worry! The detective will never find out about your role :)\n";
    }

    public static String welcomeLector()
    {
        return """
                YOU ARE THE DOCTOR OF THE MAFIA'S TEAM!!!
                If the sniper shoots your team mate, you can heal them as much as you want.
                But you only get one chance to heal YOURSELF!
                Be cautious! Otherwise the detective will find out about your role!
                """;
    }

    public static String welcomeCitizens()
    {
        return "YOU ARE A CITIZEN!\n" +
                "Try to guess who is on the mafia's team and " +
                "vote for them on the election day to get them killed!\n" +
                "Remember that the detective will never find out about your role!";
    }

    public static String welcomeDoctor()
    {
        return """
                YOU ARE THE CITY'S DOCTOR, ONE OF THE MAJOR ROLES IN THE CITY'S TEAM!!!
                By guessing the mafia's murdering target, you can choose whether to heal them or not!
                You can also choose to heal yourself ONLY ONE TIME in the game!
                """;
    }

    public static String welcomeMayor(Player doctor)
    {
        return "YOU ARE THE CITY'S MAYOR!\n" +
                "Everytime a citizen gets to be the final candidate of the election," +
                "you get to choose to cancel that election to save your team mate!\n" +
                "You also get to know the name of the doctor: " + doctor.getUsername() + "\n";
    }

    public static String welcomeDetective()
    {
        return "YOU ARE THE CITY'S DETECTIVE, ONE OF THE MAJOR ROLES IN THE CITY'S TEAM!!!\n" +
                "If you get suspicious about someone being in the mafia's team, you can ask" +
                "God to tell you if your guess is true or not!\n" +
                "but keep this in mind, that if your target is GODFATHER or a normal CITIZEN," +
                "God won't tell you their role!\n";
    }

    public static String welcomeSniper()
    {
        return "YOU ARE THE CITY'S SNIPER, YOU'VE GOT SO MUCH POWER!\n" +
                "You can choose to shoot the player whom you're suspicious of!" +
                "Always be 100% sure that your target is on mafia's team. " +
                "Otherwise, you will get kicked out of the game :(\n";
    }

    public static String welcomeTherapist()
    {
        return "YOU ARE THE CITY'S THERAPIST!" +
                "You can choose to shush the player whom you think is a mafia or misguiding others";
    }

    public static String welcomeArnold()
    {
        return "YOU ARE THE CITY'S ARNOLD! YOU'VE GOT SO MUCH POWER!\n" +
                "You can ask God about the roles that have been killed in the game at night\n" +
                "But you only get 2 chances to do so...\n" +
                "You have another power too! You won't get killed the first time that " +
                "a mafia shoots you :)";
    }
}
