package enemies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hero {
    int x, y, health, attack;

    public Hero(int x, int y, int health, int attack) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.attack = attack;
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}