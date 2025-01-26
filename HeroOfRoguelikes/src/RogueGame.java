import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class RogueGame {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final int ROOM_MAX_SIZE = 6;
    private static final int ROOM_MIN_SIZE = 3;
    private static final int NUM_ENEMIES = 5;
    private static final int NUM_ITEMS = 5;

    private static char[][] map = new char[HEIGHT][WIDTH];
    private static int playerX = 0;
    private static int playerY = 0;

    private static int playerHealth = 20;
    private static int playerAttack = 5;

    private static int[][] enemies;
    private static int[] enemyHealth;

    private static int[][] items;
    private static int level = 1;

    private static boolean isLevelDone;

    private static Random random = new Random();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Добро пожаловать в рогалик!");
        while (playerHealth > 0) {
            generateMapWithRooms(); // Генерация карты для нового уровня
            placePlayer(); // Размещение игрока на новой карте
            placeEnemiesAndItems(); // Размещение врагов и предметов

            boolean levelComplete = false;

            while (!levelComplete) {
                printMap();
                System.out.println("Ваше здоровье: " + playerHealth);
                System.out.println("Уровень: " + level);
                System.out.print("Введите команду (w/a/s/d для движения, q для выхода): ");
                String input = scanner.nextLine();

                if (input.equals("q")) {
                    System.out.println("Игра завершена. Спасибо за игру!");
                    return;
                }

                processInput(input);

                if (isLevelDone) {
                    System.out.println("Вы нашли выход на следующий уровень!");
                    level++;
                    levelComplete = true;
                    isLevelDone = false;
                }

                moveEnemies();

                if (playerHealth <= 0) {
                    System.out.println("Вы погибли. Игра окончена.");
                    return;
                }
            }
        }

        scanner.close();
    }

    private static void generateMapWithRooms() {
        // Очистка карты
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                map[y][x] = '#'; // Всё заполняется стенами
            }
        }

        // Список комнат
        ArrayList<int[]> rooms = new ArrayList<>();
        int numRooms = random.nextInt(5) + 5; // 5-10 комнат

        for (int i = 0; i < numRooms; i++) {
            int roomWidth = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE + 1) + ROOM_MIN_SIZE;
            int roomHeight = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE + 1) + ROOM_MIN_SIZE;
            int roomX = random.nextInt(WIDTH - roomWidth - 1) + 1;
            int roomY = random.nextInt(HEIGHT - roomHeight - 1) + 1;

            // Проверяем пересечение с другими комнатами
            boolean intersects = false;
            for (int[] otherRoom : rooms) {
                if (roomX < otherRoom[0] + otherRoom[2] && roomX + roomWidth > otherRoom[0] &&
                        roomY < otherRoom[1] + otherRoom[3] && roomY + roomHeight > otherRoom[1]) {
                    intersects = true;
                    break;
                }
            }

            if (!intersects) {
                rooms.add(new int[]{roomX, roomY, roomWidth, roomHeight});
                carveRoom(roomX, roomY, roomWidth, roomHeight);

                if (rooms.size() > 1) {
                    int[] previousRoom = rooms.get(rooms.size() - 2);
                    carveCorridor(previousRoom[0] + previousRoom[2] / 2, previousRoom[1] + previousRoom[3] / 2,
                            roomX + roomWidth / 2, roomY + roomHeight / 2);
                }
            }
        }

        // Размещаем выход
        int[] lastRoom = rooms.get(rooms.size() - 1);
        int exitX = lastRoom[0] + lastRoom[2] / 2;
        int exitY = lastRoom[1] + lastRoom[3] / 2;
        map[exitY][exitX] = 'X'; // Выход
    }

    private static void carveRoom(int x, int y, int width, int height) {
        for (int i = y; i < y + height; i++) {
            for (int j = x; j < x + width; j++) {
                map[i][j] = '.'; // Очищаем пространство для комнаты
            }
        }
    }

    private static void carveCorridor(int x1, int y1, int x2, int y2) {
        while (x1 != x2) {
            map[y1][x1] = '.';
            x1 += x1 < x2 ? 1 : -1;
        }
        while (y1 != y2) {
            map[y1][x1] = '.';
            y1 += y1 < y2 ? 1 : -1;
        }
    }

    private static void placePlayer() {
        do {
            playerX = random.nextInt(WIDTH);
            playerY = random.nextInt(HEIGHT);
        } while (map[playerY][playerX] != '.'); // Ищем свободную клетку
        map[playerY][playerX] = '@';
    }

    private static void placeEnemiesAndItems() {
        enemies = new int[NUM_ENEMIES][2];
        enemyHealth = new int[NUM_ENEMIES];

        items = new int[NUM_ITEMS][2];

        // Размещение врагов
        for (int i = 0; i < NUM_ENEMIES; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (map[y][x] != '.'); // Ищем свободную клетку

            enemies[i][0] = x;
            enemies[i][1] = y;
            enemyHealth[i] = 10; // Здоровье врага
            map[y][x] = 'E'; // Обозначение врага
        }

        // Размещение предметов
        for (int i = 0; i < NUM_ITEMS; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (map[y][x] != '.'); // Ищем свободную клетку

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
                fightEnemy(findEnemy(newX, newY));
            } else if (target == 'I') {
                System.out.println("Вы подобрали предмет! (+5 здоровья)");
                playerHealth += 5; // Восстанавливаем здоровье
            } else if (target == 'X') {
                isLevelDone = true;
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
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && map[y][x] != '#';
    }

    private static int findEnemy(int x, int y) {
        for (int i = 0; i < NUM_ENEMIES; i++) {
            if (enemies[i][0] == x && enemies[i][1] == y) {
                return i;
            }
        }
        return -1;
    }

    private static void fightEnemy(int enemyIndex) {
        if (enemyIndex == -1) return;

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
