package fighting;

import model.Action;
import model.Fighter;
import model.User;

import java.io.IOException;
import java.util.*;

public class Arena {

    List<User> userList = new ArrayList<>();
    List<User> deadFightersList = new ArrayList<>();

    public synchronized void addUser(User user) {
        userList.add(user);
    }

    public synchronized void startFight() {
        Random random = new Random();
        broadCastMessage("Starting the Arena. May the best one win!");
        List<User> attackerList = new ArrayList<>();

        while (userList.size() > 1) {
            User attacker = getAttacker(random, attackerList);
            Action action = findAction(attacker);

            if (action.getActionType() == Action.ActionType.USER) {
                User target = getUserByActionTarget(action);

                if (target == null) {
                    continue;
                }

                evaluateAttack(attacker, target, random);
                target.getOutputStream().println("You have " + target.getFighter().getHealth() + " health left!");

                if (target.getFighter().getHealth() <= 0) {
                    broadCastMessage("Fighter " + target.getFighter().getName() + " has been removed from the Arena. RIP in pieces");
                    userList.remove(target);
                    deadFightersList.add(target);
                }
            } else {
                evaluateStatusChange(action);
            }
        }

        broadCastMessage("exit");
    }

    private void evaluateStatusChange(Action action) {
        if (action.getActionType() == Action.ActionType.ATKBUFF) {
            User user = getUserByActionTarget(action);
            if (user == null) {
                return;
            }

            Fighter fighter = user.getFighter();
            fighter.setAttack(fighter.getAttack() * 1.25);
            broadCastMessage(fighter.getName() + " has increased his attack by 20%");
            user.getOutputStream().println("Now you have " + fighter.getAttack() + " attack.");
        }

        if (action.getActionType() == Action.ActionType.DEFBUFF) {
            User user = getUserByActionTarget(action);
            if (user == null) {
                return;
            }

            Fighter fighter = user.getFighter();
            fighter.setDefense(fighter.getDefense() * 1.25);
            broadCastMessage(fighter.getName() + " has increased his defense by 20%");
            user.getOutputStream().println("Now you have " + fighter.getDefense() + " defense.");
        }

        if (action.getActionType() == Action.ActionType.ATKDEBUFF) {
            User user = getUserByActionTarget(action);
            if (user == null) {
                return;
            }

            for (User target : userList) {
                if (target != user) {
                    Fighter targetF = target.getFighter();
                    targetF.setAttack(targetF.getAttack() * 0.85);
                    target.getOutputStream().println("Now you have only " + targetF.getAttack());
                }
            }

            broadCastMessage(user.getFighter().getName() + " has reduced everyone's attack by 10%");
        }

        if (action.getActionType() == Action.ActionType.DEFDEBUFF) {
            User user = getUserByActionTarget(action);
            if (user == null) {
                return;
            }

            for (User target : userList) {
                if (target != user) {
                    Fighter targetF = target.getFighter();
                    targetF.setDefense(targetF.getDefense() * 0.85);
                    target.getOutputStream().println("Now you have only " + targetF.getDefense());
                }
            }

            broadCastMessage(user.getFighter().getName() + " has reduced everyone's defense by 10%");
        }
    }

    private User getUserByActionTarget(Action action) {
        for (User user : userList) {
            if (action.getTargetName().equals(user.getFighter().getName())) {
                return user;
            }
        }

        return null;
    }

    private User getAttacker(Random random, List<User> attackerList) {
        // Sepehr: 2 Züge vorsprung maximum

        int attackerIndex = random.nextInt(userList.size());
        User attacker = userList.get(attackerIndex);
        int size = attackerList.size();

        if (size > 2) {
            boolean attackedOnce = attacker == attackerList.get(size - 1);
            boolean attackedTwice = attacker == attackerList.get(size - 2);

            if (attackedOnce && attackedTwice) {
                return getAttacker(random, attackerList);
            }
        }

        attackerList.add(attacker);

        return attacker;
    }

    private void evaluateAttack(User attacker, User target, Random random) {
        double attackFactor = random.nextDouble(0.2, 1.0);
        double defenseFactor = random.nextDouble(0.4, 1.2);
        double critRoll = random.nextDouble();

        Fighter aF = attacker.getFighter();
        Fighter tF = target.getFighter();
        boolean hasCrit = aF.getCritChance() > critRoll;
        double attackDamage = attackFactor * aF.getAttack() * (hasCrit ? 2 : 1);
        double inflictedDamage = Math.round(Math.max(0, attackDamage - defenseFactor * tF.getDefense()));
        tF.setHealth(tF.getHealth() - inflictedDamage);

        if (hasCrit) {
            broadCastMessage(aF.getName() + " has inflicted " + inflictedDamage + " critical damage to " + tF.getName());
        } else {
            broadCastMessage(aF.getName() + " has inflicted " + inflictedDamage + " to " + tF.getName());
        }

        System.out.println(aF.getName() + " has attacked " + tF.getName());
    }

    public void broadCastMessage(String message) {
        for (User user : userList) {
            user.getOutputStream().println(message);
        }
        for (User user : deadFightersList) {
            user.getOutputStream().println(message);
        }
    }

    public Action findAction(User attacker) {
        String actionName = "";
        attacker.getOutputStream().println("You are the attacker. Type the name of the person you want to attack or the status change you want to apply.");
        for (User user : userList) {
            if (user != attacker) {
                attacker.getOutputStream().println(user.getFighter().getName());
            }
        }

        // Ajub 1 : Berserkerbuff - 20% aktuelle Health opfern, 50% defense ignorieren + 5% attk reduzieren
        // Ajub 2 : Schild - Kein Schaden für eine Runde, buff verbraucht

        // Kein Cooldown
        attacker.getOutputStream().println("AtkDebuff - Debuff everyone's attack by 15%");
        attacker.getOutputStream().println("DefDebuff - Debuff everyone's defense by 15%");

        // 1 Runde Cooldown
        attacker.getOutputStream().println("AtkBuff - Buff your own attack by 25%");
        attacker.getOutputStream().println("DefBuff - Buff your own defense by 25%");
        attacker.getOutputStream().println("Schildbuff - Buff ");

        // Kein Cooldown
        attacker.getOutputStream().println("");
        // Crhis: Cooldowns by buffs

        try {
            while (true) {
                actionName = attacker.getInputStream().readLine();
                String attackerName = attacker.getFighter().getName();

                if (actionName.equals("AtkDebuff")) {
                    return new Action(attackerName, Action.ActionType.ATKDEBUFF);
                }

                if (actionName.equals("DefDebuff")) {
                    return new Action(attackerName, Action.ActionType.DEFDEBUFF);
                }

                if (actionName.equals("AtkBuff")) {
                    return new Action(attackerName, Action.ActionType.ATKBUFF);
                }

                if (actionName.equals("DefBuff")) {
                    return new Action(attackerName, Action.ActionType.DEFBUFF);
                }

                for (User user : userList) {
                    if (user.getFighter().getName().equals(actionName) && user != attacker) {
                        broadCastMessage(attacker.getFighter().getName() + " is attacking: " + actionName);
                        return new Action(user.getFighter().getName(), Action.ActionType.USER);
                    }
                }

                broadCastMessage("That command does not exist. Try again");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
