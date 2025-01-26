import enemies.Enemy;
import enemies.Hero;
import items.Item;
import map.GameMap;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MainGame {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private static final int NUM_ENEMIES = 5;
    private static final int NUM_ITEMS = 5;

    private static Hero hero;
    private static GameMap gameMap;
    private static ArrayList<Enemy> enemies;
    private static ArrayList<Item> items;

    private static final Random random = new Random();
    private static boolean levelComplete;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Добро пожаловать в Enemies.Hero of Roguelikes!");
        hero = new Hero(0, 0, 20, 5);

        while (hero.getHealth() > 0) {
            gameMap = new GameMap(WIDTH, HEIGHT);
            gameMap.generateMapWithRooms();
            placeHero();
            generateEnemies();
            generateItems();

            levelComplete = false;

            while (!levelComplete) {
                gameMap.printMap();
                System.out.println("Здоровье героя: " + hero.getHealth());
                System.out.print("Ваш ход (w/a/s/d, q - выйти): ");
                String input = scanner.nextLine();

                if (input.equals("q")) {
                    System.out.println("Игра завершена. Спасибо за игру!");
                    return;
                }

                processInput(input);

                if (levelComplete) {
                    System.out.println("Вы нашли выход на следующий уровень!");
                }

                moveEnemies();
            }
        }
        System.out.println("Вы погибли. Конец игры.");
    }

    private static void placeHero() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        } while (gameMap.getTile(x, y) != '.');

        hero.move(x, y);
        gameMap.setTile(x, y, '@');
    }

    private static void generateEnemies() {
        enemies = new ArrayList<>();
        for (int i = 0; i < NUM_ENEMIES; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (gameMap.getTile(x, y) != '.');

            enemies.add(new Enemy(x, y, 10, 2));
            gameMap.setTile(x, y, 'E');
        }
    }

    private static void generateItems() {
        items = new ArrayList<>();
        for (int i = 0; i < NUM_ITEMS; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (gameMap.getTile(x, y) != '.');

            items.add(new Item(x, y, "Potion"));
            gameMap.setTile(x, y, 'I');
        }
    }

    private static void processInput(String input) {
        int newX = hero.getX();
        int newY = hero.getY();

        switch (input) {
            case "w": newY--; break;
            case "s": newY++; break;
            case "a": newX--; break;
            case "d": newX++; break;
            default:
                System.out.println("Некорректная команда!");
                return;
        }

        if (gameMap.isWalkable(newX, newY)) {
            char tile = gameMap.getTile(newX, newY);

            if (tile == 'E') {
                System.out.println("Вы встретили врага!");
            } else if (tile == 'I') {
                System.out.println("Вы нашли предмет!");
                hero.setHealth(hero.getHealth() +5);
            } else if (tile == 'X') {
                levelComplete = true;
            }

            gameMap.setTile(hero.getX(), hero.getY(), '.');
            hero.move(newX, newY);
            gameMap.setTile(newX, newY, '@');
        } else {
            System.out.println("Нельзя туда идти!");
        }
    }

    private static void moveEnemies() {
        for (Enemy enemy : enemies) {
            int newX = enemy.getX();
            int newY = enemy.getY();

            int direction = random.nextInt(4);
            switch (direction) {
                case 0: newY--; break;
                case 1: newY++; break;
                case 2: newX--; break;
                case 3: newX++; break;
            }

            if (gameMap.isWalkable(newX, newY) && !gameMap.isExit(newX,newY)) {
                gameMap.setTile(enemy.getX(), enemy.getY(), '.');
                enemy.move(newX, newY);
                gameMap.setTile(newX, newY, 'E');
            }
        }
    }
}