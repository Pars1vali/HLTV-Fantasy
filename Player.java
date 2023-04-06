public class Player implements Comparable<Player> {
    private String name;
    private String role;
    private int point;
    private int countUse;
    private boolean isActive;
    private int averageDamage;


    public Player(String name, String role, int point, int countUse, boolean isActive, int averageDamage) {
        this.name = name;
        this.role = role;
        this.point = point;
        this.countUse = countUse;
        this.isActive = isActive;
        this.averageDamage = averageDamage;
    }


    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public int getPoint() {
        return point;
    }

    public int getCountUse() {
        return countUse;
    }

    public boolean isActive() {return isActive; }

    public int getAverageDamage() {return averageDamage;}

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setCountUse(int countUse) {
        this.countUse = countUse;
    }

    public void setActive(boolean active) { isActive = active;}

    public void setAverageDamage(int averageDamage) {this.averageDamage = averageDamage;}

    @Override
    public int compareTo(Player o) {
        return o.getAverageDamage()-this.getAverageDamage();
    }
}
