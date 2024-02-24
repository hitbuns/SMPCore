package com.SMPCore.commands.SubCommands;

import com.MenuAPI.Utils;
import joptsimple.internal.Strings;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class SubCommandExecutor implements CommandExecutor, TabCompleter {

    private final Map<String,SubCommand> subCommands = new HashMap<>();
    String[] usage;
    Predicate<CommandSender> predicate;

    public SubCommandExecutor(String helpPermission,String... permissions) {
        this(helpPermission != null ? commandSender -> commandSender.hasPermission(helpPermission) : null,
                permissions != null && permissions.length > 0 ? commandSender -> Arrays.stream(permissions)
                .anyMatch(commandSender::hasPermission) : null);
    }

    public SubCommandExecutor(String... permissions) {
        this(null,permissions);
    }

    public SubCommandExecutor(Predicate<CommandSender> predicate) {
        this(null,predicate);
    }

    public SubCommandExecutor(Predicate<CommandSender> helpPredicate,Predicate<CommandSender> predicate) {
        this.predicate = predicate;

        if (!(register(Arrays.stream(SubCommandType.values()).filter(subCommandType ->
                subCommandType.getClazz().getSimpleName().equals(this.getClass().getSimpleName()))
        .map(SubCommandType::getSubCommand).toArray(SubCommand[]::new)) &&
                register(new SubCommandHelp(this,helpPredicate))))
            updateUsage();
    }

    void updateUsage() {
        usage = subCommands.values().stream().map(s ->
                Utils.color("&6/%command% "+s.getUsage())).toArray(String[]::new);
    }

    public boolean register(SubCommand... subCommands) {
        if (subCommands != null && subCommands.length > 0) {
            this.subCommands.putAll(Arrays.stream(subCommands).filter(Objects::nonNull).collect(Collectors.toMap(subCommand ->
                            subCommand.getId().toLowerCase(),
                    subCommand -> subCommand)));
            updateUsage();
            return true;
        }
        return false;
    }

    public void deregister(Predicate<SubCommand> subCommandPredicates) {
        if (subCommandPredicates != null) {
            this.subCommands.values().removeIf(subCommandPredicates);
            updateUsage();
        }
    }

    public void deregister(String... ids) {
        if (ids != null && ids.length > 0) deregister(subCommand ->
                Arrays.stream(ids).anyMatch(s -> subCommand.getId().equalsIgnoreCase(s)));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (predicate != null && !predicate.test(commandSender)) {
            commandSender.sendMessage(Utils.color("&cYou are not allowed to use this command!"));
            return true;
        }

        SubCommand subCommand = strings.length == 0 ? null : subCommands.get(strings[0].toLowerCase());

        if (subCommand == null && (strings.length > 0 || onCommandNoArgs(commandSender,command.getName()))) {
            sendUsage(commandSender,command.getName(),1);
            return true;
        }

        if (strings.length == 0) return true;

        if (subCommand.getPredicate() != null && !subCommand.getPredicate().test(commandSender)) {
            commandSender.sendMessage(Utils.color("&cYou are not allowed to use this command!"));
            return true;
        }

        String[] args = strings.length == 1 ? new String[0] : Arrays.copyOfRange(strings,1,strings.length);

        subCommand.run(commandSender,command.getName(),args);

        return true;
    }

    public abstract boolean onCommandNoArgs(CommandSender commandSender,String cmd);


    void sendUsage(CommandSender commandSender,String cmd,int page) {
        if (commandSender == null) return;

        int max = (int) Math.ceil(usage.length/10.0), a = Math.min(Math.max(1,page),max);
        if (max == 0) {
            commandSender.sendMessage(Utils.color("&cNo Usage Found"));
            return;
        }

        Player player = commandSender instanceof Player player1 ? player1 : null;

        if (player != null) {
            ComponentBuilder componentBuilder1 = new ComponentBuilder(Utils.color("&6<<"))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/"+cmd+" help "+(a-1))).event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT, new Text(Utils.color("&eBack Page")))),
                    componentBuilder2 = new ComponentBuilder(Utils.color("&6>>"))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/"+cmd+" help "+(a+1))).event(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT, new Text(Utils.color("&eNext Page"))));

            BaseComponent[] baseComponent1 = TextComponent.fromLegacyText(Utils.color("&8============")),
                    baseComponent2 = TextComponent.fromLegacyText(Utils.color("&8==&6Page "+a+" of "+max+"&8==")), baseComponent3 = baseComponent1.clone();

            List<BaseComponent> list = Arrays.stream(baseComponent1).collect(Collectors.toList());
            list.addAll(Arrays.asList(componentBuilder1.create()));
            list.addAll(Arrays.asList(baseComponent2));
            list.addAll(Arrays.asList(componentBuilder2.create()));
            list.addAll(Arrays.asList(baseComponent3));

            BaseComponent[] baseComponents = list.toArray(BaseComponent[]::new);

            player.spigot().sendMessage(baseComponents);

            a--;

            Text text = new Text(Utils.color("&eClick Me!"));

            for (int i = a*10; i < (a*10)+10; i++) {
                try {

                    String u = usage[i].replace("%command%", cmd), v = Strings.join(Arrays.copyOfRange(ChatColor.stripColor(u).split(" "),
                            0,2)," ");

                    player.spigot().sendMessage(new ComponentBuilder(Utils.color(
                            u
                    )).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            v)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            text)).create());

                } catch (Exception exception) {
                    break;
                }
            }

            player.spigot().sendMessage(baseComponents);
        } else {
            commandSender.sendMessage(Utils.color("&e&lCOMMAND USAGE"));
            commandSender.sendMessage(Arrays.stream(usage).map(s -> s.replace("%command%",cmd))
            .toArray(String[]::new));
        }


    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        SubCommand subCommand;
        List<String> list;
        return strings.length == 1 ? subCommands.keySet().stream().filter(s1 ->
                s1.toLowerCase().startsWith(strings[0].toLowerCase())).collect(Collectors.toList()) : strings.length > 1
                && (subCommand = subCommands
        .get(strings[0].toLowerCase())) != null ? ((list = subCommand.onTabComplete(commandSender,
                command.getName(),Arrays.copyOfRange(strings,1, strings.length))) != null ?
                list.stream().filter(s1 -> s1.toLowerCase().startsWith(strings[strings.length-1].toLowerCase()))
                .collect(Collectors.toList()) : null) : null;
    }
}
