import java.util.Scanner;

final class Main
{
    public static final TestCommandRegistry REGISTRY = new TestCommandRegistry();

    public static void main(String[] args)
    {
        REGISTRY.register(new CommandEcho());
        REGISTRY.register(new CommandFlagTest());
        REGISTRY.register(new CommandHelp(REGISTRY));
        REGISTRY.register(new CommandRockPaperScissors());
        REGISTRY.register(new CommandScramble());

        final Scanner scanner = new Scanner(System.in);
        while (true)
        {
            System.out.print("> ");
            final String command = scanner.nextLine();
            if (command.equalsIgnoreCase("q"))
            {
                break;
            }
            REGISTRY.process(command);
        }
        System.out.println("Quitting...");
    }
}
