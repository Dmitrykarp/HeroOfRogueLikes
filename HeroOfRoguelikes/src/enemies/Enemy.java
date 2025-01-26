package enemies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Enemy {
    int x, y, health;

    public Enemy(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }
}