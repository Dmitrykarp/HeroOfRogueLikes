package items;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    int x, y;
    String name;

    public Item(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
}