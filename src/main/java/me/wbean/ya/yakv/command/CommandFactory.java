package me.wbean.ya.yakv.command;

public class CommandFactory {

    enum CommandEnum{
        HELLO(new HeartBeatCommand());

        Command command;

        private CommandEnum(Command command){
            this.command = command;
        }

        public Command getCommand(){
            return this.command;
        }
    }

    public static Command getCommand(String key){
        return CommandEnum.valueOf(key.toUpperCase()).getCommand();
    }
}
