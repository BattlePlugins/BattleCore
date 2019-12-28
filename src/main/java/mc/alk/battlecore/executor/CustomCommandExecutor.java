package mc.alk.battlecore.executor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import mc.alk.battlecore.message.MessageController;
import mc.alk.battlecore.util.Log;
import mc.alk.battlecore.util.PlayerUtil;
import mc.alk.battlecore.util.StringUtil;

import org.battleplugins.api.command.Command;
import org.battleplugins.api.command.CommandExecutor;
import org.battleplugins.api.command.CommandSender;
import org.battleplugins.api.command.ConsoleCommandSender;
import org.battleplugins.api.entity.living.player.OfflinePlayer;
import org.battleplugins.api.entity.living.player.Player;
import org.battleplugins.api.message.MessageStyle;

// FIXME: Ugly use of optionals and class cleanup
public class CustomCommandExecutor implements CommandExecutor {

    private Map<String, TreeMap<Integer, MethodWrapper>> methods = new HashMap<>();
    private Map<String, Map<String, TreeMap<Integer, MethodWrapper>>> subCmdMethods = new HashMap<>();

    private static boolean DEBUG = false;
    private static final String DEFAULT_CMD = "_dcmd_";
    private static final int LINES_PER_PAGE = 8;
    private static final String ONLY_INGAME = MessageStyle.RED + "You need to be in game to use this command";

    protected PriorityQueue<MethodWrapper> usage = new PriorityQueue<>(2, (mw1, mw2) -> {
        MCCommand cmd1 = mw1.getCommand();
        MCCommand cmd2 = mw2.getCommand();

        int c = Float.compare(cmd1.helpOrder(), cmd2.helpOrder());
        if (c != 0)
            return c;
        c = Integer.compare(cmd1.order(), cmd2.order());
        return c != 0 ? c : Integer.compare(cmd1.hashCode(), cmd2.hashCode());
    });

	protected CustomCommandExecutor(){
		addMethods(this, getClass().getMethods());
	}

    /**
     * Custom arguments class so that we can return a modified arguments
     */
    public static class Arguments {

        private Object[] args;
    }

    protected static class MethodWrapper {

        private Object obj; // Object instance the method belongs to
        private Method method; // Method
        private String usage;

        public MethodWrapper(Object obj, Method method) {
            this.obj = obj;
            this.method = method;
        }

        private MCCommand getCommand(){
            return this.method.getAnnotation(MCCommand.class);
        }
    }

    /**
     * When no arguments are supplied, no method is found
     * What to display when this happens
     * @param sender
     */
    protected void showHelp(CommandSender sender, Command command) {
        showHelp(sender,command,null);
    }

    protected void showHelp(CommandSender sender, Command command, String[] args) {
        help(sender, command, args);
    }

    public void addMethods(Object obj, Method[] methodArray) {
        for (Method method : methodArray){
            MCCommand command = method.getAnnotation(MCCommand.class);
            if (command == null)
                continue;

            Class<?>[] types = method.getParameterTypes();
            if (types.length == 0 || !CommandSender.class.isAssignableFrom(types[0])){
                System.err.println("MCCommands must start with a CommandSender,Player, or ArenaPlayer");
                continue;
            }
            if (command.cmds().length == 0){ /// There is no subcommand. just the command itself with arguments
                addMethod(obj, method, command, DEFAULT_CMD);
            } else {
                /// For each of the cmds, store them with the method
                for (String cmd : command.cmds()){
                    addMethod(obj, method, command, cmd.toLowerCase());}
            }
        }
    }

    private void addMethod(Object obj, Method method, MCCommand command, String cmd) {
        int methodLength = method.getParameterTypes().length;
        if (command.subCmds().length == 0){
            TreeMap<Integer, MethodWrapper> methods = this.methods.get(cmd);
            if (methods == null){
                methods = new TreeMap<>();
            }
            int order = (command.order() != -1 ? command.order() * 100000 : Integer.MAX_VALUE) - methodLength * 100 - methods.size();
            MethodWrapper mw = new MethodWrapper(obj, method);
            methods.put(order, mw);
            this.methods.put(cmd, methods);
            addUsage(mw, command);
        } else {
            Map<String, TreeMap<Integer, MethodWrapper>> baseMethods = subCmdMethods.computeIfAbsent(cmd, key -> new HashMap<>());
            for (String subcmd : command.subCmds()){
                TreeMap<Integer, MethodWrapper> methods = baseMethods.computeIfAbsent(subcmd, key -> new TreeMap<>());
                int order = command.order() != -1 ? command.order() * 100000 : Integer.MAX_VALUE - methodLength * 100 - methods.size();
                MethodWrapper mw = new MethodWrapper(obj, method);
                methods.put(order, mw);
                addUsage(mw, command);
            }
        }
    }
    private void addUsage(MethodWrapper method, MCCommand command) {
        /// save the usages, for showing help messages
        if (!command.usage().isEmpty()){
            method.usage = command.usage();
        } else { /// Generate an automatic usage string
            method.usage = createUsage(method.method);
        }
        usage.add(method);
    }

