import java.util.Random;
import java.util.Scanner;

public class RogueGame {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int NUM_ENEMIES = 3;
    private static final int NUM_ITEMS = 3;

    private static char[][] map = new char[HEIGHT][WIDTH];
    private static int playerX = 0;
    private static int playerY = 0;

    private static int playerHealth = 20;
    private static int playerAttack = 5;

    private static int[][] enemies = new int[NUM_ENEMIES][2]; // Координаты врагов
    private static int[] enemyHealth = new int[NUM_ENEMIES];  // Здоровье врагов

    private static int[][] items = new int[NUM_ITEMS][2];     // Координаты предметов

    private static Random random = new Random();

    public static void main(String[] args) {
        initializeMap();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Добро пожаловать в рогалик!");
        while (playerHealth > 0) {
            printMap();
            System.out.println("Ваше здоровье: " + playerHealth);
            System.out.print("Введите команду (w/a/s/d для движения, q для выхода): ");
            String input = scanner.nextLine();

            if (input.equals("q")) {
                System.out.println("Игра завершена. Спасибо за игру!");
                break;
            }

            processInput(input);
            moveEnemies();

            if (playerHealth <= 0) {
                System.out.println("Вы погибли. Игра окончена.");
                break;
            }
        }

        scanner.close();
    }

    private static void initializeMap() {
        // Заполняем карту
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                map[y][x] = '.'; // Пустое пространство
            }
        }

        // Устанавливаем игрока
        map[playerY][playerX] = '@';

        // Создаём врагов
        for (int i = 0; i < NUM_ENEMIES; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (map[y][x] != '.'); // Ищем пустую клетку

            enemies[i][0] = x;
            enemies[i][1] = y;
            enemyHealth[i] = 10; // Здоровье врага
            map[y][x] = 'E'; // Обозначение врага
        }

        // Создаём предметы
        for (int i = 0; i < NUM_ITEMS; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (map[y][x] != '.'); // Ищем пустую клетку

            items[i][0] = x;
            items[i][1] = y;
            map[y][x] = 'I'; // Обозначение предмета
        }
    }

    private static void printMap() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                System.out.print(map[y][x] + " ");
            }
            System.out.println();
        }
    }

    private static void processInput(String input) {
        int newX = playerX;
        int newY = playerY;

        switch (input) {
            case "w": newY--; break; // Вверх
            case "s": newY++; break; // Вниз
            case "a": newX--; break; // Влево
            case "d": newX++; break; // Вправо
            default:
                System.out.println("Некорректная команда!");
                return;
        }

        if (isValidMove(newX, newY)) {
            char target = map[newY][newX];

            if (target == 'E') {
                int enemyIndex = findEnemy(newX, newY);
                if (enemyIndex != -1) {
                    fightEnemy(enemyIndex);
                }
            } else if (target == 'I') {
                System.out.println("Вы подобрали предмет! (+5 здоровья)");
                playerHealth += 5; // Восстанавливаем здоровье
            }

            map[playerY][playerX] = '.'; // Очищаем старую позицию
            playerX = newX;
            playerY = newY;
            map[playerY][playerX] = '@'; // Новый ход
        } else {
            System.out.println("Нельзя туда пойти!");
        }
    }

    private static boolean isValidMove(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private static void fightEnemy(int enemyIndex) {
        System.out.println("Сражение с врагом!");
        enemyHealth[enemyIndex] -= playerAttack;
        System.out.println("Вы нанесли врагу " + playerAttack + " урона!");

        if (enemyHealth[enemyIndex] <= 0) {
            System.out.println("Враг побеждён!");
            map[enemies[enemyIndex][1]][enemies[enemyIndex][0]] = '.';
            enemies[enemyIndex][0] = -1; // Убираем врага с карты
            enemies[enemyIndex][1] = -1;
        } else {
            int damage = random.nextInt(5) + 1; // Враг наносит урон
            playerHealth -= damage;
            System.out.println("Враг атакует вас! Вы потеряли " + damage + " здоровья.");
        }
    }

    private static int findEnemy(int x, int y) {
        for (int i = 0; i < NUM_ENEMIES; i++) {
            if (enemies[i][0] == x && enemies[i][1] == y) {
                return i;
            }
        }
        return -1;
    }

    private static void moveEnemies() {
        for (int i = 0; i < NUM_ENEMIES; i++) {
            if (enemyHealth[i] <= 0) continue; // Пропускаем мёртвых врагов

            int oldX = enemies[i][0];
            int oldY = enemies[i][1];
            int newX = oldX;
            int newY = oldY;

            // Случайное направление
            int direction = random.nextInt(4);
            switch (direction) {
                case 0: newY--; break; // Вверх
                case 1: newY++; break; // Вниз
                case 2: newX--; break; // Влево
                case 3: newX++; break; // Вправо
            }

            if (isValidMove(newX, newY) && map[newY][newX] == '.') {
                map[oldY][oldX] = '.'; // Очищаем старую позицию
                enemies[i][0] = newX;
                enemies[i][1] = newY;
                map[newY][newX] = 'E'; // Перемещаем врага
            }
        }
    }
}
