import enemies.Enemy;
import enemies.Hero;
import items.Item;
import map.GameMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeroOfRoguelikesUI extends JFrame {
    private static final int TILE_SIZE = 32;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private final Hero hero;
    private final GameMap gameMap;
    private List<Enemy> enemies =new ArrayList<Enemy>();
    private java.util.List<Item> items= new ArrayList<Item>();;

    public HeroOfRoguelikesUI() {
        setTitle("Hero of Roguelikes");
        setSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        hero = new Hero(1, 1, 20, 5);
        gameMap = new GameMap(WIDTH, HEIGHT);

        startNewLevel();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                processInput(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        setVisible(true);
    }

    private void startNewLevel() {
        gameMap.generateMapWithRooms();
        placeHero();
        placeEnemies(5); // Количество врагов
        placeItems(3);   // Количество предметов
    }

    private void placeHero() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (gameMap.getTile(x, y) == '.') {
                    hero.move(x, y);
                    gameMap.setTile(x, y, '@');
                    return;
                }
            }
        }
    }

    private void placeEnemies(int count) {
        enemies.clear();
        for (int i = 0; i < count; i++) {
            int[] pos = getRandomEmptyTile();
            enemies.add(new Enemy(pos[0], pos[1], 10, 3));
            gameMap.setTile(pos[0], pos[1], 'E'); // 'E' для врагов
        }
    }

    private void placeItems(int count) {
        items.clear();
        for (int i = 0; i < count; i++) {
            int[] pos = getRandomEmptyTile();
            items.add(new Item(pos[0], pos[1], "Potion")); // Все предметы — зелья
            gameMap.setTile(pos[0], pos[1], 'I'); // 'I' для предметов
        }
    }

    private int[] getRandomEmptyTile() {
        Random random = new Random();
        while (true) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (gameMap.getTile(x, y) == '.') {
                return new int[]{x, y};
            }
        }
    }

    private void processInput(int keyCode) {
        int newX = hero.getX();
        int newY = hero.getY();

        switch (keyCode) {
            case KeyEvent.VK_W:
                newY--;
                break;
            case KeyEvent.VK_S:
                newY++;
                break;
            case KeyEvent.VK_A:
                newX--;
                break;
            case KeyEvent.VK_D:
                newX++;
                break;
        }

        char tile = gameMap.getTile(newX, newY);
        if (tile == '.' || tile == 'X' || tile == 'E' || tile == 'I') {
            gameMap.setTile(hero.getX(), hero.getY(), '.');
            hero.move(newX, newY);
            if (tile == 'E') {
                attackEnemy(newX, newY);
            } else if (tile == 'I') {
                pickUpItem(newX, newY);
            } else if (tile == 'X') {
                JOptionPane.showMessageDialog(this, "Вы нашли выход!");
                startNewLevel();
            }
            gameMap.setTile(newX, newY, '@');
        }

        repaint();
    }

    private void attackEnemy(int x, int y) {
        Enemy enemy = findEnemyAt(x, y);
        if (enemy != null) {
            enemy.setHealth(enemy.getHealth() - hero.getAttack());
            if (enemy.getHealth() <= 0) {
                enemies.remove(enemy);
                JOptionPane.showMessageDialog(this, "Враг повержен!");
            }
        }
    }

    private Enemy findEnemyAt(int x, int y) {
        for (Enemy enemy : enemies) {
            if (enemy.getX() == x && enemy.getY() == y) {
                return enemy;
            }
        }
        return null;
    }

    private void pickUpItem(int x, int y) {
        Item item = findItemAt(x, y);
        if (item != null) {
            items.remove(item);
            JOptionPane.showMessageDialog(this, "Вы подобрали " + item.getName() + "!");
        }
    }

    private Item findItemAt(int x, int y) {
        for (Item item : items) {
            if (item.getX() == x && item.getY() == y) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        char[][] map = gameMap.getMap();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                switch (map[y][x]) {
                    case '#':
                        g.setColor(Color.BLACK);
                        break;
                    case '.':
                        g.setColor(Color.GRAY);
                        break;
                    case '@':
                        g.setColor(Color.BLUE);
                        break;
                    case 'X':
                        g.setColor(Color.RED);
                        break;
                    case 'E':
                        g.setColor(Color.ORANGE);
                        break;
                    case 'I':
                        g.setColor(Color.GREEN);
                        break;
                    default:
                        g.setColor(Color.WHITE);
                        break;
                }
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        new HeroOfRoguelikesUI();
    }
}