    private String createUsage(Method method) {
        MCCommand cmd = method.getAnnotation(MCCommand.class);
        StringBuilder sb = new StringBuilder(cmd.cmds().length > 0 ? cmd.cmds()[0] +" " : "");
        int startIndex = 1;
        if (cmd.subCmds().length > 0){
            sb.append(cmd.subCmds()[0]).append(" ");
            startIndex = 2;
        }
        Class<?>[] types = method.getParameterTypes();
        for (int i = startIndex; i < types.length; i++){
            Class<?> theclass = types[i];
            sb.append(getUsageString(theclass));
        }
        return sb.toString();
    }

    protected String getUsageString(Class<?> clazz) {
        if (Player.class.isAssignableFrom(clazz)){
            return "<player> ";
        } else if (OfflinePlayer.class.isAssignableFrom(clazz)){
            return "<player> ";
        } else if (String.class.isAssignableFrom(clazz)){
            return "<string> ";
        } else if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)){
            return "<int> ";
        } else if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)){
            return "<number> ";
        } else if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)){
            return "<number> ";
        } else if (Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)){
            return "<int> ";
        } else if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)){
            return "<true|false> ";
        } else if (String[].class.isAssignableFrom(clazz) || Object[].class.isAssignableFrom(clazz)){
            return "[string ... ] ";
        }
        return "<string> ";
    }

    public static class CommandException {
        private final IllegalArgumentException err;
        private final MethodWrapper mw;

        public CommandException(IllegalArgumentException err, MethodWrapper mw){
            this.err = err;
            this.mw = mw;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TreeMap<Integer, MethodWrapper> methodmap = null;

        /// No method to handle, show some help
        if ((args.length == 0 && !methods.containsKey(DEFAULT_CMD))
                || (args.length > 0 && (args[0].equals("?") || args[0].equals("help")))) {
            showHelp(sender, command,args);
            return true;
        }
        final int length = args.length;
        final String cmd = length > 0 ? args[0].toLowerCase() : null;
        final String subcmd = length > 1 ? args[1].toLowerCase() : null;
        int startIndex = 0;

        /// check for subcommands
        if (subcmd != null && subCmdMethods.containsKey(cmd) && subCmdMethods.get(cmd).containsKey(subcmd)){
            methodmap = subCmdMethods.get(cmd).get(subcmd);
            startIndex = 2;
        }
        if (methodmap == null && cmd != null) { /// Find our method, and verify all the annotations
            methodmap = methods.get(cmd);
            if (methodmap != null)
                startIndex =1;
        }

        if (methodmap == null) { /// our last attempt
            methodmap = methods.get(DEFAULT_CMD);
        }

        if (methodmap == null || methodmap.isEmpty()){
            return MessageController.sendMessage(sender, "&cThat command does not exist!&6 /"+command.getLabel()+" help &cfor help");}

        MCCommand mccmd;
        List<CommandException> errs = null;
        boolean success = false;
        for (MethodWrapper mwrapper : methodmap.values()){
            mccmd = mwrapper.method.getAnnotation(MCCommand.class);
            final boolean isOp = sender == null || sender.isOp() || sender instanceof ConsoleCommandSender;

            if (mccmd.op() && !isOp || mccmd.admin() && !hasAdminPerms(sender)) /// no op, no pass
                continue;

            Arguments newArgs = null;
            try {
                newArgs = verifyArgs(mwrapper, mccmd, sender, command, label, args, startIndex);
                Object completed = mwrapper.method.invoke(mwrapper.obj, newArgs.args);
                if (!(completed instanceof Boolean)) {
					success = true;
					continue;
				}
                success = (Boolean) completed;
                if (success)
                	continue;

                String usage = mwrapper.usage;
                if (usage != null && !usage.isEmpty()){
                	MessageController.sendMessage(sender, usage);

                }
                break; /// success on one
            } catch (IllegalArgumentException e) { /// One of the arguments wasn't correct, store the message
                if (errs == null)
                    errs = new ArrayList<>();

                errs.add(new CommandException(e, mwrapper));
            } catch (Exception e) { /// Just all around bad
                logInvocationError(e, mwrapper, newArgs);
            }
        }
        /// and handle all errors
        if (!success && errs != null && !errs.isEmpty()){
            Set<String> usages = new HashSet<>();
            for (CommandException e: errs){
                usages.add(MessageStyle.GOLD + "/" + command.getLabel() + " " + e.mw.usage + " &c:" + e.err.getMessage());
            }
            for (String msg : usages){
                MessageController.sendMessage(sender, msg);}
        }
        return true;
    }

    private void logInvocationError(Exception e, MethodWrapper mwrapper, Arguments newArgs) {
        System.err.println("[CustomCommandExecutor Error] " + mwrapper.method + " : " + mwrapper.obj + "  : " + newArgs);
        if (newArgs != null && newArgs.args != null){
            for (Object o: newArgs.args)
                System.err.println("[Error] object=" + (o != null ? o.toString() : null));
        }
        System.err.println("[Error] Cause=" + e.getCause());
        if (e.getCause() != null) e.getCause().printStackTrace();
        System.err.println("[Error] Trace Continued ");
        e.printStackTrace();
    }

    protected Arguments verifyArgs(MethodWrapper mwrapper, MCCommand cmd, CommandSender sender,
                                   Command command, String label, String[] args, int startIndex) throws IllegalArgumentException{

        if (DEBUG) {
            Log.info(" method=" + mwrapper.method.getName() + " verifyArgs " + cmd + " sender=" + sender +
                    ", label=" + label + " args=" + Arrays.toString(args));
            for (String arg : args) {
                Log.info(" -- arg=" + arg);
            }
            for (Class<?> t : mwrapper.method.getParameterTypes()) {
                Log.info(" -- type=" + t);
            }
        }

        final int paramLength = mwrapper.method.getParameterTypes().length;

        /// Check our permissions
        if (!cmd.perm().isEmpty() && !sender.hasPermission(cmd.perm()) && !(cmd.admin() && hasAdminPerms(sender)))
            throw new IllegalArgumentException("You don't have permission to use this command");

        /// Verify min number of arguments
        if (args.length < cmd.min()){
            throw new IllegalArgumentException("You need at least "+cmd.min()+" arguments");
        }
        /// Verfiy max number of arguments
        if (args.length > cmd.max()){
            throw new IllegalArgumentException("You need less than "+cmd.max()+" arguments");
        }
        /// Verfiy max number of arguments
        if (cmd.exact()!= -1 && args.length != cmd.exact()){
            throw new IllegalArgumentException("You need exactly "+cmd.exact()+" arguments");
        }
        boolean isPlayer = sender instanceof Player;
        boolean isOp = (isPlayer && sender.isOp()) || sender == null || sender instanceof ConsoleCommandSender;

        if (cmd.op() && !isOp)
            throw new IllegalArgumentException("You need to be op to use this command");

        if (cmd.admin() && !isOp && (isPlayer && !hasAdminPerms(sender)))
            throw new IllegalArgumentException("You need to be an Admin to use this command");

        Class<?>[] types = mwrapper.method.getParameterTypes();

        //		/// In game check
        if (types[0] == Player.class && !isPlayer){
            throw new IllegalArgumentException(ONLY_INGAME);
        }
        int strIndex = startIndex, objIndex = 1;

        Arguments newArgs = new Arguments(); /// Our return value
        Object[] objs = new Object[paramLength]; /// Our new array of castable arguments

        newArgs.args = objs; /// Set our return object with the new castable arguments
        objs[0] = verifySender(sender, types[0]);
        AtomicInteger numUsedStrings = new AtomicInteger(0);
        for (int i = 1; i < types.length; i++){
            Class<?> clazz = types[i];
            try{
                if (CommandSender.class.isAssignableFrom(clazz)){
                    objs[objIndex] = sender;
                } else if (Map.class.isAssignableFrom(clazz)) {
                    Map<Integer, String> map = new HashMap<>();
                    int mapIndex = 0;
                    for (String s : args) {
                        map.put(mapIndex, s);
                        mapIndex = mapIndex + 1;
                    }
                    objs[objIndex] = map;
                } else if (Set.class.isAssignableFrom(clazz)) {
                    Set<String> set = new HashSet<>(Arrays.asList(args));
                    objs[objIndex] = set;
                } else if (List.class.isAssignableFrom(clazz)) {
                    List<String> list = Arrays.asList(args);
                    objs[objIndex] = list;
                } else if (Collection.class.isAssignableFrom(clazz)) {
                    Collection<String> c = Arrays.asList(args);
                    objs[objIndex] = c;
                } else if (String[].class.isAssignableFrom(clazz)){
                    objs[objIndex] = args;
                } else if (Object[].class.isAssignableFrom(clazz)){
                    objs[objIndex] = args;
                } else {
                    objs[objIndex] = verifyArg(sender, clazz, command, args, strIndex, numUsedStrings);
                    if (objs[objIndex] == null){
                        throw new IllegalArgumentException("Argument " + args[strIndex] + " can not be null");
                    }
                }
                if (DEBUG)
                	Log.info("   " + objIndex + " : " + strIndex + "  "
							+ (args.length > strIndex ? args[strIndex] : null) + " <-> "
							+ objs[objIndex] +" !!! Cs = " + clazz.getCanonicalName());
                if (numUsedStrings.get() > 0) {
                    strIndex+=numUsedStrings.get();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("You didn't supply enough arguments for this method");
            }
            objIndex++;
        }

        /// Verify alphanumeric
        if (cmd.alphanum().length > 0) {
            for (int index: cmd.alphanum()) {
                if (index >= args.length)
                    throw new IllegalArgumentException("String Index out of range. ");
                if (!args[index].matches("[a-zA-Z0-9_]*")) {
                    throw new IllegalArgumentException("argument '"+args[index]+"' can only be alphanumeric with underscores");
                }
            }
        }
        return newArgs; /// Success
    }

    protected Object verifyArg(CommandSender sender, Class<?> clazz, Command command, String[] args, int curIndex, AtomicInteger numUsedStrings) {
        numUsedStrings.set(0);
        if (Command.class.isAssignableFrom(clazz)) {
            return command;
        }
        String string = args[curIndex];
        if (string == null)
            throw new ArrayIndexOutOfBoundsException();
        numUsedStrings.set(1);
        if (Player.class.isAssignableFrom(clazz)) {
            return verifyPlayer(string);
        } else if (OfflinePlayer.class.isAssignableFrom(clazz)) {
            return verifyOfflinePlayer(string);
        } else if (String.class.isAssignableFrom(clazz)) {
            return string;
        } else if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) {
            return verifyInteger(string);
        } else if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
            return Boolean.parseBoolean(string);
        } else if (Object.class.isAssignableFrom(clazz)) {
            return string;
        } else if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
            return verifyFloat(string);
        } else if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) {
            return verifyDouble(string);
        }
        return null;
    }

    protected Object verifySender(CommandSender sender, Class<?> clazz) {
        if (!clazz.isAssignableFrom(sender.getClass())){
            throw new IllegalArgumentException("Sender must be a " + clazz.getSimpleName());}
        return sender;
    }

    protected Object verifyArg(Class<?> clazz, Command command, String string, AtomicBoolean usedString) {
        if (Command.class.isAssignableFrom(clazz)){
            usedString.set(false);
            return command;
        }
        if (string == null)
            throw new ArrayIndexOutOfBoundsException();

        usedString.set(true);
        if (Player.class.isAssignableFrom(clazz)){
            return verifyPlayer(string);
        } else if (OfflinePlayer.class.isAssignableFrom(clazz)){
            return verifyOfflinePlayer(string);
        } else if (String.class.isAssignableFrom(clazz)){
            return string;
        } else if (Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)){
            return verifyInteger(string);
        } else if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)){
            return Boolean.parseBoolean(string);
        } else if (Object.class.isAssignableFrom(clazz)){
            return string;
        } else if (Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)){
            return verifyFloat(string);
        } else if (Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)){
            return verifyDouble(string);
        }
        return null;
    }

    private OfflinePlayer verifyOfflinePlayer(String name) throws IllegalArgumentException {
        OfflinePlayer player = PlayerUtil.findOfflinePlayer(name).orElse(null);
        if (player == null)
            throw new IllegalArgumentException("Player " + name + " can not be found");
        return player;
    }

    private Player verifyPlayer(String name) throws IllegalArgumentException {
        Player player = PlayerUtil.findPlayer(name).orElse(null);
        if (player == null || !player.isOnline())
            throw new IllegalArgumentException(name+" is not online ");
        return player;
    }

    private int verifyInteger(Object object) throws IllegalArgumentException {
        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageStyle.RED+(String)object+" is not a valid integer.");
        }
    }

    private float verifyFloat(Object object) throws IllegalArgumentException {
        try {
            return Float.parseFloat(object.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageStyle.RED+ object.toString() +" is not a valid float.");
        }
    }

    private double verifyDouble(Object object) throws IllegalArgumentException {
        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MessageStyle.RED+ object.toString() +" is not a valid double.");
        }
    }

    protected boolean hasAdminPerms(CommandSender sender){
        return sender.isOp();
    }

    public void help(CommandSender sender, Command command, String[] args){
        int page = 1;

        if (args != null && args.length > 1){
            try{
                page = Integer.parseInt(args[1]);
            } catch (Exception e){
                MessageController.sendMessage(sender, MessageStyle.RED+" " + args[1] +" is not a number, showing help for page 1.");
            }
        }

        List<String> available = new ArrayList<>();
        List<String> unavailable = new ArrayList<>();
        List<String> onlyop = new ArrayList<>();

        for (MethodWrapper mw : usage){
            MCCommand cmd = mw.getCommand();
            String use = "&6/" + command.getLabel() + " " + mw.usage;
            if (cmd.op() && !sender.isOp())
                continue;

            if (cmd.admin() && !hasAdminPerms(sender))
                continue;

            if (!cmd.perm().isEmpty() && !sender.hasPermission(cmd.perm()))
                unavailable.add(use);
            else
                available.add(use);
        }
        int npages = available.size()+unavailable.size();
        if (sender.isOp())
            npages += onlyop.size();
        npages = (int) Math.ceil((float) npages / LINES_PER_PAGE);
        if (page > npages || page <= 0){
            MessageController.sendMessage(sender, "&4That page doesnt exist, try 1-"+npages);
            return;
        }
        if (command != null && command.getAliases() != null && !command.getAliases().isEmpty()) {
            String aliases = StringUtil.join(command.getAliases(),", ");
            MessageController.sendMessage(sender, "&eShowing page &6"+page +"/"+npages +"&6 : /"+command.getLabel()+" help <page number>");
            MessageController.sendMessage(sender, "&e    command &6"+command.getLabel()+"&e has aliases: &6" + aliases);
        } else {
            MessageController.sendMessage(sender, "&eShowing page &6"+page +"/"+npages +"&6 : /" + command.getLabel() + " help <page number>");
        }
        int i = 0;
        for (String use : available){
            i++;
            if (i < (page - 1) * LINES_PER_PAGE || i >= page * LINES_PER_PAGE)
                continue;

            MessageController.sendMessage(sender, use);
        }
        for (String use : unavailable){
            i++;
            if (i < (page - 1) * LINES_PER_PAGE || i >= page * LINES_PER_PAGE)
                continue;

            MessageController.sendMessage(sender, MessageStyle.RED + "[Insufficient Perms] " + use);
        }
        if (sender.isOp()){
            for (String use : onlyop){
                i++;
                if (i < (page-1) * LINES_PER_PAGE || i >= page * LINES_PER_PAGE)
                    continue;

                MessageController.sendMessage(sender, MessageStyle.AQUA + "[OP only] &6" + use);
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MCCommand {
        /// the cmd and all its aliases, can be blank if you want to do something when they just type
        /// the command only
        String[] cmds() default {};

        /// subCommands
        String[] subCmds() default {};

        /// Verify the number of parameters, inGuild and notInGuild imply min if they have an index > number of args
        int min() default 0;
        int max() default Integer.MAX_VALUE;
        int exact() default -1;

        int order() default -1;
        float helpOrder() default Integer.MAX_VALUE;
        boolean admin() default false; /// admin
        boolean op() default false; /// op

        String usage() default "";
        String usageNode() default "";
        String perm() default ""; /// permission node
        int[] alphanum() default {}; /// only alpha numeric

        boolean selection() default false;	/// Selected arena
        int[] ports() default {};
    }
}