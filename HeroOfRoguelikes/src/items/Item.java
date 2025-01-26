package items;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    int x, y;

    public Item(int x, int y) {
        this.x = x;
        this.y = y;
    }
}