package me.wbean.ya.yakv.command;

public class CommandFactory {

    enum CommandEnum{
        /**
         * 心跳检测命令
         */
        HELLO(new HeartBeatCommand()),

        /**
         * get
         */
        GET(new GetCommand()),

        /**
         * set
         */
        SET(new SetCommand()),
        ;

        Command command;

        private CommandEnum(Command command){
            this.command = command;
        }

        public Command getCommand(){
            return this.command;
        }
    }

    public static Command getCommand(String key){
        try {
            return CommandEnum.valueOf(key.toUpperCase()).getCommand();
        }catch (Exception e){
            return null;
        }
    }
}
