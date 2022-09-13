package model;

public class Fighter {

    private String name;
    private double health;
    private double attack;
    private double defense;
    private double critChance;

    public Fighter(String name, double health, double attack, double defense, double critChance) {
        this.name = name;
        this.health = health * 6;
        this.attack = attack;
        this.defense = defense;
        this.critChance = critChance / 1000;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public double getCritChance() {
        return critChance;
    }

    public void setCritChance(double critChance) {
        this.critChance = critChance;
    }

    @Override
    public String toString() {
        return "Fighter{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", attack=" + attack +
                ", defense=" + defense +
                ", critChance=" + critChance +
                '}';
    }
}


